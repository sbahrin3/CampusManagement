package educate.alumni.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import educate.alumni.entity.AlumniMember;
import educate.alumni.entity.AlumniSecretariat;
import educate.alumni.entity.SecretariatPosition;
import educate.db.DbPersistence;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class AlumniSecretariatModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "alumni/secretariat";
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
	}

	@Override
	public String start() {
		context.put("secretariats", db.list("select s from AlumniSecretariat s order by s.position.sequence"));
		return path + "/start.vm";
	}
	
	@Command("addSecretariat")
	public String addSecretariat() throws Exception {
		return path + "/addSecretariat.vm";
	}
	
	@Command("listMembers")
	public String listMembers() throws Exception {
		String findName = getParam("findName");
		List<AlumniMember> members = db.list("select m from AlumniMember m where m.fullName like '%" + findName + "%'");
		context.put("members", members);
		return path + "/listMembers.vm";
	}
	
	@Command("addNewSecretariat")
	public String addNewSecretariat() throws Exception {
		String memberId = getParam("memberId");
		AlumniMember member = db.find(AlumniMember.class, memberId);
		context.put("member", member);
		context.put("positions", db.list("select p from SecretariatPosition p order by p.sequence"));
		return path + "/addNewSecretariat.vm";
	}
	
	@Command("addSecretariatMember")
	public String addSecretariatMember() throws Exception {
		String memberId = getParam("memberId");
		AlumniMember member = db.find(AlumniMember.class, memberId);
		String positionId = getParam("positionId");
		SecretariatPosition position = db.find(SecretariatPosition.class, positionId);
		Date startDate = null, endDate = null;
		try {
			startDate = new SimpleDateFormat("dd-MM-yyyy").parse(getParam("startDate"));
		} catch ( Exception e ) { }
		try {
			endDate = new SimpleDateFormat("dd-MM-yyyy").parse(getParam("endDate"));
		} catch ( Exception e ) { }
		
		db.begin();
		AlumniSecretariat secretariat = new AlumniSecretariat();
		secretariat.setMember(member);
		secretariat.setPosition(position);
		secretariat.setStartDate(startDate);
		secretariat.setEndDate(endDate);
		db.persist(secretariat);
		db.commit();
		
		context.put("secretariats", db.list("select s from AlumniSecretariat s order by s.position.sequence"));
		return path + "/secretariat.vm";
	}
	
	@Command("deleteSecretariat")
	public String deleteSecretariat() throws Exception {
		String secretariatId = getParam("secretariatId");
		AlumniSecretariat secretariat = db.find(AlumniSecretariat.class, secretariatId);
		db.begin();
		db.remove(secretariat);
		db.commit();
		
		context.put("secretariats", db.list("select s from AlumniSecretariat s order by s.position.sequence"));
		return path + "/secretariat.vm";
	}

}
