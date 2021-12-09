package educate.sis.struct.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
@Table(name = "struc_schedule_entry")
public class ScheduleEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private Date startDate;

	private Date endDate;

	@Transient
	private long duration;
	
	@ManyToOne
	private Teacher teacher;

	@ManyToOne
	private Subject subject;
	
	@ManyToOne
	private SubjectSection section;

	@ManyToOne
	private Room room;
	
	@ManyToOne
	private AbstractCalendarSchedule schedule;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public long getDuration() {

		if (startDate != null && endDate != null) {
			duration = endDate.getTime() - startDate.getTime();
		}

		return duration;
	}

	public void setDuration(long duration) {
		if (startDate != null) {
			endDate = new Date(startDate.getTime() + duration);
		} else if (endDate != null) {
			startDate = new Date(endDate.getTime() - duration);
		}
	}
	
	

	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ScheduleEntry)) {
			return false;
		}
		ScheduleEntry o = (ScheduleEntry)obj;
		
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.id, o.id);
		eb.append(this.startDate, o.startDate);
		
		return eb.isEquals();
	}

	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(5,9).append(this.id).hashCode();
	}

	
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE);
		
		tsb.append("id", this.id);
		tsb.append("startDate", this.startDate);
		tsb.append("endDate", this.endDate);
		tsb.append("duration", this.getDuration());
		
		return tsb.toString();
		
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public SubjectSection getSection() {
		return section;
	}

	public void setSection(SubjectSection section) {
		this.section = section;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public AbstractCalendarSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(AbstractCalendarSchedule schedule) {
		this.schedule = schedule;
	}

}
