package portal.module.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="p_announcement")
public class Announcement {
	
	@Id
	@Column(length=50)
	private String id;
	@Column(length=50)
	private String moduleId;
	@Column(length=50)
	private String postedById;
	@Column(length=200)
	private String postedByName;
	@Temporal(TemporalType.TIMESTAMP)
	private Date datePosted;
	@Temporal(TemporalType.DATE)
	private Date dateExpired;
	@Column(length=200)
	private String title;
	@Lob
	@Column(length=1000)
	private String text;
	
	private String fileName;
	private String serverFileName;

	
	
	public Announcement() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPostedById() {
		return postedById;
	}
	public void setPostedById(String postedById) {
		this.postedById = postedById;
	}
	public String getPostedByName() {
		return postedByName;
	}
	public void setPostedByName(String postedByName) {
		this.postedByName = postedByName;
	}
	public Date getDatePosted() {
		return datePosted;
	}
	public void setDatePosted(Date datePosted) {
		this.datePosted = datePosted;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDateExpired() {
		return dateExpired;
	}

	public void setDateExpired(Date dateExpired) {
		this.dateExpired = dateExpired;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getServerFileName() {
		return serverFileName;
	}

	public void setServerFileName(String serverFileName) {
		this.serverFileName = serverFileName;
	}
	
	
	
	

}
