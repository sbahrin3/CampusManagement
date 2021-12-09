package educate.enrollment.entity;

import javax.persistence.*;

import educate.sis.exam.entity.AssessmentResult;
import educate.sis.struct.entity.*;

import java.util.*;

  
@Entity
@Table(name="enrl_studentstatus")
public class StudentStatus implements Comparable {
	
	@Id
	private String id;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Student student;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Session session;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Period period;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Session batch; 
	
	private int repeatNo;

	@ManyToOne
	private StatusType type;
	@Temporal(TemporalType.DATE)
	private Date statusDate;
	
	@ManyToOne @Column(length=50)
	private WithdrawType withdrawType;
	@Column(length=255)
	private String withdrawRemark;
	
	
	@Column(length=255)
	private String remark;
	
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="studentStatus")
	private Set<StudentSubject> studentSubjects;
	
	@ManyToOne(fetch= FetchType.LAZY)
	private SubjectSection section;
	
	private int isCurrent;
	
	private int subjectsValidated;
	
	@Transient
	private int pastStatus;
	@Transient
	private int changeable;
	
	public StudentStatus() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public StatusType getType() {
		return type;
	}
	public void setType(StatusType type) {
		this.type = type;
	}
	
	public Set<StudentSubject> getStudentSubjects() {
		return studentSubjects; 
	}

	public List<StudentSubject> getStudentSubjectsOrderByCode() {
		List<StudentSubject> list = new ArrayList<StudentSubject>();
		list.addAll(studentSubjects);
		Collections.sort(list, new StudentSubjectComparator("code"));
		return list; 
	}
	
	public List<StudentSubject> getStudentSubjectsOrderByName() {
		List<StudentSubject> list = new ArrayList<StudentSubject>();
		list.addAll(studentSubjects);
		Collections.sort(list, new StudentSubjectComparator(""));
		return list; 
	}

	public void setStudentSubjects(Set<StudentSubject> studentSubjects) {
		this.studentSubjects = studentSubjects;
	}
	public boolean addStudentSubject(StudentSubject studentSubject){
		boolean added = false;
		
		if (studentSubjects == null) studentSubjects = new HashSet<StudentSubject>();
		
		boolean hasSubject = false;
		
		if ( studentSubjects != null && studentSubjects.size() > 0 ) {
			for ( StudentSubject s : studentSubjects ) {
				if ( s.getSubject().getId().equals(studentSubject.getSubject().getId())) {
					hasSubject = true;
					break;
				}
			}
		}
		if ( !hasSubject ) {
			studentSubject.setStudentStatus(this);
			studentSubjects.add(studentSubject);
			added = true;
		}
		return added;
	}
	
	public void delStudentSubject(StudentSubject studentSubject){
		if(studentSubjects == null) return;
		studentSubjects.remove(studentSubject);
	}
	

	public boolean hasStudentSubject(Subject subject) {
		boolean found = false;
		for (StudentSubject s : studentSubjects ) {
			if ( s.getSubject().getId().equals(subject.getId())) {
				found = true;
			}
		}
		return found;
	}
	
	public String getSubjectStatus(Subject subject) {
		String subjectStatus = "";
		for (StudentSubject s : studentSubjects ) {
			if ( s.getSubject().getId().equals(subject.getId())) {
				subjectStatus = s.getSubjectStatus();
				break;
			}
		}
		return subjectStatus;		
	}
	
	public StudentSubject getStudentSubject(Subject subject) {
		if ( subject == null ) return null;
		StudentSubject st = null;
		if ( studentSubjects != null ) {
			for (StudentSubject s : studentSubjects ) {
				if ( s.getSubject().getId().equals(subject.getId())) {
					st = s;
					break;
				}
			}
		}
		return st;		
	}
	
	public StudentSubject getStudentSubject(String subjectId) {
		StudentSubject st = null;
		for (StudentSubject s : studentSubjects ) {
			if ( s.getSubject().getId().equals(subjectId)) {
				st = s;
				break;
			}
		}
		return st;		
	}	
	
	
	public boolean hasStudentSubject(Subject subject, String sectionId) {
		boolean found = false;
		//int totalsubjest = studentSubjects.size();
		for (StudentSubject s : studentSubjects ) {
			if( s.getSection() != null && s.getSubject() != null ){
				String codesection = s.getSection().getCode();
				String idsubject = s.getSubject().getId();
				if ( idsubject.equals(subject.getId()) && codesection.equals(sectionId)) {
					found = true;
				}
			}
		}
		return found;
	}	

	@Override
	public boolean equals(Object o) {
		StudentStatus result = (StudentStatus)o;
		if(result.id.equals(id))
			return true;
		else
			return false;
	}
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public int compareTo(Object o) {
		StudentStatus status1 = (StudentStatus) o;
		Session session1 = status1.getSession();
		Session session = this.getSession();
		if ( session == null || session1 == null ) return 0;
		if ( session.getStartDate().before(session1.getStartDate())) return -1;
		else if ( session.getStartDate().after(session1.getStartDate())) return 1;
		else return 0;
	}
	
	public Set<StudentSubject> getStudentSubjectsUnchecked() {
		//isDuplicateSubject = checkDuplicateSubjects();
		return studentSubjects;
	}

	public Session getBatch() {
		return batch;
	}

	public void setBatch(Session batch) {
		this.batch = batch;
	}

	public int getRepeatNo() {
		return repeatNo;
	}

	public void setRepeatNo(int repeatNo) {
		this.repeatNo = repeatNo;
	}
	
	public void setPastStatus(boolean s) {
		pastStatus = s ? 1 : 0;
	}
	
	public boolean getPastStatus() {
		return pastStatus == 1;
	}

	public SubjectSection getSection() {
		return section;
	}

	public void setSection(SubjectSection section) {
		this.section = section;
	}

	public Date getStatusDate() {
		if ( statusDate == null ) {
			return session.getStartDate();
		}
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public boolean getSubjectsValidated() {
		return subjectsValidated == 1;
	}

	public void setSubjectsValidated(boolean b) {
		this.subjectsValidated = b ? 1 : 0;
	}

	public String getWithdrawRemark() {
		return withdrawRemark;
	}

	public void setWithdrawRemark(String withdrawRemark) {
		this.withdrawRemark = withdrawRemark;
	}

	public WithdrawType getWithdrawType() {
		return withdrawType;
	}

	public void setWithdrawType(WithdrawType withdrawType) {
		this.withdrawType = withdrawType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean getChangeable() {
		return changeable == 1;
	}

	public void setChangeable(boolean changeable) {
		this.changeable = changeable? 1 : 0;
	}


	public boolean getIsCurrent() {
		return isCurrent == 1;
	}

	public void setIsCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent ? 1 : 0;
	}






	class StudentSubjectComparator extends educate.util.MyComparator {
		
		String orderBy = "code";
		
		public StudentSubjectComparator(String s) {
			orderBy = s;
		}

		public int compare(Object o1, Object o2) {
			StudentSubject r1 = (StudentSubject) o1;
			StudentSubject r2 = (StudentSubject) o2;
			
			System.out.println("compare r1 = " + r1);
			System.out.println("compare r2 = " + r2);
			
			if ( r1 == null || r2 == null ) return 0;
			
			if ( "code".equals(orderBy)) {
				return r1.getSubject().getCode().compareTo(r2.getSubject().getCode());
			}
			else
			{
				return r1.getSubject().getName().compareTo(r2.getSubject().getName());
			}
		}
		
	}
	
	
	
}
