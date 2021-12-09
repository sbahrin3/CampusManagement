package demo;

import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class HelloModule extends LebahModule {

	@Override
	public String start() {
		// TODO Auto-generated method stub
		return "hello/start.vm";
	}
	
	@Command("sayHello")
	public String sayHello() {
		context.put("name", getParam("yourName"));
		return "hello/start.vm";
	}

}
