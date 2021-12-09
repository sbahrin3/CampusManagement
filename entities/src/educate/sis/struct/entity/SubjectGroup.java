package educate.sis.struct.entity;

import javax.persistence.*;

import educate.sis.exam.entity.MarkingGrade;

import metadata.EntityLister;

@Entity
@Table(name="struc_subjectgroup")
public class SubjectGroup implements EntityLister  {
	
	@Id
	private String id;
	private String code;
	private String name;
	private double feePerCreditHours;
	
	private int createAverage;
	
	@ManyToOne
	private MarkingGrade markingGrade;	
	
	@ManyToOne
	private Faculty faculty;
	
	private int sequence;
	
	public SubjectGroup() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public double getFeePerCreditHours() {
		return feePerCreditHours;
	}
	public void setFeePerCreditHours(double feePerCreditHours) {
		this.feePerCreditHours = feePerCreditHours;
	}


	public String getValue() {
		return getCode()+" "+getName();
	}


	public Faculty getFaculty() {
		return faculty;
	}


	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}


	public int getCreateAverage() {
		return createAverage;
	}


	public void setCreateAverage(int createAverage) {
		this.createAverage = createAverage;
	}


	public MarkingGrade getMarkingGrade() {
		return markingGrade;
	}


	public void setMarkingGrade(MarkingGrade markingGrade) {
		this.markingGrade = markingGrade;
	}


	public int getSequence() {
		return sequence;
	}


	public void setSequence(int sequence) {
		this.sequence = sequence;
	}



	
	

}
