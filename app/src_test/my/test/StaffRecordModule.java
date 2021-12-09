package my.test;

import java.util.HashMap;
import java.util.Map;

import lebah.template.LebahRecordTemplateModule;

public class StaffRecordModule extends LebahRecordTemplateModule<Staff> {

	@Override
	public Class getIdType() {
		return String.class;
	}

	@Override
	public void afterSave(Staff arg0) {
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
	public boolean delete(Staff arg0) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPath() {
		return "staff";
	}

	@Override
	public Class<Staff> getPersistenceClass() {
		return Staff.class;
	}

	@Override
	public void getRelatedData(Staff arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(Staff s) throws Exception {
		String icno = get("icno");
		String name = get("name");
		String address = get("address");
		
		s.setIcno(icno);
		s.setName(name);
		s.setAddress(address);
		
	}

	@Override
	public Map<String, Object> searchCriteria() throws Exception {
		Map map = new HashMap();
		map.put("icno", get("find_icno"));
		map.put("name", get("find_name"));
		return map;
	}

}
