package educate.chat;

import lebah.portal.action.AjaxModule;

public class CometModule extends AjaxModule {
	
	String path = "vtl/chat/";
	String vm = "";

	
	@Override
	public String doAction() throws Exception {
		vm = path + "comet.vm";
		return vm;
	}

}
