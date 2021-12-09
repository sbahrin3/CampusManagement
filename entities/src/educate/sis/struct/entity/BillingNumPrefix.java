package educate.sis.struct.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="struct_billing_num_prefix")
public class BillingNumPrefix {
	
	@Id
	private String id;
	private String prefix;
	
	private int counter;
	private int digit;
	
	public BillingNumPrefix(){
		setId(id);
	}
	public BillingNumPrefix(String id) {
		setId(id);
	} 

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public int getDigit() {
		return digit;
	}
	public void setDigit(int digit) {
		this.digit = digit;
	}
	
	


}
