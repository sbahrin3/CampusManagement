package educate.enrollment;

import educate.sis.struct.entity.MatricNumPrefix2;
import lebah.db.PersistenceManager;

public class MatricNumberUtil {
	
	public static int getCurrentNumber(String prefix) throws Exception {
		int number = 0;
		PersistenceManager pm = new PersistenceManager();
		
		MatricNumPrefix2 matric = (MatricNumPrefix2) pm.find(MatricNumPrefix2.class, prefix); 
		if ( matric != null ) {
			number = matric.getNumber();
			matric = (MatricNumPrefix2) pm.find(MatricNumPrefix2.class).whereId(prefix).forUpdate();
			matric.setNumber(number+1);
			pm.update();
		}
		else {
			matric = new MatricNumPrefix2(prefix);
			matric.setNumber(1);
			PersistenceManager.add(matric);
			
		}
		return number;
	}
	
	public static String getCurrentNumber(String prefix, int numSize) throws Exception {
		char[] cf = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',};
		return prefix + new java.text.DecimalFormat(new String(cf, 0, numSize)).format(getCurrentNumber(prefix));
	}
	
	public static String getCurrentNumber(String prefix, int numSize, String separator) throws Exception {
		char[] cf = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',};
		return prefix + separator + new java.text.DecimalFormat(new String(cf, 0, numSize)).format(getCurrentNumber(prefix));
	}	
	
	public static void main(String[] args) throws Exception{
		int numSize = 4;
//		String prefix = "M3010908";
//		String number = MatricNumberUtil.getCurrentNumber(prefix, numSize);
//		System.out.println(number);
//		number = MatricNumberUtil.getCurrentNumber(prefix, numSize);
//		System.out.println(number);
//		number = MatricNumberUtil.getCurrentNumber(prefix, numSize);
//		System.out.println(number);
//		number = MatricNumberUtil.getCurrentNumber(prefix, numSize);
//		System.out.println(number);
//		number = MatricNumberUtil.getCurrentNumber("ioioio", numSize);
//		System.out.println(number);		
		
		String number = MatricNumberUtil.getCurrentNumber("CONVOCATION-", numSize);
		System.out.println(number);
	}
	
	

}
