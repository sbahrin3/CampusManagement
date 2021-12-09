package educate.sis.teacher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectSection;
import educate.sis.struct.entity.TeacherSubject;
import lebah.app.FilesRepository;
import lebah.db.Db;
import lebah.db.SQLRenderer;
import lebah.portal.action.AjaxModule;

public class BatchUploadAssignmentModule  extends AjaxModule {
	
	String path = "apps/util/teacher_util/assignment/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		String command = request.getParameter("command");
		
		data = (Hashtable) session.getAttribute("_data");
		if ( null == data ) {
			data = new Hashtable();
			session.setAttribute("_data", data);
		}
		context.put("data", data);
		
		//System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "create_assignment".equals(command)) assignment();
		else if ( "save".equals(command)) save();
		else if ( "upload".equals(command)) upload();
		else if ( "doneUploadFiles".equals(command)) doneUploadFiles();
		else if ( "list_subjects_faculty".equals(command)) listSubjectsFaculty();
		else if ( "select_subjects".equals(command)) selectSubjects();
		else if ( "submit_assignment".equals(command)) submitAssignmentToGroups();
		return vm;
	}
	
	void doneUploadFiles() {
		vm = path + "list_uploads.vm";
		System.out.println("Done uploading files.");
		List<String> files = (List<String>) session.getAttribute("uploaded_files");
		List<String> filenames = new ArrayList();
		for ( String f : files ) {
			filenames.add(f.substring(f.lastIndexOf("/")+1));
		}
		data.put("files", files);
		data.put("filenames", filenames);
	}

	private void upload() {
		vm = path + "upload.vm";
		String subject = request.getParameter("subject");
		String message = request.getParameter("message");
		data.put("subject", subject);
		data.put("message", message);
	}

	private void save() throws Exception {
		listSubjects();
		
		
	}

	private void start() {
		vm = path + "create.vm";
		data.clear();
	}	
	
	private void assignment() {
		vm = path + "create.vm";
		
	}
	
	private void listSubjects() throws Exception {
		HttpSession session = request.getSession();
		session.removeAttribute("_selected_ids");
		session.removeAttribute("_selected_subjects");
		vm = path + "list_subjects.vm";
		TeacherService s = new TeacherService();
		//get list of faculties first
		List<Faculty> faculties = s.listFaculties();
		context.put("faculties", faculties);
		
	}
	
	private void listSubjectsFaculty() throws Exception {
		vm = path + "list_subjects.vm";
		TeacherService s = new TeacherService();

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
	
	private void selectSubjects() throws Exception {
		vm = path + "subject_sections.vm";
		TeacherService s = new TeacherService();
		
		List<SubjectSection> subjectSections = s.listSubjectSections();
		context.put("subjectSections", subjectSections);
		
		String facultyId = request.getParameter("faculty_id");
		Faculty faculty = s.findFaculty(facultyId);
		context.put("faculty", faculty);
		
		HttpSession session = request.getSession();
		Set<Subject> selectedSubjects = (Set<Subject>) session.getAttribute("_selected_subjects");
		context.put("subjects", selectedSubjects);
		
	}	
	
	private void submitAssignmentToGroups() throws Exception {
		vm = path + "submitted.vm";
		TeacherService s = new TeacherService();
		
		List<SubjectSection> subjectSections = s.listSubjectSections();
		context.put("subjectSections", subjectSections);
		
		String facultyId = request.getParameter("faculty_id");
		Faculty faculty = s.findFaculty(facultyId);
		context.put("faculty", faculty);
		
		HttpSession session = request.getSession();
		Set<Subject> selectedSubjects = (Set<Subject>) session.getAttribute("_selected_subjects");
		context.put("subjects", selectedSubjects);
		
		String[] subject_sections = request.getParameterValues("subject_sections");
		List<SubjectVO> subjectList = new ArrayList<SubjectVO>();
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
				
				SubjectVO vo = new SubjectVO(subject.getCode(), subject.getName(), section.getCode(), section.getName());
				subjectList.add(vo);
				cnt++;
			}
			context.put("subjectList", subjectList);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Hashtable data = (Hashtable) session.getAttribute("_data");
			String subject = data.get("subject") != null ? (String) data.get("subject") : "";
			String message = data.get("message") != null ? (String) data.get("message") : "";
			Db db = null;
			try {
				db = new Db();
				String assignmentId = lebah.db.UniqueID.getUID(); 
				for ( int i=0; i < subject_sections.length; i++ ) {
					TeacherSubject ts = new TeacherSubject();
					ts.setSubject(subjects[i]);
					ts.setSection(sections[i]);
					
					String subjectCode = ts.getSubject().getCode() + "_" + ts.getSection().getCode();
					
					String parent_id = "0"; 
					String category_id = "ass_" + subjectCode; 
					String member_id = (String) session.getAttribute("_portal_login"); 
					String posted_date = df.format(new Date()); 
					String title = subject;
					int is_parent = 1;
					int is_delete = 0;
					String message_text = message;
					String classroom_id = subjectCode;
										
					SQLRenderer r = new SQLRenderer();
					r.add("id", assignmentId)
					.add("parent_id", parent_id)
					.add("category_id", category_id)
					.add("member_id", member_id)
					.add("posted_date", posted_date)
					.add("title", title)
					.add("is_parent", is_parent)
					.add("is_delete", is_delete)
					.add("message_text", message_text)
					.add("classroom_id", classroom_id);
					String sql = r.getSQLInsert("forum");
					System.out.println(sql);
					db.getStatement().executeUpdate(sql);
					
				}
				
				String upload_dir = FilesRepository.getUploadDir() + getId() + "/" + (String) session.getAttribute("_portal_login") + "/";
				
				List<String> files = (List<String>) session.getAttribute("uploaded_files");
				List<String> filenames = new ArrayList();
				for ( String f : files ) {
					String name = f.substring(f.lastIndexOf("/")+1);
					String dir = f.substring(0, f.lastIndexOf("/")+1);
					filenames.add(name);
					save_attachment(assignmentId, name, dir, db);
				}
				data.put("filenames", filenames);
				context.put("assignment_data", data);
			} finally {
				if ( db != null) db.close();
			}
		}
		
	}
	
	private void save_attachment(String forumid, String filename, String subdirectory, Db db) throws Exception {
		String dir = FilesRepository.getUploadDir();
		subdirectory = subdirectory.substring(dir.length());
		System.out.println(subdirectory);
		String sql = "insert into forum_attachment (forum_id, file_name, directory) values ('" +
		forumid + "', '" + filename + "', '" + subdirectory + "') ";
		db.getStatement().executeUpdate(sql);
	}

}
