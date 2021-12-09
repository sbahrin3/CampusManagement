package educate.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import lebah.portal.action.LebahModule;
import onapp.entity.OnappProgram;

public class QueryRunningNumberModule extends LebahModule {
	
	DbPersistence db = new DbPersistence();
	String path = "utils";

	@Override
	public String start() {
		List<Map> refList = new ArrayList<Map>();
		context.put("refList", refList);
		List<OnappProgram> programs = db.list("select p from OnappProgram p order by p.code");
		for ( OnappProgram p : programs ) {
			String code = p.getCode();
			String conditionalOfferRef = p.getConditionalOfferRef().replace("{YY}", "13");
			String offerRef = p.getOfferRef().replace("{YY}", "13");
			
			Integer refCount1 = (Integer) db.get("select n.counter from OnappMatricNumPrefix n where n.id = '" + conditionalOfferRef + "'");
			Integer refCount2 = (Integer) db.get("select n.counter from OnappMatricNumPrefix n where n.id = '" + offerRef + "'");
			
			Map ref = new HashMap<String, String>();
			ref.put("programCode", code);
			ref.put("conditionalOfferRef", conditionalOfferRef);
			ref.put("offerRef", offerRef);
			ref.put("countConditionalOfferRef", refCount1);
			ref.put("countOfferRef", refCount2);
			refList.add(ref);
			
		}
		return path + "/runningNumbers.vm";
	}

}
