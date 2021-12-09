package educate.sis.module;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.poi.Excel1;
import educate.sis.exam.entity.Standing;
import educate.sis.struct.entity.Subject;
import lebah.servlets.IServlet;

public class SubjectResultAnalysisListXLModule implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "result_analysis";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
	
		DecimalFormat df = new DecimalFormat("#.00");
		DecimalFormat nf = new DecimalFormat("0");
		
		List<Student> students = (List<Student>) request.getSession().getAttribute("students");
		Set<Subject> subjects = (Set<Subject>) request.getSession().getAttribute("subjects");
		Map<String, SubjectResultData> subjectMap = (Map<String, SubjectResultData>) request.getSession().getAttribute("subjectMap");
		
		Map<String, ExamResultData> resultMap = (Map<String, ExamResultData>) request.getSession().getAttribute("resultMap");
		Map<String, Integer> gpaFreq = (Map<String, Integer>) request.getSession().getAttribute("gpaFreq");
		Map<String, Integer> standingFreqs = (Map<String, Integer>) request.getSession().getAttribute("standingFreqs");
		List<Standing> standings = (List<Standing>) request.getSession().getAttribute("standings");
		
		Excel1 x = new Excel1();
		List<String> headers = new ArrayList<String>();
		headers.add("Matric");
		headers.add("Name");
		for ( Subject s : subjects ) {
			headers.add(s.getCode());
			headers.add("");
			headers.add("");
		}

		List<String> headers2 = new ArrayList<String>();
		headers2.add("");
		headers2.add("");
		for ( Subject s : subjects ) {
			headers2.add(s.getName());
			headers2.add("");
			headers2.add("");
		}	
		
		List<String> headers3 = new ArrayList<String>();
		headers3.add("");
		headers3.add("");
		for ( Subject s : subjects ) {
			headers3.add("Total");
			headers3.add("Point");
			headers3.add("Grade");
		}		
		headers3.add("Total Credit Unit");
		headers3.add("Total Grade Point");
		headers3.add("GPA");
		headers3.add("CGPA");
		headers3.add("Status");
		
		List<List> rows =  new ArrayList<List>();
		for ( Student s : students ) {
			List cols = new ArrayList();
			cols.add(s.getMatricNo());
			cols.add(s.getBiodata().getName());
			
			for ( Subject subj : subjects ) {
				if ( subjectMap.get(subj.getId() + "_" + s.getId()) != null ) {
					Double totalMark = subjectMap.get(subj.getId() + "_" + s.getId()).getTotalMark();
					Double point = subjectMap.get(subj.getId() + "_" + s.getId()).getPoint();
					String grade = subjectMap.get(subj.getId() + "_" + s.getId()).getGrade();
					cols.add(totalMark != null ? totalMark.doubleValue() : "");
					cols.add(grade != null ? grade : "");
					cols.add(point != null ? point.doubleValue() : "");
				}
				else {
					cols.add("");
					cols.add("");
					cols.add("");
				}
			}
			
			if ( resultMap.get(s.getId()) != null ) {
				Double creditHours = (Double) resultMap.get(s.getId()).getCreditHours();
				Double gradePoints = (Double) resultMap.get(s.getId()).getGradePoints();
				Double gpa = (Double) resultMap.get(s.getId()).getGpa();
				Double cgpa = (Double) resultMap.get(s.getId()).getCgpa();
				String status = (String) resultMap.get(s.getId()).getStatus();
				
				cols.add(creditHours != null ? creditHours.doubleValue() : "");
				
				double d1 = gradePoints != null ? gradePoints.doubleValue() : 0.0d;
				cols.add(gradePoints != null ? Math.round(d1 * 100.0) / 100.0 : "");
				double d2 = gpa != null ? gpa.doubleValue() : 0.0d;
				cols.add(gpa != null ? Math.round(d2 * 100.0) / 100.0 : "");
				double d3 = cgpa != null ? cgpa.doubleValue() : 0.0d;
				cols.add(cgpa != null ? Math.round(d3 * 100.0) / 100.0 : "");
				cols.add(status != null ? status : "");
				
			} else {
				cols.add("");
				cols.add("");
				cols.add("");
				cols.add("");
				cols.add("");
			}
			
			rows.add(cols);
		}
		
		ServletOutputStream os = response.getOutputStream();
		
		try {
			x.createXLS(os, headers, headers2, headers3, rows);
		} catch (Exception e) {
			e.printStackTrace();
		}
		os.flush();
		os.close();
		
	}

}
