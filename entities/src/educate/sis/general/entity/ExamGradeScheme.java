package educate.sis.general.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cm_examgrade_scheme")
public class ExamGradeScheme {

	@Id
	private String id;
	@Column(length=5)
	private String code;
	@Column(length=50)
	private String name;
	private int startYear;
	
	public ExamGradeScheme() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}
	
	
}
