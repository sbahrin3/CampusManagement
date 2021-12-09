package util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import lebah.servlets.IServlet;

public abstract class ImgSrcServlet implements IServlet {
	
	protected DbPersistence db = new DbPersistence();

	@Override
	public void doService(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		try {
			ServletOutputStream out = res.getOutputStream();
			res.setContentType("image/jpeg");
			out.write(getBinaryData(req));
			out.flush();
			out.close();			
		} catch (Exception e ) {
			res.setContentType("text/html");
			PrintWriter out = res.getWriter();
			e.printStackTrace(out);
			out.flush();
			out.close();			
		}		
	}

	public abstract byte[] getBinaryData(HttpServletRequest req);
}
