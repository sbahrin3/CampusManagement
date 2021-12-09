package educate.timetabling.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="ttb_audit_trail")
public class AuditTrailLog {
	
	@Id @Column(length=50)
	private String id;
    @Temporal(TemporalType.TIMESTAMP) @Column(name="log_date")
    private Date date;
    @Column(length=50)
    private String userId;
    @Column(length=255)
    private String logText;
    @Column(length=255)
    private String remarks;
    @Column(length=100)
    private String remoteAddr;
    
    public AuditTrailLog() {
    	setId(lebah.db.UniqueID.getUID());
    }
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getLogText() {
		return logText;
	}
	public void setLogText(String logText) {
		this.logText = logText;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
    
    
    
}
