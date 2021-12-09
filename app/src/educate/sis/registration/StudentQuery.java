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

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.velocity.VelocityContext;

/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.00
 */
public class StudentQuery {

	public static boolean initDataset(HttpSession session, HttpServletRequest request, VelocityContext context) throws Exception {
		CachedDataset dataset = null;
		Hashtable h = null;
		String sql = "";
		sql = "select id, name, icno from student order by name";
		dataset = new CachedDataset();
        int cnt = dataset.getData(session, sql);
        session.setAttribute("dataset", dataset);
        return cnt > 0;
	}
	
	public static boolean initDataset(HttpSession session, HttpServletRequest request, VelocityContext context, String sql) throws Exception {
		CachedDataset dataset = null;
		Hashtable h = null;
		dataset = new CachedDataset();
        int cnt = dataset.getData(session, sql);
        session.setAttribute("dataset", dataset);
        return cnt > 0;
	}
	
	public static boolean initDataset(HttpSession session, HttpServletRequest request, VelocityContext context, String[] fields, String where) throws Exception {
		CachedDataset dataset = null;
		Hashtable h = null;
		String sql = "";
		//sql = "select id, name, icno from student order by name";
		sql = "select ";
		for ( int i=0; i < fields.length; i++ ) {
		    String field = fields[i];
		    sql += field;
		    if ( i < fields.length-1 ) sql += ", ";
		}
		sql += " from student ";
		if ( !"".equals(where) ) {
		    sql += " where " + where;
		}
		sql += " order by name ";
		
	
		dataset = new CachedDataset();
        int cnt = dataset.getData(session, sql);
        session.setAttribute("dataset", dataset);
        return cnt > 0;
	}	
	
	
	public static Hashtable getDataset(HttpSession session, HttpServletRequest request, VelocityContext context) throws Exception {
		int pageNum = request.getParameter("pageNum") != null ?
				Integer.parseInt(request.getParameter("pageNum")) : 1;
		context.put("pageNum", new Integer(pageNum));
		int rowNum = request.getParameter("rows") != null ?
        		Integer.parseInt(request.getParameter("rows")) : 20;	
        context.put("rows", new Integer(rowNum));
        CachedDataset dataset = (CachedDataset) session.getAttribute("dataset");
        Hashtable h = dataset.getDataset2(session, rowNum, pageNum);
	    context.put("timeElapse", h.get("timeElapse"));
	    context.put("numOfRecords", h.get("numOfRecords"));
	    context.put("items", h.get("items"));
	    context.put("pages", h.get("pages"));
	    context.put("startNo", h.get("startNo"));
		return h;
	}
	
	public static Hashtable getDataset(HttpSession session, HttpServletRequest request, VelocityContext context, String[] fields) throws Exception {
		int pageNum = request.getParameter("pageNum") != null ?
				Integer.parseInt(request.getParameter("pageNum")) : 1;
		context.put("pageNum", new Integer(pageNum));
		int rowNum = request.getParameter("rows") != null ?
        		Integer.parseInt(request.getParameter("rows")) : 20;	
        context.put("rows", new Integer(rowNum));
        CachedDataset dataset = (CachedDataset) session.getAttribute("dataset");
        Hashtable h = dataset.getDataset2(session, rowNum, pageNum, fields);
	    context.put("timeElapse", h.get("timeElapse"));
	    context.put("numOfRecords", h.get("numOfRecords"));
	    context.put("items", h.get("items"));
	    context.put("pages", h.get("pages"));
	    context.put("startNo", h.get("startNo"));
		return h;
	}	
	

	
}
