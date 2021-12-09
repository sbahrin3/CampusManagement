package educate.sb.roles;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lebah.db.DbException;
import lebah.portal.action.AjaxModule;

public class CopyRoleModule extends AjaxModule {
	
	String path = "sb/role_copy/";
	String vm = "";
	lebah.db.Db lebahDb = null;

	@Override
	public String doAction() throws Exception {
		String command = request.getParameter("command");
		if ( command == null || "".equals(command)) start();
		else if ( "copy_to".equals(command)) copyTo();
		return vm;
	}

	private void copyTo() throws Exception {
		
		String role_to = request.getParameter("role_to");
		String role_from = request.getParameter("role_from");
		lebah.portal.element.Role roleTo = getRole(role_to);
		context.put("role_to", roleTo);
		lebah.portal.element.Role roleFrom = getRole(role_from);
		context.put("role_from", roleFrom);
		doCopyModules(roleFrom, roleTo);
		
		vm = path + "copied.vm";
		
	}
	
	private void doCopyModules(lebah.portal.element.Role copyfrom, lebah.portal.element.Role copyto) throws Exception {
		
		lebah.db.Db lebahDb = new lebah.db.Db();
		
		//role_module
		lebah.db.SQLRenderer r = new lebah.db.SQLRenderer();
		{
			lebahDb.getStatement().executeUpdate("delete from role_module where user_role = '" + copyto.getName() + "'");
			String sql = "insert into role_module (module_id, user_role) select module_id, '" + copyto.getName() + "' from role_module where user_role = '" + copyfrom.getName() + "'";
			System.out.println(sql);
			lebahDb.getStatement().executeUpdate(sql);
		}
		
		//tab_template
		{
			lebahDb.getStatement().executeUpdate("delete from tab_template where user_login = '" + copyto.getName() + "'");
			String sql = "insert into tab_template (user_login, tab_id, tab_title, sequence, display_type, locked) ";
			sql += "SELECT '" + copyto.getName() + "', tab_id, tab_title, sequence, display_type, locked FROM tab_template where user_login = '" + copyfrom.getName() + "'";
			System.out.println(sql);
			lebahDb.getStatement().executeUpdate(sql);
		}
		
		//user_module_template
		{
			lebahDb.getStatement().executeUpdate("delete from user_module_template where user_login = '" + copyto.getName() + "'");
			String sql = "insert into user_module_template (user_login, tab_id, module_id, module_custom_title, sequence, column_number) ";
			sql += "SELECT '" + copyto.getName() + "', tab_id, module_id, module_custom_title, sequence, column_number FROM user_module_template where user_login = '" + copyfrom.getName() + "'";
			System.out.println(sql);
			lebahDb.getStatement().executeUpdate(sql);
		}
		
		
	}

	private lebah.portal.element.Role getRole(String roleName) throws DbException, SQLException {
		lebah.portal.element.Role r = new lebah.portal.element.Role();
		lebah.db.Db lebahDb = new lebah.db.Db();
		ResultSet rs = lebahDb.getStatement().executeQuery("select name, description from role where name = '" + roleName + "'");
		if ( rs.next() ) {
			r.setName(rs.getString("name"));
			r.setDescription(rs.getString("description"));
		}
		return r;
	}

	private void start() throws Exception {
		vm = path + "start.vm";
		
		//list of roles
		lebah.db.Db lebahDb = new lebah.db.Db();
		ResultSet rs = lebahDb.getStatement().executeQuery("select name, description from role order by name");
		List<lebah.portal.element.Role> portalRoles = new ArrayList<lebah.portal.element.Role>();
		context.put("portal_roles", portalRoles);
		while ( rs.next()) {
			lebah.portal.element.Role r = new lebah.portal.element.Role();
			r.setName(rs.getString("name"));
			r.setDescription(rs.getString("description"));
			portalRoles.add(r);
		}
	}

}
