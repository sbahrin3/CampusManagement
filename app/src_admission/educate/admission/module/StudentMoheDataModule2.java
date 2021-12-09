package educate.admission.module;


public class StudentMoheDataModule2 extends StudentMoheDataModule {
	
	public void preProcess() {
		studentMode = true;
		super.preProcess();
	}

}
