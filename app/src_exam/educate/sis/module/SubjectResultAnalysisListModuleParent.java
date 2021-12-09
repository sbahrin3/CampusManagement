package educate.sis.module;

import lebah.portal.action.LebahModule;

public class SubjectResultAnalysisListModuleParent extends LebahModule {
	
	private String path = "apps/exam_subject_analysis2";

	@Override
	public String start() {
		// TODO Auto-generated method stub
		return path + "/start_parent.vm";
	}

}
