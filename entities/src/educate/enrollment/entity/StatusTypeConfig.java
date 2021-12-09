package educate.enrollment.entity;

import javax.persistence.*;

@Entity
@Table(name="enrl_statustype_cfg")
public class StatusTypeConfig {

	@Id
	private String id;
	private StatusType defaultStatus;
	private StatusType graduateStatus;
	private StatusType terminateStatus;
	
	public StatusTypeConfig() {
		setId(lebah.db.UniqueID.getUID());
	}

	public StatusType getDefaultStatus() {
		return defaultStatus;
	}

	public void setDefaultStatus(StatusType defaultStatus) {
		this.defaultStatus = defaultStatus;
	}

	public StatusType getGraduateStatus() {
		return graduateStatus;
	}

	public void setGraduateStatus(StatusType graduateStatus) {
		this.graduateStatus = graduateStatus;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public StatusType getTerminateStatus() {
		return terminateStatus;
	}

	public void setTerminateStatus(StatusType terminateStatus) {
		this.terminateStatus = terminateStatus;
	}
	
	
	
	
}
