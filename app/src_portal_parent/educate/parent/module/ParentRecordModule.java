package educate.parent.module;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.enrollment.entity.Student;
import educate.parent.entity.Parent;
import educate.sis.general.entity.Country;
import educate.sis.general.entity.State;
import lebah.portal.action.Command;
import lebah.template.LebahRecordTemplateModule;

public class ParentRecordModule extends LebahRecordTemplateModule<Parent> {

	@Override
	public Class getIdType() {
		return String.class;
	}

	@Override
	public void afterSave(Parent p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void begin() {
		setOrderBy("name");
		List<Country> countries = db.list("select c from educate.sis.general.entity.Country c order by c.name");
		context.put("countries", countries);
		context.put("nationalities", countries);
		context.put("states", db.list("select s from State s order by s.name"));		
	}

	@Override
	public boolean delete(Parent p) throws Exception {
		return true;
	}

	@Override
	public String getPath() {
		return "parent_portal/records";
	}

	@Override
	public Class<Parent> getPersistenceClass() {
		return Parent.class;
	}

	@Override
	public void getRelatedData(Parent p) {
		List<Country> countries = db.list("select c from educate.sis.general.entity.Country c order by c.name");
		context.put("countries", countries);
		context.put("nationalities", countries);
		context.put("states", db.list("select s from State s order by s.name"));
		
	}

	@Override
	public void save(Parent p) throws Exception {
		
		boolean emailChanged = false;
		String oldEmail = p.getEmail() != null ? p.getEmail() : "";

		State state = db.find(State.class, getParam("stateId"));
		Country country = db.find(Country.class, getParam("countryId"));
		Country nationality = db.find(Country.class, getParam("nationalityId"));
		Date birthDate = null;
		try {
			birthDate = new SimpleDateFormat("dd-MM-yyyy").parse(getParam("birthDate"));
		} catch ( Exception e ) { }
		
		p.setName(getParam("name"));
		p.setAddress1(getParam("address1"));
		p.setAddress2(getParam("address2"));
		p.setCity(getParam("city"));
		p.setState(state);
		p.setCountry(country);
		p.setNationality(nationality);
		p.setGender(getParam("gender"));
		p.setPhoneHome(getParam("phoneHome"));
		p.setPhoneOffice(getParam("phoneOffice"));
		p.setPhoneMobile(getParam("phoneMobile"));
		p.setBirthDate(birthDate);
		
		if ( !getParam("email").equals(oldEmail)) emailChanged = true;
		p.setIdentityNo(getParam("identityNo"));
		
		if ( isNew ) {
			p.setEmail(getParam("email"));
			PortalData.createPortalLogin(p.getEmail(), p.getIdentityNo(), p.getName(), "parent");
		}
		else if ( emailChanged ) {
			p.setEmail(getParam("email"));
			PortalData.createPortalLogin(p.getEmail(), p.getIdentityNo(), p.getName(), "parent");
		}
		
	}

	@Override
	public Map<String, Object> searchCriteria() throws Exception {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("name", getParam("find_name"));
		m.put("identityNo", getParam("find_identityNo"));
		return m;
	}
	
	@Command("addStudent")
	public String addStudent() throws Exception {
		String parentId = getParam("parentId");
		Parent parent = db.find(Parent.class, parentId);
		context.put("parentId", parent.getId());
		String matricNo = getParam("matricNo").trim();
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( student != null ) {
			db.begin();
			parent.getStudents().add(student);
			db.commit();
		}
		
		List<Student> students = parent.getStudents();
		context.put("students", students);
		return getPath() + "/divStudents.vm";
	}
	
	@Command("removeStudent")
	public String removeStudent() throws Exception {
		String parentId = getParam("parentId");
		Parent parent = db.find(Parent.class, parentId);
		context.put("parentId", parent.getId());
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		if ( student != null ) {
			db.begin();
			parent.getStudents().remove(student);
			db.commit();
		}
		
		List<Student> students = parent.getStudents();
		context.put("students", students);
		return getPath() + "/divStudents.vm";
	}	


}
