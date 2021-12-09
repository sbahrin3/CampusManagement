/* ************************************************************************
LEBAH PORTAL FRAMEWORK, http://lebah.sf.net
Copyright (C) 2007  Shamsul Bahrin








MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

* ************************************************************************ */

package u.sams;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 * @version 0.1
 */
public class Database {
    public Connection conn = null;
    
    public Connection getConnection() throws Exception {
        return conn;
    }
    
    public Database(String driver, String url, String user, String passw) throws Exception {
        open(driver, url, user, passw);
    }
    
    public Statement getStatement() throws Exception {
        return conn.createStatement();
    }
    
    public void open(String driver, String url, String user, String passw) throws Exception {
        Driver d = (Driver)Class.forName(driver).newInstance();
        conn = DriverManager.getConnection(url, user, passw); 
    }

    public void close() {
        try { 
            conn.close(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
      }
}
