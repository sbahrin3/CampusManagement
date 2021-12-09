package educate.admission.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import edu.emory.mathcs.backport.java.util.Collections;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentSubject;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SubjectRegistrationReportModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "admission/subject_registration_report";

	@Override
	public String start() {
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		List<Subject> subjects = db.list("select s from Subject s order by s.code");
		context.put("subjects", subjects);
		return path + "/start.vm";
	}
	
	@Command("listSessions")
	public String listSessions() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		int pathNo = program.getLevel().getPathNo();
		List<Session> sessions = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
		context.put("sessions", sessions);
		return path + "/listSessions.vm";
	}
	
	@Command("listSubjects")
	public String listSubjects() throws Exception {
		
		return path + "/listSubjects.vm";
	}
	
	@Command("listStudents")
	public String listStudents() throws Exception {
		String programId = getParam("programId");
		String sessionId = getParam("sessionId");
		
		String subjectId = getParam("subjectId");
		String sql = "";
		
		/*
		sql = "select s from Student s join s.status st where st.session.id = '" + sessionId + 
		"' and s.program.id = '" + programId + "'" + 
		"order by s.biodata.name";
		List<Student> students = db.list(sql);
		context.put("students", students);
		*/
		
		Set<Student> students = new HashSet<Student>();
		List<Student> studentList = new ArrayList<Student>();
		
		sql = "select ss from StudentSubject ss join ss.subject s join ss.studentStatus st " +
		"where st.session.id = '" + sessionId + "' ";
		if ( !"".equals(programId)) {
			sql += " and st.student.program.id = '" + programId + "' ";
		}
		if ( !"".equals(subjectId)) {
			sql += " and ss.subject.id = '" + subjectId + "' ";
		}
		Set<Subject> subjectList = new HashSet<Subject>();
		context.put("subjects", subjectList);
		Map<String, String> subjectMap = new HashMap<String, String>();
		context.put("subjectMap", subjectMap);
		
		List<StudentSubject> subjects = db.list(sql);
		for ( StudentSubject s : subjects ) {
			subjectList.add(s.getSubject());
			subjectMap.put(s.getSubject().getId() + "_" + s.getStudentStatus().getStudent().getId(), s.getSubjectStatus() != null ? s.getSubjectStatus() : "-");
		
			students.add(s.getStudentStatus().getStudent());
		}
		
		studentList.addAll(students);
		Collections.sort(studentList, new StudentNameComparator());
	
		context.put("students", studentList);
		
		request.getSession().setAttribute("_students", studentList);
		request.getSession().setAttribute("_subjects", subjectList);
		request.getSession().setAttribute("_subjectMap", subjectMap);
		return path + "/listStudents.vm";
	}
	
	
	static class StudentNameComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			Student s1 = (Student) o1;
			Student s2 = (Student) o2;
			return s1.getBiodata().getName().compareTo(s2.getBiodata().getName());
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
	

}
