package educate.timetabling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="ttb_invigilator")
public class Invigilator {

	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String identityNo;
	@Column(length=100)
	private String name;
	@ManyToOne @JoinColumn(name="campus_id")
	private AIMCampus campus;
	
	private int limitTeachingHours; //per day (max is 12 hours/day)
	private int limitTeachingHoursWeek; // per week
	
	private int minTeachingHoursWeek; //minimum teaching hours per week
	
	//exception slots
	@Column(length=4)
	private String mondayStartTime = "";
	@Column(length=4)
	private String mondayEndTime = "";
	@Column(length=4)
	private String tuesdayStartTime = "";
	@Column(length=4)
	private String tuesdayEndTime = "";
	@Column(length=4)
	private String wednesdayStartTime = "";
	@Column(length=4)
	private String wednesdayEndTime = "";
	@Column(length=4)
	private String thursdayStartTime = "";
	@Column(length=4)
	private String thursdayEndTime = "";
	@Column(length=4)
	private String fridayStartTime = "";
	@Column(length=4)
	private String fridayEndTime = "";
	@Column(length=4)
	private String saturdayStartTime = "";
	@Column(length=4)
	private String saturdayEndTime = "";
	@Column(length=4)
	private String sundayStartTime = "";
	@Column(length=4)
	private String sundayEndTime = "";
	
	public Invigilator() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIdentityNo() {
		return identityNo;
	}
	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public AIMCampus getCampus() {
		return campus;
	}
	public void setCampus(AIMCampus campus) {
		this.campus = campus;
	}
	public int getLimitTeachingHours() {
		return limitTeachingHours;
	}
	public void setLimitTeachingHours(int limitTeachingHours) {
		this.limitTeachingHours = limitTeachingHours;
	}
	public int getLimitTeachingHoursWeek() {
		return limitTeachingHoursWeek;
	}
	public void setLimitTeachingHoursWeek(int limitTeachingHoursWeek) {
		this.limitTeachingHoursWeek = limitTeachingHoursWeek;
	}
	public int getMinTeachingHoursWeek() {
		return minTeachingHoursWeek;
	}
	public void setMinTeachingHoursWeek(int minTeachingHoursWeek) {
		this.minTeachingHoursWeek = minTeachingHoursWeek;
	}
	public String getMondayStartTime() {
		return mondayStartTime;
	}
	public void setMondayStartTime(String mondayStartTime) {
		this.mondayStartTime = mondayStartTime;
	}
	public String getMondayEndTime() {
		return mondayEndTime;
	}
	public void setMondayEndTime(String mondayEndTime) {
		this.mondayEndTime = mondayEndTime;
	}
	public String getTuesdayStartTime() {
		return tuesdayStartTime;
	}
	public void setTuesdayStartTime(String tuesdayStartTime) {
		this.tuesdayStartTime = tuesdayStartTime;
	}
	public String getTuesdayEndTime() {
		return tuesdayEndTime;
	}
	public void setTuesdayEndTime(String tuesdayEndTime) {
		this.tuesdayEndTime = tuesdayEndTime;
	}
	public String getWednesdayStartTime() {
		return wednesdayStartTime;
	}
	public void setWednesdayStartTime(String wednesdayStartTime) {
		this.wednesdayStartTime = wednesdayStartTime;
	}
	public String getWednesdayEndTime() {
		return wednesdayEndTime;
	}
	public void setWednesdayEndTime(String wednesdayEndTime) {
		this.wednesdayEndTime = wednesdayEndTime;
	}
	public String getThursdayStartTime() {
		return thursdayStartTime;
	}
	public void setThursdayStartTime(String thursdayStartTime) {
		this.thursdayStartTime = thursdayStartTime;
	}
	public String getThursdayEndTime() {
		return thursdayEndTime;
	}
	public void setThursdayEndTime(String thursdayEndTime) {
		this.thursdayEndTime = thursdayEndTime;
	}
	public String getFridayStartTime() {
		return fridayStartTime;
	}
	public void setFridayStartTime(String fridayStartTime) {
		this.fridayStartTime = fridayStartTime;
	}
	public String getFridayEndTime() {
		return fridayEndTime;
	}
	public void setFridayEndTime(String fridayEndTime) {
		this.fridayEndTime = fridayEndTime;
	}
	public String getSaturdayStartTime() {
		return saturdayStartTime;
	}
	public void setSaturdayStartTime(String saturdayStartTime) {
		this.saturdayStartTime = saturdayStartTime;
	}
	public String getSaturdayEndTime() {
		return saturdayEndTime;
	}
	public void setSaturdayEndTime(String saturdayEndTime) {
		this.saturdayEndTime = saturdayEndTime;
	}
	public String getSundayStartTime() {
		return sundayStartTime;
	}
	public void setSundayStartTime(String sundayStartTime) {
		this.sundayStartTime = sundayStartTime;
	}
	public String getSundayEndTime() {
		return sundayEndTime;
	}
	public void setSundayEndTime(String sundayEndTime) {
		this.sundayEndTime = sundayEndTime;
	}
	
	public boolean hasMonday() {
		return !"".equals(mondayStartTime) && !"".equals(mondayEndTime);
	}
	
	public boolean hasTuesday() {
		return !"".equals(tuesdayStartTime) && !"".equals(tuesdayEndTime);
	}
	
	public boolean hasWednesday() {
		return !"".equals(wednesdayStartTime) && !"".equals(wednesdayEndTime);
	}
	
	public boolean hasThursday() {
		return !"".equals(thursdayStartTime) && !"".equals(thursdayEndTime);
	}
	
	public boolean hasFriday() {
		return !"".equals(fridayStartTime) && !"".equals(fridayEndTime);
	}
	
	public boolean hasSaturday() {
		return !"".equals(saturdayStartTime) && !"".equals(saturdayEndTime);
	}
	
	public boolean hasSunday() {
		return !"".equals(sundayStartTime) && !"".equals(sundayEndTime);
	}
	
	
	public boolean equals(Object o) {
		if ( o == this ) return true;
		if ( o == null && o.getClass() != this.getClass() ) return false;
		Invigilator i = (Invigilator) o;
		return i.getId().equals(getId());
	}
	
	public int hashCode() { 
		final int prime = 31; 
		int result = 1; 
		result = prime * result + ((id == null) ? 0 : id.hashCode()); 
		return result; 
	}


}
