package educate.sis.exam.entity;

import java.util.*;

import educate.sis.struct.entity.*;
import javax.persistence.*;

@Entity
@Table(name="exam_transcript_date")
public class TranscriptEndorsedDate {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="program_id")
	private Program program;
	@ManyToOne @JoinColumn(name="session_id")
	private Session session;
	@Temporal(TemporalType.DATE)
	private Date endorsedDate;
	
	public TranscriptEndorsedDate() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Program getProgram() {
		return program;
	}
	public void setProgram(Program program) {
		this.program = program;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public Date getEndorsedDate() {
		return endorsedDate;
	}
	public void setEndorsedDate(Date endorsedDate) {
		this.endorsedDate = endorsedDate;
	}
	
	

}
