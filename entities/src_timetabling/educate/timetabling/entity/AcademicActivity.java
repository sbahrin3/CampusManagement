package educate.timetabling.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="ttb2_activity")
public class AcademicActivity {
    
    @Id @Column(length=50)
    private String id;
    private String name;
    private String remarks;
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    private int isStudentVisible;
    @ManyToOne @JoinColumn(name="AcademicCategory")
    private AcademicCategory academicCategory;
    
    @ManyToOne @JoinColumn(name="AcademicActivityType")
    private AcademicActivityType AcademicActivityType;
   
    @ManyToOne @JoinColumn(name="AcademicCalendar")
    private AcademicCalendar AcademicCalendar;  
    private String action;
    @ManyToOne @JoinColumn(name="activityActionId")
    private AcademicActivityAction activityAction;
    
    @Transient
    private long weeks;
    @Transient
    private long days;
    
    @Column(length=20)
    private String textColor;
    
    private int scheduled; //0-not schedule, 1-put in schedule
    
    @Transient
    private String color;
    
    public AcademicActivity() {
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
    public AcademicCategory getAcademicCategory(){
        return this.academicCategory;
    }
    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
    public void setAction(String action){
        this.action = action;
    }
    public String getAction(){
    	if ( activityAction != null )
    		action = activityAction.getName();
        return action;
    }
    public void setIsStudentVisible(int isStudentVisible){
        this.isStudentVisible = isStudentVisible;
    }
    public int getIsStudentVisible(){
        return this.isStudentVisible;
    }
    public  void setAcademicCategory(AcademicCategory academicCategory){
        this.academicCategory = academicCategory;
    }
    public  void setAcademicActivityType(AcademicActivityType AcademicActivityType){
        this.AcademicActivityType = AcademicActivityType;
    }
    public AcademicActivityType getAcademicActivityType(){
        return this.AcademicActivityType;
    }
    public void setAcademicCalendar(AcademicCalendar AcademicCalendar){
       this.AcademicCalendar = AcademicCalendar ;
    }
    public AcademicCalendar getAcademicCalendar(){
        return this.AcademicCalendar;
    }
    
    
    
    
	public String getTextColor() {
		return textColor;
	}
	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}
	
	public String getColor() {
		if ( getTextColor() == null ) {
			color = "#000000";
		}
		else if ( getTextColor().equals("")) {
			color = "#000000";
		}
		else {
			color = getTextColor();
		}
		return color;
	}
	
	
	public long getWeeks() {
		Date date1 = getStartDate();
		Date date2 = getEndDate();
		if ( date1 != null && date2 != null ) {
			long weeks = getDifferenceInWeeks(date1, date2);
			setWeeks(weeks);
		} else {
			setWeeks(0);
		}
		return weeks;
	}
	public void setWeeks(long weeks) {
		this.weeks = weeks;
	}
	
	
    
	public long getDays() {
		Date date1 = getStartDate();
		Date date2 = getEndDate();
		if ( date1 != null && date2 != null ) {
			long days = getDifferenceInDays(date1, date2);
			setDays(days);
		} else {
			setDays(0);
		}
		return days;
	}
	public void setDays(long days) {
		this.days = days;
	}
	public static long getDifferenceInDays(Date startDate, Date endDate){
		//milliseconds
		long different = endDate.getTime() - startDate.getTime();
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli + 1;
		return elapsedDays;
	}
	
	public static long getDifferenceInWeeks(Date date1, Date date2) {
		long days = getDifferenceInDays(date1, date2);
		long weeks = days/7;
		return weeks;
	}
	public int getScheduled() {
		return scheduled;
	}
	public void setScheduled(int scheduled) {
		this.scheduled = scheduled;
	}
	public AcademicActivityAction getActivityAction() {
		return activityAction;
	}
	public void setActivityAction(AcademicActivityAction activityAction) {
		this.activityAction = activityAction;
	}   
	
	
	
	
    
}

