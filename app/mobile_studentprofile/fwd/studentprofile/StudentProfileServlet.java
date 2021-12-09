package fwd.studentprofile;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.module.StudentStatusUtil;
import lebah.servlets.IServlet;

public class StudentProfileServlet implements IServlet {
	
	DbPersistence db = new DbPersistence();

	@Override
	public void doService(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		
		String serverUrl = getServerURL(req);
		PrintWriter out = res.getWriter();
		String studentId = req.getParameter("studentId");
		try {
			createXML(out, studentId, serverUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void createXML(PrintWriter out, String studentId, String serverUrl) throws Exception {
		
		String matricNo = "", name = "null", icno = "null", faculty = "", program = "null", intake = "null", 
		currentSession = "null", semester = "null", photo = "null";
		
		//Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		Student student = db.find(Student.class, studentId);
		if ( student != null ) {
			StudentStatusUtil u = new StudentStatusUtil();
			StudentStatus studentStatus = u.getCurrentStudentStatus(student);
			matricNo = student.getMatricNo();
			name = student.getBiodata().getName();
			icno = student.getBiodata().getIcno();
			program = student.getProgram() != null ? student.getProgram().getName() : "";
			faculty = student.getProgram() != null ? student.getProgram().getCourse().getFaculty().getName() : "";
			intake = student.getIntake().getName();
			currentSession = studentStatus.getSession() != null ? studentStatus.getSession().getName() : "";
			semester = studentStatus.getPeriod() != null ? studentStatus.getPeriod().getName() : "";
			photo = serverUrl +"/download?file=" + student.getPhotoFileName();
		}
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		
		Element rootElement = doc.createElement("student_profile");
		doc.appendChild(rootElement);

		Element parent = doc.createElement("profile");
		rootElement.appendChild(parent);	

		X.add(doc, parent, "matricNo", matricNo);
		X.add(doc, parent, "name", name);
		X.add(doc, parent, "icno", icno);
		X.add(doc, parent, "program", program);
		X.add(doc, parent, "faculty", faculty);
		X.add(doc, parent, "intake", intake);
		X.add(doc, parent, "currentSession", currentSession);
		X.add(doc, parent, "semester", semester);
		X.add(doc, parent, "photo", photo);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		StreamResult result = new StreamResult(out);
		transformer.transform(source, result);
		
	}

	private String getServerURL(HttpServletRequest req) {
		String serverName = req.getServerName();
		int serverPort = req.getServerPort();
        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
        String http = req.getRequestURL().toString().substring(0, req.getRequestURL().toString().indexOf("://") + 3);
        String serverUrl = http + server;		
        
		String uri = req.getRequestURI();
		String s1 = uri.substring(1);
		String appName = s1.substring(0, s1.indexOf("/"));
        String url = serverUrl + "/" + appName;
		return url;
	}	
	

}
