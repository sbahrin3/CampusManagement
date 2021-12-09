package test;

public class TestModule2 extends lebah.portal.action.ToolButtonModule {
	
	String dir = "test/";
	
	@Override
	public void doList() {
		vm = dir + "search.vm";
	}
	
	@Override
	public void initPage() {
		vm = dir + "init_page.vm";
	}
	@Override
	public void doSave() {
		
	}
	@Override
	public void doNew() {
		vm = dir + "new.vm";
	} 
	
	@Override
	public void doCommand(String command) {
		
	}

	
	@Override
	public void doEdit() {
		// TODO Auto-generated method stub
		
	}

}
