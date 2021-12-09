package educate.timetabling.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.StudyMode;

@Entity
@Table(name="ttb_clsgrp_faculty")
public class ClassroomGroupFaculty {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String code;	
	@Column(length=50)
	private String name;
	@ManyToOne @JoinColumn(name="faculty_id")
	private Faculty faculty;
	@ManyToOne @JoinColumn(name="studyMode_id")
	private StudyMode studyMode; //full time, part time, pendidikan jarak jauh, etc
	@ManyToOne @JoinColumn(name="department_id")
	private AIMDepartment department;
	
	@ManyToOne @JoinColumn(name="campus_id")
	private Campus campus;
	@ManyToOne @JoinColumn(name="program_id")
	private Program program;
	
	private int semester;
	private int size;
	
	private int firstTimer;
	private int repeater;
	
	public ClassroomGroupFaculty() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Faculty getFaculty() {
		return faculty;
	}

	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}

	public Campus getCampus() {
		return campus;
	}

	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	public StudyMode getStudyMode() {
		return studyMode;
	}

	public void setStudyMode(StudyMode studyMode) {
		this.studyMode = studyMode;
	}

	public int getSemester() {
		return semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public AIMDepartment getDepartment() {
		return department;
	}

	public void setDepartment(AIMDepartment department) {
		this.department = department;
	}

	public boolean getFirstTimer() {
		return firstTimer == 1;
	}

	public void setFirstTimer(boolean firstTimer) {
		this.firstTimer = firstTimer ? 1 : 0;
	}

	public boolean getRepeater() {
		return repeater == 1;
	}

	public void setRepeater(boolean repeater) {
		this.repeater = repeater ? 1 : 0;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

}
