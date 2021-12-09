package educate.sis.module;

import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.Invoice;

public class BulkInvoiceItem {
	
	private StudentStatus studentStatus;
	private Invoice invoice;
	private double totalPaid;
	private String totalPaidInWord;
	
	public StudentStatus getStudentStatus() {
		return studentStatus;
	}
	public void setStudentStatus(StudentStatus studentStatus) {
		this.studentStatus = studentStatus;
	}
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	public double getTotalPaid() {
		return totalPaid;
	}
	public void setTotalPaid(double totalPaid) {
		this.totalPaid = totalPaid;
	}
	public String getTotalPaidInWord() {
		return totalPaidInWord;
	}
	public void setTotalPaidInWord(String totalPaidInWord) {
		this.totalPaidInWord = totalPaidInWord;
	}
	
	

}
