package educate.sis.struct.entity;

import javax.persistence.*;

@Entity
@Table(name="struc_fee_table")
public class RegistrationFeeTable {

	@Id
	@Column(length=55)
	private String id;
	private String feeType; //TUITION, ACCOMODATION
	@ManyToOne
	private Program program;
	@ManyToOne
	private Period period;
	private double amount;
	
	public RegistrationFeeTable() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	public Program getProgram() {
		return program;
	}
	public void setProgram(Program program) {
		this.program = program;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
}
