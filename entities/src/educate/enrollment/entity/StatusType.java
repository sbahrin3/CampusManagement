package educate.enrollment.entity;

import javax.persistence.*;

import metadata.EntityLister;
@Entity
@Table(name="enrl_statustype")
public class StatusType implements EntityLister{

	@Id
	private String id;
	private String category;
	private String name;
	private String code;
	private int defaultType;
	private int deferType;
	private int quitType;
	@Column(name="mohe_code", length=4)
	private String moheCode;
	private int sequence;
	
	private int inActive;
	@Transient
	private int canDelete = 0;


	public StatusType(){
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return getCategory()+" "+getName();
	}


	public boolean getDefault() {
		return defaultType == 0 ? false : true;
	}

	public void setDefault(boolean isDefault) {
		defaultType = isDefault ? 1 : 0;
	}

	public boolean getDefer() {
		return deferType == 0 ? false : true;
	}

	public void setDefer(boolean isDefer) {
		deferType = isDefer ? 1 : 0;
	}
	
	public boolean getQuit() {
		return quitType == 0 ? false : true;
	}
	
	public void setQuit(boolean isQuit) {
		quitType = isQuit ? 1 : 0;
	}

	public String getMoheCode() {
		if (moheCode == null) {
			moheCode = "";
		}
		return moheCode;
	}


	public void setMoheCode(String moheCode) {
		if (moheCode == null) {
			this.moheCode = "";
		} else {
			this.moheCode = moheCode;
		}
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	public void setInActive(boolean b) {
		inActive = b ? 1 : 0;
	}
	
	public boolean getInActive() {
		return inActive == 1;
	}

	public boolean getCanDelete() {
		if ( "active".equals(id) || "deferred".equals(id) || "quit".equals(id)) {
			return false;
		}
		return canDelete == 0;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete ? 1 : 0;
	}
	
	

}
