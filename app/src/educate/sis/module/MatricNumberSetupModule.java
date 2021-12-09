package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.sis.general.entity.GradsLevel;
import educate.sis.struct.MatricNumGenerator;
import educate.sis.struct.entity.Course;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.MatricNumPrefix;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.ProgramSessionMatricCode;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.StudyMode;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class MatricNumberSetupModule extends LebahModule {
	
	String path = "apps/matric_number/";
	String vm = "";
	DbPersistence db = new DbPersistence();
	MatricNumGenerator matricGenerator = null;
	
	@Override
	public void preProcess() {
		System.out.println("command=" + command);
	}

	@Override
	public String start() {
		try {
			//get the template
			MatricNumPrefix template = db.find(MatricNumPrefix.class, "template");
			context.put("template", template);
			if ( template == null ) {
				db.begin();
				template = new MatricNumPrefix("template");
				template.setPrefix("{LC}");
				template.setCounterScopePrefix("{LC}");
				template.setCounter(0);
				template.setDigit(5);
				db.persist(template);
				db.commit();
			}
			
			String matricNum = createSample();
			if ( matricNum != null ) context.put("matric_number", matricNum);
			else context.remove("matric_number");
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return path + "start.vm";
	}
	
	@Command("save")
	public String saveTemplate() throws Exception {
		String prefix = getParam("prefix");
		String counterScopePrefix = getParam("counterScopePrefix");
		String digit = getParam("digit");
		boolean globalCounter = false;
		try {
			if ( "1".equals(getParam("global_counter"))) globalCounter = true;
			else globalCounter = false;
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		MatricNumPrefix template = db.find(MatricNumPrefix.class, "template");
		context.put("template", template);
		
		//prefix must have & character
		if ( prefix.indexOf("&") == -1 ) {
			prefix = prefix + "&";
		}
		
		db.begin();
		template.setPrefix(prefix);
		template.setCounterScopePrefix(counterScopePrefix);
		template.setDigit(Integer.parseInt(digit));
		template.setGlobalCounter(globalCounter);
		db.commit();
		
		//display sample matric number
		String matricNum = createSample();
		
		if ( matricNum != null ) context.put("matric_number", matricNum);
		else context.remove("matric_number");
		
		return path + "template.vm";
	}
	
	private String createSample() {

		try {
			MatricNumGenerator g = new MatricNumGenerator();
			g.setSample(true);
			Program program = (Program) db.get("select p from Program p order by p.code");
			Session intake = (Session) db.get("select s from Session s where s.pathNo = 0 order by s.startDate");
			LearningCentre centre = (LearningCentre) db.get("select c from LearningCentre c order by c.code");
			StudyMode mode = (StudyMode) db.get("select c from StudyMode c where c.code = 'FT'");
			
			//g.validateCounter(program, intake, centre, mode);
			
			System.out.println("program = " + program.getCode());
			System.out.println("intake = " + intake.getCode());
			System.out.println("centre = " + centre.getCode());
			System.out.println("mode = " + mode.getCode());
			
			String matricNum = g.generateMatricNum(program, intake, centre, mode);
			setMatricGenerator(g);
			return matricNum;
		} catch (Exception e) {
			System.out.println("got error");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	
	
	public MatricNumGenerator getMatricGenerator() {
		return matricGenerator;
	}

	public void setMatricGenerator(MatricNumGenerator matricGenerator) {
		this.matricGenerator = matricGenerator;
	}

	@Command("list_codes")
	public String listCodes() throws Exception {
		List codes = null;
		String type = request.getParameter("type");
		context.put("type", type);
		if      ( "session".equals(type))  codes = db.list("select s from Session s order by s.pathNo, s.startDate");
		else if ( "level".equals(type))    codes = db.list("select l from GradsLevel l order by l.levelOrder");
		else if ( "program".equals(type))  codes = db.list("select p from Program p order by p.code");
		else if ( "centre".equals(type))   codes = db.list("select l from LearningCentre l order by l.code");
		else if ( "course".equals(type))    codes = db.list("select c from Course c order by c.code");
		else if ( "faculty".equals(type))    codes = db.list("select f from Faculty f order by f.code");
		else if ( "study_mode".equals(type))    {
			codes = db.list("select f from StudyMode f order by f.code");
			if ( codes.size() == 0 ) {
				db.begin();
				StudyMode ft = new StudyMode();
				ft.setCode("FT");
				ft.setName("FULL TIME");
				ft.setMatricCode("FT");
				db.persist(ft);
				db.commit();
				
				db.begin();
				StudyMode pt = new StudyMode();
				pt.setCode("PT");
				pt.setName("PART TIME");
				pt.setMatricCode("PT");
				db.persist(pt);
				db.commit();
				
				codes = db.list("select f from StudyMode f order by f.code");
			}
			
		}
		else if ( "ps_codes".equals(type)) {
			String programId = getParam("programId");
			Program program = db.find(Program.class, programId);
			List<Session> sessions = db.list("select s from Session s where s.pathNo = " + program.getLevel().getPathNo() + " order by s.startDate");
			for ( Session s : sessions ) {
				ProgramSessionMatricCode ps_code = (ProgramSessionMatricCode) db.get("select c from ProgramSessionMatricCode c where c.program.id = '" + programId + "' and c.session.id = '" + s.getId() + "'");
				if ( ps_code == null ) {
					
					db.begin();
					ps_code = new ProgramSessionMatricCode();
					ps_code.setProgram(program);
					ps_code.setSession(s);
					ps_code.setCode(s.getCode());
					ps_code.setName(s.getName());
					ps_code.setMatricCode("");
					db.persist(ps_code);
					db.commit();
				}
			}
			codes = db.list("select c from ProgramSessionMatricCode c where c.program.id = '" + programId + "' order by c.session.startDate");
		}
		else if ( "ps_setup".equals(type)) {
			List<Program> programs = db.list("select p from Program p order by p.code");
			context.put("programs", programs);
			return path + "ps_codes.vm";
		}
		context.put("codes", codes);
		return path + "codes.vm";
	}
	
	
	
	@Command("save_code")
	public String saveCode() throws Exception {
		String type = request.getParameter("type");
		String id = request.getParameter("code_id");
		String code = request.getParameter("code_" + id);

		if      ( "session".equals(type)) {
			Session s = db.find(Session.class, id);
			db.begin();
			s.setMatricCode(code);
			db.commit();
		}
		else if ( "level".equals(type))   {
			GradsLevel s = db.find(GradsLevel.class, id);
			db.begin();
			s.setMatricCode(code);
			db.commit();
			
		}
		else if ( "program".equals(type))  {
			Program s = db.find(Program.class, id);
			db.begin();
			s.setMatricCode(code);
			db.commit();
		}
		else if ( "centre".equals(type))   {
			LearningCentre s = db.find(LearningCentre.class, id);
			db.begin();
			s.setMatricCode(code);
			db.commit();
		}
		else if ( "course".equals(type))    {
			Course s = db.find(Course.class, id);
			db.begin();
			s.setMatricCode(code);
			db.commit();
		}
		else if ( "faculty".equals(type))    {
			Faculty s = db.find(Faculty.class, id);
			db.begin();
			s.setMatricCode(code);
			db.commit();
		}
		else if ( "ps_codes".equals(type)) {
			ProgramSessionMatricCode s = db.find(ProgramSessionMatricCode.class, id);
			db.begin();
			s.setMatricCode(code);
			db.commit();
		}
		else if ( "study_mode".equals(type)) {
			StudyMode m = db.find(StudyMode.class, id);
			db.begin();
			m.setMatricCode(code);
			db.commit();
		}
		
		return path + "empty.vm";
	}

}
