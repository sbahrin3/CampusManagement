package test;

import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class VTestModule extends LebahModule {
	
	private String path = "test/velocity";

	@Override
	public String start() {
		return path + "/start.vm";
	}
	
	@Command("doTest")
	public String doTest() throws Exception {
		context.put("value", getParam("data"));
		return path + "/value.vm";
	}

}
