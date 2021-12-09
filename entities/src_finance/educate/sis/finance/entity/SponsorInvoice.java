package educate.sis.finance.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;

@Entity
@Table(name="fin_sponsor_invoice")
public class SponsorInvoice {
	
	@Id
	private String id;
	@ManyToOne
	private Sponsor sponsor;
	@ManyToOne
	private Session session;
	@ManyToOne
	private Program program;
	
	private double amount;
	@Temporal(TemporalType.DATE)
	private Date invoiceDate;
	
	@Temporal(TemporalType.DATE)
	private Date dateFrom;
	@Temporal(TemporalType.DATE)
	private Date dateTo;
	
	private int deleted;
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="sponsorInvoice")
	private List<Invoice> invoices;
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="sponsorInvoice")
	private List<SponsorPayment> sponsorPayments;

	
	public SponsorInvoice() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public Sponsor getSponsor() {
		return sponsor;
	}
	public void setSponsor(Sponsor sponsor) {
		this.sponsor = sponsor;
	}


	public Date getInvoiceDate() {
		return invoiceDate;
	}


	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	
	public void setDeleted(boolean del) {
		deleted = del ? 1 : 0;
	}
	
	public boolean getDeleted() {
		return deleted == 1;
	}


	public List<Invoice> getInvoices() {
		return invoices;
	}


	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}


	public List<SponsorPayment> getSponsorPayments() {
		return sponsorPayments;
	}


	public void setSponsorPayments(List<SponsorPayment> sponsorPayments) {
		this.sponsorPayments = sponsorPayments;
	}


	public Program getProgram() {
		return program;
	}


	public void setProgram(Program program) {
		this.program = program;
	}


	public Date getDateFrom() {
		return dateFrom;
	}


	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}


	public Date getDateTo() {
		return dateTo;
	}


	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
	
	

}
