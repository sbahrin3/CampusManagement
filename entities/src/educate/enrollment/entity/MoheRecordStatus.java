package educate.enrollment.entity;

import java.util.Date;

import javax.persistence.*;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0 
 * @date 27.8.2011
 */
@Entity
@Table(name="mohe_record_status")
public class MoheRecordStatus {
	
	@Id
	private String id;
	private String createdBy;
	@Temporal(TemporalType.DATE)
	private Date dateCreated;
	private int countLocal;
	private int countInternational;
	
	public MoheRecordStatus() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCountInternational() {
		return countInternational;
	}

	public void setCountInternational(int countInternational) {
		this.countInternational = countInternational;
	}

	public int getCountLocal() {
		return countLocal;
	}

	public void setCountLocal(int countLocal) {
		this.countLocal = countLocal;
	}


	
	

}
