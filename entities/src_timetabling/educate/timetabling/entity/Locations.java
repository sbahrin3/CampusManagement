package educate.timetabling.entity;

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
@Table(name="ttb2_locations")
public class Locations {
    
    private int LocationId ;
    private String Location;
    private String LocationCode;
    private int LocationDisplayOrder;
    private String cuid;
    public Locations(){
        setCuid(lebah.db.UniqueID.getUID());
    }
    public  void setLocationId(int LocationId){
        this.LocationId = LocationId;
    }
    public int getLocationId(){
        return this.LocationId;
    }
    public void setLocation(String Location){
        this.Location = Location;
    }
    public String getLocation(){
        return this.Location;
    }
    public void setLocationCode(String LocationCode){
        this.LocationCode = LocationCode;
    }
    public String getLocationCode(){
        return this.LocationCode;
    }
    public void setLocationDisplayOrder(int LocationDisplayOrder){
        this.LocationDisplayOrder = LocationDisplayOrder;
    }
    public int getLocationDisplayOrder(){
        return this.LocationDisplayOrder;
    }
    public void setCuid(String cuid){
        this.cuid = cuid;
    }
    public String  getCuid(){
        return this.cuid;
    }
}
