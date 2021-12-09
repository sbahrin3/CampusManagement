package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import educate.sis.struct.entity.StudyMode;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.Teacher;

@Entity
@Table(name="ttb_classroom_temp")
public class ClassroomTemplate {
	
	@Id
	private String id;
	@ManyToOne
	private Subject subject;
	@ManyToOne
	private ClassroomSection section;
	
	@ManyToOne @JoinColumn(name="campus_id")
	private Campus campus;
	
	private int groupNumber;
	@ManyToOne
	private ClassroomTemplateGroup group;
	
	@Transient
	private String sectionName; //combination of section and group
	
	//will be use for temporary assigned teacher
	@ManyToOne
	private Teacher teacher;
	
	@Transient
	private int assigned;
	
	//will be deprecated
	@OneToMany
	private List<Teacher> coTeachers;
	
	@Column(length=50)
	private String assignTag;
	
	private int forExam;
	private int tagNumber;
	
	private int frequency;
	private int canDelete;
	
	private int sequence;
	private int priority;
	
	private int firstPosX;
	private int lastPosX;
	
	private int monday;
	private int tuesday;
	private int wednesday;
	private int thursday;
	private int friday;
	private int saturday;
	private int sunday;
	
	private int size;
	private int divideGroup;
	private int sizeGroup1;
	private int sizeGroup2;
	private int sizeGroup3;
	private int sizeGroup4;
	private int sizeGroup5;
	private int sizeGroup6;
	private int sizeGroup7;
	private int sizeGroup8;
	private int sizeGroup9;
	private int sizeGroup10;
	private int sizeGroup11;
	private int sizeGroup12;
	private int sizeGroup13;
	private int sizeGroup14;
	private int sizeGroup15;
	private int sizeGroup16;
	private int sizeGroup17;
	private int sizeGroup18;
	private int sizeGroup19;
	private int sizeGroup20;
	
	@Transient
	private int marker;
	@Transient
	private boolean placedInSlot; //because this is equivalent subject and has been placed in slot
	
	@Column(length=10)
	private String bgcolor;
	
	//@OneToMany(cascade=CascadeType.ALL)
	@Transient
	private List<ClassroomTemplate> equivalentClassrooms;
	
	private int scheduleType; // 0 - weekly, 1 - monthly
	@ManyToOne
	private StudyMode studyMode;
	
	private int overrideBlockedSlot;
	private int overrideBlockedDay;
	
	private int maxStudentPerSlot;
	private int defaultValue;
	
	private int allowSameDay = 0;
	
	@Column(length=50)
	private String createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;
	
