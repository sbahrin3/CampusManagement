package educate.sis.struct.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import metadata.EntityLister;


/*
 * Session object is used by both Academic Session and Timetable Session
 * Timetable Session is identified by having pathNo = 10000 
 */

@Entity
@Table(name="struc_session")
public class Session implements Comparable, EntityLister {
	@Id
	private String id;
	private String name;
	@Temporal(TemporalType.DATE)
	private Date startDate;
	@Temporal(TemporalType.DATE)
	private Date endDate; 
	private String altName;
	private int pathNo; //Timetable Session is identified by having pathNo = 10000
	private String code;
	private int startDateDay;
	private int startDateMonth;
	private int startDateYear;
	private int endDateDay;
	private int endDateMonth;
	private int endDateYear;
	private String examSessionName;
	@Column(length=10)
	private String matricCode;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="session")
	private List<StudentEnrollmentIntake> enrollmentIntakes;
	
	public Session() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Object d) {
		//this.startDate = startDate;
		if ( d instanceof Date ) startDate = (Date) d;
		else if ( d instanceof String) {
			setStartDate((String) d);
		}
	}
	public void setStartDate(String date) {
		if (date == null || "".equals(date)) return;
		String separator = "-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		

		setStartDate(new GregorianCalendar(year, month, day).getTime());
	}
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Object d) {
		if ( d instanceof Date ) endDate = (Date) d;
		else if ( d instanceof String) {
			setEndDate((String) d);
		}
	}
	public void setEndDate(String date) {
		if (date == null || "".equals(date)) return;
		String separator = "-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setEndDate(new GregorianCalendar(year, month, day).getTime());
	}

	public String getAltName() {
		return altName;
	}

	public void setAltName(String altName) {
		this.altName = altName;
	}

	public int getPathNo() {
		return pathNo;
	}

	public void setPathNo(int pathNo) {
		this.pathNo = pathNo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getStartDateDay() {
		Calendar c = new GregorianCalendar();
		if ( startDate == null ){
			System.out.println("startDate is NULL");
			return 0;
		}
		c.setTime(startDate);
		return c.get(Calendar.DATE);
	}

	public void setStartDateDay(int startDateDay) {
		this.startDateDay = startDateDay;
	}

	public int getStartDateMonth() {
		Calendar c = new GregorianCalendar();
		if ( startDate == null ){
			System.out.println("startDate is NULL");
			return 0;
		}
		c.setTime(startDate);
		return c.get(Calendar.MONTH) + 1;
	}

	public void setStartDateMonth(int startDateMonth) {
		this.startDateMonth = startDateMonth;
	}

	public int getStartDateYear() {
		Calendar c = new GregorianCalendar();
		if ( startDate == null ){
			System.out.println("startDate is NULL");
			return 0;
		}
		c.setTime(startDate);

		return c.get(Calendar.YEAR);
	}

	public void setStartDateYear(int startDateYear) {
		this.startDateYear = startDateYear;
	}

	public int getEndDateDay() {
		Calendar c = new GregorianCalendar();
		if ( endDate == null ){
			System.out.println("endDate is NULL");
			return 0;
		}
		c.setTime(endDate);

		return c.get(Calendar.DATE);
	}

	public void setEndDateDay(int endDateDay) {
		this.endDateDay = endDateDay;
	}

	public int getEndDateMonth() {
		Calendar c = new GregorianCalendar();
		if ( endDate == null ){
			System.out.println("endDate is NULL");
			return 0;
		}
		c.setTime(endDate);

		return c.get(Calendar.MONTH) + 1;
	}

	public void setEndDateMonth(int endDateMonth) {
		this.endDateMonth = endDateMonth;
	}

	public int getEndDateYear() {
		Calendar c = new GregorianCalendar();
		if ( endDate == null ){
			System.out.println("endDate is NULL");
			return 0;
		}
		c.setTime(endDate);
		return c.get(Calendar.YEAR);
	}

	public void setEndDateYear(int endDateYear) {
		this.endDateYear = endDateYear;
	}

	public String getExamSessionName() {
		return examSessionName;
	}

	public void setExamSessionName(String examSessionName) {
		this.examSessionName = examSessionName;
	}
	public String getValue() {
		return getName();
	}
	
	public int compareTo(Object o) {
		Session s = (Session) o;
		if (s.getStartDate().after(getStartDate())) return -1;
		else if ( s.getStartDate().before(getStartDate())) return 1;
		else return 0;
	}

	public String getMatricCode() {
		if ( matricCode != null && !"".equals(matricCode)) return matricCode;
		matricCode = getCode() != null && !"".equals(getCode()) ? getCode().substring(0,1) : "";
		return matricCode;
	}

	public void setMatricCode(String matricCode) {
		this.matricCode = matricCode;
	}

	public List<StudentEnrollmentIntake> getEnrollmentIntakes() {
		if ( enrollmentIntakes == null ) enrollmentIntakes = new ArrayList<StudentEnrollmentIntake>();
		return enrollmentIntakes;
	}

	public void setEnrollmentIntakes(List<StudentEnrollmentIntake> enrollmentIntakes) {
		this.enrollmentIntakes = enrollmentIntakes;
	}	
	
	

}
