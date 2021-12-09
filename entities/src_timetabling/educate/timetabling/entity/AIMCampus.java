package educate.timetabling.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="aim_campus")
public class AIMCampus {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=50)
	private String name;
	
	@ManyToOne @JoinColumn(name="state_id")
	private AIMState state;
	
	
	public AIMCampus() {
		setId(lebah.db.UniqueID.getUID());
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getCode() {
		if ( code == null || "".equals(code)) code = id;
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


	public AIMState getState() {
		return state;
	}


	public void setState(AIMState state) {
		this.state = state;
	}

}
