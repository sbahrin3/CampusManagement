package educate.questionnare.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="te_user_log")
public class TEUserLog {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=100)
	private String userSessionId;
	@Column(length=50)
	private String remoteAddress;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	@ManyToOne @JoinColumn(name="set_id")
	private TESet set;
	@ManyToOne @JoinColumn(name="questionnare_id")
	private TEQuestionnare questionnare;	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="userLog")
	private List<TEUserPath> userPaths;
	
	private int complete;
	@Temporal(TemporalType.DATE)
	private Date completeDate;
	
	
	
	@Column(length=50)
	private String userId;
	
	public TEUserLog() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserSessionId() {
		return userSessionId;
	}
	public void setUserSessionId(String userSessionId) {
		this.userSessionId = userSessionId;
	}
	public String getRemoteAddress() {
		return remoteAddress;
	}
	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public TESet getSet() {
		return set;
	}
	public void setSet(TESet set) {
		this.set = set;
	}

	public List<TEUserPath> getUserPaths() {
		if ( userPaths == null ) userPaths = new ArrayList<TEUserPath>();
		return userPaths;
	}

	public void setUserPaths(List<TEUserPath> userPaths) {
		this.userPaths = userPaths;
	}

	public TEQuestionnare getQuestionnare() {
		return questionnare;
	}

	public void setQuestionnare(TEQuestionnare questionnare) {
		this.questionnare = questionnare;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean getComplete() {
		return complete == 1;
	}

	public void setComplete(boolean complete) {
		this.complete = complete ? 1 : 0;
	}

	public Date getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}
	
	
	

}
