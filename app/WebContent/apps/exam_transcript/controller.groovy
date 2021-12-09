import lebah.db.*
import educate.sis.struct.entity.*
import educate.enrollment.entity.*
import educate.sis.exam.entity.*
import educate.sis.struct.entity.Subject
import educate.enrollment.*
import java.util.*

println "COMMAND=" + command

switch(command){

	case"get_student_by_name":
		get_student_by_name()
		use_view="student_transcript.vm"
		break

	case"get_student_by_matrik":
		get_student_by_matrik()
		use_view="student_transcript.vm"
		break

	case "get_student": 
		get_student_transcript()
		use_view="student_transcript.vm"
		break
	case "find_student":
		find_student()
		use_view="find_student.vm"
		break
	case "select_student_status":
		select_student_status()
		use_view= "marks_entry.vm"
		break
	case "save_result":
		save_result()
		select_student_status()
		use_view = "marks_entry.vm"
		break
		
	case "submit":
		select_student_intake ()
		use_view = "display_filter.vm"
		break
		
	default:
		clear();
		use_view="get_student.vm"
		break
}
def clear(){

	def date = new Date();
	def ndate = date + 30
	def intake =[]
	
	def pm = new PersistenceManager()
	def intakeList = pm.list("select x from Session x")
	def schoolList = pm.list("select x from Faculty x")

	
	for(a in intakeList){
		if(a.startDate < ndate){
			intake<<a
		}
	}
	
	context.put ("intakeList",intake)
	context.put ("schoolList", schoolList)
	
	context.put("sessions", "")
	context.put("quit", "")
	context.put("transcript","")
	context.put("student","")
	
}


def get_student_by_name(){
	def pm = new PersistenceManager()
	def name = req.getParameter("find_name")
	println"name: "+name
	
	def list = pm.list("select a from Student a WHERE a.biodata.name like '%name%'")
	def student_id = list.get(0).id
	println"id: "+student_id

	def transcript = ExamManager.getExamTranscript(student_id)

	def statusList = pm.list("select x from StudentStatus x where x.student.id='$student_id' order by x.session.startDate")
	def sessions = []
	def quit = 0
	
	for( studentStatus in statusList ){
		if( studentStatus.type != null ){
			if( studentStatus.type.code.equals("DES") ){
				sessions << studentStatus.session
			}
			if( studentStatus.type.code.equals("QUIT") ){
				quit++
			}
		}
	}
	
	context.put("sessions", sessions)
	context.put("quit", quit)
	context.put("transcript", transcript)

}

def get_student_by_matrik(){

	def pm = new PersistenceManager()
	
	def matrik = req.getParameter("matric_no")
	println"matrik: "+matrik
	
	def list = pm.list("select a from Student a WHERE a.matricNo='"+matrik+"'")
	def student_id = list.get(0).id
	println"id: "+student_id

	def transcript = ExamManager.getExamTranscript(student_id)
	
	def statusList = pm.list("select x from StudentStatus x where x.student.id='$student_id' order by x.session.startDate")
	def sessions = []
	def quit = 0
	
	for( studentStatus in statusList ){
		if( studentStatus.type != null ){
			if( studentStatus.type.code.equals("DES") ){
				sessions << studentStatus.session
			}
			if( studentStatus.type.code.equals("QUIT") ){
				quit++
			}
		}
	}
	
	context.put("sessions", sessions)
	context.put("quit", quit)
	context.put("transcript", transcript)
	
}


def get_student_transcript() {
	def pm = new PersistenceManager2()
	def student_id = req.getParameter("student_id")
	def transcript = ExamManager.getExamTranscript(student_id)
	
	def statusList = pm.list("select x from StudentStatus x where x.student.id='$student_id' order by x.session.startDate")
	def sessions = []
	def quit = 0
	
	for( studentStatus in statusList ){
		if( studentStatus.type != null ){
			if( studentStatus.type.code.equals("DES") ){
				sessions << studentStatus.session
			}
			if( studentStatus.type.code.equals("QUIT") ){
				quit++
			}
		}
	}
	
	context.put("sessions", sessions)
	context.put("quit", quit)
	context.put("transcript", transcript)
	
	println(student_id)
	
}
def find_student() {
	
	def pm = new PersistenceManager()
	def find_name = req.getParameter("find_name")
	def list = pm.list("select s from Student s where s.fakeStudent IS null order by s.biodata.name")
	context.put("students", list)
	context.put("transcript", "")
}
def get_student_status_list() {

	def pm = new PersistenceManager2()
	def student_id = req.getParameter("student_id")
	println "student id = " + student_id
	def student = pm.find(Student.class, student_id)
	def student_status_list = new Vector<StudentStatus>()
	student_status_list.addAll(student.status)
	Collections.sort(student_status_list)
	
	context.put("student", student)
	context.put("student_status_list", student_status_list)

}
def select_student_status() {
	def student_status_id = req.getParameter("student_status_id")
	def session_result = ExamManager.getSessionResult(student_status_id)
	context.put("session_result", session_result)
	context.put("student_status_id", student_status_id)
}
def save_result() {
	
	def student_status_id = req.getParameter("student_status_id")
	def session_result_id = req.getParameter("session_result_id")
	String[] subject_ids = req.getParameterValues("subject_ids")
	String[] subject_marks = req.getParameterValues("subject_marks")
	ExamManager.saveResult(student_status_id, subject_ids, subject_marks)

}

def select_student_intake ()
{
	
	
	
	def pm = new PersistenceManager()
	def schoolList = pm.list("select x from Faculty x")
	def intakeList = pm.list("select x from Session x")
	
	
	def id_intake = req.getParameter ("intake")
	def id_school = req.getParameter ("School")
	
	

	def studentSch = null, studentInt = null
	
	if( id_school.equals('0') ){
		studentSch = pm.list("select x from Student x where x.fakeStudent is null order by x.program.abbrev,x.biodata.name")
	}else{
		studentSch = pm.list("select x from Student x where x.program.course.faculty.id='$id_school' and x.fakeStudent is null order by x.program.abbrev,x.biodata.name")
	}
	if( id_intake.equals('0') ){
		studentInt = pm.list("select x from Student x where x.fakeStudent is null order by x.program.abbrev,x.biodata.name")
	}else{
		studentInt = pm.list("select x from Student x where x.intake.id='$id_intake' and x.fakeStudent is null order by x.program.abbrev,x.biodata.name")
	}
	
	def students = studentSch.intersect(studentInt)
	context.put("schoolList", schoolList)
	context.put("intakeList", intakeList )
	context.put("students", students)
	

}


