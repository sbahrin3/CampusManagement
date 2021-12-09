package educate.questionnare;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.questionnare.entity.TEPart;
import educate.questionnare.entity.TEPartQuestion;
import educate.questionnare.entity.TEQuestionnare;
import educate.questionnare.entity.TESet;
import educate.questionnare.entity.TEUserAnswerLog;
import educate.questionnare.entity.TEUserLog;
import educate.questionnare.entity.TEUserPath;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.Teacher;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class RunQuestionnareModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "questionnare/run";
	
	public void preProcess() {
		System.out.println(command);
		context.put("util", new lebah.util.Util());
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.remove("answerLog");
	}

	@Override
	public String start() {
		String setId = getParam("runSetId");
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);

		//get first question
		TEPart part = set.getParts().get(0);
		context.put("part", part);
		TEPartQuestion question = part.getQuestions().get(0);
		context.put("partQuestion", question);
		
		String userSession = request.getSession().getId();
		String userId = getParam("runUserId");
		String questionnareId = getParam("runQuestionnareId");
		
		System.out.println("userId = " + userId);
		System.out.println("questionnareId = " + questionnareId);
		
		TEQuestionnare questionnare = null;
		if ( !"".equals(userId) && !"".equals(questionnareId)) {
			userSession = userId + "." + questionnareId;
			questionnare = db.find(TEQuestionnare.class, questionnareId);
			
			
			Subject subject = questionnare.getSubject();
			Teacher teacher = questionnare.getTeacher();
			
			context.put("subject", subject);
			context.put("teacher", teacher);
			
			
		}
		
		TEUserLog userLog = (TEUserLog) db.get("select u from TEUserLog u where u.userSessionId = '" + userSession + "' and u.set.id = '" + setId + "'");
		if ( userLog == null ) {
			//System.out.println("new user log");
			userLog = createUserLog(userId, userSession, set, questionnare);
			request.getSession().setAttribute("_user_questionnare_log", userLog);
			context.remove("answerLog");
		}
		else {
			//System.out.println("user log exist!");
			//check if userLog really persisted
			String userLogId = userLog.getId();
			userLog = db.find(TEUserLog.class, userLogId);
			if ( userLog == null ) userLog = createUserLog(userId, userSession, set, questionnare);
			//remove all
			if ( userLog.getUserPaths().size() > 0 ) {
				db.begin();
				for ( TEUserPath p : userLog.getUserPaths() ) {
					db.remove(p);
				}
				try {
					userLog.setUserPaths(null);
					db.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			context.put("answerLog", getUserAnswerLog(userLog, question));
			

			
			request.getSession().setAttribute("_user_questionnare_log", userLog);
		}
		context.put("direction", "next");
		
		
		return path + "/start.vm";
	}

	private TEUserLog createUserLog(String userId, String userSession, TESet set, TEQuestionnare questionnare) {
		TEUserLog userLog;
		db.begin();
		userLog = new TEUserLog();
		userLog.setUserSessionId(userSession);
		userLog.setUserId(userId);
		userLog.setRemoteAddress(request.getRemoteAddr());
		userLog.setSet(set);
		userLog.setQuestionnare(questionnare);
		userLog.setCreateDate(new Date());
		db.persist(userLog);
		try {
			db.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userLog;
	}
	
	
	
	@Command("getNext")
	public String getNext() throws Exception {
		context.put("direction", "next");
		TEUserLog userLog = (TEUserLog) request.getSession().getAttribute("_user_questionnare_log");
		
		String partQuestionId = getParam("partQuestionId");
		
		TEPartQuestion partQuestion = db.find(TEPartQuestion.class, partQuestionId);

		TEPart part = partQuestion.getPart();
		context.put("part", part);
		
		TESet set = part.getSet();
		List<TEPartQuestion> questions = part.getQuestions();
		TEPartQuestion nextPartQuestion = null;
		boolean ended = false;
		
		//check for jump first
		System.out.println("Next Question, but Check for Jump first...");
		if ("single".equals(partQuestion.getQuestion().getType())) {
			int choice = getParamAsInteger("single_choice");
			System.out.println("choice = " + choice);
			switch ( choice ) {
				case 1: nextPartQuestion = partQuestion.getNextQuestion1(); break;
				case 2: nextPartQuestion = partQuestion.getNextQuestion2(); break;
				case 3: nextPartQuestion = partQuestion.getNextQuestion3(); break;
				case 4: nextPartQuestion = partQuestion.getNextQuestion4(); break;
				case 5: nextPartQuestion = partQuestion.getNextQuestion5(); break;
			}
			
		}
		
		System.out.println("nextPartQuestion is " + nextPartQuestion);
		
		if ( nextPartQuestion == null ) {
			
			int i = questions.indexOf(partQuestion);
			if ( i == questions.size() - 1 ) { //at last position
				//look for next part
				int currentPartNo = set.getParts().indexOf(part);
				int nextPartNo = currentPartNo + 1;
				if ( nextPartNo < set.getParts().size()) {
					part = set.getParts().get(nextPartNo);
					nextPartQuestion = part.getQuestions().get(0);

				} else {
					
					ended = true;
					
				}
			}
			else {
				i++;
				nextPartQuestion = questions.get(i);
				
			}

		}
		
		saveUserPath(userLog, partQuestion, nextPartQuestion);
		context.put("answerLog", getUserAnswerLog(userLog, nextPartQuestion));
		
		
		getIsAnswered(userLog, set);
		
		return !ended ? path + "/questionnare.vm" : path + "/questionnare_ended.vm";
	}

	private void getIsAnswered(TEUserLog userLog, TESet set) {
		Map<String, Boolean> answered = new HashMap<String, Boolean>();
		context.put("answered", answered);
		
		List<TEUserAnswerLog> answerLogs = db.list("select a from TEUserAnswerLog a where a.userLog.id = '" + userLog.getId() + "' and a.question.part.set.id = '" + set.getId() + "'");
		context.put("answerLogs", answerLogs);
		
		boolean isAnswered = false;
		String ref = "";
		for ( TEUserAnswerLog log : answerLogs ) {
			ref = log.getQuestion().getRefNo();
			if ( log.getQuestion().getQuestion().type.equals("single") ) {
				isAnswered = log.getSingleChoice() > 0;
			} else if ( log.getQuestion().getQuestion().type.equals("multiple") ) {
				if ( log.getChoice1() == 0 && log.getChoice2() == 0 && log.getChoice3() == 0 && log.getChoice4() == 0 && log.getChoice5() == 0 ) {
					isAnswered = false;
				} else {
					isAnswered = true;
				}
			}
			answered.put(ref, isAnswered);
			
		}
	}

	private void saveUserPath(TEUserLog userLog, TEPartQuestion partQuestion, TEPartQuestion nextPartQuestion) throws Exception {
		
		System.out.println("saveUserPath: " + userLog.getUserId() + ", " + partQuestion.getRefNo());
		
		Integer maxSeq = (Integer) db.get("select max(p.sequence) from TEUserPath p where p.userLog.id = '" + userLog.getId() + "'");
		int seq = maxSeq != null ? maxSeq.intValue() + 1 : 1;
		
		db.begin();
		TEUserPath userPath = new TEUserPath();
		userPath.setUserLog(userLog);
		userPath.setSequence(seq);
		userPath.setQuestion(partQuestion);
		userLog.getUserPaths().add(userPath);
		saveUserAnswer(userPath);
		db.commit();
		
		saveUserAnswerLog(userPath);
		
		if ( nextPartQuestion != null ) {
			context.put("partQuestion", nextPartQuestion);
			context.put("part", nextPartQuestion.getPart());
			context.put("set", nextPartQuestion.getPart().getSet());
		}
		context.put("previousPartQuestion", partQuestion);
	}
	
	@Command("getPrevious")
	public String getPrevious() throws Exception {
		context.put("direction", "previous");
		TEUserLog userLog = (TEUserLog) request.getSession().getAttribute("_user_questionnare_log");
		String partQuestionId = getParam("partQuestionId");
		TEPartQuestion partQuestion = db.find(TEPartQuestion.class, partQuestionId);
		TEPart part = partQuestion.getPart();
		context.put("part", part);
		
		List<TEUserPath> removes = new ArrayList<TEUserPath>();
		List<TEUserPath> userPaths = userLog.getUserPaths();
		boolean startRemove = false;
		for ( TEUserPath p : userPaths ) {
			if ( p.getQuestion().getId().equals(partQuestion.getId()) ) {
				startRemove = true;
			}
			if ( startRemove ) {
				removes.add(p);
			}
		}
		//remove
		db.begin();
		for ( TEUserPath p : removes ) {
			db.remove(p);
		}
		userLog.getUserPaths().removeAll(removes);
		db.commit();
		context.put("partQuestion", partQuestion);
		
		int n = userLog.getUserPaths().size() - 1;
		TEPartQuestion previousPartQuestion = n > -1 ? userLog.getUserPaths().get(n).getQuestion() : null;
		context.put("previousPartQuestion", previousPartQuestion);
		
		context.put("answerLog", getUserAnswerLog(userLog, partQuestion));
		
		getIsAnswered(userLog, part.getSet());
		
		return path + "/questionnare.vm";
	}	
	
	@Command("getQuestion")
	public String getQuestion() throws Exception {
		
		context.put("direction", "next");
		TEUserLog userLog = (TEUserLog) request.getSession().getAttribute("_user_questionnare_log");
		String fromQuestionId = getParam("fromQuestionId");
		TEPartQuestion fromPartQuestion = db.find(TEPartQuestion.class, fromQuestionId);
		//save answer

		//then go to clicked question
		String partQuestionId = getParam("partQuestionId");
		TEPartQuestion partQuestion = db.find(TEPartQuestion.class, partQuestionId);
		
		
		saveUserPath(userLog, fromPartQuestion, partQuestion);
		context.put("answerLog", getUserAnswerLog(userLog, partQuestion));
		
		getIsAnswered(userLog, partQuestion.getPart().getSet());
		
		return path + "/questionnare.vm"; 
	}
	
	
	@Command("complete")
	public String completeQuestionnare() throws Exception {
		TEUserLog userLog = (TEUserLog) request.getSession().getAttribute("_user_questionnare_log");
		db.begin();
		userLog.setComplete(true);
		userLog.setCompleteDate(new Date());
		db.commit();
		
		
		return path + "/complete.vm";
	}
	

	
	private int getChoice(String param) {
		int result = 0;
		String r = getParam(param);
		try {
			result = Integer.parseInt(r);
		} catch ( Exception e ) {
			result = 0;
		}
		return result;
		
	}

	
	private void saveUserAnswer(TEUserPath up) throws Exception {
		String qt = up.getQuestion().getQuestion().getType();
		TEPartQuestion q = up.getQuestion();
		if ( "likert".equals(qt)) {
			up.setLikertValue(getParamAsInteger("likert"));
		} else if ( "single".equals(qt)) {
			up.setSingleChoice(getParamAsInteger("single_choice"));
		} else if ( "multiple".equals(qt)) {
			up.setChoice1(getChoice("choice1"));
			up.setChoice2(getChoice("choice2"));
			up.setChoice3(getChoice("choice3"));
			up.setChoice4(getChoice("choice4"));
			up.setChoice5(getChoice("choice5"));
		} else if ( "text".equals(qt)) {
			up.setTextAnswer(getParam("textAnswer"));
		} else if ( "essay".equals(qt)) {
			up.setEssayAnswer(getParam("essayAnswer"));
		} else if ( "date".equals(qt)) {
			Date dateInput = null;
			try {
				dateInput = new SimpleDateFormat("dd-MM-yyyy").parse(getParam("dateInput"));
			} catch ( Exception e ) { }
			up.setDateInput(dateInput);
		} else if ( "upload".equals(qt)) {
			
		}
	}
	
	private void saveUserAnswerLog(TEUserPath up) throws Exception {
		boolean add = false;
		TEPartQuestion q = up.getQuestion();
		TEUserLog userLog = up.getUserLog();
		String uid = userLog.getId() + "_" + q.getId();
		TEUserAnswerLog answerLog = db.find(TEUserAnswerLog.class, uid);
		if ( answerLog == null ) {
			answerLog = new TEUserAnswerLog();
			answerLog.setId(uid);
			answerLog.setQuestion(q);
			answerLog.setUserLog(userLog);
			add = true;
		}
		answerLog.setLikertValue(up.getLikertValue());
		answerLog.setSingleChoice(up.getSingleChoice());
		answerLog.setChoice1(up.getChoice1());
		answerLog.setChoice2(up.getChoice2());
		answerLog.setChoice3(up.getChoice3());
		answerLog.setChoice4(up.getChoice4());
		answerLog.setChoice5(up.getChoice5());
		answerLog.setTextAnswer(up.getTextAnswer());
		answerLog.setEssayAnswer(up.getEssayAnswer());
		answerLog.setDateInput(up.getDateInput());
		db.begin();
		if ( add) db.persist(answerLog);
		db.commit();
	}	
	
	private TEUserAnswerLog getUserAnswerLog(TEUserLog userLog, TEPartQuestion q) {
		if ( q == null || userLog == null ) return null;
		String uid = userLog.getId() + "_" + q.getId();
		return db.find(TEUserAnswerLog.class, uid);
	}
	
	private TEUserAnswerLog getUserAnswerLog(String userLogId, String partQuestionId) {
		String uid = userLogId + "_" + partQuestionId;
		return db.find(TEUserAnswerLog.class, uid);
	}	
	
	public static void main(String[] args) throws Exception {
		
		int x = 45;
		int z = 100;
		double p = ((double) x/z) * 100;
		System.out.println(p);
		
	}
	
}
