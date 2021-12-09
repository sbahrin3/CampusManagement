package educate.sis.servlets;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.PDFToImage;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import lebah.servlets.IServlet;

public class CreateMatricCardServlet implements IServlet {

	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
	    	String pdfName = request.getParameter("pdfName");
	    	String pathUrl = request.getParameter("pathUrl");
	    	String studentId = request.getParameter("studentId");
	    	
	    	if ( pdfName == null || "".equals(pdfName)) pdfName = "noname";
	    	if ( pathUrl != null && !"".equals(pathUrl)) {
	    		
	    		
	    		DbPersistence db = new DbPersistence();
				Student student = db.find(Student.class, studentId);
				pdfName = student.getId();
	    		
				String matricCardLocation = "";
		    	ResourceBundle rb = null;
		    	try {
		    		rb = ResourceBundle.getBundle("files");
		    		matricCardLocation = rb.getString("matricCardLocation");
		    	} catch (java.util.MissingResourceException ex) {
		    		matricCardLocation = "c:/temp2/";
		    		ex.printStackTrace();
		    	}
				if ( !matricCardLocation.endsWith("/") ) matricCardLocation += "/";
				File loc = new File(matricCardLocation);
				if ( !loc.exists() ) {
					loc.mkdir();
				}
	    	
				response.setContentType("text/html");
				//response.setHeader("Content-Disposition", "attachment; filename=\"" + pdfName + ".pdf\"");
	
				String serverName = request.getServerName();
				int serverPort = request.getServerPort();
		        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
		        String http = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("://") + 3);
		        String serverUrl = http + server;
				String uri = request.getRequestURI();
				String s1 = uri.substring(1);
				String appname = s1.substring(0, s1.indexOf("/"));
				
		    	String url = serverUrl + "/" + appname + "/" + pathUrl;
			    URL u = new URL(url);
			    HttpURLConnection uc = (HttpURLConnection) u.openConnection();
	
			    String filename = matricCardLocation + "/" + lebah.db.UniqueID.getUID() + ".html";
			    
			    Tidy tidy = new Tidy();
				BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));
				tidy.parse(in, out);
				in.close();
				out.close();
				
				FileOutputStream os = new FileOutputStream(matricCardLocation + pdfName + ".pdf");
				ITextRenderer renderer = new ITextRenderer();
				File file = new File(filename);
				renderer.setDocument(file);
				renderer.layout();
				renderer.createPDF(os);     
				os.flush();
				os.close();
				file.delete();
				
				//create image
				createImage(matricCardLocation + pdfName + ".pdf", matricCardLocation + pdfName + "-");

				PrintWriter w = response.getWriter();
				try {
					
					db.begin();
//					student.setMatricCardImageFront(IOUtils.toByteArray(is1));
//					student.setMatricCardImageBack(IOUtils.toByteArray(is2));
					student.setMatricPdfName(matricCardLocation + pdfName + ".pdf");
					student.setMatricImageFrontName(matricCardLocation + pdfName + "-1.jpg");
					student.setMatricImageBackName(matricCardLocation + pdfName + "-2.jpg");
					db.commit();

					if ( student.getMatricImageFrontName() != null ) {
						w.println("<html><body style=\"background:#DED5F2\">");
						w.println("<table><tr><td>");
						w.println("<img src=\"../download?file=" + student.getMatricImageFrontName() + "\">");
						w.println("</td></tr></table>");
						
						//w.println("</td><td>");
						//w.println("<img src=\"../download?file=" + student.getMatricImageBackName() + "\">");
						//w.println("</td></tr>");
						w.println("</body></html>");
					}
					else {
						w.println("Error Creating Matric Card Images!");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void createImage(String pdfName, String imageName) {

		String [] args_1 =  new String[3];
		args_1[0]  = "-outputPrefix";
		args_1[1]  = imageName;
		args_1[2]  = pdfName;

		try {
			PDFToImage.main(args_1);
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	

}
