/**
 * 
 */
package educate.questionnare;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;

import educate.db.DbPersistence;
import educate.questionnare.entity.TEPart;
import educate.questionnare.entity.TEPartQuestion;
import educate.questionnare.entity.TEQuestion;
import educate.questionnare.entity.TEQuestionnare;
import educate.questionnare.entity.TESet;
import educate.questionnare.entity.TEUserLog;
import educate.questionnare.entity.TEUserPath;
import educate.sis.module.StudentStatusUtil;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.Teacher;
import lebah.portal.velocity.VTemplate;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class PrintReportPDFModule extends VTemplate {
	
	String path = "questionnare/publish";
	DbPersistence db = new DbPersistence();
	
	public Template doTemplate() throws Exception {
		setShowVM(false);
		
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
        String http = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("://") + 3);
        String serverUrl = http + server;
        context.put("serverUrl", serverUrl);
        String uri = request.getRequestURI();
        String appname = uri.substring(1);
        appname = appname.substring(0, appname.indexOf("/"));
        context.put("appUrl", serverUrl.concat("/").concat(appname));     
        
		context.put("today", new Date());
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("df", new SimpleDateFormat("dd MMM, yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.put("util", new lebah.util.Util());
		Template template = engine.getTemplate(viewReport());	
		return template;		
	}

	
	
	public String viewReport() throws Exception {
		
		String questionnareId = getParam("questionnareId");
		TEQuestionnare q =  db.find(TEQuestionnare.class, questionnareId);
		context.put("questionnare", q);
		
		Subject subject = q.getSubject();
		Teacher teacher = q.getTeacher();
		
		context.put("subject", subject);
		context.put("teacher", teacher);
		
		//list of answers type essay
		List<String> comments = new ArrayList<String>();
		context.put("comments", comments);
		
		List<String> respondentIds = new ArrayList<String>();
		Map<String, List<String>> respondentResults = new HashMap<String, List<String>>(); 
		
		TESet set = q.getSet();
		List<TEPart> parts = set.getParts();
		List<TEUserLog> userLogs = db.list("select u from TEUserLog u where u.questionnare.id = '" + questionnareId + "'");
		
		List<TEPartQuestion> questionList = new ArrayList<TEPartQuestion>();
		context.put("questionList", questionList);
		Map<Integer, String> questionTypes = new HashMap<Integer, String>();
		context.put("questionTypes", questionTypes);
		int counter = 0;
		for ( TEPart p : parts ) {
			List<TEPartQuestion> partQuestions = p.getQuestions();
			for ( TEPartQuestion pq : partQuestions ) {
				questionList.add(pq);
				counter++;
				questionTypes.put(counter, pq.getQuestion().getType());
			}
		}
		
		for ( TEUserLog userLog : userLogs ) {
			
			if ( userLog.getComplete() ) {
			
				List<String> results = new ArrayList<String>();
	
				respondentIds.add(userLog.getUserId());
				results.add(userLog.getUserId());
				
				Map<String, TEUserPath> answerMap = new HashMap<String, TEUserPath>();
				List<TEUserPath> userPaths = userLog.getUserPaths();
				for ( TEUserPath up : userPaths ) {
					if ( up.getQuestion() != null ) answerMap.put(up.getQuestion().getId(), up);
				}
				
				
				for ( TEPart p : parts ) { 
					List<TEPartQuestion> partQuestions = p.getQuestions();
					for ( TEPartQuestion pq : partQuestions ) {
						TEUserPath answer = answerMap.get(pq.getId());
						TEQuestion question = pq.getQuestion();
	
						if ( answer != null ) {
							//System.out.println("question type = " + question.getType());
							
							if ( "likert".equals(question.getType())) {
								results.add(answer != null ? Integer.toString(answer.getLikertValue()) : "null");
							} else if ( "single".equals(question.getType()) ) {
								if ( answer.getChoice1() == 1) results.add("1");
								if ( answer.getChoice2() == 1) results.add("2");
								if ( answer.getChoice3() == 1) results.add("3");
								if ( answer.getChoice4() == 1) results.add("4");
								if ( answer.getChoice5() == 1) results.add("5");
							} else if ( "multiple".equals(question.getType()) ) {
								String txt = "";
								if ( answer.getChoice1() == 1) { txt = "1"; }
								if ( answer.getChoice2() == 1) { if ( !"".equals(txt) ) txt = txt + ", 2"; else txt = "2"; }
								if ( answer.getChoice3() == 1) { if ( !"".equals(txt) ) txt = txt + ", 3"; else txt = "3"; }
								if ( answer.getChoice4() == 1) { if ( !"".equals(txt) ) txt = txt + ", 4"; else txt = "4"; }
								if ( answer.getChoice5() == 1) { if ( !"".equals(txt) ) txt = txt + ", 5"; else txt = "5"; }
								results.add(txt);
							} else if ( "text".equals(question.getType())) {
								results.add(answer != null ? answer.getTextAnswer() : "");
								//comments type
								comments.add(answer.getTextAnswer());
								System.out.println(answer.getTextAnswer());
							} else if ( "essay".equals(question.getType())) {
								results.add(answer != null ? answer.getEssayAnswer() : "");		
								//comments type
								comments.add(answer.getEssayAnswer());
								System.out.println(answer.getEssayAnswer());
							} else if ( "date".equals(question.getType())){
								Date date = answer.getDateInput();
								if ( date != null ) {
									results.add(new SimpleDateFormat("dd-MM-yyyy").format(date));
								} else {
									results.add(null);
								}
							} else {
								
							}
						
						}
						else {
							//answer is null
						}
					}
				}
				respondentResults.put(userLog.getUserId(), results);
			
			}
		}
		
		List<String> questionRefs = new ArrayList<String>();
		context.put("questionRefs", questionRefs);
		context.put("respondentIds", respondentIds);

		List<List<String>> resultList = new ArrayList<List<String>>();
		context.put("resultList", resultList);
		
		
		for ( TEPart p : parts ) {
			List<TEPartQuestion> partQuestions = p.getQuestions();
			for ( TEPartQuestion pq : partQuestions ) {
				questionRefs.add(pq.getRefNo());
			}
		}
		for (String s : respondentIds ) {
			if ( !"none".equals(s)) {
				List<String> results = respondentResults.get(s);
				if ( results != null ) {
					resultList.add(results);
				}
			}
		}
		
		//prepare question list with average sections
		List<String> resultHeaders = new ArrayList<String>();
		context.put("resultHeaders", resultHeaders);
		String pId = questionList.get(0).getPart().getId();
		for ( TEPartQuestion tq : questionList ) {
			
			if ( pId.equals(tq.getPart().getId())) {
				resultHeaders.add(tq.getRefNo());
			} else {
				
				resultHeaders.add("Avg.");
				pId = tq.getPart().getId();
				resultHeaders.add(tq.getRefNo());
			}
		}
		resultHeaders.add("Avg.");
		
		List<String> answerTypes = new ArrayList<String>();
		List<String> answerIdentifiers = new ArrayList<String>();
		context.put("identifiers", answerIdentifiers);
		String tempHeaderId = questionList.get(0).getPart().getId();
		String headerId = "", prevHeaderId = tempHeaderId;
		for ( TEPartQuestion pq : questionList ) {
			headerId = pq.getPart().getId();
			if ( !headerId.equals(tempHeaderId)) {
				answerIdentifiers.add("avg_" + prevHeaderId);
				answerTypes.add("avg");
				tempHeaderId = headerId;
			}
			prevHeaderId = headerId;
			answerIdentifiers.add(pq.getId());
			answerTypes.add(pq.getQuestion().getType());
		}
		answerIdentifiers.add("avg_" + prevHeaderId);
		answerTypes.add("avg");
		//
		/*
		int c = 0;
		for ( String s : answerIdentifiers ) {
			c++;
			System.out.println(c + ") " + s);
		}
		*/
		//analyze result for every students
		
		DecimalFormat f = new DecimalFormat("#.0");
		List<List<String>> resultDataList = new ArrayList<List<String>>();
		int cnt = 0;
		for ( List<String> rs : resultList ) {
			cnt++;
			List<String> resultData = new ArrayList<String>();
			//List<String> rs = resultList.get(1);
			int i = -1; //because the first is student id
			int sum = 0, n = 0;
			double avg = 0.0d;
			String tempId = questionList.get(0).getPart().getId();
			for ( String r : rs ) {
				if ( i < 0 ) { //matric number
					resultData.add(r); 
				}
				else {
					TEPartQuestion tq = questionList.get(i);
					if ( tempId.equals(tq.getPart().getId())) {
						n++;
						int ri = asPositiveInt(r);
						if ( ri > -1 ) {
							sum = sum + ri;
							resultData.add(r);
						}
						else {
							resultData.add("");
						}
						
					} else {
						
						//calc prev. avg
						if ( n > 0 ) {
							avg = (double) sum/ (double) n;
							sum = 0;
							if ( avg > 0 ) 
								resultData.add(f.format(avg));
							else
								resultData.add("");
							
							
						}
						
						tempId = tq.getPart().getId();
						n = 1;
						int ri = asPositiveInt(r);
						if ( ri > -1 ) {
							sum = ri;
							resultData.add(r);
						} else {
							resultData.add("");
						}
					}
				}
				i++;
			}
			//calc prev. avg
			if ( n > 0 ) {
				avg = (double) sum/ (double) n;
				if ( avg > 0 )
					resultData.add(f.format(avg));
				else
					resultData.add("");
			}
			
			resultDataList.add(resultData);
		
		}
		
		//remove empty answers
		int rows = 0;
		int cols = 0;
		List<List<String>> resultDataList2 = new ArrayList<List<String>>();
		context.put("resultDataList", resultDataList2);
		for ( List<String> answers : resultDataList ) {
			int i = 0;
			for ( String s : answers ) {
				if ( s != null ) {
					i++;
				}
			}
			if ( i > 1 ) {
				cols = i;
				resultDataList2.add(answers);
				rows++;
			}
		}
		
		//calc total average
		
		cols = cols - 1;
		double[][] ans = new double[cols][rows];
		int y = 0;
		for ( List<String> answers : resultDataList2 ) {
			int x = 0;
			for ( String s : answers ) {
				if ( x > 0 ) {
					ans[x-1][y] = asDouble(s);
				}
				x++;
			}
			y++;
		}
		
		//cal total average
		Map<String, AnswerValue> answerValueMap = new HashMap<String, AnswerValue>();
		context.put("answerValueMap", answerValueMap);
		
		List<Double> averages = new ArrayList<Double>();
		context.put("averages", averages);
		double rowCount = (double) rows;
		String avgType = "", prevType = "";
		boolean hasNumber = false;
		for ( int col=0; col < cols; col++ ) {
			double total = 0;
			for ( int row=0; row < rows; row++ ) {
				total += ans[col][row];
			}
			double avg = total/rowCount;
			averages.add(new Double(avg));
			
			String identifier = answerIdentifiers.get(col);
			String type = answerTypes.get(col);
			String valueType = "";
			if ( "likert".equals(type) || "single".equals(type) || "multiple".equals(type)) {
				valueType = "number";
				hasNumber = true;
			} else if ( "avg".equals(type)) {
				valueType = hasNumber ? "number" : "text";
				hasNumber = false;
			} else {
				valueType = "text";
			}
			
			AnswerValue av = new AnswerValue();
			av.questionId = identifier;
			av.average = avg;
			av.type = type;
			av.valueType = valueType;
			
			answerValueMap.put(identifier, av);
			
		}
		
		/*
		 * likert
		 * single
		 * multiple
		 * text
		 * essay
		 * date
		 */
		
		//check wether to display numeric table or not
		/*
		for ( String identifier : answerIdentifiers ) {
			AnswerValue av = answerValueMap.get(identifier);
			System.out.println(av.questionId + " - " + av.valueType);
		}
		*/
		/*
		request.getSession().setAttribute("questionTypes", questionTypes);
		request.getSession().setAttribute("questionList", questionList);
		request.getSession().setAttribute("resultList", resultList);
		request.getSession().setAttribute("averages", averages);
		request.getSession().setAttribute("answerValueMap", answerValueMap);
		request.getSession().setAttribute("identifiers", answerIdentifiers);
		
		request.getSession().setAttribute("resultHeaders", resultHeaders);
		request.getSession().setAttribute("resultDataList", resultDataList2);
		
		request.getSession().setAttribute("subject", subject);
		request.getSession().setAttribute("teacher", teacher);
		
		request.getSession().setAttribute("comments", comments);
		*/
		
		context.put("currentSession", null);
		Session intake = q.getIntake();
		int pathNo = 0;
		Program p = q.getProgram();
		if ( p != null ) 
			pathNo = p.getLevel().getPathNo();
			
			
		Date d1 = q.getStartDate();
		Date d2 = q.getEndDate();
		StudentStatusUtil u = new StudentStatusUtil();
		
		Session currentSession = u.getSessionByDate(d1, pathNo);
		context.put("currentSession", currentSession);

		return path + "/print_template.vm";
	}

	private int asPositiveInt(String s) {
		int i = -1;
		try {
			i = Integer.parseInt(s);
		} catch ( Exception e ) {}
		return i;
	}
	
	private double asDouble(String s) {
		double d = 0.0d;
		try {
			d = Double.parseDouble(s);
		} catch ( Exception e ) {}
		return d;
	}
}
