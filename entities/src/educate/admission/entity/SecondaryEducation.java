package educate.admission.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="app_secondaryeducation")
public class SecondaryEducation {
	@Id
	private String id;
	private String qualification;

	private String country;
	private int duration;
	
	public SecondaryEducation() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	@Override
	public boolean equals(Object o) {
		SecondaryEducation result = (SecondaryEducation)o;
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
