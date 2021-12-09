package educate.timetabling.entity;

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
@Table(name="ttb2_activity_type")
public class AcademicActivityType {
	
	@Id @Column(length=50)
	private String id;
	private String name;
	private String description;
	private int displayorder;
	public AcademicActivityType() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setDisplayOrder(int displayorder){
	    this.displayorder = displayorder;
	}
	public int getDisplayOrder(){
	    return this.displayorder;
	}
	public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
