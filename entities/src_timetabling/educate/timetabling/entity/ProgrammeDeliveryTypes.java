package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ttb2_programmedeliverytype")
public class ProgrammeDeliveryTypes {
	
	@Id @Column(length=50)
	private String id;
	private int PROGRAMMEDELIVERYTYPEID;
	private String PROGRAMMEDELIVERYTYPEDESC;
	
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="ProgrammeDeliveryTypes")
	private List<DeliveryPeriod> DeliveryPeriod;
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="ProgrammeDeliveryTypes")
	private List<AcademicCalendar> AcademicCalendar;
	public ProgrammeDeliveryTypes() {
    	setId(lebah.db.UniqueID.getUID());
    }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setProgrammeDeliveryTypeId(int PROGRAMMEDELIVERYTYPEID){
		this.PROGRAMMEDELIVERYTYPEID = PROGRAMMEDELIVERYTYPEID;
	}
	public void setProgrammeDeliveryTypeDesc(String PROGRAMMEDELIVERYTYPEDESC){
		this.PROGRAMMEDELIVERYTYPEDESC = PROGRAMMEDELIVERYTYPEDESC;
	}
	public int getProgrammeDeliveryTypeId(){
		return this.PROGRAMMEDELIVERYTYPEID;
	}
	public String getProgrammeDeliveryTypeDesc(){
		return this.PROGRAMMEDELIVERYTYPEDESC;
	}
	public List<DeliveryPeriod> getDeliveryPeriod() {
    	if ( DeliveryPeriod == null ) DeliveryPeriod = new ArrayList<DeliveryPeriod>();
        return DeliveryPeriod;
    }
    public void setDeliveryPeriod(List<DeliveryPeriod> DeliveryPeriod) {
       
        this.DeliveryPeriod = DeliveryPeriod;
    }
    public List<AcademicCalendar> getAcademicCalendar() {
    	if ( AcademicCalendar == null ) AcademicCalendar = new ArrayList<AcademicCalendar>();
        return AcademicCalendar;
    }
    public void setAcademicCalendar(List<AcademicCalendar> AcademicCalendar) {
       
        this.AcademicCalendar = AcademicCalendar;
    }
}
