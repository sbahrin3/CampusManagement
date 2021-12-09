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
import educate.questionnare.entity.TEPartQuestion;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.Teacher;
import lebah.servlets.IServlet;

public class QuestionnareResultXLServlet implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "questionnare_results";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
		
		
		Subject subject = (Subject) request.getSession().getAttribute("subject");
		Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
		
		List<String> textGroup1 = new ArrayList<String>();
		textGroup1.add("");
		textGroup1.add("Student's Evaluation on Teaching and Learning");
		if ( subject != null ) {
			textGroup1.add("Code: " + subject.getCode());
			textGroup1.add("Subject: " + subject.getName());
		}
		
		if ( teacher != null ) {
			textGroup1.add("Lecturer: " + teacher.getName());
		}
		textGroup1.add("");

		List<TEPartQuestion> questionList = (List<TEPartQuestion>) request.getSession().getAttribute("questionList");
		Map<String, AnswerValue> answerValueMap = (Map<String, AnswerValue>) request.getSession().getAttribute("answerValueMap");
		List<List<String>> resultList = (List<List<String>>) request.getSession().getAttribute("resultDataList"); 

		List<String> headers = new ArrayList<String>();
		List<String> identifiers = (List<String>) request.getSession().getAttribute("identifiers");
		List<String> resultHeaders = (List<String>) request.getSession().getAttribute("resultHeaders");
		headers.add("Student");
		int c = 0;
		for ( String t : resultHeaders ) {
			AnswerValue av = answerValueMap.get(identifiers.get(c));
			if ( "number".equals(av.valueType) ) {
				headers.add(t);
			}
			c++;
		}
		
	
		List<List> rows =  new ArrayList<List>();
		int cnt = 0;
		for ( List<String> data : resultList ) {
			List<Object> cols = new ArrayList<Object>();
			cnt++;
			int i = 0;
			for ( String txt : data ) {
				if ( i == 0 ) {
					cols.add("Student " + cnt);
				} else {
					
					AnswerValue av = answerValueMap.get(identifiers.get(i-1));
					if ( "number".equals(av.valueType) ) {
						if ( isDouble(txt)) {
							cols.add(round(Double.parseDouble(txt)));
						}
						else cols.add(txt);
					}
				
				}
				
				i++;
			}
			rows.add(cols);
		}
		
		List<Double> averages = (List<Double>) request.getSession().getAttribute("averages");
		List<Object> cols = new ArrayList<Object>();
		cols.add("Avg.");
		c = 0;
		for ( Double avg : averages ) {
			AnswerValue av = answerValueMap.get(identifiers.get(c));
			if ( "number".equals(av.valueType) ) {
				cols.add(round(avg));
			}
			c++;
		}
		rows.add(cols);
		
		
		List<String> textGroup2 = new ArrayList<String>();
		textGroup2.add(null);
		textGroup2.add(null);
		
		
		//rows2
		List<List> rows2 = new ArrayList<List>();
		
		for ( TEPartQuestion partQuestion : questionList ) {
			cols = new ArrayList<Object>();
			AnswerValue av = answerValueMap.get(partQuestion.getId());
			if ( "number".equals(av.valueType) ) {
				cols.add(partQuestion.getRefNo() + " - " + partQuestion.getQuestion().getQuestionText());
				for ( int i=0; i<8; i++ ) cols.add(null);
				cols.add(round(av.average));
				rows2.add(cols);
			}
			
		}
		
		ServletOutputStream os = response.getOutputStream();
		Excel1 x = new Excel1();
		try {
			x.createXLS(os, headers, null, null, textGroup1, textGroup2, rows, rows2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		os.flush();
		os.close();
		
	}
	
	
	public boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	
	public static boolean isDouble(String s) {
	    try { 
	        Double.parseDouble(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}

	private static double asDouble(String s) {
		double d = 0.0d;
		try {
			d = Double.parseDouble(s);
		} catch ( Exception e ) {}
		return d;
	}
	
	private static double round(double value, int precision) {
	    int scale = (int) Math.pow(10, precision);
	    return (double) Math.round(value * scale) / scale;
	}
	
	private static double round(double value) {
		return round(value, 1);
	}
	
}

