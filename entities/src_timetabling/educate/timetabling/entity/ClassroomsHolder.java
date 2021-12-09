package educate.timetabling.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ttb_classroom_holder")
public class ClassroomsHolder {
	
	@Id
	private String id;
	@OneToMany(cascade=CascadeType.PERSIST)
	private Set<Classroom> classrooms;
	
	public ClassroomsHolder() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Set<Classroom> getClassrooms() {
		return classrooms;
	}
	public void setClassrooms(Set<Classroom> classrooms) {
		this.classrooms = classrooms;
	}


}
