package educate.sis.module;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import educate.db.DbPersistence;
import educate.enrollment.entity.StatusType;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectPeriod;
import educate.sis.struct.entity.SubjectReg;

public class ProgramUtil {
	
	DbPersistence db = new DbPersistence();
	
	public static void main(String[] args) throws Exception {

		new ProgramUtil().test();
		
	}
	
	public void test() throws Exception {
		/*
		1308174992321
		SEMESTERS-SEM 1
		ACC00130
		ACC00130
		ACC00146
		*/
		
		ProgramStructure ps = db.find(ProgramStructure.class, "1308174992321");
		deleteSubjects(ps, "SEMESTERS-SEM 1", new String[] {"ACC00130","ACC00146"} );
	}
	
	public void deleteSubjects(ProgramStructure ps, String periodId, String[] subjectIds) throws Exception {
		//select subject period for this period id
		List<SubjectPeriod> subjectPeriods = db.list("select sp from ProgramStructure p JOIN p.subjectPeriod sp where p.id = '" + ps.getId() + "' and sp.period.id = '" + periodId + "'");
		if ( subjectPeriods.size() > 0 ) { //must be only ONE
			SubjectPeriod sp = subjectPeriods.get(0);
			Set<SubjectReg> subjectRegs = sp.getSubjectRegs();
			List<SubjectReg> removes = new ArrayList<SubjectReg>();
			for ( SubjectReg r : subjectRegs ) {
				for ( String id : subjectIds ) {
					if ( r.getId().equals(id)) removes.add(r);
				}
			}
			db.begin();
			for ( SubjectReg r : removes ) {
				subjectRegs.remove(r);
				db.remove(r);
			}
			db.commit();
		}
	}

	public Set<Subject> getSubjects(ProgramStructure structure, String periodId) {
		if ( structure.getSubjectPeriod() != null ) {
			for ( SubjectPeriod sp : structure.getSubjectPeriod() ) {
				if ( sp.getPeriod().getId().equals(periodId)) {
					return sp.getSubjects();
				}
			}
		}
		return null;
	}
	
	public Set<SubjectReg> getSubjectRegs(ProgramStructure structure, String periodId) {
		int cnt = 0;
		if ( structure != null && structure.getSubjectPeriod() != null ) {
			for ( SubjectPeriod sp : structure.getSubjectPeriod() ) {
				if ( sp.getPeriod() != null && sp.getPeriod().getId().equals(periodId)) {
					return sp.getSubjectRegs();
				}
			}
		}
		return null;
	}
	
	public List<SubjectReg> getSubjectRegsOrderByCode(ProgramStructure structure, String periodId) {
		int cnt = 0;
		if ( structure != null && structure.getSubjectPeriod() != null ) {
			for ( SubjectPeriod sp : structure.getSubjectPeriod() ) {
				if ( sp.getPeriod() != null && sp.getPeriod().getId().equals(periodId)) {
					return sp.getSubjectRegsOrderByCode();
				}
			}
		}
		return null;
	}
	
	public Set<SubjectReg> getSubjectRegs(ProgramStructure structure) {
		Set<SubjectReg> subjects = new HashSet<SubjectReg>();
		Set<SubjectPeriod> subjectPeriods = structure.getSubjectPeriod();
		for ( SubjectPeriod sp : subjectPeriods ) {
			subjects.addAll(sp.getSubjectRegs());
		}
		return subjects;
	}
	
	
	public Set<SubjectReg> listSubjectRegs(ProgramStructure structure, String periodId) {
		if ( structure.getSubjectPeriod() != null ) {
			for ( SubjectPeriod sp : structure.getSubjectPeriod() ) {
				if ( sp.getPeriod().getId().equals(periodId)) {
					return sp.getSubjectRegs();
				}
			}
		}
		return null;
	}
	
//	public List<SubjectReg> getSubjectNonSpecialisation(ProgramStructure s, String pId) {
//		List<SubjectReg> subjects = new ArrayList<SubjectReg>();
//		Set<SubjectReg> regs = listSubjectRegs(s, pId);
//		for ( SubjectReg reg : regs ) {
//			if ( reg.getSpecialisation() == null ) subjects.add(reg);
//		}
//		return subjects;
//	}
//	
//	public List<SubjectReg> getSubjectSpecialisation(ProgramStructure s, String pId) {
//		List<SubjectReg> subjects = new ArrayList<SubjectReg>();
//		Set<SubjectReg> regs = listSubjectRegs(s, pId);
//		for ( SubjectReg reg : regs ) {
//			if ( reg.getSpecialisation() != null ) subjects.add(reg);
//		}
//		return subjects;
//	}
	
