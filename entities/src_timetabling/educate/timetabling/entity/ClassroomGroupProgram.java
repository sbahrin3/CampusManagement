package educate.timetabling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Program;

@Entity
@Table(name="ttb_clsgrp_faculty")
public class ClassroomGroupProgram {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String name;
	@ManyToOne @JoinColumn(name="program_id")
	private Program program;
	@ManyToOne
	private LearningCentre learningCenter;
	
	public ClassroomGroupProgram() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Program getProgram() {
		return program;
	}
	public void setProgram(Program program) {
		this.program = program;
	}
	public LearningCentre getLearningCenter() {
		return learningCenter;
	}
	public void setLearningCenter(LearningCentre learningCenter) {
		this.learningCenter = learningCenter;
	}
	
	
	
}
