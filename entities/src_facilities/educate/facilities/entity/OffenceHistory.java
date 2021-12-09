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

@Entity @Table(name="fac_offence_history")
public class OffenceHistory {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=220)
	private String description;
	@Temporal(TemporalType.DATE)
	private Date date;
	@Column(length=50)
	private String action; //warning, clamp, compound
	@ManyToOne @JoinColumn(name="carsticker_id")
	private CarSticker carSticker;
	
	public OffenceHistory() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	public CarSticker getCarSticker() {
		return carSticker;
	}

	public void setCarSticker(CarSticker carSticker) {
		this.carSticker = carSticker;
	}
	
	

}
