package educate.alumni.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import educate.alumni.entity.AlumniActivityEvent;
import educate.alumni.entity.AlumniMember;
import educate.alumni.entity.EventFeedback;
import educate.db.DbPersistence;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class AlumniActivityEventListModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "alumni/event_list";
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("util", new lebah.util.Util());
		System.out.println(command);
		
		String login = request.getSession().getAttribute("_portal_login") != null ?
				(String) request.getSession().getAttribute("_portal_login"): "";
		AlumniMember member = (AlumniMember) db.get("select m from AlumniMember m where m.userId = '" + login + "'");
		context.put("member", member);
		String portalRole = (String) request.getSession().getAttribute("_portal_role");
		context.put("portalRole", portalRole);
		String portalUsername = (String) request.getSession().getAttribute("_portal_username");
		context.put("portalUsername", portalUsername);
	}

	@Override
	public String start() {
		List<AlumniActivityEvent> events = db.list("select e from AlumniActivityEvent e order by e.startDate desc");
		context.put("events", events);
		return path + "/start.vm";
	}
	
	@Command("addFeedback")
	public String addFeedback() throws Exception {
		String eventId = getParam("eventId");
		AlumniActivityEvent e = db.find(AlumniActivityEvent.class, eventId);
		context.put("e", e);
		String memberId = getParam("memberId");
		AlumniMember member = db.find(AlumniMember.class, memberId);
		String portalUsername = (String) request.getSession().getAttribute("_portal_username");
		String remark = getParam("remark_" + eventId);
		db.begin();
		EventFeedback feedback = new EventFeedback();
		feedback.setEvent(e);
		feedback.setMember(member);
		feedback.setPortalUsername(portalUsername);
		feedback.setRemark(remark);
		feedback.setDate(new Date());
		e.getFeedbacks().add(feedback);
		db.commit();
		return path + "/feedbackList.vm";
	}
	
	@Command("deleteFeedback")
	public String deleteFeedback() throws Exception {
		String feedbackId = getParam("feedbackId");
		EventFeedback feedback = db.find(EventFeedback.class, feedbackId);
		AlumniActivityEvent e = feedback.getEvent();
		context.put("e", e);
		db.begin();
		e.getFeedbacks().remove(feedback);
		db.commit();
		return path + "/feedbackList.vm";
	}	

}
