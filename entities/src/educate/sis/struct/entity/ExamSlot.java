package educate.sis.struct.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/*
 * History
 * -------
 * #	Date		Name				Remarks
 * ----	----------	------------------	---------------------------------------------------
 * 1.	2009-08-11	Taufek				Added a relationship with Schedule.
 */
@Entity
@Table(name="struc_exam_slot")
public class ExamSlot implements Serializable{
	
	@Id
	private String id;
	private int day;
	private int slot;
	@ManyToOne
	private Subject subject;
	//@ManyToOne
	//private Room room;
	//@ManyToOne
	//private StudentSubject studentSubject;
	private static final long serialVersionUID = 1L;

	@ManyToOne
	private ExamSchedule schedule;

	public ExamSlot() {
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
	
	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	/*
	public Room getRoom() {
		return this.room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	public StudentSubject getStudentSubject() {
		return this.studentSubject;
	}

	public void setStudentSubject(StudentSubject studentSubject) {
		this.studentSubject = studentSubject;
	}
	*/
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	
	@Override
	public boolean equals(Object obj) {
		ExamSlot result = (ExamSlot) obj;
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

	public ExamSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(ExamSchedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * @return
	 */
	/*
	public SubjectSection getSection() {
		return getStudentSubject().getSection();
	}
	*/
	/**
	 * @return
	 */
	/*
	public Subject getSubject() {
		return getStudentSubject().getSubject();
	}
	*/
}
