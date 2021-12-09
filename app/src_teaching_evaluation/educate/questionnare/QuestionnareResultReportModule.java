package educate.questionnare;


public class QuestionnareResultReportModule extends PublishQuestionnareModule {
	
	
	public void preProcess() {
		super.preProcess();
		context.put("reportView", true);
	}
	

}
