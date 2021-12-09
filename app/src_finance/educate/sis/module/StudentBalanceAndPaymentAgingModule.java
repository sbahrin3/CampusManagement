package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Session;

public class StudentBalanceAndPaymentAgingModule extends StudentRefundModule {
	
	@Override
	public String doAction() throws Exception {
		
		outstandingModule = true;
		session = request.getSession();
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		context.put("dateFormat", dateFormat);
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( "list_outstanding".equals(command)) doOutstandingList();
		super.doAction();
		return vm;
	}

	private void doOutstandingList() {
		String programId = request.getParameter("program_id");
		String intakeId = request.getParameter("intake_id");
		String sessionId = request.getParameter("session_id");
		String listType = request.getParameter("list_type");
		
		context.put("session_id", sessionId);
		
		Date todayDate = new Date();
		//selected session
		Session ses = db.find(Session.class, sessionId);
		Date endDate = ses.getEndDate();
		//date of reference
		Date refDate = endDate.after(todayDate) ? todayDate : endDate;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(refDate);
		c1.add(Calendar.MONTH, -3);
		
		Calendar c2 = Calendar.getInstance();
		c2.setTime(refDate);
		c2.add(Calendar.MONTH, -6);
		
		Calendar c3 = Calendar.getInstance();
		c3.setTime(refDate);
		c3.add(Calendar.MONTH, -12);
		
		context.put("refDate", refDate);
		context.put("dateAging3", c1.getTime());
		context.put("dateAging6", c2.getTime());
		context.put("dateAging12", c3.getTime());
		
		
		List<PaymentAging> listAging1 = paymentAging(db, ses, 3);
		List<PaymentAging> listAging2 = paymentAging(db, ses, 6);
		List<PaymentAging> listAging3 = paymentAging(db, ses, 12);
		//List<PaymentAging> listAging4 = paymentAging(db, sessionId, 36);
		
		String sql1 = "select new educate.sis.module.SAmount(s.matricNo, SUM(i.amount), i.student, st.period, st.session, st.type) " +
		"from Invoice i join i.student s join s.status st where " +
		" st.session.id = '" + sessionId + "' ";
		if ( !"all".equals(programId)) sql1 += " and s.program.id = '" + programId + "' ";
		if ( !"".equals(intakeId)) sql1 += " and s.intake.id = '" + intakeId + "' ";
		if ( "active".equals(listType)) sql1 += " and st.type.inActive = 0 ";
		sql1 += " group by s.id";
		
	
		String sql2 = "select new educate.sis.module.SAmount(s.matricNo, SUM(i.amount), i.student, st.period, st.session, st.type) " +
		"from Payment i join i.student s join s.status st where " +
		" st.session.id = '" + sessionId + "' ";
		if ( !"all".equals(programId)) sql2 += " and s.program.id = '" + programId + "' ";
		if ( !"".equals(intakeId)) sql2 += " and s.intake.id = '" + intakeId + "' ";
		if ( "active".equals(listType)) sql2 += " and st.type.inActive = 0 ";
		sql2 += " group by s.id";
		
		List<SAmount> list1 = db.list(sql1);
		System.out.println("Invoice List: " + list1.size());
		List<SAmount> list2 = db.list(sql2);
		System.out.println("Payment List: " + list2.size());
		List<RefundItem> students = new ArrayList<RefundItem>();
		
		//for excel
		List<String> headers = new ArrayList<String>();
		headers.add("Matric No");
		headers.add("IC No");
		headers.add("Name");
		headers.add("Semester");
		headers.add("Status");
		headers.add("Balance");
		headers.add("3 months");
		headers.add("6 months");
		headers.add("12 months");
		
		List<List> rows = new ArrayList<List>();
		//
		
		for (SAmount s2 : list2 ) {
			if ( list1.contains(s2)) {
				int i = list1.indexOf(s2);
				SAmount s1 = list1.get(i);
				double balance = s1.getAmount() - s2.getAmount();
				if ( balance > 0 &&  balance < 0.009 ) balance = 0; //to ignore very small negative amount
				if ( balance > 0 ) {
					
					double agingAmount1 = getPaymentAgingAmountFromList(s1.getStudentId(), listAging1);
					double agingAmount2 = getPaymentAgingAmountFromList(s1.getStudentId(), listAging2);
					double agingAmount3 = getPaymentAgingAmountFromList(s1.getStudentId(), listAging3);
					
					students.add(new RefundItem(s1.getStudent(), s1.getPeriod(), s1.getSession(), s1.getStatus(), balance, agingAmount1, agingAmount2, agingAmount3));
					
					//excel
					List cols = new ArrayList();
					rows.add(cols);
					cols.add(s1.getStudent() != null ? s1.getStudent().getMatricNo() : "NULL");
					cols.add(s1.getStudent() != null ? s1.getStudent().getBiodata().getIcno() : "NULL");
					cols.add(s1.getStudent() != null ? s1.getStudent().getBiodata().getName() : "NULL");
					cols.add(s1.getPeriod() != null ? s1.getPeriod().getName() : "NULL");
					cols.add(s1.getStatus() != null ? s1.getStatus().getName() : "NULL");
					cols.add(balance);
					cols.add(agingAmount1);
					cols.add(agingAmount2);
					cols.add(agingAmount3);
					//
					
				}
			}
		}
		context.put("students", students);
		
		//excel
		Map<String, List> report = new HashMap<String, List>();
		report.put("headers", headers);
		report.put("rows", rows);
		
		context.put("reportname", "outstanding_balance");
		session.setAttribute("outstanding_balance", report);
		//
		
		vm = path + "list_outstandings.vm";
	}
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		String sessionId = "2012/2013-M";
		
		//paymentAging(db, sessionId, 3);
		//paymentAging(db, sessionId, 6);
		//paymentAging(db, sessionId, 12);
		//paymentAging(db, sessionId, 36);
		
	}

	private List<PaymentAging> paymentAging(DbPersistence db, Session session, int agingInMonth) {
		//today date
		Date todayDate = new Date();
		//selected session
		Date endDate = session.getEndDate();
		
		//date of reference
		Date refDate = endDate.after(todayDate) ? todayDate : endDate;
		Calendar c = Calendar.getInstance();
		c.setTime(refDate);
		c.add(Calendar.MONTH, -1 * agingInMonth);
		
		Hashtable h = new Hashtable();
		h.put("date1", c.getTime());
		h.put("date2", refDate);
		String sql = "select new educate.sis.module.PaymentAging(s.id, SUM(i.amount)) " +
		"from Payment i join i.student s join s.status st where " +
		"st.session.id = '" + session.getId() + "' " +
		"and st.type.inActive = 0 " +
		"and i.createDate >= :date1 " +
		"and i.createDate <= :date2 " +
		"group by s.id";
		
		List<PaymentAging> data = db.list(sql, h);
		System.out.println(data.size());
		return data;

	}
	
	private double getPaymentAgingAmountFromList(String studentId, List<PaymentAging> list) {
		double d = 0.0d;
		for ( PaymentAging pa : list ) {
			if ( studentId.equals(pa.getStudentId()) ) {
				d = pa.getAmount();
				break;
			}
		}
		return d;
	}

}
