package test;

import lebah.db.*;
import educate.sis.struct.entity.*;
import java.util.*;

public class ProgramStructureTest {
	
	public static void main(String[] args) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		
		Program p = (Program) pm.find(Program.class, "1216949296159");
		System.out.println(p.getName());
		
		List<Subject> subjects = pm.list("select s from Subject s");
		System.out.println(subjects.size());
		
		
	}

}
