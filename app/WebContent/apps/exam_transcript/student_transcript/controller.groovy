import lebah.db.*
import javax.persistence.*
import educate.enrollment.*
import educate.sis.exam.entity.*

println command
switch(command){
	case "ok":
		index()
		use_view="index.vm"
	break;
	default:
		//index()
		//use_view="index.vm"
		use_view="message.vm"
	break;
}

def index(){
	
	def pm = new PersistenceManager()
	def matricNo = req.session.getAttribute("_portal_login")
	def student = new StudentInformation().getStudentInfo(matricNo)
	def name = student.biodata.name
	def idstudent = student.id
	
	def transcript = ExamManager.getExamTranscript(idstudent)
	
	def statusList = pm.list("select x from StudentStatus x where x.student.id='idstudent' order by x.session.startDate")
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
	if(transcript != null){
		context.put("sessions", sessions)
		context.put("quit", quit)
		context.put("transcript", transcript)
	}else{
		context.put("sessions", "")
		context.put("quit", "")
		context.put("transcript", "")
		context.put("student", student)
	}
}