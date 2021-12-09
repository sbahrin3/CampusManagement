package educate.enrollment.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.general.entity.Gender;
import educate.sis.general.entity.Race;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class DashboardStatisticByMonth extends LebahModule {
	
	protected final String path = "apps/util/student_stat/byProgramIntake";
	DbPersistence db = new DbPersistence();
	
	@Override
	public String start() {
		return path + "/start.vm";

	}
	
	@Command("stats")
	public String stats(){
		int y = Integer.parseInt(getParam("year"));
		context.put("year", y);
		int d = !"".equals(getParam("day")) ? Integer.parseInt(getParam("day")) : 28;
		List<String> programNames = new ArrayList<String>();
		Map<String, Long> stats = new HashMap<String, Long>();
		Map<String, Long> totals = new HashMap<String, Long>();
		Map<String, String> ids = new HashMap<String, String>();
		List<Program> programs = db.list("select p from StudentStatus s join s.student st join st.program p group by p.code");
		for (Program p : programs ) {
			List<Session> intakes = db.list("select i from StudentStatus s join s.student st join st.intake i where st.program.id = '" + p.getId() + "' group by i.code");
			for ( Session i : intakes ) {
				programNames.add(p.getCode() + " : " + i.getCode());
				for ( int m = 0; m < 12; m++ ) {
					long count = getStudentCount(m, y, d, p.getId(), i.getId());
					stats.put(p.getCode() + " : " + i.getCode() + " - " + m, count);
					Long total = totals.get(":" + m);
					if ( total == null ) totals.put(":" + m, count);
					else totals.put(":" + m, total + count);
				}
				ids.put(p.getCode() + " : " + i.getCode() + " - programId", p.getId());
				ids.put(p.getCode() + " : " + i.getCode() + " - intakeId", i.getId());
			}
		}

		context.put("programNames", programNames);
		context.put("stats", stats);
		context.put("totals", totals);
		context.put("ids", ids);
		
		request.getSession().setAttribute("programNames", programNames);
		request.getSession().setAttribute("stats", stats);
		request.getSession().setAttribute("totals", totals);
		
		
		Map<String, Long> genderStat = new HashMap<String, Long>();
		List<Gender> genders = db.list("select g from Gender g");
		Map<String, Long> genderTotals = new HashMap<String, Long>();
		for ( Gender g : genders ) {
			for ( int m = 0; m < 12; m++ ) {
				long count = getStudentCountByGender(g.getId(), m, y, d);
				genderStat.put(g.getId() + ":" + m, count);
				
				Long total = genderTotals.get(":" + m);
				if ( total == null ) genderTotals.put(":" + m, count);
				else genderTotals.put(":" + m, total + count);
			}
		}
		for ( int m = 0; m < 12; m++ ) {
			long count = getStudentCountByGender(null, m, y, d);
			genderStat.put("null:" + m, count);
			
			Long total = genderTotals.get(":" + m);
			if ( total == null ) genderTotals.put(":" + m, 0L);
			else genderTotals.put(":" + m, total + count);
		}
		
		context.put("genderStat", genderStat);
		context.put("genders", genders);
		context.put("genderTotals", genderTotals);

		request.getSession().setAttribute("genderStat", genderStat);
		request.getSession().setAttribute("genders", genders);
		request.getSession().setAttribute("genderTotals", genderTotals);
		
		
		Map<String, Long> raceStat = new HashMap<String, Long>();
		List<Race> races = db.list("select g from Race g");
		Map<String, Long> raceTotals = new HashMap<String, Long>();
		for ( Race g : races ) {
			for ( int m = 0; m < 12; m++ ) {
				long count = getStudentCountByRace(g.getId(), m, y, d);
				raceStat.put(g.getId() + ":" + m, count);
				
				Long total = raceTotals.get(":" + m);
				if ( total == null ) raceTotals.put(":" + m, count);
				else raceTotals.put(":" + m, total + count);
			}
		}
		for ( int m = 0; m < 12; m++ ) {
			long count = getStudentCountByRace(null, m, y, d);
			raceStat.put("null:" + m, count);
			
			Long total = raceTotals.get(":" + m);
			if ( total == null ) raceTotals.put(":" + m, 0L);
			else raceTotals.put(":" + m, total + count);
		}
		
		context.put("raceStat", raceStat);
		context.put("races", races);
		context.put("raceTotals", raceTotals);

		request.getSession().setAttribute("raceStat", raceStat);
		request.getSession().setAttribute("races", races);
		request.getSession().setAttribute("raceTotals", raceTotals);

		return path + "/stats.vm";
	}
	
	@Command("listStudents")
	public String listStudents() throws Exception {
		int month = Integer.parseInt(getParam("month"));
		int year = Integer.parseInt(getParam("year"));
		int d = !"".equals(getParam("day")) ? Integer.parseInt(getParam("day")) : 28;
		String programId = getParam("programId");
		String intakeId = getParam("intakeId");
		
		context.put("program", db.find(Program.class, programId));
		context.put("intake", db.find(Session.class, intakeId));
		
		List<Student> students = getStudentList(month, year, d, programId, intakeId);
		context.put("students", students);
		return path + "/students.vm";
	}
	
	@Command("listStudentsByGender")
	public String listStudentsByGender() throws Exception {
		int month = Integer.parseInt(getParam("month"));
		int year = Integer.parseInt(getParam("year"));
		int d = !"".equals(getParam("day")) ? Integer.parseInt(getParam("day")) : 28;
		String genderId = getParam("genderId");
		
		context.put("gender", db.find(Gender.class, genderId));
		
		List<Student> students = !"null".equals(genderId) ? getStudentListByGender(genderId, month, year, d) : getStudentListByGender(null, month, year, d);
		context.put("students", students);
		return path + "/students.vm";
	}	
	
	@Command("listStudentsByRace")
	public String listStudentsByRace() throws Exception {
		int month = Integer.parseInt(getParam("month"));
		int year = Integer.parseInt(getParam("year"));
		int d = !"".equals(getParam("day")) ? Integer.parseInt(getParam("day")) : 28;
		String raceId = getParam("raceId");
		
		context.put("race", db.find(Race.class, raceId));
		
		List<Student> students = !"null".equals(raceId) ? getStudentListByRace(raceId, month, year, d) : getStudentListByRace(null, month, year, d);
		context.put("students", students);
		return path + "/students.vm";
	}	
	private long getStudentCount(int month, int year, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		
		Hashtable h = new Hashtable();
		h.put("date1", cal.getTime());
		
		String sql = "select count(s) " +
		"from StudentStatus s JOIN s.session ss " +
		"WHERE :date1 between ss.startDate and ss.endDate " +
		"and s.type.inActive = 0 " +
		"and (s.student.fakeStudent is null OR s.student.fakeStudent = '') ";
		
		List<Long> results = db.list(sql, h);  
		return results.get(0);
	}
	

	private long getStudentCount(int month, int year, int day, String programId, String intakeId) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		
		Hashtable h = new Hashtable();
		h.put("date1", cal.getTime());
		
		String sql = "select count(s) " +
		"from StudentStatus s JOIN s.session ss " +
		"WHERE :date1 between ss.startDate and ss.endDate " +
		"and s.student.program.id = '" + programId + "' " +
		"and s.student.intake.id = '" + intakeId + "' " +
		"and s.type.inActive = 0 " +
		"and (s.student.fakeStudent is null OR s.student.fakeStudent = '') ";
		
		List<Long> results = db.list(sql, h);  
		return results.get(0);
	}

	private List<Student> getStudentList(int month, int year, int day, String programId, String intakeId) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		
		Hashtable h = new Hashtable();
		h.put("date1", cal.getTime());
		
		String sql = "select s.student " +
		"from StudentStatus s JOIN s.session ss " +
		"WHERE :date1 between ss.startDate and ss.endDate " +
		"and s.student.program.id = '" + programId + "' " +
		"and s.student.intake.id = '" + intakeId + "' " +
		"and s.type.inActive = 0 " +
		"and (s.student.fakeStudent is null OR s.student.fakeStudent = '') order by s.student.biodata.name";
		
		List<Student> results = db.list(sql, h);  
		return results;
	}
	
	private long getStudentCountByGender(String genderId, int month, int year, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		
		Hashtable h = new Hashtable();
		h.put("date1", cal.getTime());
		
		String sql = "select count(s) " +
		"from StudentStatus s JOIN s.session ss " +
		"WHERE :date1 between ss.startDate and ss.endDate " +
		"and s.type.inActive = 0 " +
		"and (s.student.fakeStudent is null OR s.student.fakeStudent = '') " +
		"and s.student.intake.id <> '' ";
		if ( genderId != null )
			sql += "and s.student.biodata.gender.id = '" + genderId + "' ";
		else
			sql += "and s.student.biodata.gender.id is null ";
		List<Long> results = db.list(sql, h);
		return results.get(0);
	}	
	
	private List<Student> getStudentListByGender(String genderId, int month, int year, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, 28);
		
		Hashtable h = new Hashtable();
		h.put("date1", cal.getTime());
		
		String sql = "select s.student " +
		"from StudentStatus s JOIN s.session ss " +
		"WHERE :date1 between ss.startDate and ss.endDate " +
		"and s.type.inActive = 0 " +
		"and (s.student.fakeStudent is null OR s.student.fakeStudent = '') " +
		"and s.student.intake.id <> '' ";
		if ( genderId != null )
			sql += "and s.student.biodata.gender.id = '" + genderId + "' ";
		else
			sql += "and s.student.biodata.gender.id is null ";
		List<Student> results = db.list(sql, h);
		return results;
	}
	
	private long getStudentCountByRace(String raceId, int month, int year, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		
		Hashtable h = new Hashtable();
		h.put("date1", cal.getTime());
		
		String sql = "select count(s) " +
		"from StudentStatus s JOIN s.session ss " +
		"WHERE :date1 between ss.startDate and ss.endDate " +
		"and s.type.inActive = 0 " +
		"and (s.student.fakeStudent is null OR s.student.fakeStudent = '') " +
		"and s.student.intake.id <> '' ";
		if ( raceId != null )
			sql += "and s.student.biodata.race.id = '" + raceId + "' ";
		else
			sql += "and s.student.biodata.race.id is null ";
		List<Long> results = db.list(sql, h);
		return results.get(0);
	}	
	
	private List<Student> getStudentListByRace(String raceId, int month, int year, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		
		Hashtable h = new Hashtable();
		h.put("date1", cal.getTime());
		
		String sql = "select s.student " +
		"from StudentStatus s JOIN s.session ss " +
		"WHERE :date1 between ss.startDate and ss.endDate " +
		"and s.type.inActive = 0 " +
		"and (s.student.fakeStudent is null OR s.student.fakeStudent = '') " +
		"and s.student.intake.id <> '' ";
		if ( raceId != null )
			sql += "and s.student.biodata.race.id = '" + raceId + "' ";
		else
			sql += "and s.student.biodata.race.id is null ";
		List<Student> results = db.list(sql, h);
		return results;
	}	

}
