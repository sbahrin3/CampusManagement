package educate.questionnare;

import java.util.List;

import educate.db.DbPersistence;
import educate.questionnare.entity.TECategory;
import educate.questionnare.entity.TELikert;
import educate.questionnare.entity.TEQuestion;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ManageQuestionModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "questionnare/manage";
	
	public void preProcess() {
		context.remove("errorMessage");
	}

	@Override
	public String start() {
		//list of categories
		List<TECategory> categories = db.list("select c from TECategory c order by c.name");
		context.put("categories", categories);
		return path + "/start.vm";
	}
	
	@Command("newCategory")
	public String newCategory() throws Exception {
		
		return path + "/category_input.vm";
	}
	
	@Command("editCategory")
	public String editCategory() throws Exception {
		String categoryId = getParam("categoryId");
		TECategory c = db.find(TECategory.class, categoryId);
		context.put("category", c);
		return path + "/category_input.vm";
	}
	
	@Command("saveCategory")
	public String saveCategory() throws Exception {
		String categoryId = getParam("categoryId");
		TECategory c = null;
		if ( "".equals(categoryId)) c = new TECategory();
		else c = db.find(TECategory.class, categoryId);
		
		c.setName(getParam("categoryName"));
		c.setDescription(getParam("categoryDescription"));
		db.begin();
		if ( "".equals(categoryId)) db.persist(c);
		db.commit();
		List<TECategory> categories = db.list("select c from TECategory c order by c.name");
		context.put("categories", categories);
		return path + "/categories.vm";
	}
	
	@Command("deleteCategory")
	public String deleteCategory() throws Exception {
		String categoryId = getParam("categoryId");
		TECategory c = db.find(TECategory.class, categoryId);
		db.begin();
		db.remove(c);
		db.commit();
		List<TECategory> categories = db.list("select c from TECategory c order by c.name");
		context.put("categories", categories);
		return path + "/categories.vm";
	}
	
	@Command("listQuestions")
	public String listQuestions() throws Exception {
		String categoryId = getParam("categoryId");
		TECategory category = db.find(TECategory.class, categoryId);
		context.put("category", category);
		
		List<TEQuestion> questions = db.list("select q from TEQuestion q where q.category.id = '" + categoryId + "'");
		context.put("questions", questions);
		
		return path + "/listQuestions.vm";
	}
	
	@Command("newQuestion")
	public String newQuestion() {
		context.put("category", db.find(TECategory.class, getParam("categoryId")));
		//get likerts
		context.put("likerts", db.list("select l from TELikert l order by l.id"));
		return path + "/question_input.vm";
	}
	
	@Command("editQuestion")
	public String editQuestion() throws Exception {
		String questionId = getParam("questionId");
		TEQuestion question = db.find(TEQuestion.class, questionId);
		context.put("question", question);
		context.put("category", question.getCategory());
		
		//get likerts
		context.put("likerts", db.list("select l from TELikert l order by l.id"));
		return path + "/question_input.vm";
	}
	
	@Command("deleteQuestion")
	public String deleteQuestion() throws Exception {
		String questionId = getParam("questionId");
		TEQuestion question = db.find(TEQuestion.class, questionId);
		TECategory category = question.getCategory();
		context.put("category", category);
		
		Long count = (Long) db.get("select count(p) from TEPartQuestion p where p.question.id = '" + questionId + "'");
		long questionCount = count != null ? count.longValue() : 0;
		
		if ( questionCount == 0 ) {
			db.begin();
			db.remove(question);
			db.commit();
			context.remove("errorMessage");
		}
		else context.put("errorMessage", "Can't delete.  Question is in use."); 
		
		List<TEQuestion> questions = db.list("select q from TEQuestion q where q.category.id = '" + category.getId() + "'");
		context.put("questions", questions);
		return path + "/listQuestions.vm";
	}

	@Command("saveQuestion")
	public String saveQuestion() throws Exception {
		String questionId = getParam("questionId");
		TEQuestion question = null;
		if ( "".equals(questionId) ) {
			question = new TEQuestion();
			String categoryId = getParam("categoryId");
			TECategory category = db.find(TECategory.class, categoryId);
			question.setCategory(category);
		}
		else question = db.find(TEQuestion.class, questionId);
		
		question.setQuestionText(getParam("questionText"));
		question.setQuestionText2(getParam("questionText2"));
		question.setType(getParam("type"));
		question.setChoice1(getParam("choice1"));
		question.setChoice2(getParam("choice2"));
		question.setChoice3(getParam("choice3"));
		question.setChoice4(getParam("choice4"));
		question.setChoice5(getParam("choice5"));

		if ( "likert".equals(getParam("type"))) {
			TELikert likert = db.find(TELikert.class, Integer.parseInt(getParam("likertId")));
			question.setLikert(likert);
		}
		
		db.begin();
		if ( "".equals(questionId)) db.persist(question);
		db.commit();
		return listQuestions();
	}
}
