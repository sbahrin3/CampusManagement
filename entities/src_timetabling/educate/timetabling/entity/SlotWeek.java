package educate.timetabling.entity;


import javax.persistence.*;
import educate.sis.struct.entity.Session;
import java.util.*;

@Entity
@Table(name="ttb_slot_week")
public class SlotWeek {
	
	@Id
	private String id;
	
	@OneToMany(cascade=CascadeType.PERSIST)
	private Set<Slot> slots;
	

	@ManyToOne
	private Session session;
	private int weekNumber;
	
	public SlotWeek() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public Set<Slot> getSlots() {
		return slots;
	}

	public void setSlots(Set<Slot> slots) {
		this.slots = slots;
	}

	

	public Session getSession() {
		return session;
	}

	public List<Slot> getSlotList() {
		List<Slot> list = new ArrayList<Slot>();
		list.addAll(slots);
		return list;
	}


	public void setSession(Session session) {
		this.session = session;
	}

	public int getWeekNumber() {
		return weekNumber;
	}

	public void setWeekNumber(int weekNumber) {
		this.weekNumber = weekNumber;
	}
	
	public void addSlot(Slot slot) {
		if ( slots == null ) slots = new HashSet<Slot>();
		slot.setSlotWeek(this);
		slots.add(slot);
	}
	
	public void removeSlot(Slot slot) {
		slots.remove(slot);
	}
	

}
