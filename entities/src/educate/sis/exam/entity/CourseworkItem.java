package educate.sis.exam.entity;

import javax.persistence.*;

@Entity
@Table(name="exam_courseworkitem")
public class CourseworkItem implements Comparable{
	
	@Id
	private String id;
	private String code;
	private String name;
	private int sequence;
	private double percentage;
	@ManyToOne
	private Coursework coursework;
	
	@ManyToOne
	private CourseworkScheme courseworkScheme;
	
	public CourseworkItem() {
		setId(lebah.util.UIDGenerator.getUID());
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
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	@Override
	public boolean equals(Object o) {
		CourseworkItem i = (CourseworkItem) o;
		if ( i.getId().equals(getId())) return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	

	public int compareTo(Object o){
		CourseworkItem a = (CourseworkItem)o;
		if(a.sequence > sequence)
			return -1;
		else if(a.sequence < sequence)
			return 1;
		
		return 0;
	}
	public Coursework getCoursework() {
		return coursework;
	}
	public void setCoursework(Coursework coursework) {
		this.coursework = coursework;
	}
	public CourseworkScheme getCourseworkScheme() {
		return courseworkScheme;
	}
	public void setCourseworkScheme(CourseworkScheme courseworkScheme) {
		this.courseworkScheme = courseworkScheme;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	
	
}
