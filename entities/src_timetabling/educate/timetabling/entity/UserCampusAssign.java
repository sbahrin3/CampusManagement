package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ttb_usercampus_assign")
public class UserCampusAssign {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String userId; //user login id
	@OneToMany
	private List<Campus> campuses;
	
	
	public UserCampusAssign() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<Campus> getCampuses() {
		if ( campuses == null ) campuses = new ArrayList<Campus>();
		return campuses;
	}
	public void setCampuses(List<Campus> campuses) {
		this.campuses = campuses;
	}
	
	

}
