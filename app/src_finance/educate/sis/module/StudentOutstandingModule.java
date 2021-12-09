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

public class StudentOutstandingModule extends StudentRefundModule {
	
	@Override
	public String doAction() throws Exception {
		outstandingModule = true;
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
		
		List<List> rows = new ArrayList<List>();
		//
		
		for (SAmount s2 : list2 ) {
			if ( list1.contains(s2)) {
				int i = list1.indexOf(s2);
				SAmount s1 = list1.get(i);
				double balance = s1.getAmount() - s2.getAmount();
				if ( balance > 0 &&  balance < 0.009 ) balance = 0; //to ignore very small negative amount
				if ( balance > 0 ) {
					students.add(new RefundItem(s1.getStudent(), s1.getPeriod(), s1.getSession(), s1.getStatus(), balance));
					
					//excel
					List cols = new ArrayList();
					rows.add(cols);
					cols.add(s1.getStudent() != null ? s1.getStudent().getMatricNo() : "NULL");
					cols.add(s1.getStudent() != null ? s1.getStudent().getBiodata().getIcno() : "NULL");
					cols.add(s1.getStudent() != null ? s1.getStudent().getBiodata().getName() : "NULL");
					cols.add(s1.getPeriod() != null ? s1.getPeriod().getName() : "NULL");
					cols.add(s1.getStatus() != null ? s1.getStatus().getName() : "NULL");
					cols.add(balance);
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
		
		paymentAging(db, sessionId, 3);
		paymentAging(db, sessionId, 6);
		paymentAging(db, sessionId, 12);
		
	}

	private static void paymentAging(DbPersistence db, String sessionId,
			int agingInMonth) {
		//today date
		Date todayDate = new Date();
		//selected session
		Session session = db.find(Session.class, sessionId);
		Date endDate = session.getEndDate();
		//date of reference
		Date refDate = endDate.after(todayDate) ? todayDate : endDate;
		Calendar c = Calendar.getInstance();
		c.setTime(refDate);
		c.add(Calendar.MONDAY, -1 * agingInMonth);
		
		Hashtable h = new Hashtable();
		h.put("date1", c.getTime());
		h.put("date2", refDate);
		String sql = "select new educate.sis.module.SAmount(s.matricNo, SUM(i.amount), i.student, st.period, st.session, st.type) " +
		"from Payment i join i.student s join s.status st where " +
		"st.session.id = '" + sessionId + "' " +
		"and st.type.inActive = 0 " +
		"and i.createDate >= :date1 " +
		"and i.createDate <= :date2 " +
		"group by s.id";
		
		List<SAmount> data = db.list(sql, h);
		System.out.println(data.size());
		
//		for ( SAmount d : data ) {
//			
//			System.out.println(d.getStudent().getMatricNo() + ", " + d.getAmount());
//			
//		}
	}

}
