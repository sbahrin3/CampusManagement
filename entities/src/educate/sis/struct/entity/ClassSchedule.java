package educate.sis.struct.entity;

import java.util.Collection;

/*
 * History
 * -------
 * #	Date		Name				Remarks
 * ----	----------	------------------	---------------------------------------------------
 * 1.	2009-08-11	Taufek				Created.
 */

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
/*
 * History
 * -------
 * #	Date		Name				Remarks
 * ----	----------	------------------	---------------------------------------------------
 * 1.	2009-08-11	Taufek				Created.
 */
@Entity
@Table(name = "struc_class_schedule")
public class ClassSchedule {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String name;
	
	@ManyToOne
	private Session session;
	
	@OneToMany(mappedBy="schedule", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
	private Collection<ClassroomSlot> slots;

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Collection<ClassroomSlot> getSlots() {
		return slots;
	}

	public void setSlots(Collection<ClassroomSlot> slots) {
		this.slots = slots;
	}

	
	
}
