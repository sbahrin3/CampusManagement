package educate.sis.module;

import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.exam.entity.SubjectResultStatus;
import lebah.portal.action.AjaxModule;

public class SetupSubjectResultStatusModule extends AjaxModule {
	
	String path = "apps/util/mark_entry/setup_result_status/";
	private String vm = "start.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	@Override
	public String doAction() throws Exception {
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "add_status".equals(command)) addStatus();
		else if ( "delete_status".equals(command)) deleteStatus();
		else if ( "edit_status".equals(command)) editStatus();
		else if ( "update_status".equals(command)) updateStatus();
		else if ( "cancel_edit".equals(command)) cancelEdit();

		return vm;
	}

	private void cancelEdit() {
		
		vm = path + "add_status.vm";
		
	}

	private void updateStatus() throws Exception {
		String id = request.getParameter("id");
		SubjectResultStatus status = db.find(SubjectResultStatus.class, id);
		
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		String exclude = request.getParameter("exclude_cgpa");
		String resetMark = request.getParameter("reset_mark");
		
		if ( status != null ) {
			db.begin();
			status.setCode(code);
			status.setName(name);
			status.setExcludeGPA("yes".equals(exclude) ? true : false);
			status.setResetMark("yes".equals(resetMark) ? true : false);
			db.commit();
		}
		
		List<SubjectResultStatus> statuses = db.list("select s from SubjectResultStatus s");
		context.put("statuses", statuses);
		
		vm = path + "statuses.vm";
	}

	private void editStatus() {
		String id = request.getParameter("id");
		SubjectResultStatus status = db.find(SubjectResultStatus.class, id);
		context.put("status", status);
		vm = path + "edit_status.vm";
	}

	private void deleteStatus() throws Exception {
		
		String id = request.getParameter("id");
		SubjectResultStatus status = db.find(SubjectResultStatus.class, id);
		
		db.begin();
		db.remove(status);
		db.commit();
		
		List<SubjectResultStatus> statuses = db.list("select s from SubjectResultStatus s");
		context.put("statuses", statuses);
		
		vm = path + "statuses.vm";
		
	}

	private void addStatus() throws Exception {
		
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		String exclude = request.getParameter("exclude_cgpa");
		String resetMark = request.getParameter("reset_mark");
		
		SubjectResultStatus status = db.find(SubjectResultStatus.class, code);
		if ( status == null ) {
			db.begin();
			status = new SubjectResultStatus(code, name, "yes".equals(exclude) ? true : false, "yes".equals(resetMark) ? true : false);
			db.persist(status);
			db.commit();
		}
		
		List<SubjectResultStatus> statuses = db.list("select s from SubjectResultStatus s");
		context.put("statuses", statuses);
		
		vm = path + "statuses.vm";
		
	}

	private void start() {
		//
		
		List<SubjectResultStatus> statuses = db.list("select s from SubjectResultStatus s");
		context.put("statuses", statuses);
		
		vm = path + "start.vm";
		
	}

}
