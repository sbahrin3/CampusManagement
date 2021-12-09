package educate.sis.struct.entity;


import javax.persistence.*;

import lebah.db.*;

import java.util.*;

import metadata.EntityLister;
/*
 * History
 * -------
 * #	Date			Name				Remarks
 * ----	--------------	------------------	---------------------------------------------------
 * 1.	Nov 02, 2009	Shaiful Nizam		Add field moheCode, for mohe course code.
 */
@Entity
@Table(name="struc_course")
public class Course implements EntityLister {
	@Id
	private String id;
	private String code;
	private String name;
	@Column(name="mohe_code", length=2)
	private String moheCode;
	@Column(length=10)
	private String matricCode;
	@Column(length=20)
	private String matricTemplateName;
	
	@ManyToOne
	private Faculty faculty;
	
	public Course() {
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
		return getCode()+" "+getName();
	}

	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}

	public Faculty getFaculty() {
		return faculty;
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

	public static Course findByCode(String code) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		List<Course> list = pm.list("select c from Course c where c.code = '" + code + "'");
		if ( list.size() == 1 ) return list.get(0);
		else return null;
	}
	
	public String getMatricCode() {
		if ( matricCode != null && !"".equals(matricCode)) return matricCode;
		else return "";
	}

	public void setMatricCode(String matricCode) {
		this.matricCode = matricCode;
	}

	public String getMatricTemplateName() {
		return matricTemplateName == null || "".equals(matricTemplateName) ? faculty.getMatricTemplateName() : matricTemplateName;
	}

	public void setMatricTemplateName(String matricTemplateName) {
		this.matricTemplateName = matricTemplateName;
	}
	
	
	
}
