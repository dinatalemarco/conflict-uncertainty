package it.univaq.disim.uncertainty.conflict.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Conflict;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.internal.spec.ReferenceChangeSpec;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import Families.FamiliesFactory;
import Families.FamiliesPackage;
import Families.FamilyRegistry;
import Families.Member;
import Families.uncertainty.UncertaintyFactory;
import Families.uncertainty.UncertaintyPackage;
import Families.uncertainty.uMember;

public class ComputeUncertaintyFamiliesModel {

	Resource originResource;
	Resource leftResource;
	Resource rightResource;
	
	ResourceSet originResourceSet;
	ResourceSet leftResourceSet;
	ResourceSet rightResourceSet;
	
	public ComputeUncertaintyFamiliesModel(String left, String right, String origin) {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		ResourceSet rs = new ResourceSetImpl();
		// enable extended metadata
		final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
		rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
		Resource r = rs.getResource(URI.createFileURI("models/Families2.ecore"), true);
		for (EObject eObject : r.getContents()) {
			if (eObject instanceof EPackage) {
				EPackage p = (EPackage) eObject;
				registerSubPackage(p);
			}
		}
		URI uriOrigin = URI.createFileURI(origin);
		URI uriLeft = URI.createFileURI(left);
		URI uriRight = URI.createFileURI(right);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		originResourceSet = new ResourceSetImpl();
		leftResourceSet = new ResourceSetImpl();
		rightResourceSet = new ResourceSetImpl();
		this.originResource = originResourceSet.getResource(uriOrigin, true);
		this.leftResource = leftResourceSet.getResource(uriLeft, true);
		this.rightResource = rightResourceSet.getResource(uriRight, true);
	}
	
	
	private void registerSubPackage(EPackage p) {
		EPackage.Registry.INSTANCE.put(p.getNsURI(), p);
		for (EPackage pack : p.getESubpackages()) {
			registerSubPackage(pack);
		}
	}

	public Comparison computeDiffModel() {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		ResourceSet rs = new ResourceSetImpl();
		// enable extended metadata
		final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
		rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
		Resource r = rs.getResource(URI.createFileURI("models/Families2.ecore"), true);
		for (EObject eObject : r.getContents()) {
			if (eObject instanceof EPackage) {
				EPackage p = (EPackage) eObject;
				registerSubPackage(p);
			}
		}
		
		IComparisonScope scope = new DefaultComparisonScope(leftResourceSet, rightResourceSet, originResourceSet);
		Comparison comparison = EMFCompare.builder().build().compare(scope);
		return comparison;
	}
	
	public static void serializeDiffModel(EObject comparison, String path) {
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
		ResourceSet resSet = new ResourceSetImpl();
		// create a resource
		Resource resource = resSet.createResource(URI.createURI(path));
		resource.getContents().add(comparison);
		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public FamilyRegistry createConflictModel(Comparison comparison) throws Exception {
		FamilyRegistry famRegistry = FamiliesFactory.eINSTANCE.createFamilyRegistry();
		
		
		//Conservative part
		HashMap<EObject,EObject> hashmap = new HashMap<>();
		hashmap.put(originResource.getAllContents().next(),famRegistry);
		TreeIterator<EObject> iterator = originResource.getAllContents();
		while (iterator.hasNext()) {
			EObject content = iterator.next();	
			if(comparison.getDifferences(content).size() == 0) {
				EObject copy = createEObject(content);
				hashmap.put(content, copy);
			}
		}
		iterator = originResource.getAllContents();
		while (iterator.hasNext()) {
			EObject content = iterator.next();	
			EObject newContent = hashmap.get(content);
			for (EReference ref : content.eClass().getEAllReferences()) {
				EReference sf = (EReference) newContent.eClass().getEStructuralFeature(ref.getName());
				
				if(ref.isMany()) {
					EList list = new BasicEList<>();
					EList el = (EList) content.eGet(ref);
					for (Object object : el)  
						((List)newContent.eGet(sf)).add(hashmap.get(object));
							//fam.eGet(refToSet);
				} else {
					newContent.eSet(sf, hashmap.get(content.eGet(ref)));
				}
			}
		}
		
		iterator = leftResource.getAllContents();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			Match m = comparison.getMatch(content);
			if(m.getOrigin() == null && m.getRight() == null) {
				hashmap.put(m.getLeft(), createEObject(m.getLeft()));
			}
		}
		iterator = rightResource.getAllContents();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			Match m = comparison.getMatch(content);
			if(m.getOrigin() == null && m.getLeft() == null) {
				hashmap.put(m.getRight(), createEObject(m.getRight()));
			}
		}	
		
	
		famRegistry = (FamilyRegistry) hashmap.get(originResource.getAllContents().next());

