package educate.sis.teacher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectSection;
import educate.sis.struct.entity.Teacher;
import educate.sis.struct.entity.TeacherSubject;
import lebah.portal.action.AjaxModule;

public class TeacherMgmtModule extends AjaxModule  {
	
	String path = "apps/util/teacher_util/";
	String vm = "";
	DbPersistence db = new DbPersistence();
	
	
	public String doAction() throws Exception {
		String command = request.getParameter("command") != null ? request.getParameter("command") : "";
		context.remove("error_message");
		context.remove("values");
		
		System.out.println(command);
		
		if ( "".equals(command)) newTeacher();
		else if ( "new".equals(command)) newTeacher();
		else if ( "add".equals(command)) addTeacher(false);
		else if ( "forceadd".equals(command)) addTeacher(true);
		else if ( "update".equals(command)) updateTeacher();
		else if ( "inline_edit".equals(command)) inlineEdit();
		else if ( "inline_update".equals(command)) inlineUpdate();
		else if ( "delete".equals(command)) deleteTeacher();
		else if ( "list_teachers".equals(command)) listTeachers();
		else if ( "teacher_info".equals(command)) teacherInfo();
		else if ( "new_info".equals(command)) teacherInfoNew();
		else if ( "list_subjects".equals(command)) listSubjects();
		else if ( "assign_subjects".equals(command)) listSubjects();
		else if ( "remove_subjects".equals(command)) removeSubjects();
		else if ( "list_subjects_faculty".equals(command)) listSubjectsFaculty();
		else if ( "select_subjects_faculty".equals(command)) selectSubjectsFaculty();
		else if ( "select_subjects_faculty2".equals(command)) selectSubjectsFaculty2();
		else if ( "delete_selected_subjects".equals(command)) listSubjectsFaculty();
		else if ( "select_subjects".equals(command)) selectSubjects();
		else if ( "assign_sections".equals(command)) assignSubjectSections();
		return vm;
	}


	private void inlineEdit() throws Exception {
		vm = path + "div_teacher_edit.vm";
		String username = request.getParameter("username");
		String teacherId = request.getParameter("teacher_id");
		String cnt = request.getParameter("cnt");
		context.put("cnt", cnt);
		Teacher teacher = (Teacher) db.find(Teacher.class, teacherId);
		context.put("teacher", teacher);
		TeacherVO t = new TeacherVO();
		t.teacher = teacher;
		t.username = username;
		context.put("t", t);

		
	}


	private void inlineUpdate() throws Exception {
		vm = path + "div_teacher_list.vm";
		String username = request.getParameter("username");
		String teacherId = request.getParameter("teacher_id");
		String cnt = request.getParameter("cnt");
		context.put("cnt", cnt);
		
		if ( !"".equals(teacherId)) {
			Teacher teacher = (Teacher) db.find(Teacher.class, teacherId);
			context.put("teacher", teacher);
			String name = request.getParameter("name_" + cnt);
			String code = request.getParameter("code_" + cnt);
			db.begin();
			teacher.setCode(code);
			teacher.setName(name);
			db.commit();
			TeacherVO t = new TeacherVO();
			t.teacher = teacher;
			t.username = username;
			context.put("t", t);
		}
		else {
			Teacher teacher = new Teacher();
			context.put("teacher", teacher);
			String name = request.getParameter("name_" + cnt);
			String code = request.getParameter("code_" + cnt);
			db.begin();
			teacher.setUserId(username);
			teacher.setCode(code);
			teacher.setName(name);
			db.persist(teacher);
			db.commit();
			TeacherVO t = new TeacherVO();
			t.teacher = teacher;
			t.username = username;
			context.put("t", t);
		}
		


	}


	private void assignSubjectSections() throws Exception {
		//vm = path + "subject_sections.vm";
		vm = path + "teacher_info.vm";
		TeacherService s = new TeacherService();
		String teacherId = request.getParameter("teacher_id");
		Teacher teacher = s.findById(teacherId);
		context.put("teacher", teacher);
		
		List<SubjectSection> subjectSections = s.listSubjectSections();
		context.put("subjectSections", subjectSections);
		
		String facultyId = request.getParameter("faculty_id");
		Faculty faculty = s.findFaculty(facultyId);
		context.put("faculty", faculty);
		
		HttpSession session = request.getSession();
		Set<Subject> selectedSubjects = (Set<Subject>) session.getAttribute("_selected_subjects");
		context.put("subjects", selectedSubjects);
		
		String[] subject_sections = request.getParameterValues("subject_sections");
		if ( subject_sections != null ) {
			
			Subject[] subjects = new Subject[subject_sections.length];
			SubjectSection[] sections = new SubjectSection[subject_sections.length];
			int cnt = 0;
			for ( String id : subject_sections ) {
				String subjectId = id.substring(0, id.indexOf("_"));
				String sectionId = id.substring(id.indexOf("_") + 1);
				Subject subject = s.findSubject(subjectId);
				SubjectSection section = s.findSubjectSection(sectionId);
				subjects[cnt] = subject;
				sections[cnt] = section;
				cnt++;
			}
			
			for ( int i=0; i < subject_sections.length; i++ ) {
				TeacherSubject ts = new TeacherSubject();
				ts.setSubject(subjects[i]);
				ts.setSection(sections[i]);
				System.out.println("section=" + sections[i]);
				teacher.addTeacherSubject(ts);
			}
			
			//update teacher into persistence
			s.update(teacher);
		
		}
		
	}


