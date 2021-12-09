package educate.sis.finance.module;


import java.util.List;


public class AccountStatement {
	
	private List<Record> records;
	private double totalInvoiced;
	private double totalPaid;
	private double totalBalance;
	
	public List<Record> getRecords() {
		return records;
	}
	public void setRecords(List<Record> records) {
		this.records = records;
	}
	public double getTotalBalance() {
		return totalBalance;
	}
	public void setTotalBalance(double totalBalance) {
		this.totalBalance = totalBalance;
	}
	public double getTotalInvoiced() {
		return totalInvoiced;
	}
	public void setTotalInvoiced(double totalInvoiced) {
		this.totalInvoiced = totalInvoiced;
	}
	public double getTotalPaid() {
		return totalPaid;
	}
	public void setTotalPaid(double totalPaid) {
		this.totalPaid = totalPaid;
	}
	
	

}
