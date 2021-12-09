package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Subject;

@Entity
@Table(name="ttb_cls_grp")
public class ClassroomTemplateGroup {
	
	@Id
	private String id;
	@ManyToOne
	private Subject subject;
	@ManyToOne
	private ClassroomSection section;
	private int groupNumber;
	
	@ManyToOne @JoinColumn(name="campus_id")
	private Campus campus;
	
	private int size;
	@OneToMany(cascade=CascadeType.ALL)
	private List<ClassTeacher> classTeachers;
	@OneToMany
	List<ClassroomGroupFaculty> facultyGroups; //study mode are defined in here --- FULL TIME, PART TIME, PJJ, bla bla bla
	@OneToMany
	List<StudentSubjectGroup> studentSubjectGroups;
	@OneToMany
	List<Program> programs;
	
	@OneToMany
	List<Room> rooms; //list of possible rooms
	
	private int takenType; //0 - first timer, 2 - repeater, 3 - both
	
	public ClassroomTemplateGroup() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	
	public ClassroomSection getSection() {
		return section;
	}

	public void setSection(ClassroomSection section) {
		this.section = section;
	}
	

	public Campus getCampus() {
		return campus;
	}

	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getTakenType() {
		return takenType;
	}

	public void setTakenType(int takenType) {
		this.takenType = takenType;
	}

	public List<ClassroomGroupFaculty> getFacultyGroups() {
		if ( facultyGroups == null ) facultyGroups = new ArrayList<ClassroomGroupFaculty>();
		return facultyGroups;
	}

	public void setFacultyGroups(List<ClassroomGroupFaculty> facultyGroups) {
		this.facultyGroups = facultyGroups;
	}

	public List<Program> getPrograms() {
		if ( programs == null ) programs = new ArrayList<Program>();
		return programs;
	}

	public void setPrograms(List<Program> programs) {
		this.programs = programs;
	}

	public List<ClassTeacher> getClassTeachers() {
		if ( classTeachers == null ) classTeachers = new ArrayList<ClassTeacher>();
		return classTeachers;
	}

	public void setClassTeachers(List<ClassTeacher> classTeachers) {
		this.classTeachers = classTeachers;
	}

	public List<StudentSubjectGroup> getStudentSubjectGroups() {
		if ( studentSubjectGroups == null ) studentSubjectGroups = new ArrayList<StudentSubjectGroup>();
		return studentSubjectGroups;
	}

	public void setStudentSubjectGroups(List<StudentSubjectGroup> studentSubjectGroups) {
		this.studentSubjectGroups = studentSubjectGroups;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}
	
	
	

}
