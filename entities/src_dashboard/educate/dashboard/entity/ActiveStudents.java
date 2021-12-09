package educate.dashboard.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "stat_active_students")
public class ActiveStudents {
	
	@Id @Column(length=50)
	private String id;
	@Temporal(TemporalType.DATE)
	private Date date;
	private long value;
	@Column(length=50)
	private String groupName;
	@Column(length=50)
	private String categoryId;
	
	public ActiveStudents() {
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
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}

	public String getGroupName() {
		return groupName; 
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	
	

}
