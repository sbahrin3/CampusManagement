package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import educate.sis.struct.entity.Session;

@Entity
@Table(name="ttb_activity_session")
public class ActivitySession {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=100)
	private String name;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="activitySession")
	private List<ActivitySessionDate> dates;
	@ManyToOne @JoinColumn(name="session_id")
	private Session session;
	@ManyToOne @JoinColumn(name="academicCalendar_id")
	private AcademicCalendar academicCalendar;
	
	@ManyToOne @JoinColumn(name="campus_id")
	private Campus campus;
	
	@Transient
	private Date compareDate;
	
	public ActivitySession() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ActivitySessionDate> getDates() {
		if ( dates == null ) dates = new ArrayList<ActivitySessionDate>();
		return dates;
	}
	public void setDates(List<ActivitySessionDate> dates) {
		this.dates = dates;
	}
	
	public List<ActivitySessionDate> getDateList() {
		List<ActivitySessionDate> list = new ArrayList<ActivitySessionDate>();
		list.addAll(getDates());
		Collections.sort(list, new DateComparator());
		return list;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	
	
	public Campus getCampus() {
		return campus;
	}

	public void setCampus(Campus campus) {
		this.campus = campus;
	}


	public AcademicCalendar getAcademicCalendar() {
		return academicCalendar;
	}

	public void setAcademicCalendar(AcademicCalendar academicCalendar) {
		this.academicCalendar = academicCalendar;
	}
	
	



	public Date getCompareDate() {
		return compareDate;
	}

	public void setCompareDate(Date compareDate) {
		this.compareDate = compareDate;
	}





	static class DateComparator extends educate.util.MyComparator {
		public int compare(Object o1, Object o2) {
			ActivitySessionDate a1 = (ActivitySessionDate) o1;
			ActivitySessionDate a2 = (ActivitySessionDate) o2;
			
			return a1.getStartDate().compareTo(a2.getStartDate());
		}
	}	

}
