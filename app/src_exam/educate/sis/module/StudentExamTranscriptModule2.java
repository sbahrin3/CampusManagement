package educate.sis.module;

public class StudentExamTranscriptModule2 extends StudentExamTranscriptModule {
	
	
	@Override
	public String doAction() throws Exception {
		studentMode = true;
		return super.doAction();
		
	}

}
