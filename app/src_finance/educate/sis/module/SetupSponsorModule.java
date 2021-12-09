package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.finance.entity.Sponsor;
import lebah.portal.action.AjaxModule;

public class SetupSponsorModule extends AjaxModule {
	
	String path = "apps/sponsor/";
	private String vm = "start.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	@Override
	public String doAction() throws Exception {
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.put("util", new lebah.util.Util());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "add_sponsor".equals(command)) addSponsor();
		else if ( "update_sponsor".equals(command)) updateSponsor();
		else if ( "delete_sponsor".equals(command)) deleteSponsor();
		return vm;
	}

	private void deleteSponsor() throws Exception {
		String id = request.getParameter("sponsor_id");
		
		//check if this sponsor has relation to any student
		if ( db.list("select ss from SponsorStudent ss where ss.sponsor.id = '" + id + "'").size() > 0 ) {
			//contains student(s) so cannot remove
		} else {
			Sponsor sponsor = db.find(Sponsor.class, id);
			db.begin();
			db.remove(sponsor);
			db.commit();
		}
		
		List<Sponsor> sponsors = db.list("select s from educate.sis.finance.entity.Sponsor s");
		context.put("sponsors", sponsors);
		
		vm = path + "list_sponsors.vm";
		
	}

	private void updateSponsor() throws Exception {
		String id = request.getParameter("sponsor_id");
		String code = request.getParameter("code_" + id);
		String name = request.getParameter("name_" + id);
		String address = request.getParameter("address_" + id);
		
		if ( !"".equals(code.trim()) && !"".equals(name.trim())) {
		
			Sponsor sponsor = db.find(Sponsor.class, id);
			
			db.begin();
			sponsor.setCode(code);
			sponsor.setName(name);
			sponsor.setAddress(address);
			db.commit();
			
			List<Sponsor> sponsors = db.list("select s from educate.sis.finance.entity.Sponsor s");
			context.put("sponsors", sponsors);
			
			vm = path + "empty.vm";
		
		}
		
	}

	private void addSponsor() throws Exception {
		// 
		String code = request.getParameter("sponsor_code");
		String name = request.getParameter("sponsor_name");
		String address = request.getParameter("sponsor_address");
		
		if ( !"".equals(code.trim()) && !"".equals(name.trim())) {
		
			Sponsor sponsor = new Sponsor();
			sponsor.setCode(code);
			sponsor.setName(name);
			sponsor.setAddress(address);
			
			db.begin();
			db.persist(sponsor);
			db.commit();
			
			List<Sponsor> sponsors = db.list("select s from educate.sis.finance.entity.Sponsor s");
			context.put("sponsors", sponsors);
			
			vm = path + "list_sponsors.vm";
		
		}
		
	}

	private void start() {
		//get list of sponsors
		List<Sponsor> sponsors = db.list("select s from educate.sis.finance.entity.Sponsor s");
		context.put("sponsors", sponsors);
		
		vm = path + "start.vm";
	}

}
