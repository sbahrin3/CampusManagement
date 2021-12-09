package educate.admission.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.sis.general.entity.GeneralExamGrade;

@Entity
@Table(name="app_alevelresult")
public class AlevelResult {
	@Id
	private String id;
	private String subject;
	private String grade;
	@ManyToOne
	private GeneralExamGrade generalExamGrade;
	
	public AlevelResult(){
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getGrade() {
		return grade;
	}
	@Override
	public boolean equals(Object o) {
		AlevelResult result = (AlevelResult)o;
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

	public void setGeneralExamGrade(GeneralExamGrade generalExamGrade) {
		this.generalExamGrade = generalExamGrade;
	}

	public GeneralExamGrade getGeneralExamGrade() {
		return generalExamGrade;
	}
	
}
