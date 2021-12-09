package educate.timetabling.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.sis.struct.entity.Session;

@Entity
@Table(name="ttb_auto_flage")
public class AutoGeneratorFlag {
	
	@Id
	private String id;
	@ManyToOne
	private Session session;
	private int week;
	private int generated;
	
	public AutoGeneratorFlag() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public int getWeek() {
		return week;
	}
	public void setWeek(int week) {
		this.week = week;
	}
	public int getGenerated() {
		return generated;
	}
	public void setGenerated(int generated) {
		this.generated = generated;
	}
	
	
}
