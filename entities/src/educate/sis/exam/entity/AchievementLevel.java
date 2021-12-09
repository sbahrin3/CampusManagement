package educate.sis.exam.entity;


import java.util.*;

import educate.sis.struct.entity.*;
import javax.persistence.*;

@Entity
@Table(name="exam_achievement_level")
public class AchievementLevel {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="program_id")
	private Program program;
	private double cgpaValue;
	private double gpaValue;
	private int creditHour;
	@Column(length=100)
	private String name;
	@Column(length=40)
	private String code;
	
	public AchievementLevel() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getCgpaValue() {
		return cgpaValue;
	}
	public void setCgpaValue(double cgpaValue) {
		this.cgpaValue = cgpaValue;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public double getGpaValue() {
		return gpaValue;
	}

	public void setGpaValue(double gpaValue) {
		this.gpaValue = gpaValue;
	}

	public int getCreditHour() {
		return creditHour;
	}

	public void setCreditHour(int creditHour) {
		this.creditHour = creditHour;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	

}
