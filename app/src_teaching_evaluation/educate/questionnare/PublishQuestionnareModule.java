package educate.questionnare;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class PublishQuestionnareModule extends LebahModule {
	
	protected DbPersistence db = new DbPersistence();
	protected String path = "questionnare/publish";
	
	public void preProcess() {
		System.out.println(command);
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("numFormat", new DecimalFormat("#.0"));
		context.remove("edit");
		context.remove("reportView");
	}

	@Override
	public String start() {
		context.put("programs", db.list("select p from Program p order by p.code"));
		context.put("sets",db.list("select s from TESet s order by s.createDate"));
		
		//listQuestionnares();
		return path + "/start.vm";
	}

	private void listQuestionnares() {
		//String userId = (String) request.getSession().getAttribute("_portal_login");
		//List<TEQuestionnare> questionnares = db.list("select q from TEQuestionnare q where q.userId = '" + userId + "' order by q.createDate, q.startDate");
		
		String listStatus = getParam("list_status");
		if ( "".equals(listStatus) ) {
			listStatus = request.getSession().getAttribute("list_status") != null ? (String) request.getSession().getAttribute("list_status") : "all"; 
		}
		request.getSession().setAttribute("list_status", listStatus);
		context.put("listStatus", listStatus);
		
		String filterProgram = getParam("filterProgram");
		String filterIntake = getParam("filterIntake");
		String filterSubject = getParam("filterSubject");
		String filterSet = getParam("filterSet");
		
		Program program = null;
		Session intake = null;
		Subject subject = null;
		TESet set = null;
		
		if ( !"".equals(filterProgram) ) program = db.find(Program.class, filterProgram);
		if ( !"".equals(filterIntake) ) intake = db.find(Session.class, filterIntake);
		if ( !"".equals(filterSubject) ) subject = db.find(Subject.class, filterSubject);
		if ( !"".equals(filterSet) ) set = db.find(TESet.class, filterSet); 
		
		context.put("program", program);
		context.put("intake", intake);
		context.put("subject", subject);
		context.put("set", set);
		
		Hashtable h = new Hashtable();
		
		List<TEQuestionnare> questionnares = null;
		String sql1 = "select q from TEQuestionnare q ";
		
		String sql2 = "";
		if ( !"".equals(filterProgram) ) sql2 = sql2 + ("".equals(sql2) ? " where " : "and ") + " q.program.id = '" + filterProgram + "' ";
		if ( !"".equals(filterIntake) ) sql2 = sql2 + ("".equals(sql2) ? " where " : "and ") + " q.intake.id = '" + filterIntake + "' ";
		if ( !"".equals(filterSubject) ) sql2 = sql2 + ("".equals(sql2) ? " where " : "and ") + " q.subject.id = '" + filterSubject + "' ";
		if ( !"".equals(filterSet) ) sql2 = sql2 + ("".equals(sql2) ? " where " : "and ") + " q.set.id = '" + filterSet + "' ";

		if ( "all".equals(listStatus)) {
			sql2 = sql2 + " order by q.publishedId, q.createDate, q.startDate";
			String sql = sql1 + sql2;
			questionnares = db.list(sql, h);
		}
		else if ( "open".equals(listStatus)) {
			h.put("date", new Date());
			sql2 = sql2 + ("".equals(sql2) ? " where " : "and ") + " :date between q.startDate and q.endDate order by q.publishedId, q.createDate, q.startDate";
			String sql = sql1 + sql2;
			questionnares = db.list(sql, h);
		}
		else if ( "closed".equals(listStatus)) {
			h.put("date", new Date());
			sql2 = sql2 + ("".equals(sql2) ? " where " : "and ") + "  q.endDate < :date order by q.publishedId, q.createDate, q.startDate";
			String sql = sql1 + sql2;
			questionnares = db.list(sql, h);
		}
		
		context.put("questionnares", questionnares);
	}
	
	@Command("listQuestionnares")
	public String showQuestionnares() throws Exception {
		listQuestionnares();
		return path + "/questionnares.vm";
	}
	
	@Command("newQuestionnare")
	public String newQuestionnare() throws Exception {
		context.remove("q");
		List<TESet> sets = db.list("select s from TESet s order by s.createDate");
		context.put("sets", sets);
		context.put("programs", db.list("select p from Program p order by p.code"));
		context.put("subjects", db.list("select distinct s from TeacherSubject t join t.subject s order by s.code"));
		
		return path + "/newQuestionnare.vm";
	}
	
	@Command("listIntakes")
	public String listIntakes() throws Exception {
		String programId = getParam("programId");
		context.put("programId", programId);
		Program program = db.find(Program.class, programId);
		if ( program.getLevel() == null ) System.out.println("WARNING: Program " + program.getCode() + " " + program.getName() + " has NO LEVEL!");
		String sql = "select distinct i from StudentStatus ss join ss.student s join s.intake i where ss.type.inActive = 0 and s.program.id = '" + programId + "'  order by i.startDate";
		List<Session> intakes = db.list(sql);
		context.put("intakes", intakes);
		

		return path + "/listIntakes.vm";
	}
	
	@Command("listFilterIntakes")
	public String listFilterIntakes() throws Exception {
		String programId = getParam("filterProgram");
		context.put("filterProgram", programId);
		Program program = db.find(Program.class, programId);
		if ( program != null ) {
			String sql = "select distinct i from StudentStatus ss join ss.student s join s.intake i where ss.type.inActive = 0 and s.program.id = '" + programId + "'  order by i.startDate";
			List<Session> intakes = db.list(sql);
			context.put("intakes", intakes);
		} else
			context.remove("intakes");
		

		return path + "/divFilterIntake.vm";
	}	
	
	@Command("listFilterSubjects")
	public String listFilterSubjects() throws Exception {
		String programId = getParam("filterProgram");
		Program program = db.find(Program.class, programId);
		if ( program != null ) {
			if ( program.getLevel() == null ) System.out.println("WARNING: Program " + program.getCode() + " " + program.getName() + " has NO LEVEL!");
			String sql = "select distinct s from SubjectPeriod sp join sp.subjectRegs sr join sr.subject s where sp.programStructure.program.id = '" + programId + "' order by s.code";
			context.put("subjects", db.list(sql));	
		}
		else {
			context.remove("subjects");
		}

		return path + "/divFilterSubject.vm";
	}		
	
	@Command("listSubjects")
	public String listSubjects() throws Exception {
		String programId = getParam("programId");
		context.put("programId", programId);
		Program program = db.find(Program.class, programId);
		if ( program.getLevel() == null ) System.out.println("WARNING: Program " + program.getCode() + " " + program.getName() + " has NO LEVEL!");
		
		String sql = "select distinct s from SubjectPeriod sp join sp.subjectRegs sr join sr.subject s where sp.programStructure.program.id = '" + programId + "' order by s.code";
		context.put("subjects", db.list(sql));		

		return path + "/listSubjects.vm";
	}	
	
	
	@Command("listSessions")
	public String listSessions() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
		String sql = "select s from Session s where s.pathNo = " + pathNo + " order by s.startDate";
		List<Session> sessions = db.list(sql);
		context.put("sessions", sessions);
		return path + "/listSessions.vm";
	}
	
	@Command("editQuestionnare")
	public String editQuestionnare() throws Exception {
		TEQuestionnare questionnare = db.find(TEQuestionnare.class, getParam("questionnareId"));
		context.put("q", questionnare);
		List<TESet> sets = db.list("select s from TESet s order by s.createDate");
		context.put("sets", sets);
		context.put("programs", db.list("select p from Program p order by p.code"));
		context.put("subjects", db.list("select distinct s from TeacherSubject t join t.subject s order by s.code"));
		
		if ( questionnare.getProgram() != null ) {
			String programId = questionnare.getProgram().getId();
			Program program = db.find(Program.class, programId);
			if ( program.getLevel() == null ) System.out.println("WARNING: Program " + program.getCode() + " " + program.getName() + " has NO LEVEL!");
			String sql = "select distinct i from StudentStatus ss join ss.student s join s.intake i where ss.type.inActive = 0 and s.program.id = '" + programId + "'  order by i.startDate";
			List<Session> intakes = db.list(sql);
			context.put("intakes", intakes);
		} else {
			context.remove("intakes");
		}
		
		context.put("edit", true);
		
		return path + "/newQuestionnare.vm";
	}
	
	
	
	@Command("saveQuestionnare")
	public String saveQuestionnare() throws Exception {
		
		Program program = null;
		Session intake = null;
		Subject subject = null;
		TESet set = null;
		
		if ( !"".equals(getParam("programId"))) program = db.find(Program.class, getParam("programId"));
		if ( !"".equals(getParam("intakeId"))) intake = db.find(Session.class, getParam("intakeId"));
		if ( !"".equals(getParam("setId"))) set = db.find(TESet.class, getParam("setId"));
		if ( !"".equals(getParam("subjectId"))) subject = db.find(Subject.class, getParam("subjectId"));
		
		String questionnareId = getParam("questionnareId");
		TEQuestionnare quest = null;
		if ( !"".equals(questionnareId)) {
			quest = db.find(TEQuestionnare.class, questionnareId);
		}
		
		String publishedId = lebah.db.UniqueID.getUID();
		
		if ( subject != null ) {
			List<Teacher> teachers = db.list("select t from TeacherSubject ts join ts.teacher t where ts.subject.id = '" + subject.getId() + "'");
			if ( teachers.size() > 0 ) {
				
				if ( quest == null ) {
					for ( Teacher t : teachers ) {
						createQuestionnare(null, publishedId, program, intake, subject, t, set);
					}
				}
				else
					createQuestionnare(quest, publishedId, program, intake, subject, null, set);
			}
			else
				createQuestionnare(quest, publishedId, program, intake, subject, null, set);
		}
		else {
			createQuestionnare(quest, publishedId, program, intake, subject, null, set);
		}
		
		listQuestionnares();
		return path + "/questionnares.vm";
	}
	
	private void createQuestionnare(TEQuestionnare quest, String publishedId, Program program, Session intake, Subject subject, Teacher teacher, TESet set) throws Exception {
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = new SimpleDateFormat("dd-MM-yyyy").parse(getParam("startDate"));
		} catch ( Exception e ) { }
		try {
			endDate = new SimpleDateFormat("dd-MM-yyyy").parse(getParam("endDate"));
		} catch ( Exception e ) { }

		
		boolean createNew = false;
		if ( quest == null ) {
			createNew = true;
		}
		if ( createNew ) {
			quest = new TEQuestionnare();
			quest.setCreateDate(new Date());
			quest.setUserId((String) request.getSession().getAttribute("_portal_login"));
			
			quest.setAudienceType(getParam("audienceType"));
			quest.setSet(set);
			quest.setProgram(program);
			quest.setIntake(intake);
			quest.setSubject(subject);
			
			if ( teacher != null ) {
				quest.setTeacher(teacher);
			}

		}
		
		quest.setStartDate(startDate);
		quest.setEndDate(endDate);
		
		db.begin();
		if ( createNew ) {
			quest.setPublishedId(publishedId);
			db.persist(quest);
		}
		db.commit();
	}
	

	@Command("deleteQuestionnare")
	public String deleteQuestionnare() throws Exception {
		
		String questionnareId = getParam("questionnareId");
		TEQuestionnare quest =  db.find(TEQuestionnare.class, questionnareId);
		
		String publishedId = quest.getPublishedId();
		
		String sql = "delete from TEQuestionnare t where t.publishedId = '" + publishedId + "'";
		db.begin();
		db.executeUpdate(sql);
		db.commit();
		
		listQuestionnares();
		return path + "/questionnares.vm";
		
	}
	
	
	
	@Command("testRun")
	public String testRun() throws Exception {
		String setId = getParam("setId");
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);
		context.put("questionnareId", getParam("questionnareId"));
		context.put("userId", (String) request.getSession().getAttribute("_portal_login"));
		return path + "/testRun.vm";
	}
	
	@Command("viewReport_bk")
	public String viewReport_bk() throws Exception {
		
		String questionnareId = getParam("questionnareId");
		TEQuestionnare q =  db.find(TEQuestionnare.class, questionnareId);
		context.put("questionnare", q);
		
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

				
					if ( "likert".equals(question.getType())) {
						results.add(answer != null ? answer.getLikertValue() + "": "");
					} else if ( "single".equals(question.getType()) ) {
						if ( answer != null ) {
							if ( answer.getChoice1() == 1) results.add("1");
							if ( answer.getChoice2() == 1) results.add("2");
							if ( answer.getChoice3() == 1) results.add("3");
							if ( answer.getChoice4() == 1) results.add("4");
							if ( answer.getChoice5() == 1) results.add("5");
						} else
							results.add("-");
					} else if ( "multiple".equals(question.getType()) ) {
						if ( answer != null ) {
							
							String txt = "";
							if ( answer.getChoice1() == 1) { txt = "1"; }
							if ( answer.getChoice2() == 1) { if ( !"".equals(txt) ) txt = txt + ", 2"; else txt = "2"; }
							if ( answer.getChoice3() == 1) { if ( !"".equals(txt) ) txt = txt + ", 3"; else txt = "3"; }
							if ( answer.getChoice4() == 1) { if ( !"".equals(txt) ) txt = txt + ", 4"; else txt = "4"; }
							if ( answer.getChoice5() == 1) { if ( !"".equals(txt) ) txt = txt + ", 5"; else txt = "5"; }

							results.add(txt);
						} else
							results.add("-");
					} else if ( "text".equals(question.getType())) {
						results.add(answer != null ? answer.getTextAnswer() : "");
					} else if ( "essay".equals(question.getType())) {
						results.add(answer != null ? answer.getEssayAnswer() : "");					
					} else if ( "date".equals(question.getType())){
						Date date = answer.getDateInput();
						if ( date != null ) {
							results.add(new SimpleDateFormat("dd-MM-yyyy").format(date));
						} else {
							results.add(null);
						}
					}
				}
			}
			
			respondentResults.put(userLog.getUserId(), results);
			
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
		
		
		
		
		request.getSession().setAttribute("questionTypes", questionTypes);
		request.getSession().setAttribute("questionList", questionList);
		request.getSession().setAttribute("resultList", resultList);
		
		return path + "/viewReport.vm";
	}
	
	@Command("viewReport")
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
		
		System.out.println("userLogs = " + userLogs.size());
		
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
			
			System.out.println("complete = " + userLog.getComplete());
			
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
							} else if ( "essay".equals(question.getType())) {
								results.add(answer != null ? answer.getEssayAnswer() : "");		
								//comments type
								comments.add(answer.getEssayAnswer());
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
							System.out.println("answer is null.");
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
		
		System.out.println("cols = " + cols);
		System.out.println("rows = " + rows);
		
		cols = cols > 0 ? cols - 1 : 0;
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
		
		return path + "/viewReport2.vm";
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
	
	@Command("viewResult")
	public String viewResult() throws Exception {
		
		String questionnareId = getParam("questionnareId");
		TEQuestionnare q =  db.find(TEQuestionnare.class, questionnareId);
		context.put("questionnare", q);
		
		Subject subject = q.getSubject();
		Teacher teacher = q.getTeacher();
		
		context.put("subject", subject);
		context.put("teacher", teacher);
		
		TESet set = q.getSet();
		List<TEPart> parts = set.getParts();
		List<TEUserLog> userLogs = db.list("select u from TEUserLog u where u.questionnare.id = '" + questionnareId + "'");
		
		System.out.println("userLogs = " + userLogs.size());
		context.put("userLogs", userLogs);
		
		Map<String, Map> userAnswers = new HashMap<String, Map>();
		context.put("userAnswers", userAnswers);
		
		Map<String, Map> userResults = new HashMap<String, Map>();
		context.put("userResults", userResults);
		
		for ( TEUserLog userLog : userLogs ) {
			
			Map<String, Object> answer = new HashMap<String, Object>();
			userAnswers.put(userLog.getUserId(), answer);
			
			Map<String, Boolean> result = new HashMap<String, Boolean>();
			userResults.put(userLog.getUserId(), result);
			
			int correctCount = 0;
			int totalCount = 0;
			for ( TEUserPath up : userLog.getUserPaths()) {
				TEPartQuestion pq = up.getQuestion();
				
				if ( pq.getQuestion().getType().equals("likert")) {
					answer.put(pq.getRefNo(), up.getLikertValue());
					result.put(pq.getRefNo(), true);
					
				} else if ( pq.getQuestion().getType().equals("single")) {
					
					
					answer.put(pq.getRefNo(), up.getSingleChoice());
					result.put(pq.getRefNo(), pq.getQuestion().getCorrectSingleChoice() == up.getSingleChoice());
				
					totalCount++;
					if ( pq.getQuestion().getCorrectSingleChoice() == up.getSingleChoice() ) {
						correctCount++;
					}
				} else if ( pq.getQuestion().getType().equals("multiple")) {
					List<Integer> multipleAnswer = new ArrayList<Integer>();
					multipleAnswer.add(up.getChoice1());
					multipleAnswer.add(up.getChoice2());
					multipleAnswer.add(up.getChoice3());
					multipleAnswer.add(up.getChoice4());
					multipleAnswer.add(up.getChoice5());
					answer.put(pq.getRefNo(), multipleAnswer);
					
					boolean correct = true;
					if ( pq.getQuestion().getCorrect1() && up.getChoice1() == 0 ) {
						correct = false;
					}
					if ( correct && pq.getQuestion().getCorrect2() && up.getChoice2() == 0 ) {
						correct = false;
					}
					if ( correct && pq.getQuestion().getCorrect3() && up.getChoice3() == 0 ) {
						correct = false;
					}
					if ( correct && pq.getQuestion().getCorrect4() && up.getChoice4() == 0 ) {
						correct = false;
					}
					if ( correct && pq.getQuestion().getCorrect5() && up.getChoice5() == 0 ) {
						correct = false;
					}
					
					result.put(pq.getRefNo(), correct);
					
					
					totalCount++;
					if ( correct ) {
						correctCount++;
					}
					
				} else if ( pq.getQuestion().getType().equals("text")) {
					answer.put(pq.getRefNo(), up.getTextAnswer());
				} else if ( pq.getQuestion().getType().equals("essay")) {
					answer.put(pq.getRefNo(), up.getEssayAnswer());
				} else if ( pq.getQuestion().getType().equals("date")) {
					answer.put(pq.getRefNo(), up.getDateInput());
				}
				
				answer.put("total_count", totalCount);
				answer.put("correct_count", correctCount);
				answer.put("wrong_count", totalCount - correctCount);
				
				double pct = round(((double) correctCount / (double) totalCount) * 100, 2);
				answer.put("percentage", pct);
			}
			
		}
		
		
		
		return path + "/viewResult.vm";
	}
	
	@Command("getTranscript")
	public String getTranscript() throws Exception {
		String userLogId = getParam("userLogId");
		TEUserLog userLog = db.find(TEUserLog.class, userLogId);
		context.put("userLog", userLog);
		
		TEQuestionnare questionnare = userLog.getQuestionnare();
		TESet set = questionnare.getSet();
		context.put("set", set);
		

		
		Map<String, Object> answer = new HashMap<String, Object>();
		context.put("answer", answer);
		
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		context.put("result", result);
		
		int correctCount = 0;
		int totalCount = 0;
		for ( TEUserPath up : userLog.getUserPaths()) {
			TEPartQuestion pq = up.getQuestion();
			
			if ( pq.getQuestion().getType().equals("likert")) {
				answer.put(pq.getRefNo(), up.getLikertValue());
				result.put(pq.getRefNo(), true);
				
			} else if ( pq.getQuestion().getType().equals("single")) {
				
				
				answer.put(pq.getRefNo(), up.getSingleChoice());
				result.put(pq.getRefNo(), pq.getQuestion().getCorrectSingleChoice() == up.getSingleChoice());
			
				totalCount++;
				if ( pq.getQuestion().getCorrectSingleChoice() == up.getSingleChoice() ) {
					correctCount++;
				}
			} else if ( pq.getQuestion().getType().equals("multiple")) {
				List<Integer> multipleAnswer = new ArrayList<Integer>();
				multipleAnswer.add(up.getChoice1());
				multipleAnswer.add(up.getChoice2());
				multipleAnswer.add(up.getChoice3());
				multipleAnswer.add(up.getChoice4());
				multipleAnswer.add(up.getChoice5());
				answer.put(pq.getRefNo(), multipleAnswer);
				
				boolean correct = true;
				if ( pq.getQuestion().getCorrect1() && up.getChoice1() == 0 ) {
					correct = false;
				}
				if ( correct && pq.getQuestion().getCorrect2() && up.getChoice2() == 0 ) {
					correct = false;
				}
				if ( correct && pq.getQuestion().getCorrect3() && up.getChoice3() == 0 ) {
					correct = false;
				}
				if ( correct && pq.getQuestion().getCorrect4() && up.getChoice4() == 0 ) {
					correct = false;
				}
				if ( correct && pq.getQuestion().getCorrect5() && up.getChoice5() == 0 ) {
					correct = false;
				}
				
				result.put(pq.getRefNo(), correct);
				
				
				totalCount++;
				if ( correct ) {
					correctCount++;
				}
				
			} else if ( pq.getQuestion().getType().equals("text")) {
				answer.put(pq.getRefNo(), up.getTextAnswer());
			} else if ( pq.getQuestion().getType().equals("essay")) {
				answer.put(pq.getRefNo(), up.getEssayAnswer());
			} else if ( pq.getQuestion().getType().equals("date")) {
				answer.put(pq.getRefNo(), up.getDateInput());
			}
			
			answer.put("total_count", totalCount);
			answer.put("correct_count", correctCount);
			answer.put("wrong_count", totalCount - correctCount);
			
			double pct = round(((double) correctCount / (double) totalCount) * 100, 2);
			answer.put("percentage", pct);
		}
		
		
		return path + "/resultTranscript.vm";
	}
	
	
	public static double round(double value, int places) {

	    BigDecimal bd = new BigDecimal(value);

	    bd = bd.setScale(places, RoundingMode.HALF_UP);

	    return bd.doubleValue();

	}

}
