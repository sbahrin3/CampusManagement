package educate.admission.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.admission.entity.OnappProgramMapper;
import educate.db.DbPersistence;
import educate.sis.struct.entity.Program;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import onapp.entity.OnappProgram;

public class OnappProgramMapperModule extends LebahModule {
	
	private String path = "admission/mapper/program";
	private DbPersistence db = new DbPersistence();
	
	public void preProcess() {
		context.put("path", path);
	}

	@Override
	public String start() {
		
		context.put("onappPrograms", db.list("select p from OnappProgram p order by p.code"));
		context.put("programs", db.list("select p from Program p order by p.code"));
		
		List<OnappProgramMapper> list = db.list("select m from OnappProgramMapper m");
		Map<String, String> mapper = new HashMap<String, String>();
		context.put("mapper", mapper);
		for ( OnappProgramMapper m : list ) {
			if ( m.getOnappProgram() != null && m.getProgram() != null )
				mapper.put(m.getOnappProgram().getId(), m.getProgram().getId());
		}
		
		return path + "/start.vm";
	}
	
	@Command("saveMapper")
	public String saveMapper() throws Exception {
		String onappProgramId = getParam("onappProgramId");
		String programId = getParam("program_" + onappProgramId);
		
		OnappProgram onappProgram = db.find(OnappProgram.class, onappProgramId);
		Program program = db.find(Program.class, programId);
		
		OnappProgramMapper m = (OnappProgramMapper) db.get("select m from OnappProgramMapper m where m.onappProgram.id = '" + onappProgramId + "'");
		if ( m == null ) {
			db.begin();
			m = new OnappProgramMapper();
			m.setOnappProgram(onappProgram);
			m.setProgram(program);
			db.persist(m);
			db.commit();
		}
		else {
			db.begin();
			m.setProgram(program);
			db.commit();
		}
		 
		return path + "/saved.vm";
	}

}
