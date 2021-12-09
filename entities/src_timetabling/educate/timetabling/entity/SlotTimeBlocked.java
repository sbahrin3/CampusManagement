package educate.timetabling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import educate.sis.struct.entity.StudyMode;

@Entity
@Table(name="ttb_slot_time_blocked")
public class SlotTimeBlocked {
	
	@Id @Column(length=50)
	private String id;
	
	@OneToOne @Column(name="studyMode_id")
	private StudyMode studyMode;
	@OneToOne @Column(name="slotTime_id")
	private SlotTime slotTime;
	
	private int blocked;  //do not put classroom in automatic generate timetable
	private int blockedMonday;
	private int blockedTuesday;
	private int blockedWednesday;
	private int blockedThursday;
	private int blockedFriday;
	private int blockedSaturday;
	private int blockedSunday;
	
	public SlotTimeBlocked() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public StudyMode getStudyMode() {
		return studyMode;
	}
	public void setStudyMode(StudyMode studyMode) {
		this.studyMode = studyMode;
	}
	public SlotTime getSlotTime() {
		return slotTime;
	}
	public void setSlotTime(SlotTime slotTime) {
		this.slotTime = slotTime;
	}
	

	public boolean getBlocked() {
		return blocked == 1;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked ? 1 : 0;
	}

	public boolean getBlockedMonday() {
		return blockedMonday == 1;
	}

	public void setBlockedMonday(boolean blockedMonday) {
		this.blockedMonday = blockedMonday ? 1 : 0;
	}

	public boolean getBlockedTuesday() {
		return blockedTuesday == 1;
	}

	public void setBlockedTuesday(boolean blocked) {
		this.blockedTuesday = blocked ? 1 : 0;
	}
	
	public boolean getBlockedWednesday() {
		return blockedWednesday == 1;
	}

	public void setBlockedWednesday(boolean blocked) {
		this.blockedWednesday = blocked ? 1 : 0;
	}
	
	public boolean getBlockedThursday() {
		return blockedThursday == 1;
	}

	public void setBlockedThursday(boolean blocked) {
		this.blockedThursday = blocked ? 1 : 0;
	}
	
	public boolean getBlockedFriday() {
		return blockedFriday == 1;
	}

	public void setBlockedFriday(boolean blocked) {
		this.blockedFriday = blocked ? 1 : 0;
	}
	
	public boolean getBlockedSaturday() {
		return blockedSaturday == 1;
	}

	public void setBlockedSaturday(boolean blocked) {
		this.blockedSaturday = blocked ? 1 : 0;
	}
	
	public boolean getBlockedSunday() {
		return blockedSunday == 1;
	}

	public void setBlockedSunday(boolean blocked) {
		this.blockedSunday = blocked ? 1 : 0;
	}
	
	
	
	

}
