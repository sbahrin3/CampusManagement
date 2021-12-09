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
@Table(name="ttb2_level_group")
public class Level_Group {
    @Id @Column(length=50)
    private String id ;
    @ManyToOne @JoinColumn(name="AcademicGroup")
    private AcademicGroup AcademicGroup;
    @ManyToOne @JoinColumn(name="ModuleLevels")
    private ModuleLevels ModuleLevels;
    public Level_Group(){
        setId(lebah.db.UniqueID.getUID());
    }
    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public  void setAcademicGroup(AcademicGroup AcademicGroup){
        this.AcademicGroup = AcademicGroup;
    }
    public AcademicGroup getAcademicGroup(){
        return this.AcademicGroup;
    }  
    public  void setModuleLevels(ModuleLevels ModuleLevels){
        this.ModuleLevels = ModuleLevels;
    }
    public ModuleLevels getModuleLevels(){
        return this.ModuleLevels;
    }  
}
