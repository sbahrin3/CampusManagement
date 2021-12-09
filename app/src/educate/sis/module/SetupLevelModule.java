package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.general.entity.GradsLevel;
import lebah.portal.action.AjaxModule;

public class SetupLevelModule extends AjaxModule {
	
	String path = "apps/util/setups/level/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "add".equals(command)) add();
		else if ( "delete".equals(command)) delete();
		else if ( "update_code".equals(command)) updateCode();
		else if ( "update_name".equals(command)) updateName();
		else if ( "update_path".equals(command)) updatePath();
		else if ( "sort".equals(command)) sort();
		return vm;
	}

	private void updatePath() throws Exception {
		String levelId = request.getParameter("level_id");
		GradsLevel GradsLevel = db.find(GradsLevel.class, levelId);
		
		try {
			int pathNo = Integer.parseInt(request.getParameter("path_" + levelId));
			
			db.begin();
			GradsLevel.setPathNo(pathNo);
			db.commit();
			
		} catch ( Exception e ) {}
		
		vm = path + "empty.vm";
		
	}

	private void sort() throws Exception {
		String[] ids = request.getParameterValues("level_ids");
		int i = 0;
		for ( String id : ids ) {
			GradsLevel gradsLevel = db.find(GradsLevel.class, id);
			
			db.begin();
			gradsLevel.setLevelOrder(i++);
			db.commit();
			
		}
		
		list();
		
		vm = path + "list_levels.vm";
		
	}

	private void delete() throws Exception {
		String gradsLevelId = request.getParameter("level_id");
		GradsLevel gradsLevel = db.find(GradsLevel.class, gradsLevelId);
		
		//
		long count  = (Long) db.get("select count(p) from Program p where p.level.id = '" + gradsLevel.getId() + "'");
		if ( count == 0 ) {
			db.begin();
			db.remove(gradsLevel);
			db.commit();
		}
		else {
			context.put("error", "Can't delete GradsLevel " + gradsLevel.getCode() + " " + gradsLevel.getName());
		}
		list();
		
		vm = path + "list_levels.vm";
		
	}

	private void add() throws Exception {
		
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		String pathNo = request.getParameter("path_no");
		
		int order = 0;
		try {
			order = (Integer) db.get("select max(g.levelOrder) from GradsLevel g");
		} catch ( java.lang.NullPointerException e ) {}
		
		db.begin();
		GradsLevel gradsLevel = new GradsLevel();
		gradsLevel.setCode(code);
		gradsLevel.setName(name);
		gradsLevel.setLevelOrder(order);
		try {
			gradsLevel.setPathNo(Integer.parseInt(pathNo));
		} catch ( Exception e ) {
			gradsLevel.setPathNo(0);
		}
		db.persist(gradsLevel);
		db.commit();
		
		list();
		
		vm = path + "list_levels.vm";
		
	}
	
	private void updateCode() throws Exception {
		String levelId = request.getParameter("level_id");
		GradsLevel GradsLevel = db.find(GradsLevel.class, levelId);
		
		String code = request.getParameter("code_" + levelId);
		
		db.begin();
		GradsLevel.setCode(code);
		db.commit();
		
		vm = path + "empty.vm";
		
	}
	
	private void updateName() throws Exception {
		String levelId = request.getParameter("level_id");
		GradsLevel GradsLevel = db.find(GradsLevel.class, levelId);
		
		String name = request.getParameter("name_" + levelId);
		
		db.begin();
		GradsLevel.setName(name);
		db.commit();
		
		vm = path + "empty.vm";
		
	}



	private void start() {
		list();
		
		vm = path + "start.vm";
		
	}

	private void list() {
		
		String sql = "select l from GradsLevel l order by l.levelOrder";
		List<GradsLevel> levels = db.list(sql);
		context.put("levels", levels);
	}

}
