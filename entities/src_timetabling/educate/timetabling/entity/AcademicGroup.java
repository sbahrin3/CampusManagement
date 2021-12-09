package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="ttb2_academic_group")
public class AcademicGroup {
	
	@Id @Column(length=50)
	private String id;
	private String name;
	private int status = 1;
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="AcademicGroup")
    private List<Level_Group> Level_Group;
	public AcademicGroup() {
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
	public void setStatus(int status){
	    this.status = status;
	}
	public int getStatus(){
	    return status;
	}
    public List<Level_Group> getLevel_Group() {
        return Level_Group;
    }
    public void setLevel_GroupList(List<Level_Group> Level_Group) {
        if ( Level_Group == null ) Level_Group = new ArrayList<Level_Group>();
        this.Level_Group = Level_Group;
    }
}
