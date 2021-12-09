package educate.enrollment.report;

import java.text.SimpleDateFormat;
import java.util.List;

import educate.enrollment.entity.ChProgrammeApp;
import educate.sis.struct.entity.Session;
import lebah.db.PersistenceManager;
import lebah.portal.action.AjaxModule;

public class ChangeProgSummaryReportModule extends AjaxModule {

	private final String path="apps/enrollment/chProg_sumary/";
	private String vm = path +"index.vm";
	@Override
	public String doAction() throws Exception {
		String command = getParam("command");
		

		if(command.equals("search")){
			search();
		}

		else{
			index();
			
		}
		return vm;
	}
	private void index()throws Exception{
		PersistenceManager pm = new PersistenceManager();
		List<Session> sessionList = pm.list("select a from Session a ORDER BY a.startDate");
		context.put("sessionList",sessionList);
		context.put("warning","");
		context.put("display", "");
		context.put("notice", "");
		vm=path+"view/index.vm";
	}

	private void search()throws Exception{
		PersistenceManager pm = new PersistenceManager();
		List<Session> sessionList = pm.list("select a from Session a ORDER BY a.startDate");
		context.put("sessionList",sessionList);

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		if(getParam("session_select")==""){
			context.put("warning", "Please Select a Session!");
			context.put("notice", "");
			context.put("display", "");
			vm=path+"view/index.vm";
		}else{
			
			context.put("warning", "");
			String getSessionId = getParam("session_select");
				
			Session getSession = (Session) pm.find(Session.class, getSessionId);
			String startDate = sdf.format(getSession.getStartDate());
			
			String endDate = sdf.format(getSession.getEndDate());
			
			List<ChProgrammeApp> ChProgList = pm.list("select a from ChProgrammeApp a WHERE a.reqDate BETWEEN'"+startDate+"'AND'"+endDate+"'");
			
			if(ChProgList.size()==0){
				
				context.put("display","yes");
				context.put("notice", "Empty List.");
				context.put("appList","");
				vm=path+"view/index.vm";
			}else{
				
				context.put("display","yes");
				context.put("notice", "");
				context.put("appList",ChProgList);
				vm=path+"view/index.vm";
			}
			
		}
	}
	
	

}
