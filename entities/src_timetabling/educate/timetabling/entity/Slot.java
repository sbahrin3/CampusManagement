package educate.timetabling.entity;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name="ttb_slot")
public class Slot {
	@Id
	private String id;
	private int posX;
	private int posY;
	
	@OneToMany(cascade=CascadeType.PERSIST)
	private List<Classroom> classrooms;
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="slot")
	private List<SlotClassroom> slotClassrooms;	
	
	@ManyToOne
	private SlotWeek slotWeek;
	@Temporal(TemporalType.DATE)
	private Date slotDate;
	
	@ManyToOne @JoinColumn(name="campus_id")
	private Campus campus;

	public Slot() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Campus getCampus() {
		return campus;
	}

	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public List<Classroom> getClassrooms() {
		return classrooms;
	}

	public void setClassrooms(List<Classroom> classSessions) {
		this.classrooms = classSessions;
	}

	public SlotWeek getSlotWeek() {
		return slotWeek;
	}

	public void setSlotWeek(SlotWeek slotWeek) {
		this.slotWeek = slotWeek;
	}
	
	public void addClassSession(Classroom classSession) {
		if ( classrooms == null ) classrooms = new ArrayList<Classroom>();
		classSession.setSlot(this);
		classrooms.add(classSession);
	}
	
	public void removeClassSession(Classroom classSession) {
		classrooms.remove(classSession);
	}

	public List<SlotClassroom> getSlotClassrooms() {
		if ( slotClassrooms == null ) slotClassrooms = new ArrayList<SlotClassroom>();
		if ( slotClassrooms.size() > 0 ) {
			Collections.sort(slotClassrooms);
		}
		return slotClassrooms;
	}


	public void setSlotClassrooms(List<SlotClassroom> slotClassrooms) {
		this.slotClassrooms = slotClassrooms;
	}
	
	public void addSlotClassroom(SlotClassroom c) {
		if ( slotClassrooms == null ) slotClassrooms = new ArrayList<SlotClassroom>();
		c.setSlot(this);
		slotClassrooms.add(c);
	}
	
	public void removeSlotClassroom(SlotClassroom c) {
		slotClassrooms.remove(c);
	}	

	public Date getSlotDate() {
		return slotDate;
	}

	public void setSlotDate(Date slotDate) {
		this.slotDate = slotDate;
	}
	
	
	
}
