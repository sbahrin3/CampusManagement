package educate.alumni.module;

import educate.alumni.entity.AlumniMember;

public class AlumniMembershipEditModule extends AlumniMembershipFormModule {
	
	public void preProcess() {
		String login = request.getSession().getAttribute("_portal_login") != null ?
				(String) request.getSession().getAttribute("_portal_login"): "";
		AlumniMember member = (AlumniMember) db.get("select m from AlumniMember m where m.userId = '" + login + "'");
		context.put("member", member);
	}

}
