package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.struct.entity.SubjectSection;
import lebah.portal.action.AjaxModule;

public class SectionSetupModule extends AjaxModule {
	
	String path = "apps/util/setup_subject_section/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	@Override
	public String doAction() throws Exception {
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "list_sections".equals(command)) listSections();
		else if ( "add_section".equals(command)) addSection();
		else if ( "delete_section".equals(command)) deleteSection();
		else if ( "edit_section".equals(command)) editSection();
		else if ( "update_section".equals(command)) updateSection();
		else if ( "update_code".equals(command)) updateCode();
		else if ( "update_name".equals(command)) updateName();
		else if ( "save_order".equals(command)) saveOrder();
		return vm;
	}
	
	private void saveOrder() throws Exception {
		vm = path + "empty.vm";
		String[] sectionIds = request.getParameterValues("section_ids");
		int i = 0;
		for ( String id : sectionIds) {
			SubjectSection section = db.find(SubjectSection.class, id);
			db.begin();
			section.setSequence(i);
			db.commit();
			i++;
		}
	}

	private void updateCode() throws Exception {
		vm = path + "empty.vm";
		String id = request.getParameter("section_id");
		String code = request.getParameter("section_code_" + id);
		
		SubjectSection section = db.find(SubjectSection.class, id);
		db.begin();
		section.setCode(code);
		db.commit();
		
	}

	private void updateName() throws Exception {
		vm = path + "empty.vm";
		String id = request.getParameter("section_id");
		String name = request.getParameter("section_name_" + id);
		
		SubjectSection section = db.find(SubjectSection.class, id);
		db.begin();
		section.setName(name);
		db.commit();
		
	}

	private void updateSection() throws Exception {
		String sectionId = request.getParameter("section_id");
		SubjectSection section = db.find(SubjectSection.class, sectionId);
		
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		
		db.begin();
		section.setId(code);
		section.setCode(code);
		section.setName(name);
		section.setDescription(name);
		db.commit();
		
		listSections();
		
	}

	private void editSection() {
		String sectionId = request.getParameter("section_id");
		SubjectSection section = db.find(SubjectSection.class, sectionId);
		context.put("section", section);
		
		vm = path + "edit_section.vm";
		
	}

	private void deleteSection() throws Exception {
		String sectionId = request.getParameter("section_id");
		SubjectSection section = db.find(SubjectSection.class, sectionId);
		
		if ( db.list("select s from StudentSubject s where s.section.id = '" + sectionId + "'").size() == 0 ) {
			db.begin();
			db.remove(section);
			db.commit();
		}
		
		listSections();
		
	}

	private void addSection() throws Exception {
		String centreId = request.getParameter("centre_id");
		
		//String id = request.getParameter("id");
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		
		//if ( id == null || "".equals(id) ) id = code;
		
		SubjectSection section = new SubjectSection();
		section.setId(code);
		section.setCode(code);
		section.setName(name);
		section.setDescription(name);
		
		db.begin();
		db.persist(section);
		db.commit();
		
		listSections();
		
	}

	private void listSections() {
		String centreId = request.getParameter("centre_id");
		List<SubjectSection> sections = db.list("select s from SubjectSection s order by s.sequence");
		context.put("sections", sections);
		
		vm = path + "list_sections.vm";
		
	}

	private void start() {
		vm = path + "start.vm";
		String centreId = request.getParameter("centre_id");
		List<SubjectSection> sections = db.list("select s from SubjectSection s order by s.sequence");
		context.put("sections", sections);
	}

}
