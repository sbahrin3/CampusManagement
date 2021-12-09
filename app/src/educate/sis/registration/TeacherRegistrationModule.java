/* ************************************************************************
LEBAH PORTAL FRAMEWORK, http://lebah.sf.net
Copyright (C) 2007  Shamsul Bahrin








MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

* ************************************************************************ */
package educate.sis.registration;


import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.velocity.Template;

import educate.sis.struct.FacultyData;
import educate.sis.struct.SubjectData;
import educate.sis.struct.entity.Subject;
import lebah.db.Db;
import lebah.db.PersistenceManager;
import lebah.db.SQLRenderer;

/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
 
public class TeacherRegistrationModule extends lebah.portal.velocity.VTemplate {
	protected boolean isLastPage = false;
	protected int LIST_ROWS = 10;		

	@Override
	public Template doTemplate() throws Exception {
		HttpSession session = request.getSession();
		
		String template_name = "vtl/sis/register/register_teacher.vm";
		String submit = getParam("command");
		prepareParameters();
		
		if ( "list_teachers".equals(submit) ) {
			template_name = "vtl/sis/register/subject_teachers.vm";	
			String subject_id = getParam("subject_id");
			context.put("subject", SubjectData.getSubject(subject_id));
			context.put("subject_id", subject_id);
			doTeacherList(subject_id);
		}
		else if ( "add_teacher".equals(submit) ) {
			template_name = "vtl/sis/register/subject_teachers.vm";	
			String subject_id = getParam("subject_id");
			String teacher_id = getParam("teacher_id");
			context.put("subject", SubjectData.getSubject(subject_id));
			addTeacher(subject_id, teacher_id);
			doTeacherList(subject_id);
		}
		else if ( "delete_teacher".equals(submit) ) {
			template_name = "vtl/sis/register/subject_teachers.vm";	
			String subject_id = getParam("subject_id");
			String teacher_id = getParam("teacher_id");
			removeTeacher(subject_id, teacher_id);
			doTeacherList(subject_id);
		}		
		
		Template template = engine.getTemplate(template_name);	
		return template;
	}
	
	private void prepareParameters() throws Exception {
		String[] faculties = request.getParameterValues("faculties");
		context.put("faculties", faculties);		
		Vector subjectList = SubjectData.getListOrderBy("name", faculties);
		context.put("subjectList", subjectList);
		
		
		//PersistenceManager pm = new PersistenceManager();
		
		new FacultyData();
		Vector facultyList = FacultyData.getList();
		context.put("facultyList", facultyList);
	}
	
	public void doTeacherList(String subject_id) throws Exception {
		Vector teacherList = getTeacherList(subject_id);
		context.put("teacherList", teacherList);		
	}
	
