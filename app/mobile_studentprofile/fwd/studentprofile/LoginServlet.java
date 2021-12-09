package fwd.studentprofile;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import lebah.servlets.IServlet;

public class LoginServlet implements IServlet {
	
	DbPersistence db = new DbPersistence();

	@Override
	public void doService(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		PrintWriter out = res.getWriter();
		String userId = req.getParameter("userId");
		String password = req.getParameter("userPass");
		try {
			
			lebah.portal.db.AuthenticateUser au = new lebah.portal.db.AuthenticateUser(req);
			boolean success = au.lookup(userId, password);
			String userLogin = au.getUserLogin();
			if ( success ) {
				String role = au.getRole();
				System.out.println("Role = " + role);
				createXML(out, "success", role, userLogin);
			} else {
				createXML(out, "denied", "anon", "anon");
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}

	private void createXML(PrintWriter out, String success, String role, String userLogin)
			throws ParserConfigurationException,
			TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException {
		
		
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + userLogin + "'");
		String studentId = student != null ? student.getId() : "NULL";
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		
		
		Element rootElement = doc.createElement("login_status");
		doc.appendChild(rootElement);
		
		Element loginStatus = doc.createElement("login");
		rootElement.appendChild(loginStatus);	
		
		Element status = doc.createElement("status");
		status.appendChild(doc.createTextNode(success));
		loginStatus.appendChild(status);
		
		Element roleEl = doc.createElement("role");
		roleEl.appendChild(doc.createTextNode(role));
		loginStatus.appendChild(roleEl);
		
		Element loginId = doc.createElement("login_id");
		loginId.appendChild(doc.createTextNode(userLogin));
		loginStatus.appendChild(loginId);	
		
		Element elStudentId = doc.createElement("student_id");
		elStudentId.appendChild(doc.createTextNode(studentId));
		loginStatus.appendChild(elStudentId);		
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		
		StreamResult result = new StreamResult(out);
		transformer.transform(source, result);
		
	}

}
