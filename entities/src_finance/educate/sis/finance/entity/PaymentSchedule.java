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

import edu.emory.mathcs.backport.java.util.Collections;
import educate.enrollment.entity.Student;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */


@Entity
@Table(name="fin_pmt_schedule")
public class PaymentSchedule {
	
	@Id @Column(length=50)
	private String id;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="paymentSchedule")
	private List<PaymentScheduleItem> items;
	@ManyToOne
	private Student student;
	
	private double tuitionFees;
	private double rebateFees;
	private double ptptnAmount;
	
	private double totalAmount;
	private double monthlyAmount;
	private int totalMonths;
	
	private double totalFlexiAmount;
	private double totalPtptnAmount;
	
	
	
	private int startMonth;
	private int startYear;
	@Temporal(TemporalType.DATE)
	private Date startPaymentDate;
	@Temporal(TemporalType.DATE)
	private Date endPaymentDate;
	@Temporal(TemporalType.DATE)
	private Date expectedStartPaymentDate;
	@Temporal(TemporalType.DATE)
	private Date expectedEndPaymentDate;
	private int invoiceCreated;
	
	
	private double applicationFeeDue;
	private double registrationFeeDue;
	private double resourceFeeDue;
	private double firstPaymentDue;
	
	private double applicationFeePaid;
	private double registrationFeePaid;
	private double resourceFeePaid;
	private double firstPaymentPaid;
	
	@Column(length=50)
	private String firstPaymentNo;
	
	@Temporal(TemporalType.DATE)
	private Date applicationFeePaidDate;
	@Temporal(TemporalType.DATE)
	private Date registrationFeePaidDate;
	@Temporal(TemporalType.DATE)
	private Date resourceFeePaidDate;
	@Temporal(TemporalType.DATE)
	private Date firstPaymentPaidDate;
	
	public PaymentSchedule() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public List<PaymentScheduleItem> getItems() {
		return items;
	}
	
	public void setItems(List<PaymentScheduleItem> items) {
		this.items = items;
	}
	
	public List<PaymentScheduleItem> getScheduleItems() {
		Collections.sort(items, new ScheduleItemComparator());
		return items;
	}
	
	public List<PaymentScheduleItem> getScheduleItemsReverse() {
		Collections.sort(items, new ScheduleItemReverseComparator());
		return items;
	}
	
	public List<PaymentScheduleItem> getScheduleItemsBySequence() {
		Collections.sort(items, new ScheduleItemSequenceComparator());
		return items;
	}
	
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public double getMonthlyAmount() {
		return monthlyAmount;
	}
	public void setMonthlyAmount(double monthlyAmount) {
		this.monthlyAmount = monthlyAmount;
	}
	public int getTotalMonths() {
		return totalMonths;
	}
	public void setTotalMonths(int totalMonths) {
		this.totalMonths = totalMonths;
	}
	public int getStartMonth() {
		return startMonth;
	}
	public void setStartMonth(int startMonth) {
		this.startMonth = startMonth;
	}
	public int getStartYear() {
		return startYear;
	}
	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}
	public Date getStartPaymentDate() {
		return startPaymentDate;
	}
	public void setStartPaymentDate(Date startPaymentDate) {
		this.startPaymentDate = startPaymentDate;
	}
	public Date getEndPaymentDate() {
		return endPaymentDate;
	}
	public void setEndPaymentDate(Date endPaymentDate) {
		this.endPaymentDate = endPaymentDate;
	}
	public Date getExpectedStartPaymentDate() {
		return expectedStartPaymentDate;
	}
	public void setExpectedStartPaymentDate(Date expectedStartPaymentDate) {
		this.expectedStartPaymentDate = expectedStartPaymentDate;
	}
	public Date getExpectedEndPaymentDate() {
		return expectedEndPaymentDate;
	}
	public void setExpectedEndPaymentDate(Date expectedEndPaymentDate) {
		this.expectedEndPaymentDate = expectedEndPaymentDate;
	}
	
	public boolean getInvoiceCreated() {
		return invoiceCreated == 1;
	}

	public void setInvoiceCreated(boolean invoiceCreated) {
		this.invoiceCreated = invoiceCreated ? 1 : 0;
	}

	public double getTuitionFees() {
		return tuitionFees;
	}

	public void setTuitionFees(double tuitionFees) {
		this.tuitionFees = tuitionFees;
	}

	public double getRebateFees() {
		return rebateFees;
	}

	public void setRebateFees(double rebateFees) {
		this.rebateFees = rebateFees;
	}

	public double getPtptnAmount() {
		return ptptnAmount;
	}

	public void setPtptnAmount(double ptptnAmount) {
		this.ptptnAmount = ptptnAmount;
	}





	public double getTotalFlexiAmount() {
		return totalFlexiAmount;
	}

	public void setTotalFlexiAmount(double totalFlexiAmount) {
		this.totalFlexiAmount = totalFlexiAmount;
	}

	public double getTotalPtptnAmount() {
		return totalPtptnAmount;
	}

	public void setTotalPtptnAmount(double totalPtptnAmount) {
		this.totalPtptnAmount = totalPtptnAmount;
	}





	public double getApplicationFeeDue() {
		return applicationFeeDue;
	}

	public void setApplicationFeeDue(double applicationFeeDue) {
		this.applicationFeeDue = applicationFeeDue;
	}

	public double getRegistrationFeeDue() {
		return registrationFeeDue;
	}

	public void setRegistrationFeeDue(double registrationFeeDue) {
		this.registrationFeeDue = registrationFeeDue;
	}

	public double getResourceFeeDue() {
		return resourceFeeDue;
	}

	public void setResourceFeeDue(double resourceFeeDue) {
		this.resourceFeeDue = resourceFeeDue;
	}

	public double getFirstPaymentDue() {
		return firstPaymentDue;
	}

	public void setFirstPaymentDue(double firstPaymentDue) {
		this.firstPaymentDue = firstPaymentDue;
	}

	public double getApplicationFeePaid() {
		return applicationFeePaid;
	}

	public void setApplicationFeePaid(double applicationFeePaid) {
		this.applicationFeePaid = applicationFeePaid;
	}

	public double getRegistrationFeePaid() {
		return registrationFeePaid;
	}

	public void setRegistrationFeePaid(double registrationFeePaid) {
		this.registrationFeePaid = registrationFeePaid;
	}

	public double getResourceFeePaid() {
		return resourceFeePaid;
	}

	public void setResourceFeePaid(double resourceFeePaid) {
		this.resourceFeePaid = resourceFeePaid;
	}

	public double getFirstPaymentPaid() {
		return firstPaymentPaid;
	}

	public void setFirstPaymentPaid(double firstPaymentPaid) {
		this.firstPaymentPaid = firstPaymentPaid;
	}

	public Date getApplicationFeePaidDate() {
		return applicationFeePaidDate;
	}

	public void setApplicationFeePaidDate(Date applicationFeePaidDate) {
		this.applicationFeePaidDate = applicationFeePaidDate;
	}

	public Date getRegistrationFeePaidDate() {
		return registrationFeePaidDate;
	}

	public void setRegistrationFeePaidDate(Date registrationFeePaidDate) {
		this.registrationFeePaidDate = registrationFeePaidDate;
	}

	public Date getResourceFeePaidDate() {
		return resourceFeePaidDate;
	}

	public void setResourceFeePaidDate(Date resourceFeePaidDate) {
		this.resourceFeePaidDate = resourceFeePaidDate;
	}

	public Date getFirstPaymentPaidDate() {
		return firstPaymentPaidDate;
	}

	public void setFirstPaymentPaidDate(Date firstPaymentPaidDate) {
		this.firstPaymentPaidDate = firstPaymentPaidDate;
	}


	public String getFirstPaymentNo() {
		return firstPaymentNo;
	}

	public void setFirstPaymentNo(String firstPaymentNo) {
		this.firstPaymentNo = firstPaymentNo;
	}





	static class ScheduleItemComparator extends educate.util.MyComparator {
		
		public int compare(Object o1, Object o2) {
			PaymentScheduleItem s1 = (PaymentScheduleItem) o1;
			PaymentScheduleItem s2 = (PaymentScheduleItem) o2;
			if ( s1.getPaymentDate() == null || s2.getPaymentDate() == null ) return 0;
			return s1.getPaymentDate().compareTo(s2.getPaymentDate());
		}
		
	}
	
	static class ScheduleItemReverseComparator extends educate.util.MyComparator {
		
		public int compare(Object o1, Object o2) {
			PaymentScheduleItem s1 = (PaymentScheduleItem) o1;
			PaymentScheduleItem s2 = (PaymentScheduleItem) o2;
			if ( s1.getPaymentDate() == null || s2.getPaymentDate() == null ) return 0;
			return s2.getPaymentDate().compareTo(s1.getPaymentDate());
		}
		
	}
	
	static class ScheduleItemSequenceComparator extends educate.util.MyComparator {
		
		public int compare(Object o1, Object o2) {
			PaymentScheduleItem s1 = (PaymentScheduleItem) o1;
			PaymentScheduleItem s2 = (PaymentScheduleItem) o2;
			return s1.getSeq() < s2.getSeq() ? 0 : 1;
		}
		
	}

}
