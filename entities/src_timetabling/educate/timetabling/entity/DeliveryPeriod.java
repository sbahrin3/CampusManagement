package educate.timetabling.entity;

import java.util.Calendar;
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
@Table(name="ttb2_deliveryperiods")
public class DeliveryPeriod {
    
	@Id @Column(length=50)
	private String id;
   
    private int DELIVERYPERIODID;
    @Temporal(TemporalType.TIMESTAMP)
    private Date DELIVERYPERIODSTARTDATE;
    @Temporal(TemporalType.TIMESTAMP)
    private Date DELIVERYPERIODENDDATE;
    
    
    private String  DELIVERYPERIODCODE;
    private String  DELIVERYPERIODDESCLONG;
    private String  DELIVERYPERIODDESC;
    private String PROGRAMMEDELIVERYTYPEDESC;
    
    private String CUID;
    
    private int DELIVERYPERIODGROUPID;
    private String DELIVERYPERIODGROUPDESC;
    private int DELGROUPROLLOVERCOMPLETE;
    private int DELGROUPPUBROLLOVERCOMPLETE;
    
    @ManyToOne @JoinColumn(name="DeliveryPeriodGroups")
	private DeliveryPeriodGroups DeliveryPeriodGroups;
    
    @ManyToOne @JoinColumn(name="ProgrammeDeliveryTypes")
    private ProgrammeDeliveryTypes ProgrammeDeliveryTypes;
    
    @Transient
    private String yearName;
    
    public DeliveryPeriod() {
    	setId(lebah.db.UniqueID.getUID());
    }
    
    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	public void setDELIVERYPERIODID(int DELIVERYPERIODID) {
        this.DELIVERYPERIODID = DELIVERYPERIODID;  
    }
    public void setDELIVERYPERIODSTARTDATE(Date DELIVERYPERIODSTARTDATE){
        this.DELIVERYPERIODSTARTDATE = DELIVERYPERIODSTARTDATE;
    }
    public void setDELGROUPROLLOVERCOMPLETE(int DELGROUPROLLOVERCOMPLETE){
        this.DELGROUPROLLOVERCOMPLETE = DELGROUPROLLOVERCOMPLETE;
    }
    public void setDELIVERYPERIODENDDATE(Date DELIVERYPERIODENDDATE){
        this.DELIVERYPERIODENDDATE = DELIVERYPERIODENDDATE;
    }
    public void setDELIVERYPERIODGROUPID(int DELIVERYPERIODGROUPID){
        this.DELIVERYPERIODGROUPID = DELIVERYPERIODGROUPID;
    }
    public void setDELIVERYPERIODCODE(String DELIVERYPERIODCODE){
        this.DELIVERYPERIODCODE =    DELIVERYPERIODCODE;
    }
    public void setDELIVERYPERIODDESCLONG(String  DELIVERYPERIODDESCLONG){
        this.DELIVERYPERIODDESCLONG =    DELIVERYPERIODDESCLONG;
    }
    public void setDELIVERYPERIODDESC(String  DELIVERYPERIODDESC){
        this.DELIVERYPERIODDESC =    DELIVERYPERIODDESC;
    }
    public void setPROGRAMMEDELIVERYTYPE(ProgrammeDeliveryTypes  ProgrammeDeliveryTypes){
        this.ProgrammeDeliveryTypes =    ProgrammeDeliveryTypes;
    }
    public void setCUID(String  CUID){
        this.CUID = CUID;
    }
    public void setDeliveryPeriodGroups(DeliveryPeriodGroups DeliveryPeriodGroups){
	    this.DeliveryPeriodGroups = DeliveryPeriodGroups;
	}
    
    //setter
    public int getDELIVERYPERIODID(){
        return this.DELIVERYPERIODID;
    }
    public Date getDELIVERYPERIODSTARTDATE(){
        return this.DELIVERYPERIODSTARTDATE;
    }
    public Date getDELIVERYPERIODENDDATE(){
        return this.DELIVERYPERIODENDDATE;
    }
    public int getDELIVERYPERIODGROUPID(){
        return this.DELIVERYPERIODGROUPID;
    }
    public String getDELIVERYPERIODCODE(){
        return this.DELIVERYPERIODCODE;
    }
    public String getDELIVERYPERIODDESCLONG(){
        return this.DELIVERYPERIODDESCLONG;
    }
    public String getDELIVERYPERIODDESC(){
        return this.DELIVERYPERIODDESC;
    }
    public ProgrammeDeliveryTypes getPROGRAMMEDELIVERYTYPE(){
        return this.ProgrammeDeliveryTypes;
    }
    public String getCUID(){
        return this.CUID;
    }
    public int setDelGroupRollOverComplete(){
        return this.DELGROUPROLLOVERCOMPLETE;
    }

	public String getPROGRAMMEDELIVERYTYPEDESC() {
		return PROGRAMMEDELIVERYTYPEDESC;
	}

	public void setPROGRAMMEDELIVERYTYPEDESC(String pROGRAMMEDELIVERYTYPEDESC) {
		PROGRAMMEDELIVERYTYPEDESC = pROGRAMMEDELIVERYTYPEDESC;
	}

	public String getDELIVERYPERIODGROUPDESC() {
		return DELIVERYPERIODGROUPDESC;
	}

	public void setDELIVERYPERIODGROUPDESC(String dELIVERYPERIODGROUPDESC) {
		DELIVERYPERIODGROUPDESC = dELIVERYPERIODGROUPDESC;
	}

	public int getDELGROUPPUBROLLOVERCOMPLETE() {
		return DELGROUPPUBROLLOVERCOMPLETE;
	}

	public void setDELGROUPPUBROLLOVERCOMPLETE(int dELGROUPPUBROLLOVERCOMPLETE) {
		DELGROUPPUBROLLOVERCOMPLETE = dELGROUPPUBROLLOVERCOMPLETE;
	}

	public int getDELGROUPROLLOVERCOMPLETE() {
		return DELGROUPROLLOVERCOMPLETE;
	}
	
	public DeliveryPeriodGroups getDeliveryPeriodGroups(){
	    return this.DeliveryPeriodGroups;
	}
	
	public String getYearName() {
		yearName = "";
		if ( DELIVERYPERIODSTARTDATE != null ) {
			Calendar c = Calendar.getInstance();
			c.setTime(DELIVERYPERIODSTARTDATE);
			yearName = Integer.toString(c.get(Calendar.YEAR));
		}
		return yearName;
	}
	
	public String getName() {
		return DELIVERYPERIODDESCLONG;
	}
    
}
