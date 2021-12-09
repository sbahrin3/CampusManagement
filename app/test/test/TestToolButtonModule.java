package test;

import lebah.portal.action.ToolButtonModule;

public class TestToolButtonModule extends ToolButtonModule {
	
	String path = "vtl/test/";
	

	@Override
	public void doCommand(String command) {
		System.out.println(command);
		
	}

	@Override
	public void doEdit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doList() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doNew() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initPage() {
		vm = path + "start.vm";
		
	}

}
