package educate.timetabling.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="ttb_slot_classroom")
public class SlotClassroom implements Comparable {
	
	@Id
	private String id;
	@ManyToOne
	private Slot slot;
	//@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@ManyToOne
	private Classroom classroom;
	private int sequence;
	
	@Column(length=50)
	private String createId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="campus_id")
	private Campus campus;
	
	@Transient
	private int marker;
	
	public SlotClassroom() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Slot getSlot() {
		return slot;
	}
	public void setSlot(Slot slot) {
		this.slot = slot;
	}
	public Classroom getClassroom() {
		return classroom;
	}
	public void setClassroom(Classroom classroom) {
		this.classroom = classroom;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	

	public String getCreateId() {
		return createId;
	}

	public void setCreateId(String createId) {
		this.createId = createId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	

	public Campus getCampus() {
		return campus;
	}

	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	public int getMarker() {
		return marker;
	}

	public void setMarker(int marker) {
		this.marker = marker;
	}

	@Override
	public int compareTo(Object o) {
		SlotClassroom s = (SlotClassroom) o;
		if ( s.getSequence() > this.getSequence() ) return -1;
		else return 1;
	}
	
	
}
