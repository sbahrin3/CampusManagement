package educate.sis.struct.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import educate.timetabling.entity.AIMCampus;
import educate.timetabling.entity.AIMDepartment;
import educate.timetabling.entity.TeacherGroup;
import metadata.EntityLister;

@Entity
@Table(name="struc_teachsubject")
public class Teacher implements EntityLister{
	@Id
	private String id;
	private String userId;
	private String code;
	private String name;
	private String email;
	private String assesment;
	private String finalExam;
	private String photoFileName;
	private String avatarFileName;
	@OneToMany(fetch=FetchType.LAZY)
	private List<LearningCentre> centres;
	
	/*
	@ManyToOne 
	private Faculty faculty;
	*/
	
	@OneToMany(fetch=FetchType.LAZY)
	private List<TeacherGroup> teacherGroups;
	
	
	@OneToMany(cascade=CascadeType.ALL)
	private Set<TeacherSubject> teacherSubjects; //for LMS use only
	
	
	
	
	//teacher's personal preferences for scheduling
	private int limitTeachingHours; //per day (max is 12 hours/day)
	private int limitTeachingHoursWeek; // per week
	
	private int minTeachingHoursWeek; //minimum teaching hours per week
	
	//exception slots
	@Column(length=4)
	private String mondayStartTime = "";
	@Column(length=4)
	private String mondayEndTime = "";
	@Column(length=4)
	private String tuesdayStartTime = "";
	@Column(length=4)
	private String tuesdayEndTime = "";
	@Column(length=4)
	private String wednesdayStartTime = "";
	@Column(length=4)
	private String wednesdayEndTime = "";
	@Column(length=4)
	private String thursdayStartTime = "";
	@Column(length=4)
	private String thursdayEndTime = "";
	@Column(length=4)
	private String fridayStartTime = "";
	@Column(length=4)
	private String fridayEndTime = "";
	@Column(length=4)
	private String saturdayStartTime = "";
	@Column(length=4)
	private String saturdayEndTime = "";
	@Column(length=4)
	private String sundayStartTime = "";
	@Column(length=4)
	private String sundayEndTime = "";
	

	
	@Column(name="gred_gaji", length=50)
	private String salaryGrade; //GRED_GAJI
	@Column(name="jawatan_hakiki", length=50)
	private String assignedPosition; //JAWATAN_HAKIKI
	@Column(name="jawatan_sekarang", length=50)
	private String currentPosition; //JAWATAN_SEKARANG
	
	
	//extra field
	@ManyToOne @JoinColumn(name="campus_id")
	private AIMCampus campus;
	
	@ManyToOne @JoinColumn(name="faculty_id")
	private AIMDepartment faculty;
	
	@Transient
	private String abbrevName;
	
	
	public Teacher() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public Teacher(String id) {
		setId(id);
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
	
	

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/*
	@Override
	public boolean equals(Object o) {
		Teacher result = (Teacher)o;
		if(result.getUserId().equals(userId))
			return true;
		else
			return false;
	}
	*/
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

//	public TeachSubject getTeachSubject() {
//		return teachSubject;
//	}
//
//	public void setTeachSubject(TeachSubject teachSubject) {
//		this.teachSubject = teachSubject;
//	}

	/*
	public Set<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(Set<Subject> subjects) {
		this.subjects = subjects;
	}
	
	public void addSubject(Subject subject) {
		if ( subjects == null ) subjects = new HashSet<Subject>();
		subjects.remove(subject);
		subjects.add(subject);
	}
	*/

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<TeacherSubject> getTeacherSubjects() {
		return teacherSubjects;
	}
	
	public TeacherSubject[] getSubjectTeachingOrderByCode() {
		Object[] array = teacherSubjects.toArray();
		Arrays.sort(array);
		TeacherSubject[] subjects = new TeacherSubject[array.length];
		int cnt = 0;
		for ( Object o : array ) {
			subjects[cnt++] = (TeacherSubject) o;
		}
		return subjects;
	}

//	public void setTeacherSubjects(Set<TeacherSubject> teacherSubjects) {
//		for(Iterator<TeacherSubject> it = teacherSubjects.iterator();it.hasNext();){
//			it.next().setTeacher(this);
//		}
//		this.teacherSubjects = teacherSubjects;
//	}
	

	public void addTeacherSubject(TeacherSubject teacherSubject) {
		if ( teacherSubjects == null ) teacherSubjects = new HashSet<TeacherSubject>();
		//if this subject + section already has, do not add
		boolean add = true;
		if ( teacherSubjects.size() > 0 ) {
			try {
				for ( TeacherSubject ts : teacherSubjects ) {
					if ( ts.getSubject().getId().equals(teacherSubject.getSubject().getId())
							&& ts.getTeacher().getId().equals(teacherSubject.getTeacher().getId())) {
						add = false;
						break;
					}
				}
			} catch ( Exception e ) {
				add = false;
			}
		}
		if ( add ) {
			teacherSubject.setTeacher(this);
			teacherSubjects.add(teacherSubject);
		}
	}

	
	public void removeTeacherSubject(TeacherSubject teacherSubject) {
		teacherSubjects.remove(teacherSubject);
	}

	public String getAssesment() {
		return assesment;
	}

	public void setAssesment(String assesment) {
		this.assesment = assesment;
	}

	public String getFinalExam() {
		return finalExam;
	}

	public void setFinalExam(String finalExam) {
		this.finalExam = finalExam;
	}

	
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE);
		
