package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ttb_state")
public class State {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=50)
	private String name;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="state")
	private List<Campus> campuses;
	
	public State() {
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
	public List<Campus> getCampuses() {
		if ( campuses == null ) campuses = new ArrayList<Campus>();
		return campuses;
	}
	public void setCampuses(List<Campus> campuses) {
		this.campuses = campuses;
	}
	
	
	

}
