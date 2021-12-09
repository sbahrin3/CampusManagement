package educate.sis.teacher;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectSection;
import educate.sis.struct.entity.Teacher;
import educate.sis.struct.entity.TeacherSubject;
import lebah.db.Db;
import lebah.db.SQLRenderer;

public class TeacherService {
	//private PersistenceManager  pm;
	
	
	public Teacher findById(String teacherId) {
		return new DbPersistence().find(Teacher.class, teacherId);
	}
	
	public Teacher findByUsername(String username) {
		Hashtable h = new Hashtable();
		h.put("username", username);
		List<Teacher> list = new DbPersistence().list("select t from educate.sis.struct.entity.Teacher t where t.userId = :username", h);
		return list.size() > 0 ? list.get(0) : null;
	}
	
	public void delete(String teacherId) throws Exception {
		Teacher teacher = findById(teacherId);
		DbPersistence db = new DbPersistence();
		db.begin();
		db.remove(teacher);
		db.commit();
	}
	
	public void update(Teacher teacher) throws Exception {
		DbPersistence db = new DbPersistence();
		db.begin();
		db.commit();
	}
	
	public void removeSubjects(Teacher teacher, String[] subjectIds) throws Exception {
		DbPersistence db = new DbPersistence();
		TeacherSubject[] subjects = new TeacherSubject[subjectIds.length];
		int cnt = 0;
		for ( String id : subjectIds) {
			TeacherSubject s = db.find(TeacherSubject.class, id);
			subjects[cnt++] = s;
		}
		
		db.begin();
		for ( TeacherSubject ts : subjects ) {
			teacher.removeTeacherSubject(ts);
		}
		db.commit();
	}
	
	public Teacher save(String username, String code, String name, String email) throws Exception{
		return save(false, username, code, name, email);
	}
	
	public Teacher saveIgnoreUserExist(String username, String code, String name, String email) throws Exception{
		return save(true, username, code, name, email);
	}
	
	public Teacher save(boolean forceAdd, String username, String code, String name, String email) throws Exception{
		DbPersistence db = new DbPersistence();
		Teacher teacher = (Teacher) db.get("select t from Teacher t where t.userId = '" + username + "'");
		if( teacher != null ){
			db.begin();
			teacher.setEmail(email);
			teacher.setName(name);
			if ( code != null ) teacher.setCode(code);
			db.commit();
		} else {
			if ( createLogin(name, username, username, "teacher", "default.css") ) {
				teacher = addNewTeacher(db, code, username, name, email);
			} else if ( forceAdd ) {
				teacher = addNewTeacher(db, code, username, name, email);
			}
		}
		return teacher;
	}
	
	public Teacher save(String teacherId, String username, String code, String name, String email) throws Exception{
		DbPersistence db = new DbPersistence();
		//Teacher teacher = (Teacher) db.get("select t from Teacher t where t.userId = '" + username + "'");
		Teacher teacher = db.find(Teacher.class, teacherId);
		
		if( teacher != null ){
			String prevUsername = teacher.getUserId();
			db.begin();
			teacher.setUserId(username);
			teacher.setEmail(email);
			teacher.setName(name);
			if ( code != null ) teacher.setCode(code);
			db.commit();
			
			TeacherService ts = new TeacherService();
			ts.deleteTeacher(prevUsername);
			
			createLogin(name, username, username, "teacher", "default.css");
			
		} else {
			if ( createLogin(name, username, username, "teacher", "default.css") ) {
				teacher = addNewTeacher(db, code, username, name, email);
			}
		}
		return teacher;
	}	

	private Teacher addNewTeacher(DbPersistence db, String code, String username, String name, String email) throws Exception {
		Teacher teacher = new Teacher();
		db.begin();
		teacher.setUserId(username);
		teacher.setCode(code);
		teacher.setName(name);
		teacher.setEmail(email);
		db.persist(teacher);
		db.commit();
		return teacher;
	}
	
	public Vector teacherSubjectList(String teacher_id)throws Exception{
		
		Vector<TeacherSubject> v = new Vector<TeacherSubject>();
		DbPersistence db = new DbPersistence();
		List<TeacherSubject> teacherSubjects = db.list("select t from TeacherSubject t where t.teacher.userId = '" + teacher_id + "'");
		for ( TeacherSubject ts : teacherSubjects ) {
			v.add(ts);
		}
		/*
		System.out.println("+++");
		
		List<Teacher> l = db.list("select a from educate.sis.struct.entity.Teacher a WHERE a.userId='"+teacher_id+"'");
		if(l.size()>0){
			Set<TeacherSubject> subjects = l.get(0).getTeacherSubjects();
			if (subjects != null) {
				
				for ( TeacherSubject s : subjects ) {
					System.out.println(s.getSubject().getName());
				}
				
				v.addAll(subjects);
			}
		}
		*/
		return v;
	}
	

