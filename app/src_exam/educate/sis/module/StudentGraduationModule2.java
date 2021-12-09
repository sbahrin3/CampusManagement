package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.List;

import educate.admission.entity.StudentMoheData;
import educate.enrollment.entity.Student;
import educate.sis.general.entity.State;
import educate.sis.struct.entity.Graduate;
import educate.sis.struct.entity.GraduationForm;
import onapp.module.FileNameUtil;

public class StudentGraduationModule2 extends StudentGraduationModule {
	
	
	@Override
	public String doAction() throws Exception {

		
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		context.put("dateFormat", dateFormat);
		context.put("programUtil", new ProgramUtil());
		context.put("list_type", "");
		context.put("student_view", true);
		
		context.put("fileNameUtil", new FileNameUtil());
		
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) getMy();
		else if ( "get_credit_earned".equals(command)) getCreditEarned();
		else if ( "get_credit_balance".equals(command)) getCreditBalance();
		else if ( "get_credit_total_earned".equals(command)) getCreditTotalEarned();
		else if ( "get_credit_total_balance".equals(command)) getCreditTotalBalance();
		else if ( "apply_graduation".equals(command)) applyGraduation();
		else if ( "apply_graduation_done".equals(command)) applyGraduationDone();
		else if ( "apply_graduation_cancel".equals(command)) applyGraduationCancel();
		else if ( "update_biodata".equals(command)) updateBiodata();
		
		else if ( "attendRehearsal".equals(command)) attendRehearsal();
		else if ( "attendCeremony".equals(command)) attendCeremony();
		
		return vm;
	}
	
	private void attendRehearsal() throws Exception {
		vm = path + "confirm_attend.vm";
		String graduateId = getParam("graduate_id");
		Graduate graduate = db.find(Graduate.class, graduateId);
		
		String attend = getParam("attend");
		db.begin();
		if ( "yes".equals(attend)) graduate.setAttendRehearsal(true);
		else if ( "no".equals(attend)) graduate.setAttendRehearsal(false);
		db.commit();
		
	}
	
	private void attendCeremony() throws Exception {
		vm = path + "confirm_attend.vm";
		String graduateId = getParam("graduate_id");
		Graduate graduate = db.find(Graduate.class, graduateId);
		
		String attend = getParam("attend");
		db.begin();
		if ( "yes".equals(attend)) graduate.setAttendCeremony(true);
		else if ( "no".equals(attend)) graduate.setAttendCeremony(false);
		db.commit();
		
	}	

	private void updateBiodata() throws Exception {
		vm = path + "biodata_updated.vm";
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		String name = request.getParameter("student_name");
		String icno = request.getParameter("student_icno");
		String passport = request.getParameter("student_passport");
		String address1 = request.getParameter("student_address1");
		String address2 = request.getParameter("student_address2");
		String address3 = request.getParameter("student_address3");
		String address4 = request.getParameter("student_address4");
		String city = request.getParameter("student_city");
		String postcode = request.getParameter("student_postcode");
		String stateId = request.getParameter("student_state");
		State state = null;
		state = db.find(State.class, stateId);
		String telephoneNo = request.getParameter("student_telephone");
		String mobileNo = request.getParameter("student_mobile");
		String email = request.getParameter("student_email");
		
		db.begin();
		student.getBiodata().setName(name);
		student.getBiodata().setIcno(icno);
		student.getBiodata().setPassport(passport);
		student.getAddress().setAddress1(address1);
		student.getAddress().setAddress2(address2);
		student.getAddress().setAddress3(address3);
		student.getAddress().setAddress4(address4);
		student.getAddress().setPostcode(postcode);
		student.getAddress().setCity(city);
		student.getAddress().setState(state);
		student.getBiodata().setTelephoneNo(telephoneNo);
		student.getBiodata().setMobileNo(mobileNo);
		student.getBiodata().setEmail(email);
		db.commit();
		
	}

	private void applyGraduationCancel() {
		vm = path + "button_apply_graduation.vm";
		
	}

	private void applyGraduationDone() {
		vm = path + "apply_graduation_done.vm";
		
	}

	private void applyGraduation() throws Exception {
		vm = path + "apply_graduation.vm";
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		String graduateId = request.getParameter("graduate_id");
		Graduate graduate = db.find(Graduate.class, graduateId);
		
		db.begin();
		graduate.setStatus("APPLIED");
		db.commit();
		
		context.put("student_graduate", graduate);

		context.put("states", db.list("SELECT a from State a"));
		context.put("nationalities", db.list("select n from Nationality n"));
	}

	private void getMy() throws Exception {
		vm = path + "student.vm";
		String matricNo = (String) request.getSession().getAttribute("_portal_login");
		//for testing ONLY
		//matricNo = "PHARM 1308-3357";
		
		String sql = "";
		sql = "select s from Student s where s.matricNo = '" + matricNo + "'";
		Student student = (Student) db.get(sql);
		if ( student == null ) {
			context.remove("student");
			return;
		}
		else {
			context.put("student", student);
			
			sql = "select g from Graduate g where g.student.id = '" + student.getId() + "'";
			List<Graduate> list = db.list(sql);
			if ( list.size() > 0 ) {
				Graduate graduate = list.get(0);
				context.put("student_graduate", graduate);
				
				db.begin();
				graduate.setStatus("APPLIED");
				db.commit();
			}
			else {
				context.remove("student_graduate");
			}
			
			studentGraduation(student);
			
			//list documents
			
			GraduationForm gf = db.find(GraduationForm.class, "1");
			context.put("graduationForm", gf);
			
			StudentMoheData mohe = (StudentMoheData) db.get("select s from StudentMoheData s where s.matric = '" + matricNo + "'");
			if ( mohe != null ) context.put("tracerStudy", mohe);
			else context.remove("tracerStudy");
			
		}
	}

}
