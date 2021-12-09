package educate.sis.module;

public class StudentRegisterSubjectModule2 extends StudentRegisterSubjectModule {
	
	
	@Override
	public String doAction() throws Exception {
		studentMode = true;
		return super.doAction();
	}

}
