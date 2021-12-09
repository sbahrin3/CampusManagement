package educate.admission.entity;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name="educationdetail")
public class EducationDetail {
	
	@Id
	private String id;
	@Temporal(TemporalType.DATE)
	private Date dateFrom;
	@Temporal(TemporalType.DATE)
	private Date dateTo;
	private String institutionName;
	private String certificationName;
	private String result;
	
	public EducationDetail() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getCertificationName() {
		return certificationName;
	}
	public void setCertificationName(String certificationName) {
		this.certificationName = certificationName;
	}
	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Object d) {
		if ( d instanceof Date ) dateFrom = (Date) d;
		else if ( d instanceof String) {
			setDateFrom((String) d);
		}
	}
	
	public void setDateFrom(String date) {
		if (date == null || "".equals(date)) return;
		String separator = "-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setDateFrom(new GregorianCalendar(year, month, day).getTime());
	}	
	public Date getDateTo() {
		return dateTo;
	}
	public void setDateTo(Object d) {
		if ( d instanceof Date ) dateTo = (Date) d;
		else if ( d instanceof String) {
			setDateTo((String) d);
		}		
	}
	
	public void setDateTo(String date) {
		if (date == null || "".equals(date)) return;
		String separator = "-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setDateTo(new GregorianCalendar(year, month, day).getTime());
	}	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInstitutionName() {
		return institutionName;
	}
	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	

}