	public void addTeacherSubjectLMS(TeacherSubject teacherSubject, String userId) throws Exception{
		Db db = null;
		try{
			db = new Db();

			//this subjectId is a subject code
			String subjectId = teacherSubject.getSubject().getCode();
			doAddTeacherSubjectLMS(db, teacherSubject, subjectId, null, userId);
			
			//this subjectId is a combination of subject code and section code
			//and has a parent subject id
			String subjectGroupId = teacherSubject.getSubject().getCode()+"_"+ teacherSubject.getSection().getCode();
			doAddTeacherSubjectLMS(db, teacherSubject, subjectGroupId, subjectId, userId);
			
		}
		catch(Exception e){
			System.err.println(e.getMessage());
		}
		finally{
			if ( db != null ) db.close();
		}
	}

	private void doAddTeacherSubjectLMS(Db db, TeacherSubject teacherSubject, String subjectId, String parentSubjectId, String userId) throws SQLException {
		
		String teacher_id = userId;
		String sql;
		Statement stmt = db.getStatement();
		SQLRenderer r = new SQLRenderer();

		boolean found = false;
		{
			r.clear();
			r.add("subject_code");
			r.add("subject_id");
			r.add("subject_id", subjectId);
			sql = r.getSQLSelect("subject");
			ResultSet rs = stmt.executeQuery(sql);	
			if ( rs.next() ) found = true;
		}
		if ( !found ) {
			
			//add data into table subject
			{
				r.clear();
				r.add("subject_id",subjectId);
		        //by default, classroom_id is the same as subject_id
		        //classroom_id refer to subject_id
		        //because sometime, a subject may use other subject as classroom
				r.add("classroom_id", subjectId);
				r.add("subject_code", teacherSubject.getSubject().getCode());
				r.add("subject_title",  teacherSubject.getSubject().getName());
				//module id must be empty space (not null)
				r.add("module_id", "");
				r.add("subject_comment", "");
				r.add("subject_text", "");
				if ( parentSubjectId != null) {
					r.add("subjectGroup", teacherSubject.getSection().getName());
					r.add("is_group", 1);
					r.add("parent_id", parentSubjectId);
				}
				else {
					r.add("subjectGroup", "");
					r.add("is_group", 0);
					r.add("parent_id", "");
				}
				sql = r.getSQLInsert("subject");
				stmt.executeUpdate(sql);
			}
		} else {
		    // if found then check its subject code
		    // if different then update
		    String subject_code = "";
		    {
		        r.clear();
		        r.add("subject_id",subjectId);
		        r.add("subject_code");
		        sql = r.getSQLSelect("subject");
		        ResultSet rs = stmt.executeQuery(sql);
		        
		        if ( rs.next()) {
		            subject_code = rs.getString(1);
		        }
		    }
		    
		    //if ( !teacherSubject.getSubject().getCode().equals(subject_code)) 
		    {
		        r.clear();
		        r.update("subject_id",subjectId);
		        r.add("subject_code", teacherSubject.getSubject().getCode());
		        r.add("subject_title", teacherSubject.getSubject().getName());
		        if ( parentSubjectId != null ) {
		        	r.add("subjectGroup", teacherSubject.getSection().getName());
		        	r.add("is_group", 1);
		        	r.add("parent_id", parentSubjectId);
		        }
		        else {
		        	r.add("subjectGroup", "");
		        	r.add("is_group", 0);
		        	r.add("parent_id", "");
		        }
		        
		        sql = r.getSQLUpdate("subject");
		        stmt.executeUpdate(sql);
		    }

		}
		{
			r.clear();
			r.add("subject_id");
			r.add("member_id", teacher_id);
			r.add("subject_id", subjectId);
			sql = r.getSQLSelect("member_subject");
		    
			ResultSet rs = stmt.executeQuery(sql);
			if ( rs.next()) found = true;
			else found = false;
			
		}
		if ( !found ){
			//then add
			r.clear();
			r.add("member_id", teacher_id);
			r.add("subject_id", subjectId);
			r.add("role", "tutor");
			r.add("status", "active");
			r.add("module_id", "");
			sql = r.getSQLInsert("member_subject");
			//-
			stmt.executeUpdate(sql);
		}
	}
	public void deleteTeacherSubjectLMS(String teacher_id) throws Exception{
		Db db = null;
	    String sql = "";
	    try {
	        db = new Db();
	        Statement stmt = db.getStatement();
			sql = "delete from member_subject where member_id = '" + teacher_id + "'";
			stmt.executeUpdate(sql);
	        
	    } finally {
	        if ( db != null ) db.close();
	    }
	}
	
