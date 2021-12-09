package test;

import educate.sis.struct.entity.*;
import lebah.db.*;
import java.util.*;

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
			pm.add(matric);
			
		}
		return number;
	}
	
	public static void main(String[] args) throws Exception{
		String prefix = "M3010908";
		int number = MatricNumberUtil.getCurrentNumber(prefix);
		System.out.println(number);
		number = MatricNumberUtil.getCurrentNumber(prefix);
		System.out.println(number);
		number = MatricNumberUtil.getCurrentNumber(prefix);
		System.out.println(number);
		number = MatricNumberUtil.getCurrentNumber(prefix);
		System.out.println(number);
		number = MatricNumberUtil.getCurrentNumber("ioioio");
		System.out.println(number);		
		
	}
	
	

}
