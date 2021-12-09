package educate.sis.exam.entity;

import java.util.*;

import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Session;

public class TranscriptSection {
	
	private Session session;
	private Period period;
	private Vector<TranscriptSubject> subjects;
	private String creditHours;
	private String gpa;
	private String cumCreditHours;
	private String cgpa;
	private String standing;
	private String gpa1;
	private String cgpa1;
	private String studentType;
	private String isMaster;
	
	public String getStanding() {
		return standing;
	}
	public void setStanding(String s)
	{
		this.standing =s;
		
	}
	public void setStanding(double gpa, boolean level) 
		
	{
		System.out.println("GPA "+gpa);
		if(level==true){
			if(gpa >= 2.00){
				this.standing = "GOOD STANDING";
			}
		
			else if(gpa < 2.00){
				this.standing = "PROBATIONAL STANDING";
			}
			else{
				this.standing = "NO STANDING";
			}
		}
		
		if(level==false){
			if(gpa>=3.00){
				this.standing = "GOOD STANDING";
			}
		
			else if(gpa<3.00){
				this.standing = "PROBATIONAL STANDING";
			}
			else{
				this.standing = "";
			}
		}
	}
	public String getGpa1() {
		return gpa1;
	}
	
	public void setGpa1(double gpa1) {
		
		System.out.println("GPA "+gpa1);
		
			if(gpa1>= 3.70){
				this.gpa1 = "Dean's List"; 
			}
				else if (gpa1<3.70){
					this.gpa1 = "not";	
			}
				else{
					this.gpa1="";
				}
	}
	
	public String getCgpa1() {
		return cgpa1;
	}
	
	public void setCgpa1(double cgpa1) {
		
		System.out.println("CGPA "+cgpa1);
		
			if(cgpa1 >= 3.70){
				this.cgpa1 = "Dean"; 
			}
				else if (cgpa1<3.70){
					this.cgpa1 = "not Dean";	
			}
				else{
					this.cgpa1="";
				}
	}
	
	
			
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	
	public String getCgpa() {
		return cgpa;
	}
	
	public void setCgpa(String cgpa) {
		this.cgpa = cgpa;
	}
	public String getCreditHours() {
		return creditHours;
	}
	public void setCreditHours(String creditHours) {
		this.creditHours = creditHours;
	}
	public String getCumCreditHours() {
		return cumCreditHours;
	}
	public void setCumCreditHours(String cumCreditHrs) {
		this.cumCreditHours = cumCreditHrs;
	}
	public String getGpa() {
		return gpa;
	}
	public void setGpa(String gpa) {
		this.gpa = gpa;
	}
	
	
	public Vector<TranscriptSubject> getSubjects() {
		return subjects;
	}
	public void setSubjects(Vector<TranscriptSubject> subjects) {
		this.subjects = subjects;
	}
	public void addSubject(TranscriptSubject subject) {
		if ( subjects == null ) subjects = new Vector<TranscriptSubject>();
		subjects.addElement(subject);
	}
	
	public String getStudentType() {
		return studentType;
	}
	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}
	public String getIsMaster() {
		return isMaster;
	}
	public void setIsMaster(String isMaster) {
		this.isMaster = isMaster;
	}
	

}
