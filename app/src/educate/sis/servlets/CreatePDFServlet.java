package educate.sis.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import lebah.servlets.IServlet;

public class CreatePDFServlet implements IServlet {

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
				
				ServletOutputStream os = response.getOutputStream();
				ITextRenderer renderer = new ITextRenderer();
				renderer.setDocument(url);      
				renderer.layout();
				renderer.createPDF(os);     
				os.flush();
				os.close();
	    	}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
