package educate.sis.billing.entity;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.sis.struct.entity.Session;

@Entity
@Table(name="bill_parameter")
public class FinanceParameter {
	@Id
	private String id;
	@Temporal(TemporalType.DATE)
	private Date dicountExpiryDate;
	@ManyToOne
	private Session session;
	
	private double discountPercentage;
	private double rebateAmount;
	public FinanceParameter() {
		setId(lebah.db.UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getDicountExpiryDate() {
		return dicountExpiryDate;
	}
	public void setDicountExpiryDate(String date) {
		if (date == null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setDicountExpiryDate(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setDicountExpiryDate(Object d){
		if (d instanceof Date) dicountExpiryDate=(Date) d;
		else if (d instanceof String){
			setDicountExpiryDate((String) d);
		}
	}
	
	public void setSession(Session session) {
		this.session = session;
	}
	public Session getSession() {
		return session;
	}
	public double getDiscountPercentage() {
		return discountPercentage;
	}
	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	public double getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(double rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	
	
}
