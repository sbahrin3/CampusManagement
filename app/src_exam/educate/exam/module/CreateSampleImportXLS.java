package educate.exam.module;

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

public class CreateSampleImportXLS  implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "result_example";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
	
		List<String> headers = new ArrayList<String>();
		headers.add("Matric No");
		headers.add("Session Code");
		headers.add("Subject Code");
		headers.add("Mark");

		
		List<List> rows =  new ArrayList<List>();
		List cols = new ArrayList();
		cols.add("PHARM 1308-3373");
		cols.add("AUG 2013");
		cols.add("PHAR 1222");
		cols.add(67);
		rows.add(cols);
		
		cols = new ArrayList();
		cols.add("PHARM 1308-3364");
		cols.add("AUG 2013");
		cols.add("PHAR 1222");
		cols.add(86);
		rows.add(cols);

		cols = new ArrayList();
		cols.add("PHARM 1308-3357");
		cols.add("AUG 2013");
		cols.add("PHAR 1222");
		cols.add(98);
		rows.add(cols);

		cols = new ArrayList();
		cols.add("PHARM 1308-3358");
		cols.add("AUG 2013");
		cols.add("PHAR 1222");
		cols.add(56);
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

