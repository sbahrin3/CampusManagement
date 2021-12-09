package educate.sis.general.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import metadata.EntityLister;

@Entity
@Table(name="cm_schoolsubject")
public class SchoolSubject implements EntityLister, Comparable {
	@Id
	private String id;
	@Column(length=10)
	public String code;
	@Column(length=50)
	public String name;
	@Column(length=10)
	private String stype;//either SPM or STPM
	private int isdefault;
	private int subjectOrder;
	
	public SchoolSubject() {
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
	public String getType() {
		return stype;
	}
	public void setType(String stype) {
		this.stype = stype;
	}
	
	public String getValue() {
		return getCode()+" "+getName();
	}
	
	public void setDefault(boolean b) {
		isdefault = b ? 1 : 0;
	}
	
	public boolean getDefault() {
		return isdefault == 1;
	}
	public int getSubjectOrder() {
		return subjectOrder;
	}
	public void setSubjectOrder(int subjectOrder) {
		this.subjectOrder = subjectOrder;
	}
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
}
