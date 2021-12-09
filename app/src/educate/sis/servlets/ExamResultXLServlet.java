package educate.sis.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.poi.Excel1;
import educate.sis.exam.entity.CourseworkItem;
import educate.sis.module.AssessmentResultRequest;
import educate.sis.struct.entity.Subject;
import lebah.portal.db.User;
import lebah.servlets.IServlet;

public class ExamResultXLServlet implements IServlet {
	
	private DbPersistence db = new DbPersistence();

	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String filename = "exam_result";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
		
		
		try {
			String userId = request.getParameter("user_id") != null ? request.getParameter("user_id") : "";
			String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
			String subjectId = request.getParameter("subject_id");
			String sessionId = request.getParameter("session_id") != null ? request.getParameter("session_id") : "";
			String intakeId = request.getParameter("intake_id") != null ? request.getParameter("intake_id") : "";
			String sectionId = request.getParameter("section_id") != null ? request.getParameter("section_id") : "";
			String centreId = request.getParameter("centre_id") != null ? request.getParameter("centre_id") : "";
			
			
			//get teacher information
			if ( !"".equals(userId)) {
				User user = lebah.portal.db.UserData.getUser(userId);
			}
			
			AssessmentResultRequest resultRequest = new AssessmentResultRequest(db);
			
			Subject subject = db.find(Subject.class, subjectId);
			List<CourseworkItem> markItems = resultRequest.getCourseworkItem(subjectId, sessionId, centreId);
			
			List<String> headers = new ArrayList<String>();
			List<List> rows =  new ArrayList<List>();
			headers.add("Matric No");
			headers.add("Name");
			//headers.add("Semester");
			for ( CourseworkItem i : markItems ) {
				if ( i.getPercentage() > 0 )
					headers.add(i.getCode()); //i.getName());
			}
			headers.add("Total");
			headers.add("Grade");
			headers.add("Status");
			rows = resultRequest.listStudents(subject, programId, sessionId, intakeId, centreId, sectionId, markItems);
			
			
			ServletOutputStream os = response.getOutputStream();
			Excel1 x = new Excel1();
			try {
				x.createXLS(os, headers, rows);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			os.flush();
			os.close();


			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	

}
