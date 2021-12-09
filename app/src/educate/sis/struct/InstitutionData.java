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

import educate.sis.struct.entity.Institution;
import lebah.db.PersistenceManager;

public class InstitutionData {
	private PersistenceManager pm;
	private Institution institution;
	public static void tmp() throws Exception {
		
	}	
	
	
	public  void add(Institution ins) throws Exception {
		pm = new PersistenceManager();
		PersistenceManager.add(ins);
	}	
	
	public  Vector getList() throws Exception {
		pm = new PersistenceManager();
		List<Institution> l =pm.list("SELECT a FROM Institution a");
		Vector v = new Vector();
		v.addAll(l);
		return v;
	}
	
	public void update(String id,String name,String abbrv) throws Exception {
		pm = new PersistenceManager();
		institution = (Institution)pm.find(Institution.class).whereId(id).forUpdate();
		institution.setName(name);
		institution.setAbbrev(abbrv);
		pm.update();
		
	}			
	
	public void delete(String id) throws Exception {
		pm = new PersistenceManager();
		institution = (Institution)pm.find(Institution.class,id);
		if(institution !=null){
			pm.delete(institution);
		}
	}			
	
}
