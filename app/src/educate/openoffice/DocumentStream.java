package educate.openoffice;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lebah.servlets.IServlet;


/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 *
 * @version 1.0
 * 
 * "http://localhost:8181/educate/servlet/educate.dms.module.DocumentStream?filename=XYZ.pdf"
		
 */
public class DocumentStream implements IServlet {
	
	private static ResourceBundle rb;

	public void doService(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		ServletOutputStream out = res.getOutputStream();
		res.setContentType("application/pdf");
		rb = ResourceBundle.getBundle("files");
		String dir = rb.getString("doc.pdf");
		if ( !dir.endsWith("/")) dir = dir + "/";
		String fileName = req.getParameter("filename");
		
		DocumentUtil du = new DocumentUtil();
		try {
			du.convertToPDF(dir + fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fileToOutputStream(out, dir + fileName);
		
	}
	
	public void fileToOutputStream(ServletOutputStream out, String fileName) {
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
