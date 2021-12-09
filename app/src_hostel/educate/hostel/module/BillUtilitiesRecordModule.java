package educate.hostel.module;

import java.util.HashMap;
import java.util.Map;

import educate.hostel.entity.BillUtility;
import lebah.template.LebahRecordTemplateModule;
import lebah.template.OperatorEqualTo;

public class BillUtilitiesRecordModule  extends LebahRecordTemplateModule<BillUtility> {

	@Override
	public Class getIdType() {
		return String.class;
	}

	@Override
	public void afterSave(BillUtility bu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void begin() {
		context.put("path", getPath());
		context.put("hostels", db.list("select h from Hostel h"));
	}

	@Override
	public boolean delete(BillUtility bu) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPath() {
		return "apps/hostel_utilities_record";
	}

	@Override
	public Class<BillUtility> getPersistenceClass() {
		return BillUtility.class;
	}

	@Override
	public void getRelatedData(BillUtility bu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(BillUtility bu) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Object> searchCriteria() throws Exception {
		Map map = new HashMap();
		map.put("type.id", new OperatorEqualTo(getParam("findTypeId")));
		map.put("unit.id", new OperatorEqualTo(getParam("findUnitId")));
		return map;
	}

}
