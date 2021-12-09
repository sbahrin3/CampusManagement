/* ************************************************************************
LEBAH PORTAL FRAMEWORK, http://lebah.sf.net
Copyright (C) 2007  Shamsul Bahrin








MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

* ************************************************************************ */
package educate.sis.struct;


import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import educate.sis.struct.entity.Subject;
import lebah.db.Db;
import lebah.db.SQLRenderer;

/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
public class SubjectData {
	
	public static Vector getStudentSubjects(String studentId, String periodId) throws Exception{
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			r.add("f.subject_id");
			r.add("f.subject_code");
			r.add("f.subject_name");
			r.add("f.subject_name_alt");
			r.add("f.remark");
			r.add("f.synopsis");
			r.add("f.credit_hrs");
			r.relate("s.subject_id", "f.subject_id");
			r.add("s.student_id", studentId);
			r.add("s.period_id", periodId);
			
			sql = r.getSQLSelect("faculty_subject f, student_subject s");
			
			ResultSet rs = stmt.executeQuery(sql);
			Vector list = new Vector();
			while ( rs.next() ) {
				Subject subject = new Subject();
				subject.setId(rs.getString("subject_id"));
				subject.setCode(rs.getString("subject_code"));
				subject.setName(rs.getString("subject_name"));
				subject.setAltName(Db.getString(rs, "subject_name_alt"));
				subject.setRemarks(Db.getString(rs, "remark"));
				subject.setSynopsis(Db.getString(rs, "synopsis"));
				subject.setCredithrs(rs.getInt("credit_hrs"));
				list.addElement(subject);
			}
			return list;
		} finally {
			if ( db != null ) db.close();
		}				

	}
	
	public static Vector getList() throws Exception {
		return getListOrderBy("");
	}
	
	public static Vector getListOrderBy(String orderBy) throws Exception {
		return getListOrderBy(orderBy, null);
	}
	
	public static Vector getListOrderBy(String orderBy, String[] faculties) throws Exception {
		if ( faculties == null) return new Vector();
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			r.add("subject_id");
			r.add("subject_code");
			r.add("subject_name");
			r.add("subject_name_alt");
			r.add("remark");
			r.add("synopsis");
			r.add("credit_hrs");
			
			sql = r.getSQLSelect("faculty_subject");
			
			if ( faculties != null ) {
				sql += " WHERE (";
				int i=0;
				for ( String faculty : faculties ) {
					if ( ++i > 1 ) sql += " OR ";
					sql += " faculty_id = '" + faculty + "' ";
				}
				sql += ") ";				
			}
			
			if ( "".equals(orderBy)) {
				sql += " order by subject_code";
			}
			else if ("code".equals(orderBy)){
				sql += " order by subject_code";
			}
			else if ("name".equals(orderBy)){
				sql += " order by subject_name";
			}			
			
			ResultSet rs = stmt.executeQuery(sql);
			Vector list = new Vector();
			while ( rs.next() ) {
				Subject subject = new Subject();
				subject.setId(rs.getString("subject_id"));
				subject.setCode(rs.getString("subject_code"));
				subject.setName(rs.getString("subject_name"));
				subject.setAltName(Db.getString(rs, "subject_name_alt"));
				subject.setRemarks(Db.getString(rs, "remark"));
				subject.setSynopsis(Db.getString(rs, "synopsis"));
				subject.setCredithrs(rs.getInt("credit_hrs"));
				list.addElement(subject);
			}
			return list;
		} finally {
			if ( db != null ) db.close();
		}				
	}	
	
	public static Vector getList(String program_code) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			r.add("s.subject_id");
			r.add("s.subject_code");
			r.add("s.subject_name");
			r.add("s.subject_name_alt");
			r.add("remark");
			r.add("synopsis");			
			r.add("s.credit_hrs");
			r.add("s.subject_id", r.unquote("p.subject_id"));
			r.add("p.program_code", program_code);
			sql = r.getSQLSelect("faculty_subject s, subject_program p", "subject_code");
			ResultSet rs = stmt.executeQuery(sql);
			Vector<Subject> list = new Vector<Subject>();
			while ( rs.next() ) {
				Subject subject = new Subject();
				subject.setId(rs.getString("subject_id"));
				subject.setCode(rs.getString("subject_code"));
				subject.setName(rs.getString("subject_name"));
				subject.setAltName(Db.getString(rs, "subject_name_alt"));
				subject.setRemarks(Db.getString(rs, "remark"));
				subject.setSynopsis(Db.getString(rs, "synopsis"));				
				subject.setCredithrs(rs.getInt("credit_hrs"));
				list.addElement(subject);
			}
			return list;
		} finally {
			if ( db != null ) db.close();
		}				
	}		
	
	public static Subject getSubject(String id) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			r.add("subject_id", id);
			r.add("subject_code");
			r.add("subject_name");
			r.add("subject_name_alt");
			r.add("remark");
			r.add("synopsis");			
			r.add("credit_hrs");
			sql = r.getSQLSelect("faculty_subject");

			ResultSet rs = stmt.executeQuery(sql);
			Subject subject = new Subject();
			if ( rs.next() ) {
				
				subject.setId(id);
				subject.setCode(rs.getString("subject_code"));
				subject.setName(rs.getString("subject_name"));
				subject.setAltName(Db.getString(rs, "subject_name_alt"));
				subject.setRemarks(Db.getString(rs, "remark"));
				subject.setSynopsis(Db.getString(rs, "synopsis"));						
				subject.setCredithrs(rs.getInt("credit_hrs"));
			}
			return subject;
		} finally {
			if ( db != null ) db.close();
		}				
	}	
	
	public static Vector getNoneCoreList(Hashtable info) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			r.add("fs.subject_id");
			r.add("subject_code");
			r.add("subject_name");
			r.add("subject_name_alt");
			r.add("remark");
			r.add("synopsis");					
			r.add("credit_hrs");
			r.add("cs.subject_option", "e");
			r.add("fs.subject_id", r.unquote("cs.subject_id"));
			r.add("cs.course_id", (String) info.get("course_id"));			
			r.add("cs.period_id", (String) info.get("period_id"));
			r.add("cs.period_root_id", (String) info.get("period_scheme"));
			sql = r.getSQLSelect("faculty_subject fs, course_structure cs");

			ResultSet rs = stmt.executeQuery(sql);
			Vector list = new Vector();
			while ( rs.next() ) {
				Subject subject = new Subject();
				subject.setId(rs.getString("subject_id"));
				subject.setCode(rs.getString("subject_code"));
				subject.setName(rs.getString("subject_name"));
				subject.setAltName(Db.getString(rs, "subject_name_alt"));
				subject.setRemarks(Db.getString(rs, "remark"));
				subject.setSynopsis(Db.getString(rs, "synopsis"));						
				subject.setCredithrs(rs.getInt("credit_hrs"));
				list.addElement(subject);
			}
			return list;
		} finally {
			if ( db != null ) db.close();
		}				
	}	
	
	public static void saveSynopsis(String subject_id, String synopsis) throws Exception {
		Db db = null;
		try {
			db = new Db();
			saveSynopsis(db.getStatement(), subject_id, synopsis);
		} finally {
			if ( db != null ) db.close();
		}
	}
	
	public static void saveSynopsis(Statement stmt, String subject_id, String synopsis) throws Exception {
		SQLRenderer r = new SQLRenderer();
		r.update("subject_id", subject_id);
		r.add("synopsis", synopsis);
		String sql = r.getSQLUpdate("faculty_subject");
		stmt.executeUpdate(sql);
	}
	
}
