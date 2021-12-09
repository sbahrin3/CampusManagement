package educate.sis.billing.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="bill_academicservicesrefund")
public class AcademicSevicesRefundValue implements Serializable{
	@Id
	private String id;
	private int week1;
	private double rate1;
	private int week2;
	private double rate2;
	private int week3;
	private double rate3;
	public AcademicSevicesRefundValue(){
		
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getWeek1() {
		return week1;
	}
	public void setWeek1(int week1) {
		this.week1 = week1;
	}
	public double getRate1() {
		return rate1;
	}
	public void setRate1(double rate1) {
		this.rate1 = rate1;
	}
	public int getWeek2() {
		return week2;
	}
	public void setWeek2(int week2) {
		this.week2 = week2;
	}
	public double getRate2() {
		return rate2;
	}
	public void setRate2(double rate2) {
		this.rate2 = rate2;
	}
	public int getWeek3() {
		return week3;
	}
	public void setWeek3(int week3) {
		this.week3 = week3;
	}
	public double getRate3() {
		return rate3;
	}
	public void setRate3(double rate3) {
		this.rate3 = rate3;
	}
	
}
