/**
 * @author Shaiful
 * @since Dec 6, 2010
 */
package educate.koha;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.ResourceBundle;

import educate.admission.entity.Address;
import educate.admission.entity.Biodata;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.koha.entity.KohaAccount;
import educate.sis.general.entity.GradsLevel;

public class KohaHelper {
	private String className = this.getClass().getName();
	private static KohaHelper object;
	private String url;
	private String password;
	private String user;
	private String driver;
	
	private String kohaPwdUrl;
	
	private KohaHelper() {
		try {
			getConnectionSource();
		} catch(Exception ex) {
			System.out.println("["+className+"] "+ex.getMessage());
		}
	}
	
	public static synchronized KohaHelper getInstance() {
		if (object == null) {
			object = new KohaHelper();
		}
		return object;
	}
	
    @Override
	public Object clone() throws CloneNotSupportedException {
    	/*
    	 * Do not allow this class to be cloned since
    	 * it is a singleton.
    	 */
    	throw new CloneNotSupportedException();
    }
    
    public synchronized KohaAccount createAccount(Student student, String pwdNormal) throws Exception {
    	return createAccount(student, pwdNormal, "");
    }
	
    public synchronized KohaAccount createAccount(Student student, String pwdNormal, String pwdEnc) throws Exception {
    	
    	if ( "".equals(pwdEnc)) {
    		pwdEnc = UrlRead.get(kohaPwdUrl + pwdNormal);
    		System.out.println("koha pwd: " + pwdNormal + " == " + pwdEnc);
    		
    		if ( "".equals(pwdEnc)) { //need to use default
    	    	pwdNormal = "EIUKLEB8";
    	    	pwdEnc = "RB3D5EAVqfdZsM/bErv77g";
    		}
    	}
    	


    	Connection conn = this.getConnection();
    	//look for cardnumber
    	ResultSet rs = conn.createStatement().executeQuery("select cardnumber from borrowers where cardnumber = '" + student.getMatricNo() + "'");
    	boolean isExist = rs.next();
    	System.out.println("Koha borrower " + student.getMatricNo() + ": " + isExist);
    	
    	String userid = student.getMatricNo();
    	if ( !isExist ) {
    		createBorrower(conn, student, userid, pwdEnc);
    	}
    	else {
    		//update borrower
    		updateBorrower(conn, pwdEnc, student);
    	}
    	
    	DbPersistence db = new DbPersistence();
    	KohaAccount acct = db.find(KohaAccount.class, userid);
    	if ( acct == null ) {
    		db.begin();
    		acct = new KohaAccount(userid);
        	acct.setPassword(pwdNormal);  //unencrypted password
        	db.persist(acct);
        	db.commit();
    	}
    	else {
    		db.begin();
        	acct.setPassword(pwdNormal);  //unencrypted password
        	db.commit();
    	}
    	return acct;
    }

	private void updateBorrower(Connection conn, String pwdEnc, Student student) throws SQLException {
		String categorycode = "";
		GradsLevel gradsLevel = student.getProgram().getLevel();
		if ( gradsLevel != null ) {
			System.out.println(gradsLevel.getCode() + ", " + gradsLevel.getName());
			if ( gradsLevel.getName().indexOf("POSTGRADUATE") > -1 ) {
				categorycode = "PG";
			}
			else {
				categorycode = "UG";
			}
		}
		
		//String sqlUpdate = "UPDATE borrowers SET password = '" + pwdEnc + "', categorycode = '" + categorycode + "' where cardnumber = '" + student.getMatricNo() + "'";
		String sqlUpdate = "UPDATE borrowers SET password = '" + pwdEnc + "' where cardnumber = '" + student.getMatricNo() + "'";
		conn.createStatement().executeUpdate(sqlUpdate);
	}

