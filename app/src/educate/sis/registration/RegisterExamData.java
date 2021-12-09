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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import lebah.db.DataHelper;
import lebah.db.Db;
import lebah.db.SQLPStmtRenderer;
import lebah.db.SQLRenderer;

/**
 * 
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 * @version 0.1
 */

public class RegisterExamData {
    
    public final static String TUTORIAL = "T";
    public final static String EXAMINATION = "E";
    public final static String EXEMPTION = "EX";
    public final static String SPECIAL_SESSION = "SS";
    
    public static void addSubjects(final String studentId, 
    		final String[] subjects, final String type) throws Exception {
    	
        new DataHelper() {
            public String[] doManySQL() {
            	
            	
                String[] sql = new String[subjects.length+1]; //include first delete statement
                SQLRenderer r = new SQLRenderer();
                //delete first
                sql[0] = r.add("student_id", studentId).add("type", type).getSQLDelete("exam_register");
                //then insert
                for ( int i=0; i < subjects.length; i++ ) {
                    r.clear();
                    r.add("student_id", studentId).add("subject_id", subjects[i]);
                    r.add("type", type);
                    sql[i+1] = r.getSQLInsert("exam_register");
                }
                return sql;
            }
        }.executeMany();

    }
    
    public static Hashtable getExamDate() throws Exception {
        return
        new DataHelper() {
            public String doSQL() {
                SQLRenderer r = new SQLRenderer();
                return r
                .add("subject_id").add("date_exam")
                .getSQLSelect("exam_date")
                ;
            }
            public void doMappings(ResultSet rs, Hashtable h) throws Exception {
                h.put(rs.getString("subject_id"), rs.getDate("date_exam"));
            }
        }.getMapping();
    }
    
    public static Vector getSubjects(final String studentId, final String type) throws Exception {

        return new DataHelper() {
            int counter = 0;
            public String doSQL(){
                SQLRenderer r = new SQLRenderer();
                r.add("fs.subject_id")
                .add("s.currency_type")
                .add("fs.subject_code")
                .add("fs.subject_name")
                .add("fee.amount_local")
                .add("fee.amount_international");
                
                if ( "EX".equals(type)){ 
                	r
                	.add("ed.date_exam")
                	.add("fs.subject_id", r.unquote("ed.subject_id"))
                	;
                }
                
                r
                .add("ex.student_id", studentId) //for this student
                .add("ex.subject_id", r.unquote("fs.subject_id")) //relate to table faculty_subject
                .add("ex.subject_id", r.unquote("fee.subject_id")) //relate to table fee_structure_subject
                .add("s.id", r.unquote("ex.student_id"))
                .add("ex.type", type)
                .add("fee.fee_type", type) //type is EXAMINATION
                ;
                String tables = "faculty_subject fs, exam_register ex, fee_structure_subject fee, student s";
                if ( "EX".equals(type)) tables +=  ", exam_date ed";
                String sql = r.getSQLSelect(tables);
               
                return sql;
            }
            
            public Object createObject(ResultSet rs) throws Exception {
                String currency_type = rs.getString("currency_type");
                float amount = "local".equals(currency_type) ? rs.getFloat("amount_local") : rs.getFloat("amount_international");
                String amountStr = new java.text.DecimalFormat("#,###,###.00").format(amount);
                Hashtable h = new Hashtable();
                h.put("docnum", "A"); //for the purpose of statement of eligibility
                h.put("item", Integer.toString(++counter)); //for the purpose of statement of eligibility
                if ( "EX".equals(type)) {
                	h.put("exam_date", new SimpleDateFormat("d MMM, yyyy").format(rs.getDate("date_exam")));  //for the purpose of statement of eligibility
                }
                h.put("subject_id", rs.getString("subject_id"));
                h.put("subject_code", rs.getString("subject_code"));
                h.put("subject_name", rs.getString("subject_name"));
                h.put("amount", new Float(amount));
                h.put("amountStr", amountStr);
                h.put("currency", "local".equals(currency_type) ? "RM" : "USD");
                return h;
            }
        }.getObjectList();
    }
    
    public static Vector getSubjects(final String studentId, String[] subjects, final String type) throws Exception {
        
        final Vector subjectIds = new Vector(Arrays.asList(subjects));

        return new DataHelper() {
            public String doSQL(){
                SQLRenderer r = new SQLRenderer();
                r.add("fs.subject_id")
                .add("s.currency_type")
                .add("fs.subject_code")
                .add("fs.subject_name")
                .add("fee.amount_local")
                .add("fee.amount_international")
                .add("ex.student_id", studentId) //for this student
                .add("ex.subject_id", r.unquote("fs.subject_id")) //relate to table faculty_subject
                .add("ex.subject_id", r.unquote("fee.subject_id")) //relate to table fee_structure_subject
                .add("s.id", r.unquote("ex.student_id"))
                .add("ex.type", type)
                .add("fee.fee_type", type)
                ;
                String sql = r.getSQLSelect("faculty_subject fs, exam_register ex, fee_structure_subject fee, student s");
                return sql;
            }
            
            public Object createObject(ResultSet rs) throws Exception {
                String subjectId = rs.getString("subject_id");
                if ( subjectIds.contains(subjectId)) {
                    String currency_type = rs.getString("currency_type");
                    float amount = "international".equals(currency_type) ? rs.getFloat("amount_international") : rs.getFloat("amount_local");
                    String amountStr = new java.text.DecimalFormat("#,###,###.00").format(amount);
                    Hashtable h = new Hashtable();
                    h.put("subject_id", rs.getString("subject_id"));
                    h.put("subject_code", rs.getString("subject_code"));
                    h.put("subject_name", rs.getString("subject_name"));
                    h.put("amount", new Float(amount));
                    h.put("amountStr", amountStr);
                    h.put("currency", "international".equals(currency_type) ? "USD" : "RM");
                    return h;
                }
                return null;
            }
        }.getObjectList();
    }    
    