	public ClassroomTemplate() {
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
	
	
	public Campus getCampus() {
		return campus;
	}

	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	public ClassroomSection getSection() {
		return section;
	}
	public void setSection(ClassroomSection section) {
		this.section = section;
	}
	public Teacher getTeacher() {
		return teacher;
	}
	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public void setDelete(boolean b) {
		canDelete = b ? 1 : 0;
	}
	
	public boolean canDelete() {
		return canDelete == 1;
	}

	public List<Teacher> getCoTeachers() {
		if ( coTeachers == null ) coTeachers = new ArrayList<	Teacher>();
		return coTeachers;
	}
	
	public List<Teacher> getTeachers() {
		List<Teacher> teachers = new ArrayList<Teacher>();

		if ( getGroup() != null ) {
			List<ClassTeacher> classTeachers = getGroup().getClassTeachers();
			for ( ClassTeacher t : classTeachers ) {
				Teacher teacher = t.getTeacher();
				teachers.add(teacher);
			}
		}
		
		return teachers;
	}

	public void setCoTeachers(List<Teacher> coTeachers) {
		this.coTeachers = coTeachers;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getFirstPosX() {
		return firstPosX;
	}

	public void setFirstPosX(int firstPosX) {
		this.firstPosX = firstPosX;
	}

	public int getLastPosX() {
		return lastPosX;
	}

	public void setLastPosX(int lastPosX) {
		this.lastPosX = lastPosX;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean getMonday() {
		return monday == 1;
	}

	public void setMonday(boolean monday) {
		this.monday = monday ? 1 : 0;
	}

	public boolean getTuesday() {
		return tuesday == 1;
	}

	public void setTuesday(boolean tuesday) {
		this.tuesday = tuesday ? 1 : 0;
	}

	public boolean getWednesday() {
		return wednesday == 1;
	}

	public void setWednesday(boolean wednesday) {
		this.wednesday = wednesday ? 1 : 0;
	}

	public boolean getThursday() {
		return thursday == 1;
	}

	public void setThursday(boolean thursday) {
		this.thursday = thursday ? 1 : 0;
	}

	public boolean getFriday() {
		return friday == 1;
	}

	public void setFriday(boolean friday) {
		this.friday = friday ? 1 : 0;
	}

	public boolean getSaturday() {
		return saturday == 1;
	}

	public void setSaturday(boolean saturday) {
		this.saturday = saturday ? 1 : 0;
	}

	public boolean getSunday() {
		return sunday == 1;
	}

	public void setSunday(boolean sunday) {
		this.sunday = sunday ? 1 : 0;
	}

	public int getDivideGroup() {
		return divideGroup;
	}

	public void setDivideGroup(int divideGroup) {
		this.divideGroup = divideGroup;
	}

	public int getSizeGroup1() {
		return sizeGroup1;
	}

	public void setSizeGroup1(int sizeGroup1) {
		this.sizeGroup1 = sizeGroup1;
	}

	public int getSizeGroup2() {
		return sizeGroup2;
	}

	public void setSizeGroup2(int sizeGroup2) {
		this.sizeGroup2 = sizeGroup2;
	}

	public int getSizeGroup3() {
		return sizeGroup3;
	}

	public void setSizeGroup3(int sizeGroup3) {
		this.sizeGroup3 = sizeGroup3;
	}

	public int getSizeGroup4() {
		return sizeGroup4;
	}

	public void setSizeGroup4(int sizeGroup4) {
		this.sizeGroup4 = sizeGroup4;
	}

	public int getSizeGroup5() {
		return sizeGroup5;
	}

	public void setSizeGroup5(int sizeGroup5) {
		this.sizeGroup5 = sizeGroup5;
	}

	public int getSizeGroup6() {
		return sizeGroup6;
	}

	public void setSizeGroup6(int sizeGroup6) {
		this.sizeGroup6 = sizeGroup6;
	}

	public int getSizeGroup7() {
		return sizeGroup7;
	}

	public void setSizeGroup7(int sizeGroup7) {
		this.sizeGroup7 = sizeGroup7;
	}

	public int getSizeGroup8() {
		return sizeGroup8;
	}

	public void setSizeGroup8(int sizeGroup8) {
		this.sizeGroup8 = sizeGroup8;
	}

	public int getSizeGroup9() {
		return sizeGroup9;
	}

	public void setSizeGroup9(int sizeGroup9) {
		this.sizeGroup9 = sizeGroup9;
	}

	public int getSizeGroup10() {
		return sizeGroup10;
	}

	public void setSizeGroup10(int sizeGroup10) {
		this.sizeGroup10 = sizeGroup10;
	}

	public int getSizeGroup11() {
		return sizeGroup11;
	}

	public void setSizeGroup11(int sizeGroup11) {
		this.sizeGroup11 = sizeGroup11;
	}

	public int getSizeGroup12() {
		return sizeGroup12;
	}

	public void setSizeGroup12(int sizeGroup12) {
		this.sizeGroup12 = sizeGroup12;
	}

	public int getSizeGroup13() {
		return sizeGroup13;
	}

	public void setSizeGroup13(int sizeGroup13) {
		this.sizeGroup13 = sizeGroup13;
	}

	public int getSizeGroup14() {
		return sizeGroup14;
	}

	public void setSizeGroup14(int sizeGroup14) {
		this.sizeGroup14 = sizeGroup14;
	}

	public int getSizeGroup15() {
		return sizeGroup15;
	}

	public void setSizeGroup15(int sizeGroup15) {
		this.sizeGroup15 = sizeGroup15;
	}

	public int getSizeGroup16() {
		return sizeGroup16;
	}

	public void setSizeGroup16(int sizeGroup16) {
		this.sizeGroup16 = sizeGroup16;
	}

	public int getSizeGroup17() {
		return sizeGroup17;
	}

	public void setSizeGroup17(int sizeGroup17) {
		this.sizeGroup17 = sizeGroup17;
	}

	public int getSizeGroup18() {
		return sizeGroup18;
	}

	public void setSizeGroup18(int sizeGroup18) {
		this.sizeGroup18 = sizeGroup18;
	}

	public int getSizeGroup19() {
		return sizeGroup19;
	}

	public void setSizeGroup19(int sizeGroup19) {
		this.sizeGroup19 = sizeGroup19;
	}

	public int getSizeGroup20() {
		return sizeGroup20;
	}

	public void setSizeGroup20(int sizeGroup20) {
		this.sizeGroup20 = sizeGroup20;
	}

	public boolean getForExam() {
		return forExam == 1;
	}

	public void setForExam(boolean forExam) {
		this.forExam = forExam ? 1 : 0;
	}

	public int getTagNumber() {
		return tagNumber;
	}

	public void setTagNumber(int tagNumber) {
		this.tagNumber = tagNumber;
	}

	public String getAssignTag() {
		return assignTag;
	}

	public void setAssignTag(String assignTag) {
		this.assignTag = assignTag;
	}

	public int getMarker() {
		return marker;
	}

	public void setMarker(int marker) {
		this.marker = marker;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

	public String getSectionName() {
		sectionName = getSection().getName() + "_" + this.getGroupNumber() + "_" + this.studyMode.getCode() + "_" + this.scheduleType;
		return sectionName;
	}

	public ClassroomTemplateGroup getGroup() {
		return group;
	}

	public void setGroup(ClassroomTemplateGroup group) {
		this.group = group;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public boolean isPlacedInSlot() {
		return placedInSlot;
	}

	public void setPlacedInSlot(boolean placedInSlot) {
		this.placedInSlot = placedInSlot;
	}

	public List<ClassroomTemplate> getEquivalentClassrooms() {
		if ( equivalentClassrooms == null ) equivalentClassrooms = new ArrayList<ClassroomTemplate>();
		return equivalentClassrooms;
	}

	public void setEquivalentClassrooms(List<ClassroomTemplate> equivalentClassrooms) {
		this.equivalentClassrooms = equivalentClassrooms;
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

	public boolean getAssigned() {
		return assigned == 1;
	}

	public void setAssigned(boolean assigned) {
		this.assigned = assigned ? 1 : 0;
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

	public int getMaxStudentPerSlot() {
		return maxStudentPerSlot;
	}

	public void setMaxStudentPerSlot(int maxStudentPerSlot) {
		this.maxStudentPerSlot = maxStudentPerSlot;
	}

	public int getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(int defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean getAllowSameDay() {
		return allowSameDay == 1;
	}

	public void setAllowSameDay(boolean allowSameDay) {
		this.allowSameDay = allowSameDay ? 1 : 0;
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


	

}
