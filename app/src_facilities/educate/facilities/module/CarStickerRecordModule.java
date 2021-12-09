package educate.facilities.module;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.facilities.entity.CarSticker;
import educate.facilities.entity.OffenceHistory;
import lebah.portal.action.Command;
import lebah.template.LebahRecordTemplateModule;

public class CarStickerRecordModule  extends LebahRecordTemplateModule<CarSticker> {

	@Override
	public Class getIdType() {
		return String.class;
	}

	@Override
	public void afterSave(CarSticker cs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void begin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean delete(CarSticker cs) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getPath() {
		return "facilities/car_sticker";
	}

	@Override
	public Class<CarSticker> getPersistenceClass() {
		return CarSticker.class;
	}

	@Override
	public void getRelatedData(CarSticker cs) {
		String carStickerId = cs.getId();
		List<OffenceHistory> offences = db.list("select h from OffenceHistory h where h.carSticker.id = '" + carStickerId + "' order by h.date desc");
		context.put("offences", offences);
		
	}

	@Override
	public void save(CarSticker cs) throws Exception {
		cs.setCarDetail(getParam("carDetail"));
		cs.setOwnerId(getParam("ownerId"));
		cs.setOwnerType(getParam("ownerType"));
		cs.setOwnerName(getParam("ownerName"));
		cs.setPlateNo(getParam("plateNo"));
		cs.setCarDetail(getParam("carDetail"));
		cs.setOwnerType(getParam("ownerType"));
	}

	@Override
	public Map<String, Object> searchCriteria() throws Exception {
		Map m = new HashMap();
		m.put("ownerId", getParam("findOwnerId"));
		m.put("ownerName", getParam("findOwnerName"));
		m.put("plateNo", getParam("findPlateNo"));
		return m;
	}

	@Command("offenceHistory")
	public String offenceHistory() throws Exception {
		String carStickerId = getParam("carStickerId");
		context.put("carStickerId", carStickerId);
		CarSticker cs = db.find(CarSticker.class, carStickerId);
		List<OffenceHistory> offences = db.list("select h from OffenceHistory h where h.carSticker.id = '" + carStickerId + "' order by h.date desc");
		context.put("offences", offences);
		return getPath() + "/offences/offenceHistory.vm";
	}
	
	@Command("getOffence")
	public String getOffence() throws Exception {
		String carStickerId = getParam("carStickerId");
		context.put("carStickerId", carStickerId);
		String offenceId = getParam("offenceId");
		OffenceHistory offence = db.find(OffenceHistory.class, offenceId);
		context.put("offence", offence);
		return getPath() + "/offences/getOffence.vm";
	}
	
	@Command("saveOffence")
	public String saveOffence() throws Exception {
		String carStickerId = getParam("carStickerId");
		context.put("carStickerId", carStickerId);
		CarSticker cs = db.find(CarSticker.class, carStickerId);
		
		String offenceId = getParam("offenceId");
		OffenceHistory offence = null;
		if ( !"".equals(offenceId)) 
			offence = db.find(OffenceHistory.class, offenceId);
		else
			offence = new OffenceHistory();
		
		String _date = getParam("offenceDate");
		Date date = new Date();
		try {
			date = new SimpleDateFormat("dd-MM-yyyy").parse(_date);
		} catch ( Exception e ) { }

		db.begin();
		offence.setAction(getParam("action"));
		offence.setDescription(getParam("description"));
		offence.setDate(date);
		offence.setCarSticker(cs);
		if ( "".equals(offenceId)) db.persist(offence);
		db.commit();
		
		List<OffenceHistory> offences = db.list("select h from OffenceHistory h where h.carSticker.id = '" + carStickerId + "' order by h.date desc");
		context.put("offences", offences);
		return getPath() + "/offences/offenceHistory.vm";
	}
	
	@Command("deleteOffence")
	public String deleteOffence() throws Exception {
		String offenceId = getParam("offenceId");
		OffenceHistory offence = db.find(OffenceHistory.class, offenceId);
		db.begin();
		db.remove(offence);
		db.commit();
		
		String carStickerId = getParam("carStickerId");
		context.put("carStickerId", carStickerId);
		CarSticker cs = db.find(CarSticker.class, carStickerId);
		List<OffenceHistory> offences = db.list("select h from OffenceHistory h where h.carSticker.id = '" + carStickerId + "' order by h.date desc");
		context.put("offences", offences);
		
		return getPath() + "/offences/offenceHistory.vm";
	}
	
}
