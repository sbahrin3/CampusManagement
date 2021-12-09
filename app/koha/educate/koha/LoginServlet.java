package educate.koha;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lebah.servlets.IServlet;

public class LoginServlet implements IServlet {
	
    public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            PrintWriter out = response.getWriter();
            //http://opac/cgi-bin/koha/opac-user.pl
            String url = request.getParameter("url");
            String userid = request.getParameter("userid");
            String password = request.getParameter("password");
            
            response.setContentType("text/html");
            out.println("<form action=\"" + url + "\" method=\"post\" target=\"_new\">");
            out.println("<input type=\"hidden\" name=\"userid\" value=\"" + userid + "\">");
            out.println("<input type=\"hidden\" name=\"password\" value=\"" + password + "\">");
            out.println("<input type=\"submit\" value=\"LOGIN TO KOHA\">");
            out.println("</form>");
            
            out.println("");
            out.println("");
            
			
        } catch ( Exception e ) {
        	e.printStackTrace();
        }
    }

}
