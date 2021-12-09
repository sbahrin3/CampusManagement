package educate.sis.billing.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="bill_productservice")
public class ProductService implements Serializable {
	@Id
	@Column(nullable=false,length=5)
	private String id;
	private String description;
	private double rate;
	
	private double week1;
	private double rate1;
	private double week2;
	private double rate2;
	private double week3;
	private double rate3;
	public ProductService(){
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getWeek1() {
		return week1;
	}

	public void setWeek1(double week1) {
		this.week1 = week1;
	}

	public double getRate1() {
		return rate1;
	}

	public void setRate1(double rate1) {
		this.rate1 = rate1;
	}

	public double getWeek2() {
		return week2;
	}

	public void setWeek2(double week2) {
		this.week2 = week2;
	}

	public double getRate2() {
		return rate2;
	}

	public void setRate2(double rate2) {
		this.rate2 = rate2;
	}

	public double getWeek3() {
		return week3;
	}

	public void setWeek3(double week3) {
		this.week3 = week3;
	}

	public double getRate3() {
		return rate3;
	}

	public void setRate3(double rate3) {
		this.rate3 = rate3;
	}
	
}
