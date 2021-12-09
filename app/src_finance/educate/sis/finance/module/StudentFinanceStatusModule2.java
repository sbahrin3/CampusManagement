package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.module.PAmount;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentFinanceStatusModule2  extends LebahModule {
	
	DbPersistence db = new DbPersistence();
	String path = "apps/util/finance/student_finance_status2";
	
	public void preProcess() {
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
	}

	@Override
	public String start() {
		return path + "/start.vm";
	}

	@SuppressWarnings("unchecked")
	@Command("listStudents")
	public String listStudents() throws Exception {
		String intakeId = getParam("intakeId");
		
		int aging1 = 0, aging2 = 0, aging3 = 0;
		try { aging1 = Integer.parseInt(getParam("aging1")); } catch ( Exception e ) {}
		try { aging2 = Integer.parseInt(getParam("aging2")); } catch ( Exception e ) {}
		try { aging3 = Integer.parseInt(getParam("aging3")); } catch ( Exception e ) {}
		context.put("age1", aging1);
		context.put("age2", aging2);
		context.put("age3", aging3);
		Map<String, BalanceRecord> agingMapper1 = null;
		Map<String, BalanceRecord> agingMapper2 = null;
		Map<String, BalanceRecord> agingMapper3 = null;
		if ( aging1 > 0 ) {
			agingMapper1 = balanceAging(aging1);
			context.put("aging1", agingMapper1);
		} else context.remove("aging1");
		if ( aging2 > 0 ) {
			agingMapper2 = balanceAging(aging2);
			context.put("aging2", agingMapper2);
		} else context.remove("aging2");
		if ( aging3 > 0 ) {
			agingMapper3 = balanceAging(aging3);
			context.put("aging3", agingMapper3);
		} else context.remove("aging3");
		

	
		String sql1 = "select new educate.sis.module.PAmount(s.program.code, SUM(i.amount), i.student.program)" +
		" from Payment i join i.student s ";
		//sql1 += " group by s.biodata.name";
		sql1 += " group by s.program.id";
		
		String sql2 = "select new educate.sis.module.PAmount(s.program.code, SUM(i.amount), i.student.program)" +
		" from Invoice i join i.student s ";
		//sql2 += " group by s.biodata.name";
		sql2 += " group by s.program.id";
		
		List<PAmount> payments = db.list(sql1);
		List<PAmount> invoices = db.list(sql2);
		
		//--excel
		List<String> headers = new ArrayList<String>();
		headers.add("Code");
		headers.add("Name");
		headers.add("Current Balance");
		if ( aging1 > 0 ) headers.add(aging1 + " Days");
		if ( aging2 > 0 ) headers.add(aging2 + " Days");
		if ( aging3 > 0 ) headers.add(aging3 + " Days");
		List<List> rows = new ArrayList<List>();
		//--
		
		List<BalanceRecord> records = new ArrayList<BalanceRecord>();
		context.put("records", records);
		for ( PAmount invoice : invoices ) {
			double balance = invoice.getAmount();
			if ( payments.contains(invoice)) {
				int i = payments.indexOf(invoice);
				PAmount payment = payments.get(i);
				balance = invoice.getAmount() - payment.getAmount();
				if ( balance > 0 &&  balance < 0.009 ) balance = 0; //to ignore very small negative amount
			}
			BalanceRecord rec = new BalanceRecord(invoice.getProgram(), balance);
			records.add(rec);
			
			//excel
			List cols = new ArrayList();
			rows.add(cols);
			cols.add(rec.getProgram() != null ? rec.getProgram().getCode() : "NULL");
			cols.add(rec.getProgram() != null ? rec.getProgram().getName() : "NULL");
			cols.add(rec.getAmount());
			cols.add(agingMapper1 != null ? agingMapper1.get(rec.getProgram().getId()) != null ? agingMapper1.get(rec.getProgram().getId()).getAmount() : "" : "");
			cols.add(agingMapper2 != null ? agingMapper2.get(rec.getProgram().getId()) != null ? agingMapper2.get(rec.getProgram().getId()).getAmount() : "" : "");
			cols.add(agingMapper3 != null ? agingMapper3.get(rec.getProgram().getId()) != null ? agingMapper3.get(rec.getProgram().getId()).getAmount() : "" : "");
			//
		}
		
		Collections.sort(records, new ProgramCodeComparator());
		
		//excel
		Map<String, List> report = new HashMap<String, List>();
		report.put("headers", headers);
		report.put("rows", rows);
		context.put("reportname", "outstanding_balance");
		request.getSession().setAttribute("outstanding_balance", report);
		//
		return path + "/listStudents.vm";
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, BalanceRecord> balanceAging(int agingInDays) {

		Date date = new Date();
		Date refDate = date;
		Calendar c = Calendar.getInstance();
		c.setTime(refDate);
		c.add(Calendar.DAY_OF_MONTH, -1 * agingInDays);		
		Hashtable h = new Hashtable();
		h.put("date", c.getTime());
	
		String sql1 = "select new educate.sis.module.PAmount(s.program.code, SUM(i.amount), i.student.program)" +
		" from Payment i join i.student s ";
		sql1 += " where i.createDate < :date ";
		//sql1 += " group by s.biodata.name";
		sql1 += " group by s.program.id";
		
		String sql2 = "select new educate.sis.module.PAmount(s.program.code, SUM(i.amount), i.student.program)" +
		" from Invoice i join i.student s";
		sql2 += " where i.createDate < :date ";
		//sql2 += " group by s.biodata.name";
		sql2 += " group by s.program.id";
		
		List<PAmount> payments = db.list(sql1, h);
		List<PAmount> invoices = db.list(sql2, h);
		
		Map<String, BalanceRecord> agingMapper = new HashMap<String, BalanceRecord>();
		context.put("agingMapper", agingMapper);
		for ( PAmount invoice : invoices ) {
			double balance = invoice.getAmount();
			if ( payments.contains(invoice)) {
				int i = payments.indexOf(invoice);
				PAmount payment = payments.get(i);
				balance = invoice.getAmount() - payment.getAmount();
				if ( balance > 0 &&  balance < 0.009 ) balance = 0; //to ignore very small negative amount
			}
			BalanceRecord rec = new BalanceRecord(invoice.getProgram(), balance);
			agingMapper.put(invoice.getProgram().getId(), rec);
		}
		return agingMapper;
	}	
	
	class ProgramCodeComparator extends educate.util.MyComparator {
		public int compare(Object o1, Object o2) {
			BalanceRecord s1 = (BalanceRecord) o1;
			BalanceRecord s2 = (BalanceRecord) o2;
			if ( s1.getProgram().getCode() == null || s2.getProgram().getCode() == null ) return 0;
			return s1.getProgram().getCode().compareTo(s2.getProgram().getCode());
		}
	}	
	
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		Calendar c = Calendar.getInstance();
		c.set(2013, 11, 31);
		
		Hashtable h = new Hashtable();
		h.put("date", c.getTime());
		
		String sql = "select s from Student s where s.intake.startDate >= :date";
		List<Student> list = db.list(sql);
		
		
		
		
		
		
	}

	

}
