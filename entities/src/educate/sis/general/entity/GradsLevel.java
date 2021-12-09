package educate.sis.general.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import metadata.EntityLister;


@Entity
@Table(name="cm_gradlevel")
public class GradsLevel implements EntityLister {
	@Id
	public String id;
	@Column(length=10)
	public String code;
	@Column(length=50)
	public String name;
	private int pathNo;
	@Column(name="mohe_code", length=2)
	private String moheCode;
	@Column(length=10)
	private String matricCode;
	
	private int levelOrder;

	//============================
	public GradsLevel(){
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getValue() {
		//return getCode()+" "+getName();
		return getName();
	}

	public int getPathNo() {
		return pathNo;
	}

	public void setPathNo(int pathNo) {
		this.pathNo = pathNo;
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

	public int getLevelOrder() {
		return levelOrder;
	}

	public void setLevelOrder(int levelOrder) {
		this.levelOrder = levelOrder;
	}
	
	public String getMatricCode() {
		if ( matricCode != null && !"".equals(matricCode)) return matricCode;
		else return "";
	}

	public void setMatricCode(String matricCode) {
		this.matricCode = matricCode;
	}
	
}
