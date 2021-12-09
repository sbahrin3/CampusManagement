package educate.lms2.entity;

import javax.persistence.*;

@Entity
@Table(name="lms2_classroom_enrolled")
public class ClassroomEnrolled {
	
	@Id
	private String id;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private UserProfile user;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Classroom classroom;
	private String role;
	
	public ClassroomEnrolled() {
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
	public UserProfile getUser() {
		return user;
	}
	public void setUser(UserProfile user) {
		this.user = user;
	}
	
	
	

}
