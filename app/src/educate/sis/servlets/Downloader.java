package educate.sis.servlets;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lebah.servlets.IServlet;

public class Downloader implements IServlet {
	

	public void doService(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		ServletOutputStream out = res.getOutputStream();
		String fileName = req.getParameter("file");
		String name = fileName.substring(fileName.lastIndexOf("/") + 1);
		res.setContentType("application/x-msdownload");
		res.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
		filetoOS(out, fileName);
		
	}
	
	public void filetoOS(ServletOutputStream out, String fileName) {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			fis = new FileInputStream(fileName);
			bis = new BufferedInputStream(fis);
			int ch;
			while ((ch = bis.read()) > -1) {
				out.print( (char)ch );
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
				if( out != null ) {
					out.flush();
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