		tsb.append("id", id);
		tsb.append("userId", userId);
		tsb.append("name", name);
		tsb.append("email", email);
		tsb.append("assesment", assesment);
		tsb.append("finalExam", finalExam);
		
		return tsb.toString();
	}

	public String getValue() {
		return this.name;
	}

	public String getPhotoFileName() {
		return photoFileName;
	}

	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}

	public void setTeacherSubjects(Set<TeacherSubject> teacherSubjects) {
		this.teacherSubjects = teacherSubjects;
	}

	public String getAvatarFileName() {
		return avatarFileName;
	}

	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}

	public List<LearningCentre> getCentres() {
		if ( centres == null ) centres = new ArrayList<LearningCentre>();
		return centres;
	}

	public void setCentres(List<LearningCentre> centres) {
		this.centres = centres;
	}

	public List<TeacherGroup> getTeacherGroups() {
		if ( teacherGroups == null ) teacherGroups = new ArrayList<TeacherGroup>();
		return teacherGroups;
	}

	public void setTeacherGroups(List<TeacherGroup> teacherGroups) {
		this.teacherGroups = teacherGroups;
	}


	
	//teacher preferences
	public int getLimitTeachingHours() {
		return limitTeachingHours;
	}

	public void setLimitTeachingHours(int limitTeachingHours) {
		this.limitTeachingHours = limitTeachingHours;
	}

	public String getMondayStartTime() {
		return mondayStartTime;
	}

	public void setMondayStartTime(String mondayStartTime) {
		this.mondayStartTime = mondayStartTime;
	}

	public String getMondayEndTime() {
		return mondayEndTime;
	}

	public void setMondayEndTime(String mondayEndTime) {
		this.mondayEndTime = mondayEndTime;
	}

	public String getTuesdayStartTime() {
		return tuesdayStartTime;
	}

	public void setTuesdayStartTime(String tuesdayStartTime) {
		this.tuesdayStartTime = tuesdayStartTime;
	}

	public String getTuesdayEndTime() {
		return tuesdayEndTime;
	}

	public void setTuesdayEndTime(String tuesdayEndTime) {
		this.tuesdayEndTime = tuesdayEndTime;
	}

	public String getWednesdayStartTime() {
		return wednesdayStartTime;
	}

	public void setWednesdayStartTime(String wednesdayStartTime) {
		this.wednesdayStartTime = wednesdayStartTime;
	}

	public String getWednesdayEndTime() {
		return wednesdayEndTime;
	}

	public void setWednesdayEndTime(String wednesdayEndTime) {
		this.wednesdayEndTime = wednesdayEndTime;
	}

	public String getThursdayStartTime() {
		return thursdayStartTime;
	}

	public void setThursdayStartTime(String thursdayStartTime) {
		this.thursdayStartTime = thursdayStartTime;
	}

	public String getThursdayEndTime() {
		return thursdayEndTime;
	}

	public void setThursdayEndTime(String thursdayEndTime) {
		this.thursdayEndTime = thursdayEndTime;
	}

	public String getFridayStartTime() {
		return fridayStartTime;
	}

	public void setFridayStartTime(String fridayStartTime) {
		this.fridayStartTime = fridayStartTime;
	}

	public String getFridayEndTime() {
		return fridayEndTime;
	}

	public void setFridayEndTime(String fridayEndTime) {
		this.fridayEndTime = fridayEndTime;
	}

	public String getSaturdayStartTime() {
		return saturdayStartTime;
	}

	public void setSaturdayStartTime(String saturdayStartTime) {
		this.saturdayStartTime = saturdayStartTime;
	}

	public String getSaturdayEndTime() {
		return saturdayEndTime;
	}

	public void setSaturdayEndTime(String saturdayEndTime) {
		this.saturdayEndTime = saturdayEndTime;
	}

	public String getSundayStartTime() {
		return sundayStartTime;
	}

	public void setSundayStartTime(String sundayStartTime) {
		this.sundayStartTime = sundayStartTime;
	}

	public String getSundayEndTime() {
		return sundayEndTime;
	}

	public void setSundayEndTime(String sundayEndTime) {
		this.sundayEndTime = sundayEndTime;
	}

	public boolean hasMonday() {
		return !"".equals(mondayStartTime) && !"".equals(mondayEndTime);
	}
	
	public boolean hasTuesday() {
		return !"".equals(tuesdayStartTime) && !"".equals(tuesdayEndTime);
	}
	
	public boolean hasWednesday() {
		return !"".equals(wednesdayStartTime) && !"".equals(wednesdayEndTime);
	}
	
	public boolean hasThursday() {
		return !"".equals(thursdayStartTime) && !"".equals(thursdayEndTime);
	}
	
	public boolean hasFriday() {
		return !"".equals(fridayStartTime) && !"".equals(fridayEndTime);
	}
	
	public boolean hasSaturday() {
		return !"".equals(saturdayStartTime) && !"".equals(saturdayEndTime);
	}
	
	public boolean hasSunday() {
		return !"".equals(sundayStartTime) && !"".equals(sundayEndTime);
	}

	public int getLimitTeachingHoursWeek() {
		return limitTeachingHoursWeek;
	}

	public void setLimitTeachingHoursWeek(int limitTeachingHoursWeek) {
		this.limitTeachingHoursWeek = limitTeachingHoursWeek;
	}

	public AIMCampus getCampus() {
		return campus;
	}

	public void setCampus(AIMCampus campus) {
		this.campus = campus;
	}

	public String getSalaryGrade() {
		return salaryGrade;
	}

	public void setSalaryGrade(String salaryGrade) {
		this.salaryGrade = salaryGrade;
	}

	public String getAssignedPosition() {
		return assignedPosition;
	}

	public void setAssignedPosition(String assignedPosition) {
		this.assignedPosition = assignedPosition;
	}

	public String getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(String currentPosition) {
		this.currentPosition = currentPosition;
	}

	public int getMinTeachingHoursWeek() {
		return minTeachingHoursWeek;
	}

	public void setMinTeachingHoursWeek(int minTeachingHours) {
		this.minTeachingHoursWeek = minTeachingHours;
	}

	public AIMDepartment getFaculty() {
		return faculty;
	}

	public void setFaculty(AIMDepartment faculty) {
		this.faculty = faculty;
	}
	
	public void setAbbrevName(String abbrevName) {
		this.abbrevName = abbrevName;
	}

	public String getAbbrevName() {
		if ( abbrevName == null || "".equals(abbrevName)) {
			abbrevName = getNS(name);
		}
		return abbrevName;
	}
	
	private static String getNS(String name) {
		boolean next = true;
		String s = "";
		name = name.toUpperCase();
		name = name.replaceAll("'", "");
		name = name.replaceAll(" BIN ", " ");
		name = name.replaceAll(" BINTI ", " ");
		name = name.replaceAll(" BT ", " ");
		name = name.replaceAll(" BTE ", " ");
		name = name.replaceAll(" BT. ", " ");
		name = name.replaceAll(" BTE. ", " ");
		name = name.replaceAll(" B. ", " ");
		
		for ( int i=0; i<name.length(); i++) {
			if ( next ) {
				s += name.substring(i, i+1);
				next = false;
			}
			if ( " ".equals(name.substring(i, i+1))) {
				next = true;
			}
		}
		return s;
	}
	
}