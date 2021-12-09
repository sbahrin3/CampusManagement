package migration.module;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import educate.db.DbPersistence;
import educate.sis.module.ProgramUtil;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectCategory;
import educate.sis.struct.entity.SubjectPeriod;
import educate.sis.struct.entity.SubjectReg;
import lebah.db.Db;

public class ProgramStructureMigration {
	
	private static String centreId = "CY";
	
	public static void main(String[] args) throws Exception {
		String programCode = "BOSH JAN 2013";
		DbPersistence dp = new DbPersistence();
		migrate(dp, programCode);
		
	}
	
	public static List<String> getProgramCodes() throws Exception {
		List<String> codes = new ArrayList<String>();
		Db db = null;
		try {
			db = new Db();
			ResultSet rs = db.getStatement().executeQuery("SELECT distinct(program_code) FROM course_structure order by program_code;");
			while ( rs.next() ) {
				codes.add(rs.getString(1));
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( db != null ) db.close();
		}
		return codes;
	}

	public static void migrate(DbPersistence dp, String programCode) throws Exception {
		
		Map<String, Object> m = getProgramAndIntake(dp, programCode);
		Program program = (Program) m.get("program");
		Session intake = (Session) m.get("intake");
		
		System.out.println("prgram = " + program.getCode());
		System.out.println("intake = " + intake.getCode());
		
		ProgramStructure ps = getProgramStructure(dp, intake, program);
		System.out.println("Program Structure = " + ps.getId());
		
		List<Period> periods = program.getPeriodScheme().getFunctionalPeriodList();
		for ( Period p : periods ) {
			System.out.println(p.getId() + ", " + p.getName());
		}
		
//		Hashtable h = new Hashtable();
//		h.put("driver", "com.mysql.jdbc.Driver");
//		h.put("url", "jdbc:mysql://localhost:3306/cucms_data");
//		h.put("user", "root");
//		h.put("password", "");
		
		//
		Db db = null;
		try {
			db = new Db();
			String sql = "SELECT period_id, subject_id FROM course_structure c where program_code = '" + programCode + "' order by period_id";
			ResultSet rs = db.getStatement().executeQuery(sql);
			String semesterId = "";
			List<String> subjectIds = null;
			int cnt = 0;
			while ( rs.next()) {
				String periodId = rs.getString("period_id");
				String subjectId = rs.getString("subject_id");
				
				if ( !semesterId.equals(periodId)) {
					
					if ( !"".equals(semesterId)) {
						if ( cnt < periods.size()) {
							Period period = periods.get(cnt);
							if ( period != null ) {
								System.out.println(period.getCode());
								addSubjects(dp, subjectIds, period.getId(), ps);
							}
							else {
								System.out.println("Period is NULL for matching " + semesterId);
							}
						} else {
							System.out.println("Period is Out Of Bound " + semesterId);
						}
						cnt++;
					}
					
					semesterId = periodId;
					subjectIds = new ArrayList<String>();
					subjectIds.add(subjectId);
				}
				else {
					subjectIds.add(subjectId);
				}
			}
			if ( cnt < periods.size()) {
				Period period = periods.get(cnt);
				if ( period != null ) {
					System.out.println(period.getCode());
					addSubjects(dp, subjectIds, period.getId(), ps);
				}
				else {
					System.out.println("Period is NULL for matching " + semesterId);
				}
			}

		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, Object> getProgramAndIntake(DbPersistence db, String programCode) throws Exception {
		Program program = (Program) db.get("select p from Program p where p.code = '" + programCode + "'");
		Session intake = getIntake(db, program);
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("program", program);
		m.put("intake", intake);
		return m;
	}

	private static ProgramStructure getProgramStructure(DbPersistence db, Session intake, Program program) throws Exception {
		
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		
		String sql = "select p from ProgramStructure p where p.program.id = '" + program.getId() + "' " +
		"and p.learningCenter.id = '" + centreId + "' " +
				"and p.session.id = '" + intake.getId() + "'";
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		
		if ( ps == null ) {
			ps = new ProgramStructure();
			ps.setLearningCenter(centre);
			ps.setProgram(program);
			ps.setSession(intake);
			
			db.begin();
			db.persist(ps);
			db.commit();
		}
		
		return ps;
	}
	
	private static Session getIntake(DbPersistence db, Program program) {
		if ( program.getLevel() != null ) {
			List<Session> list = db.list("select s from Session s where s.pathNo = " + program.getLevel().getPathNo() + " order by s.startDate");
			if ( list.size() > 0 ) {
				return (Session) list.get(0);
			}
		}
		return null;
	}	
	
	
	private static void addSubjects(DbPersistence db, List<String> subjectIds, String periodId, ProgramStructure programStructure) throws Exception {
		
		SubjectCategory subjectCategory = db.find(SubjectCategory.class, "C");
		
		if ( subjectIds != null ) {
			Set<Subject> subjects = new HashSet<Subject>();
			for ( String id : subjectIds ) {
				Subject subject = (Subject) db.find(Subject.class, id);
				if ( subject != null ) {
					subjects.add(subject);
				}
			}
			SubjectPeriod sp = new ProgramUtil().getSubjectPeriod(programStructure, periodId);
			if ( sp == null ) {
				System.out.println("sp is null");
				sp = new SubjectPeriod();
				Period period = (Period) db.find(Period.class, periodId);
				sp.setPeriod(period);
				sp.setProgramStructure(programStructure);
				for ( Subject s : subjects ) {
					if ( subjectCategory == null ) sp.addSubject(s);
					else sp.addSubject(s, subjectCategory);
				}
				db.begin();
				programStructure.addSubjectPeriod(sp);
				db.commit();
			} else if ( sp != null ) {
				System.out.println("sp is not null");
				db.begin();
				for ( Subject s : subjects ) {
					//if subject already exit, do not add
					boolean found = false;
					if ( sp.getSubjectRegs() != null ) {
						for ( SubjectReg sr : sp.getSubjectRegs() ) {
							if (sr.getSubject().getId().equals(s.getId())) {
								found = true;
								break;
							}
						}
					}
					if ( !found ) {
						if ( subjectCategory == null ) sp.addSubject(s);
						else sp.addSubject(s, subjectCategory);
					}
				}
				db.commit();
			}
		}
	}
	

}
