package educate.facilities.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.facilities.entity.Utility;
import educate.facilities.entity.UtilityType;
import lebah.template.LebahRecordTemplateModule;
import lebah.template.OperatorEqualTo;

public class UtilityRecordModule  extends LebahRecordTemplateModule<Utility> {

	@Override
	public Class getIdType() {
		return String.class;
	}

	@Override
	public void afterSave(Utility arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void begin() {
		context.put("path", getPath());
		this.setReadonly(false);
		List<UtilityType> utilityTypes = db.list("select c from UtilityType c order by c.sequence");
		context.put("utilityTypes", utilityTypes);
		
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
	}

	@Override
	public boolean delete(Utility a) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getPath() {
		return "facilities/utility_records";
	}

	@Override
	public Class<Utility> getPersistenceClass() {
		return Utility.class;
	}

	@Override
	public void getRelatedData(Utility arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(Utility a) throws Exception {
		String utilityTypeId = getParam("utilityTypeId");
		UtilityType utilityType = db.find(UtilityType.class, utilityTypeId);
		save(a, utilityType);
	}

	@Override
	public Map<String, Object> searchCriteria() throws Exception {
		Map map = new HashMap();
		map.put("type.id", new OperatorEqualTo(getParam("findUtilityTypeId")));
		map.put("accountNo", getParam("findAccountNo"));
		map.put("month", new OperatorEqualTo(getParam("findMonth")));
		map.put("year", new OperatorEqualTo(getParam("findYear")));
		return map;
	}
	
	private void save(Utility a, UtilityType utilityType) {
		a.setType(utilityType);
		a.setAccountNo(getParam("accountNo"));
		a.setMonth(Integer.parseInt(getParam("month")));
		a.setYear(Integer.parseInt(getParam("year")));
		try {
			a.setDate(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("date")));
		} catch ( Exception e ) { a.setDate(null); }
		try {
			a.setAmountConsumption(Double.parseDouble(getParam("amountConsumption")));
		} catch ( Exception e ) { a.setAmountConsumption(0.0d); }
		a.setUnitConsumption(getParam("unitConsumption"));
	}
	
}
