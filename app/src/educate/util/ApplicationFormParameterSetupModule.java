package educate.util;

import lebah.portal.action.LebahModule;

public class ApplicationFormParameterSetupModule extends LebahModule {
	String path = "apps/app_param/";

	@Override
	public String start() {
		try {
			ApplicationFormParametersSetup.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path + "start.vm";
	}

}
