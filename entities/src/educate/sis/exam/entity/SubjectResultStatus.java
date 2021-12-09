package educate.sis.exam.entity;

import javax.persistence.*;

@Entity
@Table(name="exam_result_status")
public class SubjectResultStatus {
	
	@Id
	@Column(length=50)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=100)
	private String name;
	private int excludeGPA;
	private int resetMark; //reset mark to zero
	
	public SubjectResultStatus() {
		
	}
	
	public SubjectResultStatus(String code, String name, boolean g, boolean r) {
		setId(code);
		setCode(code);
		setName(name);
		setExcludeGPA(g);
		setResetMark(r);
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
	
	public void setExcludeGPA(boolean b) {
		excludeGPA = b ? 1 : 0;
	}
	
	public boolean getExcludeGPA() {
		return excludeGPA == 1;
	}
	
	public void setResetMark(boolean b) {
		resetMark = b ? 1 : 0;
	}
	
	public boolean getResetMark() {
		return resetMark == 1;
	}
	

}
