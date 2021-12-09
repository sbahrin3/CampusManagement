package educate.sis.module;

import lebah.portal.action.Command;

public class CheckApplicantStatusByAdminModule extends CheckApplicantStatusModule {
	
	@Override
	public void preProcess() {
		byAdmin = true;
		super.preProcess();
	}
	
	@Override
	@Command("print_letter")
	public String printLetter() {
		return super.printLetter();
	}
	
	@Override
	@Command("check_status")
	public String getApplicantStatus() {
		return super.getApplicantStatus();
	}

}