	private void selectSubjects() throws Exception {
		vm = path + "subject_sections.vm";
		TeacherService s = new TeacherService();
		String teacherId = request.getParameter("teacher_id");
		Teacher teacher = s.findById(teacherId);
		context.put("teacher", teacher);
		
		List<SubjectSection> subjectSections = s.listSubjectSections();
		context.put("subjectSections", subjectSections);
		
		String facultyId = request.getParameter("faculty_id");
		Faculty faculty = s.findFaculty(facultyId);
		context.put("faculty", faculty);
		
		HttpSession session = request.getSession();
		Set<Subject> selectedSubjects = (Set<Subject>) session.getAttribute("_selected_subjects");
		
		List<Subject> subjects = new ArrayList<Subject>();
		subjects.addAll(selectedSubjects);
		Collections.sort(subjects, new SubjectComparator());
		
		context.put("subjects", subjects);
		
	}
	
	private void selectSubjectsFaculty2() throws Exception {
		vm = path + "div_subjects2.vm";
		TeacherService s = new TeacherService();
		
		HttpSession session = request.getSession();
		Set<Subject> selectedSubjects = (Set<Subject>) session.getAttribute("_selected_subjects");
		String[] ids = request.getParameterValues("selected_subject_ids");
		if ( ids != null ) {
			for ( String id : ids ) {
				Subject subject2 = s.findSubject(id);
				selectedSubjects.remove(subject2);
			}
		}
		if ( selectedSubjects == null ) {
			selectedSubjects = new HashSet<Subject>();
			session.setAttribute("_selected_subjects", selectedSubjects);
		}
		String[] subject_ids = request.getParameterValues("subject_ids");
		if ( subject_ids != null ) {
			for ( String id : subject_ids) {
				Subject subject = s.findSubject(id);
				selectedSubjects.add(subject);
			}
		}
		
		List<Subject> subjects = new ArrayList<Subject>();
		subjects.addAll(selectedSubjects);
		Collections.sort(subjects, new SubjectComparator());
		context.put("selected_subjects", subjects);
		
	}
	
