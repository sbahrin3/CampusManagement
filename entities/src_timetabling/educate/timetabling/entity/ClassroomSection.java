package educate.timetabling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="ttb_classroomsection")
public class ClassroomSection {
	
	@Id
	@Column(length=100)
	private String id;
	@Column(length=100)
	private String code; 
	@Column(length=100)
	private String name;
	@ManyToOne(fetch=FetchType.LAZY)
	private ClassroomType classroomType;
	private int sequence;
	
	private int autoCreate;
	
	
	public ClassroomSection() {
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

	public ClassroomType getClassroomType() {
		return classroomType;
	}

	public void setClassroomType(ClassroomType classroomType) {
		this.classroomType = classroomType;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public boolean getAutoCreate() {
		return autoCreate == 1;
	}

	public void setAutoCreate(boolean autoCreate) {
		this.autoCreate = autoCreate ? 1 : 0;
	}
	
	
	
	

}
