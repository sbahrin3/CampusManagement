package educate.admission.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.sis.general.entity.DegreeClass;
import educate.sis.general.entity.StudyLevel;

@Entity
@Table(name="app_tertiaryeducation")
public class TertiaryEducation {
	@Id
	private String id;
	private String instituition;
	private String specialisation;
	@Column(precision=4)
	private int year;
	private double cgpa;
	@ManyToOne
	private StudyLevel level;
	@ManyToOne
	private DegreeClass degreeClass;
	
	public TertiaryEducation() {
		setId(lebah.db.UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInstituition() {
		return instituition;
	}
	public void setInstituition(String instituition) {
		this.instituition = instituition;
	}
	public String getSpecialisation() {
		return specialisation;
	}
	public void setSpecialisation(String specialisation) {
		this.specialisation = specialisation;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getCgpa() {
		return cgpa;
	}
	public void setCgpa(double cgpa) {
		this.cgpa = cgpa;
	}
	public StudyLevel getLevel() {
		return level;
	}
	public void setLevel(StudyLevel level) {
		this.level = level;
	}
	@Override
	public boolean equals(Object o) {
		TertiaryEducation result = (TertiaryEducation)o;
		System.out.println(result.id);
		if(result.id.equals(id))
			return true;
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	public void setDegreeClass(DegreeClass degreeClass) {
		this.degreeClass = degreeClass;
	}
	public DegreeClass getDegreeClass() {
		return degreeClass;
	}
}
