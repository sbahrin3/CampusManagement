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

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import com.sun.rowset.CachedRowSetImpl;

import lebah.db.Db;
import lebah.db.SQLRenderer;
import lebah.upload.FileLocation;
import lebah.util.DateTool;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 * @version 0.1
 */
public class CachedDataset {
	protected int numberOfRows = 0;
    protected CachedRowSetImpl rowSet = null;
    protected String timeElapse = "";
    protected String fields[];
    protected Hashtable periodRef;
    protected String programCode = "";
    static Hashtable studyStatus;
    static Hashtable services, kors, units, ranks;
    
    
    public CachedDataset() {
        Db db = null;
        String sql = "";
        try {
            db = new Db();
            Statement stmt = db.getStatement();
            SQLRenderer r = new SQLRenderer();
            studyStatus = getStudyStatus(stmt, r);
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            if ( db != null ) db.close();
        }
    }
    
    static Hashtable getStudyStatus(Statement stmt, SQLRenderer r) throws Exception {
    	String sql = r
    	.reset()
    	.add("status_id")
    	.add("status_name")
    	.getSQLSelect("study_status")
    	;
    	ResultSet rs = stmt.executeQuery(sql);
    	Hashtable<String, String> h = new Hashtable<String, String>();
    	h.put("active", "ACTIVE");
    	while ( rs.next() ) {
    		h.put(rs.getString(1), rs.getString(2));
    	}
    	return h;
    }
	
	public int setResultSet(HttpSession session, ResultSet rs) throws Exception {
	    GregorianCalendar time1 = new GregorianCalendar();
	    rowSet = new CachedRowSetImpl();
	    rowSet.populate(rs);
        int numberOfRows = 0;
        rs.last();
        numberOfRows = rs.getRow();	 
        GregorianCalendar time2 = new GregorianCalendar();
	    long diffMillis = time2.getTimeInMillis() - time1.getTimeInMillis();
	    timeElapse = Double.toString((double) diffMillis/1000) + " seconds";
	    session.setAttribute("records", rowSet);
	    session.setAttribute("numOfRows", new Integer(numberOfRows));
	    session.setAttribute("timeElapse", timeElapse);
	    return numberOfRows;
	}
	
	public Hashtable getDataset(HttpSession session, int rowNum, int pageNum) throws Exception {
		int itemNum = ((pageNum-1)*rowNum) + 1;
        int numOfItems = rowNum;
        rowSet = (CachedRowSetImpl) session.getAttribute("records");
        numberOfRows = ((Integer) session.getAttribute("numOfRows")).intValue();
        timeElapse = (String) session.getAttribute("timeElapse");
	    int pages =  numberOfRows / numOfItems;
		double leftover = ((double)numberOfRows % (double)numOfItems);
		if ( leftover > 0.0 ) ++pages;
	    Vector<Hashtable> items = new Vector<Hashtable>();
	    int cnt = 1;
	    rowSet.absolute(itemNum);
        addField(items);
	    while ( rowSet.next() ) {
	        if ( cnt++ == numOfItems ) break;
	        addField(items);
	    }
	    Hashtable<String, Object> context = new Hashtable<String, Object>();
	    context.put("timeElapse", timeElapse);
	    context.put("numOfRecords", numberOfRows);
	    context.put("items", items);
	    context.put("pages", pages);
	    context.put("startNo", itemNum);
	    return context;
	}
	
	public Vector getDataset(HttpSession session) throws Exception {
        rowSet = (CachedRowSetImpl) session.getAttribute("records");
        numberOfRows = ((Integer) session.getAttribute("numOfRows")).intValue();
	    Vector<Hashtable> items = new Vector<Hashtable>();
	    rowSet.absolute(1);
        addField(items);
	    while ( rowSet.next() ) {
	        addField(items);
	    }
	    return items;
	}	
	
	void addField(Vector<Hashtable> items) throws Exception {
        Hashtable<String, Object> h = new Hashtable<String, Object>();
        String period_root_id = rowSet.getString("period_root_id");
        for ( int i=0; i < fields.length; i++ ) {
            if ( fields[i].equals("period_name")) {
                String period_id = rowSet.getString("period_id");
                String period_name = "";
                if ( !"".equals(programCode) ) {
                    period_name = periodRef.get(period_id) != null ? (String) periodRef.get(period_id) : "";
                } 
                else {
                    Hashtable h2 = (Hashtable) periodRef.get(period_root_id);
                    period_name = h2.get(period_id) != null ? (String) h2.get(period_id) : "";
                }
                h.put(fields[i], period_name != null ? period_name : "");
            }
            else if ( fields[i].equals("country")) {
                String country_code = rowSet.getString("country_code");
                h.put(fields[i], country_code != null ? country_code : "");
            }
            else if ( fields[i].equals("birth_date")) {
                String birth_date = DateTool.getDateFormatted(rowSet.getDate("birth_date"));
                h.put(fields[i], birth_date != null ? birth_date : "");
            }
            else if ( fields[i].equals("photoFileName")) {
            	String photoFileName = rowSet.getString("photoFileName");
            	String studentId = rowSet.getString("id");
            	String dir = FileLocation.getPATH() + "photos/" + studentId + "/thumb";
            	if ( photoFileName != null && !"".equals(photoFileName)) {
            		dir = "<img src='../download?file=" + dir + photoFileName + "'>";
            	} else {
            		dir = "<img src='../img/silho.gif'>";
            	}
                h.put(fields[i], dir);
            }
            else if ( fields[i].equals("code_service")) {
                String s = rowSet.getString(fields[i]);
                h.put(fields[i], s != null ? ((Code) services.get(s)).getName() : "");
            }
            else if ( fields[i].equals("code_unit")) {
                String s = rowSet.getString(fields[i]);
                h.put(fields[i], s != null ? ((Code) units.get(s)).getName() : "");
            }
            else if ( fields[i].equals("code_rank")) {
                String s = rowSet.getString(fields[i]);
                h.put(fields[i], s != null ? ((Code) ranks.get(s)).getName() : "");
            }            
            else if ( fields[i].equals("code_choir")) {
                String s = rowSet.getString(fields[i]);
                h.put(fields[i], s != null ? ((Code) kors.get(s)).getName() : "");
            }     
            else if ( fields[i].equals("status")) {
            	String s = rowSet.getString(fields[i]);
            	h.put(fields[i], s != null ? studyStatus.get(s) : "");
            }
            else {
                String s = rowSet.getString(fields[i]);
                h.put(fields[i], s != null ? s : "");
            }
        }
        items.addElement(h);	    
	}
	

