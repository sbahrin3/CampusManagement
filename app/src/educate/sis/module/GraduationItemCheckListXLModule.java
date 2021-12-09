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
import educate.sis.struct.entity.GraduanItemCheck;
import lebah.servlets.IServlet;

public class GraduationItemCheckListXLModule implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "grad_list";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
	
		List<String> headers = new ArrayList<String>();
		headers.add("Matric");
		headers.add("Name");
		headers.add("Robe");
		headers.add("Scroll");
		headers.add("Transcript");
		headers.add("Photos");
		headers.add("Alumni Card");
		headers.add("Tracer Study");
		
		List<GraduanItemCheck> items = (List<GraduanItemCheck>) request.getSession().getAttribute("graduans");
		
		List<List> rows =  new ArrayList<List>();
		for ( GraduanItemCheck i : items ) {
			List cols = new ArrayList();
			cols.add(i.getStudent().getMatricNo());
			cols.add(i.getStudent().getBiodata().getName());
			cols.add(i.getRobeStatus() == 1 ? "Y" : "");
			cols.add(i.getScrollStatus() == 1 ? "Y" : "");
			cols.add(i.getTranscriptStatus() == 1 ? "Y" : "");
			cols.add(i.getPhotoStatus() == 1 ? "Y" : "");
			cols.add(i.getAlumniCardStatus() == 1 ? "Y" : "");
			cols.add(i.getTracerStudyStatus() == 1 ? "Y" : "");
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
