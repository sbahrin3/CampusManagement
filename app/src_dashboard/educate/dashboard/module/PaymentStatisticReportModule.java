package educate.dashboard.module;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Program;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class PaymentStatisticReportModule extends LebahModule {
	
	DbPersistence db = new DbPersistence();
	String path = "apps/dashboard/payment_statistic";
	String[] pellets = {
						"#ff0000","#ff3399","#009900","#2db300","#ff9900","#cc6600","#990033","#800040","#6600ff","#d1b3ff",
						"#0099ff","#004d80","#006622","#ff8c1a","#994d00","#006bb3","#99d6ff","#99b3e6","#2952a3","#ff5500"
					  };	
	
	
	public void preProcess() throws Exception {
		
		Calendar calendar = Calendar.getInstance();
		int reportYear = calendar.get(Calendar.YEAR);
		int firstYear = reportYear - 20;
		context.put("reportYear", reportYear);
		context.put("firstYear", firstYear);
		
	}

	@Override
	public String start() {

		return path + "/start.vm";
	}
	
	
	@Command("getPaymentMonthlyChart")
	public String getPaymentMonthlyChart() throws Exception {
		String reportYear = getParam("reportYear");
		context.put("reportYear", reportYear);
		List<Hashtable> dataList = createReport(reportYear);
		context.put("dataList", dataList);
		return path + "/dataLineChart.vm";
	}
	
	private List<Hashtable> createReport(String reportYear) throws Exception {
		//String reportYear = "2017";
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
				
		List<Hashtable> dateList = new ArrayList<Hashtable>();
		Date dateStart = df.parse("01-01-" + reportYear);
		
		Calendar c1 = Calendar.getInstance();
		c1.setTime(dateStart);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(dateStart);
		int d = c2.getActualMaximum(Calendar.DAY_OF_MONTH);
		c2.add(Calendar.DATE, d-1);
		Hashtable param = new Hashtable();
		param.put("date1", c1.getTime());
		param.put("date2", c2.getTime());
		dateList.add(param);
		
		c2.add(Calendar.DATE, 1);
		
		for ( int i=0; i < 11; i++ ) {
			c1.setTime(c2.getTime());
			c2.setTime(c1.getTime());
			d = c2.getActualMaximum(Calendar.DAY_OF_MONTH);
			c2.add(Calendar.DATE, d-1);
			param = new Hashtable();
			param.put("date1", c1.getTime());
			param.put("date2", c2.getTime());
			
			dateList.add(param);
			
			c2.add(Calendar.DATE, 1);
		}
		
		df.applyPattern("MMM");
		Calendar cal = Calendar.getInstance();
		List<Hashtable> dataList = new ArrayList<Hashtable>();
		for ( Hashtable dates : dateList ) {
			Date date1 = (Date) dates.get("date1");
			Date date2 = (Date) dates.get("date2");
			
			cal.setTime(date2);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			double amount = getAmount(db, date1, date2);
			
			Hashtable h = new Hashtable();
			h.put("year", year);
			h.put("month", month);
			h.put("day", day);
			h.put("monthName", df.format(date2));
			h.put("value", amount);
			
			dataList.add(h);
		}
		
		return dataList;
		
	}
	
	private static double getAmount(DbPersistence db, Date date1, Date date2) {
		Hashtable param = new Hashtable();
		param.put("date1", date1);
		param.put("date2", date2);
		boolean isReceipt = true;
		boolean isCreditNote = false;
		String sql = "";
		sql = "select SUM(i.amount) from Payment i Join i.student s where i.createDate between :date1 and :date2 and i.amount > 0 ";
		if ( !isReceipt || !isCreditNote ) {
			if ( isReceipt ) sql += " and i.isCreditNote = 0 ";
			if ( isCreditNote ) sql += " and i.isCreditNote = 1 ";
		}
		
		List<Double> list = db.list(sql, param);
		double totalAmount = 0.0d;
		if ( list.size() > 0 ) {
			totalAmount = list.get(0) != null ? list.get(0) : 0.0d;
		}
		return round(totalAmount, 2);
	}
	
	
	public static void main2(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		
		Date date1 = df.parse("01-07-2016");
		Date date2 = df.parse("30-07-2016");
		Hashtable param = new Hashtable();
		param.put("date1", date1);
		param.put("date2", date2);
		
		String programId = "";
		boolean isReceipt = true;
		boolean isCreditNote = false;
		String sql = "";
		
		List<Program> programs = db.list("select p from Program p");
		for ( Program p : programs ) {
		
			sql = "select SUM(i.amount) from Payment i Join i.student s where i.createDate between :date1 and :date2 and i.amount > 0 ";
			if ( !"".equals(p.getId())) sql += " and s.program.id = '" + p.getId() + "' ";
			if ( !isReceipt || !isCreditNote ) {
				if ( isReceipt ) sql += " and i.isCreditNote = 0 ";
				if ( isCreditNote ) sql += " and i.isCreditNote = 1 ";
			}
			
			List<Double> list = db.list(sql, param);
			double totalAmount = 0.0d;
			if ( list.size() > 0 ) {
				totalAmount = list.get(0) != null ? list.get(0) : 0.0d;
			}
			
			System.out.println(p.getCode() + ": " + round(totalAmount, 2));
		}
		
		
		
	}

	
	public static double round(double value, int places) {
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

}
