package fwd.studentprofile;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

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
import educate.enrollment.entity.StudentSubject;
import educate.sis.module.ProgramUtil;
import educate.sis.module.StudentStatusUtil;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.SubjectReg;
import lebah.servlets.IServlet;

public class StudentCoursesServlet implements IServlet {
	
	DbPersistence db = new DbPersistence();

	@Override
	public void doService(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		PrintWriter out = res.getWriter();
		String studentId = req.getParameter("studentId");
		try {
			createXML(out, studentId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createXML(PrintWriter out, String studentId) throws Exception {
		Student student = db.find(Student.class, studentId);
		StudentStatus studentStatus = getCurrentStudentStatus(student);
		
		Set<StudentSubject> subjects = studentStatus.getStudentSubjects();
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		
		Element rootElement = doc.createElement("registered_subjects");
		doc.appendChild(rootElement);

		for ( StudentSubject s : subjects ) {
			Element parent = doc.createElement("subject");
			rootElement.appendChild(parent);
			
			X.add(doc, parent, "code", s.getSubject().getCode());
			X.add(doc, parent, "name", s.getSubject().getName());
			X.add(doc, parent, "credit", Integer.toString(s.getSubject().getCredithrs()));
			X.add(doc, parent, "status", s.getSubjectStatus() != null ? s.getSubjectStatus() : "");
			
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		StreamResult result = new StreamResult(out);
		transformer.transform(source, result);
		
	}
	
	private StudentStatus getCurrentStudentStatus(Student student) {
		StudentStatus studentStatus = null;
		try {
			StudentStatusUtil pu = new StudentStatusUtil();
			studentStatus = pu.getCurrentStudentStatus(student); //StudentRegistrationUtil.getCurrentStudentStatus(student);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ( studentStatus == null ) studentStatus = getLastStudentStatus(student);
		return studentStatus;
	}	
	
	private StudentStatus getLastStudentStatus(Student student) {
		try {
			StudentStatusUtil pu = new StudentStatusUtil();
			StudentStatus studentStatus = pu.getLastStudentStatus(student); 
			return studentStatus;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}	
	
	private Set<SubjectReg> getRegisteredSubjects(StudentStatus studentStatus) throws Exception {
		Set<SubjectReg> subjects = null;
		Student student = studentStatus.getStudent();
		String programId = student.getProgram() != null ? student.getProgram().getId() : null;
		String centreId = student.getLearningCenter() != null ? student.getLearningCenter().getId() : null;
		String intakeId = student.getIntake() != null ? student.getIntake().getId() : null;
		
		Period period = studentStatus.getPeriod();
		if ( period != null ) {
			if ( programId != null && centreId != null && intakeId != null ) {
				String sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
					"and p.learningCenter.id = '" + centreId + "' " +
					"and p.session.id = '" + intakeId + "'";
				ProgramStructure ps = (ProgramStructure) db.get(sql);
				if ( ps != null ) {
					ProgramUtil pu = new ProgramUtil();
					if ( studentStatus != null ) {
						subjects = pu.getSubjectRegs(ps, studentStatus.getPeriod().getId());
					}
				}
			}
		}
		return subjects;
	}

}
