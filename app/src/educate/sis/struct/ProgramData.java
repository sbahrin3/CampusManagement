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
import java.util.List;
import java.util.Vector;

import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import lebah.db.Db;
import lebah.db.PersistenceManager;
import lebah.db.SQLPStmtRenderer;
import lebah.db.SQLRenderer;


/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
public class ProgramData {
	
	public static Vector<Period> getPeriodListByTrack(String track_id) throws Exception {
		Db db = null;
		try {
			db = new Db();
			SQLRenderer r = new SQLRenderer();
			//get period scheme
			String scheme_id = "";
			{
				r.add("period_root_id");
				r.add("track_id", track_id);
				String sql = r.getSQLSelect("program_track");
				ResultSet rs = db.getStatement().executeQuery(sql);
				if ( rs.next() ) {
					scheme_id = rs.getString(1);
				}
			}
			//Vector<Period> list = PeriodData.getPeriodData(scheme_id);
			return null;
			
		} finally {
			if ( db != null ) db.close();
		}
	}
	
	public static boolean isOpenPeriod(String track_id) throws Exception {
		Db db = null;
		try {
			db = new Db();
			SQLRenderer r = new SQLRenderer();
			//get period scheme
			boolean open = false;
			{
				r.relate("p.period_root_id", "t.period_root_id");
				r.add("t.track_id", track_id);
				r.add("p.path_no");
				String sql = r.getSQLSelect("period_root p, program_track t");
				ResultSet rs = db.getStatement().executeQuery(sql);
				if ( rs.next() ) {
					open = rs.getInt(1) == -1 ? true : false;
				}
			}
			return open;
		} finally {
			if ( db != null ) db.close();
		}
	}
	
	public static Vector getPeriodDisplayList(String student_id) throws Exception {
		String scheme_id = "";
		Db db = null;
		try {
			db = new Db();
			SQLRenderer r = new SQLRenderer();
			String sql =
			r
			.add("c.student_id", student_id)
			.add("c.period_root_id")
			.getSQLSelect("student_course c")
			;
			ResultSet rs = db.getStatement().executeQuery(sql);
			if ( rs.next() ) {
				scheme_id = rs.getString(1);
			}
			
		} finally {
			if ( db != null ) db.close();
		}
		return PeriodData.getPeriodDisplayName(scheme_id);
	}	
	
	public static Vector getPeriodList(String student_id) throws Exception {
		String scheme_id = "";
		Db db = null;
		try {
			db = new Db();
			SQLRenderer r = new SQLRenderer();
			String sql =
			r
			.add("c.student_id", student_id)
			.add("c.period_root_id")
			.getSQLSelect("student_course c")
			;
			ResultSet rs = db.getStatement().executeQuery(sql);
			if ( rs.next() ) {
				scheme_id = rs.getString(1);
			}
			
		} finally {
			if ( db != null ) db.close();
		}
		return PeriodData.getPeriodData(scheme_id);
	}	
	
	public static Vector getPeriodList2(String program) throws Exception {
		String scheme_id = "";
		Db db = null;
		try {
			db = new Db();
			SQLRenderer r = new SQLRenderer();
			String sql =
			r
			.add("period_root_id")
			.add("program_code", program)
			.getSQLSelect("program")
			;
			ResultSet rs = db.getStatement().executeQuery(sql);
			if ( rs.next() ) {
				scheme_id = rs.getString(1);
			}
			
		} finally {
			if ( db != null ) db.close();
		}
		return PeriodData.getPeriodData(scheme_id);
	}
	
