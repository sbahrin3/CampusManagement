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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import lebah.db.Db;
import lebah.db.SQLRenderer;

/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
public class PeriodData {
	

	
	public static Vector getPeriodDisplayName(String period_scheme) throws Exception {
		Vector<Period> names = new Vector<Period>();
		Vector<Period> list = PeriodData.getPeriodData(period_scheme);
		getItems(list, names);
		return names;
	}


	private static void getItems(Vector<Period> list, Vector<Period> names) {
		for ( Period p : list) {
			getItems2(p, names);
		}
	}


	private static void getItems2(Period p, Vector<Period> names) {
		if ( p.hasChild()) {
			Vector childs = p.getChild();
			getItems(childs, names);
		}
		else {
			names.addElement(p);
		}
	}

	
	public static int getPathNumber(String period_scheme) throws Exception {
		Db db = null;
		try {
			db = new Db();
			String sql = "select path_no from period_root where period_root_id = '" + period_scheme + "'";
			ResultSet rs = db.getStatement().executeQuery(sql);
			if ( rs.next() ) return rs.getInt(1);
			else return 0;
		} finally {
			if ( db != null ) db.close();
		}
	}
	
	
	public static Vector getList() throws Exception {
		return getSchemeList();
	}
	
	public static Vector getSchemeList() throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			r.add("period_root_id");
			r.add("period_schema_code");
			r.add("path_no");
			sql = r.getSQLSelect("period_root");
			ResultSet rs = stmt.executeQuery(sql);
			Vector<Hashtable> list = new Vector<Hashtable>();
			while ( rs.next() ) {
				Hashtable<String, Object> h = new Hashtable<String, Object>();
				h.put("id", rs.getString("period_root_id"));
				h.put("code", rs.getString("period_schema_code"));
				h.put("isOpen", rs.getInt("path_no") == -1 ? true : false);
				list.addElement(h);
			}	
			return list;
		} finally {
			if ( db != null ) db.close();
		}				
	}
	
	public static Hashtable getPeriodScheme(String scheme_id, String program_code) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			Hashtable h = new Hashtable();
			{
				r.add("period_schema_code");
				r.add("period_root_id", scheme_id);
				sql = r.getSQLSelect("period_root");
				ResultSet rs = stmt.executeQuery(sql);
				
				if ( rs.next() ) {
					h.put("id", scheme_id);
					h.put("code", rs.getString("period_schema_code"));
				}	
			}
			
			{
				r.clear();
				r.add("program_name");
				r.add("program_code");
				r.add("program_code", program_code);
				r.add("period_root_id", scheme_id);
				sql = r.getSQLSelect("program");
				
				
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) {
					h.put("program_code", rs.getString("program_code"));	
					h.put("program_name", rs.getString("program_name"));	
				}
			}
			return h;
		} finally {
			if ( db != null ) db.close();
		}			
	}	
	
	public static Hashtable getPeriodSchemeX(String scheme_id, String course_id) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			Hashtable h = new Hashtable();
			{
				r.add("period_schema_code");
				r.add("period_root_id", scheme_id);
				sql = r.getSQLSelect("period_root");
				ResultSet rs = stmt.executeQuery(sql);
				
				if ( rs.next() ) {
					h.put("id", scheme_id);
					h.put("code", rs.getString("period_schema_code"));
				}	
			}
			
			{
				r.clear();
				r.add("program_name");
				r.add("program_code");
				r.add("course_id", course_id);
				r.add("period_root_id", scheme_id);
				sql = r.getSQLSelect("program");
				
				
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) {
					h.put("program_code", rs.getString("program_code"));	
					h.put("program_name", rs.getString("program_name"));	
				}
			}
			return h;
		} finally {
			if ( db != null ) db.close();
		}			
	}
	
	
	public static Vector<Period> getPeriodData(String schema_id) throws Exception {
		
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			String schema_code = "";
			if ( !"".equals(schema_id) ){
				r.add("period_schema_code");
				r.add("period_root_id", schema_id);
				sql = r.getSQLSelect("period_root");
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) schema_code = rs.getString("period_schema_code");
			}
			
			//1. select period where parent_id is 0
			Vector<Period> list = new Vector<Period>();
			{
				r.clear();
				r.add("period_id");
				r.add("period_level");
				r.add("period_name");
                r.add("display_name");
				if ( !"".equals(schema_code) ) r.add("period_root_id", schema_code);			
				r.add("parent_id", "0");
				sql = r.getSQLSelect("period", "period_sequence");
				ResultSet rs = stmt.executeQuery(sql);
			
				while ( rs.next() ) {
					Period period = new Period();
					period.setId(rs.getString("period_id"));
					period.setLevel(rs.getInt("period_level"));
					period.setName(rs.getString("period_name"));
                    period.setDisplayName(Db.getString(rs, "display_name"));
					list.addElement(period);
				}
			}
			
			//for each period get childs
			{
				for ( int i=0; i < list.size(); i++ ) {
					Period period = (Period) list.elementAt(i);
					{
						r.clear();
						r.add("period_id");
						r.add("period_name");
                        r.add("display_name");
						if ( !"".equals(schema_code) ) r.add("period_root_id", schema_code);			
						r.add("parent_id", period.getId());
						sql = r.getSQLSelect("period", "period_sequence");
						ResultSet rs = stmt.executeQuery(sql);
					
						while ( rs.next() ) {
							Period p = new Period();
							p.setId(rs.getString("period_id"));
							p.setName(rs.getString("period_name"));
                            p.setDisplayName(Db.getString(rs, "display_name"));
							period.addChild(p);
						}
					}					
				}
					
				tabulateChild(list, stmt, r, sql);
				//createLongName(schema_id, list, stmt);
			}
			
			return list;
			
		} finally {
			if ( db != null ) db.close();
		}	
	}
	
	public static void getPeriodData() throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			Vector periods = new Vector();
			{
				r.add("period_schema_code");
				sql = r.getSQLSelect("period_root");
				ResultSet rs = stmt.executeQuery(sql);
				while ( rs.next() ) {
				    periods.addElement(rs.getString(1));
				}
			}
			Hashtable h = new Hashtable();
			for ( int i=0; i < periods.size(); i++ ) {
			    String period = (String) periods.elementAt(i);
			    Vector v = getPeriodData(period);
			}
			
		} finally {
		    if ( db != null ) db.close();
		}
	}	

	private static void tabulateChild(Vector list, Statement stmt, SQLRenderer r, String sql) throws SQLException, Exception {
		for ( int i=0; i < list.size(); i++ ) {
			Period period = (Period) list.elementAt(i);
			tabulateChild1(period, stmt, r, sql);
		}		
	}

	private static void tabulateChild1(Period period, Statement stmt, SQLRenderer r, String sql) throws SQLException, Exception {
		Vector childs = period.getChild();
		for ( int k=0; k < childs.size(); k++ ) {
			Period p = (Period) childs.elementAt(k);
			tabulateChild2(p, stmt, r, sql);
			tabulateChild1(p, stmt, r, sql);
		}	
	}	
	
	private static void tabulateChild2(Period period, Statement stmt, SQLRenderer r, String sql) throws SQLException, Exception {
		r.clear();
		r.add("period_id");
		r.add("period_name");
        r.add("display_name");
		r.add("parent_id", period.getId());
		sql = r.getSQLSelect("period", "period_sequence");
		ResultSet rs = stmt.executeQuery(sql);
					
		while ( rs.next() ) {
			Period p = new Period();
			p.setId(rs.getString("period_id"));
			p.setName(rs.getString("period_name"));
            p.setDisplayName(Db.getString(rs, "display_name"));
			period.addChild(p);
		}
	}
	
	public static void createPeriodList(Vector periods, Vector v) {
		for ( int i=0; i < periods.size(); i++ ) {
			Period period = (Period) periods.elementAt(i);
			v.addElement(period);
			if ( period.hasChild() ) {
				createPeriodList(period.getChild(), v);		
			}
		}
	}	
    
    public static Vector getPeriodStructure(Vector periods) {
        Vector v = new Vector();
        createPeriodList(periods, v);
        return v;
    }
	
	//Reference of period name from it's id
	public static Hashtable getPeriodNameRef(String schema_id) throws Exception {
		Vector list = getPeriodData(schema_id);
		Vector periodList = new Vector();
		createPeriodList(list, periodList);
		String[] str = new String[5];
		int n = 0;
		Hashtable ref = new Hashtable();
		for ( int i=0; i < periodList.size(); i++ ) {
			Period p = (Period) periodList.elementAt(i);
			int level = p.getLevel();
			for ( int j = level; j < 5; j++ ) str[j] = "";
			str[level] = p.getName();
			String name = "";
			for ( int k=0; k < str.length; k++ ) {
				if ( k == 0 ) name += str[k];
				else if ( !str[k].equals("") ) name += " - " + str[k];
			}
			ref.put(p.getId(), name);
		}
		return ref;
	}
	
	public static Hashtable getPeriodNameRef() throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			Vector periods = new Vector();
			{
				r.add("period_schema_code");
				sql = r.getSQLSelect("period_root");
				ResultSet rs = stmt.executeQuery(sql);
				while ( rs.next() ) {
				    periods.addElement(rs.getString(1));
				}
			}
			Hashtable h = new Hashtable();
			for ( int i=0; i < periods.size(); i++ ) {
			    String period = (String) periods.elementAt(i);
			    Hashtable h2 = getPeriodNameRef(period);
			    h.put(period, h2);
			}
			return h;
		} finally {
		    if ( db != null ) db.close();
		}	    
	    
	}	
	
	/*
		select
		s.session_id,
		s.session_name
		from session s, intake_batch b
		where s.session_id = b.session_id
		group by b.intake_session
		order by s.start_date desc
		*/
	public static Vector getIntakeSessionList() throws Exception {
		Db db =  null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();	
			sql = "	select " +
			"s.session_id, " +
            //--
			"s.session_name, " +
			"count(s.session_id) " +
			"from sessions s, intake_batch b " +
			"where s.session_id = b.intake_session " +
			"group by s.session_id, s.session_name ";// +
			//"order by s.start_date desc";
			//
			ResultSet rs = stmt.executeQuery(sql);
			Vector list = new Vector();
			while ( rs.next() ) {
				Hashtable h = new Hashtable();
				h.put("id", rs.getString("session_id"));
				h.put("name", rs.getString("session_name"));
				list.add(h);
			}
			return list;
		} finally {
			if ( db != null ) db.close();
		}
	}
	
	public static Vector<Hashtable> getStudentIntakeList() throws Exception {
		return getStudentIntakeList("");
	}
    
    public static Vector<Hashtable> getStudentIntakeList(String programCode) throws Exception {
        Db db = null;
        String sql = "";
        try {
            db = new Db();
            SQLRenderer r = new SQLRenderer();
            r
            .add("s.session_id")
            .add("s.session_name")
            ;
            
            if ( !"".equals(programCode) ) r.add("sc.program_code", programCode);
            
            r
            .relate("b.session_id", "s.session_id")
            .relate("b.intake_session", "sc.intake_session")
            ;
            
            sql = r.getSQLSelectDistinct("intake_batch b, student_course sc, sessions s", "s.session_id")
            ;
            
            ResultSet rs = db.getStatement().executeQuery(sql);
            Vector<Hashtable> list = new Vector<Hashtable>();
            
            while ( rs.next() ) {
                Hashtable<String, String> h = new Hashtable<String, String>();
                h.put("id", rs.getString("session_id"));
                h.put("name", rs.getString("session_name"));
                list.addElement(h);
            }
            return list;
        } finally {
            if ( db != null ) db.close();
        }
    }
	
	public static Vector getPeriodStructure(String id) throws Exception {
		Vector periodList = PeriodData.getPeriodData(id);
		Vector periodStructure = new Vector();
		createPeriodStructure(periodList, periodStructure);	
		//createLongName(id, periodStructure); //SHAMSUL - No more needed, as long name created in the Period class itself
		return periodStructure;
	}
	
	private static void createPeriodStructure(Vector periods, Vector v) {
		for ( int i=0; i < periods.size(); i++ ) {
			Period period = (Period) periods.elementAt(i);
			v.addElement(period);
			if ( period.hasChild() ) {
				createPeriodStructure(period.getChild(), v);		
			}
		}
	}
	
	public static String getLongPeriodName(String period_id, String period_scheme) throws Exception {
		Db db = null;
		try {
			db = new Db();
			return getLongPeriodName(db.getStatement(), period_id, period_scheme);
		} finally {
			if ( db != null ) db.close();
		}
	}
	

	
	public static String getLongPeriodName(Statement stmt, String period_id, String period_scheme) throws Exception {
	    String period_name = "";
		String sql = "";
		SQLRenderer r = new SQLRenderer();
		{
			r.clear();
			r.add("period_name");
			r.add("period_level");
			r.add("parent_id");
			r.add("period_id", period_id);
			r.add("period_root_id", period_scheme);
			sql = r.getSQLSelect("period");

			int period_level = 0;
			String parent_id = "";
			ResultSet rs = stmt.executeQuery(sql);
			if ( rs.next() ) {
				period_name = rs.getString("period_name");	
				period_level = rs.getInt("period_level");
				parent_id = rs.getString("parent_id");
			}

			while ( period_level != 0 ) {
				r.clear();
				r.add("period_name");
				r.add("parent_id");
				r.add("period_level");
				r.add("period_id", parent_id);
				sql = r.getSQLSelect("period");

				rs = stmt.executeQuery(sql);
				if ( rs.next() ) {
					period_name = rs.getString("period_name") + ", " + period_name;
					parent_id = rs.getString("parent_id");
					period_level = rs.getInt("period_level");
				}	

			}
		}
		return period_name;
	}	
	

	public static void getStudentStatus(String student_id, String session_id) throws Exception {
	    Db db = null;
	    String sql = "";
	    try {
	        db = new Db();
	        Statement stmt = db.getStatement();
	        getStudentStatus(stmt, student_id, session_id);
	        
	    } finally {
	        if ( db != null ) db.close();
	    }
	}
	
	public static void getStudentStatus(Statement stmt, String student_id, String session_id) throws Exception {
	    SQLRenderer r = new SQLRenderer();
        r.clear();
        r.add("p.period_id");
        r.add("sta.batch_id");
        r.add("sta.repeat_no");
        r.add("sta.status");
        r.add("ses.session_name");
        r.add("ses2.session_name as batch_session");
        r.add("sc.period_root_id as period_scheme");
        
        r.add("sta.student_id", student_id);
        r.add("sta.session_id", session_id);
        r.add("p.period_root_id", r.unquote("sc.period_root_id"));
        r.add("sta.session_id", r.unquote("ses.session_id"));
        r.add("sta.batch_id", r.unquote("ses2.session_id"));
        r.add("sc.student_id", r.unquote("sta.student_id"));
        
        String sql = r.getSQLSelect("student_course sc, student_status sta, period p, session ses, session ses2", "ses.start_date");
        ResultSet rs = stmt.executeQuery(sql);
        //-
        //System.out.println( rs.)
        while ( rs.next() ) {
            String session_name = rs.getString("session_name");
            String period_id = rs.getString("period_id");
            System.out.println(session_name + ": " + period_id);
        }
        
	}
    
    
    private static void createPeriodList(Vector periods, Vector v, String to_period) {
        for ( int i=0; i < periods.size(); i++ ) {
            Period period = (Period) periods.elementAt(i);
            
            v.addElement(period);
            if ( period.hasChild() ) {
                createPeriodList(period.getChild(), v, to_period); 
            }
            
            if ( period.getId().equals(to_period)) break;
        }
    } 
    
    public static Vector getPeriodList(String period_scheme, String to_period) throws Exception {
        Vector periodList = getPeriodData(period_scheme);    
        Vector allPeriod = new Vector();
        createPeriodList(periodList, allPeriod, to_period);
        return allPeriod;
    }
    
    public static Vector getPeriodList(String period_scheme) throws Exception {
        Vector periodList = getPeriodData(period_scheme);    
        Vector allPeriod = new Vector();
        createPeriodList(periodList, allPeriod);
        return allPeriod;
    }  
    
    public static Vector getPeriodListByProgram(String program_code) throws Exception {
        String period_scheme = "";
        Vector periodList = getPeriodData(period_scheme);    
        Vector allPeriod = new Vector();
        createPeriodList(periodList, allPeriod);
        return allPeriod;
    }  
    
    
	

    public static void saveDisplayName(String periodId, String displayName) throws Exception {
        Db db = null;
        String sql = "";
        try {
            db = new Db();
            SQLRenderer r = new SQLRenderer();
            sql = r
            .update("period_id", periodId)
            .add("display_name", displayName)
            .getSQLUpdate("period")
            ;
            db.getStatement().executeUpdate(sql);
        } finally {
            if ( db != null ) db.close();
        }
    }
    

	
}
