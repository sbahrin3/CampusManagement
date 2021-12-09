/**
 * 
 */
package educate.payroll.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 * 
 * Untuk mewakili Teacher => educate.sis.struct.entity.Teacher
 *
 */

@Entity
@Table(name="employee")
public class Employee {

	@Id @Column(length=50)
	private String id;
	
	@Column(length=100)
	private String name;
	@Column(length=100)
	private String icNo;
	private int icType; // 0 = mykad, 1 = passport
	@Column(length=50)
	private String telephoneNo;
	@Column(length=50)
	private String email;
	
	public Employee() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	private double basicSalary;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcNo() {
		return icNo;
	}

	public void setIcNo(String icNo) {
		this.icNo = icNo;
	}

	public int getIcType() {
		return icType;
	}

	public void setIcType(int icType) {
		this.icType = icType;
	}

	public String getTelephoneNo() {
		return telephoneNo;
	}

	public void setTelephoneNo(String telephoneNo) {
		this.telephoneNo = telephoneNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public double getBasicSalary() {
		return basicSalary;
	}

	public void setBasicSalary(double basicSalary) {
		this.basicSalary = basicSalary;
	}
	
	
}
