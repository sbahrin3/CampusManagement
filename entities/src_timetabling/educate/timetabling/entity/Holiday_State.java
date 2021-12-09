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
@Table(name="ttb2_holiday_state")
public class Holiday_State {
    @Id @Column(length=50)
    private String id ;
    @ManyToOne @JoinColumn(name="state")
    private State State;
    @ManyToOne @JoinColumn(name="Holiday")
    private Holiday Holiday;
    public Holiday_State(){
        setId(lebah.db.UniqueID.getUID());
    }
    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public  void setState(State State){
        this.State = State;
    }
    public State getState(){
        return this.State;
    }  
    public  void setHoliday(Holiday Holiday){
        this.Holiday = Holiday;
    }
    public Holiday getHoliday(){
        return this.Holiday;
    }  
}
