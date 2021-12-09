package educate.admission.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.sis.general.entity.GeneralExamGrade;
import educate.sis.general.entity.SchoolSubject;

@Entity
@Table(name="app_spmresult")
public class SpmResult implements Comparable {
	@Id
	private String id; 
	@ManyToOne
	private SchoolSubject subject;
	@Column(length=5)
	private String grade;
	@Column(length=100)
	private String otherName;
	
	@ManyToOne
	private GeneralExamGrade generalExamGrade;
	
	public SpmResult() {
		setId(lebah.db.UniqueID.getUID());
	}
	public SpmResult(String id) {
		setId(id);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public SchoolSubject getSubject() {
		return subject;
	}
	public void setSubject(SchoolSubject subject) {
		this.subject = subject;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
	@Override
	public boolean equals(Object o) {
		SpmResult result = (SpmResult)o;
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
	
	public int compareTo(Object o) {
		SpmResult s = (SpmResult) o;
		if ( subject == null ) return -1;
		if ( subject != null && s.subject != null ) {
			return subject.getSubjectOrder() > s.subject.getSubjectOrder() ? 1 : -1;
		}
		return 0;
	}
	public String getOtherName() {
		return otherName;
	}
	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}
	
	
}
