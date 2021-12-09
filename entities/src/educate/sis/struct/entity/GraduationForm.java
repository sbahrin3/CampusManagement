package educate.sis.struct.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="enrl_graduation_form")
public class GraduationForm {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=200)
	private String document1;
	@Column(length=200)
	private String document2;
	@Column(length=200)
	private String document3;
	@Column(length=200)
	private String document4;
	@Column(length=200)
	private String document5;
	@Column(length=200)
	private String document6;
	@Column(length=200)
	private String document7;
	@Column(length=200)
	private String document8;
	@Column(length=200)
	private String document9;
	@Column(length=200)
	private String document10;
	@Column(length=200)
	private String document11;
	@Column(length=200)
	private String document12;
	@Column(length=200)
	private String document13;
	@Column(length=200)
	private String document14;
	@Column(length=200)
	private String document15;
	@Column(length=200)
	private String document16;
	@Column(length=200)
	private String document17;
	@Column(length=200)
	private String document18;
	@Column(length=200)
	private String document19;
	@Column(length=200)
	private String document20;
	
	public GraduationForm() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDocument1() {
		return document1;
	}
	public void setDocument1(String document1) {
		this.document1 = document1;
	}
	public String getDocument2() {
		return document2;
	}
	public void setDocument2(String document2) {
		this.document2 = document2;
	}
	public String getDocument3() {
		return document3;
	}
	public void setDocument3(String document3) {
		this.document3 = document3;
	}
	public String getDocument4() {
		return document4;
	}
	public void setDocument4(String document4) {
		this.document4 = document4;
	}
	public String getDocument5() {
		return document5;
	}
	public void setDocument5(String document5) {
		this.document5 = document5;
	}
	public String getDocument6() {
		return document6;
	}
	public void setDocument6(String document6) {
		this.document6 = document6;
	}
	public String getDocument7() {
		return document7;
	}
	public void setDocument7(String document7) {
		this.document7 = document7;
	}
	public String getDocument8() {
		return document8;
	}
	public void setDocument8(String document8) {
		this.document8 = document8;
	}
	public String getDocument9() {
		return document9;
	}
	public void setDocument9(String document9) {
		this.document9 = document9;
	}
	public String getDocument10() {
		return document10;
	}
	public void setDocument10(String document10) {
		this.document10 = document10;
	}
	public String getDocument11() {
		return document11;
	}
	public void setDocument11(String document11) {
		this.document11 = document11;
	}
	public String getDocument12() {
		return document12;
	}
	public void setDocument12(String document12) {
		this.document12 = document12;
	}
	public String getDocument13() {
		return document13;
	}
	public void setDocument13(String document13) {
		this.document13 = document13;
	}
	public String getDocument14() {
		return document14;
	}
	public void setDocument14(String document14) {
		this.document14 = document14;
	}
	public String getDocument15() {
		return document15;
	}
	public void setDocument15(String document15) {
		this.document15 = document15;
	}
	public String getDocument16() {
		return document16;
	}
	public void setDocument16(String document16) {
		this.document16 = document16;
	}
	public String getDocument17() {
		return document17;
	}
	public void setDocument17(String document17) {
		this.document17 = document17;
	}
	public String getDocument18() {
		return document18;
	}
	public void setDocument18(String document18) {
		this.document18 = document18;
	}
	public String getDocument19() {
		return document19;
	}
	public void setDocument19(String document19) {
		this.document19 = document19;
	}
	public String getDocument20() {
		return document20;
	}
	public void setDocument20(String document20) {
		this.document20 = document20;
	}
	
	
	
	

}