	public static Vector getProgramCodeList() throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			r.add("program_code");
			sql = r.getSQLSelect("program", "program_code");
			ResultSet rs = stmt.executeQuery(sql);
			Vector list = new Vector();
			while ( rs.next() ) {
				list.addElement(rs.getString(1));
			}	
			return list;
		} finally {
			if ( db != null ) db.close();
		}				
	}
	
	public static Vector getProgramList() throws Exception {
		return getProgramList("name");
	}
	
	public static Vector getProgramList(String orderBy) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		List<Program> l = pm.list("SELECT a FROM Program a ORDER BY a."+orderBy+"");
		Vector<Program> v = new Vector();
		v.addAll(l);
		return v;
		/*Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			
			r.add("program_code");
			r.add("program_name");
			r.add("program_abbr");
            r.add("period_root_id");
            if ( "code".equals(orderBy)) sql = r.getSQLSelect("program", "program_code");
            else sql = r.getSQLSelect("program", "program_name");
			ResultSet rs = stmt.executeQuery(sql);
			Vector list = new Vector();
			while ( rs.next() ) {
				Hashtable h = new Hashtable();
				h.put("code", rs.getString("program_code"));
				h.put("name", rs.getString("program_name"));
				h.put("abbrev", rs.getString("program_abbr"));
                h.put("period_scheme", rs.getString("period_root_id"));
				h.put("tracks", new Vector());
				list.addElement(h);
			}	
			for ( int i=0; i < list.size(); i++ ) {
				Hashtable h = (Hashtable) list.elementAt(i);
				Vector v = getTrackList(stmt, (String) h.get("code"));
				h.put("tracks", v);
			}
			return list;
		} finally {
			if ( db != null ) db.close();
		}	*/			
	}
	
	public static Vector getList() throws Exception {
		return getProgramList();	
	}
	
	public static Vector getTrackList(Statement stmt, String program_code) throws Exception {
		SQLRenderer r = new SQLRenderer();
		r.add("program_code", program_code);
		r.add("track_id");
		r.add("track_name");
		r.add("period_root_id");
		String sql = r.getSQLSelect("program_track");
		ResultSet rs = stmt.executeQuery(sql);
		Vector v = new Vector();
		while ( rs.next() ) {
			Hashtable h = new Hashtable();
			h.put("program_code", program_code);
			h.put("track_id", rs.getString("track_id"));
			h.put("track_name", rs.getString("track_name"));
			h.put("period_scheme", rs.getString("period_root_id"));
			v.addElement(h);
		}
		return v;
	}
	
	public static Hashtable getProgramCodeMap(Statement stmt) throws Exception {
		SQLRenderer r = new SQLRenderer();
		String sql = "";
		r.add("program_code");
		r.add("program_name");
		sql = r.getSQLSelect("program");
		ResultSet rs = stmt.executeQuery(sql);
		Hashtable h = new Hashtable();
		while ( rs.next() ) {
			h.put(rs.getString("program_code"), rs.getString("program_name"));
		}	
		return h;
	    
	}
	
	public static Hashtable getProgramCodeMap() throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			return getProgramCodeMap(stmt);
		} finally {
			if ( db != null ) db.close();
		}				
	}	
	
	public static String getProgramName(String code) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			
			r.add("program_name");
			r.add("program_code", code);
			sql = r.getSQLSelect("program");
			ResultSet rs = stmt.executeQuery(sql);
			String program_name = "";
			if ( rs.next() ) {
				program_name = rs.getString("program_name");
			}	
			return program_name;
		} finally {
			if ( db != null ) db.close();
		}				
	}
	
	public static Hashtable getInfo(String code) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			
			r.add("program_name");
			r.add("period_root_id");
			r.add("program_abbr");
			r.add("program_code", code);
			sql = r.getSQLSelect("program");
			ResultSet rs = stmt.executeQuery(sql);
			Hashtable h = new Hashtable();
			if ( rs.next() ) {
			    h.put("code", code);
				h.put("name", rs.getString("program_name"));
				h.put("period", rs.getString("period_root_id"));
				h.put("abbrev", rs.getString("program_abbr"));
			}	
			return h;
		} finally {
			if ( db != null ) db.close();
		}				
	}	
	
	public static void saveProgramIntake(String programCode, String sessionId, String periodId) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			boolean found = false;
			{
				r.add("program_code");
				r.add("program_code", programCode);
				r.add("intake_session", sessionId);
				sql = r.getSQLSelect("program_intake");
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) {
					found = true;
				}
			}
			
			if ( found ) {
				r.clear();
				r.update("program_code", programCode);
				r.update("intake_session", sessionId);
				r.add("period_root_id", periodId);
				sql = r.getSQLUpdate("program_intake");
				stmt.executeUpdate(sql);				
				
			} else {
				r.clear();
				r.add("program_code", programCode);
				r.add("intake_session", sessionId);
				r.add("period_root_id", periodId);
				sql = r.getSQLInsert("program_intake");
				stmt.executeUpdate(sql);	
			}
		} finally {
			if ( db != null ) db.close();
		}	
	}
	
	public static Vector getProgramIntakeList() throws Exception {
		return getProgramIntakeList("");	
	}
	
	public static Vector getProgramIntakeList(String programCode) throws Exception {
		Db db = null;
		String sql = "";	
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			r.add("i.program_code");
			r.add("i.intake_session");
			r.add("i.period_root_id");
			r.add("p.program_name");
			r.add("s.session_name");
			if ( !"".equals(programCode) ) {
				r.add("i.program_code", programCode);
			}
			r.add("i.program_code", r.unquote("p.program_code"));
			r.add("i.intake_session", r.unquote("s.session_id"));
			if ( !"".equals(programCode) ) {
				sql = r.getSQLSelect("program_intake i, program p, sessions s", "p.program_code, s.start_date");
			} 
			else {
				sql = r.getSQLSelect("program_intake i, program p, sessions s", "s.start_date");
			}
			ResultSet rs = stmt.executeQuery(sql);
			Vector v = new Vector();
			while ( rs.next() ) {
				Hashtable h = new Hashtable();
				h.put("programCode", rs.getString("program_code"));
				h.put("intakeSession", rs.getString("intake_session"));
				h.put("periodId", rs.getString("period_root_id"));
				h.put("programName", rs.getString("program_name"));
				h.put("sessionName", rs.getString("session_name"));
				v.addElement(h);	
			}
			return v;
		} finally {
			if ( db != null ) db.close();
		}
	}
	
	public static Vector getProgramTrackList(String program_code) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			r.add("t.program_code", program_code);
			r.add("t.track_id");
			r.add("t.track_name");
			r.add("t.period_root_id");
			r.add("p.program_name");
			r.add("t.intake_session_path");
			r.add("t.program_code", r.unquote("p.program_code"));
			
			sql = r.getSQLSelect("program_track t, program p");
			//-
			ResultSet rs = stmt.executeQuery(sql);
			Vector v = new Vector();
			while ( rs.next() ) {
				Hashtable h = new Hashtable();
				h.put("program_code", program_code);
				h.put("track_id", rs.getString("track_id"));
				h.put("track_name", rs.getString("track_name"));
				h.put("period_scheme", rs.getString("period_root_id"));
				h.put("program_name", rs.getString("program_name"));
				h.put("intake_session_path", rs.getString("intake_session_path"));
				v.addElement(h);
			}	
			return v;
		} finally {
			if ( db != null ) db.close();
		}	
		
	}
	
	public static Vector getPeriodStructureList(String program_code) throws Exception {
		Db db = null;
		String sql = "";
		try {
			Vector v = new Vector();
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			{
				r.add("period_root_id");
				r.add("program_code", program_code);
				sql = r.getSQLSelect("program");
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) {
					String period_scheme = rs.getString(1);
					v.addElement(period_scheme);
				}
			}
			{
				r.clear();
				r.add("t.program_code", program_code);
				r.add("t.period_root_id");
				sql = r.getSQLSelect("program_track t");
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) {
					String period_scheme = rs.getString(1);
					v.addElement(period_scheme);
				}				
			}
			return v;
		} finally {
			if ( db != null ) db.close();
		}
		
	}
	
	public static Vector getPeriodStructureList2(String program_code) throws Exception {
		Db db = null;
		String sql = "";
		try {
			Vector v = new Vector();
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			{
				r.add("period_root_id");
				r.add("program_code", program_code);
				sql = r.getSQLSelect("program");
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) {
					String period_scheme = rs.getString(1);
					Hashtable h = new Hashtable();
					h.put("track_name", "DEFAULT");
					h.put("period_scheme", period_scheme);
					v.addElement(h);
				}
			}
			{
				r.clear();
				r.add("t.program_code", program_code);
				r.add("t.period_root_id");
				r.add("t.track_id");
				r.add("t.track_name");
				sql = r.getSQLSelect("program_track t");
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) {
					String period_scheme = rs.getString("period_root_id");
					String name = rs.getString("track_name");
					Hashtable h = new Hashtable();
					h.put("track_name", name);
					h.put("period_scheme", period_scheme);
					
					v.addElement(h);
				}				
			}
			return v;
		} finally {
			if ( db != null ) db.close();
		}
		
	}	
	
	public static Hashtable getProgramTrackInfo(String program_code, String track_id) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			r.add("t.program_code", program_code);
			r.add("t.track_id", track_id);
			r.add("t.track_name");
			r.add("t.period_root_id");
			r.add("p.program_name");
			r.add("t.program_code", r.unquote("p.program_code"));
			
			sql = r.getSQLSelect("program_track t, program p");
			ResultSet rs = stmt.executeQuery(sql);
			Hashtable h = new Hashtable();			
			while ( rs.next() ) {

				h.put("program_code", program_code);
				h.put("track_id", track_id);
				h.put("track_name", rs.getString("track_name"));
				h.put("period_scheme", rs.getString("period_root_id"));
				h.put("program_name", rs.getString("program_name"));
			}	
			return h;
		} finally {
			if ( db != null ) db.close();
		}	
		
	}	

	
	public static boolean saveProgramTrack(String programCode, String trackId, String trackName, String periodId, int pathNo) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			
			
			{
				r.clear();
				r.add("period_root_id");
				r.add("program_code", programCode);
				sql = r.getSQLSelect("program");
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) {
					if ( periodId.equals(rs.getString("period_root_id"))) {
						return false;
					}
				}
			}
			
			{
				r.clear();
				r.add("period_root_id");
				r.add("track_id");
				r.add("program_code", programCode);
				sql = r.getSQLSelect("program_track");
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) {
					if ( periodId.equals(rs.getString("period_root_id")) && !trackId.equals(rs.getString("track_id"))) {
						return false;
					}
				}
			}			
			
			//find out about periodId - term based or open based?
			boolean isOpen = false;
			int realPathNo = 0;
			{
				r.clear();
				r.add("path_no");
				r.add("period_root_id", periodId);
				sql = r.getSQLSelect("period_root");
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) {
					realPathNo = rs.getInt("path_no");
					if ( realPathNo == -1 ) {
						isOpen = true;
					}
					else {
						pathNo = realPathNo;
					}
				}
				
			}
			
			//
			boolean found = false;
			if ( !"0".equals(trackId) ) {
				r.clear();
				r.add("program_code");
				r.add("program_code", programCode);
				r.add("track_id", trackId);
				sql = r.getSQLSelect("program_track");
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) {
					found = true;
				}
			}
			if ( found ) {
				r.clear();
				r.update("program_code", programCode);
				r.update("track_id", trackId);
				r.add("track_name", trackName);
				r.add("period_root_id", periodId);
				r.add("intake_session_path", pathNo);
				sql = r.getSQLUpdate("program_track");
				stmt.executeUpdate(sql);				
			} else {
				String uid = Long.toString(lebah.db.UniqueID.get());
				r.clear();
				r.add("program_code", programCode);
				r.add("track_id", uid);
				r.add("track_name", trackName);
				r.add("period_root_id", periodId);
				r.add("intake_session_path", pathNo);
				sql = r.getSQLInsert("program_track");
				stmt.executeUpdate(sql);	
			}
		} finally {
			if ( db != null ) db.close();
		}
		return true;
	}
	
	public static void deleteProgramTrack(String trackId) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			sql = "delete from program_track where track_id = '" + trackId + "'";
			stmt.executeUpdate(sql);
			
		} finally {
		    if ( db != null ) db.close();
		}
	}
    
    public static String getDefaultPeriodScheme(String programCode) throws Exception {
        Db db = null;
        String sql = "";
        try {
            db = new Db();
            Statement stmt = db.getStatement();
            SQLRenderer r = new SQLRenderer();
            r.add("program_code", programCode);
            r.add("period_root_id");
            sql = r.getSQLSelect("program");
            ResultSet rs = stmt.executeQuery(sql);
            if ( rs.next())
                return rs.getString(1);
            else
                return "";
            
        } finally {
            if ( db != null ) db.close();
        }
    }
    
    public static void saveSubjectProgram(String programCode, String[] subjects) throws Exception {
    	Db db = null;
    	try {
    		db = new Db();
    		{
    			SQLRenderer r = new SQLRenderer();
    			db.getStatement().executeUpdate(
    				r.add("program_code", programCode).getSQLDelete("subject_program")
    			);
    		}
    		if ( subjects != null ){
	    		SQLPStmtRenderer r = new SQLPStmtRenderer();
	    		r
	    		.reset()
	    		.add("subject_id","")
	    		.add("program_code", "")
	    		;
	    		r.getPStmtInsert(db.getConnection(), "subject_program");
	    		for ( String subject : subjects ) {
	    			r
	    			.set("subject_id", subject)
	    			.set("program_code", programCode)
	    			;
	    			r.getPStmt().executeUpdate();
	    		}
    		}
    	} finally {
    		if ( db != null ) db.close();
    	}
    }
	
}
