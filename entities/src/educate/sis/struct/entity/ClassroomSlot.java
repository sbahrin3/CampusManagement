package educate.sis.struct.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/*
 * History
 * -------
 * #	Date		Name				Remarks
 * ----	----------	------------------	---------------------------------------------------
 * 1.	2009-08-11	Taufek				Added a relationship with Schedule.
 */

/**
 * Entity implementation class for Entity: ClassroomSlot
 * 
 * @author Chong Herng Wah
 */
@Entity
@Table(name = "struc_classroom_slot")
public class ClassroomSlot implements Serializable {

	@Id
	private String id;
	private int day;
	private int slot;
	@ManyToOne
	private Room room;
	@ManyToOne
	private TeacherSubject teacherSubject;
	private static final long serialVersionUID = 1L;

	@ManyToOne
	private ClassSchedule schedule;
	

	public ClassroomSlot() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDay() {
		return this.day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getSlot() {
		return this.slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public Room getRoom() {
		return this.room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public TeacherSubject getTeacherSubject() {
		return this.teacherSubject;
	}

	public void setTeacherSubject(TeacherSubject teacherSubject) {
		this.teacherSubject = teacherSubject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	
	@Override
	public boolean equals(Object obj) {
		ClassroomSlot result = (ClassroomSlot) obj;
		if (result.getId().equals(id))
			return true;
		else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	/**
	 * @return
	 */
	public educate.sis.struct.entity.Teacher getTeacher() {
		return getTeacherSubject().getTeacher();
	}

	/**
	 * @return
	 */
	public SubjectSection getSection() {
		return getTeacherSubject().getSection();
	}

	/**
	 * @return
	 */
	public Subject getSubject() {
		return getTeacherSubject().getSubject();
	}

	
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE);
		
		tsb.append("id", id);
		tsb.append("day", day);
		tsb.append("slot", slot);
		
		return tsb.toString();
	}

	public ClassSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(ClassSchedule schedule) {
		this.schedule = schedule;
	}

}
