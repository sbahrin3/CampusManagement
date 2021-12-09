package educate.sis.billing.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lebah.db.UniqueID;
import metadata.EntityLister;

@Entity
@Table(name="bill_discount")
public class Discount implements EntityLister {
	@Id
	private String id;
	private String code;
	private double amount;
	private String description;
	private String type;
	
	public static final String ACTUAL_TYPE="ACTUAL";
	public static final String PERCENTAGE_TYPE ="PERCENTAGE";
	public Discount() {
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
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
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
