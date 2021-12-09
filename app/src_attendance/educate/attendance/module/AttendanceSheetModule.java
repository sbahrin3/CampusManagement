package educate.attendance.module;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import educate.attendance.entity.AttendanceSheet;
import educate.db.DbPersistence;
import educate.enrollment.entity.StudentStatus;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.SubjectSection;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class AttendanceSheetModule extends LebahModule {
	
	private String path = "apps/attendanceSheet";
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void preProcess() {
		context.put("path", path);
		context.put("sections", db.list("select s from SubjectSection s order by s.sequence"));
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
	}

	@Override
	public String start() {
		Hashtable h = new Hashtable();
		h.put("date", new Date());
//		List<PeriodScheme> periodSchemes = db.list(
//				"select p from StudentStatus st Join st.period.periodScheme p " +
//				"where st.session.startDate <= :date and st.session.endDate >= :date " +
//				"group by p.code", h);
//		context.put("periodSchemes", periodSchemes);
		
		List<Program> programs = db.list(
				"select p from StudentStatus st Join st.student.program p " +
				"where st.session.startDate <= :date and st.session.endDate >= :date " +
				"group by p.code", h);
		context.put("programs", programs);

		return path + "/start.vm";
	}
	
	@Command("selectPeriod")
	public String selectPeriod() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		Hashtable h = new Hashtable();
		h.put("date", new Date());
		List<Period> periods = db.list(
				"select p from StudentStatus st Join st.period p " +
				"where st.session.startDate <= :date and st.session.endDate >= :date " +
				"and st.student.program.id = '" + programId + "' group by p.periodScheme.code, p.sequence", h);
		context.put("periods", periods);
		
		return path + "/selectPeriod.vm";
	}
	
	@Command("selectSection")
	public String selectSection() throws Exception {
		String programId = getParam("programId");
		String periodId = getParam("periodId");
		
		Program program = db.find(Program.class, programId);
		Period period = db.find(Period.class, periodId);
		
		context.put("program", program);
		context.put("period", period);
		
		Hashtable h = new Hashtable();
		h.put("date", new Date());
		List<SubjectSection> sections = db.list(
				"select s from StudentStatus st Join st.section s " +
				"where st.session.startDate <= :date and st.session.endDate >= :date " +
				"and st.student.program.id = '" + programId + "' and st.period.id = '" + periodId + "' " +
				"group by s.code order by s.sequence", h);

		context.put("sections", sections);
		return path + "/selectSection.vm";
	}
	
	@Command("listStudents")
	public String listStudents() throws Exception {
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DAY_OF_WEEK, -dayOfWeek + 1);
		Date[] dates = new Date[7];
		for ( int i = 0; i < 7; i++ ) {
			dates[i] = c.getTime();
			c.add(Calendar.DAY_OF_WEEK, 1);
		}
		context.put("dates", dates);
		
		String periodId = getParam("periodId");
		Period period = db.find(Period.class, periodId);
		context.put("period", period);
		String sectionId = getParam("sectionId");
		SubjectSection section = db.find(SubjectSection.class, sectionId);
		context.put("section", section);
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		context.put("program", program);
	
		Hashtable h = new Hashtable();
		h.put("date", new Date());
		h.put("period", period);
		h.put("section", section);
		h.put("program", program);
		List<StudentStatus> students = db.list("select st from StudentStatus st Join st.student s " +
				" where st.session.startDate <= :date and st.session.endDate >= :date " +
				" and st.period = :period and st.section = :section and st.student.program = :program group by s.biodata.name", h);
		context.put("students", students);
		
		Map<String, Map<String, String>> attendanceMapper = new HashMap<String, Map<String, String>>();
		
		h = new Hashtable();
		h.put("period", period);
		h.put("section", section);
		h.put("program", program);
		for ( int i = 0; i < 7; i++ ) {
			h.put("attDate", dates[i]);
			List<AttendanceSheet> attendanceSheets = db.list(
					"select a from AttendanceSheet a  Join a.student st  " +
					" where a.date = :attDate " +
					" and st.period = :period and st.section = :section " +
					"and st.student.program = :program", h);
			
			Map<String, String> map = new HashMap<String, String>();
			for ( AttendanceSheet a : attendanceSheets ) {
				map.put(a.getStudent().getId(), Integer.toString(a.getStatus()));
			}
			attendanceMapper.put(new SimpleDateFormat("dd-MM-yyyy").format(dates[i]), map);
		}
		context.put("attendanceMapper", attendanceMapper);
		
		return path + "/listStudents.vm";
	}
	
	@Command("saveAttendance")
	public String saveAttendance() throws Exception {
		String studentStatusId = getParam("studentStatusId");
		StudentStatus studentStatus = db.find(StudentStatus.class, studentStatusId);
		String dateValue = getParam("dateValue");
		
		System.out.println("status id = " + studentStatusId);
		System.out.println("date = " + dateValue);
		
		Date date = new SimpleDateFormat("dd-MM-yyyy").parse(dateValue);
		Hashtable h = new Hashtable();
		h.put("date", date);
		List<AttendanceSheet> list = db.list("select a from AttendanceSheet a where a.student.id = '" + studentStatusId + "' " +
				"and a.date = :date", h);
		AttendanceSheet attsheet = list.size() > 0 ? (AttendanceSheet) list.get(0) : null;
		if ( attsheet != null ) {
			db.begin();
			if ( attsheet.getStatus() == 0 )
				attsheet.setStatus(1);
			else if ( attsheet.getStatus() == 1 )
				attsheet.setStatus(2);
			else if ( attsheet.getStatus() == 2 )
				attsheet.setStatus(0);
			db.commit();
		}
		else {
			db.begin();
			attsheet = new AttendanceSheet();
			attsheet.setDate(date);
			attsheet.setStudent(studentStatus);
			attsheet.setStatus(1);
			db.persist(attsheet);
			db.commit();
		}
		context.put("attendance", attsheet);
		return path + "/saveAttendance.vm";
	}
	
	public static void main(String[] args) throws Exception {
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int weekOfMonth = c.get(Calendar.WEEK_OF_MONTH);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		System.out.println("week = " + weekOfMonth + ", day = " + dayOfWeek);
		c.add(Calendar.DAY_OF_WEEK, -dayOfWeek + 1);
		System.out.println(c.getTime());
		
		Date[] dates = new Date[7];
		for ( int i = 0; i < 7; i++ ) {
			dates[i] = c.getTime();
			c.add(Calendar.DAY_OF_WEEK, 1);
		}
		
		for ( int i = 0; i < 7; i++ ) {
			System.out.println(dates[i]);
		}
		
		
	}
	
	
	public static int[] createRandom(int high, int count) {
		//make sure count NEVER exceeds high
		if ( count > high ) count = high;
		int[] numbers = new int[count];
		Random random = new Random();
		for ( int k=0; k < count; k++ ) {
			boolean looping = true;
			int num = 0;
			while ( isDuplicate(num, numbers, k) ) num = random.nextInt(high) + 1;
			numbers[k] = num;
		}
		return numbers;
	}	
	
	static boolean isDuplicate(int num, int[] numbers, int position) {
		boolean b = false;
		for (int i=position; i > -1; i-- ) {
			if ( num == numbers[i]) {
				b = true;
				break;
			}
		}
		return b;
	}


}
