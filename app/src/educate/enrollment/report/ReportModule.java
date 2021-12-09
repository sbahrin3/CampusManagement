package educate.enrollment.report;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.SessionPath;
import lebah.portal.action.AjaxModule;

public class ReportModule  extends AjaxModule {
	
	private final String path = "apps/util/report/";
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
		
		if ( null == command || "".equals(command)) doReport();
		if ( "list_programs".equals(command)) doReport();
		return vm;
	}

	private void doReport() {
		vm = path + "report.vm";
		String sql = "";
		sql = "select sp from SessionPath sp order by sp.pathNo";
		List<SessionPath> paths  = db.list(sql);
		context.put("paths", paths);
		
		String pathId = request.getParameter("path_id");
		if ( null != pathId && !"".equals(pathId)) {
			SessionPath path = db.find(SessionPath.class, pathId);
			Hashtable param = new Hashtable();
			param.put("path_no", path.getPathNo());
			sql = "select p from Program p where p.level.pathNo = :path_no";
			List<Program> programs = db.list(sql, param);
			context.put("programs", programs);
			context.put("path", path);
		}
		else {
			context.remove("programs");
			context.remove("path");
		}
		
	}
	
	public static void main(String[] args) {
		DbPersistence db = new DbPersistence();
		String sql = "";
		sql = "select sp from SessionPath sp order by sp.pathNo";
		List<SessionPath> paths  = db.list(sql);
		for ( SessionPath path : paths ) {
			System.out.println(path.getName() + ", " + path.getPathNo());
		}
		
	}

}
