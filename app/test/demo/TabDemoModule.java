package demo;

import javax.servlet.http.HttpSession;

import lebah.portal.action.AjaxModule;

public class TabDemoModule extends AjaxModule {
	
	String path = "apps/tab_demo/";
	private String vm = path + "default.vm";
	HttpSession session;

	
	@Override
	public String doAction() throws Exception {
		String command = request.getParameter("command");
		if ( command == null || "".equals(command)) start();
		else if ( "perjanjian_info".equals(command)) perjanjianInfo();
		return vm;
	}

	private void perjanjianInfo() {
		vm = path + "perjanjian_info.vm";
		
	}

	private void start() {
		vm = path + "demo.vm";
		
	}

}
