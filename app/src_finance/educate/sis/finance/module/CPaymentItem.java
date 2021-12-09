package educate.sis.finance.module;

import java.text.DecimalFormat;

public class CPaymentItem {
	
	private String id;
	private String description;
	private String amount;
	private int flexi;
	private int ptptn;
	private int resourceFee;
	private double paymentAmount;
	
	public CPaymentItem(String a, double d) {
		id = lebah.db.UniqueID.getUID();
		description = a;
		paymentAmount = d;
		amount =  new DecimalFormat("#,###,###.00").format(paymentAmount);
	}
	
	/*
	public CPaymentItem(String a, double d, String flexiPayment, String ptptnPayment) {
		id = lebah.db.UniqueID.getUID();
		description = a;
		paymentAmount = d;
		flexi = "1".equals(flexiPayment) ? 1 : 0;
		ptptn = "1".equals(ptptnPayment) ? 1 : 0;
		amount =  new DecimalFormat("#,###,###.00").format(paymentAmount);
	}
	*/
	
	public CPaymentItem(String a, double d, boolean flexiPayment, boolean ptptnPayment, boolean resourceFeePayment) {
		id = lebah.db.UniqueID.getUID();
		description = a;
		paymentAmount = d;
		flexi = flexiPayment ? 1 : 0;
		ptptn = ptptnPayment ? 1 : 0;
		resourceFee = resourceFeePayment ? 1 : 0;
		amount =  new DecimalFormat("#,###,###.00").format(paymentAmount);
	}
	
	public CPaymentItem(String a, double d, String flexiPayment, String ptptnPayment, String resourceFeePayment) {
		id = lebah.db.UniqueID.getUID();
		description = a;
		paymentAmount = d;
		flexi = "1".equals(flexiPayment) ? 1 : 0;
		ptptn = "1".equals(ptptnPayment) ? 1 : 0;
		resourceFee = "1".equals(resourceFeePayment) ? 1 : 0;
		amount =  new DecimalFormat("#,###,###.00").format(paymentAmount);
	}
	
	public CPaymentItem(String id, String a, double d) {
		this.id = id;
		description = a;
		paymentAmount = d;
		amount =  new DecimalFormat("#,###,###.00").format(paymentAmount);
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean getFlexi() {
		return flexi == 1;
	}

	public void setFlexi(boolean flexi) {
		this.flexi = flexi? 1 : 0;
	}

	public boolean getPtptn() {
		return ptptn == 1;
	}

	public void setPtptn(boolean ptptn) {
		this.ptptn = ptptn ? 1 : 0;
	}

	public boolean getResourceFee() {
		return resourceFee == 1;
	}

	public void setResourceFee(boolean resourceFee) {
		this.resourceFee = resourceFee ? 1 : 0;
	}
	
	
	
	

}
