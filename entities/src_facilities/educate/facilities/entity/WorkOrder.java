package educate.facilities.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity @Table(name="fac_workorder")
public class WorkOrder {
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="issue_id")
	private WorkOrderIssue issue;
	
	@Column(length=200)
	private String issueDescription;
	
	@ManyToOne @JoinColumn(name="category_id")
	private WorkCategory category;	
	
	private String officerName;
	private String officerEmail;
	@Temporal(TemporalType.DATE)
	private Date startDate;
	@Temporal(TemporalType.DATE)
	private Date closeDate;
	private String actionRemark;
	private String status; //open, close, overdue
	
	public WorkOrder() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public WorkOrderIssue getIssue() {
		return issue;
	}
	public void setIssue(WorkOrderIssue issue) {
		this.issue = issue;
	}
	public WorkCategory getCategory() {
		return category;
	}
	public void setCategory(WorkCategory category) {
		this.category = category;
	}
	public String getOfficerName() {
		return officerName;
	}
	public void setOfficerName(String officerName) {
		this.officerName = officerName;
	}
	public String getOfficerEmail() {
		return officerEmail;
	}
	public void setOfficerEmail(String officerEmail) {
		this.officerEmail = officerEmail;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getCloseDate() {
		return closeDate;
	}
	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getActionRemark() {
		return actionRemark;
	}

	public void setActionRemark(String actionRemark) {
		this.actionRemark = actionRemark;
	}

	public String getIssueDescription() {
		return issueDescription;
	}

	public void setIssueDescription(String issueDescription) {
		this.issueDescription = issueDescription;
	}
	
	
	
	
}
