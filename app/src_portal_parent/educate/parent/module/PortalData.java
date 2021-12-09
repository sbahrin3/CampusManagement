/* ************************************************************************
LEBAH PORTAL FRAMEWORK, http://lebah.sf.net
Copyright (C) 2007  Shamsul Bahrin








MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

* ************************************************************************ */
package educate.parent.module;


import java.sql.Connection;
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
public class PortalData {
	
	
	public static void updatePortalPassword(String login, String password) throws Exception {
		Db db = null;
		try {
			db = new Db();
			SQLRenderer r = new SQLRenderer();
			r.add("user_password", password);
			r.update("user_login", login);	
			db.getStatement().executeUpdate(r.getSQLUpdate("users"));
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( db != null ) db.close();
		}
	}


	public static void createPortalLogin(String login, String password, String name, String role) throws Exception {
		String sql = "";
		Connection conn = null;
		Db db = null;
		try {
			db = new Db();
			conn = db.getConnection();
			conn.setAutoCommit(false);			
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			//look for login id
			boolean found = false;
			{
				sql = "select user_login from users where user_login = '" + login + "'";

				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) found = true;
			}
			
			if ( !found ) {
				{
					r.add("user_login", login);
					r.add("user_password", lebah.util.PasswordService.encrypt(password));
					r.add("user_name", name);
					r.add("user_role", role);
					sql = r.getSQLInsert("users");
					stmt.executeUpdate(sql);
				}
				
				//prepare portal item
				//-- portal's theme
				{
					String css_name = "default.css";
					//if ( rs.next() ) css_name = rs.getString("css_name");
					sql = "insert into user_css (user_login, css_name) values ('" + login + "', '" + css_name + "')";

					stmt.executeUpdate(sql);
				}

				{
					Vector vector = new Vector();

					{
						sql = "select tab_id, tab_title, sequence, display_type from tab_template where user_login = '" + role + "'";
						ResultSet rs = stmt.executeQuery(sql);
						
						while ( rs.next() ) {
							Hashtable h = new Hashtable();
							h.put("tab_id", rs.getString("tab_id"));
							h.put("tab_title", rs.getString("tab_title"));
							h.put("sequence", rs.getString("sequence"));
							h.put("display_type", rs.getString("display_type"));
							//System.out.println("tab");
							vector.addElement(h);
						}
					}						
					//System.out.println("===" + vector.size());
					{
						for ( int i=0; i < vector.size(); i++ ) {
							Hashtable h = (Hashtable) vector.elementAt(i);
							r.clear();
							r.add("tab_id", (String) h.get("tab_id"));
							r.add("tab_title", (String) h.get("tab_title"));
							r.add("sequence", Integer.parseInt((String) h.get("sequence")));
							r.add("display_type", (String) h.get("display_type"));
							r.add("user_login", login);
							sql = r.getSQLInsert("tabs");

							stmt.executeUpdate(sql);
						}
					}
				}
				//-- portal's modules

				{
					Vector vector = new Vector();
					{
						sql = "select tab_id, module_id, sequence, module_custom_title, column_number " +
						"from user_module_template where user_login = '" + role + "'";
						ResultSet rs = stmt.executeQuery(sql);
						
						while ( rs.next() ) {
							Hashtable h = new Hashtable();
							h.put("tab_id", rs.getString("tab_id"));
							h.put("module_id", rs.getString("module_id"));
							h.put("sequence", rs.getString("sequence"));
							h.put("module_custom_title", lebah.db.Db.getString(rs, "module_custom_title"));
							String coln = lebah.db.Db.getString(rs, "column_number");
							h.put("column_number", coln.equals("") ? "0" : coln);
							vector.addElement(h);
						}
					}
					
					if ( vector.size() > 0 ) {
						for ( int i=0; i < vector.size(); i++ ) {
							Hashtable h = (Hashtable) vector.elementAt(i);
							r.clear();
							r.add("tab_id", (String) h.get("tab_id"));
							r.add("module_id", (String) h.get("module_id"));
							r.add("sequence", Integer.parseInt((String) h.get("sequence")));
							r.add("module_custom_title", (String) h.get("module_custom_title"));
							r.add("column_number", Integer.parseInt((String) h.get("column_number")));
							r.add("user_login", login);
							sql = r.getSQLInsert("user_module");
							stmt.executeUpdate(sql);	
						}
					}
				}
			}
			else {
				r.add("user_name", name);
				r.update("user_login", login);	
				sql = r.getSQLUpdate("users");		
				stmt.executeUpdate(sql);	
			}
			conn.commit();
		} catch ( Exception ex ) {
			try {
				conn.rollback();
			} catch ( SQLException rollex ) {}
			throw ex;
		} finally {
			if ( db != null ) db.close();
		}
	}	
 

}
