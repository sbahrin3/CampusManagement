package educate.admission.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.admission.entity.OnappCentreMapper;
import educate.db.DbPersistence;
import educate.sis.struct.entity.LearningCentre;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import onapp.entity.OnappLearningCentre;

public class OnappCentreMapperModule extends LebahModule {
		
		private String path = "admission/mapper/centre";
		private DbPersistence db = new DbPersistence();
		
		public void preProcess() {
			context.put("path", path);
		}

		@Override
		public String start() {
			
			context.put("onappCentres", db.list("select p from OnappLearningCentre p order by p.sequence"));
			context.put("centres", db.list("select p from LearningCentre p order by p.name"));
			
			List<OnappCentreMapper> list = db.list("select m from OnappCentreMapper m");
			Map<String, String> mapper = new HashMap<String, String>();
			context.put("mapper", mapper);
			for ( OnappCentreMapper m : list ) {
				if ( m.getOnappCentre() != null && m.getCentre() != null )
					mapper.put(m.getOnappCentre().getId(), m.getCentre().getId());
			}
			
			return path + "/start.vm";
		}
		
		@Command("saveMapper")
		public String saveMapper() throws Exception {
			String onappCentreId = getParam("onappCentreId");
			String centreId = getParam("centre_" + onappCentreId);
			
			OnappLearningCentre onappCentre = db.find(OnappLearningCentre.class, onappCentreId);
			LearningCentre centre = db.find(LearningCentre.class, centreId);
			
			OnappCentreMapper m = (OnappCentreMapper) db.get("select m from OnappCentreMapper m where m.onappCentre.id = '" + onappCentreId + "'");
			if ( m == null ) {
				db.begin();
				m = new OnappCentreMapper();
				m.setOnappCentre(onappCentre);
				m.setCentre(centre);
				db.persist(m);
				db.commit();
			}
			else {
				db.begin();
				m.setCentre(centre);
				db.commit();
			}
			 
			return path + "/saved.vm";
		}
}
