package educate.enrollment.report;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.DeferStudy;
import educate.enrollment.entity.StdWithdrawApp;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.SessionPath;
import lebah.portal.action.AjaxModule;

public class DeferStudyModule extends AjaxModule {
	
	private final String path = "apps/util/defer_study/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		context.put("dateFormat", dateFormat);
		String command = request.getParameter("command");
		vm = path + "select_session.vm";
		if ( command == null || "".equals(command)) selectSession();
		else if ( "list_defer_studies".equals(command)) listDeferStudies();
		else if ( "list_study_withdrawals".equals(command)) listStudyWithdrawals();
		
		return vm;
	}
	
	private void listStudyWithdrawals() {
		vm = path + "list_students.vm";
		String sessionId = request.getParameter("session_id");
		Session studySession = db.find(Session.class, sessionId);
		Hashtable h = new Hashtable();
		h.put("startDate", studySession.getStartDate());
		h.put("endDate", studySession.getEndDate());

		String sql = "select ds from StdWithdrawApp ds " +
				"WHERE ds.reqDate BETWEEN :startDate AND :endDate";
		List<StdWithdrawApp> list = db.list(sql, h);
		context.put("appList", list);
	}

	private void listDeferStudies() {
		vm = path + "list_students.vm";
		String sessionId = request.getParameter("session_id");
		Session studySession = db.find(Session.class, sessionId);
		Hashtable h = new Hashtable();
		h.put("startDate", studySession.getStartDate());
		h.put("endDate", studySession.getEndDate());

		String sql = "select ds from DeferStudy ds " +
				"WHERE ds.reqDate BETWEEN :startDate AND :endDate";
		List<DeferStudy> list = db.list(sql, h);
		context.put("appList", list);
		
	}

	private void selectSession() {
		vm = path + "select_session.vm";
		context.put("paths", getPathSet());
	}

	private Set<Path> getPathSet() {
		Set<Path> pathSet = new LinkedHashSet<Path>();
		String sql = "";
		DbPersistence db = new DbPersistence();
		sql = "select p from SessionPath p order by p.pathNo";
		List<SessionPath> paths = db.list(sql);
		for ( SessionPath p : paths ) {
			Path path = new Path();
			path.setSessionPath(p);
			sql = "select s from Session s where s.pathNo = " + p.getPathNo() + " order by s.startDate";
			List<Session> sessions = db.list(sql);
			for ( Session s : sessions ) path.add(s);
			pathSet.add(path);
		}
		return pathSet;
	}
	
	public static void main(String[] args) {
		DbPersistence db = new DbPersistence();
		String sql = "select d from DeferStudy d ";
	}

}
