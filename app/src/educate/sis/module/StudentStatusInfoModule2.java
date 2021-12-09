package educate.sis.module;

public class StudentStatusInfoModule2 extends StudentStatusInfoModule {
	
	
	@Override
	public String doAction() throws Exception {
		studentMode = true;
		return super.doAction();
		
	}

}