	public boolean createLogin(String fullname, String username, String password, String role, String style) throws Exception {
		//this will return false if username already exist
		return lebah.portal.db.RegisterUser.add(fullname, username, password, role, style);
	}
	
	public List<TeacherVO> listTeacherVO() throws Exception {
		return listTeacherVO("");
	}
	
	public void deleteTeacher(String login) throws Exception {
		Db db = null;
		try {
			db = new Db();
			db.getStatement().executeUpdate("delete from users where user_login = '" + login + "'");
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( db != null ) db.close();
		}
	}
	
	public List<TeacherVO> listTeacherVO(String namePattern) throws Exception {
		List<TeacherVO> users = new ArrayList<TeacherVO>();
		String sql = "select user_login, user_name from users where user_role LIKE 'teacher%'";
		if ( !"".equals(namePattern)) sql += " and user_name like '%" + namePattern + "%'";
		sql += " UNION " +
		"select distinct user_login, user_name from users u " +
		"join user_role r " +
		"where u.user_login = r.user_id " +
		"and r.role_id LIKE 'teacher%' ";
		if ( !"".equals(namePattern)) sql += " and user_name like '%" + namePattern + "%'";
		sql += " order by user_login";
		Db db = null;
		try {
			db = new Db();
			ResultSet rs = db.getStatement().executeQuery(sql);
			while ( rs.next() ) {
				TeacherVO user = new TeacherVO();
				user.username = rs.getString(1);
				user.name = rs.getString(2);
				Teacher teacher = findByUsername(user.username);
				if ( teacher != null ) {
					user.teacher = teacher;
					user.code = teacher.getCode();
					user.hasEntity = true;
				}
				else {
					user.code = "";
				}
				users.add(user);
			}
		} finally {
			if ( db != null ) db.close();
		}
		return users;
	}
	
	public TeacherVO getTeacherVO(String username) throws Exception {
		TeacherVO user = new TeacherVO();
		String sql = "select user_login, user_name from users where user_role LIKE 'teacher%'";
		sql += " and user_login = '" + username + "'";
		Db db = null;
		try {
			db = new Db();
			ResultSet rs = db.getStatement().executeQuery(sql);
			if ( rs.next() ) {
				user.username = rs.getString(1);
				user.name = rs.getString(2);
				user.hasLogin = true;
				Teacher teacher = findByUsername(user.username);
				if ( teacher != null ) {
					user.teacher = teacher;
					user.hasEntity = true;
				}
			}
		} finally {
			if ( db != null ) db.close();
		}
		return user;
	}	
	
	public List<Teacher> listTeachers() throws Exception {
		List<TeacherVO> users = listTeacherVO();
		List<Teacher> teachers = new DbPersistence().list("select t from Teacher t order by t.name");
		for ( Teacher teacher : teachers ) {
			System.out.println(teacher.getUserId());
		}
		return teachers;
	}
	
	public List<Subject> listSubjects() throws Exception {
		return listSubjects("");
	}
	
	public List<Subject> listSubjects(String facultyId) throws Exception {
		DbPersistence db = new DbPersistence();
		String sql = "select s from Subject s ";
		if ( !"".equals(facultyId)) sql += " where s.faculty.id = '" + facultyId + "' ";
		sql += " order by s.code";
		return db.list(sql);
	}
	
	public List<Faculty> listFaculties() throws Exception {
		DbPersistence db = new DbPersistence();
		return db.list("select f from Faculty f order by f.code");
	}
	
	public Faculty findFaculty(String id) {
		DbPersistence db = new DbPersistence();
		return db.find(Faculty.class, id);
	}
	
	public Subject findSubject(String id) {
		DbPersistence db = new DbPersistence();
		return db.find(Subject.class, id);
	}
	
	public List<Subject> listFacultySubjects(String facultyId) {
		DbPersistence db = new DbPersistence();
		return db.list("select s from Subject s where s.faculty.id = '" + facultyId + "' order by s.code");
	}
	

	public List<SubjectSection> listSubjectSections() throws Exception {
		DbPersistence db = new DbPersistence();
		return db.list("select s from SubjectSection s order by s.code");
	}
	
	public SubjectSection findSubjectSection(String id) {
		DbPersistence db = new DbPersistence();
		return db.find(SubjectSection.class, id);
	}
	
	
	public static void main(String[] args) throws Exception {


	}
	
	
}