	public static Vector getTeacherList(String subject_id) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			r.add("member_id");
			r.add("user_name");
			r.add("role", "tutor");
			r.add("subject_id", subject_id);
			r.add("m.member_id", r.unquote("u.user_login"));
			sql = r.getSQLSelect("member_subject m, users u");
			ResultSet rs = stmt.executeQuery(sql);
			Vector list = new Vector();
			while ( rs.next() ) {
				Hashtable h = new Hashtable();
				h.put("teacher_id", rs.getString("member_id"));
				h.put("name", rs.getString("user_name"));
				list.addElement(h);
			}
			return list;
		} finally {
			if ( db != null ) db.close();
		}
	}
	
	/* 
	 * addTeacher will add the teacher into table member_subject for this subject.
	 * There are two tables, faculty_subject and subject in the database.
	 * This led to confusion.
	 * Actually, table subject is likely refer to classroom.
	 * So, when adding teacher to a subject (from table faculty_subject) from SIS, must add a classroom
	 * for this subject in table subject
	 */
	
	public static void addTeacher(String subject_id, String teacher_id) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			boolean found = false;
			{
				r.add("member_id");
				r.add("member_id", teacher_id);
				r.add("subject_id", subject_id);
				sql = r.getSQLSelect("member_subject");
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) found = true;
			}
			if ( !found ) {
				r.clear();
				r.add("member_id", teacher_id);
				r.add("subject_id", subject_id);
				r.add("role", "tutor");
				r.add("status", "active");
				r.add("module_id", "");
				sql = r.getSQLInsert("member_subject");
				stmt.executeUpdate(sql);

			}
			else {
				r.clear();
				r.update("member_id", teacher_id);
				r.update("subject_id", subject_id);
				r.add("role", "tutor");
				r.add("status", "active");
				r.add("module_id", "");
				sql = r.getSQLUpdate("member_subject");
				stmt.executeUpdate(sql);
				
			}
			
			
			//add to table subject (classroom)
			addClassroom(subject_id);			
			
		} finally {
			if ( db != null ) db.close();
		}
		
	}
	
	public static void removeTeacher(String subject_id, String teacher_id) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			r.add("member_id", teacher_id);
			r.add("subject_id", subject_id);
			sql = r.getSQLDelete("member_subject");
			
			stmt.executeUpdate(sql);
		} finally {
			if ( db != null ) db.close();
		}	
		
	}
	
	public static void addClassroom(String subject_id) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
            
            String code = "", title = "";
            /*
            {
                r.clear();
                r.add("subject_id", subject_id);
                r.add("subject_code");
                r.add("subject_name");
                sql = r.getSQLSelect("faculty_subject");
                ResultSet rs = stmt.executeQuery(sql);
                if ( rs.next() ) {
                    code = rs.getString("subject_code");
                    title = rs.getString("subject_name");
                }
            }
            */
            PersistenceManager pm = new PersistenceManager();
            Subject subject = (Subject) pm.find(Subject.class, subject_id);
            if ( subject != null ) {
            	code = subject.getCode();
            	title = subject.getName();
            }
			
			boolean found = false;
			{
                r.clear();
				r.add("subject_id");
				r.add("subject_id", subject_id);
				sql = r.getSQLSelect("subject");
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next()) found = true;
			}
			
			if ( !found ) {
				addClassroom(subject_id, code, title, r, stmt);
			} else {
                updateClassroom(subject_id, code, title, r, stmt);
            }

		} finally {
			if ( db != null ) db.close();
		}		
	}
	
	private static void addClassroom(String subject_id, String code, String title,
									 SQLRenderer r, Statement stmt) throws Exception {
		r.clear();
		r.add("subject_id", subject_id);
		r.add("subject_code", code);
		r.add("subject_title", title);
		r.add("subject_comment", "");
		r.add("subject_text", "");
		r.add("module_id", "");
        r.add("classroom_id", subject_id);
		String sql = r.getSQLInsert("subject");
		stmt.executeUpdate(sql);
	}
    
    private static void updateClassroom(String subject_id, String code, String title,
             SQLRenderer r, Statement stmt) throws Exception {
        
        boolean hasClassroomId = false;
        {
            r.clear();
            r.add("subject_id", subject_id);
            r.add("classroom_id");
            String sql = r.getSQLSelect("subject");
            ResultSet rs = stmt.executeQuery(sql);
            if ( rs.next() ) {
                String classroomId = rs.getString(1);
                if ( classroomId != null || !"".equals(classroomId)) {
                    hasClassroomId = true;
                }
            }
        }
        
        {
            r.clear();
            r.update("subject_id", subject_id);
            r.add("subject_code", code);
            r.add("subject_title", title);
            r.add("subject_comment", "");
            r.add("subject_text", "");
            r.add("module_id", "");
            if ( !hasClassroomId ) r.add("classroom_id", subject_id);
            String sql = r.getSQLUpdate("subject");
            stmt.executeUpdate(sql);
        }
    }    

	
	/**
	//-- start paging methods
	
	void prepareList(HttpSession session, Hashtable result) throws Exception {
		Vector list = (Vector) result.get("teacherList");
		int pages =  list.size() / LIST_ROWS;
		double leftover = ((double)list.size() % (double)LIST_ROWS);
		if ( leftover > 0.0 ) ++pages;
		context.put("pages", new Integer(pages));	
		session.setAttribute("pages", new Integer(pages));
			
		session.setAttribute("teacherList", list);
		getRows(session, 1);
		session.setAttribute("page_number", "1");
		context.put("page_number", new Integer(1));
		context.put("teacherList", list);		
	}	
	
	void getRows(HttpSession session, int page) throws Exception {
		Vector list = (Vector) session.getAttribute("teacherList");
		Vector items = getPage(page, LIST_ROWS, list);
		context.put("items", items);	
		context.put("page_number", new Integer(page));
		context.put("pages", (Integer) session.getAttribute("pages" ));			
	}
	
	Vector getPage(int page, int size, Vector list) throws Exception {
		int elementstart = (page - 1) * size;
		int elementlast = 0;
		//int elementlast = page * size < list.size() ? (page * size) - 1 : list.size() - 1;
		if ( page * size < list.size() ) {
			elementlast = (page * size) - 1;
			isLastPage = false;
			context.put("eol", new Boolean(false));
		} else {
			elementlast = list.size() - 1;
			isLastPage = true;
			context.put("eol", new Boolean(true));
		}
		if ( page == 1 ) context.put("bol", new Boolean(true));
		else context.put("bol", new Boolean(false));
		Vector v = new Vector();
		for ( int i = elementstart; i < elementlast + 1; i++ ) {
			v.addElement(list.elementAt(i));
		}
		
		return v;
	}
	
	//---- end paging methods	
	**/	
	
}			
