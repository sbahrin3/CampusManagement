package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Specialisation;
import lebah.portal.action.AjaxModule;

public class SpecialisationSetupModule extends AjaxModule {
	
	String path = "apps/util/specialisation/";
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
		if ( null == command || "".equals(command)) listPrograms();
		else if ( "list_specialisations".equals(command)) listSpecialisations();
		else if ( "add_specialisation".equals(command)) addSpecialisation();
		else if ( "edit_specialisation".equals(command)) editSpecialisation();
		else if ( "update_specialisation".equals(command)) updateSpecialisation();
		else if ( "delete_specialisation".equals(command)) deleteSpecialisation();
		return vm;
	}
	
	
	private void deleteSpecialisation() throws Exception {

		String programId = request.getParameter("program_id");
		Program program = db.find(Program.class, programId);
		
		String specialisationId = request.getParameter("specialisation_id");
		Specialisation specialisation = db.find(Specialisation.class, specialisationId);
		
		db.begin();
		db.remove(specialisation);
		db.commit();
		
		listSpecialisations();
		
	}


	private void updateSpecialisation() throws Exception {

		String programId = request.getParameter("program_id");
		Program program = db.find(Program.class, programId);
		
		String specialisationId = request.getParameter("specialisation_id");
		Specialisation specialisation = db.find(Specialisation.class, specialisationId);
		
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		
		db.begin();
		specialisation.setCode(code);
		specialisation.setName(name);
		db.commit();
		
		listSpecialisations();
		
	}


	private void editSpecialisation() {
		vm = path + "edit_specialisation.vm";
		String programId = request.getParameter("program_id");
		Program program = db.find(Program.class, programId);
		
		String specialisationId = request.getParameter("specialisation_id");
		Specialisation specialisation = db.find(Specialisation.class, specialisationId);
		
		context.put("specialisation", specialisation);
		
	}


	private void addSpecialisation() throws Exception {

		String programId = request.getParameter("program_id");
		Program program = db.find(Program.class, programId);
		
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		
		Specialisation specialisation = new Specialisation();
		specialisation.setCode(code);
		specialisation.setName(name);
		specialisation.setProgram(program);
		
		db.begin();
		db.persist(specialisation);
		db.commit();
		
		context.put("specialisation", specialisation);
		
		listSpecialisations();
	}


	private void listSpecialisations() {
		vm = path + "list_specialisations.vm";
		String programId = request.getParameter("program_id");
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		
		String sql = "select s from Specialisation s where s.program.id = '" + programId + "'";
		List<Specialisation> specialisations = db.list(sql); 
		context.put("specialisations", specialisations);
		
	}


	private void listPrograms() {
		vm = path + "list_programs.vm";
		context.remove("intakes");
		params();
	}
	
	
	private void params() {
		List<Program> programs = db.list("SELECT a from Program a order by a.code");
		context.put("programs",programs);
		session.setAttribute("programs", programs);
	}

}
