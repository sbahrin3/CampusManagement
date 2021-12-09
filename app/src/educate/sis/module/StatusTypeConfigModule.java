package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.StatusType;
import lebah.portal.action.AjaxModule;

public class StatusTypeConfigModule  extends AjaxModule {
	
	String path = "apps/util/status_config/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "set_default".equals(command)) setDefault();
		else if ( "set_defer".equals(command)) setDefer();
		else if ( "set_quit".equals(command)) setQuit();
		else if ( "update_sequence".equals(command)) updateSequence();
		else if ( "set_inactive".equals(command)) setInactive();
		else if ( "save_order".equals(command)) saveOrder();
		else if ( "add_status".equals(command)) addStatus();
		else if ( "update_name".equals(command)) updateName();
		else if ( "delete_status".equals(command)) deleteStatus();
		return vm;
	}

	private void deleteStatus() throws Exception {
		String typeId = request.getParameter("type_id");
		StatusType type = db.find(StatusType.class, typeId);
		
		if ( type.getCanDelete() ) {
		
			List<Long> list = db.list("select count(s) from educate.enrollment.entity.StudentStatus s where s.type.id = '" + typeId + "'");
			long len = list.get(0);
			
			if ( len == 0 ) {
				db.begin();
				db.remove(type);
				db.commit();
			}
			else {
				System.out.println("Status deletion failed because status type in use.");
			}
			
		}
		
		list();
	}

	private void updateName() throws Exception {
		String typeId = request.getParameter("type_id");
		StatusType type = db.find(StatusType.class, typeId);
		
		String name = request.getParameter("name_" + typeId);
		
		db.begin();
		type.setName(name);
		db.commit();
		
		list();
		
	}

	private void addStatus() throws Exception {
		
		int len = db.list("select s from StatusType s").size();
		
		String code = request.getParameter("status_code");
		String name = request.getParameter("status_name");
		
		db.begin();
		StatusType type = new StatusType();
		type.setCategory("");
		type.setCode(code);
		type.setName(name);
		type.setSequence(++len);
		db.persist(type);
		db.commit();
		
		list();
		
	}

	private void saveOrder() throws Exception {
		String[] typeIds = request.getParameterValues("type_ids");
		int i = 0;
		for ( String typeId : typeIds ) {
			StatusType type = db.find(StatusType.class, typeId);
			db.begin();
			type.setSequence(i);
			db.commit();
			i++;
		}
		
		list();
		
	}

	private void setInactive() throws Exception {
		String typeId = request.getParameter("type_id");
		StatusType type = db.find(StatusType.class, typeId);
		db.begin();
		if ( type.getInActive() ) type.setInActive(false);
		else type.setInActive(true);
		db.commit();
		list();
		
	}

	private void updateSequence() throws Exception {
		String typeId = request.getParameter("type_id");
		StatusType type = db.find(StatusType.class, typeId);
		String sequence = request.getParameter("sequence_" + typeId);
		int _sequence = 0;
		try {
			_sequence = Integer.parseInt(sequence);
		} catch ( Exception e ) {}
		db.begin();
		type.setSequence(_sequence);
		db.commit();
		
		list();
		
	}

	private void setDefer()  throws Exception {
		String typeId = request.getParameter("type_id");
		StatusType type = db.find(StatusType.class, typeId);
		db.begin();
		if ( type.getDefer() ) type.setDefer(false);
		else type.setDefer(true);
		db.commit();
		
		list();
	}
	
	private void setQuit()  throws Exception {
		String typeId = request.getParameter("type_id");
		StatusType type = db.find(StatusType.class, typeId);
		db.begin();
		if ( type.getQuit() ) type.setQuit(false);
		else type.setQuit(true);
		db.commit();
		
		list();
	}

	private void setDefault() throws Exception {
		String typeId = request.getParameter("type_id");
		//I DON'T KNOW UPDATE STATEMENT FOR NOW....
		List<StatusType> types = db.list("select s from StatusType s order by s.sequence");
		db.begin();
		for ( StatusType type : types ) {
			if ( type.getId().equals(typeId)) type.setDefault(true);
			else type.setDefault(false);
		}
		db.commit();
		list();
	}
	
	private void list() {
		vm = path + "config.vm";
		List<StatusType> types = db.list("select s from StatusType s order by s.sequence");
		context.put("types", types);
	}

	private void start() {
		vm = path + "start.vm";
		try {
			StatusType active = db.find(StatusType.class, "active");
			if ( active == null ) {
				db.begin();
				active = new StatusType();
				active.setId("active");
				active.setCode("active");
				active.setName("ACTIVE");
				active.setCategory("");
				active.setDefault(true);
				active.setDefer(false);
				active.setInActive(false);
				active.setSequence(1);
				active.setQuit(false);
				db.persist(active);
				db.commit();
			}
			StatusType defer = db.find(StatusType.class, "deferred");
			if ( defer == null ) {
				db.begin();
				defer = new StatusType();
				defer.setId("deferred");
				defer.setCode("deferred");
				defer.setName("DEFERRED");
				defer.setCategory("");
				defer.setDefault(false);
				defer.setDefer(true);
				defer.setInActive(true);
				defer.setSequence(2);
				defer.setQuit(false);
				db.persist(defer);
				db.commit();
			}
			StatusType quit = db.find(StatusType.class, "quit");
			if ( quit == null ) {
				db.begin();
				quit = new StatusType();
				quit.setId("quit");
				quit.setCode("quit");
				quit.setName("WITHDRAW");
				quit.setCategory("");
				quit.setDefault(false);
				quit.setDefer(false);
				quit.setInActive(true);
				quit.setSequence(3);
				quit.setQuit(true);
				db.persist(quit);
				db.commit();
			}
			
			if ( active.getInActive() || active.getQuit() ) {
				db.begin();
				active.setInActive(false);
				active.setQuit(false);
				db.commit();
			}
			if ( !defer.getInActive() || defer.getQuit() ) {
				db.begin();
				defer.setInActive(true);
				defer.setQuit(false);
				db.commit();
			}
			if ( !quit.getInActive() || !quit.getQuit() ) {
				db.begin();
				quit.setInActive(true);
				quit.setQuit(true);
				db.commit();
			}
			
		
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		List<StatusType> types = db.list("select s from StatusType s order by s.sequence");
		context.put("types", types);
	}

}
