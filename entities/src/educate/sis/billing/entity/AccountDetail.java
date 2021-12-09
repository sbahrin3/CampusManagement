package educate.sis.billing.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lebah.db.UniqueID;

import educate.enrollment.entity.Student;

@Entity
@Table(name="acct_detail")
public class AccountDetail implements Comparable {
	@Id
	private String id;
	private String item;
	private double debit;
	private double credit;
	private double balance;
	@OneToOne
	private Student student;
	private int seq;
	private String code;
	public AccountDetail() {
		setId(UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
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
	
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int compareTo(Object o) {
		AccountDetail d = (AccountDetail)o;
		if(d.seq > seq)
			return -1;
		else if(d.seq < seq)
			return 1;
		
		return 0;
	}
	
}
