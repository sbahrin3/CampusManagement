package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name="tt_user")
public class TimetableUser {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String userId;
	@Column(length=100)
	private String name;
	@Column(length=100)
	private String email;
	
	@Column(length=50)
	private String portalRole;
	
	@OneToMany
	private List<Campus> campuses;
	
	//@OneToMany
	//private List<Division> divisions;
	
	@ManyToOne @JoinColumn(name="fms_dept_id")
	private Department department;
	
	
	@OneToMany
	private List<Department> departments;
	
	
	@OneToMany
	private List<AIMCampus> aimCampuses;
	@OneToMany
	private List<AIMDepartment> aimDepartments;
	
	/*
	@OneToMany 
	private List<Department> Department;
	*/
	
	@Column(length=50, name="dept_id")
	private String aimDeptId;
	
	public TimetableUser() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Campus> getCampuses() {
		if ( campuses == null ) campuses = new ArrayList<Campus>();
		Collections.sort(campuses, new Campus2Comparator());
		return campuses;
	}
	public void setCampuses(List<Campus> campuses) {
		this.campuses = campuses;
	}
	
	public List<Campus> getCampuses2() {
		if ( campuses == null ) campuses = new ArrayList<Campus>();
		Collections.sort(campuses, new Campus2Comparator());
		return campuses;
	}
	
	/*
	public List<Division> getDivisions() {
		if ( divisions == null ) divisions = new ArrayList<Division>();
		return divisions;
	}
	public void setDivisions(List<Division> divisions) {
		this.divisions = divisions;
	}
	*/

	public boolean hasCampus(Campus campus) {
		return campuses.contains(campus);
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAimDeptId() {
		return aimDeptId;
	}

	public void setAimDeptId(String aimDeptId) {
		this.aimDeptId = aimDeptId;
	}

	public String getPortalRole() {
		return portalRole;
	}

	public void setPortalRole(String portalRole) {
		this.portalRole = portalRole;
	}

	/*
	public void setDepartment(List<Department> department) {
		Department = department;
	}
	*/
	
	
	public List<Department> getDepartments() {
		if ( departments == null ) departments = new ArrayList<Department>();
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public List<AIMDepartment> getAimDepartments() {
		if ( aimDepartments == null ) aimDepartments = new ArrayList<AIMDepartment>();
		List<AIMDepartment> list = new ArrayList<AIMDepartment>();
		list.addAll(aimDepartments);
		Collections.sort(list, new AIMDepartmentComparator());
		return aimDepartments;
	}

	public void setAimDepartments(List<AIMDepartment> aimDepartments) {
		this.aimDepartments = aimDepartments;
	} 
	

	public List<AIMCampus> getAimCampuses() {
		if ( aimCampuses == null ) aimCampuses = new ArrayList<AIMCampus>();
		List<AIMCampus> list = new ArrayList<AIMCampus>();
		list.addAll(aimCampuses);
		Collections.sort(list, new AIMCampusComparator());
		
		return aimCampuses;
	}

	public void setAimCampuses(List<AIMCampus> aimCampuses) {
		this.aimCampuses = aimCampuses;
	}
	
	static class AIMDepartmentComparator extends educate.util.MyComparator {
		
		public int compare(Object o1, Object o2) {
			AIMDepartment x1 = (AIMDepartment) o1;
			AIMDepartment x2 = (AIMDepartment) o2;
			
			if ( x1 == null || x2 == null ) return 0;
			return x1.getId().compareTo(x2.getId());
		}
		
	}	
		
	static class AIMCampusComparator extends educate.util.MyComparator {
		
		public int compare(Object o1, Object o2) {
			AIMCampus x1 = (AIMCampus) o1;
			AIMCampus x2 = (AIMCampus) o2;
			
			if ( x1 == null || x2 == null ) return 0;
			
			return x1.getId().compareTo(x2.getId());
		}
		
	}	
	
	static class Campus2Comparator extends educate.util.MyComparator {
		
		public int compare(Object o1, Object o2) {
			
			Campus y1 = (Campus) o1;
			Campus y2 = (Campus) o2;
			
			
			AIMCampus x1 = y1.getAimCampus();
			AIMCampus x2 = y2.getAimCampus();
			
			if ( x1 == null || x2 == null ) return 0;
			
			return x1.getId().compareTo(x2.getId());
		}
		
	}

}
