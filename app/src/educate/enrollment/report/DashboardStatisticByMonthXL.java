package educate.enrollment.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.poi.Excel1;
import lebah.servlets.IServlet;

public class DashboardStatisticByMonthXL implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "monthly_statistic";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
	
		List<String> headers = new ArrayList<String>();
		headers.add("PROGRAM : INTAKE");
		headers.add("JAN");
		headers.add("FEB");
		headers.add("MAC");
		headers.add("APR");
		headers.add("MAY");
		headers.add("JUN");
		headers.add("JUL");
		headers.add("AUG");
		headers.add("SEP");
		headers.add("OCT");
		headers.add("NOV");
		headers.add("DEC");
		
		List<String> items = (List<String>) request.getSession().getAttribute("programNames");
		Map<String, Long> stats = (HashMap<String, Long>) request.getSession().getAttribute("stats");
		Map<String, Long> totals = (HashMap<String, Long>) request.getSession().getAttribute("totals");		
		
		List<List> rows =  new ArrayList<List>();
		for ( String i : items ) {
			List cols = new ArrayList();
			cols.add(i);
			for ( int m = 0; m < 12; m++ ) {
				if ( stats.get(i + " - " + m) != null) {
					int value = (int) stats.get(i + " - " + m).longValue();
					cols.add(value);
				}
				else cols.add(0);
			}
			rows.add(cols);
		}
		List cols = new ArrayList();
		cols.add("");
		for ( int m = 0; m < 12; m++ ) {
			if ( totals.get(":" + m) != null) {
				int value = (int) totals.get(":" + m).longValue();
				cols.add(value);
			}
			else cols.add(0);
		}
		rows.add(cols);
		
		
		
		
		ServletOutputStream os = response.getOutputStream();
		Excel1 x = new Excel1();
		try {
			x.createXLS(os, headers, rows);
		} catch (Exception e) {
			e.printStackTrace();
		}
		os.flush();
		os.close();
		
	}

}
