package migration.module;

public class Item {
	
	private String matricNo;
	private String sessionCode;
	private String itemNo;
	private String itemDate;
	private String itemCode;
	private String itemDescription;
	private double amount;
	
	private String paymentMode;
	private String bankName;
	private String checkNo;
	
	public Item(String matricNo, String sessionCode, String itemNo, String itemDate, String itemCode, String itemDescription, double amount) {
		this.matricNo = matricNo;
		this.sessionCode = sessionCode;
		this.itemNo = itemNo;
		this.itemDate = itemDate;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.amount = amount;
	}

	
	

	public Item(String matricNo, String itemNo, String itemDate, String itemDescription, String paymentMode, String bankName, String checkNo, double amount) {
		this.matricNo = matricNo;
		this.itemNo = itemNo;
		this.itemDate = itemDate;
		this.itemDescription = itemDescription;
		this.amount = amount;
		this.paymentMode = paymentMode;
		this.bankName = bankName;
		this.checkNo = checkNo;
	}
	
	
	public String getMatricNo() {
		return matricNo;
	}
	public void setMatricNo(String matricNo) {
		this.matricNo = matricNo;
	}
	public String getSessionCode() {
		return sessionCode;
	}
	public void setSessionCode(String sessionCode) {
		this.sessionCode = sessionCode;
	}

	public String getItemDescription() {
		return itemDescription;
	}
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getItemNo() {
		return itemNo;
	}

	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
	}

	public String getItemDate() {
		return itemDate;
	}

	public void setItemDate(String itemDate) {
		this.itemDate = itemDate;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}



	public String getPaymentMode() {
		return paymentMode;
	}



	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}



	public String getBankName() {
		return bankName;
	}



	public void setBankName(String bankName) {
		this.bankName = bankName;
	}



	public String getCheckNo() {
		return checkNo;
	}



	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}


	

}
