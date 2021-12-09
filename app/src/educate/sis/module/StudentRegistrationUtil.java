package educate.sis.module;

import java.util.Date;

import educate.admission.entity.Applicant;
import educate.admission.entity.Biodata;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.registration.StudentData;
import educate.sis.struct.MatricNumGenerator;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.StudyMode;

public class StudentRegistrationUtil {
	
	DbPersistence db = new DbPersistence();
	
	public Student registerNewStudent(Biodata biodata, Program program, Session intake, LearningCentre centre, StudyMode mode, String matricPrefix) throws Exception {
		return registerNewStudent(biodata, program, intake, centre, mode, matricPrefix, true);
	}
	
	
	
	public Student registerNewStudent(Biodata biodata, Program program, Session intake, LearningCentre centre, StudyMode mode, String matricPrefix, boolean addSubjects) throws Exception {
		String matricNo = "";
		matricNo = generateMatricNo(program, intake, centre, mode, matricPrefix);
		//make sure it's not duplication
		while ( ((Long) db.get("select count(s) from Student s where s.matricNo = '" + matricNo + "'") ) > 0 ) {
			matricNo = generateMatricNo(program, intake, centre, mode, matricPrefix);
		}
		return registerNewStudent(biodata, program, intake, centre, addSubjects, matricNo);
	}



	private String generateMatricNo(Program program, Session intake, LearningCentre centre, StudyMode mode, String matricPrefix) throws Exception {
		String matricNo = "";
		if ( !"".equals(matricPrefix)) matricNo = new MatricNumGenerator().generateMatricNum(matricPrefix); 
		else matricNo = new MatricNumGenerator().generateMatricNum(program, intake, centre, mode);
		return matricNo;
	}
	
	public Student registerNewStudentWithMatricNo(Biodata biodata, Program program, Session intake, LearningCentre centre,  String matricNum) throws Exception {
		return registerNewStudent(biodata, program, intake, centre, true, matricNum);
	}

	public Student registerNewStudent(Biodata biodata, Program program,
			Session intake, LearningCentre centre, boolean addSubjects,
			String matricNum) throws Exception {
		//add new student
		db.begin();
		Student student = new Student();
		student.setMatricNo(matricNum);
		student.setBiodata(biodata);
		student.setProgram(program);
		student.setIntake(intake);
		student.setLearningCenter(centre);
		student.setRegisterDate(new Date());
		student.setRegisterTime(new Date());
		db.persist(student);
		db.commit();
		
		//create student status
		StudentStatusUtil util = new StudentStatusUtil();
		util.addStatus(student, student.getIntake(), addSubjects);
		
		if ( student.getProgram().getIsNoneSessionType() ) {
			StudentStatus currentStatus = util.getCurrentStudentStatus(student);
			util.updateCurrentStudentStatus(student, currentStatus);
		}
		
		StudentData.createPortalLogin(student.getMatricNo(), student.getMatricNo(), student.getBiodata().getName());
		return student;
	}

	public Student registerNewStudent(Program program, Session intake, LearningCentre centre, StudyMode mode) throws Exception {
		return registerNewStudent((Applicant) null, program, intake, centre, mode, "");
	}
	
	public Student registerNewStudent(Program program, Session intake, LearningCentre centre, StudyMode mode, String matricPrefix) throws Exception {
		return registerNewStudent((Applicant) null, program, intake, centre, mode, matricPrefix);
	}
	
	public Student registerNewStudent(Applicant app, Program program, Session intake, LearningCentre centre, StudyMode mode) throws Exception {
		return registerNewStudent(app, program, intake, centre, mode, "");
	}
	
	public Student registerNewStudent(Applicant applicant, Program program, Session intake, LearningCentre centre, StudyMode mode, String matricPrefix) throws Exception {
		String matricNo = "";
		matricNo = generateMatricNo(program, intake, centre, mode, matricPrefix);
		//make sure it's not duplication
		while ( ((Long) db.get("select count(s) from Student s where s.matricNo = '" + matricNo + "'") ) > 0 ) {
			matricNo = generateMatricNo(program, intake, centre, mode, matricPrefix);
		}
		//add new student
		db.begin();
		Student student = new Student();
		student.setMatricNo(matricNo);
		
		if ( applicant != null ) {
			student.setApplicant(applicant);
			student.setBiodata(applicant.getBiodata());
		}
		
		student.setProgram(program);
		student.setIntake(intake);
		student.setLearningCenter(centre);
		student.setRegisterDate(new Date());
		db.persist(student);
		db.commit();
		
		//create student status
		StudentStatusUtil util = new StudentStatusUtil();
		util.addStatus(student, student.getIntake(), true);
		
		StudentData.createPortalLogin(student.getMatricNo(), student.getMatricNo(), student.getBiodata().getName());
		return student;
	}

}
