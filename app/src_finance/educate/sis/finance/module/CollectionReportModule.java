/**
 * 
 */
package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.Program;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class CollectionReportModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/collectionReport";
	//private static String[] paymentModes = {"Cash", "Check", "Online", "Credit Card", "Bank-In", "Direct Debit", "Telegraph Transfer", "Bank Draft"};
	private static String[] paymentModes = {"Cash", "Check", "Credit Card", "Bank-In"};
	
	@Override
	public String start() {
		context.put("date", new Date());
		return path + "/start.vm";
	}
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("numFormat", new DecimalFormat("#.00"));
		context.put("print", false);
		
		context.put("portal_login", (String) request.getSession().getAttribute("_portal_login"));
		context.put("portal_username", (String) request.getSession().getAttribute("_portal_username"));

	}
	
	@Command("createReport")
	public String createReport() throws Exception {
		
		String _date = getParam("date");
		Date date = new SimpleDateFormat("dd-MM-yyyy").parse(_date);
		context.put("date", date);
		
		int dayDiff = 0;
		
		try {
			dayDiff = Integer.parseInt(getParam("dayDiff"));
		} catch ( Exception e ) {
			
		}
		
		System.out.println("dayDiff = " + dayDiff);
		context.put("dayDiff", dayDiff);
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		//c.add(Calendar.DATE, dayDiff);
				
		createCollectionReport(db, context, c.getTime());
		
		return path + "/report.vm";
	}

	public static void createCollectionReport(DbPersistence db, VelocityContext context, Date date) throws ParseException {
		context.put("paymentModes", paymentModes);
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		//c1.add(Calendar.DATE, -1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date);
		//c2.add(Calendar.DATE, 1);
		Hashtable h = new Hashtable();
		h.put("date1", c1.getTime());
		h.put("date2", c2.getTime());
		
		System.out.println("date 1 = " + c1.getTime());
		System.out.println("date 2 = " + c2.getTime());
		
		List<Program> programs = db.list("select p from Program p order by p.course.faculty.name");
		
		Map<String, Double> paymentCollection = new HashMap<String, Double>();
		context.put("paymentCollection", paymentCollection);
		
		for ( Program p : programs ) {
			for ( String paymentMode : paymentModes ) {
				String sql = "select sum(p.amount) from Payment p where p.createDate between :date1 and :date2 and p.student.program.id = '" + p.getId() + "' and p.paymentMode = '" + paymentMode + "'";
				Double amount = (Double) db.get(sql, h);
				paymentCollection.put(p.getId() + "_" + paymentMode, amount);
			}
		}
		
		Map<String, Double> totalPaymentCollection = new HashMap<String, Double>();
		context.put("totalPaymentCollection", totalPaymentCollection);
		
		for ( Program p : programs ) {
			String sql = "select sum(p.amount) from Payment p where p.createDate between :date1 and :date2 and p.student.program.id = '" + p.getId() + "'";
			Double amount = (Double) db.get(sql, h);
			totalPaymentCollection.put(p.getId(), amount);
		}
		
		//--test
		String q = "select SUM(i.amount) from Payment i where i.createDate between :date1 and :date2 ";
		List<Double> list = db.list(q, h);
		double totalAmount = 0.0d;
		if ( list.size() > 0 ) {
			totalAmount = list.get(0) != null ? list.get(0) : 0.0d;
		}
		System.out.println("Total Amount = " + totalAmount);
		//--
		
		
		List<Faculty> faculties = db.list("select f from Faculty f order by f.name");
		context.put("faculties", faculties);
		
		Map<String, Double> schoolPaymentCollection = new HashMap<String, Double>();
		context.put("schoolPaymentCollection", schoolPaymentCollection);
		
		Map<String, Double> totalSchoolPaymentCollection = new HashMap<String, Double>();
		context.put("totalSchoolPaymentCollection", totalSchoolPaymentCollection);
		
		Map<String, List<Program>> programList = new HashMap<String, List<Program>>();
		context.put("programList", programList);
		for ( Faculty f : faculties ) {
			List<Program> ps = db.list("select p from Program p where p.course.faculty.id = '" + f.getId() + "' order by p.name");
			programList.put(f.getId(), ps);
			
			for ( String paymentMode : paymentModes ) {
				String sql = "select sum(p.amount) from Payment p where p.createDate between :date1 and :date2 and p.student.program.course.faculty.id = '" + f.getId() + "' and p.paymentMode = '" + paymentMode + "'";
				Double amount = (Double) db.get(sql, h);
				schoolPaymentCollection.put(f.getId() + "_" + paymentMode, amount);
			}
			
			String sql = "select sum(p.amount) from Payment p where p.createDate between :date1 and :date2 and p.student.program.course.faculty.id = '" + f.getId() + "'";
			Double amount = (Double) db.get(sql, h);
			totalSchoolPaymentCollection.put(f.getId(), amount);
			
		}
	}

	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		List<Program> programs = db.list("select p from Program p order by p.course.faculty.name");
		for ( Program p : programs ) {
			System.out.println(p.getName());
		}
	}

}
