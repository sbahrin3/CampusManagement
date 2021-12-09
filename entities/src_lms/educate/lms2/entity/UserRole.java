package educate.lms2.entity;

import javax.persistence.*;

@Entity
@Table(name="lms2_user_role")
public class UserRole {
	
	@Id
	@Column(length=50)
	private String id;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	private UserProfile userProfile;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Classroom classroom;
	@Column(length=50)
	private String role;  //either teacher, or student
	
	public UserRole() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	
	public Classroom getClassroom() {
		return classroom;
	}
	public void setClassroom(Classroom classroom) {
		this.classroom = classroom;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public UserProfile getUserProfile() {
		return userProfile;
	}
	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}
	
	
	
	
	
	

}
