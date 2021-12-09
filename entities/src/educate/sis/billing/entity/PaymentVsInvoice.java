package educate.sis.billing.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lebah.db.UniqueID;

import educate.enrollment.entity.Student;

@Entity
@Table(name="rpt_paymentvsinvoice")
public class PaymentVsInvoice implements Serializable {
	@Id
	private String id;
	private String matricNo;
	private double paid;
	private double due;
	private double invoice;
	@OneToOne
	private Student student;
	
	public PaymentVsInvoice(){
		setId(UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMatricNo() {
		return matricNo;
	}
	public void setMatricNo(String matricNo) {
		this.matricNo = matricNo;
	}
	
	public double getPaid() {
		return paid;
	}
	public void setPaid(double paid) {
		this.paid = paid;
	}
	public double getDue() {
		return due;
	}
	public void setDue(double due) {
		this.due = due;
	}
	public double getInvoice() {
		return invoice;
	}
	public void setInvoice(double invoice) {
		this.invoice = invoice;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
		
	
}
