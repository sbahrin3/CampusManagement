package test;

import java.util.List;

import educate.db.DbPersistence;
import onapp.entity.OnappProgram;

public class ApplicantRunningNumQuery {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		List<OnappProgram> programs = db.list("select p from OnappProgram p order by p.code");
		for ( OnappProgram p : programs ) {
			String code = p.getCode();
			String conditionalOfferRef = p.getConditionalOfferRef().replace("{YY}", "13");
			String offerRef = p.getOfferRef().replace("{YY}", "13");
			
			
			Integer refCount1 = (Integer) db.get("select n.counter from OnappMatricNumPrefix n where n.id = '" + conditionalOfferRef + "'");
			Integer refCount2 = (Integer) db.get("select n.counter from OnappMatricNumPrefix n where n.id = '" + offerRef + "'");
			
			System.out.println(code + ", " + offerRef + " = " + refCount2 + ", " + conditionalOfferRef + " = " + refCount1);
			
		}
		//List<OnappMatricNumPrefix> nums = db.list("select o from OnappMatricNumPrefix o");
		
		
	}

}
