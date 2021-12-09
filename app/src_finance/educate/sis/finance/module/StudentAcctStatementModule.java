package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.module.StudentStatusUtil;
import lebah.portal.action.AjaxModule;

public class StudentAcctStatementModule  extends AjaxModule {
	
	String path = "apps/util/finance/acct_stmt/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));			
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "get_student".equals(command)) getStudent();
		return vm;
	}

	private void getStudent() throws Exception {
		vm = path + "statement.vm";
		
		String matricNo = request.getParameter("matric_no");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		
		AccountStatementUtil util = new AccountStatementUtil();
		AccountStatement acct = util.getAccountStatement(student, new Date());
		
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		context.put("student_status", studentStatus);
		
		context.put("account_statement", acct);


	}

	private void start() {
		vm = path + "get_student.vm";
		
	}
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		String matricNo = "C30101100012";
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");

		String sql = "";
		List<Record> records = new ArrayList<Record>();
		sql = "select new educate.sis.finance.module.R(i.createDate, it.description, it.amount)" +
				" from Invoice i " +
				" join i.invoiceItems it " +
				" where i.student.id = '" + student.getId() + "' " +
				" order by i.createDate";
		List<R> rs = db.list(sql);
		for ( R r : rs ) {
			records.add(new Record(r.getDate(), r.getDescription(), r.getValue(), 0));
		}
		//sql = "select new educate.sis.finance.module.R(p.createDate, it.invoiceItem.description, it.amount)" +
		sql = "select new educate.sis.finance.module.R(p.createDate, it.description, it.amount)" +
				" from Payment p " +
				" join p.paymentItems it " +
				" where p.student.id = '" + student.getId() + "' " +
				" order by p.createDate";
		rs = db.list(sql);
		for ( R r : rs ) {
			records.add(new Record(r.getDate(), r.getDescription(), r.getValue(), 1));
		}
		double balance = 0.0d;
		for ( Record r : records ) {
			double amt = r.getAmount();
			if ( r.getSide() == 0 ) balance += r.getAmount();
			else balance -= r.getAmount();
			r.setBalance(balance);
		}
		Collections.sort(records, new RecordComparator());
		for ( Record r : records ) {
			if ( r.getSide() == 0 ) System.out.println("LEFT:" + r.getDescription() + ", " + r.getAmount() + ", " + r.getBalance());
			else System.out.println("RIGHT:" + r.getDescription() + ", " + r.getAmount() + ", " + r.getBalance());
		}
	}
	
	public static class RecordComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			Record r1 = (Record) o1;
			Record r2 = (Record) o2;
			int i = r1.getDate().compareTo(r2.getDate());
			if ( i == 0 ) {
				if ( r1.getSide() == r2.getSide()) return 0;
				else
					return r1.getSide() > r2.getSide() ? 1 : -1;
			}
			else
				return i;
		}
		
	}

}
