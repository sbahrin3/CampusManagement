package educate.sis.billing.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lebah.db.UniqueID;
import metadata.EntityLister;

@Entity
@Table(name="bill_item")
public class PaymentItem implements EntityLister {
	@Id
	private String id;
	private String code;
	private String description;
	private String type;
	
	public static final String PAYMENT_TYPE="PAYMENT";
	public static final String INVOICE_TYPE ="INVOICE";
	public static final String BOTH_TYPE ="BOTH";
	
	public static final String RF_CODE ="RF";
	public static final String NRF_CODE ="NRF";
	public static final String TF_CODE ="TF";
	public static final String EF_CODE ="EF";
	public static final String DISCOUNT_CODE ="DIS";
	public static final String PAYMENT_CODE ="FP";
	public static final String AF_CODE ="AF";
	public PaymentItem() {
		setId(UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getValue() {
		return getCode()+" "+getDescription();
	}
	
}
