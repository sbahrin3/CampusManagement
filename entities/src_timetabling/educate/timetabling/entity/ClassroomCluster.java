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
@Table(name="ttb_classroom_cluster")
public class ClassroomCluster {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=20)
	private String code;
	@Column(length=100)
	private String name;
	@OneToMany(cascade=CascadeType.PERSIST)
	private List<ClassroomTemplate> classrooms;
	private int sequence;
	
	public ClassroomCluster() {
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
	public List<ClassroomTemplate> getClassrooms() {
		if ( classrooms == null ) classrooms = new ArrayList<ClassroomTemplate>();
		return classrooms;
	}
	public void setClassrooms(List<ClassroomTemplate> classrooms) {
		this.classrooms = classrooms;
	}
	
	public boolean isExists(ClassroomTemplate c) {
		boolean got = false;
		if ( classrooms == null ) return false;
		else {
			for ( ClassroomTemplate t : classrooms ) {
				if ( c.getId().equals(t.getId())) {
					got = true;
					break;
				}
			}
			return got;
		}
	}
	
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	

}
