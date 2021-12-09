package educate.koha;


import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.koha.entity.KohaAccount;
import lebah.portal.action.AjaxModule;

public class StudentKohaAccountModule2 extends AjaxModule {
	String path = "koha/";
	String vm = "";
	DbPersistence db =new DbPersistence();
	String login = "";

	@Override
	public String doAction() throws Exception {
		
		login = (String) request.getSession().getAttribute("_portal_login");
		System.out.println("student login = " + login);
		
		String command = request.getParameter("command");
		if ( command == null || "".equals(command)) start();
		return vm;
	}
	

	private void getStudent() throws Exception {
		
	
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + login + "'");
		if ( student != null ) {
			context.put("student", student);
			//generate a password
			String pwdNormal = lebah.db.UniqueID.getUID();
			KohaAccount acct = KohaHelper.getInstance().createAccount(student, pwdNormal);
			context.put("koha_user", acct);
		}
		else context.remove("student");
		
	}

	private void start() throws Exception {
		
		getStudent();
		
		vm = path + "start.vm";
	}
	

}
