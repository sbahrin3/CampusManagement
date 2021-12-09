package test;

import lebah.db.*;
import java.util.*;
import educate.enrollment.entity.*;
import educate.sis.struct.entity.*;

public class StudentStatusTest {
	
	public static void main(String[] args) throws Exception {
		
		PersistenceManager pm = new PersistenceManager();
		pm.list("select s from Student where s.matricNo = '123'");
		
		
		
	}

}
