package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ttb_classroomtype")
public class ClassroomType {
	
	@Id
	private String id;
	@Column(length=10)
	private String code;
	@Column(length=100)
	private String name;
	private int sequence;
	private int priority;
	private int slotCount;
	@OneToMany(cascade=CascadeType.PERSIST)
	private List<Room> rooms;
	
	//@ManyToOne @JoinColumn(name="activity_type_id")
	//private AcademicActivityType activityType;
	
	@ManyToOne @JoinColumn(name="learning_type_id")
	private LearningActivityType learningActivityType;
	
	@ManyToOne @JoinColumn(name="before_id")
	private ClassroomType beforeType;
	
	private int examType;
	
	public ClassroomType() {
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

	/*
	public AcademicActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(AcademicActivityType activityType) {
		this.activityType = activityType;
	}
	*/
	
	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getSlotCount() {
		return slotCount;
	}

	public void setSlotCount(int slotCount) {
		this.slotCount = slotCount;
	}

	public List<Room> getRooms() {
		if ( rooms == null ) rooms = new ArrayList<Room>();
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}
	
	public void addRoom(Room room) throws Exception {
		boolean b = false;
		for ( Room r : rooms ) {
			if ( r.getId().equals(room.getId())) {
				b = true;
				break;
			}
		}
		if ( !b ) rooms.add(room);
	}
	
	public void removeRoom(Room room) throws Exception {
		rooms.remove(room);
	}

	public boolean hasRoom(Room room) throws Exception {
		boolean b = false;
		for ( Room r : rooms ) {
			if ( r.getId().equals(room.getId())) {
				b = true;
				break;
			}
		}
		return b;
	}

	public ClassroomType getBeforeType() {
		return beforeType;
	}

	public void setBeforeType(ClassroomType beforeType) {
		this.beforeType = beforeType;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean getExamType() {
		return examType == 1;
	}

	public void setExamType(boolean examType) {
		this.examType = examType ? 1 : 0;
	}

	public LearningActivityType getLearningActivityType() {
		return learningActivityType;
	}

	public void setLearningActivityType(LearningActivityType learningActivityType) {
		this.learningActivityType = learningActivityType;
	}
	
	
	

}
