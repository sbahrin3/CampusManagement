package educate.admission.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.admission.entity.OnappIntakeMapper;
import educate.db.DbPersistence;
import educate.sis.struct.entity.Session;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import onapp.entity.OnappIntakeSession;

public class OnappIntakeMapperModule extends LebahModule {
	
	private String path = "admission/mapper/intake";
	private DbPersistence db = new DbPersistence();
	
	public void preProcess() {
		context.put("path", path);
	}

	@Override
	public String start() {
		
		context.put("onappIntakes", db.list("select p from OnappIntakeSession p order by p.sequence"));
		context.put("intakes", db.list("select p from Session p order by p.startDate DESC"));
		
		List<OnappIntakeMapper> list = db.list("select m from OnappIntakeMapper m");
		Map<String, String> mapper = new HashMap<String, String>();
		context.put("mapper", mapper);
		for ( OnappIntakeMapper m : list ) {
			if ( m.getOnappIntake() != null && m.getIntake() != null )
				mapper.put(m.getOnappIntake().getId(), m.getIntake().getId());
		}
		
		return path + "/start.vm";
	}
	
	@Command("saveMapper")
	public String saveMapper() throws Exception {
		String onappIntakeId = getParam("onappIntakeId");
		String intakeId = getParam("intake_" + onappIntakeId);
		
		OnappIntakeSession onappIntake = db.find(OnappIntakeSession.class, onappIntakeId);
		Session intake = db.find(Session.class, intakeId);
		
		OnappIntakeMapper m = (OnappIntakeMapper) db.get("select m from OnappIntakeMapper m where m.onappIntake.id = '" + onappIntakeId + "'");
		if ( m == null ) {
			db.begin();
			m = new OnappIntakeMapper();
			m.setOnappIntake(onappIntake);
			m.setIntake(intake);
			db.persist(m);
			db.commit();
		}
		else {
			db.begin();
			m.setIntake(intake);
			db.commit();
		}
		 
		return path + "/saved.vm";
	}


}