	public int getData(HttpSession session, String sql, String fields[], Hashtable periodRef, String program_code) throws Exception {
	    Db db = null;
	    try {
	        db = new Db();
	        Statement stmt = db.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	        ResultSet rs = stmt.executeQuery(sql);
	        int cnt = setResultSet(session, rs);
	        this.fields = fields;
	        this.periodRef = periodRef;
	        this.programCode = program_code;
	        return cnt;
	    } finally {
	        if ( db != null ) db.close();
	    }
	}
	
	public int getData(HttpSession session, String sql) throws Exception {
	    Db db = null;
	    try {
	        db = new Db();
	        Statement stmt = db.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	        ResultSet rs = stmt.executeQuery(sql);
	        int cnt = setResultSet(session, rs);
	        return cnt;
	    } finally {
	        if ( db != null ) db.close();
	    }
	}
	
	public Hashtable getDataset2(HttpSession session, int rowNum, int pageNum) throws Exception {

		int itemNum = ((pageNum-1)*rowNum) + 1;
        int numOfItems = rowNum;
        rowSet = (CachedRowSetImpl) session.getAttribute("records");
        numberOfRows = ((Integer) session.getAttribute("numOfRows")).intValue();
        timeElapse = (String) session.getAttribute("timeElapse");
	    int pages =  numberOfRows / numOfItems;
		double leftover = ((double)numberOfRows % (double)numOfItems);
		if ( leftover > 0.0 ) ++pages;
	    Vector items = new Vector();
	    int cnt = 1;
	    rowSet.absolute(itemNum);

	    Hashtable item = new Hashtable();
        item.put("id", rowSet.getString("id").trim());
        item.put("name", rowSet.getString("name").trim());
        item.put("icno", rowSet.getString("icno").trim());
        items.addElement(item);
        while ( rowSet.next()) {
        	if ( cnt++ == numOfItems ) break;
        	item = new Hashtable();
        	String id = rowSet.getString("id");
        	//System.out.println("=" + id);
            item.put("id", rowSet.getString("id").trim());
            item.put("name", rowSet.getString("name").trim());
            item.put("icno", rowSet.getString("icno").trim());
            items.addElement(item);
        }
        
	    Hashtable context = new Hashtable();
	    context.put("timeElapse", timeElapse);
	    context.put("numOfRecords", new Integer(numberOfRows));
	    context.put("items", items);
	    context.put("pages", new Integer(pages));
	    context.put("startNo", new Integer(itemNum));
	    return context;
	}
	
	public Hashtable getDataset2(HttpSession session, int rowNum, int pageNum, String[] fields) throws Exception {

		int itemNum = ((pageNum-1)*rowNum) + 1;
        int numOfItems = rowNum;
        rowSet = (CachedRowSetImpl) session.getAttribute("records");
        numberOfRows = ((Integer) session.getAttribute("numOfRows")).intValue();
        timeElapse = (String) session.getAttribute("timeElapse");
	    int pages =  numberOfRows / numOfItems;
		double leftover = ((double)numberOfRows % (double)numOfItems);
		if ( leftover > 0.0 ) ++pages;
	    Vector items = new Vector();
	    int cnt = 1;
	    rowSet.absolute(itemNum);

	    Hashtable item = new Hashtable();
        for ( int i=0; i < fields.length; i++ ) {
            String field = fields[i];
            item.put(field, rowSet.getString(field).trim());
        }
        items.addElement(item);
        while ( rowSet.next()) {
        	if ( cnt++ == numOfItems ) break;
        	item = new Hashtable();
            for ( int i=0; i < fields.length; i++ ) {
                String field = fields[i];
                item.put(field, rowSet.getString(field).trim());
            }
            items.addElement(item);
        }
        
	    Hashtable context = new Hashtable();
	    context.put("timeElapse", timeElapse);
	    context.put("numOfRecords", new Integer(numberOfRows));
	    context.put("items", items);
	    context.put("pages", new Integer(pages));
	    context.put("startNo", new Integer(itemNum));
	    return context;
	}	
	

}
