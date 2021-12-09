package test;

import educate.sis.struct.entity.*;
import lebah.db.*;
import java.util.*;

public class ListAllPeriod {
	
	public static void main(String[] args) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		PeriodScheme ps = (PeriodScheme) pm.find(PeriodScheme.class, "bit");
		System.out.println(ps.getCode());
		
		List<Period> list = ps.getFunctionalPeriodList();
		for ( Iterator<Period> it = list.iterator(); it.hasNext();) {
			Period p = it.next();
			System.out.println(p.getName());
		}
	}

}
