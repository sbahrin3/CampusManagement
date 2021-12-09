package test;

import lebah.db.*;
import educate.sis.struct.entity.*;
import java.util.*;

public class IntakeTest {
	
	public static void main(String[] args) throws Exception {
		listIntakes();
	}

	private static void listIntakes() throws Exception {
		PersistenceManager pm = new PersistenceManager();
		List<Intake> intakes = pm.list("select i from Intake i");
		System.out.println(intakes.size());
	}
	
	private static void addIntake() throws Exception {
		Intake intake = new Intake();
		
		
	}

}
