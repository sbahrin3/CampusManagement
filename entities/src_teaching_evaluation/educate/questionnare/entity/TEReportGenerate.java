package educate.questionnare.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="te_report_generate")
public class TEReportGenerate {
	
	@Id @Column(length=50)
	private String id;
	private Date generatedDate;
	@ManyToOne @JoinColumn(name="set_id")
	private TESet set;
	private Date startDate;
	
	public TEReportGenerate() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getGeneratedDate() {
		return generatedDate;
	}
	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
	}

	public TESet getSet() {
		return set;
	}

	public void setSet(TESet set) {
		this.set = set;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	

}
