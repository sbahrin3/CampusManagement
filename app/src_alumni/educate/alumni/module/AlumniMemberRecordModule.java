package educate.alumni.module;

import java.util.HashMap;
import java.util.Map;

import educate.alumni.entity.AlumniMember;
import lebah.portal.action.Command;
import lebah.template.LebahRecordTemplateModule;
import lebah.template.OperatorEqualTo;

public class AlumniMemberRecordModule extends LebahRecordTemplateModule<AlumniMember> {

	@Override
	public Class getIdType() {
		return String.class;
	}

	@Override
	public void afterSave(AlumniMember arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void begin() {
		setReadonly(true);
	}

	@Override
	public boolean delete(AlumniMember m) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPath() {
		return "alumni/member_records";
	}

	@Override
	public Class<AlumniMember> getPersistenceClass() {
		return AlumniMember.class;
	}

	@Override
	public void getRelatedData(AlumniMember m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(AlumniMember m) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Object> searchCriteria() throws Exception {
		Map m = new HashMap();
		m.put("fullName", getParam("findName"));
		m.put("status", new OperatorEqualTo(getParam("findStatus")));
		return m;
	}
	
	@Command("updateStatus")
	public String updateStatus() throws Exception {
		String memberId = getParam("memberId");
		AlumniMember member = db.find(AlumniMember.class, memberId);
		String status = getParam("membershipStatus");
		db.begin();
		member.setStatus(status);
		db.commit();
		context.put("member", member);
		return getPath() + "/updateStatus.vm";
	}

}
