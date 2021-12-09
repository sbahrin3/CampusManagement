package educate.sis.finance.entity;

import java.util.Date;

import javax.persistence.*;


@Entity
@Table(name="fin_refund_log")
public class RefundLog {
	
	@Id
	private String id;
	private String userId;
	private String invoiceId;
	private String invoiceNo;
	private String studentId;
	private String studentMatricNo;
	private String studentName;
	private double refundAmount;
	@Temporal(TemporalType.DATE)
	private Date createDate;
	@Temporal(TemporalType.TIME)
	private Date createTime;
	private int isCancelled;
	private String remark;
	
	public RefundLog() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public double getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(double refundAmount) {
		this.refundAmount = refundAmount;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getStudentMatricNo() {
		return studentMatricNo;
	}
	public void setStudentMatricNo(String studentMatricNo) {
		this.studentMatricNo = studentMatricNo;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public void setCancelled(boolean c) {
		isCancelled = c ? 1 : 0;
	}
	
	public boolean getCancelled() {
		return isCancelled == 1;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	

}
