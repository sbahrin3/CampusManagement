package portal.module;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import educate.db.DbPersistence;
import lebah.db.Db;
import lebah.db.DbException;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import portal.module.entity.AdminRole;
import portal.module.entity.AnnouncementAdmin;

public class AnnouncementAdminModule extends LebahModule {
	
	private String path = "apps/portal/announcement_admin/";
	DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		try {
			Db d = new Db();
			ResultSet rs = d.getStatement().executeQuery("select module_id, module_title from module where module_class = 'portal.module.AnnouncementModule'");
			List<Record> list = new ArrayList<Record>();
			while ( rs.next() ) {
				list.add(new Record(rs.getString(1), rs.getString(2)));
			}
			context.put("ann_modules", list);
		} catch (DbException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return path + "start.vm";
	}
	
	private List<String> getRoles() {
		try {
			Db d = new Db();
			ResultSet rs = d.getStatement().executeQuery("select name from role r order by r.name");
			List<String> roles = new ArrayList<String>();
			while ( rs.next() ) {
				roles.add(rs.getString(1));
			}
			return roles;
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Command("select_announcement")
	public String selectAnnouncement() throws Exception {
		String moduleId = request.getParameter("announcement_module_id");
		AnnouncementAdmin announcementAdmin = db.find(AnnouncementAdmin.class, moduleId);
		if ( announcementAdmin == null ) {
			db.begin();
			announcementAdmin = new AnnouncementAdmin(moduleId);
			db.persist(announcementAdmin);
			db.commit();
		}
		context.put("announcement_admin", announcementAdmin);
		context.put("roles", getRoles());
		return path + "announcement.vm";
	}
	
	@Command("add_role")
	public String addRoleAdmin() throws Exception {
		String moduleId = request.getParameter("announcement_module_id");
		AnnouncementAdmin announcementAdmin = db.find(AnnouncementAdmin.class, moduleId);	
		context.put("announcement_admin", announcementAdmin);
		
		String role = request.getParameter("role_name");
		db.begin();
		if ( announcementAdmin.getAdminRoles() == null ) announcementAdmin.setAdminRoles(new ArrayList<AdminRole>());
		announcementAdmin.getAdminRoles().add(new AdminRole(role));
		db.commit();
		context.put("roles", getRoles());
		return path + "announcement.vm";
	}
	
	@Command("remove_role")
	public String removeRoleAdmin() throws Exception {
		String moduleId = request.getParameter("announcement_module_id");
		AnnouncementAdmin announcementAdmin = db.find(AnnouncementAdmin.class, moduleId);	
		context.put("announcement_admin", announcementAdmin);
		
		if ( announcementAdmin.getAdminRoles() != null && announcementAdmin.getAdminRoles().size() > 0 ) {
			
			String role = request.getParameter("remove_role_name");
			int i = 0;
			for ( AdminRole r : announcementAdmin.getAdminRoles() ) {
				if ( r.getUserRole().equals(role)) break;
				i++;
			}
			
			AdminRole adminRole = announcementAdmin.getAdminRoles().get(i);
			
			db.begin();
			announcementAdmin.getAdminRoles().remove(i);
			db.remove(adminRole);
			db.commit();
			
		}
		context.put("roles", getRoles());
		return path + "announcement.vm";
	}
	
	

}
