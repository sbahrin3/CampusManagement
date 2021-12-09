package test;

import lebah.db.*;
import educate.sis.struct.entity.*;
import java.util.*;

public class Faculties {
	
	public static void main(String[] args) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		List<Faculty> faculties = pm.list("select f from Faculty f");
		System.out.println(faculties.size());
		for ( Faculty faculty : faculties ) {
			System.out.println(faculty.getId() + " - " + faculty.getCode() + ", " + faculty.getName());
		}
		
	}
	
	static void addFaculty(String code, String name) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		
	}

}
