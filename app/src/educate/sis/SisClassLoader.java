/* ************************************************************************
LEBAH PORTAL FRAMEWORK, http://lebah.sf.net
Copyright (C) 2007  Shamsul Bahrin








MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

* ************************************************************************ */
package educate.sis;

/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
import java.util.Hashtable;
public class SisClassLoader {
	static Hashtable loadedList = new Hashtable();

	public static Class load(String className) throws Exception {
		Class klazz = null;
		klazz = (Class) loadedList.get(className);
		//if not load it
		if ( klazz == null ) {
			//klazz = Class.forName(className);
			Thread t = Thread.currentThread();
			ClassLoader cl = t.getContextClassLoader();
			klazz = cl.loadClass(className);			
			//put into cache
			loadedList.put(className, klazz);
		}
		return klazz;
	}
}
