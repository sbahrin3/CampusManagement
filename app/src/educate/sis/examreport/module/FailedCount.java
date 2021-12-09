package educate.sis.examreport.module;

public class FailedCount {
	
	private String programCode;
	private int count;
	
	public FailedCount(String s, int i) {
		programCode = s;
		count = i;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getProgramCode() {
		return programCode;
	}
	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}
	
	
	

}