		//Handle conflict
		for (Conflict conflict : comparison.getConflicts()) {
			
			if (conflict.getDifferences().size() == 2) {
				Diff first = conflict.getDifferences().get(0);
				Diff second = conflict.getDifferences().get(1);
				if (first instanceof ReferenceChangeSpec && second instanceof ReferenceChangeSpec) {
					
					if (((ReferenceChangeSpec) first).getReference().equals(
							((ReferenceChangeSpec) second).getReference())) {
						EReference valueRefFirst = ((ReferenceChangeSpec) first).getReference();
						EReference valueRefSecond = ((ReferenceChangeSpec) second).getReference();
						EObject firstValue = ((ReferenceChangeSpec) first).getValue();
						EObject secondValue = ((ReferenceChangeSpec) second).getValue();
						if(!valueRefFirst.isMany()) {
							
							
							//name of referenced class
							String refName = "u" + valueRefFirst.getEReferenceType().getName();
							//get type of uncertanty
							EClass uMemberClass = (EClass) UncertaintyPackage.eINSTANCE.getEClassifier(refName);
							//create upoint
							uMember containerUpoint = (uMember)UncertaintyFactory.eINSTANCE.create(uMemberClass);
							
							if(first.getKind() == DifferenceKind.ADD && second.getKind() == DifferenceKind.ADD) {
								EClass firstClass = (EClass) FamiliesPackage.eINSTANCE.getEClassifier(firstValue.eClass().getName());
								EClass secondClass = (EClass) FamiliesPackage.eINSTANCE.getEClassifier(secondValue.eClass().getName());
								EObject firstUpoint = FamiliesFactory.eINSTANCE.create(firstClass);
								EObject secondUpoint = FamiliesFactory.eINSTANCE.create(secondClass);
								
								for (EAttribute attr : firstClass.getEAllAttributes()) {
									EStructuralFeature sf = firstUpoint.eClass().getEStructuralFeature(attr.getName());
									firstUpoint.eSet(sf, firstValue.eGet(firstValue.eClass().getEStructuralFeature(attr.getName())));
								}
								for (EAttribute attr : secondClass.getEAllAttributes()) {
									EStructuralFeature sf = secondUpoint.eClass().getEStructuralFeature(attr.getName());
									secondUpoint.eSet(sf, secondValue.eGet(secondValue.eClass().getEStructuralFeature(attr.getName())));
								}
								
								
								containerUpoint.getUleft().add((Member) firstUpoint);
								containerUpoint.getUright().add((Member) secondUpoint);
								
								EObject containerClass = hashmap.get(comparison.getMatch(firstValue.eContainer()).getOrigin());
								EReference refToSet = (EReference) containerClass.eClass().getEStructuralFeature(valueRefFirst.getName());
								containerClass.eSet(refToSet, containerUpoint);
							}		
						}
						else {
							EObject containerClass = hashmap.get(comparison.getMatch(firstValue.eContainer()).getOrigin());
							EReference refToSet = (EReference) containerClass.eClass().getEStructuralFeature(valueRefFirst.getName());
							EObject newFirstValue = createEObject(firstValue);
							((List)containerClass.eGet(refToSet)).add(newFirstValue);
							EObject newSecondValue = createEObject(secondValue);
							((List)containerClass.eGet(refToSet)).add(newSecondValue);
						}
					}
				}
				
				
			} else
				throw new Exception("");
		}
		
		
		//handle difference
		for (Diff difference : comparison.getDifferences()) {
			if (difference instanceof ReferenceChangeSpec) {
				ReferenceChangeSpec refChangeSpec = (ReferenceChangeSpec) difference;
				EReference reference = refChangeSpec.getReference();
				EList<Conflict> conflictList = comparison.getConflicts();
				List<Diff> conflictDiffs = new ArrayList<>();  
				for (Conflict conflict : conflictList) 
					if(conflict.getDifferences().contains(refChangeSpec))
						conflictDiffs.add(refChangeSpec);
				if(!conflictDiffs.contains(difference)) {
					//EClass clazz = (EClass) FamiliesPackage.eINSTANCE.getEClassifier(refChangeSpec.getReference().getEType().getName());
					EObject newElement = hashmap.get(refChangeSpec.getValue());
					EObject container = null;
					if(difference.getSource() == DifferenceSource.LEFT)
						if(((Match)difference.eContainer()).getOrigin() != null)
							container = hashmap.get(((Match)difference.eContainer()).getOrigin());
						else container = hashmap.get(((Match)difference.eContainer()).getLeft());
					if(difference.getSource() == DifferenceSource.RIGHT)
						if(((Match)difference.eContainer()).getOrigin() != null)
							container = hashmap.get(((Match)difference.eContainer()).getOrigin());
						else container = hashmap.get(((Match)difference.eContainer()).getRight());
						//container = hashmap.get(((Match)difference.eContainer()).getRight());
					EClass containerClass = 
							(EClass)FamiliesPackage.eINSTANCE.getEClassifier(reference.getEContainingClass().getName());
					EReference refToSet = (EReference) containerClass.getEStructuralFeature(reference.getName());
							//getEAllReferences().stream()
							//.filter(z -> z.getName().equals(refChangeSpec.getReference().getName())).findFirst().get();
					if(refToSet.isMany()) {
						((List)container.eGet(refToSet)).add(newElement);
					}
					else container.eSet(refToSet, newElement);
				}
			}
				
		}
		return famRegistry;
	}


	private EObject createEObject(EObject content) {
//		if(content instanceof EClass) {
			EClass clazz = (EClass) FamiliesPackage.eINSTANCE.getEClassifier(content.eClass().getName());
			
			EObject objectToCreate = FamiliesFactory.eINSTANCE.create(clazz);
			for (EAttribute attr : content.eClass().getEAllAttributes()) {
				EStructuralFeature sf = objectToCreate.eClass().getEStructuralFeature(attr.getName());
				objectToCreate.eSet(sf, content.eGet(content.eClass().getEStructuralFeature(attr.getName())));
			}
			return objectToCreate;
//		}
//		return null;
	}
}
