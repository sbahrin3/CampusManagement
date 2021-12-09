/**
 * 
 */
package educate.sis.finance.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.enrollment.entity.Student;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */


@Entity
@Table(name="fin_pmt_ptptnsch")
public class PtptnPaymentSchedule {
	
	@Id @Column(length=50)
	private String id;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="paymentSchedule")
	private List<PtptnPaymentScheduleItem> items;
	@ManyToOne
	private Student student;
	private double ptptnAmount;
	
	public PtptnPaymentSchedule() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<PtptnPaymentScheduleItem> getItems() {
		return items;
	}
	public void setItems(List<PtptnPaymentScheduleItem> items) {
		this.items = items;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public double getPtptnAmount() {
		return ptptnAmount;
	}
	public void setPtptnAmount(double ptptnAmount) {
		this.ptptnAmount = ptptnAmount;
	}
	
	
	

}
