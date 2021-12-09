package educate.enrollment.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;

@Entity
@Table(name="student_month_count")
public class StudentMonthCount {
	
	@Id
	private String id;
	private int month;
	private int year;
	@ManyToOne @JoinColumn(name="program_id")
	private Program program;
	@ManyToOne @JoinColumn(name="intake_id")
	private Session intake;
	private long counts;
	
	public StudentMonthCount() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getCounts() {
		return counts;
	}
	public void setCounts(long counts) {
		this.counts = counts;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public Session getIntake() {
		return intake;
	}

	public void setIntake(Session intake) {
		this.intake = intake;
	}
	
}