	private void createBorrower(Connection conn, Student student, String userid, String pwdEnc) throws SQLException {
		
		String categorycode = "";
		GradsLevel gradsLevel = student.getProgram().getLevel();
		if ( gradsLevel != null ) {
			System.out.println(gradsLevel.getCode() + ", " + gradsLevel.getName());
			if ( gradsLevel.getName().indexOf("POSTGRADUATE") > -1 ) {
				categorycode = "PG";
			}
			else {
				categorycode = "UG";
			}
		}
    	
		String sql = "";
		
		int flags = 384;
		
		Address address = student.getAddress();
		String streetAddress = "";
		if(address != null) {
			try {
				if(address.getAddress1() != null || !address.getAddress1().equals("")) {
					streetAddress = address.getAddress1();
				}
			} catch(Exception ex) {
				
			}
			try {
				if(address.getAddress2() != null || !address.getAddress2().equals("")) {
					streetAddress = streetAddress + " " + address.getAddress2();
				}
			} catch(Exception ex) {
				
			}
			try {
				if(address.getAddress3() != null || !address.getAddress3().equals("")) {
					streetAddress = streetAddress + " " + address.getAddress3();
				}
			} catch(Exception ex) {
				
			}
			try {
				if(address.getAddress4() != null || !address.getAddress4().equals("")) {
					streetAddress = streetAddress + " " + address.getAddress4();
				}
			} catch(Exception ex) {
				
			}
		}
		java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
		//java.text.DateFormat df = new java.text.SimpleDateFormat("dd-MM-yyyy");
		Biodata biodata = student.getBiodata();
		Calendar cal = Calendar.getInstance();
		

		String sqlInsert = "INSERT INTO borrowers (cardnumber, surname, firstname, streetaddress, suburb, " +
				"city, phone, emailaddress, dateofbirth, studentnumber, school, sex, password, " +
				"flags, userid, zipcode, gonenoaddress, lost, debarred, altphone, dateenrolled, " +
				"initials, categorycode) " +
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		
		PreparedStatement ps = conn.prepareStatement(sqlInsert);
		ps.setString(1, student.getMatricNo());
		
		String name = student.getBiodata().getName();
		String surname = name.substring(name.lastIndexOf(" "));
		String firstname = name.substring(0, name.lastIndexOf(" "));
		
		ps.setString(2, surname);
		try {
			ps.setString(3, firstname);
		} catch(Exception ex) {
			ps.setString(3, "");
		}
		ps.setString(4, streetAddress);
		try {
			ps.setString(5, address.getState() != null ? address.getState().getName() : "");
		} catch(Exception ex) {
			ps.setString(5, "");
		}
		try {
			ps.setString(6, address.getCity() != null ? address.getCity() : "");
		} catch(Exception ex) {
			ps.setString(6, "");
		}
		try {
			ps.setString(7, biodata.getTelephoneNo() != null ? biodata.getTelephoneNo() : "");
		} catch(Exception ex) {
			ps.setString(7, "");
		}
		try {
			ps.setString(8, biodata.getEmail() != null ? biodata.getEmail() : "");
		} catch(Exception ex) {
			ps.setString(8, "");
		}
		try {
			ps.setString(9, biodata.getDob() != null ? df.format(biodata.getDob()) : df.format(cal.getTime()));
			//ps.setDate(9,  biodata.getDob() != null ? biodata.getDob() : (Date) null);
		} catch(Exception ex) {
			ps.setString(9, df.format(cal.getTime()));
		}
		ps.setString(10, student.getMatricNo());
		try {
			ps.setString(11, student.getProgram().getCourse().getFaculty().getName());
		} catch(Exception ex) {
			ps.setString(11, "");
		}
		try {
			if ( biodata.getGender() != null ) {
				if(biodata.getGender().getCode().equals("1")) {
		    		ps.setString(12, "M");
				} else if(biodata.getGender().getCode().equals("2")) {
		    		ps.setString(12, "F");
				} else {
		    		ps.setString(12, "");
				}
			}
			else ps.setString(12, "");
		} catch(Exception ex) {
			ps.setString(12, "");
		}
		
		//PASSWORD
		
		try {
			ps.setString(13, pwdEnc);
		} catch(Exception ex) {
			//    		
		}
		
		//FLAGS
		ps.setInt(14, flags); //flags
		
		//USERID
		ps.setString(15, userid);
		
		
		try {
			ps.setString(16, address.getPostcode() != null ? address.getPostcode() : "");
		} catch(Exception ex) {
			ps.setString(16, "");    		
		}
		ps.setInt(17, 0);
		ps.setInt(18, 0);
		ps.setInt(19, 0);
		try {
			ps.setString(20, biodata.getMobileNo() != null ? biodata.getMobileNo() : "");
		} catch(Exception ex) {
			ps.setString(20, "");			
		}
		try {
			ps.setString(21, student.getRegisterDate() != null ? df.format(student.getRegisterDate()) : df.format(cal.getTime()));
		} catch(Exception ex) {
			ps.setString(21, df.format(cal.getTime()));
		}
		ps.setString(22, "");
		
		//categorycode
		ps.setString(23, categorycode);
		ps.executeUpdate();
		conn.close();
	}
    
    private synchronized void getConnectionSource() throws Exception {
    	ResourceBundle rb;
    	try {
    		rb = ResourceBundle.getBundle("kohadb");
    	} catch (java.util.MissingResourceException ex) {
    		throw new Exception("Unable to find kohadb.properties file.");
    	}
        driver = rb.getString("driver");
        url = rb.getString("url");
        password = rb.getString("password");
        user = rb.getString("user");
        
        try {
        	kohaPwdUrl = rb.getString("koha_pwd_url");
        } catch ( Exception e ) {
        	kohaPwdUrl = "";
        }
        
        System.out.println("["+className+"] driver = "+driver);
        System.out.println("["+className+"] url = "+url);
        System.out.println("["+className+"] password = "+password);
        System.out.println("["+className+"] user = "+user);
    }
    
    private synchronized Connection getConnection() throws Exception {
    	return DriverManager.getConnection(url, user, password);
    }
    
    
    public static String md5(String input) throws NoSuchAlgorithmException {
    	byte[] defaultBytes = input.getBytes();
    	try{
    		MessageDigest algorithm = MessageDigest.getInstance("MD5");
    		algorithm.reset();
    		algorithm.update(defaultBytes);
    		byte messageDigest[] = algorithm.digest();
    	            
    		StringBuffer hexString = new StringBuffer();
    		for (int i=0;i<messageDigest.length;i++) {
    			hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
    		}
    		String foo = messageDigest.toString();
    		System.out.println("sessionid "+input+" md5 version is "+hexString.toString());
    		return hexString.toString();
    	}catch(NoSuchAlgorithmException nsae){
    	            
    	}
    	return "";

    }

}
