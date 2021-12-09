package test;

import java.util.*;
import lebah.db.*;
import educate.sis.struct.entity.*;

public class PeriodsGet {
	
	public static void main(String[] args) throws Exception {
		listFunctional();
		//listSchemes();
		
	}

	private static void listFunctional() throws Exception {
		
		PersistenceManager pm = new PersistenceManager();
		PeriodScheme scheme = (PeriodScheme) pm.find(PeriodScheme.class, "bit");
		System.out.println(scheme.getCode());
		

		//List<Period> list = scheme.getAllPeriodList();
		List<Period> list = scheme.getFunctionalPeriodList();
		for ( Iterator<Period> it = list.iterator(); it.hasNext(); ) {
			Period p = it.next();
			System.out.println(p.getId() + ", " + (p.getParent() != null ? p.getParent().getName() : "") + ", " + p.getName());
		}
	}
	
	private static void listSchemes() throws Exception {
		PersistenceManager pm = new PersistenceManager();
		List<PeriodScheme> list = pm.list("select ps from PeriodScheme ps");
		for ( int i=0; i < list.size(); i++ ) {
			PeriodScheme ps = list.get(i);
			System.out.println(ps.getCode() + ", " + ps.getId());
		}
		
	}

}
