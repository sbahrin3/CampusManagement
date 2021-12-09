package educate.sis.module;

public class StudentInformationModule2 extends StudentInformationModule {

	@Override
	public String doAction() throws Exception {
		studentMode = true;
		return super.doAction();
	}

}
