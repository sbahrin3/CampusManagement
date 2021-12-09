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
@Table(name="ttb2_holiday_location")
public class Holiday_Locations {
    @Id @Column(length=50)
    private String id ;
    @ManyToOne @JoinColumn(name="Locations")
    private Locations Locations;
    @ManyToOne @JoinColumn(name="Holiday")
    private Holiday Holiday;
    public Holiday_Locations(){
        setId(lebah.db.UniqueID.getUID());
    }
    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public  void setLocations(Locations Locations){
        this.Locations = Locations;
    }
    public Locations getLocations(){
        return this.Locations;
    }  
    public  void setHoliday(Holiday Holiday){
        this.Holiday = Holiday;
    }
    public Holiday getHoliday(){
        return this.Holiday;
    }  
}
