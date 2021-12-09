package educate.admission.entity;
import javax.persistence.*;

@Embeddable
public class PaymentInfo {

	private String resitNo;
	private String slipNo;
	
	public PaymentInfo(){
		
	}
	
	public String getResitNo() {
		return resitNo;
	}
	public void setResitNo(String resitNo) {
		this.resitNo = resitNo;
	}
	public String getSlipNo() {
		return slipNo;
	}
	public void setSlipNo(String slipNo) {
		this.slipNo = slipNo;
	}
}
