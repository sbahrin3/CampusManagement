/* ************************************************************************
LEBAH PORTAL FRAMEWORK, http://lebah.sf.net
Copyright (C) 2007  Shamsul Bahrin








MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

* ************************************************************************ */
package educate.sis.billing;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpSession;

import org.apache.velocity.Template;

import lebah.db.Db;
import lebah.db.SQLRenderer;

/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
public class ReceiptNumberFormatModule extends lebah.portal.velocity.VTemplate {
	
	@Override
	public Template doTemplate() throws Exception {
		HttpSession session = request.getSession();
		
		String template_name = "vtl/sis/billing/receipt_number_format.vm";
		String submit = getParam("command");
		
		if ( "update".equals(submit) ) {
			String prefix = getParam("prefix");
			String numsize = getParam("size");
			updateReceiptFormat(prefix, Integer.parseInt(numsize));	
		}
        else if ( "reNumberUpdate".equals(submit) ) {
            String prefix = getParam("prefix");
            String numsize = getParam("size");
            String current = getParam("current");
            reNumberUpdateReceiptFormat(prefix, Integer.parseInt(numsize), Integer.parseInt(current)); 
        }		
		Hashtable formatInfo = getReceiptFormat();
		context.put("formatInfo", formatInfo);
		
		
		Template template = engine.getTemplate(template_name);	
		return template;		
	}
	
	public static Hashtable getReceiptFormat() throws Exception {
        Format formatter;
        Date date = new Date();
        formatter = new SimpleDateFormat("MM");
        String mn = formatter.format(date);        
		Db db = null;
		String sql = "";
		try {
			String strNo = "";
			String prefix = "";
			char[] cf = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',};
			int num_size = 0, current_num = 0;
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			Hashtable h = new Hashtable();
			boolean found = false;
			{
				r.add("doc_name");
				r.add("doc_name", "student_receipt");
				sql = r.getSQLSelect("billing_doc");
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) found = true;
				
			}
			if ( found ){
				r.clear();
				r.add("doc_name", "student_receipt");
				r.add("doc_prefix");
				r.add("doc_num_size");
				r.add("doc_current_num");
				sql = r.getSQLSelect("billing_doc");
				ResultSet rs = stmt.executeQuery(sql);
				
				if ( rs.next() ) {
					prefix = rs.getString("doc_prefix");
                    h.put("prefix", prefix);
					num_size = rs.getInt("doc_num_size");
					current_num = rs.getInt("doc_current_num");
                    if ( prefix.indexOf("{YY}") > -1 ) {
                        formatter = new SimpleDateFormat("yy");
                        String yr = formatter.format(date); 
                        prefix = replace(prefix, "{YY}", yr);
                    }
                    if ( prefix.indexOf("{YYYY}") > -1 ) {
                        formatter = new SimpleDateFormat("yyyy");
                        String yr = formatter.format(date); 
                        prefix = replace(prefix, "{YYYY}", yr);
                    }
                    if ( prefix.indexOf("{MM}") > -1 ) {
                        formatter = new SimpleDateFormat("MM");
                        String m = formatter.format(date);  
                        prefix = replace(prefix, "{MM}", m);
                    }
                    if ( prefix.indexOf("{M}") > -1 ) {
                        formatter = new SimpleDateFormat("M");
                        String m = formatter.format(date);  
                        prefix = replace(prefix, "{M}", m);
                    }
					strNo = prefix + new java.text.DecimalFormat(new String(cf, 0, num_size)).format(current_num);
					h.put("size", Integer.toString(num_size));
					h.put("current", Integer.toString(current_num));
					h.put("result", strNo);
				}
			}
			else {
				r.clear();
				r.add("doc_name", "student_receipt");
				r.add("doc_prefix", "");
				r.add("doc_num_size", 5);
				r.add("doc_current_num", 0);
				sql = r.getSQLInsert("billing_doc");
				stmt.executeUpdate(sql);
				h.put("prefix", "");
				h.put("size", "5");
				h.put("current", "0");
				h.put("result", "00000");				
			}
			return h;
		} finally {
			if ( db != null ) db.close();
		}	
	}
	
	public static void updateReceiptFormat(String prefix, int numsize) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			sql = r
            .update("doc_name", "student_receipt")
			.add("doc_prefix", prefix)
			.add("doc_num_size", numsize)
			.getSQLUpdate("billing_doc")
            ;
			stmt.executeUpdate(sql);	
		} finally {
			if ( db != null ) db.close();
		}
	}
    
    public static void reNumberUpdateReceiptFormat(String prefix, int numsize, int current) throws Exception {
        Db db = null;
        String sql = "";
        try {
            db = new Db();
            Statement stmt = db.getStatement();
            SQLRenderer r = new SQLRenderer();
            sql = r
            .update("doc_name", "student_receipt")
            .add("doc_prefix", prefix)
            .add("doc_num_size", numsize)
            .add("doc_current_num", current)
            .getSQLUpdate("billing_doc")
            ;
            stmt.executeUpdate(sql);    
        } finally {
            if ( db != null ) db.close();
        }
    }    
    
    static String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();
    
        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e+pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }       
}		
