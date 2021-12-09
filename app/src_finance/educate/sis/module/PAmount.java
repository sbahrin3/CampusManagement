package educate.sis.module;

import educate.sis.struct.entity.Program;

public class PAmount {
	
	private String programId;
	private double amount;
	private Program program;
	
	public PAmount(String id, double d, Program program) {
		this.programId = id;
		this.amount = d;
		this.program = program;
	}	
	

	public double getAmount() {
		return amount;
	}


	
	public String getProgramId() {
		return programId;
	}


	public void setProgramId(String programId) {
		this.programId = programId;
	}


	@Override
	public boolean equals(Object o) {
		PAmount s = (PAmount) o;
		if ( s.getProgramId().equals(this.getProgramId())) return true;
		return false;
	}
	
	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}
	
	

}
