/**
 */
package Families.uncertainty;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>uData</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link Families.uncertainty.uData#getName <em>Name</em>}</li>
 *   <li>{@link Families.uncertainty.uData#getUtype <em>Utype</em>}</li>
 * </ul>
 *
 * @see Families.uncertainty.UncertaintyPackage#getuData()
 * @model abstract="true"
 * @generated
 */
public interface uData extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see Families.uncertainty.UncertaintyPackage#getuData_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link Families.uncertainty.uData#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Utype</b></em>' attribute.
	 * The literals are from the enumeration {@link Families.uncertainty.OperatorType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Utype</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Utype</em>' attribute.
	 * @see Families.uncertainty.OperatorType
	 * @see #setUtype(OperatorType)
	 * @see Families.uncertainty.UncertaintyPackage#getuData_Utype()
	 * @model
	 * @generated
	 */
	OperatorType getUtype();

	/**
	 * Sets the value of the '{@link Families.uncertainty.uData#getUtype <em>Utype</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Utype</em>' attribute.
	 * @see Families.uncertainty.OperatorType
	 * @see #getUtype()
	 * @generated
	 */
	void setUtype(OperatorType value);

} // uData
