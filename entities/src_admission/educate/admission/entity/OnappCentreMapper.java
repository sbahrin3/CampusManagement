package educate.admission.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import onapp.entity.OnappLearningCentre;
import educate.sis.struct.entity.LearningCentre;

@Entity @Table(name="onapp_centre_mapper")
public class OnappCentreMapper {
	
	@Id @Column(length=50)
	private String id;
	
	@OneToOne @JoinColumn(name="onappCentre_id")
	private OnappLearningCentre onappCentre;
	@OneToOne @JoinColumn(name="centre_id")
	private LearningCentre centre;
	
	public OnappCentreMapper() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public OnappLearningCentre getOnappCentre() {
		return onappCentre;
	}
	public void setOnappCentre(OnappLearningCentre onappCentre) {
		this.onappCentre = onappCentre;
	}
	public LearningCentre getCentre() {
		return centre;
	}
	public void setCentre(LearningCentre centre) {
		this.centre = centre;
	}
	
	
	
	

}
