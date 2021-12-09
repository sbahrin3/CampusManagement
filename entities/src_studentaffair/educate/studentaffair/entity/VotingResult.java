package educate.studentaffair.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import educate.enrollment.entity.Student;

@Entity @Table(name="stdaf_voting_result")
public class VotingResult {
	
	@Id @Column(length=50)
	private String id;
	@OneToOne @JoinColumn(name="candidate_id")
	private VotingCandidate selectedCandidate;
	@ManyToOne @JoinColumn(name="voting_session_id")
	private VotingSession votingSession;
	@OneToOne @JoinColumn(name="student_id")
	private Student student;
	
	
	public VotingResult() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public VotingCandidate getSelectedCandidate() {
		return selectedCandidate;
	}
	public void setSelectedCandidate(VotingCandidate selectedCandidate) {
		this.selectedCandidate = selectedCandidate;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}

	public VotingSession getVotingSession() {
		return votingSession;
	}

	public void setVotingSession(VotingSession votingSession) {
		this.votingSession = votingSession;
	}
	
	
	
}
