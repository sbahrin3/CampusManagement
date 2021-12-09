package educate.timetabling.entity;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import educate.sis.struct.entity.StudyMode;

@Entity
@Table(name="ttb_slot_time")
public class SlotTime implements java.io.Serializable {
	
	@Id
	private String id;
	private int posX; //also a sequence
	@Temporal(TemporalType.TIME)
	private Time startTime;
	@Temporal(TemporalType.TIME)
	private Time endTime;
	@Transient
	private String label;
	@Transient
	private int lengthInMinutes;
	@Transient
	private int lengthInSeconds;
	
	
	//--
	@ManyToOne @JoinColumn(name="state_id")
	private State state;
	
	private int blocked;  //do not put classroom in automatic generate timetable
	private int blockedMonday;
	private int blockedTuesday;
	private int blockedWednesday;
	private int blockedThursday;
	private int blockedFriday;
	private int blockedSaturday;
	private int blockedSunday;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="slotTime")
	private List<SlotTimeBlocked> slotTimeBlockedList;
	
	public SlotTime() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	
	
	public State getState() {
		return state;
	}



	public void setState(State state) {
		this.state = state;
	}



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
		
	}
	public int getPosX() {
		return posX;
	}
	public void setPosX(int posX) {
		this.posX = posX;
	}
	public Time getStartTime() {
		return startTime;
	}
	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}
	public Time getEndTime() {
		return endTime;
	}
	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}
	public int getLengthInMinutes() {
		long diff = Math.abs(endTime.getTime () - startTime.getTime ());
		lengthInSeconds = (int) (diff / 1000);
		lengthInMinutes = lengthInSeconds / 60;
		return lengthInMinutes;
	}


	public int getLengthInSeconds() {
		long diff = Math.abs(endTime.getTime () - startTime.getTime ());
		lengthInSeconds = (int) (diff / 1000);
		lengthInMinutes = lengthInSeconds / 60;		
		return lengthInSeconds;
	}

	public String getLabel() {
		SimpleDateFormat f = new SimpleDateFormat("h:mm a");
		label = f.format(startTime) + "-" + f.format(endTime);
		
		label = label.replaceAll(" ", "");
		label = label.replaceAll("AM", "a");
		label = label.replaceAll("PM", "p");
		
		return label;
	}
	
	public String getDuration() {
		int t = lengthInMinutes;
		int hours = t / 60;
		int minutes = t % 60;
		return hours + " hours " + minutes + " minutes";
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
	
	
	
	public List<SlotTimeBlocked> getSlotTimeBlockedList() {
		if ( slotTimeBlockedList == null ) slotTimeBlockedList = new ArrayList<SlotTimeBlocked>();
		return slotTimeBlockedList;
	}



	public void setSlotTimeBlockedList(List<SlotTimeBlocked> slotTimeBlockedList) {
		this.slotTimeBlockedList = slotTimeBlockedList;
	}



	public int getHourStart() {
		SimpleDateFormat f = new SimpleDateFormat("hh");
		return Integer.parseInt(f.format(startTime));
	}
	
	public int getMinuteStart() {
		SimpleDateFormat f = new SimpleDateFormat("mm");
		return Integer.parseInt(f.format(startTime));
	}
	
	public int getHourEnd() {
		SimpleDateFormat f = new SimpleDateFormat("hh");
		return Integer.parseInt(f.format(endTime));
	}
	
	public int getMinuteEnd() {
		SimpleDateFormat f = new SimpleDateFormat("mm");
		return Integer.parseInt(f.format(endTime));
	}
	
	public String getDisplayHourStart() {
		SimpleDateFormat f = new SimpleDateFormat("HH");
		return f.format(startTime);
	}
	
	public String getDisplayMinuteStart() {
		SimpleDateFormat f = new SimpleDateFormat("mm");
		return f.format(startTime);
	}
	
	public String getDisplayHourEnd() {
		SimpleDateFormat f = new SimpleDateFormat("HH");
		return f.format(endTime);
	}
	
	public String getDisplayMinuteEnd() {
		SimpleDateFormat f = new SimpleDateFormat("mm");
		return f.format(endTime);
	}
	
	public void setBlockedMonday(StudyMode studyMode, boolean blocked) {
		if ( slotTimeBlockedList == null ) slotTimeBlockedList = new ArrayList<SlotTimeBlocked>();
		boolean add = true;
		for ( SlotTimeBlocked slotTimeBlocked :  slotTimeBlockedList) {
			if ( slotTimeBlocked.getStudyMode().getId().equals(studyMode.getId())) {
				slotTimeBlocked.setBlockedMonday(blocked);
				add = false;
				break;
			}
		}
		if ( add ) {
			SlotTimeBlocked slotTimeBlocked = new SlotTimeBlocked();
			slotTimeBlocked.setStudyMode(studyMode);
			slotTimeBlocked.setBlockedMonday(blocked);
			slotTimeBlocked.setSlotTime(this);
			slotTimeBlockedList.add(slotTimeBlocked);
		}
	}
	
	
	public void setBlockedTuesday(StudyMode studyMode, boolean blocked) {
		if ( slotTimeBlockedList == null ) slotTimeBlockedList = new ArrayList<SlotTimeBlocked>();
		boolean add = true;
		for ( SlotTimeBlocked slotTimeBlocked :  slotTimeBlockedList) {
			if ( slotTimeBlocked.getStudyMode().getId().equals(studyMode.getId())) {
				slotTimeBlocked.setBlockedTuesday(blocked);
				add = false;
				break;
			}
		}
		if ( add ) {
			SlotTimeBlocked slotTimeBlocked = new SlotTimeBlocked();
			slotTimeBlocked.setStudyMode(studyMode);
			slotTimeBlocked.setBlockedTuesday(blocked);
			slotTimeBlocked.setSlotTime(this);
			slotTimeBlockedList.add(slotTimeBlocked);
		}
	}
	
	
	public void setBlockedWednesday(StudyMode studyMode, boolean blocked) {
		if ( slotTimeBlockedList == null ) slotTimeBlockedList = new ArrayList<SlotTimeBlocked>();
		boolean add = true;
		for ( SlotTimeBlocked slotTimeBlocked :  slotTimeBlockedList) {
			if ( slotTimeBlocked.getStudyMode().getId().equals(studyMode.getId())) {
				slotTimeBlocked.setBlockedWednesday(blocked);
				add = false;
				break;
			}
		}
		if ( add ) {
			SlotTimeBlocked slotTimeBlocked = new SlotTimeBlocked();
			slotTimeBlocked.setStudyMode(studyMode);
			slotTimeBlocked.setBlockedWednesday(blocked);
			slotTimeBlocked.setSlotTime(this);
			slotTimeBlockedList.add(slotTimeBlocked);
		}
	}
	
	
	public void setBlockedThursday(StudyMode studyMode, boolean blocked) {
		if ( slotTimeBlockedList == null ) slotTimeBlockedList = new ArrayList<SlotTimeBlocked>();
		boolean add = true;
		for ( SlotTimeBlocked slotTimeBlocked :  slotTimeBlockedList) {
			if ( slotTimeBlocked.getStudyMode().getId().equals(studyMode.getId())) {
				slotTimeBlocked.setBlockedThursday(blocked);
				add = false;
				break;
			}
		}
		if ( add ) {
			SlotTimeBlocked slotTimeBlocked = new SlotTimeBlocked();
			slotTimeBlocked.setStudyMode(studyMode);
			slotTimeBlocked.setBlockedThursday(blocked);
			slotTimeBlocked.setSlotTime(this);
			slotTimeBlockedList.add(slotTimeBlocked);
		}
	}
	
	
	public void setBlockedFriday(StudyMode studyMode, boolean blocked) {
		if ( slotTimeBlockedList == null ) slotTimeBlockedList = new ArrayList<SlotTimeBlocked>();
		boolean add = true;
		for ( SlotTimeBlocked slotTimeBlocked :  slotTimeBlockedList) {
			if ( slotTimeBlocked.getStudyMode().getId().equals(studyMode.getId())) {
				slotTimeBlocked.setBlockedFriday(blocked);
				add = false;
				break;
			}
		}
		if ( add ) {
			SlotTimeBlocked slotTimeBlocked = new SlotTimeBlocked();
			slotTimeBlocked.setStudyMode(studyMode);
			slotTimeBlocked.setBlockedFriday(blocked);
			slotTimeBlocked.setSlotTime(this);
			slotTimeBlockedList.add(slotTimeBlocked);
		}
	}
	
	public void setBlockedSaturday(StudyMode studyMode, boolean blocked) {
		if ( slotTimeBlockedList == null ) slotTimeBlockedList = new ArrayList<SlotTimeBlocked>();
		boolean add = true;
		for ( SlotTimeBlocked slotTimeBlocked :  slotTimeBlockedList) {
			if ( slotTimeBlocked.getStudyMode().getId().equals(studyMode.getId())) {
				slotTimeBlocked.setBlockedSaturday(blocked);
				add = false;
				break;
			}
		}
		if ( add ) {
			SlotTimeBlocked slotTimeBlocked = new SlotTimeBlocked();
			slotTimeBlocked.setStudyMode(studyMode);
			slotTimeBlocked.setBlockedSaturday(blocked);
			slotTimeBlocked.setSlotTime(this);
			slotTimeBlockedList.add(slotTimeBlocked);
		}
	}
	
	
	public void setBlockedSunday(StudyMode studyMode, boolean blocked) {
		if ( slotTimeBlockedList == null ) slotTimeBlockedList = new ArrayList<SlotTimeBlocked>();
		boolean add = true;
		for ( SlotTimeBlocked slotTimeBlocked :  slotTimeBlockedList) {
			if ( slotTimeBlocked.getStudyMode().getId().equals(studyMode.getId())) {
				slotTimeBlocked.setBlockedSunday(blocked);
				add = false;
				break;
			}
		}
		if ( add ) {
			SlotTimeBlocked slotTimeBlocked = new SlotTimeBlocked();
			slotTimeBlocked.setStudyMode(studyMode);
			slotTimeBlocked.setBlockedSunday(blocked);
			slotTimeBlocked.setSlotTime(this);
			slotTimeBlockedList.add(slotTimeBlocked);
		}
	}
	
	
	public boolean getBlockedMonday(StudyMode studyMode) {
		boolean blocked = false;
		if ( slotTimeBlockedList == null ) return false;
		for ( SlotTimeBlocked slotTimeBlocked :  slotTimeBlockedList) {
			if ( slotTimeBlocked.getStudyMode().getId().equals(studyMode.getId())) {
				blocked = slotTimeBlocked.getBlockedMonday();
				break;
			}
		}
		return blocked;
	}
	
	public boolean getBlockedTuesday(StudyMode studyMode) {
		boolean blocked = false;
		if ( slotTimeBlockedList == null ) return false;
		for ( SlotTimeBlocked slotTimeBlocked :  slotTimeBlockedList) {
			if ( slotTimeBlocked.getStudyMode().getId().equals(studyMode.getId())) {
				blocked = slotTimeBlocked.getBlockedTuesday();
				break;
			}
		}
		return blocked;
	}
	
	public boolean getBlockedWednesday(StudyMode studyMode) {
		boolean blocked = false;
		if ( slotTimeBlockedList == null ) return false;
		for ( SlotTimeBlocked slotTimeBlocked :  slotTimeBlockedList) {
			if ( slotTimeBlocked.getStudyMode().getId().equals(studyMode.getId())) {
				blocked = slotTimeBlocked.getBlockedWednesday();
				break;
			}
		}
		return blocked;
	}
	
	public boolean getBlockedThursday(StudyMode studyMode) {
		boolean blocked = false;
		if ( slotTimeBlockedList == null ) return false;
		for ( SlotTimeBlocked slotTimeBlocked :  slotTimeBlockedList) {
			if ( slotTimeBlocked.getStudyMode().getId().equals(studyMode.getId())) {
				blocked = slotTimeBlocked.getBlockedThursday();
				break;
			}
		}
		return blocked;
	}
	
	public boolean getBlockedFriday(StudyMode studyMode) {
		boolean blocked = false;
		if ( slotTimeBlockedList == null ) return false;
		for ( SlotTimeBlocked slotTimeBlocked :  slotTimeBlockedList) {
			if ( slotTimeBlocked.getStudyMode().getId().equals(studyMode.getId())) {
				blocked = slotTimeBlocked.getBlockedFriday();
				break;
			}
		}
		return blocked;
	}
	
	public boolean getBlockedSaturday(StudyMode studyMode) {
		boolean blocked = false;
		if ( slotTimeBlockedList == null ) return false;
		for ( SlotTimeBlocked slotTimeBlocked :  slotTimeBlockedList) {
			if ( slotTimeBlocked.getStudyMode().getId().equals(studyMode.getId())) {
				blocked = slotTimeBlocked.getBlockedSaturday();
				break;
			}
		}
		return blocked;
	}
	
	public boolean getBlockedSunday(StudyMode studyMode) {
		boolean blocked = false;
		if ( slotTimeBlockedList == null ) return false;
		for ( SlotTimeBlocked slotTimeBlocked :  slotTimeBlockedList) {
			if ( slotTimeBlocked.getStudyMode().getId().equals(studyMode.getId())) {
				blocked = slotTimeBlocked.getBlockedSunday();
				break;
			}
		}
		return blocked;
	}
}
