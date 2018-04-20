package it.univaq.disim.uncertainty.conflict.engine.test;

import it.univaq.disim.uncertainty.conflict.engine.ComputeUncertaintyFamiliesModel;

public class Main {

	public static void main(String[] args) throws Exception {
		
		ComputeUncertaintyFamiliesModel cufm = new ComputeUncertaintyFamiliesModel("models/FamilyRegistry_3_L.xmi", "models/FamilyRegistry_3_R.xmi", "models/FamilyRegistry_3_O.xmi");
		ComputeUncertaintyFamiliesModel.serializeDiffModel(cufm.computeDiffModel(),"diff.xmi");
		ComputeUncertaintyFamiliesModel.serializeDiffModel(cufm.createConflictModel(cufm.computeDiffModel()),"model-U.xmi");
		
//		ComputeUncertaintyFamiliesModel.serializeDiffModel(
//				ComputeUncertaintyFamiliesModel.computeDiffModel("metamodels/Family_1_L.xmi", "metamodels/Family_1_R.xmi", "metamodels/Family_1_O.xmi"), "pippo.xmi");
//		ComputeUncertaintyModel.createConflictModel(
//				ComputeUncertaintyModel.computeDiffModel("metamodels/Family_2_L.xmi", "metamodels/Family_2_R.xmi", "metamodels/Family_2_O.xmi"));

//		ComputeUncertaintyModel.createConflictModel(
//				ComputeUncertaintyModel.computeDiffModel("metamodels/Family_1_L.xmi", "metamodels/Family_1_R.xmi", "metamodels/Family_1_O.xmi"));

	}

}
