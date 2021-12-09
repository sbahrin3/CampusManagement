package educate.studentaffair.module;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.poi.Excel1;
import educate.studentaffair.entity.ClubActivity;
import lebah.servlets.IServlet;

public class ClubActivityReportXLServlet  implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "club_activities_report";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
	
	
		List<String> headers = new ArrayList<String>();
		headers.add("Club Name");
		headers.add("Activitiy");
		headers.add("Begin Date");
		headers.add("End Date");
		headers.add("Location");
		headers.add("Description");
		headers.add("Status");
		

		List<ClubActivity> activities = (List<ClubActivity>) request.getSession().getAttribute("activities");
		
		List<List> rows =  new ArrayList<List>();
		for ( ClubActivity i : activities ) {
			List cols = new ArrayList();
			cols.add(i.getClub().getName());
			cols.add(i.getName());
			cols.add(new SimpleDateFormat("dd-MM-yyyy").format(i.getStartDate()));
			cols.add(new SimpleDateFormat("dd-MM-yyyy").format(i.getEndDate()));
			cols.add(i.getLocationRemark());
			cols.add(i.getDescription());
			cols.add(i.getProposalStatus());
			rows.add(cols);
		}
		
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

