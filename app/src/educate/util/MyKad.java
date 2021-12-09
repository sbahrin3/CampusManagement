package educate.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyKad {
	
	private String no = "";
	private String stateName = "";
	private String gender = ""; //M or F
	private Date dateOfBirth;
	private String _dateOfBirth;
	
	public String getNumber() {
		return no;
	}
	
	public MyKad(String s) throws ContainsOnlyNumbersException, DatePartNotValidException, NumberLengthException, BirthPlacePartException {
		no = s.replace("-", "");
		
		if ( no.length() != 12 ) {
			throw new NumberLengthException();
		}
		
		if ( !containsOnlyNumbers(no)) {
			throw new ContainsOnlyNumbersException();
		}
		String s1 = no.substring(0, 6);
		String s2 = no.substring(6,8);
		String s3 = no.substring(8);
		String yy = s1.substring(0,2);
		String mm = s1.substring(2,4);
		String dd = s1.substring(4);
		int y = Integer.parseInt(yy);	
		yy = ( y > 0 && y < 20 ) ? "20" + yy : "19" + yy;
		_dateOfBirth = dd + "-" + mm + "-" + yy;
		if ( !isValidDate(_dateOfBirth)) {
			throw new DatePartNotValidException();
		}
		
		stateName = getStateName(s2);
		if ( "".equals(stateName)) {
			throw new BirthPlacePartException();
		}
		
		String g = s3.substring(3);
		if ( "1 3 5 7 9".indexOf(g) > -1 ) {
			gender = "M";
		} else {
			gender = "F";
		}
	}
	
	public static boolean isValidDate(String inDate) {
		if (inDate == null)
			return false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		if (inDate.trim().length() != dateFormat.toPattern().length())
			return false;
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate.trim());
		} catch (Exception pe) {
			return false;
		}
		return true;
	}

	public static boolean containsOnlyNumbers(String str) {
		if (str == null || str.length() == 0)
			return false;

		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i)))
				return false;
		}

		return true;
	}
	
	public String getStateName(String code) {
		//01, 21, 22, 23, 24 � Johor
		if ( "01, 21, 22, 23, 24".indexOf(code) > -1 ) return "Johor";
		//02, 25, 26, 27 � Kedah
		else if ( "02, 25, 26, 27".indexOf(code) > -1 ) return "Kedah";
		//03, 28, 29 � Kelantan
		else if ( "03, 28, 29".indexOf(code) > -1 ) return "Kelantan";
		//04, 30 � Malacca
		else if ( "04, 30".indexOf(code) > -1 ) return "Melaka";
		//05, 31, 59 � Negeri Sembilan
		else if ( "05, 31, 59".indexOf(code) > -1 ) return "Negeri Sembilan";
		//06, 32, 33 � Pahang
		else if ( "06, 32, 33".indexOf(code) > -1 ) return "Pahang";
		//07, 34, 35 � Penang
		else if ( "07, 34, 35".indexOf(code) > -1 ) return "Penang";
		//08, 36, 37, 38, 39 � Perak
		else if ( "08, 36, 37, 38, 39".indexOf(code) > -1 ) return "Perak";
		//09, 40 � Perlis
		else if ( "09, 40".indexOf(code) > -1 ) return "Perlis";
		//10, 41, 42, 43, 44 � Selangor
		else if ( "10, 41, 42, 43, 44".indexOf(code) > -1 ) return "Selangor";
		//11, 45, 46 � Terengganu
		else if ( "11, 45, 46".indexOf(code) > -1 ) return "Terengganu";
		//12, 47, 48, 49 � Sabah
		else if ( "12, 47, 48, 49".indexOf(code) > -1 ) return "Sabah";
		//13, 50, 51, 52, 53 � Sarawak
		else if ( "13, 50, 51, 52, 53".indexOf(code) > -1 ) return "Sarawak";
		//14, 54, 55, 56, 57 � Federal Territory of Kuala Lumpur
		else if ( "14, 54, 55, 56, 57".indexOf(code) > -1 ) return "Kuala Lumpur";
		//15, 58 � Federal Territory of Labuan
		else if ( "15, 58".indexOf(code) > -1 ) return "Labuan";
		//16 � Federal Territory of Putrajaya
		else if ( "16".indexOf(code) > -1 ) return "Putrajaya";
		return "Other";
	}

	public String getStateName() {
		return stateName;
	}

	public String getGender() {
		return gender;
	}
	
	public Date getDateOfBirth() {
		try {
			return new SimpleDateFormat("dd-MM-yyyy").parse(_dateOfBirth);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		String icno = "970929146533";
		MyKad mk = new MyKad(icno);
		
		String icleft = icno.substring(0, 6);
		String icmiddle = icno.substring(6,8);
		String icright = icno.substring(8);
		
		System.out.println(icleft + "-" + icmiddle + "-" + icright);
	}
}
