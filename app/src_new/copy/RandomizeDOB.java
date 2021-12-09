/**
 * 
 */
package copy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class RandomizeDOB {
	
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		List<Student> students = db.list("select s from Student s");
		db.begin();
		for ( Student s : students ) {
			if ( s.getBiodata().getIcno() != null ) {
				
				GregorianCalendar gc = new GregorianCalendar();
		        int year = randBetween(1991, 1999);
		        gc.set(gc.YEAR, year);
		        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
		        gc.set(gc.DAY_OF_YEAR, dayOfYear);
		        Date date = gc.getTime();
		        String _date = new SimpleDateFormat("yyMMdd").format(date);
		        String icno = s.getBiodata().getIcno();
		        if ( icno.length() == 12 ) {
		        	String icno3 = icno.substring(8);
		        	String newICNO = _date + "14" + icno3;
		        	s.getBiodata().setIcno(newICNO);
		        } else {
		        	s.getBiodata().setIcno("");
		        	s.getBiodata().setPassport(_date);
		        }
		        System.out.println(s.getBiodata().getName());
		        
		        s.getAddress().setAddress1("");
		        s.getAddress().setAddress2("");
		        s.getAddress().setAddress3("");
		        s.getAddress().setAddress4("");
		        s.getPermanentAddress().setAddress1("");
		        s.getPermanentAddress().setAddress2("");
		        s.getPermanentAddress().setAddress3("");
		        s.getPermanentAddress().setAddress4("");
		        s.getBiodata().setTelephoneNo("");
		        s.getBiodata().setMobileNo("");
			}
		}
		db.commit();
	}
	
	public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

}
