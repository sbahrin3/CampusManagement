package educate.sis.struct;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import educate.enrollment.StudentRegistrationUtil;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectSection;
import lebah.db.PersistenceManager;

public class GroupManagement {
	
	private final static int TOTAL_MEMBER = 25;
	private PersistenceManager pm;
	private StudentRegistrationUtil sru;
	public SubjectSection assignGroup( Student student, String centre_code,Subject subject )throws Exception{
		List<SubjectSection> sections = getSections(centre_code);
		SubjectSection section = null;
		System.out.println("sections size: "+sections.size());
		for( SubjectSection sec : sections ){
			System.out.println("sec.id: "+sec.getId());
			/*int capacity = getGroupCapacity(subject.getId(),sec.getId(),student.getProgram().getLevel().getCode());
			if(capacity <= GroupManagement.TOTAL_MEMBER){
				section = sec;
				break;
				
			}else{
				section = sec;
			}*/
			section = sec;
			
		}
		return section;
	}
	public void assignGroup( String matricNo, String centre_code )throws Exception{
		
		pm = new PersistenceManager();
		Vector<Hashtable> v = new Vector<Hashtable>();
		
		
		Vector<StudentSubject> subjects = StudentRegistrationUtil.getCurrentStudentSubjectList(matricNo);
		System.out.println("subjects size: "+subjects.size());
		List<SubjectSection> sections = getSections(centre_code);
		System.out.println("sections size: "+sections.size());
		
		for( StudentSubject s : subjects ){
			System.out.println("s.id: "+s.getId());
			System.out.println("session.id: "+s.getStudentStatus().getSession().getId());
			
			for( SubjectSection sec : sections ){
				System.out.println("sec.id: "+sec.getId());
				
				int capacity = getGroupCapacity( s.getSubject().getId(), sec.getId(), s.getStudentStatus().getStudent().getProgram().getLevel().getCode() );
				System.out.println(s.getSubject().getName()+": "+sec.getName()+"'s capacity: "+capacity);
				
				if( capacity <= GroupManagement.TOTAL_MEMBER ){
					Hashtable ht = new Hashtable();
					ht.put("studentStatus", s.getStudentStatus());
					ht.put("section", sec);
					ht.put("subject", s.getSubject());
					ht.put("idsession", s.getStudentStatus().getSession().getId());
					v.addElement(ht);
					break;
				}
			}
		}
		Set<StudentSubject> hs_subjects = new HashSet<StudentSubject>();
		
		for( int i=0; i<v.size(); i++ ){
			System.out.println("loop "+i);
			Hashtable ht = v.elementAt(i);
			
			StudentSubject ss = new StudentSubject();
			ss.setSection((SubjectSection) ht.get("section"));
			ss.setStudentStatus((StudentStatus) ht.get("studentStatus"));
			ss.setSubject((Subject) ht.get("subject"));
			
			hs_subjects.add(ss);
		}
		
		StudentStatus status = StudentRegistrationUtil.getCurrentStudentStatus(matricNo);
		try{
			StudentStatus statusUpdate = (StudentStatus) pm.find(StudentStatus.class).whereId(status.getId()).forUpdate();
			statusUpdate.setStudentSubjects(hs_subjects);
			pm.update();
		}catch( Exception ex ){
			System.err.println("ROLLBACK!!");
			pm.rollback();
		}
	}
	
	public List<SubjectSection> getSections(String centre_code)throws Exception{
		System.out.println("get subject section");
		pm = new PersistenceManager();
		List<SubjectSection> sections = pm.list("SELECT a FROM SubjectSection a WHERE a.learningCentre.code='"+centre_code+"'");
		
		return sections;
	}
	
	public SubjectSection getSection(String centre_code) throws Exception{
		pm = new PersistenceManager();
		SubjectSection section = null;
		List<SubjectSection> sections = pm.list("SELECT a FROM SubjectSection a WHERE a.learningCentre.code='"+centre_code+"'");
		if( sections.size() > 0 ){
			section = sections.get(0);
		}
		return section;
	}
	
	private LearningCentre getCentre(String code)throws Exception{
		pm = new PersistenceManager();
		List<LearningCentre> l = pm.list("SELECT a FROM LearningCentre a WHERE a.code='"+code+"'");
		if(l.size()>0){
			return l.get(0);
		}
		else{
			return null;
		} 
		
	}
	private int getGroupCapacity( String idsubject, String idsection, String levelCode ) throws Exception{
		pm = new PersistenceManager();
		List<Student> students = pm.list("select x from Student x where x.fakeStudent is null and x.program.level.code='"+levelCode+"'");
		Subject subject = (Subject) pm.find(Subject.class, idsubject);
		int i = 0; 
		for( Student student : students ){
			StudentStatus status = StudentRegistrationUtil.getCurrentStudentStatus(student);
			if( status.hasStudentSubject(subject, idsection) ){
				i++;
			}
		}
		return i;
	}
}

