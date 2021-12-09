
package educate.timetabling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="department")
public class Department {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=255)
	private String name;
	@ManyToOne @JoinColumn(name="division_id")
	private Division division;
	
	@OneToOne
	private AIMDepartment aimDepartment;

	
	public Department() {
		setId(lebah.db.UniqueID.getUID());
	}
	
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
	public Division getDivision() {
		return division;
	}
	public void setDivision(Division division) {
		this.division = division;
	}

	public AIMDepartment getAimDepartment() {
		return aimDepartment;
	}

	public void setAimDepartment(AIMDepartment aimDepartment) {
		this.aimDepartment = aimDepartment;
	}
	
	

}