package demo;

import java.util.HashMap;
import java.util.Map;

import lebah.template.LebahRecordTemplateModule;

public class DemoRecordModule extends LebahRecordTemplateModule<Customer> {

	@Override
	public Class getIdType() {
		return String.class;
	}

	@Override
	public void afterSave(Customer arg0) {
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
	public boolean delete(Customer arg0) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPath() {
		return "demo";
	}

	@Override
	public Class<Customer> getPersistenceClass() {
		return Customer.class;
	}

	@Override
	public void getRelatedData(Customer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(Customer customer) throws Exception {
		customer.setName(getParam("name"));
		customer.setAddress(getParam("address"));
	}

	@Override
	public Map<String, Object> searchCriteria() throws Exception {
		Map map = new HashMap();
		map.put("name", getParam("findName"));
		map.put("address", getParam("address"));
		return map;
	}

}
