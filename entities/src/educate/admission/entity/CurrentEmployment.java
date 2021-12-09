package educate.admission.entity;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import educate.sis.general.entity.EmploymentStatus;
import educate.sis.general.entity.Position;
import educate.sis.general.entity.Sector;

@Embeddable
public class CurrentEmployment {
	@ManyToOne
	private EmploymentStatus status;
	@ManyToOne
	private Sector sector;
	private String companyName;
	private String dateJoin;
	private int experience;
	@ManyToOne
	private Position position;
	public CurrentEmployment(){
		
	}
	public EmploymentStatus getStatus() {
		return status;
	}
	public void setStatus(EmploymentStatus status) {
		this.status = status;
	}
	public Sector getSector() {
		return sector;
	}
	public void setSector(Sector sector) {
		this.sector = sector;
	}
	public int getExperience() {
		return experience;
	}
	public void setExperience(int experience) {
		this.experience = experience;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setDateJoin(String dateJoin) {
		this.dateJoin = dateJoin;
	}
	public String getDateJoin() {
		return dateJoin;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	public Position getPosition() {
		return position;
	}
}
