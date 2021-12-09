package educate.enrollment;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.struct.entity.Session;
import lebah.db.PersistenceManager;

public class StudentInformation {
	private PersistenceManager pm;
	private Student student;
	private String className = this.getClass().getName();
	
	public Student getStudentInfo(String matricNo) throws Exception{
		//System.out.println("["+className+".getStudentInfo] matricNo = "+matricNo);
		pm = new PersistenceManager();
		List<Student> list = pm.list("SELECT a FROM Student a WHERE a.matricNo='"+matricNo+"'");
		if(list.size()>0){
			student = list.get(0);
			return student;
		}else{
			System.out.println("["+className+".getStudentInfo] No Student Found.");
			return null;
		}
	}
	
	public Student getStudent(String id)throws Exception{
		pm = new PersistenceManager();
		student=(Student)pm.find(Student.class,id);
		return student;
	}
	
	/*
	 * 
	 */
	public Vector<StudentSubject> getStudentSubjects(String sessionId, String student_id)throws Exception{
		
		pm = new PersistenceManager();
		Vector<StudentSubject> v = new Vector<StudentSubject>();
		List<StudentStatus> list = pm.list( "SELECT a FROM StudentStatus a WHERE a.session.id= '"+sessionId+"' " +
				"AND a.student.id='"+student_id+"'");
		StudentStatus studentStatus = list.get(0);
		Set<StudentSubject> subjectList = studentStatus.getStudentSubjects();
		v.addAll(subjectList);
		return v;
		
	}
	
	public StudentStatus getCurrentStatus(Student student) throws Exception{
		pm = new PersistenceManager();
		//Date date = new Date();
		
		/*
		 * The session return here is not the session for a particular student.
		 * This is the current academic session.
		 */
		Session session = StudentRegistrationUtil.getCurrentSession(student.getProgram().getLevel().getPathNo());
		//System.out.println("["+className+".getCurrentStatus] sessionId = "+session.getId());
		//System.out.println("["+className+".getCurrentStatus] studentId = "+student.getId());
		
		//Session session = new StudentRegistrationUtil().getSessionByDate(date);
		
		System.out.println("student = " + student.getId());
		
		List<StudentStatus> stat = pm.list("SELECT x FROM StudentStatus x WHERE x.session.id='" + session.getId() + "' AND x.student.id='" + student.getId() + "'");
		//System.out.println("["+className+".getCurrentStatus] no. of StudentStatus = "+stat.size());
		if (stat.isEmpty()) {
			return null;
		} else {
			StudentStatus status = stat.get(0);
			return status;
		}
		
	}
	
	/*
	 * this method for checking matric no are exist and student is not fake.
	 */
	public boolean isActiveStudent(String matricNo)throws Exception{
		pm = new PersistenceManager();
		List<Student> l = pm.list("SELECT a FROM Student a WHERE a.matricNo='"+matricNo+"'");
		if(l.size()>0){
			Student s = l.get(0);
			if(s.getFakeStudent() == null || s.getFakeStudent().equals("")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/*
	public static void main(String[] args ) throws Exception {
		
		String sessionId= "1220894342215";
		String student_id= "1254506615430";

		DbPersistence db = new DbPersistence();
		//List<StudentSubject> subjects = db.list( "SELECT a FROM StudentSubject a");// WHERE a.studentStatus.session.id='"+sessionId+"' AND a.studentStatus.student.id='"+student_id+"'");
		List<StudentStatus> list = db.list( "SELECT a FROM StudentStatus a WHERE a.session.id='"+sessionId+"' AND a.student.id='"+student_id+"'");
		System.out.println(list.size());
		StudentStatus studentStatus = (StudentStatus) list.get(0);
		Set<StudentSubject> subjectList = studentStatus.getStudentSubjects();
		for ( StudentSubject s : subjectList ) {
			System.out.println(s.getSubject().getCode());
		}
		
	}
	*/
}
