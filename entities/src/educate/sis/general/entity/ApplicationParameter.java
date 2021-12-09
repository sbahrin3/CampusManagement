package educate.sis.general.entity;
import javax.persistence.*;

@Entity
@Table(name="app_param")
public class ApplicationParameter {
	
	@Id
	private String id;
	private double fee;  //for Rm
	private double fee2; //for us dollar
	private String accName;
	private String accNo;
	private String bankName;
	private String bankAdd;
	private String swiftCode;
	private String telNo;
	private String faxNo;
	private String email;
	private String uniAdd;
	private String ltrApprover;
	private String designation;
	private String uniUrl;
			
	public ApplicationParameter(){
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public double getFee2() {
		return fee2;
	}

	public void setFee2(double fee2) {
		this.fee2 = fee2;
	}

	public String getAccName() {
		return accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAdd() {
		return bankAdd;
	}

	public void setBankAdd(String bankAdd) {
		this.bankAdd = bankAdd;
	}

	public String getSwiftCode() {
		return swiftCode;
	}

	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}

	public String getTelNo() {
		return telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}

	public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUniAdd() {
		return uniAdd;
	}

	public void setUniAdd(String uniAdd) {
		this.uniAdd = uniAdd;
	}

	public String getLtrApprover() {
		return ltrApprover;
	}

	public void setLtrApprover(String ltrApprover) {
		this.ltrApprover = ltrApprover;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getUniUrl() {
		return uniUrl;
	}

	public void setUniUrl(String uniUrl) {
		this.uniUrl = uniUrl;
	}

	
}
