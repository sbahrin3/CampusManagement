package educate.questionnare;

import java.util.Date;

import educate.db.DbPersistence;
import educate.questionnare.entity.TeachingEvaluation;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class TeachingEvaluationModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "teaching_evaluation";

	@Override
	public String start() {
		return path + "/start.vm";
	}
	
	@Command("saveQuestionnare")
	public String saveQuestionnare() throws Exception {
		String q1 = getParam("q1");
		System.out.println(q1);
		return path + "/save.vm";
	}
	
	@Command("save")
	public String save() throws Exception {
		TeachingEvaluation te = new TeachingEvaluation();
		update(te);
		
		db.begin();
		te.setCreateDate(new Date());
		db.persist(te);
		db.commit();
		
		return path + "/save.vm";
	}
	
	@Command("update")
	public String update() throws Exception {
		TeachingEvaluation te = db.find(TeachingEvaluation.class, getParam("teachingEvaluationId"));
		update(te);
		
		db.begin();
		db.commit();
		
		return path + "/save.vm";
	}	
	
	@Command("getQuestionare")
	public String getQuestionare() throws Exception {
		TeachingEvaluation te = db.find(TeachingEvaluation.class, getParam("teachingEvaluationId"));
		context.put("te", te);
		return path + "/questionare.vm";
	}

	private void update(TeachingEvaluation te) {
		te.setFacultyName(getParam("facultyName"));
		te.setCodeName(getParam("codeName"));
		te.setCourseName(getParam("courseName"));
		te.setSemesterYear(getParam("semesterYear"));
		te.setQ1(getParamAsInteger("q1"));
		te.setQ2(getParamAsInteger("q2"));
		te.setQ3(getParamAsInteger("q3"));
		te.setQ4(getParamAsInteger("q4"));
		te.setQ5(getParamAsInteger("q5"));
		te.setQ6(getParamAsInteger("q6"));
		te.setQ7(getParamAsInteger("q7"));
		te.setQ8(getParamAsInteger("q8"));
		te.setQ9(getParamAsInteger("q9"));
		te.setQ10(getParamAsInteger("q10"));
		te.setQ11(getParamAsInteger("q11"));
		te.setQ12(getParamAsInteger("q12"));
		te.setQ13(getParamAsInteger("q13"));
		te.setQ14(getParamAsInteger("q14"));
		te.setQ15(getParamAsInteger("q15"));
		te.setQ16(getParamAsInteger("q16"));
		te.setQ17(getParamAsInteger("q17"));
		te.setQ18(getParamAsInteger("q18"));
		te.setQ19(getParamAsInteger("q19"));
		te.setQ20(getParamAsInteger("q20"));
		te.setQ21(getParamAsInteger("q21"));
		te.setQ22(getParamAsInteger("q22"));
		te.setQ23(getParamAsInteger("q23"));
		te.setQ24(getParamAsInteger("q24"));
		te.setOverallRating(getParamAsInteger("overallRating"));
		te.setComment1(getParam("comment1"));
		te.setComment2(getParam("comment2"));
		te.setComment3(getParam("comment3"));
	}
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
	}

}
