package demo;

import lebah.portal.action.AjaxModule;

public class TestModule extends AjaxModule {
	String vm = "";

	
	@Override
	public String doAction() throws Exception {
		vm = "demo/test.vm";
		String command = request.getParameter("command");
		if ( "say_hello".equals(command)) doSayHello();
		return vm;
	}

	private void doSayHello() {
		vm = "demo/say_hello.vm";
		
		waiting(2);
		String my_name = request.getParameter("my_name");
		String greeting = request.getParameter("greeting");
		
		context.put("greeting", greeting);
		context.put("my_name", my_name);
		
	}
	
	static void waiting (int n){
		long t0, t1;
        t0 =  System.currentTimeMillis();
        do{
            t1 = System.currentTimeMillis();
        } 
        while ((t1 - t0) < (n * 1000));
    }

}
