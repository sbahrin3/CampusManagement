package educate.sis.exam.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="exam_marking_period")
public class MarkEntryPeriod {
	
	@Id
	@Column(name="id")
	private String id;
	@Column(name="start_date")
	@Temporal(TemporalType.DATE)
	private Date startDate;
	@Column(name="end_date")
	@Temporal(TemporalType.DATE)
	private Date endDate;
	
	public MarkEntryPeriod() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public MarkEntryPeriod(String s) {
		setId(s);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
	
	


}
