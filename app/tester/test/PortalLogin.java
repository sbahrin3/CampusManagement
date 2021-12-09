package test;

import java.util.*;
import lebah.db.*;
import educate.sis.struct.entity.*;
import educate.enrollment.entity.*;
import educate.enrollment.*;

public class PortalLogin {
	
	public static void main(String[] args) throws Exception {
		
		String matricNo = "BIT-2008-2-0005";
		
		PersistenceManager pm = new PersistenceManager();
		List<Student> list = pm.list("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( list.size() == 0 ) throw new Exception("not found");
		StudentRegistrationUtil.createPortalLogin(list.get(0));
		
	}

}
