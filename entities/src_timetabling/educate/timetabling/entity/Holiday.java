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
@Table(name="ttb2_holiday")
public class Holiday {
    
    @Id @Column(length=50)
    private String id;
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    private int  status;
    private int  year;
    @OneToMany(cascade=CascadeType.ALL, mappedBy="Holiday")
    private List<Holiday_State> Holiday_State;
    public Holiday() {
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
    public void setYear(int year) {
        this.year = year;
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
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public int getYear() {
        return this.year;
    }
    public List<Holiday_State> getHoliday_State() {
    	if ( Holiday_State == null ) Holiday_State = new ArrayList<Holiday_State>();
        return Holiday_State;
    }
    public void setHoliday_State(List<Holiday_State> Holiday_State) {
        
        this.Holiday_State = Holiday_State;
    }
}

