package educate.admission.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import onapp.entity.OnappIntakeSession;
import educate.sis.struct.entity.Session;

@Entity @Table(name="onapp_intake_mapper")
public class OnappIntakeMapper {
	
	@Id @Column(length=50)
	private String id;
	
	@OneToOne @JoinColumn(name="onappIntake_id")
	private OnappIntakeSession onappIntake;
	@OneToOne @JoinColumn(name="intake_id")
	private Session intake;
	
	public OnappIntakeMapper() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public OnappIntakeSession getOnappIntake() {
		return onappIntake;
	}
	public void setOnappIntake(OnappIntakeSession onappIntake) {
		this.onappIntake = onappIntake;
	}
	public Session getIntake() {
		return intake;
	}
	public void setIntake(Session intake) {
		this.intake = intake;
	}
	
	

}
