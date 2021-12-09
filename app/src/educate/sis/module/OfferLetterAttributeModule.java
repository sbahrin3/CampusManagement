package educate.sis.module;

import java.util.List;

import educate.admission.entity.OfferLetterAttribute;
import educate.db.DbPersistence;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class OfferLetterAttributeModule extends LebahModule {
	
	String path = "apps/util/offer_letter/";
	DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		List<OfferLetterAttribute> list = db.list("select a from OfferLetterAttribute a");
		if ( list.size() == 0 ) {
			db.begin();
			OfferLetterAttribute a = new OfferLetterAttribute();
			db.persist(a);
			try {
				db.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			context.put("attr", a);
		}
		else {
			context.put("attr", list.get(0));
		}
		return path + "start.vm";
	}
	
	@Command("save")
	public String save() throws Exception {
		String id = request.getParameter("attr_id");
		String date = request.getParameter("date");
		String time = request.getParameter("time");
		String location = request.getParameter("location");
		
		OfferLetterAttribute attr = db.find(OfferLetterAttribute.class, id);
		db.begin();
		attr.setDate(date);
		attr.setTime(time);
		attr.setLocation(location);
		db.commit();
		
		context.put("attr", attr);
		
		return path + "attr.vm";
	}

}
