package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.velocity.Template;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.module.StudentStatusUtil;
import lebah.portal.velocity.VTemplate;

public class PrintStudentAcctStatementModule extends VTemplate {
	
	String path = "apps/util/finance/acct_stmt/";
	DbPersistence db = new DbPersistence();
	
	public Template doTemplate() throws Exception {
		setShowVM(false);
		context.put("today", new Date());
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("df", new SimpleDateFormat("dd MMM, yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.put("util", new lebah.util.Util());
		Template template = engine.getTemplate(getAcctStatement());	
		return template;		
	}

	private String getAcctStatement() throws Exception {
		String matricNo = request.getParameter("matricNo");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		
		AccountStatementUtil util = new AccountStatementUtil();
		AccountStatement acct = util.getAccountStatement(student);
		
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		context.put("student_status", studentStatus);
		
		context.put("account_statement", acct);		
		return path + "print_template.vm";
	}

}
