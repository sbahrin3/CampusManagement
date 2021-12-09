package educate.admission.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EnglishProfiency {
	@Column(precision=10)
	private int toefl;
	private String ielts;
	public int getToefl() {
		return toefl;
	}
	public void setToefl(int toefl) {
		this.toefl = toefl;
	}
	public String getIelts() {
		return ielts;
	}
	public void setIelts(String ielts) {
		this.ielts = ielts;
	}
	
}
