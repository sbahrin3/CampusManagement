package educate.sis.billing.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lebah.db.UniqueID;

import educate.enrollment.entity.Student;

@Entity
@Table(name="disc_student")
public class StudentDiscount {
	@Id
	private String id;
	@ManyToOne
	private Student student;
	@ManyToOne
	private Discount discount;
	private double amount;
	public StudentDiscount(){
		setId(UniqueID.getUID());
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
	public Discount getDiscount() {
		return discount;
	}
	public void setDiscount(Discount discount) {
		this.discount = discount;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
}