	public static class SubjectComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			Subject s1 = (Subject) o1;
			Subject s2 = (Subject) o2;
			return s1.getCode().compareTo(s2.getCode());
		}
		
	}
	
	private void selectSubjectsFaculty() throws Exception {
		vm = path + "div_subjects1.vm";
		TeacherService s = new TeacherService();

		String facultyId = request.getParameter("faculty_id");
		if ( !"".equals(facultyId)) {
			Faculty faculty = s.findFaculty(facultyId);
			context.put("faculty", faculty);
			List<Subject> subjects = s.listFacultySubjects(facultyId);
			context.put("subjects", subjects);
		}
		else {
			context.remove("faculty");
			List<Subject> subjects = s.listSubjects();
			context.put("subjects", subjects);
		}
	}


	private void listSubjectsFaculty() throws Exception {
		vm = path + "list_subjects.vm";
		TeacherService s = new TeacherService();
		String teacherId = request.getParameter("teacher_id");
		Teacher teacher = s.findById(teacherId);
		context.put("teacher", teacher);
		
		Set<TeacherSubject> teachSubjects = teacher.getTeacherSubjects();
		String[] subjectIds = new String[teachSubjects.size()];
		int cnt = 0;
		for ( TeacherSubject teachSubject : teachSubjects ) subjectIds[cnt++] = teachSubject.getSubject().getId();
		
		List<SubjectSection> subjectSections = s.listSubjectSections();
		context.put("subjectSections", subjectSections);
		
		List<Faculty> faculties = s.listFaculties();
		context.put("faculties", faculties);
		
		String facultyId = request.getParameter("faculty_id");
		Faculty faculty = s.findFaculty(facultyId);
		context.put("faculty", faculty);
		
		List<Subject> subjects = s.listFacultySubjects(facultyId);
		context.put("subjects", subjects);
		
		HttpSession session = request.getSession();
		Set<Subject> selectedSubjects = (Set<Subject>) session.getAttribute("_selected_subjects");
		String[] ids = request.getParameterValues("selected_subject_ids");
		if ( ids != null ) {
			for ( String id : ids ) {
				Subject subject2 = s.findSubject(id);
				selectedSubjects.remove(subject2);
			}
		}
		if ( selectedSubjects == null ) {
			selectedSubjects = new HashSet<Subject>();
			session.setAttribute("_selected_subjects", selectedSubjects);
		}
		String[] subject_ids = request.getParameterValues("subject_ids");
		if ( subject_ids != null ) {
			for ( String id : subject_ids) {
				Subject subject = s.findSubject(id);
				selectedSubjects.add(subject);
			}
		}
		context.put("selected_subjects", selectedSubjects);
		
	}


	private void removeSubjects() throws Exception {
		vm = path + "teacher_info.vm";
		TeacherService s = new TeacherService();
		String teacherId = request.getParameter("teacher_id");
		Teacher teacher = s.findById(teacherId);
		context.put("teacher", teacher);
		
		String[] ids = request.getParameterValues("subject_teachings");
		if ( ids != null ) {
			s.removeSubjects(teacher, ids);
		}
	
	}


	private void updateTeacher() throws Exception {
		vm = path + "teacher_info.vm";
		TeacherService s = new TeacherService();
		String teacherId = request.getParameter("teacher_id");
		Teacher teacher = s.findById(teacherId);
		
		teacher.setCode(request.getParameter("code"));
		teacher.setName(request.getParameter("name"));
		teacher.setEmail(request.getParameter("email"));
		
		String assesment = request.getParameter("assesment");
		String finalExam = request.getParameter("final_exam");
		
		if ( !"".equals(assesment)) teacher.setAssesment(assesment);
		if ( !"".equals(finalExam)) teacher.setFinalExam(finalExam);
		
		s.update(teacher);
		
		context.put("teacher", teacher);
		
	}


	private void deleteTeacher() throws Exception {
		vm = path + "add_teacher.vm";
		TeacherService s = new TeacherService();
		String teacherId = request.getParameter("teacher_id");
		s.delete(teacherId);

	}
	
	private void teacherInfoNew() throws Exception {
		HttpSession session = request.getSession();
		session.removeAttribute("_selected_ids");
		vm = path + "teacher_info.vm";
		
		String username = request.getParameter("username");
		
		TeacherService s = new TeacherService();
		s.save(true, username, null, username, username);

		Teacher teacher = s.findByUsername(username);
		context.put("teacher", teacher);

	}

	private void teacherInfo() throws Exception {
		HttpSession session = request.getSession();
		String namePattern = request.getParameter("name_pattern");
		context.put("name_pattern", namePattern);
		session.removeAttribute("_selected_ids");
		vm = path + "teacher_info.vm";
		TeacherService s = new TeacherService();
		String teacherId = request.getParameter("teacher_id");
		Teacher teacher = s.findById(teacherId);
		context.put("teacher", teacher);
		
		
	}

	private void listSubjects() throws Exception {
		HttpSession session = request.getSession();
		session.removeAttribute("_selected_ids");
		session.removeAttribute("_selected_subjects");
		vm = path + "list_subjects.vm";
		TeacherService s = new TeacherService();
		String teacherId = request.getParameter("teacher_id");
		Teacher teacher = s.findById(teacherId);
		context.put("teacher", teacher);
		
		//get list of faculties first
		List<Faculty> faculties = s.listFaculties();
		context.put("faculties", faculties);
		
	}

	private void listTeachers() throws Exception {
		vm = path + "list_teachers.vm";
		String namePattern = request.getParameter("name_pattern");
		context.put("name_pattern", namePattern);
		TeacherService s = new TeacherService();
		context.put("teachers", s.listTeacherVO(namePattern));
	}
	
	private void addTeacher(boolean forceAdd) {
		
		vm = path + "add_teacher.vm";

		TeacherVO user = new TeacherVO();
		user.code = request.getParameter("code");
		user.name =  request.getParameter("name");
		user.email = request.getParameter("email");
		user.username = request.getParameter("username");
		user.password = request.getParameter("username");  //password same as username
		
		context.put("user", user);
		
		if ( "".equals(user.name) || "".equals(user.email) || "".equals(user.username) || "".equals(user.password)) {
			context.put("error_message", "All fields are required!");
			return;
		}
		
		TeacherService ts = new TeacherService();
		try {
			Teacher teacher = null;
			if ( forceAdd )
				teacher = ts.saveIgnoreUserExist(user.getUsername(), user.getCode(), user.getName(), user.getEmail());
			else
				teacher = ts.save(user.getUsername(), user.getCode(), user.getName(), user.getEmail());
			if ( teacher == null ) {
				context.put("error_message", "Data already exist!");
			}
			else {
				vm = path + "teacher_info.vm";
				context.put("teacher", teacher);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	private void newTeacher() throws Exception {
		context.remove("user");
		vm = path + "add_teacher.vm";
		String username = request.getParameter("username");
		if ( username != null && !"".equals(username) ) {
			TeacherService s = new TeacherService();
			TeacherVO user = s.getTeacherVO(username);
			context.put("user", user);
		}
		 
	}
	
	
	public static void main(String[] args) {
		
	}

}
