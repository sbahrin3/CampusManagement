package educate.studentaffair.entity;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity @Table(name="stdaf_activity")
public class ClubActivity {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=100)
	private String shortName;
	@Column(length=200)
	private String name;
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate2;
	
	@ManyToOne @JoinColumn(name="club_id")
	private Club club;
	
	private String locationRemark;
	@Lob
	private String description;
	
	private String proposalDocumentName;
	@Column(length=50)
	private String proposalStatus; //in-process, approved, rejected
	@Temporal(TemporalType.TIMESTAMP)
	private Date statusDate;	
	private String adminRemark;
	
	private int day;
	private int month;
	private int year;
	
	private int day2;
	private int month2;
	private int year2;
	
	
	public ClubActivity() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
		
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		setDay(c.get(Calendar.DAY_OF_MONTH));
		setMonth(c.get(Calendar.MONTH));
		setYear(c.get(Calendar.YEAR));
		
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
		
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		setDay2(c.get(Calendar.DAY_OF_MONTH));
		setMonth2(c.get(Calendar.MONTH));
		setYear2(c.get(Calendar.YEAR));
		
		c.add(Calendar.DATE, 1);
		setEndDate2(c.getTime());

	}

	public Club getClub() {
		return club;
	}

	public void setClub(Club club) {
		this.club = club;
	}

	public String getLocationRemark() {
		return locationRemark;
	}

	public void setLocationRemark(String locationRemark) {
		this.locationRemark = locationRemark;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProposalDocumentName() {
		return proposalDocumentName;
	}

	public void setProposalDocumentName(String proposalDocumentName) {
		this.proposalDocumentName = proposalDocumentName;
	}

	public String getProposalStatus() {
		return proposalStatus;
	}

	public void setProposalStatus(String proposalStatus) {
		this.proposalStatus = proposalStatus;
	}

	public String getAdminRemark() {
		return adminRemark;
	}

	public void setAdminRemark(String adminRemark) {
		this.adminRemark = adminRemark;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getDay2() {
		return day2;
	}

	public void setDay2(int day2) {
		this.day2 = day2;
	}

	public int getMonth2() {
		return month2;
	}

	public void setMonth2(int month2) {
		this.month2 = month2;
	}

	public int getYear2() {
		return year2;
	}

	public void setYear2(int year2) {
		this.year2 = year2;
	}

	public Date getEndDate2() {
		return endDate2;
	}

	public void setEndDate2(Date endDate2) {
		this.endDate2 = endDate2;
	}	
	
}
