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
import educate.enrollment.entity.Student;
import educate.questionnare.entity.TEPart;
import educate.questionnare.entity.TEPartQuestion;
import educate.questionnare.entity.TEQuestion;
import educate.questionnare.entity.TEQuestionnare;
import educate.questionnare.entity.TEReportGenerate;
import educate.questionnare.entity.TEResult;
import educate.questionnare.entity.TESet;
import educate.questionnare.entity.TEUserLog;
import educate.questionnare.entity.TEUserPath;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.Teacher;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class QuestionnareReportModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "questionnare/report";
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("r", new DecimalFormat("#.00"));
		context.put("util", new lebah.util.Util());
		
		System.out.println(command);
	}

	@Override
	public String start() {
		listQuestionnareGroups();
		return path + "/start.vm";
	}
	
	@Command("home")
	public String home() {
		listQuestionnareGroups();
		return path + "/groups.vm";
	}

	private void listQuestionnareGroups() {

		List<TEQuestionnare> questionnares = db.list("select q from TEQuestionnare q order by q.set.id, q.startDate desc");

		List<TEQuestionnare> groups = new ArrayList<TEQuestionnare>();
		context.put("questionnares", groups);
		
		Map<String, Boolean> hasSubjectMap = new HashMap<String, Boolean>();
		context.put("hasSubjectMap", hasSubjectMap);
		
		String groupId = "";
		for (TEQuestionnare q : questionnares) {
			if (!groupId.equals(q.getSet().getId() + "-" + q.getStartDate())) {
				groups.add(q);
				groupId = q.getSet().getId() + "-" + q.getStartDate();
				
				if ( q.getSubject() != null ) 
					hasSubjectMap.put(groupId, new Boolean(true));
				else
					hasSubjectMap.put(groupId, new Boolean(false));
			}
		}
		
		Map<String, TEReportGenerate> statGenerateMap = new HashMap<String, TEReportGenerate>();
		context.put("statGenerateMap", statGenerateMap);
		List<TEReportGenerate> generates = db.list("select g from TEReportGenerate g group by g.set.id, g.startDate");
		for ( TEReportGenerate g : generates ) {
			String key = g.getSet().getId() + "-" + g.getStartDate();
			statGenerateMap.put(key, g);
		}
	}
	

	@Command("generateReport")
	public String generateReport() throws Exception {
		
		String setId = getParam("setId");
		TESet set = db.find(TESet.class, setId);
		String dateStart = getParam("dateStart");
		Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStart);

		Hashtable h = new Hashtable();
		h.put("startDate", startDate);
		
		String sql = "select q from TEQuestionnare q where q.set.id = '" + setId + "' and q.startDate = :startDate order by q.startDate desc";
		List<TEQuestionnare> questionnares = db.list(sql, h);
		for ( TEQuestionnare q : questionnares ) {
			generateReport2(q, startDate);
		}
		
		db.begin();
		db.executeUpdate("delete from TEReportGenerate g where g.set.id = '" + setId + "' and g.startDate = :startDate", h);
		db.commit();
		
		db.begin();
		TEReportGenerate g = new TEReportGenerate();
		g.setGeneratedDate(new Date());
		g.setStartDate(startDate);
		g.setSet(set);
		db.persist(g);
		db.commit();
		
		return path + "/generateReportDone.vm";
	}
	
	public void generateReport2(TEQuestionnare q, Date startDate) throws Exception {
		
		context.put("questionnare", q);
		String questionnareId = q.getId();
		
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
						if ( date != null ) results.add(new SimpleDateFormat("dd-MM-yyyy").format(date));
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
		
		
		
		//delete first
		Hashtable h = new Hashtable();
		h.put("startDate", startDate);
		String sql = "delete from TEResult r where r.questionnare.id = '" + q.getId() + "' and r.startDate = :startDate";
		db.begin();
		db.executeUpdate(sql, h);
		db.commit();
		
		String teacherId = q.getTeacher() != null ? q.getTeacher().getId() : "";
		String subjectId = q.getSubject() != null ? q.getSubject().getId() : "";
		
		Teacher teacher = db.find(Teacher.class, teacherId);
		Subject subject = db.find(Subject.class, subjectId);
		
		for ( List<String> results : resultList ) {
			
			int i = 0;
			
			String matricNo = results.get(i);
			Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
			if ( student != null ) {
				
			
				for ( TEPartQuestion pq : questionList ) {
					i++;
					String refNo = pq.getRefNo();
					String result = results.get(i);
					String type = questionTypes.get(i);
					
					db.begin();
					
					TEResult qr = new TEResult();
					qr.setQuestionnare(q);
					qr.setStartDate(startDate);
					qr.setSubject(subject);
					qr.setTeacher(teacher);
					qr.setStudent(student);
					qr.setQuestionRefNo(refNo);
					qr.setQuestionType(type);
					qr.setSequence(i);
					if ( "likert".equals(type) || "single".equals(type) || "multiple".equals(type)) {
						int data = 0;
						try {
							data = Integer.parseInt(result);
						} catch ( Exception e ) { }
						qr.setAnswerNum(data);
					} else {
						qr.setAnswerNum(0);
						qr.setAnswerText(result);
					}
					
					db.persist(qr);
					db.commit();
					
				}
			
			}
			
		}

	}	
	
	@Command("listTeachers")
	public String listTeachers() throws Exception {
		String setId = getParam("setId");
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);
		String dateStart = getParam("dateStart");
		Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStart);
		context.put("startDate", startDate);
		
		Hashtable h = new Hashtable();
		h.put("startDate", startDate);
		
		String sql = "select r.teacher from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate group by r.teacher.name";
		List<Teacher> teachers = db.list(sql, h);
		context.put("teachers", teachers);
		
		return path + "/listTeachers.vm";
	}
	
	@Command("statisticByTeacher")
	public String getStatisticByTeacher() throws Exception {
		
		String setId = getParam("setId");
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);
		String dateStart = getParam("dateStart");
		Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStart);
		context.put("startDate", startDate);
		
		Hashtable h = new Hashtable();
		h.put("startDate", startDate);
		String sql = "";
		
		String teacherId = getParam("teacherId");
		Teacher teacher = db.find(Teacher.class, teacherId);
		context.put("teacher", teacher);
		
		sql = "select r.questionRefNo from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate group by r.questionRefNo order by r.sequence";
		List<String> refNos = db.list(sql, h);
		context.put("refNos", refNos);
		
		
		sql = "select r.subject from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate and r.answerNum > 0 and r.teacher.id = '" + teacher.getId() + "' and r.student is not null group by r.subject.code order by r.subject.code";
		List<Subject> subjects = db.list(sql, h);
		context.put("subjects", subjects);
		
		TEQuestionnare questionnare = null;
		sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate and r.teacher.id = '" + teacher.getId() + "' group by r.subject.code order by r.subject.code";
		List<TEQuestionnare> questionnares = db.list(sql, h);
		if ( questionnares.size() == 0 ) {
			sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate";
			questionnares = db.list(sql, h);
		}
		questionnare = questionnares.get(0);
		TEPart part = questionnare.getSet().getParts().get(0);
		List<TEPartQuestion> partQuestions = part.getQuestions();
		
		Map<String, TEQuestion> questionMap = new HashMap<String, TEQuestion>();
		context.put("questionMap", questionMap);
		
		Map<String, Stat> statMap = new HashMap<String, Stat>();
		context.put("statMap", statMap);
		
		for ( TEPartQuestion pq : partQuestions ) {
			
			questionMap.put(pq.getRefNo(), pq.getQuestion());
			
			for ( Subject s : subjects ) {
				
				sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
						"and r.subject.id = '" + s.getId() + "' and r.teacher.id = '" + teacher.getId() + "' " +
						"and r.questionRefNo = '" + pq.getRefNo() + "' " +
						"and r.startDate = :startDate ";
				List<TEResult> results = db.list(sql, h);
				List<Double> data = new ArrayList<Double>();
				for ( TEResult r : results ) {
					String type = r.getQuestionType();
					if ( "likert".equals(type) || "single".equals(type) || "multiple".equals(type)) {
						data.add((double) r.getAnswerNum());
					}
				}
				Stat stat = new Stat();
				stat.setMean(mean(data));
				stat.setMedian(median(data));
				stat.setSd(sd(data));
				stat.setSize(data.size());
				statMap.put(teacher.getId() + s.getId() + pq.getRefNo(), stat);
				
			}
		}
		
		return path + "/viewStatisticByTeacher.vm";
	}
	
	@Command("statisticByAllTeachers")
	public String getStatisticByAllTeachers() throws Exception {
		
		String setId = getParam("setId");
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);
		String dateStart = getParam("dateStart");
		Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStart);
		context.put("startDate", startDate);
		
		String subjectId = getParam("subjectId");
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		
		Hashtable h = new Hashtable();
		h.put("startDate", startDate);
		String sql = "";
		
		sql = "select r.subject from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate and r.answerNum > 0 and r.teacher is not null and r.student is not null group by r.subject.code order by r.subject.code";
		List<Subject> subjects = db.list(sql, h);
		context.put("subjects", subjects);

		
		sql = "select r.questionRefNo from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate group by r.questionRefNo order by r.sequence";
		List<String> refNos = db.list(sql, h);
		context.put("refNos", refNos);
		
		sql = "select r.teacher from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate and r.answerNum > 0 and r.teacher is not null and r.student is not null group by r.teacher.id order by r.teacher.id";
		List<Teacher> teachers = db.list(sql, h);
		context.put("teachers", teachers);

		
		TEQuestionnare questionnare = null;
		
		if ( "".equals(subjectId))
			sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate";
		else
			sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.subject.id = '" + subjectId + "' and r.startDate = :startDate";
		
		List<TEQuestionnare> questionnares = db.list(sql, h);
		questionnare = questionnares.get(0);
		TEPart part = questionnare.getSet().getParts().get(0);
		List<TEPartQuestion> partQuestions = part.getQuestions();
		
		Map<String, TEQuestion> questionMap = new HashMap<String, TEQuestion>();
		context.put("questionMap", questionMap);

		Map<String, Stat> statMap = new HashMap<String, Stat>();
		context.put("statMap", statMap);
		

		
		for ( TEPartQuestion pq : partQuestions ) {
			
			questionMap.put(pq.getRefNo(), pq.getQuestion());
			
			for ( Teacher t : teachers ) {
				
				if ( "".equals(subjectId))
					sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
							"and r.teacher.id = '" + t.getId() + "' " +
							"and r.questionRefNo = '" + pq.getRefNo() + "' " +
							"and r.subject is not null " +
							"and r.answerNum > 0 and r.teacher is not null and r.student is not null " +
							"and r.startDate = :startDate ";
				else
					sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
							"and r.teacher.id = '" + t.getId() + "' " +
							"and r.questionRefNo = '" + pq.getRefNo() + "' " +
							"and r.subject.id ='" + subjectId + "' " +
							"and r.answerNum > 0 and r.teacher is not null and r.student is not null " +
							"and r.startDate = :startDate ";
					
				List<TEResult> results = db.list(sql, h);
				List<Double> data = new ArrayList<Double>();
				for ( TEResult r : results ) {
					String type = r.getQuestionType();
					if ( "likert".equals(type) || "single".equals(type) || "multiple".equals(type)) {
						data.add((double) r.getAnswerNum());
					}
				}
				Stat stat = new Stat();
				stat.setMean(mean(data));
				stat.setMedian(median(data));
				stat.setSd(sd(data));
				stat.setSize(data.size());
				statMap.put(t.getId() + pq.getRefNo(), stat);
				
			}
		}

		
		request.getSession().setAttribute("subject", subject);
		request.getSession().setAttribute("refNos", refNos);
		request.getSession().setAttribute("teachers", teachers);
		request.getSession().setAttribute("questionMap", questionMap);
		request.getSession().setAttribute("statMap", statMap);
		
		return path + "/viewStatisticByAllTeachers2.vm";
	}
	
	@Command("statisticByAllFaculties")
	public String getStatisticByAllFaculties() throws Exception {
		
		String setId = getParam("setId");
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);
		String dateStart = getParam("dateStart");
		Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStart);
		context.put("startDate", startDate);
		
		String subjectId = getParam("subjectId");
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		Hashtable h = new Hashtable();
		h.put("startDate", startDate);
		String sql = "";
		
		sql = "select r.subject from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate and r.answerNum > 0 and r.teacher is not null and r.student is not null group by r.subject.code order by r.subject.code";
		List<Subject> subjects = db.list(sql, h);
		context.put("subjects", subjects);

		
		sql = "select r.questionRefNo from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate group by r.questionRefNo order by r.sequence";
		List<String> refNos = db.list(sql, h);
		context.put("refNos", refNos);
		
		sql = "select r.subject.faculty from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate and r.answerNum > 0 and r.teacher is not null and r.student is not null group by r.subject.faculty.id order by r.subject.faculty.id";
		List<Faculty> faculties = db.list(sql, h);
		context.put("faculties", faculties);
		
		TEQuestionnare questionnare = null;
		
		if ( "".equals(subjectId))
			sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate";
		else
			sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.subject.id = '" + subjectId + "' and r.startDate = :startDate";
		
		List<TEQuestionnare> questionnares = db.list(sql, h);
		questionnare = questionnares.get(0);
		TEPart part = questionnare.getSet().getParts().get(0);
		List<TEPartQuestion> partQuestions = part.getQuestions();
		
		Map<String, TEQuestion> questionMap = new HashMap<String, TEQuestion>();
		context.put("questionMap", questionMap);
		
		Map<String, Stat> statMap = new HashMap<String, Stat>();
		context.put("statMap", statMap);
		
		for ( TEPartQuestion pq : partQuestions ) {
			
			questionMap.put(pq.getRefNo(), pq.getQuestion());
			
			for ( Faculty f : faculties ) {
				
				if ( "".equals(subjectId))
					sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
							"and r.subject.faculty.id = '" + f.getId() + "' " +
							"and r.questionRefNo = '" + pq.getRefNo() + "' " +
							"and r.subject is not null " +
							"and r.answerNum > 0 and r.teacher is not null and r.student is not null " +
							"and r.startDate = :startDate ";
				else
					sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
							"and r.subject.faculty.id = '" + f.getId() + "' " +
							"and r.questionRefNo = '" + pq.getRefNo() + "' " +
							"and r.subject.id = '" + subjectId + "' " +
							"and r.answerNum > 0 and r.teacher is not null and r.student is not null " +
							"and r.startDate = :startDate ";
				
				List<TEResult> results = db.list(sql, h);

				List<Double> data = new ArrayList<Double>();
				for ( TEResult r : results ) {
					String type = r.getQuestionType();
					if ( "likert".equals(type) || "single".equals(type) || "multiple".equals(type)) {
						data.add((double) r.getAnswerNum());
					}
				}
				Stat stat = new Stat();
				stat.setMean(mean(data));
				stat.setMedian(median(data));
				stat.setSd(sd(data));
				stat.setSize(data.size());
				statMap.put(f.getId() + pq.getRefNo(), stat);
				
			}
		}
		
		
		request.getSession().setAttribute("subject", subject);
		request.getSession().setAttribute("refNos", refNos);
		request.getSession().setAttribute("faculties", faculties);
		request.getSession().setAttribute("questionMap", questionMap);
		request.getSession().setAttribute("statMap", statMap);
		
		return path + "/viewStatisticByAllFaculties.vm";
	}
	
	@Command("statisticByAllPrograms")
	public String getStatisticByAllPrograms() throws Exception {
		
		String setId = getParam("setId");
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);
		String dateStart = getParam("dateStart");
		Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStart);
		context.put("startDate", startDate);
		
		String subjectId = getParam("subjectId");
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		Hashtable h = new Hashtable();
		h.put("startDate", startDate);
		String sql = "";
		
		sql = "select r.subject from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate and r.answerNum > 0 and r.teacher is not null and r.student is not null group by r.subject.code order by r.subject.code";
		List<Subject> subjects = db.list(sql, h);
		context.put("subjects", subjects);

		
		sql = "select r.questionRefNo from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate group by r.questionRefNo order by r.sequence";
		List<String> refNos = db.list(sql, h);
		context.put("refNos", refNos);
		
		sql = "select r.student.program from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate and r.answerNum > 0 and r.teacher is not null and r.student is not null group by r.student.program.id order by r.student.program.id";
		List<Program> programs = db.list(sql, h);
		context.put("programs", programs);
		
		TEQuestionnare questionnare = null;
		
		if ( "".equals(subjectId))
			sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate";
		else
			sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.subject.id = '" + subjectId + "' and r.startDate = :startDate";
		
		List<TEQuestionnare> questionnares = db.list(sql, h);
		questionnare = questionnares.get(0);
		TEPart part = questionnare.getSet().getParts().get(0);
		List<TEPartQuestion> partQuestions = part.getQuestions();
		
		Map<String, TEQuestion> questionMap = new HashMap<String, TEQuestion>();
		context.put("questionMap", questionMap);
		
		Map<String, Stat> statMap = new HashMap<String, Stat>();
		context.put("statMap", statMap);
		
		for ( TEPartQuestion pq : partQuestions ) {
			
			questionMap.put(pq.getRefNo(), pq.getQuestion());
			
			for ( Program p : programs ) {
				
				if ( "".equals(subjectId))
					sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
							"and r.student.program.id = '" + p.getId() + "' " +
							"and r.questionRefNo = '" + pq.getRefNo() + "' " +
							"and r.subject is not null " +
							"and r.answerNum > 0 and r.teacher is not null and r.student is not null " +
							"and r.startDate = :startDate ";
				else
					sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
							"and r.student.program.id = '" + p.getId() + "' " +
							"and r.questionRefNo = '" + pq.getRefNo() + "' " +
							"and r.subject.id = '" + subjectId + "' " + 
							"and r.answerNum > 0 and r.teacher is not null and r.student is not null " +
							"and r.startDate = :startDate ";
				
				List<TEResult> results = db.list(sql, h);
				List<Double> data = new ArrayList<Double>();
				for ( TEResult r : results ) {
					String type = r.getQuestionType();
					if ( "likert".equals(type) || "single".equals(type) || "multiple".equals(type)) {
						data.add((double) r.getAnswerNum());
					}
				}
				Stat stat = new Stat();
				stat.setMean(mean(data));
				stat.setMedian(median(data));
				stat.setSd(sd(data));
				stat.setSize(data.size());
				statMap.put(p.getId() + pq.getRefNo(), stat);
				
			}
		}
		
		
		request.getSession().setAttribute("subject", subject);
		request.getSession().setAttribute("refNos", refNos);
		request.getSession().setAttribute("programs", programs);
		request.getSession().setAttribute("questionMap", questionMap);
		request.getSession().setAttribute("statMap", statMap);
		
		return path + "/viewStatisticByAllPrograms.vm";
	}
	
	
	@Command("statisticByOverall")
	public String getStatisticByOverall() throws Exception {
		
		String setId = getParam("setId");
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);
		String dateStart = getParam("dateStart");
		Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStart);
		context.put("startDate", startDate);
		
		String subjectId = getParam("subjectId");
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		Hashtable h = new Hashtable();
		h.put("startDate", startDate);
		String sql = "";
		
		sql = "select r.subject from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate and r.answerNum > 0 and r.teacher is not null and r.student is not null group by r.subject.code order by r.subject.code";
		List<Subject> subjects = db.list(sql, h);
		context.put("subjects", subjects);

		
		sql = "select r.questionRefNo from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate group by r.questionRefNo order by r.sequence";
		List<String> refNos = db.list(sql, h);
		context.put("refNos", refNos);
		
		TEQuestionnare questionnare = null;
		
		if ( "".equals(subjectId))
			sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate";
		else
			sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.subject.id = '" + subjectId + "' and r.startDate = :startDate";
		
		List<TEQuestionnare> questionnares = db.list(sql, h);
		questionnare = questionnares.get(0);
		TEPart part = questionnare.getSet().getParts().get(0);
		List<TEPartQuestion> partQuestions = part.getQuestions();
		
		Map<String, TEQuestion> questionMap = new HashMap<String, TEQuestion>();
		context.put("questionMap", questionMap);
		
		Map<String, Stat> statMap = new HashMap<String, Stat>();
		context.put("statMap", statMap);
		
		for ( TEPartQuestion pq : partQuestions ) {
			
			questionMap.put(pq.getRefNo(), pq.getQuestion());

			
			if ( "".equals(subjectId))
				sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
						"and r.questionRefNo = '" + pq.getRefNo() + "' " +
						"and r.answerNum > 0 and r.student is not null " +
						"and r.startDate = :startDate ";
			else
				sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
						"and r.questionRefNo = '" + pq.getRefNo() + "' " +
						"and r.subject.id = '" + subjectId + "' " +
						"and r.answerNum > 0 and r.teacher is not null and r.student is not null " +
						"and r.startDate = :startDate ";
			List<TEResult> results = db.list(sql, h);
			List<Double> data = new ArrayList<Double>();
			for ( TEResult r : results ) {
				String type = r.getQuestionType();
				if ( "likert".equals(type) || "single".equals(type) || "multiple".equals(type)) {
					data.add((double) r.getAnswerNum());
				}
			}
			Stat stat = new Stat();
			stat.setMean(mean(data));
			stat.setMedian(median(data));
			stat.setSd(sd(data));
			stat.setSize(data.size());
			statMap.put(pq.getRefNo(), stat);

		}
		
		request.getSession().setAttribute("subject", subject);
		request.getSession().setAttribute("refNos", refNos);
		request.getSession().setAttribute("questionMap", questionMap);
		request.getSession().setAttribute("statMap", statMap);
		
		return path + "/viewStatisticByOverall.vm";
	}
	
	@Command("viewStatisticReport")
	public String viewStatisticReport() throws Exception {
		
		String setId = getParam("setId");
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);
		String dateStart = getParam("dateStart");
		Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStart);
		context.put("startDate", startDate);
		
		Hashtable h = new Hashtable();
		h.put("startDate", startDate);
	
		String sql = "select r.questionRefNo from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate group by r.questionRefNo order by r.sequence";
		List<String> refNos = db.list(sql, h);
		context.put("refNos", refNos);

		
		TEQuestionnare questionnare = null;
		sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate group by r.subject.code order by r.subject.code";
		List<TEQuestionnare> questionnares = db.list(sql, h);
		if ( questionnares.size() == 0 ) {
			sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate";
			questionnares = db.list(sql, h);
		}
		questionnare = questionnares.get(0);
		TEPart part = questionnare.getSet().getParts().get(0);
		List<TEPartQuestion> partQuestions = part.getQuestions();
		
		
		
		
		return path + "/viewStatisticReport.vm";
	}
	
	@Command("viewStatistic")
	public String viewStatistic() throws Exception {
		
		String refNo = getParam("refNo");
		context.put("refNo", refNo);
		
		String setId = getParam("setId");
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);
		String dateStart = getParam("dateStart");
		Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStart);
		context.put("startDate", startDate);
		
		Hashtable h = new Hashtable();
		h.put("startDate", startDate);
		
		TEQuestionnare questionnare = null;
		String sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate group by r.subject.code order by r.subject.code";
		List<TEQuestionnare> questionnares = db.list(sql, h);
		
		if ( questionnares.size() == 0 ) {
			sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate";
			questionnares = db.list(sql, h);
		}
		
		questionnare = questionnares.get(0);
		TEPart part = questionnare.getSet().getParts().get(0);
		List<TEPartQuestion> partQuestions = part.getQuestions();
		TEPartQuestion partQuestion = null;
		for ( TEPartQuestion pq : partQuestions ) {
			if ( pq.getRefNo().equals(refNo) ) {
				partQuestion = pq;
				break;
			}
		}
		
		if ( partQuestion != null ) {
			context.put("question", partQuestion);
		}
		else {
			context.remove("question");
		}
		
		
		sql = "select r.subject from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate and r.answerNum > 0 and r.teacher is not null and r.student is not null group by r.subject.code order by r.subject.code";
		List<Subject> subjects = db.list(sql, h);
		context.put("subjects", subjects);
		
		sql = "select r.teacher from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate and r.answerNum > 0 and r.teacher is not null and r.student is not null group by r.teacher.id order by r.teacher.id";
		List<Teacher> teachers = db.list(sql, h);
		context.put("teachers", teachers);
		
		Map<String, Stat> statMap = new HashMap<String, Stat>();
		context.put("statMap", statMap);
		
		for ( Subject s : subjects ) {
			for ( Teacher t : teachers ) {
				sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
						"and r.subject.id = '" + s.getId() + "' and r.teacher.id = '" + t.getId() + "' " +
						"and r.questionRefNo = '" + refNo + "' " +
						"and r.startDate = :startDate ";
				List<TEResult> results = db.list(sql, h);
				List<Double> data = new ArrayList<Double>();
				for ( TEResult r : results ) {
					String type = r.getQuestionType();
					if ( "likert".equals(type) || "single".equals(type) || "multiple".equals(type)) {
						data.add((double) r.getAnswerNum());
					}
				}
				Stat stat = new Stat();
				stat.setMean(mean(data));
				stat.setMedian(median(data));
				stat.setSd(sd(data));
				statMap.put(s.getId() + t.getId(), stat);
			}
		}
		
		for ( Teacher t : teachers ) {
			sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
					"and r.teacher.id = '" + t.getId() + "' " +
					"and r.questionRefNo = '" + refNo + "' " +
					"and r.startDate = :startDate ";
			List<TEResult> results = db.list(sql, h);
			List<Double> data = new ArrayList<Double>();
			for ( TEResult r : results ) {
				String type = r.getQuestionType();
				if ( "likert".equals(type) || "single".equals(type) || "multiple".equals(type)) {
					data.add((double) r.getAnswerNum());
				}
			}
			Stat stat = new Stat();
			stat.setMean(mean(data));
			stat.setMedian(median(data));
			stat.setSd(sd(data));
			stat.setSize(data.size());
			statMap.put(t.getId(), stat);
		}

		
		for ( Subject s : subjects ) {

			sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
					"and r.subject.id = '" + s.getId() + "' " +
					"and r.questionRefNo = '" + refNo + "' " +
					"and r.startDate = :startDate ";
			List<TEResult> results = db.list(sql, h);
			List<Double> data = new ArrayList<Double>();
			for ( TEResult r : results ) {
				String type = r.getQuestionType();
				if ( "likert".equals(type) || "single".equals(type) || "multiple".equals(type)) {
					data.add((double) r.getAnswerNum());
				}
			}
			Stat stat = new Stat();
			stat.setMean(mean(data));
			stat.setMedian(median(data));
			stat.setSd(sd(data));
			stat.setSize(data.size());
			statMap.put(s.getId(), stat);
		
		}
		
		
		
		return path + "/viewStatistic.vm";
	}
	
	@Command("viewStatisticByFaculty")
	public String viewStatisticByFaculty() throws Exception {
		
		String refNo = getParam("refNo");
		context.put("refNo", refNo);
		
		String setId = getParam("setId");
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);
		String dateStart = getParam("dateStart");
		Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStart);
		context.put("startDate", startDate);
		
		Hashtable h = new Hashtable();
		h.put("startDate", startDate);
		
		TEQuestionnare questionnare = null;
		String sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate group by r.subject.code order by r.subject.code";
		List<TEQuestionnare> questionnares = db.list(sql, h);
		
		if ( questionnares.size() == 0 ) {
			sql = "select r.questionnare from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate";
			questionnares = db.list(sql, h);
		}
		
		questionnare = questionnares.get(0);
		TEPart part = questionnare.getSet().getParts().get(0);
		List<TEPartQuestion> partQuestions = part.getQuestions();
		TEPartQuestion partQuestion = null;
		for ( TEPartQuestion pq : partQuestions ) {
			if ( pq.getRefNo().equals(refNo) ) {
				partQuestion = pq;
				break;
			}
		}
		
		if ( partQuestion != null ) {
			context.put("question", partQuestion);
		}
		else {
			context.remove("question");
		}
		
		
		sql = "select r.subject from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate group by r.subject.code order by r.subject.code";
		List<Subject> subjects = db.list(sql, h);
		context.put("subjects", subjects);
		
		sql = "select r.student.program.course.faculty from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate group by r.teacher.id order by r.student.program.course.faculty.id";
		List<Faculty> faculties = db.list(sql, h);
		context.put("faculties", faculties);
		
		Map<String, Stat> statMap = new HashMap<String, Stat>();
		context.put("statMap", statMap);
		
		for ( Subject s : subjects ) {
			for ( Faculty f : faculties ) {
				sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
						"and r.subject.id = '" + s.getId() + "' and r.student.program.course.faculty.id = '" + f.getId() + "' " +
						"and r.questionRefNo = '" + refNo + "' " +
						"and r.startDate = :startDate ";
				List<TEResult> results = db.list(sql, h);
				List<Double> data = new ArrayList<Double>();
				for ( TEResult r : results ) {
					String type = r.getQuestionType();
					if ( "likert".equals(type) || "single".equals(type) || "multiple".equals(type)) {
						data.add((double) r.getAnswerNum());
					}
				}
				Stat stat = new Stat();
				stat.setMean(mean(data));
				stat.setMedian(median(data));
				stat.setSd(sd(data));
				statMap.put(s.getId() + f.getId(), stat);
			}
		}
		
		for ( Faculty f : faculties ) {
			sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
					"and r.student.program.course.faculty.id = '" + f.getId() + "' " +
					"and r.questionRefNo = '" + refNo + "' " +
					"and r.startDate = :startDate ";
			List<TEResult> results = db.list(sql, h);
			List<Double> data = new ArrayList<Double>();
			for ( TEResult r : results ) {
				String type = r.getQuestionType();
				if ( "likert".equals(type) || "single".equals(type) || "multiple".equals(type)) {
					data.add((double) r.getAnswerNum());
				}
			}
			Stat stat = new Stat();
			stat.setMean(mean(data));
			stat.setMedian(median(data));
			stat.setSd(sd(data));
			statMap.put(f.getId(), stat);
		}

		
		for ( Subject s : subjects ) {

			sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' " +
					"and r.subject.id = '" + s.getId() + "' " +
					"and r.questionRefNo = '" + refNo + "' " +
					"and r.startDate = :startDate ";
			List<TEResult> results = db.list(sql, h);
			List<Double> data = new ArrayList<Double>();
			for ( TEResult r : results ) {
				String type = r.getQuestionType();
				if ( "likert".equals(type) || "single".equals(type) || "multiple".equals(type)) {
					data.add((double) r.getAnswerNum());
				}
			}
			Stat stat = new Stat();
			stat.setMean(mean(data));
			stat.setMedian(median(data));
			stat.setSd(sd(data));
			stat.setSize(data.size());
			statMap.put(s.getId(), stat);
		
		}
		
		
		
		return path + "/viewStatisticFaculty.vm";
	}
	
	
	@Command("viewGraphReport")
	public String viewGraphReport() throws Exception {
		
		String setId = getParam("setId");
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);
		String dateStart = getParam("dateStart");
		Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStart);
		context.put("startDate", startDate);
		
		Hashtable h = new Hashtable();
		h.put("startDate", startDate);
	
		String sql = "select r.questionRefNo from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate group by r.questionRefNo order by r.sequence";
		List<String> refNos = db.list(sql, h);
		context.put("refNos", refNos);
		
		sql = "select r from TEResult r where r.questionnare.set.id = '" + setId + "' and r.startDate = :startDate";
		List<TEResult> results= db.list(sql, h);
		
		Map<String, Stat> graphs = new HashMap<String, Stat>();
		context.put("graphs", graphs);
		
		for ( String refNo : refNos ) {
			List<Double> data = new ArrayList<Double>();
			for ( TEResult r : results ) {
				if ( r.getQuestionRefNo().equals(refNo)) {
					String type = r.getQuestionType();
					if ( "likert".equals(type) || "single".equals(type) || "multiple".equals(type)) {
						data.add((double) r.getAnswerNum());
					}
				}
			}
			Stat stat = new Stat();
			stat.setMean(mean(data));
			stat.setMedian(median(data));
			stat.setSd(sd(data));
			//System.out.println(refNo + " ; " + stat.getMean());
			graphs.put(refNo, stat);
		}
		
		return path + "/viewGraphReport.vm";
	}
	
	
	@Command("listGroups")
	public String listGroups() throws Exception {
		listQuestionnareGroups();
		return path + "/groups.vm";
	}
	
	
	
    private static double sum(List<Double> a){
        if (a.size() > 0) {
            double sum = 0;

            for (Double i : a) {
                sum += i;
            }
            return sum;
        }
        return 0;
    }
	
    private static double mean(List<Double> a){
    	if ( a.size() == 0 ) return 0;
        double sum = sum(a);
        double mean = 0;
        mean = sum / (a.size() * 1.0);
        return round2(mean);
    }
    
    private static double median(List<Double> a){
    	if ( a.size() == 0 ) return 0;
        int middle = a.size()/2;
        if (a.size() % 2 == 1) {
            return round2(a.get(middle));
        } else {
           return round2((a.get(middle-1) + a.get(middle)) / 2.0);
        }
    }  
    
    
    public static double round2(double value) {
    	int places = 2;
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    
    private static double sd(List<Double> a){
    	return getSD(a);
    }
    
    
    private static double sdx(List<Integer> x){
    	
    	List<Double> a = new ArrayList<Double>();
    	for ( Integer d : x ) {
    		a.add(Double.parseDouble(Integer.toString(d)));
    	}
    	
    	if ( a.size() - 1 == 0 ) return 0;
        int sum = 0;
        double mean = mean(a);
 
        for (Double i : a)
            sum += Math.pow((i - mean), 2);
        return round2(Math.sqrt( sum / ( a.size() - 1 ) ));
    }   
    
    public static double getSDI(List<Integer> x) {
    	List<Double> a = new ArrayList<Double>();
    	for ( Integer d : x ) {
    		a.add(Double.parseDouble(Integer.toString(d)));
    	}
    	return getSD(a);
    }
    
    public static double getSD(List<Double> ns) {
    	double numbers[] = new double[ns.size()];
    	int i = 0;
    	for ( double n : ns ) {
    		numbers[i] = n;
    		i++;
    	}
    	return getSDX(numbers);
    }
    
    
    public static double getSDX(double numbers[]) {
    	
    	double average = 0;
    	double total = 0;
    	for (int i=0; i<numbers.length;i++) {
    		total += numbers[i];
    	}
    	average = total/numbers.length;
    	
        double sd = 0;
        for (int i=0; i<numbers.length;i++) {
        	sd += ((numbers[i] - average)*(numbers[i] - average)) / (numbers.length - 1);
        }
        
        return Math.sqrt(sd);
        
        
    }
 
    public static void main(String[] args) {
    	
    	List<Integer> n = new ArrayList<Integer>();
    	n.add(3);
    	n.add(4);
    	n.add(5);
    	n.add(4);
    	n.add(3);
    	n.add(3);
    	n.add(3);
    	n.add(4);
    	n.add(5);
    	n.add(4);
    	n.add(4);
    	n.add(4);
    	n.add(3);
    	n.add(4);
    	n.add(5);
    	n.add(4);
    	n.add(4);
    	n.add(5);
    	n.add(3);
    	n.add(4);
    	n.add(4);
    	n.add(4);
    	n.add(5);
    	n.add(3);
    	n.add(4);
    	n.add(4);
    	n.add(5);
    	n.add(5);
    	n.add(5);
    	n.add(4);
    	n.add(4);
    	n.add(4);
    	n.add(4);
    	n.add(5);
    	n.add(5);
    	n.add(4);
    	n.add(4);
    	n.add(3);
    	n.add(4);
    	
    	double s = getSDI(n);
    	
    	System.out.println("sd = " + s);
    }
   

}
