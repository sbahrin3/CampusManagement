package educate.sis.struct.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.timetabling.entity.Campus;
import lebah.db.PersistenceManager;
import metadata.EntityLister;

@Entity
@Table(name="struc_faculty")
public class Faculty implements EntityLister {
	@Id
	private String id;
	private String code;
	private String name;
	@Column(length=10)
	private String matricCode;
	@Column(length=20)
	private String matricCardColor;
	@Column(length=20)
	private String matricTemplateName;
	
	@ManyToOne
	private Institution institution;
	@ManyToOne @JoinColumn(name="campus_id")
	private Campus campus;
	
	public Faculty() {
		setId(lebah.db.UniqueID.getUID());
	}
	public Faculty(String code) {
		setId(code);
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
	public void setInstitution(Institution institution) {
		this.institution = institution;
	}
	public Institution getInstitution() {
		return institution;
	}
	
	public static Faculty findByCode(String code) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		List<Faculty> list = pm.list("select f from Faculty f where f.code = '" + code + "'");
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
	public String getMatricCardColor() {
		return matricCardColor;
	}
	public void setMatricCardColor(String matricCardColor) {
		this.matricCardColor = matricCardColor;
	}
	public String getMatricTemplateName() {
		return matricTemplateName;
	}
	public void setMatricTemplateName(String matricTemplateName) {
		this.matricTemplateName = matricTemplateName;
	}
	public Campus getCampus() {
		return campus;
	}
	public void setCampus(Campus campus) {
		this.campus = campus;
	}
	
	
	
}
