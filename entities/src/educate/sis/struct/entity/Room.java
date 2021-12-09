package educate.sis.struct.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import metadata.EntityLister;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/*
 * History
 * -------
 * #	Date		Name				Remarks
 * ----	----------	------------------	---------------------------------------------------
 * 1.	2009-08-11	Taufek				Added relationship with LearningCentre.
 */

/**
 * Entity implementation class for Entity: Room
 * 
 * @author Chong Herng Wah
 */
@Entity
@Table(name = "struc_room")
public class Room implements Serializable, EntityLister {

	@Id
	private String id;
	private String code;
	private String name;
	@OneToMany(mappedBy = "room")
	private Set<ClassroomSlot> classroomSlots = new HashSet<ClassroomSlot>();
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="centre_id")
	private LearningCentre learningCentre;

	public Room() {
		setId(lebah.util.UIDGenerator.getUID());
	}

	public Room(String id) {
		setId(id);
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	
	@Override
	public boolean equals(Object obj) {
		Room result = (Room) obj;
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

	public Set<ClassroomSlot> getClassroomSlots() {
		return this.classroomSlots;
	}

	public void setClassroomSlots(Set<ClassroomSlot> slots) {
		this.classroomSlots = slots;
	}

	
	@Override
	public String toString() {
		
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE);
		
		tsb.append("id", id);
		tsb.append("code", code);
		tsb.append("name", name);
		tsb.append("learningCentre", learningCentre);
		
		
		return tsb.toString();

	}

	public LearningCentre getLearningCentre() {
		return learningCentre;
	}

	public void setLearningCentre(LearningCentre learningCentre) {
		this.learningCentre = learningCentre;
	}

	public String getValue() {
		return name;
	}
	
	

}
