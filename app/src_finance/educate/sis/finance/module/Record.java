package educate.sis.finance.module;

import java.util.Date;

public class Record implements Comparable {
	
	private String id;
	private Date date;
	private String referenceNo;
	private String description;
	private String matricNo;
	private String icno;
	private String name;
	private double invoiced;
	private double paid;
	private double balance;
	private int side;
	private double amount;
	private String remark;
	
	public Record(Date date, String ref, String description, double amount, int side) {
		this.date = date;
		this.referenceNo = ref;
		this.description = description;
		this.amount = amount;
		this.side = side;
	}
	
	public Record(Date date, String description, double amount, int side) {
		this.date = date;
		this.description = description;
		this.amount = amount;
		this.side = side;
	}

	public Record(String s, String matricNo, String name,
			double d1, double d2) {
		id = s;
		this.matricNo = matricNo;
		this.name = name;
		invoiced = d1;
		paid = d2;
		balance = d1 - d2;
	}
	
	public Record(String s, String matricNo, String icno, String name,
			double d1, double d2) {
		id = s;
		this.matricNo = matricNo;
		this.icno = icno;
		this.name = name;
		invoiced = d1;
		paid = d2;
		balance = d1 - d2;
	}
		
	
	public Record(String s, String matricNo, String name, Date date,
			double d1, double d2) {
		id = s;
		this.matricNo = matricNo;
		this.name = name;
		this.date = date;
		invoiced = d1;
		paid = d2;
		balance = d1 - d2;
	}
	
	public Record(String s, String matricNo, String icno, String name, Date date,
			double d1, double d2) {
		id = s;
		this.matricNo = matricNo;
		this.icno = icno;
		this.name = name;
		this.date = date;
		invoiced = d1;
		paid = d2;
		balance = d1 - d2;
	}	
	
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getInvoiced() {
		return invoiced;
	}
	public void setInvoiced(double invoiced) {
		this.invoiced = invoiced;
	}
	public double getPaid() {
		return paid;
	}
	public void setPaid(double paid) {
		this.paid = paid;
	}

	public String getMatricNo() {
		return matricNo;
	}

	public String getName() {
		return name;
	}

	public Date getDate() {
		return date;
	}

	public double getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public int getSide() {
		return side;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public int compareTo(Object o) {
		Record r1 = (Record) o;
		return r1.getDate().after(getDate()) ? 0 : 1;
	}

	public String getIcno() {
		return icno;
	}

	public void setIcno(String icno) {
		this.icno = icno;
	}
	
	

}
