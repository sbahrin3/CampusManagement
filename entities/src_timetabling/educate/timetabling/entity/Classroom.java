package educate.timetabling.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.StudyMode;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectSection;
import educate.sis.struct.entity.Teacher;


@Entity
@Table(name="ttb_classroom")
public class Classroom {
	
	@Id
	private String id;
	@ManyToOne
	private Slot slot;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	private Room room;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private ClassroomSection classroomSection;
	
	@ManyToOne
	private Subject subject;
	@ManyToOne
	private SubjectSection section;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Teacher teacher;
	@OneToMany
	private List<Teacher> coTeachers;
	
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="campus_id")
	private Campus campus;

	private int isCombinedSlot;
	private int combineSlotNo;
	private String combineId;
	private int combined;
	private int grouped;
	private int autoGenerate;
	//@ManyToOne(fetch=FetchType.LAZY) 
	@ManyToOne @JoinColumn(name="template_id")
	private ClassroomTemplate classroomTemplate;
	
	
	private int groupNumber;
	private int size;
	@OneToMany(cascade=CascadeType.PERSIST)
	private List<ClassTeacher> classTeachers;
	@OneToMany
	List<StudentSubjectGroup> studentSubjectGroups;
	@OneToMany
	List<Invigilator> invigilators;
	
	private int totalInvigilators;
	
	
	private int groupCount;
	
	@Column(length=200)
	private String subjectTopic;
	
	private int replacement;
	
	@Temporal(TemporalType.TIME)
	private Date actualStartTime;
	@Temporal(TemporalType.TIME)
	private Date actualEndTime;
	
	
	@Column(length=10)
	private String bgcolor;
	
	private int scheduleType; // 0 - weekly, 1 - monthly
	@ManyToOne
	private StudyMode studyMode;
	
	//flag if this classroom should change room because of current assigned room is not available
	private int shouldChangeRoom;
	
	private String displayId; //academicCalendar.ProgramDeliveryType - academicCalendar.DeliveryGroup
	
	private int overrideBlockedSlot;
	private int overrideBlockedDay;
	
	@ManyToOne @JoinColumn(name="logger_id")
	private TimetableSlottingLog logger;
	
	private int published;
	
	@Column(length=50)
	private String createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;
	
	@Column(name="has_students")
	private int hasStudents;
	
	public Classroom() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public SubjectSection getSection() {
		return section;
	}

	public void setSection(SubjectSection section) {
		this.section = section;
	}

	public Slot getSlot() {
		return slot;
	}

	public void setSlot(Slot slot) {
		this.slot = slot;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	
	

	public Campus getCampus() {
		return campus;
	}

	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	public Teacher getTeacher() {
		if ( teacher == null && coTeachers != null ) {
			if ( coTeachers.size() > 0 )
				teacher = coTeachers.get(0);
		}
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public int getIsCombinedSlot() {
		return isCombinedSlot;
	}

	public void setIsCombinedSlot(int isCombinedSlot) {
		this.isCombinedSlot = isCombinedSlot;
	}

	public int getCombineSlotNo() {
		return combineSlotNo;
	}

	public void setCombineSlotNo(int combineSlotNo) {
		this.combineSlotNo = combineSlotNo;
	}

	public ClassroomSection getClassroomSection() {
		return classroomSection;
	}

	public void setClassroomSection(ClassroomSection classroomSection) {
		this.classroomSection = classroomSection;
	}

	public List<Teacher> getCoTeachers() {
		if ( coTeachers == null ) coTeachers = new ArrayList<Teacher>();
		return coTeachers;
	}

	public void setCoTeachers(List<Teacher> coTeachers) {
		this.coTeachers = coTeachers;
	}

	public boolean getCombined() {
		return combined == 1;
	}

	public void setCombined(boolean combined) {
		this.combined = combined ? 1 : 0;
	}

	public String getCombineId() {
		return combineId;
	}

	public void setCombineId(String combineId) {
		this.combineId = combineId;
	}

	public String getSubjectTopic() {
		return subjectTopic;
	}

	public void setSubjectTopic(String subjectTopic) {
		this.subjectTopic = subjectTopic;
	}

	public boolean getAutoGenerate() {
		return autoGenerate == 1;
	}

	public void setAutoGenerate(boolean autoGenerate) {
		this.autoGenerate = autoGenerate ? 1 : 0;
	}

	public ClassroomTemplate getClassroomTemplate() {
		return classroomTemplate;
	}

	public void setClassroomTemplate(ClassroomTemplate classroomTemplate) {
		this.classroomTemplate = classroomTemplate;
	}

	public boolean getGrouped() {
		return grouped == 1;
	}

	public void setGrouped(boolean grouped) {
		this.grouped = grouped ? 1 : 0;
	}

	public int getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}

	public boolean getReplacement() {
		return replacement == 1;
	}

	public void setReplacement(boolean replacement) {
		this.replacement = replacement ? 1 : 0;
	}

	public Date getActualStartTime() {
		return actualStartTime;
	}

	public void setActualStartTime(Date actualStartTime) {
		this.actualStartTime = actualStartTime;
	}

	public Date getActualEndTime() {
		return actualEndTime;
	}

	public void setActualEndTime(Date actualEndTime) {
		this.actualEndTime = actualEndTime;
	}
	
	public String getActualStartTimeDisplay() {
		return actualStartTime != null ? new SimpleDateFormat("hh:mm a").format(actualStartTime) : "";
	}
	
	public String getActualEndTimeDisplay() {
		return actualEndTime != null ? new SimpleDateFormat("hh:mm a").format(actualEndTime) : "";
	}
	
	public Hashtable getActualStartTimeComponent() {
		Hashtable h = new Hashtable();
		if ( actualStartTime != null ) {
			h.put("hour", new SimpleDateFormat("hh").format(actualStartTime));
			h.put("minute", new SimpleDateFormat("mm").format(actualStartTime));
			h.put("a", new SimpleDateFormat("a").format(actualStartTime));
		}
		return h;
	}
	
	public Hashtable getActualEndTimeComponent() {
		Hashtable h = new Hashtable();
		if ( actualEndTime != null ) {
			h.put("hour", new SimpleDateFormat("hh").format(actualEndTime));
			h.put("minute", new SimpleDateFormat("mm").format(actualEndTime));
			h.put("a", new SimpleDateFormat("a").format(actualEndTime));
		}
		return h;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
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

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public int getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(int scheduleType) {
		this.scheduleType = scheduleType;
	}

	public StudyMode getStudyMode() {
		return studyMode;
	}

	public void setStudyMode(StudyMode studyMode) {
		this.studyMode = studyMode;
	}

	public boolean getShouldChangeRoom() {
		return shouldChangeRoom == 1;
	}

	public void setShouldChangeRoom(boolean shouldChangeRoom) {
		this.shouldChangeRoom = shouldChangeRoom ? 1 : 0;
	}

	public String getDisplayId() {
		return displayId;
	}

	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}
	
	
	public boolean hasDepartmentByGroup(List<AIMDepartment> depts) {
		boolean has = false;
		if ( depts != null && studentSubjectGroups != null && studentSubjectGroups.size() > 0 ) {
			for ( AIMDepartment d : depts ) {
				
				for ( StudentSubjectGroup g : studentSubjectGroups ) {
					if ( g.getFacultyGroup() != null ) {
						if ( g.getFacultyGroup().getDepartment() != null ) {
							if ( g.getFacultyGroup().getDepartment().getId().equals(d.getId())) {
								has = true;
								break;
							}
						}
					}
				}
				
				if ( has ) {
					break;
				}
			}
		}
		
		return has;
	}
	
	public boolean hasDepartmentBySubject(List<AIMDepartment> depts) {
		boolean has = false;
		if ( depts != null && subject != null ) {
			for ( AIMDepartment d : depts ) {
				if ( subject.getDepartment() != null ) {
					if ( subject.getDepartment().getId().equals(d.getId())) {
						has = true;
						break;
					}
				}
			}
		}
		
		return has;
	}
	
	
	public boolean getOverrideBlockedSlot() {
		return overrideBlockedSlot == 1;
	}

	public void setOverrideBlockedSlot(boolean overrideBlockedSlot) {
		this.overrideBlockedSlot = overrideBlockedSlot ? 1 : 0;
	}


	public boolean getOverrideBlockedDay() {
		return overrideBlockedDay == 1;
	}

	public void setOverrideBlockedDay(boolean overrideBlockedDay) {
		this.overrideBlockedDay = overrideBlockedDay ? 1 : 0;
	}

	public TimetableSlottingLog getLogger() {
		return logger;
	}

	public void setLogger(TimetableSlottingLog logger) {
		this.logger = logger;
	}

	public boolean getPublished() {
		return published == 1;
	}

	public void setPublished(boolean published) {
		this.published = published ? 1 : 0;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public boolean getHasStudents() {
		return hasStudents == 1;
	}

	public void setHasStudents(boolean hasStudents) {
		this.hasStudents = hasStudents ? 1 : 0;
	}

	public List<Invigilator> getInvigilators() {
		if ( invigilators == null ) invigilators = new ArrayList<Invigilator>();
		return invigilators;
	}

	public void setInvigilators(List<Invigilator> invigilators) {
		this.invigilators = invigilators;
	}

	public int getTotalInvigilators() {
		return totalInvigilators;
	}

	public void setTotalInvigilators(int totalInvigilators) {
		this.totalInvigilators = totalInvigilators;
	}
	
	public void addInvigilator(Invigilator i) {
		if ( !getInvigilators().contains(i)) {
			getInvigilators().add(i);
		}
	}


}
