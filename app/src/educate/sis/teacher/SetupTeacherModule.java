package educate.sis.teacher;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.db.DbPersistence;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectSection;
import educate.sis.struct.entity.Teacher;
import educate.sis.struct.entity.TeacherSubject;
import lebah.app.FilesRepository;
import lebah.db.Db;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SetupTeacherModule extends LebahModule {
	
	private String path = "apps/teacher/setup";
	private DbPersistence db = new DbPersistence();

	@Override
	public void preProcess() {
		context.put("path", path);
		System.out.println(command);
	}
	
	@Override
	public String start() {
		List<Teacher> teachers = db.list("select t from educate.sis.struct.entity.Teacher t order by t.name");
		context.put("teachers", teachers);
		return path + "/start.vm";
	}
	
	@Command("listTeachers")
	public String listTeachers() {
		List<Teacher> teachers = db.list("select t from educate.sis.struct.entity.Teacher t order by t.name");
		context.put("teachers", teachers);
		return path + "/list_teachers.vm";
	}	
	
	@Command("getTeacher")
	public String getTeacher() throws Exception {
		String teacherId = getParam("teacherId");
		Teacher teacher = db.find(Teacher.class, teacherId);
		context.put("teacher", teacher);
		context.put("centres", db.list("select c from LearningCentre c order by c.code"));
		return path + "/teacher_info.vm";
	}
	
	@Command("addTeacher")
	public String addTeacher() throws Exception {
		context.remove("teacher");
		return path + "/teacher_info.vm";
	}	
	
	@Command("updateTeacher")
	public String updateTeacher() throws Exception {
		
		String code = getParam("teacherCode");
		String name = getParam("teacherName");
		String login = getParam("teacherLogin");
		String email = getParam("teacherEmail");

		String prevUsername = "";
		String teacherId = getParam("teacherId");
		Teacher teacher = db.find(Teacher.class, teacherId);
		if ( teacher != null ) prevUsername = teacher.getUserId();
		boolean exists = false;
		if ( !prevUsername.equals(login)) {
			Db db = null;
			try {
				db = new Db();
				ResultSet rs = db.getStatement().executeQuery("select user_login from users where user_login = '" + login + "'");
				exists = rs.next();
			} catch ( Exception e ) {
				e.printStackTrace();
			} finally {
				if ( db != null ) db.close();
			}
		}
		if ( !exists ) {
			TeacherService ts = new TeacherService();
			teacher = ts.save(teacherId, login, code, name, email);
			context.remove("login_exists");
		}
		else context.put("login_exists", true);
		context.put("teacher", teacher);
		
		return path + "/update.vm";
	}
	
	@Command("deleteTeacher")
	public String deleteTeacher() throws Exception {
		String teacherId = getParam("teacherId");
		Teacher teacher = db.find(Teacher.class, teacherId);
		String login = teacher.getUserId();
		
		db.begin();
		db.remove(teacher);
		db.commit();
		
		TeacherService ts = new TeacherService();
		ts.deleteTeacher(login);
		
		List<Teacher> teachers = db.list("select t from educate.sis.struct.entity.Teacher t order by t.name");
		context.put("teachers", teachers);
		
		return path + "/list_teachers.vm";
	}
	
	@Command("addLearningCentre")
	public String addLearningCentre() throws Exception {
		String teacherId = getParam("teacherId");
		Teacher teacher = db.find(Teacher.class, teacherId);
		context.put("teacher", teacher);
		String centreId = getParam("learningCentreId");
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		if ( centre != null && !teacher.getCentres().contains(centre)) {
			db.begin();
			teacher.getCentres().add(centre);
			db.commit();
		}
		return path + "/centres.vm";
	}
	
	@Command("removeLearningCentre")
	public String removeLearningCentre() throws Exception {
		String teacherId = getParam("teacherId");
		Teacher teacher = db.find(Teacher.class, teacherId);
		context.put("teacher", teacher);
		String centreId = getParam("removeCentreId");
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		db.begin();
		teacher.getCentres().remove(centre);
		db.commit();
		
		return path + "/centres.vm";
	}
	
	@Command("addClassroom")
	public String addClassroom() throws Exception {
		
		String teacherId = getParam("teacherId");
		Teacher teacher = db.find(Teacher.class, teacherId);
		context.put("teacher", teacher);
		
		String subjectId = getParam("subjectId");
		String sectionId = getParam("sectionId");
		
		Subject subject = db.find(Subject.class, subjectId);
		SubjectSection section = db.find(SubjectSection.class, sectionId);
		
		Hashtable h = new Hashtable();
		h.put("subject", subject);
		h.put("section", section);
		h.put("teacher", teacher);
		List<TeacherSubject> list = db.list("select t from TeacherSubject t where t.teacher = :teacher and t.subject = :subject and t.section = :section", h);
		if ( list.size() == 0 ) {
			db.begin();
			TeacherSubject ts = new TeacherSubject();
			ts.setSubject(subject);
			ts.setSection(section);
			ts.setTeacher(teacher);
			db.persist(ts);
			db.commit();
		} else {
			System.out.println("Classroom already exists.");
		}

		List<TeacherSubject> subjects = db.list("select t from TeacherSubject t where t.teacher.id = '" + teacherId + "' order by t.subject.code, t.section.code");
		context.put("subjects", subjects);
		context.put("subjectList", db.list("select s from Subject s order by s.code"));
		context.put("sectionList", db.list("select s from SubjectSection s order by s.code"));
		
		return path + "/list_subjects.vm";
	}
	
	@Command("deleteClassroom")
	public String deleteClassroom() throws Exception {
		
		String teacherSubjectId = getParam("teacherSubjectId");
		TeacherSubject teacherSubject = db.find(TeacherSubject.class, teacherSubjectId);
		String teacherId = teacherSubject.getTeacher().getId();
		
		db.begin();
		db.remove(teacherSubject);
		db.commit();
		
		List<TeacherSubject> subjects = db.list("select t from TeacherSubject t where t.teacher.id = '" + teacherId + "' order by t.subject.code, t.section.code");
		context.put("subjects", subjects);
		context.put("subjectList", db.list("select s from Subject s order by s.code"));
		context.put("sectionList", db.list("select s from SubjectSection s order by s.code"));
		
		return path + "/list_subjects.vm";
		
	}
	
	@Command("uploadPhoto")
	public String uploadPhoto() throws Exception {

		String uploadDir = FilesRepository.getUploadDir() + "teacherPhoto/";
		File dir = new File(uploadDir);
		if ( !dir.exists() ) dir.mkdir();
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = upload.parseRequest(request);
		Iterator itr = items.iterator();
		List<FileItem> files = new ArrayList<FileItem>();
		String teacherId = "";
		while (itr.hasNext()) {
			FileItem item = (FileItem)itr.next();
			if ((!(item.isFormField())) && (item.getName() != null) && (!("".equals(item.getName())))) {
				files.add(item);
			}
		}

		for ( FileItem item : files ) {
			String fileName = item.getName();
			String imgName = uploadDir + fileName;
			item.write(new File(imgName));
			String avatarName = imgName.substring(0, imgName.lastIndexOf(".")) + "_avatar" + imgName.substring(imgName.lastIndexOf("."));
			//lebah.repository.Thumbnail.create(imgName, imgName, 200, 160, 100);
			//lebah.repository.Thumbnail.create(imgName, avatarName, 50, 40, 100);
			System.out.println(fileName);
			
			teacherId = getParam("teacherId");
			Teacher teacher = db.find(Teacher.class, teacherId);
			context.put("teacher", teacher);
			db.begin();
			teacher.setPhotoFileName(imgName);
			teacher.setAvatarFileName(avatarName);
			db.commit();
		}
		
		return path + "/uploadphoto.vm";
	}
	
	public void saveAvatar(String userId, String avatarName) {
		try {
			lebah.db.Db database = new lebah.db.Db();
			String sql = "update users set avatar = '" + avatarName + "' where user_login = '" + userId + "'";
			database.getStatement().executeUpdate(sql);
		} catch ( Exception e ) {
			System.out.println(e.getMessage());
		}
	}


}
