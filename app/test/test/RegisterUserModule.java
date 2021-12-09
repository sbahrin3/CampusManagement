package test;

public class RegisterUserModule extends lebah.portal.action.ToolButtonModule {
	
	String dir = "test/register/";

	
	@Override
	public void doCommand(String command) {
		if ( "list".equals(command)) {
			vm = dir + "list.vm";
		}
		
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
		vm = dir + "register_form.vm";
		
	}

	
	@Override
	public void doSave() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void initPage() {
		vm = dir + "register_form.vm";
		
	}

}
