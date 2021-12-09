package educate.sb.roles;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import educate.db.DbPersistence;
import educate.sb.roles.entity.RoleCentre;
import educate.sis.struct.entity.LearningCentre;
import lebah.portal.action.AjaxModule;

public class RoleCentreSetupModule extends AjaxModule {
	
	private String path = "sb/role_centre_setup/";
	private String vm = "";
	private DbPersistence db = new DbPersistence();

	@Override
	public String doAction() throws Exception {
		String command = request.getParameter("command");
		System.out.println(command);
		if ( command == null || "".equals(command)) start();
		else if ( "get_centres".equals(command)) getCentres();
		else if ( "select_centres".equals(command)) selectCentres();
		else if ( "remove_centres".equals(command)) removeCentres();
		return vm;
	}
	
	private void removeCentres() throws Exception {
		
		String roleName = request.getParameter("role_name");
		//get RoleCentre
		RoleCentre roleCentre = (RoleCentre) db.get("select rc from RoleCentre rc where rc.id = '" + roleName + "'");
		context.put("role_centre", roleCentre);
		
		String[] centreIds = request.getParameterValues("remove_centre_ids");
		if ( centreIds != null ) {
			if ( centreIds.length > 0 ) {
				List<LearningCentre> centreList = new ArrayList<LearningCentre>();
				for ( String id : centreIds ) {
					LearningCentre c = db.find(LearningCentre.class, id);
					centreList.add(c);
				}
				
				
				db.begin();
				for ( LearningCentre c : centreList ) {
					roleCentre.getCentres().remove(c);
				}
				db.commit();
			}
		}
		
		//list all learning centres
		listLearningCentres(roleCentre);
		
		vm = path + "div_centres.vm";
		
	}

	private void selectCentres() throws Exception {
	
		String roleName = request.getParameter("role_name");
		//get RoleCentre
		RoleCentre roleCentre = (RoleCentre) db.get("select rc from RoleCentre rc where rc.id = '" + roleName + "'");
		context.put("role_centre", roleCentre);
		
		String[] centreIds = request.getParameterValues("centre_ids");
		if ( centreIds != null ) {
			if ( centreIds.length > 0 ) {
				if ( roleCentre.getCentres() == null ) roleCentre.setCentres(new ArrayList<LearningCentre>());
				List<LearningCentre> centreList = new ArrayList<LearningCentre>();
				for ( String id : centreIds ) {
					LearningCentre c = db.find(LearningCentre.class, id);
					centreList.add(c);
				}
				
				
				db.begin();
				for ( LearningCentre c : centreList ) {
					boolean isExist = false;
					for ( LearningCentre c2 : roleCentre.getCentres() ) {
						if ( c.getId().equals(c2.getId())) {
							isExist = true;
							break;
						}
					}
					if ( !isExist ) roleCentre.getCentres().add(c);
				}
				db.commit();
			}
		}
		
		//list all learning centres
		listLearningCentres(roleCentre);
		
		vm = path + "div_centres.vm";
		
	}

	private void listLearningCentres(RoleCentre roleCentre) {
		List<LearningCentre> centres = db.list("select c from LearningCentre c");
		
		List<LearningCentre> centreList = new ArrayList<LearningCentre>();
		for ( LearningCentre c : centres ) {
			boolean isExist = false;
			for ( LearningCentre c2 : roleCentre.getCentres() ) {
				if ( c.getId().equals(c2.getId())) {
					isExist = true;
					break;
				}
			}
			if ( !isExist ) centreList.add(c);
		}
		
		context.put("centres", centreList);
	}

	private void getCentres() throws Exception {
		
		String roleName = request.getParameter("role_name");
		lebah.db.Db lebahDb = new lebah.db.Db();
		ResultSet rs = lebahDb.getStatement().executeQuery("select name, description from role where name = '" + roleName + "'");
		if ( rs.next() ) {
			lebah.portal.element.Role r = new lebah.portal.element.Role();
			r.setName(rs.getString("name"));
			r.setDescription(rs.getString("description"));
			context.put("role", r);
			
			//get RoleCentre
			RoleCentre roleCentre = (RoleCentre) db.get("select rc from RoleCentre rc where rc.id = '" + roleName + "'");
			if ( roleCentre == null ) {
				System.out.println("create new RoleCentre with id " + roleName);
				db.begin();
				roleCentre = new RoleCentre(roleName);
				db.persist(roleCentre);
				db.commit();
			}
			context.put("role_centre", roleCentre);
			//list all learning centres
			listLearningCentres(roleCentre);
			
			
		} else context.remove("role");
		
		vm = path + "role_centres.vm";
		
	}

	private void start() throws Exception {

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

		
		vm = path + "start.vm";
		
	}

}
