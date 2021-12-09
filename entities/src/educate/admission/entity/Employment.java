package educate.admission.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="app_employment")
public class Employment {
	@Id
	private String id;
	private String jobPosition;
	private String company;
	private String yfrom;
	private String yto;
	private int numberOfYears;
	
	public Employment() {
		setId(lebah.db.UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPosition() {
		return jobPosition;
	}
	public void setPosition(String position) {
		this.jobPosition = position;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public void setYfrom(String yfrom) {
		this.yfrom = yfrom;
	}
	public String getYfrom() {
		return yfrom;
	}
	public void setYto(String yto) {
		this.yto = yto;
	}
	public String getYto() {
		return yto;
	}
	@Override
	public boolean equals(Object o) {
		Employment result = (Employment)o;
		if(result.id.equals(id))
			return true;
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	public int getNumberOfYears() {
		return numberOfYears;
	}
	public void setNumberOfYears(int numberOfYears) {
		this.numberOfYears = numberOfYears;
	}
	
	
	
}
