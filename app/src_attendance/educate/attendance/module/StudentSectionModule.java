package educate.attendance.module;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import educate.db.DbPersistence;
import educate.enrollment.entity.StudentStatus;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.PeriodScheme;
import educate.sis.struct.entity.SubjectSection;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentSectionModule extends LebahModule {
	
	private String path = "apps/attendanceSheet/section";
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void preProcess() {
		context.put("path", path);
		context.put("sections", db.list("select s from SubjectSection s order by s.sequence"));
	}

	@Override
	public String start() {
		Hashtable h = new Hashtable();
		h.put("date", new Date());
		List<PeriodScheme> periodSchemes = db.list(
				"select p from StudentStatus st Join st.period.periodScheme p " +
				"where st.session.startDate <= :date and st.session.endDate >= :date " +
				"group by p.code", h);
		context.put("periodSchemes", periodSchemes);

		return path + "/start.vm";
	}
	
	@Command("listStudents")
	public String listStudents() throws Exception {

		doListStudents();
		
		return path + "/students.vm";
	}

	private void doListStudents() {
		String periodId = getParam("periodId");
		Period period = db.find(Period.class, periodId);
		context.put("period", period);
		String sectionId = getParam("sectionId");
		SubjectSection section = db.find(SubjectSection.class, sectionId);
		context.put("section", section);
	
		Hashtable h = new Hashtable();
		h.put("date", new Date());
		h.put("period", period);
		if ( !"".equals(sectionId)) {
			h.put("section", section);
			List<StudentStatus> students = db.list("select st from StudentStatus st Join st.student s " +
					" where st.session.startDate <= :date and st.session.endDate >= :date " +
					" and st.period = :period and st.section = :section group by s.biodata.name", h);
			context.put("students", students);
		}
		else {
			List<StudentStatus> students = db.list("select st from StudentStatus st Join st.student s " +
					" where st.session.startDate <= :date and st.session.endDate >= :date " +
					" and st.period = :period and st.section is null group by s.biodata.name", h);
			context.put("students", students);
		}
	}
	
	@Command("moveStudents")
	public String moveStudents() throws Exception {
		String sectionId = getParam("moveSectionId");
		SubjectSection section = db.find(SubjectSection.class, sectionId);
		String[] ids = request.getParameterValues("moveStudentIds");
		for ( String id : ids ) {
			StudentStatus st = db.find(StudentStatus.class, id);
			db.begin();
			st.setSection(section);
			db.commit();
		}
		doListStudents();
		return path + "/divStudents.vm";
	}
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		List<SubjectSection> sections = db.list("select s from SubjectSection s order by s.sequence");
		int n = sections.size();
		System.out.println(n);
		
		Hashtable h = new Hashtable();
		h.put("date", new Date());
		List<StudentStatus> students = db.list("select st from StudentStatus st Join st.student s " +
				" where st.session.startDate <= :date and st.session.endDate >= :date", h);
		
		db.begin();
		for ( StudentStatus s : students ) {
			int i = createRandom(n, 2)[0] - 1;
			SubjectSection section = sections.get(i);
			System.out.println(i + ", " + section.getName());
			s.setSection(section);
		}
		db.commit();
		
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


