package educate.lms2.entity;

import java.util.*;
import javax.persistence.*;

@Entity
@Table(name="lms2_group")
public class Group {
	
	@Id
	@Column(length=100)
	private String id;
	
	public Group() {
		id = lebah.db.UniqueID.getUID();
	}
	
	@Column(length=100)
	private String name;
	
	@ManyToOne
	private Classroom classroom;
	
	@OneToMany(cascade=CascadeType.PERSIST)
	private List<UserProfile> members;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<UserProfile> getMembers() {
		return members;
	}

	public void setMembers(List<UserProfile> users) {
		this.members = users;
	}
	
	
	
	
}