	public List<SubjectReg> getSubjectRegsOrderedByCode(ProgramStructure structure, String periodId) {
		Set<SubjectReg> regs = getSubjectRegs(structure, periodId);
		List<SubjectReg> list = new ArrayList<SubjectReg>();
		list.addAll(regs);
		Collections.sort(list, new ComparatorByCode());
		return list;
	}
	
	public List<SubjectReg> getSubjectRegsOrderedByName(ProgramStructure structure, String periodId) {
		Set<SubjectReg> regs = getSubjectRegs(structure, periodId);
		List<SubjectReg> list = new ArrayList<SubjectReg>();
		list.addAll(regs);
		Collections.sort(list, new ComparatorByName());
		return list;
	}
	
	public List<SubjectReg> getSubjectRegsOrderedByCategory(ProgramStructure structure, String periodId) {
		Set<SubjectReg> regs = getSubjectRegs(structure, periodId);
		List<SubjectReg> list = new ArrayList<SubjectReg>();
		list.addAll(regs);
		Collections.sort(list, new ComparatorByCategory());
		return list;
	}	
	
	
	public Set<Subject> getSubjects(ProgramStructure structure) {
		//get period scheme
		String periodSchemeId = structure.getProgram().getPeriodScheme().getId();
		Set<Subject> subjects = new HashSet<Subject>();
		List<SubjectPeriod> list = structure.getSubjectPeriodList();
		String schemeId = "";
		if ( list != null ) {
			for ( SubjectPeriod sp : list ) {
				//System.out.println(sp.getPeriod().getName() + " : " + sp.getSubjects().size());
				//for ( Subject s : sp.getSubjects() ) {
				//	System.out.println(s.getCode());
				//}
				schemeId = sp.getPeriod().getParent() != null ? 
						sp.getPeriod().getParent().getPeriodScheme().getId() : 
							sp.getPeriod().getPeriodScheme() != null ? 
								sp.getPeriod().getPeriodScheme().getId() : "";
				if ( schemeId != null )
					if ( schemeId.equals(periodSchemeId))
						subjects.addAll(sp.getSubjects());
			}
		}
		return subjects;
	}
	

	public SubjectPeriod getSubjectPeriod(ProgramStructure structure, String periodId) {
		Set<SubjectPeriod> list = structure.getSubjectPeriod();
		for ( SubjectPeriod sp : list ) {
			if ( periodId.equals(sp.getPeriod().getId())) return sp;
		}
		return null;
	}
	
	public double getSubjectFee(ProgramStructure structure, String periodId) {
		SubjectPeriod subjectPeriod = null;
		Set<SubjectPeriod> list = structure.getSubjectPeriod();
		for ( SubjectPeriod sp : list ) {
			if ( periodId.equals(sp.getPeriod().getId())) {
				subjectPeriod = sp;
				break;
			}
		}
		return subjectPeriod.getFeeTotal();
	}
	
	public Set<Subject> getSubjects(Student student, Period period) {
		Set<Subject> subjects = new HashSet<Subject>();
		Set<StudentStatus> statusList = student.getStatus();
		for ( StudentStatus ss : statusList ) {
			if ( ss.getPeriod().getId().equals(period.getId())) {
				Set<StudentSubject> subjectList = ss.getStudentSubjects();
				for ( StudentSubject s : subjectList ) {
					subjects.add(s.getSubject());
				}
			}
		}
		return subjects;
	}
	
