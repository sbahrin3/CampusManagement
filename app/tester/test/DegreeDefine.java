package test;

import lebah.db.*;
import educate.sis.struct.entity.*;
import java.util.*;

public class DegreeDefine {
	
	public static void main(String[] args) throws Exception {
		
		DegreeScheme scheme1 = new DegreeScheme("scheme1");
		
		Degree firstClassDegree = new Degree("1stClass");
		firstClassDegree.setTitle("First Class");
		firstClassDegree.setHigh(4.0f);
		firstClassDegree.setLow(3.5f);
		
		Degree secondClassDegree = new Degree("2ndClass");
		secondClassDegree.setTitle("Second Class");
		secondClassDegree.setHigh(3.5f);
		secondClassDegree.setLow(3.0f);		
		
		scheme1.addDegree(firstClassDegree);
		scheme1.addDegree(secondClassDegree);
		
		DegreeScheme scheme2 = new DegreeScheme("scheme2");
		
		firstClassDegree = new Degree("1stClass");
		firstClassDegree.setTitle("First Class");
		firstClassDegree.setHigh(4.0f);
		firstClassDegree.setLow(3.0f);
		
		secondClassDegree = new Degree("2ndClass");
		secondClassDegree.setTitle("Second Class");
		secondClassDegree.setHigh(3.0f);
		secondClassDegree.setLow(2.0f);		
		
		scheme2.addDegree(firstClassDegree);
		scheme2.addDegree(secondClassDegree);
		

		
	}

}
