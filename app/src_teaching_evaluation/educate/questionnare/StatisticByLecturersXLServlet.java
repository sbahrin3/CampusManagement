package educate.questionnare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.poi.Excel1;
import educate.questionnare.entity.TEQuestion;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.Teacher;
import lebah.servlets.IServlet;

public class StatisticByLecturersXLServlet implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/x-msdownload");
		
		Subject subject = (Subject) request.getSession().getAttribute("subject");
		String filename = "statistic_lecturers";
		if ( subject != null || !subject.equals("")) filename += "_" + subject.getCode();
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
		
		
		List<String> refNos = (List<String>) request.getSession().getAttribute("refNos");
		List<Teacher> teachers = (List<Teacher>) request.getSession().getAttribute("teachers");
		Map<String, TEQuestion> questionMap = (Map<String, TEQuestion>) request.getSession().getAttribute("questionMap");
		Map<String, Stat> statMap = (Map<String, Stat>) request.getSession().getAttribute("statMap");
		
	
		List<String> headers = new ArrayList<String>();
		headers.add("#");
		headers.add("Item");
		for ( Teacher t : teachers ) {
			headers.add(t.getCode() + " " + t.getName());
			headers.add("");
			headers.add("");
			headers.add("");
		}
		
		List<List> rows =  new ArrayList<List>();
		List cols = new ArrayList();
		cols.add("");
		cols.add("");
		for ( Teacher t : teachers ) {
			cols.add("Mean");
			cols.add("Median");
			cols.add("SD");
			cols.add("n");
		}
		rows.add(cols);
		
		for ( String refNo : refNos ) {
			
			TEQuestion question = (TEQuestion) questionMap.get(refNo);
			
			cols = new ArrayList();
			cols.add(refNo);
			cols.add(question.questionText);
			
			for ( Teacher t : teachers ) {
				Stat stat = statMap.get(t.getId() + refNo);
				cols.add(stat.getMean());
				cols.add(stat.getMedian());
				cols.add(stat.getSd());
				cols.add(stat.getSize());
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
