package educate.sis.struct.entity;

import javax.persistence.*;

@Entity
@Table(name="struc_degree")
public class Degree {
	@Id
	private String id;
	private String title;
	private float high;
	private float low;
	
	public Degree() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public Degree(String id) {
		setId(id);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public float getHigh() {
		return high;
	}
	public void setHigh(float high) {
		this.high = high;
	}
	public float getLow() {
		return low;
	}
	public void setLow(float low) {
		this.low = low;
	}
	
	
}
