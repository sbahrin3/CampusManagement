package educate.alumni.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity @Table(name="almn_secretariat")
public class AlumniSecretariat {

	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="member_id")
	private AlumniMember member;
	@Temporal(TemporalType.DATE)
	private Date startDate;
	@Temporal(TemporalType.DATE)
	private Date endDate;
	@ManyToOne @JoinColumn(name="position_id")
	private SecretariatPosition position;
	
	public AlumniSecretariat() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public AlumniMember getMember() {
		return member;
	}
	public void setMember(AlumniMember member) {
		this.member = member;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public SecretariatPosition getPosition() {
		return position;
	}

	public void setPosition(SecretariatPosition position) {
		this.position = position;
	}
	
	

}
