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
@Table(name="ttb2_ModuleLevels")
public class ModuleLevels {
    @Id 
    private int MODULELEVELID ;
    private String MODULELEVEL;
    private int MODULELEVELDISPLAYORDER ;
    private int EQFLEVELID ;
    private int EHEALEVELID ;
    private int MODULELEVELVALUE ;
    private String CUID ;
    public ModuleLevels(){
    }
    public int getMODULELEVELID() {
        return MODULELEVELID;
    }
    public void setMODULELEVELID(int mODULELEVELID) {
        MODULELEVELID = mODULELEVELID;
    }
    public String getMODULELEVEL() {
        return MODULELEVEL;
    }
    public void setMODULELEVEL(String mODULELEVEL) {
        MODULELEVEL = mODULELEVEL;
    }
    public int getMODULELEVELDISPLAYORDER() {
        return MODULELEVELDISPLAYORDER;
    }
    public void setMODULELEVELDISPLAYORDER(int mODULELEVELDISPLAYORDER) {
        MODULELEVELDISPLAYORDER = mODULELEVELDISPLAYORDER;
    }
    public int getEQFLEVELID() {
        return EQFLEVELID;
    }
    public void setEQFLEVELID(int eQFLEVELID) {
        EQFLEVELID = eQFLEVELID;
    }
    public int getEHEALEVELID() {
        return EHEALEVELID;
    }
    public void setEHEALEVELID(int eHEALEVELID) {
        EHEALEVELID = eHEALEVELID;
    }
    public int getMODULELEVELVALUE() {
        return MODULELEVELVALUE;
    }
    public void setMODULELEVELVALUE(int mODULELEVELVALUE) {
        MODULELEVELVALUE = mODULELEVELVALUE;
    }
    public String getCUID() {
        return CUID;
    }
    public void setCUID(String cUID) {
        CUID = cUID;
    }
}
