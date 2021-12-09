package educate.sis.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.poi.Excel1;
import lebah.servlets.IServlet;

public class StudentRefundListExcelServlet implements IServlet {
	
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "refund_list";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
	
		List<String> headers = new ArrayList<String>();
		headers.add("Matric");
		headers.add("Name");
		headers.add("Program");
		headers.add("Intake");
		headers.add("Refund Amount");
		
		String programId = request.getParameter("programId");
		String intakeId = request.getParameter("intakeId");
		List<RefundItem> items = RefundItem.getRefundList(db, programId, intakeId);
		
		List<List> rows =  new ArrayList<List>();
		for ( RefundItem i : items ) {
			List cols = new ArrayList();
			cols.add(i.getStudent().getMatricNo());
			cols.add(i.getStudent().getBiodata().getName());
			cols.add(i.getStudent().getProgram().getName());
			cols.add(i.getStudent().getIntake().getName());
			cols.add(i.getAmount());
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
