package educate.admission.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.enrollment.entity.StudentStatus;
import educate.poi.Excel1;
import educate.sis.struct.entity.Subject;
import lebah.servlets.IServlet;

public class SubjectRegistrationXLServlet implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "subjects_registration";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
		
		List<StudentStatus> students = (List<StudentStatus>) request.getSession().getAttribute("_students");
		List<Subject> subjectList = (List<Subject>) request.getSession().getAttribute("_subjects");
		Map<String, String> subjectMap = (Map<String, String>) request.getSession().getAttribute("_subjectMap");
	
		List<String> formats = new ArrayList<String>();
		List<String> headers = new ArrayList<String>();
		headers.add("Matric");
		headers.add("Name");
		//headers.add("ICNo/Passport");
		
		for ( Subject subject : subjectList ) {
			headers.add(subject.getCode()); // + "-" + subject.getName());
			formats.add("center");
		}
		
		List<List> rows =  new ArrayList<List>();
		
		for ( StudentStatus s : students ) {
			
			List cols = new ArrayList();
			cols.add(s.getStudent().getMatricNo());
			cols.add(s.getStudent().getBiodata().getName());
			//String no =  s.getStudent().getBiodata().getIcno() != null && !"".equals(s.getStudent().getBiodata().getIcno()) ? s.getStudent().getBiodata().getIcno() : s.getStudent().getBiodata().getPassport();
			//cols.add(no);
			
			for ( Subject subject : subjectList ) {
				if (subjectMap.get(subject.getId() + "_" + s.getStudent().getId()) != null ) {
					String str = subjectMap.get(subject.getId() + "_" + s.getStudent().getId());
					if ( !"".equals(str)) cols.add(1);
					
				}
				else {
					cols.add("");
				}
			}
			rows.add(cols);
		}
		
		
		
	
		ServletOutputStream os = response.getOutputStream();
		Excel1 x = new Excel1();
		try {
			x.createXLS(os, headers, rows);
		} catch (Exception e) {
			e.printStackTrace();
		}
		os.flush();
		os.close();
		
	}

}