	public Set<StudentSubject> getStudentSubjects(Student student, Period period) {
		Set<StudentSubject> subjects = new HashSet<StudentSubject>();
		Set<StudentStatus> statusList = student.getStatus();
		for ( StudentStatus ss : statusList ) {
			if ( ss.getPeriod().getId().equals(period.getId())) {
				Set<StudentSubject> subjectList = ss.getStudentSubjects();
				subjects.addAll(subjectList);
			}
		}
		return subjects;
	}	
	
	public Set<Subject> getSubjects(Student student, Session session) {
		Set<Subject> subjects = new HashSet<Subject>();
		Set<StudentStatus> statusList = student.getStatus();
		for ( StudentStatus ss : statusList ) {
			if ( ss.getSession().getId().equals(session.getId()) ) {
				Set<StudentSubject> subjectList = ss.getStudentSubjects();
				for ( StudentSubject s : subjectList ) {
					subjects.add(s.getSubject());
				}
			}
		}
		return subjects;
	}
	
	public Set<StudentSubject> getStudentSubjects(Student student, Session session) {
		Set<StudentSubject> subjects = new HashSet<StudentSubject>();
		Set<StudentStatus> statusList = student.getStatus();
		for ( StudentStatus ss : statusList ) {
			if ( ss.getSession().getId().equals(session.getId()) ) {
				Set<StudentSubject> subjectList = ss.getStudentSubjects();
				subjects.addAll(subjectList);
			}
		}
		return subjects;
	}	
	
	public List<StudentStatus> ListStudentStatuses(Student student) {
		Set<StudentStatus> statuses = student.getStatus();
		List<StudentStatus> list = new ArrayList<StudentStatus>();
		list.addAll(statuses);
		Collections.sort(list);
		return list;
	}
	
	public List<StudentStatus> getStudentStatuses(Student student) {
		Set<StudentStatus> statuses = student.getStatus();
		List<StudentStatus> list = new ArrayList<StudentStatus>();
		list.addAll(statuses);
		Collections.sort(list);
		return list;
	}
	

	public Set<FinalSubjectResult> getResults(Student student, Session session) {
		Set<FinalSubjectResult> subjects = new HashSet<FinalSubjectResult>();
		Hashtable h = new Hashtable();
		DbPersistence db = new DbPersistence();
		Hashtable param = new Hashtable();
		param.put("student", student);
		param.put("session", session);
		String sql = "select e from FinalResult e where e.student = :student and e.session = :session";
		//List<FinalResult> list = db.list(sql, param);
		
		
		sql = "select r from FinalResult r " +
		" where r.student.id = '" + student.getId() + "' " +
		" and r.session.id = '" + session.getId() + "' "; //" and r.period.id = '" + studentStatus.getPeriod().getId() + "' ";
		
		System.out.println(sql);
		
		List<FinalResult> list = db.list(sql);
		
		System.out.println("Final Result = " + list.size());
		
		FinalResult ex = null;
		if ( list.size() > 0 ) {
			ex = list.get(0);
			List<FinalSubjectResult> results = ex.getSubjects();
			
			System.out.println("subjects in final result = " + results.size());
			
//			subjects.addAll(results);
			for ( FinalSubjectResult r : results ) {
//				if ( r.getStatus() != null && r.getStatus().equals("ACTIVE")) {
//					subjects.add(r);
//				}
				subjects.add(r);
			}
		}
		return subjects;

	}
	

	
	public StudentStatus getCurrentStudentStatus(Student student) throws Exception{
		Session session = getCurrentSession(student.getProgram().getLevel().getPathNo());
		return getStudentStatus(student, session);
	}
	
