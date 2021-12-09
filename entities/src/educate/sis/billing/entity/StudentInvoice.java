package educate.sis.billing.entity;

import java.util.*;
import javax.persistence.*;

import educate.enrollment.entity.Student;
import educate.sis.struct.entity.Session;

@Entity
@Table(name="inv_student")
public class StudentInvoice implements Financer {
	@Id
	private String id;
	@OneToOne
	private Student student;
	private String invoiceNo;
	@Temporal(TemporalType.DATE)
	private Date invoiceDate;
	@OneToMany(cascade=CascadeType.ALL)
	private Set <InvoiceDetail> invoiceDetails;
	private String voids;
	@OneToOne
	private Session session;
	public StudentInvoice(){
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

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Set<InvoiceDetail> getInvoiceDetails() {
		return invoiceDetails;
	}
	
	public void addInvoiceDetail(InvoiceDetail detail) {
		if (invoiceDetails == null ) invoiceDetails = new HashSet<InvoiceDetail>();
		detail.setInvoice(this);
		invoiceDetails.remove(detail);
		invoiceDetails.add(detail);
	}
	
	public void deleteInvoiceDetail(InvoiceDetail detail) {
		if (invoiceDetails == null ) return;
		invoiceDetails.remove(detail);
	}
	
	public InvoiceDetail getInvoiceDetail(String id) {
		InvoiceDetail inv = null;
		for ( InvoiceDetail detail : invoiceDetails ) {
			if ( id.equals(detail.getId()) ) {
				inv = detail;
				break;
			}
		}
		return inv;
	}
	

	public void setInvoiceDetails(Set<InvoiceDetail> invoiceDetails) {
		for(Iterator<InvoiceDetail> it = invoiceDetails.iterator(); it.hasNext();){
			it.next().setInvoice(this);
		}
		this.invoiceDetails = invoiceDetails;
	}
	public double getTotalAmout(){
		double total = 0.00;
		if(invoiceDetails != null){
			for ( Iterator<InvoiceDetail> it = invoiceDetails.iterator(); it.hasNext(); ) {
				InvoiceDetail detail = it.next();
				total += detail.getAmount();
				
			}
		}
		return total;
	}
	/*
	 * This method is to CORRECT the spelling mistake above....
	 */
	public double getTotalAmount() {
		return getTotalAmout();
	}
	
	/*
	 * this method to getting actual total invoice amount including discount(-ve amount)
	 */
	public double getNegativeAmount(){
		double total = 0.00;
		if(invoiceDetails != null){
			for ( Iterator<InvoiceDetail> it = invoiceDetails.iterator(); it.hasNext(); ) {
				InvoiceDetail detail = it.next();
				System.out.println(detail.getDescription());
				System.out.println(detail.getAmount());
				double amount = 0;
				if(detail.getAmount() < 0)
					amount = detail.getAmount() * -1;
				total = total + amount;
			}
		}
		return total;
	}

	public Date trasactionDate() {
		return getInvoiceDate();
	}

	

	public String getVoids() {
		return voids;
	}

	public void setVoids(String voids) {
		this.voids = voids;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
		
	
}
