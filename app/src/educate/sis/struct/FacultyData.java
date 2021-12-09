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

import java.util.List;
import java.util.Vector;

import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.Institution;
import lebah.db.PersistenceManager;

/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
public class FacultyData {
	private static PersistenceManager pm;
	private Faculty faculty;
	public static Vector getList() throws Exception {
		pm = new PersistenceManager();
		List<Faculty> l = pm.list("SELECT a FROM Faculty a ORDER BY a.code");
		Vector<Faculty> v = new Vector<Faculty>();
		v.addAll(l);
		return v;
	} 
	
	public void add(String faculty_code, String faculty_name, Institution institution) throws Exception {
		pm = new PersistenceManager();
		faculty = new Faculty();
		faculty.setCode(faculty_code);
		faculty.setName(faculty_name);
		faculty.setInstitution(institution);
		PersistenceManager.add(faculty);
			
	}
	
	public void update(String faculty_id, String faculty_code, String faculty_name,Institution institution) throws Exception {
		pm = new PersistenceManager();
		faculty = (Faculty)pm.find(Faculty.class).whereId(faculty_id).forUpdate();
		if(faculty != null){
			faculty.setCode(faculty_code);
			faculty.setName(faculty_name);
			faculty.setInstitution(institution);
			pm.update();
		}
			
	}
	public Faculty get(String id)throws Exception{
		pm = new PersistenceManager();
		faculty = (Faculty)pm.find(Faculty.class,id);
		return faculty;
	}
	
	public void delete(String faculty_id) throws Exception {
		pm = new PersistenceManager();
		faculty = (Faculty)pm.find(Faculty.class,faculty_id);
		pm.delete(faculty);
			
	}					
}
