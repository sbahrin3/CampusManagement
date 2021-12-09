package educate.sis.servlets;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import lebah.servlets.IServlet;

public class CreatePDFServlet2 implements IServlet {

	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
	    	String pdfName = request.getParameter("pdfName");
	    	String pathUrl = request.getParameter("pathUrl");
	    	
	    	if ( pdfName == null || "".equals(pdfName)) pdfName = "noname";
	    	if ( pathUrl != null && !"".equals(pathUrl)) {
	    	
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + pdfName + ".pdf\"");
	
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
			    
		        File dir = new File("/temp");
				if ( !dir.exists() ) new File("/temp").mkdir();
			    String filename = "/temp/" + lebah.db.UniqueID.getUID() + ".html";
			    
			    Tidy tidy = new Tidy();
				BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));
				tidy.parse(in, out);
				in.close();
				out.close();
				
				ServletOutputStream os = response.getOutputStream();
				ITextRenderer renderer = new ITextRenderer();
				File file = new File(filename);
				renderer.setDocument(file);
				renderer.layout();
				renderer.createPDF(os);     
				os.flush();
				os.close();
				
				file.delete();
				
	    	}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
