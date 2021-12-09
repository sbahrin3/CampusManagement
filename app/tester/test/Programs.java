package test;

import java.util.*;
import lebah.db.*;
import educate.sis.struct.entity.*;

public class Programs {
	
	//1216949296159

	public static void main(String[] args) throws Exception {
		createPrograms();
		listPrograms();
	}

	private static void listPrograms() throws Exception {
		PersistenceManager pm = new PersistenceManager();
		List<Program> programs = pm.list("select p from Program p");
		System.out.println(programs.size());
		for ( Program p : programs ) {
			System.out.println(p.getId() + ", " + p.getCode() + " = " + p.getName() + ", " + p.getPeriodScheme().getCode());
		}
	}
	
	static void createPrograms() throws Exception {
		PersistenceManager pm = new PersistenceManager();
		pm.executeUpdate("delete from Program p");
		
		Program program = new Program();
		program.setCode("BIT");
		program.setCourse(Course.findByCode("IT"));
		program.setPeriodScheme(PeriodScheme.findByCode("bit"));
		program.setName("Bachelor of Information Technology");
		program.setAbbrev("BIT");
		program.setRegisterNo("KKNN123");
		
		pm.add(program);
		
	}
}
