package educate.sis.general.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import metadata.EntityLister;
/*
 * History
 * -------
 * #	Date			Name				Remarks
 * ----	--------------	------------------	---------------------------------------------------
 * 1.	Nov 02, 2009	Shaiful Nizam		Add field moheCode, for mohe education level code.
 */

@Entity
@Table(name="cm_educationlevel")
public class EducationLevel implements EntityLister {
	@Id
	private String id;
	private String code;
	private String name;
	private int ranking;
	@Column(name="mohe_code", length=2)
	private String moheCode;
	
	public EducationLevel(){
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
		return getName();
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public int getRanking() {
		return ranking;
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
	
}
