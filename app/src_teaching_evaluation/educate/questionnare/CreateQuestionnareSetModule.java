package educate.questionnare;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.questionnare.entity.TECategory;
import educate.questionnare.entity.TELikert;
import educate.questionnare.entity.TEPart;
import educate.questionnare.entity.TEPartQuestion;
import educate.questionnare.entity.TEQuestion;
import educate.questionnare.entity.TESet;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class CreateQuestionnareSetModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "questionnare/create";
	
	public void preProcess() {
		System.out.println(command);
		context.remove("errorMessage");
	}

	@Override
	public String start() {
		listSets();
		
		return path + "/start.vm";
	}

	private void listSets() {
		String userId = (String) request.getSession().getAttribute("_portal_login");
		
		List<TESet> sets = new ArrayList<TESet>();
		List<TESet> sets1 = db.list("select s from TESet s where s.typeQuestionBank = 1 order by s.createDate");
		List<TESet> sets2 = db.list("select s from TESet s where s.typeQuestionBank <> 1 order by s.createDate");
		
		sets.addAll(sets1);
		sets.addAll(sets2);
		
		context.put("sets", sets);
	}
	
	@Command("newSet")
	public String newSet() {
		context.remove("set");
		return path + "/set_input.vm";
	}
	
	@Command("editSet")
	public String editSet() throws Exception {
		TESet set = db.find(TESet.class, getParam("setId"));
		context.put("set", set);
		return path + "/set_input.vm";
	}
	
	@Command("saveSet")
	public String saveSet() throws Exception {
		String setId = getParam("setId");
		TESet set = null;
		if ( "".equals(setId)) set = new TESet();
		else set = db.find(TESet.class, setId);
		set.setName(getParam("setName"));
		set.setDescription(getParam("setDescription"));
		try {
			set.setTypeQuestionBank("1".equals(getParam("typeQuestionBank")));
		} catch ( Exception e ) { }
		try {
			set.setTypeQuestionnare("1".equals(getParam("typeQuestionnare")));
		} catch ( Exception e ) { }
		db.begin();
		if ( "".equals(setId)) {
			set.setCreateDate(new Date());
			db.persist(set);
		} else {
			
		}
		db.commit();
		
		listSets();
		return path + "/sets.vm";
	}
	
	@Command("listQuestions")
	public String listQuestions() throws Exception {
		System.out.println("setId = " + getParam("setId"));
		TESet set = db.find(TESet.class, getParam("setId"));
		context.put("set", set);
		
		if ( set.getParts().size() > 0 ) 
			context.put("currentPart", set.getParts().get(0));
		else
			context.remove("currentPart");
		
		return path + "/listQuestions.vm";
	}
	
	@Command("addPart")
	public String addPart() throws Exception {
		TESet set = db.find(TESet.class, getParam("setId"));
		context.put("set", set);
		context.remove("part");
		return path + "/addPart.vm";
	}
	
	@Command("editPart")
	public String editPart() throws Exception {
		TESet set = db.find(TESet.class, getParam("setId"));
		context.put("set", set);
		
		TEPart part = db.find(TEPart.class, getParam("partId"));
		context.put("part", part);
		
		return path + "/addPart.vm";
	}	
	
	@Command("deletePart")
	public String deletePart() throws Exception {
		
		TEPart part = db.find(TEPart.class, getParam("partId"));
		TESet set = part.getSet();
		context.put("set", set);
		
		if ( part.getQuestions().size() == 0 ) {
		
			db.begin();
			set.getParts().remove(part);
			db.remove(part);
			db.commit();
			
			if ( set.getParts().size() > 0 ) 
				context.put("currentPart", set.getParts().get(0));
			else
				context.remove("currentPart");
			
		} else {
			context.put("currentPart", part);
			context.put("errorMessage", "Can't delete part.");
		}
		
		
		return path + "/listQuestions.vm";
	}
	
	@Command("savePart")
	public String savePart() throws Exception {
		
		TESet set = db.find(TESet.class, getParam("setId"));
		context.put("set", set);		
		
		String partId = getParam("partId");
		TEPart part = null;
		if ( "".equals(partId)) {
			Integer maxSeq = (Integer) db.get("select max(p.sequence) from TEPart p where p.set.id = '" + set.getId() + "'");
			int seq = maxSeq != null ? maxSeq.intValue() + 1 : 1;
			part = new TEPart();
			part.setSequence(seq);
		}
		else part = db.find(TEPart.class, partId);
		
		part.setTitle(getParam("title"));
		part.setInstructionText(getParam("instructionText"));
		part.setSet(set);
		
		db.begin();
		if ( "".equals(partId)) {
			set.getParts().add(part);
		}
		db.commit();
		
		context.put("currentPart", part);
		
		return path + "/listQuestions.vm";
	}
	
	@Command("addQuestion")
	public String addQuestion() throws Exception {
		String partId = getParam("partId");
		TEPart part = db.find(TEPart.class, partId);
		context.remove("partQuestion");
		context.put("part", part);
		context.put("set", part.getSet());
		context.put("likerts", db.list("select l from TELikert l order by l.id"));

		List<TEPartQuestion> partQuestions = db.list("select q from TEPartQuestion q where q.part.set.id = '" + part.getSet().getId() + "' order by q.part.sequence, q.sequence");
		context.put("partQuestions", partQuestions);
		
		
		if ( part.getSet().getTypeQuestionnare()) {
			List<TEPartQuestion> questionBanks = db.list("select q from TEPartQuestion q where q.part.set.typeQuestionBank = 1 and q.part.set.typeQuestionnare = 1 order by q.part.sequence, q.sequence");
			context.put("questionBanks", questionBanks);
		} else {
			List<TEPartQuestion> questionBanks = db.list("select q from TEPartQuestion q where q.part.set.typeQuestionBank = 1 and q.part.set.typeQuestionnare = 0 order by q.part.sequence, q.sequence");
			context.put("questionBanks", questionBanks);
		}
		
		
		return path + "/question_input.vm";
	}
	
	@Command("selectQuestion")
	public String selectQuestion() throws Exception {
		String partId = getParam("partId");
		TEPart part = db.find(TEPart.class, partId);
		TESet set = part.getSet();
		context.put("part", part);
		context.put("set", set);
		String questionId = getParam("questionId");
		TEQuestion question = db.find(TEQuestion.class, questionId);
		
		TEPartQuestion partQuestion = new TEPartQuestion();
		partQuestion.setQuestion(question);
		
		context.put("partQuestion", partQuestion);
		context.put("likerts", db.list("select l from TELikert l order by l.id"));

		return path + "/question_input2.vm";
	}
	
	@Command("editQuestion")
	public String editQuestion() throws Exception {
		String partQuestionId = getParam("partQuestionId");
		TEPartQuestion partQuestion = db.find(TEPartQuestion.class, partQuestionId);
		TEPart part = partQuestion.getPart();
		context.put("partQuestion", partQuestion);
		context.put("part", part);
		context.put("set", part.getSet());
		context.put("likerts", db.list("select l from TELikert l order by l.id"));
	
		List<TEPartQuestion> partQuestions = db.list("select q from TEPartQuestion q where q.part.set.id = '" + part.getSet().getId() + "' order by q.part.sequence, q.sequence");
		context.put("partQuestions", partQuestions);		
		
		return path + "/question_input.vm";
	}	
	
	@Command("moveQuestion")
	public String moveQuestion() throws Exception {
		String partQuestionId = getParam("partQuestionId");
		TEPartQuestion partQuestion = db.find(TEPartQuestion.class, partQuestionId);
		TEPart part = partQuestion.getPart();
		context.put("partQuestion", partQuestion);
		context.put("part", part);
		context.put("set", part.getSet());
		
		List<TEPartQuestion> partQuestions = db.list("select q from TEPartQuestion q where q.part.id = '" + part.getId() + "' order by q.part.sequence, q.sequence");
		context.put("partQuestions", partQuestions);
		
		return path + "/moveQuestion.vm";
	}
	
	@Command("saveMoveQuestion")
	public String saveMoveQuestion() throws Exception {
		String partQuestionId = getParam("partQuestionId");
		TEPartQuestion partQuestion = db.find(TEPartQuestion.class, partQuestionId);
		context.put("partQuestion", partQuestion);
		context.put("set", partQuestion.getPart().getSet());
		
		String position = getParam("position");
		String moveId = getParam("moveId");
		TEPartQuestion partQuestion2 = db.find(TEPartQuestion.class, moveId);
		
		List<TEPartQuestion> tmps = new ArrayList<TEPartQuestion>();
		if ( partQuestion.getPart().getId().equals(partQuestion2.getPart().getId()) ) {
			TEPart part = partQuestion.getPart();
			List<TEPartQuestion> qs =  part.getQuestions();
			for ( TEPartQuestion q : qs ) {
				if ( !q.getId().equals(partQuestion.getId())) {
					tmps.add(q);
				}
			}

			int i = 0;
			if ( "before".equals(position)) {
				for ( TEPartQuestion q : tmps ) {
					if ( q.getId().equals(moveId)) {
						break;
					}
					i++;
				}
				tmps.add(i, partQuestion);
			} else if ( "after".equals(position)) {
				for ( TEPartQuestion q : tmps ) {
					i++;
					if ( q.getId().equals(moveId)) {
						break;
					}
				}
				tmps.add(i, partQuestion);
			}
			
			int c = 0;
			int partNo = part.getSequence();
			
			for ( TEPartQuestion q : tmps ) {
				db.begin();
				c++;
				String refNo = partNo + "." + c;
				q.setRefNo(refNo);
				q.setSequence(c);
				db.commit();
			}
		}
		
		context.put("currentPart", partQuestion.getPart());

		return path + "/listQuestions.vm";
	}
	
	@Command("saveQuestion")
	public String saveQuestion() throws Exception {
		String partId = getParam("partId");
		TEPart part = db.find(TEPart.class, partId);
		context.put("part", part);
		
		String partQuestionId = getParam("partQuestionId");
		TEPartQuestion partQuestion = null;
		TEQuestion question = null;
		
		String questionId = getParam("questionId");
		if ( "".equals(questionId)) {
			question = new TEQuestion();
			question.setCategory(null);
		} else {
			question = db.find(TEQuestion.class, questionId);
		}
		
		if ( "".equals(partQuestionId)) {
			Integer maxSeq = (Integer) db.get("select max(q.sequence) from TEPartQuestion q where q.part.id = '" + part.getId() + "'");
			int seq = maxSeq != null ? maxSeq.intValue() + 1 : 1;
			partQuestion = new TEPartQuestion();
			partQuestion.setPart(part);
			partQuestion.setSequence(seq);
			String refNo = part.getSequence() + "." + seq;
			partQuestion.setRefNo(refNo);
			partQuestion.setQuestion(question);
			part.getQuestions().add(partQuestion);
		}
		else {
			partQuestion = db.find(TEPartQuestion.class, partQuestionId);
			question = partQuestion.getQuestion();
		}
		
		TELikert likert = db.find(TELikert.class, getParam("likertId"));
		
		question.setQuestionText(getParam("questionText"));
		question.setQuestionText2(getParam("questionText2"));
		question.setType(getParam("type"));
		question.setLikert(likert);
		question.setChoice1(getParam("choice1"));
		question.setChoice2(getParam("choice2"));
		question.setChoice3(getParam("choice3"));
		question.setChoice4(getParam("choice4"));
		question.setChoice5(getParam("choice5"));
		
		question.setCorrect1(getParam("correct1").equals("1"));
		question.setCorrect2(getParam("correct2").equals("1"));
		question.setCorrect3(getParam("correct3").equals("1"));
		question.setCorrect4(getParam("correct4").equals("1"));
		question.setCorrect5(getParam("correct5").equals("1"));
		
		if ( !"".equals(getParam("nextQuestionId1")) ) 
			partQuestion.setNextQuestion1(db.find(TEPartQuestion.class, getParam("nextQuestionId1")));
		else 
			partQuestion.setNextQuestion1(null);
		
		if ( !"".equals(getParam("nextQuestionId2")) ) 
			partQuestion.setNextQuestion2(db.find(TEPartQuestion.class, getParam("nextQuestionId2")));
		else 
			partQuestion.setNextQuestion2(null);
		
		if ( !"".equals(getParam("nextQuestionId3")) ) 
			partQuestion.setNextQuestion3(db.find(TEPartQuestion.class, getParam("nextQuestionId3")));
		else 
			partQuestion.setNextQuestion3(null);
		
		if ( !"".equals(getParam("nextQuestionId4")) ) 
			partQuestion.setNextQuestion4(db.find(TEPartQuestion.class, getParam("nextQuestionId4")));
		else 
			partQuestion.setNextQuestion4(null);
		
		if ( !"".equals(getParam("nextQuestionId5")) ) 
			partQuestion.setNextQuestion5(db.find(TEPartQuestion.class, getParam("nextQuestionId5")));
		else 
			partQuestion.setNextQuestion5(null);
		
		int required = 0;
		try {
			required = Integer.parseInt(getParam("required"));
		} catch ( Exception e ) { }
		int minimumChars = 0;
		try {
			minimumChars = !"".equals(getParam("minimumChars")) ? Integer.parseInt(getParam("minimumChars")) : 0;
		} catch ( Exception e ) { }
		
		partQuestion.setRequired(required == 1);
		partQuestion.setMinimumChars(minimumChars);
		
		
		db.begin();
		if ( "".equals(partQuestionId)) {
			db.persist(partQuestion);
		}
		db.commit();
		
		//put question in question bank if created here
		TECategory category = question.getCategory();
		if ( category == null ) {
			//look for category with id value same as this set id
			String setId = part.getSet().getId();
			category = db.find(TECategory.class, setId);
			if ( category == null ) {
				category = new TECategory();
				category.setId(setId);
				category.setName(part.getSet().getName());
				category.setDescription(part.getSet().getDescription());
				db.begin();
				db.persist(category);
				db.commit();
			}
			db.begin();
			question.setCategory(category);
			db.commit();
		}
		
		
		return path + "/listPartQuestions.vm";
	}
	
	@Command("deleteQuestion")
	public String deleteQuestion() throws Exception {
		String partQuestionId = getParam("partQuestionId");
		TEPartQuestion partQuestion = db.find(TEPartQuestion.class, partQuestionId);
		TEPart part = partQuestion.getPart();
		context.put("part", part);
		
		db.begin();
		part.getQuestions().remove(partQuestion);
		db.remove(partQuestion);
		db.commit();
		
		return path + "/listPartQuestions.vm";
	}
	
	@Command("testRun")
	public String testRun() throws Exception {
		String setId = getParam("setId");
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);
		return path + "/testRun.vm";
	}
	
}
