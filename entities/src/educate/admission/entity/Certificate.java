package educate.admission.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.sis.general.entity.ProfesionalQualification;

@Entity
@Table(name="app_certificate")
public class Certificate {
	@Id
	private String id;
	@ManyToOne
	private ProfesionalQualification qualification;
	@Column(precision=4)
	private int year;
	public Certificate() {
		setId(lebah.db.UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public void setQualification(ProfesionalQualification qualification) {
		this.qualification = qualification;
	}
	public ProfesionalQualification getQualification() {
		return qualification;
	}
	@Override
	public boolean equals(Object o) {
		Certificate result = (Certificate)o;
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
}
