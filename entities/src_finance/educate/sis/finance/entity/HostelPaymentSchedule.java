/**
 * 
 */
package educate.sis.finance.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import educate.enrollment.entity.Student;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */


@Entity
@Table(name="fin_pmt_hostelchedule")
public class HostelPaymentSchedule {

	@Id @Column(length=50)
	private String id;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="paymentSchedule")
	private List<HostelPaymentScheduleItem> items;
	@ManyToOne
	private Student student;
	private double feeAmount;
	
	
	public HostelPaymentSchedule() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<HostelPaymentScheduleItem> getItems() {
		return items;
	}
	public void setItems(List<HostelPaymentScheduleItem> items) {
		this.items = items;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public double getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(double ptptnAmount) {
		this.feeAmount = ptptnAmount;
	}
	
	
}