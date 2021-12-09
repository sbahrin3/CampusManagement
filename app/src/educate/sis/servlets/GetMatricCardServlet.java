package educate.sis.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import lebah.servlets.IServlet;

public class GetMatricCardServlet implements IServlet {

	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		DbPersistence db = new DbPersistence();
    	String studentId = request.getParameter("studentId");
    	Student student = db.find(Student.class, studentId);
    	PrintWriter w = response.getWriter();
		if ( student.getMatricImageFrontName() != null ) {
			w.println("<html><body style=\"background:#DED5F2\">");
			w.println("<table><tr><td>");
			w.println("<img src=\"../download?file=" + student.getMatricImageFrontName() + "\">");
			w.println("</td>");
			//w.println("<td>");
			//w.println("<img src=\"../download?file=" + student.getMatricImageBackName() + "\">");
			//w.println("</td>");
			w.println("</tr></table></body></html>");
		}
		else {
			w.println("Error Creating Matric Card Images!");
		}		
	}
	
}
