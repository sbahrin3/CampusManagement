package my.test;

import java.util.List;

import educate.db.DbPersistence;
import educate.sis.finance.entity.Payment;

public class MakeDummyStudents {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();

		/*
		List<Student> students = db.list("select s from Student s order by s.biodata.name");
		int i = 0;
		for ( Student s : students ) {
			i++;
			System.out.println(i + ") " + s.getBiodata().getName() + ", " + s.getMatricNo());

		}
		*/
		
		List<Payment> payments = db.list("select p from Payment p");
		int i = 0;
		for ( Payment p : payments ) {
			i++;
			String no = "P" + lebah.db.UniqueID.getUID().substring(6);
			System.out.println(i + ") " + p.getPaymentNo() + " - " + no);
			
			p.setPaymentNo(no);
		}

		
		db.begin();
		db.commit();
	}
	

}
