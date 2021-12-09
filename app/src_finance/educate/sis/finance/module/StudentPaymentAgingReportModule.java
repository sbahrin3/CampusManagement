/**
 * 
 */
package educate.sis.finance.module;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.finance.entity.PaymentScheduleItem;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class StudentPaymentAgingReportModule extends LebahModule {
	
	private String path = "apps/finance/studentPaymentAgingReport";
	private DbPersistence db = new DbPersistence();
	
	
	public void preProcess() {
		context.put("numFormat", new DecimalFormat("#,###.00"));
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		System.out.println("command: " + command);
	}

	/* (non-Javadoc)
	 * @see lebah.portal.action.LebahModule#start()
	 */
	@Override
	public String start() {
		listPrograms();
		return path + "/start.vm";
	}
	
	
	private void listPrograms() {
		List<Program> programs = db.list("SELECT a from Program a order by a.code");
		context.put("programs",programs);
		context.put("date", new Date());
	}
	
	@Command("listIntakes")
	public String listIntakes() {
		
		String programId = request.getParameter("programId");
			context.put("program_id", programId);
			
			if ( !"".equals(programId)) {
			Program program = (Program) db.find(Program.class, programId);
			
			if ( program.getLevel() == null ) System.out.println("WARNING: Program " + program.getCode() + " " + program.getName() + " has NO LEVEL!");
			
			String sql = "select distinct i from StudentStatus ss join ss.student s join s.intake i where ss.type.inActive = 0 and s.program.id = '" + programId + "'  order by i.startDate";
			List<Session> intakes = db.list(sql);
			context.put("intakes", intakes);
		} else {
			context.remove("intakes");
		}
			
		
		return path + "/listIntakes.vm";
		
	}
	
	
	
	@Command("getAgingReport2")
	public String getAgingReport2() throws Exception {
		
		String programId = getParam("programId");
		String intakeId = getParam("intakeId");
		
		List<PaymentScheduleItem> payments_30 = new ArrayList<PaymentScheduleItem>();
		List<PaymentScheduleItem> payments_60 = new ArrayList<PaymentScheduleItem>();
		List<PaymentScheduleItem> payments_90 = new ArrayList<PaymentScheduleItem>();
		List<PaymentScheduleItem> payments_120 = new ArrayList<PaymentScheduleItem>();
		
		Date reportDate = new Date();
		String dateIn = getParam("reportDate");
		if ( !"".equals(dateIn)) {
			try {
				reportDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateIn);
			} catch ( Exception e ) { }
		}
		
		context.put("reportDate", reportDate);
		
		System.out.println("getPaymentAgingList");
		payments_30 = getPaymentAgingList(db, 30, programId, intakeId, reportDate);
		System.out.println("done 1");
		payments_60 = getPaymentAgingList(db, 60, programId, intakeId, reportDate);
		System.out.println("done 2");
		payments_90 = getPaymentAgingList(db, 90, programId, intakeId, reportDate);
		System.out.println("done 3");
		payments_120 = getPaymentAgingList(db, 120, programId, intakeId, reportDate);
		System.out.println("done...");
			
		Map<String, Map<String, Object>> students = new HashMap<String, Map<String, Object>>();
		context.put("students", students);
		List<String> studentIds = new ArrayList<String>();
		context.put("studentIds", studentIds);
		
		List<Map<String, Object>> studentList = new ArrayList<Map<String, Object>>();
		
		double total = 0.0d;
		for ( PaymentScheduleItem p : payments_30 ) {
			if ( p.getPaymentSchedule().getStudent() != null ) {
				String studentId = p.getPaymentSchedule().getStudent().getId();
				double balance = round(p.getCurrentBalance(), 2);
				studentIds.add(studentId);
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("student", p.getPaymentSchedule().getStudent());
				data.put("30", balance);
				students.put(studentId, data);
				total += balance;
				
				studentList.add(data);
			}
		}
		context.put("total_30", total);
		
		total = 0.0d;
		for ( PaymentScheduleItem p : payments_60 ) {
			if ( p.getPaymentSchedule().getStudent() != null ) {
				String studentId = p.getPaymentSchedule().getStudent().getId();
				double balance = round(p.getCurrentBalance(), 2);
				Map<String, Object> data = students.get(studentId);
				if ( data != null ) {
					data.put("60", balance);
					total += balance;
				} else {
					studentIds.add(studentId);
					
					data = new HashMap<String, Object>();
					data.put("student", p.getPaymentSchedule().getStudent());
					data.put("60", balance);
					students.put(studentId, data);
					total += balance;
					studentList.add(data);
				}
			}
			
		}
		context.put("total_60", total);
		
		total = 0.0d;
		for ( PaymentScheduleItem p : payments_90 ) {
			if ( p.getPaymentSchedule().getStudent() != null ) {
				String studentId = p.getPaymentSchedule().getStudent().getId();
				double balance = round(p.getCurrentBalance(), 2);
				Map<String, Object> data = students.get(studentId);
				if ( data != null ) {
					data.put("90", balance);
					total += balance;
				} else {
					studentIds.add(studentId);
					data = new HashMap<String, Object>();
					data.put("student", p.getPaymentSchedule().getStudent());
					data.put("90", balance);
					students.put(studentId, data);
					total += balance;
					
					studentList.add(data);
				}
			}
			
		}
		context.put("total_90", total);
		
		total = 0.0d;
		for ( PaymentScheduleItem p : payments_120 ) {
			if ( p.getPaymentSchedule().getStudent() != null ) {
				String studentId = p.getPaymentSchedule().getStudent().getId();
				double balance = round(p.getCurrentBalance(), 2);
				Map<String, Object> data = students.get(studentId);
				if ( data != null ) {
					data.put("120", balance);
					total += balance;
				} else {
					studentIds.add(studentId);
					data = new HashMap<String, Object>();
					data.put("student", p.getPaymentSchedule().getStudent());
					data.put("120", balance);
					students.put(studentId, data);
					total += balance;
					
					studentList.add(data);
				}
			}
			
		}
		context.put("total_120", total);
		
		Collections.sort(studentList, new ProgramIdComparator());
		context.put("studentList", studentList);
		
		List<Map<String, Object>> programList = new ArrayList<Map<String, Object>>();
		context.put("programList", programList);
		
		Map<String, Object> programs = new HashMap<String, Object>();
		
		double total30 = 0.0d;
		double total60 = 0.0d;
		double total90 = 0.0d;
		double total120 = 0.0d;
		
		double sum30 = 0.0d, sum60 = 0.0d, sum90 = 0.0d, sum120 = 0.0d;
		String tempId = "";
		for ( Map<String, Object> data : studentList ) {
			Student s = (Student) data.get("student");
			Double d30 = (Double) data.get("30");
			Double d60 = (Double) data.get("60");
			Double d90 = (Double) data.get("90");
			Double d120 = (Double) data.get("120");
			
			Program program = s.getProgram();
			if ( !tempId.equals(program.getId())) {
				tempId = program.getId();
				
				if ( d30 != null ) total30 = d30.doubleValue();
				if ( d60 != null ) total60 = d60.doubleValue();
				if ( d90 != null ) total90 = d90.doubleValue();
				if ( d120 != null ) total120 = d120.doubleValue();
				
				Map<String, Object> programData = new HashMap<String, Object>();
				programData.put("program", program);
				programData.put("30", total30);
				programData.put("60", total60);
				programData.put("90", total90);
				programData.put("120", total120);
				
				programs.put(tempId, programData);
				programList.add(programData);
				
				
				
			}
			else {
				Map<String, Object> programData = (Map<String, Object>) programs.get(program.getId());
				if ( d30 != null ) total30 += d30.doubleValue();
				if ( d60 != null ) total60 += d60.doubleValue();
				if ( d90 != null ) total90 += d90.doubleValue();
				if ( d120 != null ) total120 += d120.doubleValue();
				
				programData.put("30", total30);
				programData.put("60", total60);
				programData.put("90", total90);
				programData.put("120", total120);
			}
			
			sum30 += total30;
			sum60 += total60;
			sum90 += total90;
			sum120 += total120;
			
		}
		
		context.put("sum30", sum30);
		context.put("sum60", sum60);
		context.put("sum90", sum90);
		context.put("sum120", sum120);
		
		return path + "/agingReport.vm";
	}
	
	private static List<PaymentScheduleItem> getPaymentAgingList(DbPersistence db, int days) throws Exception {
		Date date = new Date();
		Calendar cdate = Calendar.getInstance();
		cdate.setTime(date);
		cdate.add(Calendar.DATE, -(days+1));
		Hashtable h = new Hashtable();
		h.put("date", cdate.getTime());
		
		//String sql = "select p from PaymentScheduleItem p where p.paymentDate > :date group by p.paymentSchedule.student order by p.paymentDate";
		String sql = "select distinct s, p from PaymentScheduleItem p Join p.paymentSchedule.student s where p.paymentDate > :date order by s.biodata.name, p.paymentDate";

		List<PaymentScheduleItem> payments = db.list(sql, h);
		
		return payments;
		
		
	}
	
	
	private static List<PaymentScheduleItem> getPaymentAgingList(DbPersistence db, int days, String programId, String intakeId, Date dateIn) {
		
		System.out.println("days: " + days);
		System.out.println("programId: " + programId);
		System.out.println("intakeId: " + intakeId);
		System.out.println("date: " + dateIn);
		
		if ( dateIn == null ) dateIn = new Date();
		Calendar cdate = Calendar.getInstance();
		cdate.setTime(dateIn);
		cdate.add(Calendar.DATE, -(days+1));
		Hashtable h = new Hashtable();
		h.put("date", cdate.getTime());
		
		String sql = "select p from PaymentScheduleItem p where p.paymentDate > :date ";
		if ( !"".equals(programId) )
			sql += " and p.paymentSchedule.student.program.id = '" + programId + "' ";
		if ( !"".equals(intakeId) )
			sql += " and p.paymentSchedule.student.intake.id = '" + intakeId + "' ";
		if ( "".equals(programId) || "".equals(intakeId))
			sql += " and p.paymentSchedule.student.program is not null and p.paymentSchedule.student.intake is not null ";
		sql += " group by p.paymentSchedule.student order by p.paymentDate";
		
		System.out.println(sql);
		
		List<PaymentScheduleItem> payments = null;
		try {
			payments = db.list(sql, h);
		} catch ( Exception e ) {
			System.out.println("Error get payments:");
			e.printStackTrace();
		}
		
		return payments;
		
		
	}
	
	
	static class StudentNameComparator extends educate.util.MyComparator {
		
		public int compare(Object o1, Object o2) {
			Map<String, Object> s1 = (Map<String, Object>) o1;
			Map<String, Object> s2 = (Map<String, Object>) o2;
			Student student1 = (Student) s1.get("student");
			Student student2 = (Student) s2.get("student");
			if ( student1 == null || student2 == null ) return 0;
			String n1 = student1.getBiodata().getName();
			String n2 = student2.getBiodata().getName();
			return n1.compareTo(n2);
		}
		
	}
	
	static class ProgramIdComparator extends educate.util.MyComparator {
		
		public int compare(Object o1, Object o2) {
			Map<String, Object> s1 = (Map<String, Object>) o1;
			Map<String, Object> s2 = (Map<String, Object>) o2;
			Student student1 = (Student) s1.get("student");
			Student student2 = (Student) s2.get("student");
			if ( student1 == null || student2 == null ) return 0;
			
			Program p1 = student1.getProgram();
			Program p2 = student2.getProgram();
			
			if ( p1 == null || p2 == null ) return 0;
			
			String n1 = p1.getId();
			String n2 = p2.getId();
			return n1.compareTo(n2);
		}
		
	}
	
	
	public static double round(double value, int places) {
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		int days = 30;
		Date date = new Date();
		Calendar cdate = Calendar.getInstance();
		cdate.setTime(date);
		cdate.add(Calendar.DATE, -(days+1));
		Hashtable h = new Hashtable();
		h.put("date", cdate.getTime());
		
		//String sql = "select p from PaymentScheduleItem p where p.paymentDate > :date group by p.paymentSchedule.student order by p.paymentDate";
		String sql = "select s, p from PaymentScheduleItem p Join p.paymentSchedule.student s where p.paymentDate > :date group by s order by p.paymentDate";
		
		List<Object[]> objects = db.list(sql, h);
		
		for ( Object o[] : objects ) {
			
			Student student = (Student) o[0];
			PaymentScheduleItem paymentScheduleItem = (PaymentScheduleItem) o[1];
			
			System.out.println(student.getBiodata().getName());
		}
		
		
		
		
		/*
		List<PaymentScheduleItem> payments = getPaymentAgingList(db, 30);
		System.out.println("30 days");
		for ( PaymentScheduleItem p : payments ) {
			System.out.println(p.getPaymentSchedule().getStudent().getMatricNo() + ", " + p.getPaymentSchedule().getStudent().getBiodata().getName() + ", " + new SimpleDateFormat("dd-MM-yyyy").format(p.getPaymentDate()) + ", " + p.getCurrentBalance());
		}
		

		
		payments = getPaymentAgingList(db, 60);
		System.out.println("60 days");
		for ( PaymentScheduleItem p : payments ) {
			System.out.println(p.getPaymentSchedule().getStudent().getMatricNo() + ", " + p.getPaymentSchedule().getStudent().getBiodata().getName() + ", " + new SimpleDateFormat("dd-MM-yyyy").format(p.getPaymentDate()) + ", " + p.getCurrentBalance());
		}
		
		payments = getPaymentAgingList(db, 90);
		System.out.println("90 days");
		for ( PaymentScheduleItem p : payments ) {
			System.out.println(p.getPaymentSchedule().getStudent().getMatricNo() + ", " + p.getPaymentSchedule().getStudent().getBiodata().getName() + ", " + new SimpleDateFormat("dd-MM-yyyy").format(p.getPaymentDate()) + ", " + p.getCurrentBalance());
		}
		
		payments = getPaymentAgingList(db, 120);
		System.out.println("120 days");
		for ( PaymentScheduleItem p : payments ) {
			System.out.println(p.getPaymentSchedule().getStudent().getMatricNo() + ", " + p.getPaymentSchedule().getStudent().getBiodata().getName() + ", " + new SimpleDateFormat("dd-MM-yyyy").format(p.getPaymentDate()) + ", " + p.getCurrentBalance());
		}
		*/
	}
	
	
	

}
