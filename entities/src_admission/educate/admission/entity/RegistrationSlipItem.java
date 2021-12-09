package educate.admission.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.enrollment.entity.Student;

@Entity @Table(name="enrl_regslip_items")
public class RegistrationSlipItem {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="student_id")
	private Student student;
	@Column(length=100)
	private String itemName;
	private int sequence;
	
	public RegistrationSlipItem() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	

}
