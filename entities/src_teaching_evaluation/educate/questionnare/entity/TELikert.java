package educate.questionnare.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="te_likert")
public class TELikert {
	
	@Id
	private int id;
	@Column(length=50)
	private String label1;
	@Column(length=50)
	private String label2;
	@Column(length=50)
	private String label3;
	@Column(length=50)
	private String label4;
	@Column(length=50)
	private String label5;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLabel1() {
		return label1;
	}
	public void setLabel1(String label1) {
		this.label1 = label1;
	}
	public String getLabel2() {
		return label2;
	}
	public void setLabel2(String label2) {
		this.label2 = label2;
	}
	public String getLabel3() {
		return label3;
	}
	public void setLabel3(String label3) {
		this.label3 = label3;
	}
	public String getLabel4() {
		return label4;
	}
	public void setLabel4(String label4) {
		this.label4 = label4;
	}
	public String getLabel5() {
		return label5;
	}
	public void setLabel5(String label5) {
		this.label5 = label5;
	}
	
	
}

