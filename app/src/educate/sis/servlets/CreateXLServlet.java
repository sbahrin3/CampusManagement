package educate.sis.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.poi.Excel1;
import lebah.servlets.IServlet;


public class CreateXLServlet implements IServlet {

	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	
	    	String filename = request.getParameter("filename");
	    	String reportname = request.getParameter("reportname");
	
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
			
			Map<String, List> report = (Map) request.getSession().getAttribute(reportname);
			List<String> headers = report.get("headers");
			List<List> rows = report.get("rows");

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
