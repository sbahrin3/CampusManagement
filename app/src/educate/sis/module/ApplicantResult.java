package educate.sis.module;

public class ApplicantResult {
	
	private String programId;
	private String programCode;
	private String programName;
	private long total;
	
	public ApplicantResult(String id, String code, String name, long total) {
		programId = id;
		programCode = code;
		programName = name;
		this.total = total;
	}
	
	public String getProgramCode() {
		return programCode;
	}
	public String getProgramId() {
		return programId;
	}
	public String getProgramName() {
		return programName;
	}
	public long getTotal() {
		return total;
	}
	
	

}
