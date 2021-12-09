package educate.studentaffair.module;

public class VoteCount {
	
	private String candidateId;
	private int count;
	
	public VoteCount(String candidateId, int count) {
		this.candidateId = candidateId;
		this.count = count;
	}

	public String getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	

}
