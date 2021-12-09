package educate.sis.finance;

import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;

public class ListPeriodsOfProgram {
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		String programCode = "C301";
		Program program = (Program) db.get("select p from Program p where p.code = '" + programCode + "'");
		List<Period> periods = program.getPeriodScheme().getParentPeriodList();
		for ( Period p : periods ) {
			System.out.println(p.getName());
		}
		System.out.println("****");
		periods = program.getPeriodScheme().getAllPeriodList();
		for ( Period p : periods ) {
			System.out.println(p.getName());
		}

		
	}
	
	public static void listPrograms() throws Exception {
		DbPersistence db = new DbPersistence();
		List<Program> programs = db.list("select p from Program p order by p.code");
		for (Program p : programs ) {
			System.out.println(p.getCode() + ", " + p.getName());
		}
	}

}
