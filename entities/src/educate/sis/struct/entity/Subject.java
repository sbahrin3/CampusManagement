package educate.sis.struct.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import educate.sis.exam.entity.CourseworkScheme;
import educate.sis.exam.entity.MarkingGrade;
import educate.timetabling.entity.AIMDepartment;
import metadata.EntityLister;

@Entity
@Table(name="struc_subject")
public class Subject implements EntityLister {
	@Id
	private String id;
	private String code;
	private String name;
	private String altName;
	@Column(length=100)
	private String shortName;
	private String remarks;
	private String synopsis;
	private int credithrs;
	@ManyToOne
	private Faculty faculty;
	private int excludeGpa;
	@ManyToOne
	private MarkingGrade markingGrade;
	@OneToOne
	private SubjectGroup subjectGroup;
	@ManyToOne
	private CourseworkScheme courseworkScheme;
	@OneToMany(fetch=FetchType.LAZY)
	private List<Subject> prerequisites;
	
	@ManyToOne @JoinColumn(name="dept_id")
	private AIMDepartment department;
	
	private int quota;
	
	@Column(length=10)
	private String version;
	
	public Subject(){
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAltName() {
		return altName;
	}

	public void setAltName(String altName) {
		this.altName = altName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}
	
	public String getValue() {
		return getCode()+" "+getName();
	}

	public void setCredithrs(int credithrs) {
		this.credithrs = credithrs;
	}

	public int getCredithrs() {
		return credithrs;
	}

	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}

	public Faculty getFaculty() {
		return faculty;
	}

	public void setExcludeGpa(int excludeGpa) {
		this.excludeGpa = excludeGpa;
	}

	public int getExcludeGpa() {
		return excludeGpa;
	}

	@Override
	public boolean equals(Object o) {
		Subject result = (Subject) o;
		if ( result == null ) return false;
		if(result.id.equals(id))
			return true;
		else
			return false;
	}
	@Override
	public int hashCode() {
		return id.hashCode();
	}


	public MarkingGrade getMarkingGrade() {
		return markingGrade;
	}

	public void setMarkingGrade(MarkingGrade markingGrade) {
		this.markingGrade = markingGrade;
	}

	public SubjectGroup getSubjectGroup() {
		return subjectGroup;
	}

	public void setSubjectGroup(SubjectGroup subjectGroup) {
		this.subjectGroup = subjectGroup;
	}

	public CourseworkScheme getCourseworkScheme() {
		return courseworkScheme;
	}

	public void setCourseworkScheme(CourseworkScheme courseworkScheme) {
		this.courseworkScheme = courseworkScheme;
	}

	public List<Subject> getPrerequisites() {
		if ( prerequisites == null ) prerequisites = new ArrayList<Subject>();
		return prerequisites;
	}

	public void setPrerequisites(List<Subject> prerequisites) {
		this.prerequisites = prerequisites;
	}

	public int getQuota() {
		return quota;
	}

	public void setQuota(int quota) {
		this.quota = quota;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public AIMDepartment getDepartment() {
		return department;
	}

	public void setDepartment(AIMDepartment department) {
		this.department = department;
	}

	public String getVersion() {
		if ( version == null ) version = "";
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	
	
	
	
}
