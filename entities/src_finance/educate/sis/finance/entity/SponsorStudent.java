package educate.sis.finance.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.enrollment.entity.Student;

@Entity
@Table(name="fin_sponsor_student")
public class SponsorStudent {
	
	@Id
	private String id;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Sponsor sponsor;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Student student;
	//there should be more attributes
	//like.. date start sponsored and until date ended.
	//and.. maybe amount of sponsored values 
	
	@Temporal(TemporalType.DATE)
	private Date sponsorDateStart;
	@Temporal(TemporalType.DATE)
	private Date sponsorDateEnd;
	private double sponsorAmount;
	@Column(length=100)
	private String sponsorRefNo;
	
	
	
	public SponsorStudent() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Sponsor getSponsor() {
		return sponsor;
	}

	public void setSponsor(Sponsor sponsor) {
		this.sponsor = sponsor;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Date getSponsorDateStart() {
		return sponsorDateStart;
	}

	public void setSponsorDateStart(Date sponsorDateStart) {
		this.sponsorDateStart = sponsorDateStart;
	}

	public Date getSponsorDateEnd() {
		return sponsorDateEnd;
	}

	public void setSponsorDateEnd(Date sponsorDateEnd) {
		this.sponsorDateEnd = sponsorDateEnd;
	}

	public double getSponsorAmount() {
		return sponsorAmount;
	}

	public void setSponsorAmount(double sponsorAmount) {
		this.sponsorAmount = sponsorAmount;
	}

	public String getSponsorRefNo() {
		return sponsorRefNo;
	}

	public void setSponsorRefNo(String sponsorRefNo) {
		this.sponsorRefNo = sponsorRefNo;
	}
	

}
