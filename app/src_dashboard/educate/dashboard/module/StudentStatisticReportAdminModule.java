package educate.dashboard.module;

public class StudentStatisticReportAdminModule extends StudentStatisticReportModule {
	
	public void preProcess() {
		isAdmin = true;
		super.preProcess();
	}

}
