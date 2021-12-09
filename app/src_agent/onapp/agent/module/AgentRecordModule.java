/**
 * 
 */
package onapp.agent.module;

import java.util.HashMap;
import java.util.Map;

import lebah.portal.action.Command;
import lebah.template.LebahRecordTemplateModule;
import onapp.agent.entity.OnappAgent;
import onapp.agent.entity.OnappContact;
import onapp.entity.OnappCity;
import onapp.entity.OnappCountry;
import onapp.entity.OnappState;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class AgentRecordModule extends LebahRecordTemplateModule<OnappAgent> {

	
	@Override
	public Class getIdType() {
		return String.class;
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#afterSave(java.lang.Object)
	 */
	@Override
	public void afterSave(OnappAgent agent) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#beforeSave()
	 */
	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#begin()
	 */
	@Override
	public void begin() {
		context.put("countries", db.list("select c from OnappCountry c order by c.name"));
		context.put("states", db.list("select s from OnappState s order by s.name"));		
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#delete(java.lang.Object)
	 */
	@Override
	public boolean delete(OnappAgent agent) throws Exception {
		return true;
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#getPath()
	 */
	@Override
	public String getPath() {
		return "agent/agentRecordModule";
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#getPersistenceClass()
	 */
	@Override
	public Class<OnappAgent> getPersistenceClass() {
		return OnappAgent.class;
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#getRelatedData(java.lang.Object)
	 */
	@Override
	public void getRelatedData(OnappAgent agent) {
		String stateId = agent.getState() != null ? agent.getState().getCode() : null;
		if ( stateId != null)
			context.put("cities", db.list("select c from OnappCity c where c.state.code = '" + stateId + "' order by c.name"));

	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#save(java.lang.Object)
	 */
	@Override
	public void save(OnappAgent agent) throws Exception {
		agent.setName(getParam("name"));
		agent.setTelephoneNo(getParam("telephoneNo"));
		agent.setEmail(getParam("email"));
		
		String countryId = getParam("countryId");
		OnappCountry country = db.find(OnappCountry.class, countryId);
		String stateId = getParam("stateId");
		OnappState state = db.find(OnappState.class, stateId);
		String cityId = getParam("cityId");
		OnappCity city = db.find(OnappCity.class, cityId);
		
		agent.setCountry(country);
		agent.setState(state);
		agent.setCity(city);
		
		context.put("cities", db.list("select c from OnappCity c where c.state.code = '" + stateId + "' order by c.name"));
		
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#searchCriteria()
	 */
	@Override
	public Map<String, Object> searchCriteria() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", getParam("find_name"));
		return map;
	}
	
	@Command("listCities")
	public String listCities() {
		String stateId = getParam("stateId");
		context.put("cities", db.list("select c from OnappCity c where c.state.code = '" + stateId + "' order by c.name"));
		return getPath() + "/listCities.vm";
	}
	
	@Command("addContact")
	public String addContact() throws Exception {
		String agentId = getParam("agentId");
		OnappAgent agent = db.find(OnappAgent.class, agentId);
		context.put("r", agent);
		db.begin();
		OnappContact c = new OnappContact();
		c.setAgent(agent);
		c.setName(getParam("contactName"));
		c.setPhoneNo(getParam("contactPhoneNo"));
		agent.getContacts().add(c);
		db.commit();
		return getPath() + "/contactDetails.vm";
	}
	
	@Command("deleteContact")
	public String deleteContact() throws Exception {
		String agentId = getParam("agentId");
		OnappAgent agent = db.find(OnappAgent.class, agentId);
		context.put("r", agent);
		String contactId = getParam("contactId");
		OnappContact contact = db.find(OnappContact.class, contactId);
		db.begin();
		agent.getContacts().remove(contact);
		db.remove(contact);
		db.commit();
		return getPath() + "/contactDetails.vm";
	}
	
	@Command("getContact")
	public String getContact() throws Exception {
		String contactId = getParam("contactId");
		OnappContact contact = db.find(OnappContact.class, contactId);
		context.put("contact", contact);
		return getPath() + "/getContact.vm";
	}
	
	@Command("saveContact")
	public String saveContact() throws Exception {
		String contactId = getParam("contactId");
		OnappContact contact = db.find(OnappContact.class, contactId);
		context.put("r", contact.getAgent());
		db.begin();
		contact.setName(getParam("contact_name"));
		contact.setPhoneNo(getParam("contact_phoneNo"));
		db.commit();
		return getPath() + "/contactDetails.vm";
	}

}
