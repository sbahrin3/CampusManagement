package educate.sis.exam.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.enrollment.entity.Student;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;

@Entity
@Table(name="exam_finalresults")
@IdClass(FinalResultPK.class)
public class FinalResult {
	@Id
	private String studentId;
	@Id
	private String sessionCode;
	@ManyToOne
	private Session session;
	@ManyToOne
	private Period period;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Student student;
	private double cgpa;
	private double gpa;
	private double totalHours; //cumulativeHours
	
	private String operator;
	private String modified;
	@Temporal(TemporalType.DATE)
	private Date created;
	@Temporal(TemporalType.TIME)
	private Date time;
	private String standing;
	private String textGpa;
	private String textCgpa;
	private double currentHours;
	private double takenHours;
	private double cumulativeHours;
	private double currentPoints;
	private double totalPoints;
	private double cumulativePoints;
	
	@ManyToOne @JoinColumn(name="result_standing_id")
	private Standing resultStanding;
	@ManyToOne @JoinColumn(name="achievement_level_id")
	private AchievementLevel achievementLevel;
	
	@OneToMany(mappedBy="parent",cascade=CascadeType.ALL)
	private List<FinalSubjectResult> subjects = new ArrayList<FinalSubjectResult>(); 
	
	private int updated;
	private int needCalculateCGPA;
	
	public FinalResult(){
		
	}
	public FinalResult(String sessionCode,String matricNo){
		this.sessionCode = sessionCode;
		this.studentId = matricNo;
	}
	
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public double getCgpa() {
		return cgpa;
	}
	public void setCgpa(double cgpa) {
		this.cgpa = cgpa;
	}
	public double getGpa() {
		return gpa;
	}
	public void setGpa(double gpa) {
		this.gpa = gpa;
	}
	public double getTotalHours() {
		return totalHours;
	}
	public void setTotalHours(double totalHours) {
		this.totalHours = totalHours;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getModified() {
		return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}

	
	public FinalSubjectResult getSubject(String subjectId) {
		for ( FinalSubjectResult s : subjects ) {
			if ( s.getSubject() != null ) {
				if ( s.getSubject().getId().equals(subjectId)) {
					return s;
				}
			}
		}
		return null;
	}
	
	public void setSubjects(Collection subjects) {
		for(FinalSubjectResult d : this.subjects){
			if(!subjects.contains(d)){
				removeSubject(d);
			}
		}
		
		for(FinalSubjectResult d :(Collection<FinalSubjectResult>)subjects){
			addSubject(d);
		}
		
	}
	public void addSubject(FinalSubjectResult d) {
		if(!this.subjects.contains(d)){
			this.subjects.add(d);
			d.setParent(this);
		}
		
	}
	public void removeSubject(FinalSubjectResult d) {
		if(this.subjects.contains(d)){
			this.subjects.remove(d);
			d.removeParent();
		}
		
	}
	public String getStanding() {
		return standing;
	}
	public void setStanding(String standing) {
		this.standing = standing;
	}
	public String getTextGpa() {
		return textGpa;
	}
	public void setTextGpa(String textGpa) {
		this.textGpa = textGpa;
	}
	public String getTextCgpa() {
		return textCgpa;
	}
	public void setTextCgpa(String textCgpa) {
		this.textCgpa = textCgpa;
	}
	public double getCurrentHours() {
		return currentHours;
	}
	public void setCurrentHours(double currentHours) {
		this.currentHours = currentHours;
	}
	public double getTotalSubjectPoint(){
		double total = 0.0d;
		if(subjects != null){
			for(Iterator<FinalSubjectResult> it = subjects.iterator();it.hasNext();){
				FinalSubjectResult temp = it.next();
				total += temp.getPoint();
			}
		}
		return total;
	}
	
	public boolean getUpdated() {
		return updated == 0 ? false : true;
	}
	
	public void setUpdated(boolean b) {
		updated = b ? 1 : 0;
	}
	public double getCumulativePoints() {
		return cumulativePoints;
	}
	public void setCumulativePoints(double cumulativePoints) {
		this.cumulativePoints = cumulativePoints;
	}
	public Standing getResultStanding() {
		return resultStanding;
	}
	public void setResultStanding(Standing resultStanding) {
		this.resultStanding = resultStanding;
	}
	public double getCumulativeHours() {
		return cumulativeHours;
	}
	public void setCumulativeHours(double cumulativeHours) {
		this.cumulativeHours = cumulativeHours;
	}
	public double getCurrentPoints() {
		return currentPoints;
	}
	public void setCurrentPoints(double currentPoints) {
		this.currentPoints = currentPoints;
	}
	public AchievementLevel getAchievementLevel() {
		return achievementLevel;
	}
	public void setAchievementLevel(AchievementLevel achievementLevel) {
		this.achievementLevel = achievementLevel;
	}
	public double getTotalPoints() {
		return totalPoints;
	}
	public void setTotalPoints(double totalPoints) {
		this.totalPoints = totalPoints;
	}
	public double getTakenHours() {
		return takenHours;
	}
	public void setTakenHours(double takenHours) {
		this.takenHours = takenHours;
	}
	
	public List<FinalSubjectResult> getSubjects() {
		Collections.sort(subjects, new SubjectComparator());
		return subjects;
	}

	public static class SubjectComparator extends educate.util.MyComparator2<FinalSubjectResult> {
		public int compare(FinalSubjectResult o1, FinalSubjectResult o2) {
			if ( o1 == null || o2 == null ) return 0;
			if ( o1.getSubject() == null || o2.getSubject() == null ) return 0;
			return o1.getSubject().getCode().compareTo(o2.getSubject().getCode());
		}
	}

	public boolean getNeedCalculateCGPA() {
		return needCalculateCGPA == 1;
	}
	public void setNeedCalculateCGPA(boolean needCalculateCGPA) {
		this.needCalculateCGPA = needCalculateCGPA ? 1 : 0;
	}
	
	
	
}
