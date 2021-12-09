package educate.timetabling.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="ttb2_academic_calendar")
public class AcademicCalendar {
	
    @Id @Column(length=50)
	private String id;
	
	private String remarks;
	@ManyToOne @JoinColumn(name="AcademicGroup")
	private AcademicGroup AcademicGroup;
	@ManyToOne @JoinColumn(name="DeliveryPeriod")
	private DeliveryPeriod DeliveryPeriod;

	
	@ManyToOne @JoinColumn(name="Status")
	private Status Status;
	@Temporal(TemporalType.TIMESTAMP)
	private Date date_created;
	@Temporal(TemporalType.TIMESTAMP)
    private Date date_updated;
	@Temporal(TemporalType.TIMESTAMP)
    private Date date_last_updated;
	@Temporal(TemporalType.TIMESTAMP)
    private Date date_last_approved;
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="AcademicCalendar")
	private List<AcademicActivity> AcademicActivity;
	
	@ManyToOne @JoinColumn(name="DeliveryPeriodGroups")
	private DeliveryPeriodGroups DeliveryPeriodGroups;
	@ManyToOne @JoinColumn(name="ProgrammeDeliveryTypes")
	private ProgrammeDeliveryTypes ProgrammeDeliveryTypes;
	
	public AcademicCalendar() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setRemarks(String remarks){
	    this.remarks = remarks;
	}
	public String getRemarks(){
	    return this.remarks;
	}
	public void setAcademicGroup(AcademicGroup Academicgroup){
	    this.AcademicGroup = Academicgroup;
	}
	public AcademicGroup getAcademicGroup(){
        return this.AcademicGroup;
    }
	public void setDeliveryPeriod(DeliveryPeriod DeliveryPeriod){
	    this.DeliveryPeriod = DeliveryPeriod;
	}
	public DeliveryPeriod getDeliveryPeriod(){
	    return this.DeliveryPeriod;
	}
	public void setStatus(Status Status){
	    this.Status = Status;
	}
	public Status getStatus(){
	    return this.Status;
	}
	public void setDateCreated(){
	    this.date_created = new Date();
	}
	public void setDateCreated(Date dateCreated){
	    this.date_created = dateCreated;
	}
	
	public void setDateUpdated(Date updated){
        this.date_updated = updated;
    }
	public void setDateLastUpdated(){
	    this.date_last_updated = new Date();
	}
	public void setDateLastApproved(){
        this.date_last_approved = new Date();
    }
	public void setDateApproved(Date approved){
        this.date_last_approved = approved;
    }
	public Date getDateCreated(){
	    return this.date_created;
	}
	public Date getDateUpdated(){
        return this.date_updated;
    }
	public Date getDateLastUpdated(){
	    return this.date_last_updated;
	}
	public Date getDateApproved(){
	    return this.date_last_approved;
	}
    public List<AcademicActivity> getAcademicActivity() {
    	if ( AcademicActivity == null ) AcademicActivity = new ArrayList<AcademicActivity>();
        return AcademicActivity;
    }
    public void setAcademicActivity(List<AcademicActivity> AcademicActivity) {
       
        this.AcademicActivity = AcademicActivity;
    }

	public void setDeliveryPeriodGroups(DeliveryPeriodGroups DeliveryPeriodGroups) {
		this.DeliveryPeriodGroups = DeliveryPeriodGroups;
		
	}

	public void setProgrammeDeliveryTypes(ProgrammeDeliveryTypes ProgrammeDeliveryTypes) {
		this.ProgrammeDeliveryTypes = ProgrammeDeliveryTypes;
		
	}

	public ProgrammeDeliveryTypes getProgrammeDeliveryTypes() {
		 return this.ProgrammeDeliveryTypes;
	}

	public DeliveryPeriodGroups getDeliveryPeriodGroups() {
		// TODO Auto-generated method stub
		return this.DeliveryPeriodGroups;
	}
}
