package educate.sis.module;

public class ItemCheckStat {
	
	private String programCode;
	private String programName;
	private long count;
	
	public ItemCheckStat(String programCode, String programName, long count) {
		this.programCode = programCode;
		this.programName = programName;
		this.count = count;
	}

	public String getProgramCode() {
		return programCode;
	}

	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
	
	

}
