package educate.questionnare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.poi.Excel1;
import educate.questionnare.entity.TeachingEvaluation;
import lebah.servlets.IServlet;

public class TeachingEvaluationXLServlet implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "teaching_evaluation";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
		
		List<TeachingEvaluation> list = db.list("select t from TeachingEvaluation t order by t.createDate");
	
		List<String> headers = new ArrayList<String>();
		headers.add("Faculty");
		headers.add("Course");
		headers.add("Code");
		headers.add("Semester");
		headers.add("Q1");
		headers.add("Q2");
		headers.add("Q3");
		headers.add("Q4");
		headers.add("Q5");
		headers.add("Q6");
		headers.add("Q7");
		headers.add("Q8");
		headers.add("Q9");
		headers.add("Q10");
		headers.add("Q11");
		headers.add("Q12");
		headers.add("Q13");
		headers.add("Q14");
		
		headers.add("Comment A");
		headers.add("Comment B");
		headers.add("Ovrall");
		
		headers.add("Q15");
		headers.add("Q16");
		headers.add("Q17");
		headers.add("Q18");
		headers.add("Q19");
		headers.add("Q20");
		headers.add("Q21");
		headers.add("Q22");
		headers.add("Q23");
		headers.add("Q24");
		
		headers.add("Comment C");

		List<List> rows =  new ArrayList<List>();
		for ( TeachingEvaluation e : list ) {
			List cols = new ArrayList();
			cols.add(e.getFacultyName());
			cols.add(e.getCourseName());
			cols.add(e.getCodeName());
			cols.add(e.getSemesterYear());
			
			cols.add(e.getQ1());
			cols.add(e.getQ2());
			cols.add(e.getQ3());
			cols.add(e.getQ4());
			cols.add(e.getQ5());
			cols.add(e.getQ6());
			cols.add(e.getQ7());
			cols.add(e.getQ8());
			cols.add(e.getQ9());
			cols.add(e.getQ10());
			
			cols.add(e.getQ11());
			cols.add(e.getQ12());
			cols.add(e.getQ13());
			cols.add(e.getQ14());
			
			
			cols.add(e.getComment1());
			cols.add(e.getComment2());
			cols.add(e.getOverallRating());
			
			cols.add(e.getQ15());
			cols.add(e.getQ16());
			cols.add(e.getQ17());
			cols.add(e.getQ18());
			cols.add(e.getQ19());
			cols.add(e.getQ20());

			cols.add(e.getQ21());
			cols.add(e.getQ22());
			cols.add(e.getQ23());
			cols.add(e.getQ24());
			
			cols.add(e.getComment3());

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
