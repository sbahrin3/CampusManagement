package educate.enrollment.module;

public class Value {
	
	private String name;
	private String matricNo;
	private String programName;
	private String statusName;
	private String sessionName;

	public Value(String name, String matricNo, String programName, String sessionName, String statusName) {
		this.name = name;
		this.matricNo = matricNo;
		this.programName = programName;
		this.statusName = statusName;
		this.sessionName = sessionName;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMatricNo() {
		return matricNo;
	}

	public void setMatricNo(String matricNo) {
		this.matricNo = matricNo;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	
	

}
