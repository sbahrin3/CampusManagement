package test;

import java.util.*;
import lebah.db.*;
import educate.sis.struct.entity.*;

public class ProgramFees {
	
	public static void main(String[] args) throws Exception {
		listAll();

	}
	
	static void listAll() throws Exception {
		PersistenceManager pm = new PersistenceManager();
		List<ProgramFee> fees = pm.list("select f from ProgramFee f");
		System.out.println(fees.size());
		for ( ProgramFee fee : fees ) {
			System.out.println(fee.getId() + ", " + fee.getProgram().getName() + ", " + fee.getTotalPayment());
		}
	}
	
	static void deleteAll() throws Exception {
		PersistenceManager pm = new PersistenceManager();
		pm.executeUpdate("delete from ProgramFee f");
	}

}
