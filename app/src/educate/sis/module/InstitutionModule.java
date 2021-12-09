package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Institution;
import educate.sis.struct.entity.LearningCentre;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class InstitutionModule extends LebahModule {
	
	String path = "apps/util/institution/";
	DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		Institution institution = (Institution) db.get("select i from Institution i");
		context.put("institution", institution);
		
		if ( institution != null ) {
			List<LearningCentre> centres = db.list("select c from LearningCentre c where c.institution.id = '" + institution.getId() + "' order by c.id");
			context.put("centres", centres);
		}
		else {
			context.remove("centres");
		}
		return path + "start.vm";
	}
	
	@Command("save_changes")
	public String saveChanges() throws Exception {
		String abbrev = request.getParameter("abbrev");
		String name = request.getParameter("name");
		String id = request.getParameter("id");
		
		Institution institution = db.find(Institution.class, id);
		
		if ( institution != null ) {
			db.begin();
			institution.setAbbrev(abbrev);
			institution.setName(name);
			db.commit();
		}
		else {
			db.begin();
			institution = new Institution();
			institution.setAbbrev(abbrev);
			institution.setName(name);
			db.persist(institution);
			db.commit();
		}
		
		return path + "save.vm";
	}
	
	@Command("save_centre")
	public String saveCentre() throws Exception {
		String id = request.getParameter("id");
		String code = request.getParameter("code_" + id);
		String name = request.getParameter("name_" + id);
		
		db.begin();
		LearningCentre c = db.find(LearningCentre.class, id);
		c.setCode(code);
		c.setName(name);
		db.commit();
		
		return path + "empty.vm";
	}
	
	@Command("delete_centre")
	public String deleteCentre() throws Exception {
		String id = request.getParameter("id");
		LearningCentre c = db.find(LearningCentre.class, id);
		String institutionId = c.getInstitution().getId();
		
		context.put("institution", c.getInstitution());
		
		db.begin();
		db.remove(c);
		db.commit();
		
		List<LearningCentre> centres = db.list("select c from LearningCentre c where c.institution.id = '" + institutionId + "'");
		context.put("centres", centres);
		
		return path + "centres.vm";
	}
	
	@Command("add_centre")
	public String addCentre() throws Exception {
		String id = request.getParameter("institution_id");
		String code = request.getParameter("centre_code");
		String name = request.getParameter("centre_name");
		
		Institution institution = db.find(Institution.class, id);
		context.put("institution", institution);
		
		List<LearningCentre> centres = db.list("select c from LearningCentre c where c.institution.id = '" + institution.getId() + "'");
		boolean isMainCampus = false;
		if ( centres.size() == 0 ) {
			isMainCampus = true;
		}
		
		db.begin();
		LearningCentre c = new LearningCentre();
		c.setId(code);
		c.setCode(code);
		c.setName(name);
		c.setInstitution(institution);
		
		if ( isMainCampus ) c.setMainCampus(true);
		
		db.persist(c);
		db.commit();
		
		centres = db.list("select c from LearningCentre c where c.institution.id = '" + institution.getId() + "'");
		context.put("centres", centres);
		
		return path + "centres.vm";
	}
	
	@Command("updateCampusStatus")
	public String updateCampusStatus() throws Exception {
		String id = request.getParameter("id");
		LearningCentre c = db.find(LearningCentre.class, id);
		context.put("c", c);
		
		db.begin();
		if ( c.getMainCampus() ) c.setMainCampus(false);
		else c.setMainCampus(true);
		db.commit();
		
		return path + "campus_status.vm";
	}

}
