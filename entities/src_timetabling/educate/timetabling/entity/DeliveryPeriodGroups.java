package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * 
 * This entity was created by a AkariSoftware programmers, based on the related database table.
 * I have to follow them.
 *
 */
@Entity
@Table(name="ttb2_deliveryperiodgroups")
public class DeliveryPeriodGroups {
	@Id @Column(length=50)
	private String id;
	public int DELIVERYPERIODGROUPID;
	public String DELIVERYPERIODGROUPDESC;
	public int DELGROUPROLLOVERCOMPLETE;
	public int DELGROUPPUBROLLOVERCOMPLETE;
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="DeliveryPeriodGroups")
	private List<DeliveryPeriod> DeliveryPeriod;
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="DeliveryPeriodGroups")
	private List<AcademicCalendar> AcademicCalendar;
	
	public DeliveryPeriodGroups() {
    	setId(lebah.db.UniqueID.getUID());
    }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	//setter
	public void setDeliveryPeriodId(int DELIVERYPERIODGROUPID){
		this.DELIVERYPERIODGROUPID = DELIVERYPERIODGROUPID;
	}
	public void setDeliveryPeriodDesc(String DELIVERYPERIODGROUPDESC){
		this.DELIVERYPERIODGROUPDESC = DELIVERYPERIODGROUPDESC;
	}
	public void setDelGroupPubRollOverComplete(int DELGROUPPUBROLLOVERCOMPLETE){
		this.DELGROUPPUBROLLOVERCOMPLETE = DELGROUPPUBROLLOVERCOMPLETE;
	}
	public void setDelGroupRollOverComplete(int DELGROUPROLLOVERCOMPLETE){
		this.DELGROUPROLLOVERCOMPLETE = DELGROUPROLLOVERCOMPLETE;
	}

	
	//Getter
	public int getDeliveryPeriodId(){
		return this.DELIVERYPERIODGROUPID;
	}
	public String getDeliveryPeriodDesc(){
		return this.DELIVERYPERIODGROUPDESC;
	}
	public int getDelGroupPubRollOverComplete(){
		return this.DELGROUPPUBROLLOVERCOMPLETE;
	}
	public int getDelGroupRollOverComplete(){
		return this.DELGROUPROLLOVERCOMPLETE;
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
