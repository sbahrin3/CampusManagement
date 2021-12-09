package educate.sis.billing.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lebah.db.UniqueID;

import educate.enrollment.entity.Student;
import educate.sis.struct.entity.Session;

@Entity
@Table(name="shoping_statement")
public class ShoppingStatement implements Comparable {
	@Id
	private String id;
	private String descripition;
	@Temporal(TemporalType.DATE)
	private Date date;
	private double debit;
	private double credit;
	private String type;
	@OneToOne
	private Student student;
	private double balance;
	private int seq;
	public static final String PAYMENT="PAYMENT";
	public static final String INVOICE ="INVOICE";
	@ManyToOne
	private Session session;
	public ShoppingStatement(){
		setId(UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescripition() {
		return descripition;
	}
	public void setDescripition(String descripition) {
		this.descripition = descripition;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getDebit() {
		return debit;
	}
	public void setDebit(double debit) {
		this.debit = debit;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	
	public int compareTo(Object o) {
		ShoppingStatement a = (ShoppingStatement)o;
		if(a.seq > seq)
			return -1;
		else if(a.seq < seq)
			return 1;
		return 0;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	
	
}
