package educate.admission.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name="app_offerletterattr")
public class OfferLetterAttribute {
	
	@Id
	private String id;
	@Column(length=50)
	private String date;
	@Column(length=50)
	private String time;
	@Column(length=200)
	private String location;
	
	public OfferLetterAttribute() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	
	
	

}
