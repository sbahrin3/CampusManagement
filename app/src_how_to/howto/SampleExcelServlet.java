package howto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.poi.Excel1;
import educate.sis.module.RefundItem;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.servlets.IServlet;

public class SampleExcelServlet  implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "refund_list";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
	
		List<String> headers = new ArrayList<String>();
		headers.add("Matric");
		headers.add("Name");
		headers.add("Refund Amount");
		
		String programId = request.getParameter("programId");
		String intakeId = request.getParameter("intakeId");
		Program program = !"null".equals(programId) ? db.find(Program.class, programId) : null;
		Session intake = !"null".equals(intakeId) ? db.find(Session.class, intakeId) : null;
		List<RefundItem> items = RefundItem.getRefundList(db, programId, intakeId);
		
		List<List> rows =  new ArrayList<List>();
		for ( RefundItem i : items ) {
			List cols = new ArrayList();
			cols.add(i.getStudent().getMatricNo());
			cols.add(i.getStudent().getBiodata().getName());
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

