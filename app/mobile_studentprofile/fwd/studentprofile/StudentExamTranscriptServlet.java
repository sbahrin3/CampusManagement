package fwd.studentprofile;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

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
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.module.SessionUtil;
import educate.sis.struct.entity.Session;
import lebah.servlets.IServlet;

public class StudentExamTranscriptServlet implements IServlet {
	
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

		SessionUtil u = new SessionUtil();
		Session currentSession = u.getCurrentSession(student.getProgram().getLevel().getPathNo());		
		String sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' order by r.session.startDate";
		List<FinalResult> results = db.list(sql);
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		
		Element rootElement = doc.createElement("exam_results");
		doc.appendChild(rootElement);
		
		for ( FinalResult r : results ) {
			Element resultEl = doc.createElement("result");
			rootElement.appendChild(resultEl);
			
			String sessionName = r.getSession().getName();
			String periodName = r.getPeriod() != null ? r.getPeriod().getName() : "null";
			if ( r.getPeriod() != null && r.getPeriod().hasParent() ) periodName = periodName + " (" + r.getPeriod().getParent().getName() + ")";
			String gpa = new DecimalFormat("##.00").format(r.getGpa());
			String cgpa = new DecimalFormat("##.00").format(r.getCgpa());
			String cumHours = new DecimalFormat("##").format(r.getCumulativeHours());
			
			X.add(doc, resultEl, "period_name", periodName);
			X.add(doc, resultEl, "session_name", sessionName);
			X.add(doc, resultEl, "gpa", gpa);
			X.add(doc, resultEl, "cgpa", cgpa);
			X.add(doc, resultEl, "cumulative_credit", cumHours);
			
			
			Element subjectsEl = doc.createElement("subjects");
			resultEl.appendChild(subjectsEl);
			
			List<FinalSubjectResult> subjects = r.getSubjects();
			for ( FinalSubjectResult s : subjects ) {
				Element subjectEl = doc.createElement("subject");
				subjectsEl.appendChild(subjectEl);
				X.add(doc, subjectEl, "code", s.getSubject().getCode());
				X.add(doc, subjectEl, "name", s.getSubject().getName());
				X.add(doc, subjectEl, "credit", "" + s.getSubject().getCredithrs() );
				X.add(doc, subjectEl, "grade", s.getGrade());
				X.add(doc, subjectEl, "point", new DecimalFormat("##.00").format(s.getPoint()));
				
			}

		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		StreamResult result = new StreamResult(out);
		transformer.transform(source, result);
		
	}
	

	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		Student student = db.find(Student.class, "1376628735113");
		SessionUtil u = new SessionUtil();
		Session currentSession = u.getCurrentSession(student.getProgram().getLevel().getPathNo());		
		String sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' order by r.session.startDate";
		List<FinalResult> results = db.list(sql);
		
		for ( FinalResult r : results ) {
			String sessionName = r.getSession().getName();
			String periodName = r.getPeriod() != null ? r.getPeriod().getName() : "null";
			if ( r.getPeriod() != null && r.getPeriod().hasParent() ) periodName = periodName + " (" + r.getPeriod().getParent().getName() + ")";
			r.getSubjects();
		}
		
	}


}

/*
 * 
<exam_results>
    <result>

      <session_name></session_name>
      <period_name></period_name>

      <credit_unit_taken></credit_unit_taken>
      <calculated_credit_unit></calculated_credit_unit>
      <total_grade_point></total_grade_point>
      <gpa></gpa>
      <cgpa></cgpa>
      <academic_status></academic_status>


      <subjects>
        <subject>
	<code>PHAR 1113</code>
	<name>Pharmaceutical Chemistry I</name>
        <credit>3</credit>
        <grade>A+</grade>
        <point>4.0</point>
        </subject>
      <subjects>

  </result>
</exam_results>

*/
