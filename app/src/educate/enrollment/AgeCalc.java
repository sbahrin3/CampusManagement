package educate.enrollment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AgeCalc {
	
	public int getAge(Date dob) {
		Calendar dateOfBirth = Calendar.getInstance();
	    dateOfBirth.setTime(dob);
	    Calendar today = Calendar.getInstance();
	    int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
	    dateOfBirth.add(Calendar.YEAR, age);
	    if (today.before(dateOfBirth)) age--;
	    return age;
	}
	
	public static void main(String[] args) {
		AgeCalc a = new AgeCalc();
	    Calendar c = new GregorianCalendar(1997, Calendar.JULY, 1);
	    Date dob = c.getTime();
		System.out.println(a.getAge(dob));
	}

}
