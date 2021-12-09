package migration.module;

import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ProgramStructureMigrationModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "migration/program_structure";

	@Override
	public String start() {
		try {
			List<String> programCodes = ProgramStructureMigration.getProgramCodes();
			context.put("programCodes", programCodes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path + "/start.vm";
	}
	
	@Command("migrate")
	public String migrate() throws Exception {
		String programCode = getParam("programCode");
		ProgramStructureMigration.migrate(db, programCode);
		
		Map<String, Object> m = ProgramStructureMigration.getProgramAndIntake(db, programCode);
		Program program = (Program) m.get("program");
		Session intake = (Session) m.get("intake");
		
		context.put("program", program);
		context.put("intake", intake);
		
		return path + "/migrate.vm";
	}

}