	public StudentStatus getStudentStatus(Student student, Session session) {
		Set<StudentStatus> statuses = student.getStatus();
		StudentStatus status = null;
		for( StudentStatus ss : statuses ){
			if ( ss.getSession() != null && ss.getSession().getId().equals(session.getId()) ){
				status = ss;
				break;
			}
		}
		return status;
	}
	
	
	
	
	public Session getSessionByDate(Date date, int pathNo) throws Exception {
		Session session = null;
		Hashtable h = new Hashtable();
		h.put("date", date);
		List<Session> list = db.list("select s from Session s where (:date BETWEEN s.startDate AND s.endDate) AND s.pathNo="+pathNo, h);
		if ( list.size() == 0 ) return null;
		session = list.get(0);
		return session;
	}
	
	public Session getCurrentSession(int pathNo) throws Exception {
		Calendar cal = new GregorianCalendar();
		Date date = cal.getTime();
		return getSessionByDate(date,pathNo);
	}	
	
	/*
	 * In creating StudentStatus..
	 * Session MUST BE different.
	 * Period CAN BE the same.
	 */
	private void createStudentStatus(Student student, Session session, Period period, StatusType type) throws Exception {
		
		StudentStatus st = new StudentStatus();
		st.setStudent(student);
		st.setPeriod(period);
		st.setSession(session);
		st.setType(type);
		
		
		
	}
	

	
	public static class ComparatorByCode implements Comparator {
		public int compare(Object o1, Object o2) {
			SubjectReg s1 = (SubjectReg) o1;
			SubjectReg s2 = (SubjectReg) o2;
			try {
				return s1.getSubject().getCode().compareTo(s2.getSubject().getCode());
			} catch ( Exception e ) {
				return 0;
			}
		}

		@Override
		public Comparator reversed() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Comparator other) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Function keyExtractor,
				Comparator keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Function keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingInt(ToIntFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingLong(ToLongFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingDouble(ToDoubleFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> nullsFirst(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> nullsLast(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T, U> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor,
				Comparator<? super U> keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> comparingInt(
				ToIntFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> comparingLong(
				ToLongFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> comparingDouble(
				ToDoubleFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static class ComparatorByName implements Comparator {
		public int compare(Object o1, Object o2) {
			SubjectReg s1 = (SubjectReg) o1;
			SubjectReg s2 = (SubjectReg) o2;
			try {
				return s1.getSubject().getName().compareTo(s2.getSubject().getName());
			} catch ( Exception e ) {
				return 0;
			}
		}

		@Override
		public Comparator reversed() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Comparator other) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Function keyExtractor,
				Comparator keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Function keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingInt(ToIntFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingLong(ToLongFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingDouble(ToDoubleFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> nullsFirst(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> nullsLast(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T, U> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor,
				Comparator<? super U> keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> comparingInt(
				ToIntFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> comparingLong(
				ToLongFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> comparingDouble(
				ToDoubleFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static class ComparatorByCategory extends educate.util.MyComparator {
		public int compare(Object o1, Object o2) {
			SubjectReg s1 = (SubjectReg) o1;
			SubjectReg s2 = (SubjectReg) o2;
			try {
				return s1.getCategory().getCode().compareTo(s2.getCategory().getCode());
			} catch ( Exception e ) {
				return 0;
			}
		}

		


	}
	

	public StatusType getDefaultStatus() throws Exception {
		StatusType type = (StatusType) db.get("select s from StatusType s where s.defaultType = 1");
		return type;
	}

	private static void testComparator() {
		DbPersistence db = new DbPersistence();
		ProgramUtil pu = new ProgramUtil();
		ProgramStructure ps = db.find(ProgramStructure.class, "1247823215050");
		Set<SubjectReg> regs = pu.getSubjectRegs(ps, "1240892404963");
		List<SubjectReg> list = new ArrayList<SubjectReg>();
		list.addAll(regs);
		Collections.sort(list, new ComparatorByName());
		for ( SubjectReg reg : list ) {
			System.out.println(reg.getSubject().getCode() + " " + reg.getSubject().getName());
		}
	}

}
