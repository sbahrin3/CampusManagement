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

import educate.sis.struct.entity.LearningCentre;
import lebah.db.Db;
import lebah.db.PersistenceManager;
import lebah.db.SQLRenderer;
import lebah.db.UniqueID;


/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.00
 */
public class LearningCentreData {
	PersistenceManager pm;
	public static Vector getCentreList() throws Exception {
		Db db = null;
		try {
			db = new Db();
			return getCentreList(db.getStatement(), "");
		} finally {
			if ( db != null ) db.close();
		}
	}	

	public  Vector getCentreList(String institution_id) throws Exception {
		pm = new PersistenceManager();
		List<LearningCentre> l = pm.list("SELECT a FROM LearningCentre a WHERE a.institution.id='"+institution_id+"'");
		Vector v = new Vector();
		v.addAll(l);
		return v;
		
	}
	
	public static Vector getCentreList(Statement stmt, String institution_id) throws Exception {
		SQLRenderer r = new SQLRenderer();
		r.add("centre_id");
		r.add("centre_code");
		r.add("centre_name");
		r.add("centre_address");
		if ( !"".equals(institution_id))
			r.add("institution_id", institution_id);
		String sql = r.getSQLSelect("learning_centre", "centre_name");
		ResultSet rs = stmt.executeQuery(sql);
		Vector v = new Vector();
		while ( rs.next() ) {
			Hashtable h = new Hashtable();
			h.put("id", rs.getString(1));
			h.put("code", rs.getString(2));
			h.put("name", rs.getString(3));
			h.put("address", Db.getString(rs, "centre_address"));
			v.addElement(h);
		}
		return v;
	}
	
	public void addCentre(LearningCentre cen) throws Exception {
		pm = new PersistenceManager();
		PersistenceManager.add(cen);
	}
	
	public static void addCentre(Statement stmt, Hashtable info) throws Exception {
		SQLRenderer r = new SQLRenderer();
		
		String centre_id = Long.toString(UniqueID.get());
		String centre_code = (String) info.get("centre_code");
		String centre_name = (String) info.get("centre_name");
		String centre_address = (String) info.get("centre_address");
		String institution_id = (String) info.get("institution_id");
		r.add("centre_id", centre_id);
		r.add("centre_code", centre_code);
		r.add("centre_name", centre_name);
		r.add("centre_address", centre_address);
		r.add("institution_id", institution_id);
		String sql = r.getSQLInsert("learning_centre");
		stmt.executeUpdate(sql);
		
	}
	
	public void updateCentre(String id,LearningCentre cen) throws Exception {
		pm = new PersistenceManager();
		try{
			LearningCentre centre =(LearningCentre)pm.find(LearningCentre.class).whereId(id).forUpdate();
			centre.setCode(cen.getCode());
			centre.setName(cen.getName());
			centre.setAddress(cen.getAddress());
			centre.setInstitution(cen.getInstitution());
			pm.update();
		}
		catch(Exception e){
			e.printStackTrace();
			pm.rollback();
		}
	}
	
	public static void updateCentre(Statement stmt, Hashtable info) throws Exception {
		SQLRenderer r = new SQLRenderer();
		
		String centre_id = (String) info.get("centre_id");
		String centre_code = (String) info.get("centre_code");
		String centre_name = (String) info.get("centre_name");
		String centre_address = (String) info.get("centre_address");
		String institution_id = (String) info.get("institution_id");
		r.update("centre_id", centre_id);
		r.add("centre_code", centre_code);
		r.add("centre_name", centre_name);
		r.add("centre_address", centre_address);
		r.add("institution_id", institution_id);
		String sql = r.getSQLUpdate("learning_centre");
		stmt.executeUpdate(sql);
		
	}
	
	public void deleteCentre(String centre_id) throws Exception {
		pm = new PersistenceManager();
		try{
			LearningCentre centre =(LearningCentre)pm.find(LearningCentre.class,centre_id);
			pm.delete(centre);
		}
		catch(Exception e){
			e.printStackTrace();
			pm.rollback();
		}
	}
	
	public static void deleteCentre(Statement stmt, String centre_id) throws Exception {
		String sql = "";
		boolean found = false;
		{
			sql = "select COUNT(id) cnt from student where centre_id = '" + centre_id + "'";
			ResultSet rs = stmt.executeQuery(sql);
			if ( rs.next() ) {
				int cnt = rs.getInt("cnt");
				if ( cnt > 0 ) found = true;
			}
		}
		if ( !found ) {
			sql = "select COUNT(student_id) cnt from student_centre where centre_id = '" + centre_id + "'";
			ResultSet rs = stmt.executeQuery(sql);
			if ( rs.next() ) {
				int cnt = rs.getInt("cnt");
				if ( cnt > 0 ) found = true;
			}
		}
		if ( !found ) {
			sql = "delete from learning_centre where centre_id = '" + centre_id + "'";
			stmt.executeUpdate(sql);
		}
	}
}
