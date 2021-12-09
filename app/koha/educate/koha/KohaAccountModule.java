package educate.koha;


import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.koha.entity.KohaAccount;
import lebah.portal.action.AjaxModule;

public class KohaAccountModule extends AjaxModule {
	String path = "koha/";
	String vm = "";
	DbPersistence db =new DbPersistence();

	@Override
	public String doAction() throws Exception {
		String command = request.getParameter("command");
		if ( command == null || "".equals(command)) start();
		else if ( "get_student".equals(command)) getStudent();
		return vm;
	}
	
	private void getStudent() throws Exception {
		vm = path + "get_student.vm";
		
		String matricNo = request.getParameter("matric_no");
		
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( student != null ) {
			context.put("student", student);
	    	String pwdNormal = "EIUKLEB8";
	    	String pwdEnc = "RB3D5EAVqfdZsM/bErv77g";
			KohaAccount acct = KohaHelper.getInstance().createAccount(student, pwdNormal, pwdEnc);
			context.put("koha_user", acct);
		}
		else context.remove("student");
		
	}

	private void start() {
		vm = path + "start.vm";
		
	}

}
