package educate.timetabling.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="ttb_sql_log")
public class SQLLog {
	
	@Id @Column(length=50)
	private String id;
	private String queryText;
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	public SQLLog() {
		setId(lebah.db.UniqueID.getUID());
		setDate(new Date());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getQueryText() {
		return queryText;
	}
	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
	

}
