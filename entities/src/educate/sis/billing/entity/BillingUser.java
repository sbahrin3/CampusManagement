package educate.sis.billing.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lebah.db.UniqueID;

@Entity
@Table(name="billing_user")
public class BillingUser {
	@Id
	private String id;
	private String userId;
	public BillingUser(){
		setId(UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
