package educate.sis.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.enrollment.entity.StudentStatus;
import educate.poi.Excel1;
import lebah.servlets.IServlet;

public class BatchCreateInvoiceXLServlet  implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "batch_invoice_";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
		
		List<StudentStatus> statuses = (List<StudentStatus>) request.getSession().getAttribute("_status_list");
	
		List<String> headers = new ArrayList<String>();
		headers.add("Matric");
		headers.add("IC No");
		headers.add("Name");
		headers.add("Semester");
		
		List<List> rows =  new ArrayList<List>();
		for ( StudentStatus i : statuses ) {
			List cols = new ArrayList();
			cols.add(i.getStudent().getMatricNo());
			cols.add(i.getStudent().getBiodata().getIcno());
			cols.add(i.getStudent().getBiodata().getName());
			cols.add(i.getPeriod() != null ? i.getPeriod().getName() : "null");
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
