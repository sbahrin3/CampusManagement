package test;

import lebah.db.*;
import educate.sis.struct.entity.*;
import java.util.*;

public class Subjects {
	
	public static void main(String[] args) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		List<Subject> subjects = pm.list("select s from Subject s");
		for ( Subject subject : subjects ) {
			System.out.println(subject.getId());
		}
		
		Subject s1 = (Subject) pm.find(Subject.class, "1217175768590");
		Subject s2 = (Subject) pm.find(Subject.class, "1217175768590");
		
		System.out.println(s2.equals(s1));
		
	}

}