    public static void setInvoiceNo2(final String studentId, final String invoiceNo, String[] subjects, final String type) throws Exception {
    	Db db = null;
    	try {
    		db = new Db();
    		SQLPStmtRenderer r = new SQLPStmtRenderer();
    		r
    		.update("student_id", "")
    		.add("subject_id", "")
    		.add("invoice_no", "")
    		.add("type", "")
    		//.getSQLUpdate("exam_register")
    		;
    		r.getPStmtUpdate(db.getConnection(), "exam_register");
    		for ( int i=0; i < subjects.length; i++) {
    			r.setUpdate("student_id", studentId)
    			.set("invoice_no", invoiceNo)
    			.set("subject_id", subjects[i])
    			.set("type", type)
    			;
    			r.getPStmt().executeUpdate();
    		}
    	} finally {
    		if ( db != null) db.close();
    	}
    }
    
    public static void setInvoiceNo(String studentId, String invoiceNo, String[] subjects, String type) throws Exception {
    	Db db = null;
    	try {
    		db = new Db();
    		Statement stmt = db.getStatement();
    		SQLRenderer r = new SQLRenderer();
    		for ( int i=0; i < subjects.length; i++) {
    			stmt.executeUpdate(
    			r.reset().update("student_id", studentId).add("subject_id", subjects[i])
    			//.add("invoice_no", invoiceNo)
    			.add("type", type)
    			.getSQLUpdate("exam_register")
    			);

    		}
    	} finally {
    		if ( db != null) db.close();
    	}
    } 
    
    public static void setExamRegisterInvoice(final String studentId, final String invoiceNo, final String type) throws Exception {
    	Db db = null;
    	try {
    		db = new Db();
    		SQLRenderer r = new SQLRenderer();
    		boolean toInsert = !(db.getStatement().executeQuery(
				r
				.add("student_id", studentId)
				.add("type", type)
				.add("student_id")
				.getSQLSelect("exam_register_invoice")
    		).next());
    		r.reset();
    		if ( toInsert ) {
    			r.add("student_id", studentId);
    			r.add("type", type);
    		}
    		else r.update("student_id", studentId);
    		r.add("invoice_no", invoiceNo);
    		db.getStatement().executeUpdate(
    				toInsert ? r.getSQLInsert("exam_register_invoice") 
    				: r.getSQLUpdate("exam_register_invoice")
    				);
    	} finally {
    		if ( db != null ) db.close();
    	}
    }
    
    //public static String getExamRegisterInvoice(final String studentId, final String type) throws Exception {
    public static String getExamRegisterInvoice(final String studentId, final String type) throws Exception {
    	return (String) new DataHelper() {
    		public String doSQL() {
    			SQLRenderer r = new SQLRenderer();
    			r.add("student_id", studentId);
    			r.add("type", type);
    			r.add("invoice_no");
    			return r.getSQLSelect("exam_register_invoice");
    		}
    		public Object createObject(ResultSet rs) throws Exception {
    			return rs.getString("invoice_no");
    		}
    	}.getObject();
    }
    
    public static String delete(final String studentId, final String type) throws Exception {
        Db db = null;
        String sql = "";
        String invoiceNo = "";
        try {
            db = new Db();
            SQLRenderer r = new SQLRenderer();
            //get invoice no
            sql = r
            .add("student_id", studentId)
            .add("type", type)
            .add("invoice_no")
            .getSQLSelect("exam_register_invoice")
            ;            
            ResultSet rs = db.getStatement().executeQuery(sql);
            if ( rs.next()) invoiceNo = rs.getString(1);
            
            //delete from exam_register_invoice
            sql = r
            .reset()
            .add("student_id", studentId)
            .add("type", type)
            .getSQLDelete("exam_register_invoice")
            ;
            db.getStatement().executeUpdate(sql);
            
            return invoiceNo;
        } finally {
            if ( db != null ) db.close();
        }
    }
    
    public static void main(String[] args) throws Exception {
        Hashtable h = getExamDate();
        for ( Enumeration e = h.keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            System.out.println(key);
            Date date = (Date) h.get(key);
            System.out.println(date);
        }
    }
    
}
