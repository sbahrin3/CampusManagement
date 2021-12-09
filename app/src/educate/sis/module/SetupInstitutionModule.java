/**
 * 
 */
package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Institution;
import educate.sis.struct.entity.LearningCentre;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class SetupInstitutionModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/setupInstitution";

	/* (non-Javadoc)
	 * @see lebah.portal.action.LebahModule#start()
	 */
	@Override
	public String start() {
		reload();		
		return path + "/start.vm";
	}
	
	@Command("reload")
	public String reload() {
		Institution institution = (Institution) db.get("select i from Institution i");
		context.put("institution", institution);
		
		if ( institution != null ) {
			List<LearningCentre> centres = db.list("select c from LearningCentre c where c.institution.id = '" + institution.getId() + "' order by c.mainCampus desc");
			context.put("centres", centres);
		}
		else {
			context.remove("centres");
		}		
		return path + "/data.vm";
	}
	
	
	@Command("save_institution")
	public String saveInstitution() throws Exception {
		
		Institution institution = (Institution) db.get("select i from Institution i");
		context.put("institution", institution);
		
		institution.setAbbrev(getParam("abbrev"));
		institution.setName(getParam("name"));
		
		reload();
		
		return path + "/data.vm";
	}
	
	
	@Command("edit_institution")
	public String editInstitution() throws Exception {
		
		Institution institution = (Institution) db.get("select i from Institution i");
		context.put("institution", institution);
		
		return path + "/edit_institution.vm";
	}
	
	@Command("edit_centre")
	public String editCentre() throws Exception {
		String centreId = getParam("centre_id");
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		context.put("centre", centre);
		return path + "/edit_centre.vm";
	}
	
	@Command("delete_centre")
	public String deleteCentre() throws Exception {
		String centreId = getParam("centre_id");
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		boolean canDelete = true;
		
		if ( canDelete ) {
			db.begin();
			db.remove(centre);
			db.commit();
		}
		
		reload();
		return path + "/data.vm";
	}
	
	@Command("add_centre")
	public String addCentre() throws Exception {
		context.remove("centre");
		return path + "/edit_centre.vm";
	}
	
	
	@Command("save_centre")
	public String saveCentre() throws Exception {
		
		Institution institution = (Institution) db.get("select i from Institution i");
		context.put("institution", institution);
		
		boolean mainCampus = false;
		List<LearningCentre> centres = db.list("select c from LearningCentre c where c.mainCampus = 1");
		if ( centres.size() == 0 ) {
			mainCampus = true;
		}
		
		String centreId = getParam("centre_id");
		LearningCentre centre = null;
		
		if ( !"".equals(centreId)) {
			centre = db.find(LearningCentre.class, centreId);
			if ( !mainCampus ) mainCampus = centre.getMainCampus();
		}
		else {
			centre = new LearningCentre();
			centre.setInstitution(institution);
		}
		
		
		centre.setCode(getParam("centre_code"));
		centre.setName(getParam("centre_name"));
		centre.setAddress1(getParam("centre_address1"));
		centre.setAddress2(getParam("centre_address2"));
		centre.setAddress3(getParam("centre_address3"));
		centre.setAddress4(getParam("centre_address4"));
		centre.setTelephone(getParam("centre_telephone"));
		centre.setFax(getParam("centre_fax"));
		centre.setWebsite(getParam("centre_website"));
		
		//-- set as main campus (only one main campus allowed)
		centre.setMainCampus(mainCampus);
		
		db.begin();
		if ( "".equals(centreId)) {
			db.persist(centre);
		}
		db.commit();
		
		reload();
		
		return path + "/data.vm";
	}

}
