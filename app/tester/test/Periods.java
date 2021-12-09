package test;

import java.util.*;
import lebah.db.*;
import educate.sis.struct.entity.*;

public class Periods {
	
	public static void main(String[] args) throws Exception {
		
		init();
		
	}
	
	static void init() throws Exception {
		PersistenceManager pm = new PersistenceManager();
		pm.executeUpdate("delete from Period p");
		pm.executeUpdate("delete from PeriodScheme ps");
		

		PeriodScheme ps = new PeriodScheme("bit", "BIT");
		
		Period y1 = new Period("bit-y1", "YEAR 1");
		Period y2 = new Period("bit-y2", "YEAR 2");
		
		ps.addPeriod(y1);
		ps.addPeriod(y2);
		
		y1.addChild(new Period("bit-y1-s1", "SEMESTER 1"));
		y1.addChild(new Period("bit-y1-s2", "SEMESTER 2"));
		
		y2.addChild(new Period("bit-y2-s1", "SEMESTER 1"));
		y2.addChild(new Period("bit-y2-s2", "SEMESTER 2"));
		
		pm.add(ps);

	}

}
