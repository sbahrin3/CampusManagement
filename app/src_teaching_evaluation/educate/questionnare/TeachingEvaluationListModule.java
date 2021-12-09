package educate.questionnare;

import java.util.List;

import educate.db.DbPersistence;
import educate.questionnare.entity.TeachingEvaluation;
import lebah.portal.action.LebahModule;

public class TeachingEvaluationListModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "teaching_evaluation/list";

	@Override
	public String start() {
		List<TeachingEvaluation> list = db.list("select t from TeachingEvaluation t order by t.createDate");
		context.put("list", list);
		return path + "/start.vm";
	}

}
