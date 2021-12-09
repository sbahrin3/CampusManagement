/**
 * 
 */
package educate.sis.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.poi.Excel1;
import lebah.servlets.IServlet;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class StudentListXLModule  implements IServlet {

	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	
			String filename = "studentList";
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
			
			List<Map<String, Object>> studentData =  (List<Map<String, Object>>) request.getSession().getAttribute("studentData");
			
			List<String> headers = new ArrayList<String>();
			List<List> rows =  new ArrayList<List>();
			headers.add("Student Id");
			headers.add("IC No");
			headers.add("Name");
			headers.add("Session");
			headers.add("Semester"); 	
			headers.add("Status");
			headers.add("Total Payment");
			
			for (  Map<String, Object> s : studentData ) {
				List cols = new ArrayList();
				cols.add(s.get("matricno"));
				cols.add(s.get("icno"));
				cols.add(s.get("name"));
				cols.add(s.get("session"));
				cols.add(s.get("semester"));
				cols.add(s.get("status"));
				cols.add(s.get("amountPaidValue"));
				
				rows.add(cols);
			}

			ServletOutputStream os = response.getOutputStream();
			Excel1 x = new Excel1();
			try {
				x.createXLS(os, headers, rows);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			os.flush();
			os.close();
		
	}


}
