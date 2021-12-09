package educate.sis.module;

import java.util.ArrayList;
import java.util.List;

import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.Invoice;
import educate.sis.finance.entity.InvoiceItem;

public class StudentInvoice {
	
	private StudentStatus studentStatus;
	private List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
	private List<Invoice> invoices = new ArrayList<Invoice>();
	
	public StudentStatus getStudentStatus() {
		return studentStatus;
	}
	public void setStudentStatus(StudentStatus studentStatus) {
		this.studentStatus = studentStatus;
	}
	public List<InvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}
	public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}
	public List<Invoice> getInvoices() {
		return invoices;
	}
	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}
	
	
	

}
