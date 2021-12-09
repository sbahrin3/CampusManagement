package educate.sis.struct.entity;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/*
 * History
 * -------
 * #	Date			Name				Remarks
 * ----	--------------	------------------	---------------------------------------------------
 * 1.	Aug 20, 2009	taufek				File created.
 */

@Entity
@DiscriminatorValue("CLASS")
public class ClassCalendarSchedule extends AbstractCalendarSchedule{
	
	@OneToMany(mappedBy="schedule", cascade={CascadeType.REMOVE})
	private Collection<ScheduleEntry> entries;

	public Collection<ScheduleEntry> getEntries() {
		return entries;
	}

	public void setEntries(Collection<ScheduleEntry> entries) {
		this.entries = entries;
	}
}
