package educate.sis.struct.entity;

import javax.persistence.*;

@Entity
@Table(name="struc_program_fee")
public class ProgramFee {
	
	@Id
	private String id;
	@OneToOne
	private Program program;
	private double recurringFee;
	private double nonRecurringFee;
	private double tuitionFee;
	private double examFee;
	private double minimumPayment;
	private double discount;
    private double onlineTuitionFee;
    @Column(name="start_year", length=4)
    private int startYear;
    @Column(name="end_year", length=4)
    private int endYear;
    
	public ProgramFee() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public ProgramFee(Program p) {
		setId(lebah.util.UIDGenerator.getUID());
		this.program = p;
	}
	
	public double getExamFee() {
		return examFee;
	}
	public void setExamFee(double examFee) {
		this.examFee = examFee;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getNonRecurringFee() {
		return nonRecurringFee;
	}
	public void setNonRecurringFee(double nonRecurringFee) {
		this.nonRecurringFee = nonRecurringFee;
	}
	public Program getProgram() {
		return program;
	}
	public void setProgram(Program program) {
		this.program = program;
	}
	public double getRecurringFee() {
		return recurringFee;
	}
	public void setRecurringFee(double recurringFee) {
		this.recurringFee = recurringFee;
	}
	public double getTuitionFee() {
		return tuitionFee;
	}
	public void setTuitionFee(double tuitionFee) {
		this.tuitionFee = tuitionFee;
	}

	public double getMinimumPayment() {
		return minimumPayment;
	}

	public void setMinimumPayment(double minimumPayment) {
		this.minimumPayment = minimumPayment;
	}

	public double getTotalPayment() {
		return recurringFee + nonRecurringFee + tuitionFee + examFee;
	}
	
	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

    public double getOnlineTuitionFee() {
        return onlineTuitionFee;
    }

    public void setOnlineTuitionFee(double onlineTuitionFee) {
        this.onlineTuitionFee = onlineTuitionFee;
    }

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}	
}
