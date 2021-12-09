package test;

import lebah.portal.action.AjaxModule;

public class TestModule  extends AjaxModule {
	
	String path = "test/";
	String vm = "";

	
	@Override
	public String doAction() throws Exception {
		
		return vm;
	}

}
