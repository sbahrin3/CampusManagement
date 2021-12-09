package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.StudentAccomodation;
import educate.sis.struct.entity.StudentAccomodationStatus;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentAccomodationSetupModule extends LebahModule {
	
	private String path = "apps/util/accomodation/";
	private DbPersistence db = new DbPersistence();
	
	
	@Override
	public String start() {
		try {
			List<StudentAccomodation> accomodations = db.list("select a from StudentAccomodation a order by a.name");
			context.put("accomodations", accomodations);
		} catch ( Exception e ) {
			
		}
		return path + "start.vm";
	}
	
	@Override
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
	}
	
	@Command("add")
	public String add() throws Exception {
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		int quotaMale = 0, quotaFemale = 0, months = 0;
		try {
			quotaMale = Integer.parseInt(request.getParameter("quota_male"));
		} catch ( Exception e ) {}
		try {
			quotaFemale = Integer.parseInt(request.getParameter("quota_female"));
		} catch ( Exception e ) {}
		double fee = 0.0d;
		try {
			fee = Double.parseDouble(request.getParameter("fee"));
		} catch ( Exception e) {}
		try {
			months = Integer.parseInt(request.getParameter("months"));
		} catch ( Exception e ) {}
		
		
		db.begin();
		StudentAccomodation a = new StudentAccomodation();
		a.setCode(code);
		a.setName(name);
		a.setQuotaFemale(quotaFemale);
		a.setQuotaMale(quotaMale);
		db.persist(a);
		db.commit();
		
		List<StudentAccomodation> accomodations = db.list("select a from StudentAccomodation a order by a.name");
		context.put("accomodations", accomodations);
		
		return path + "accomodations.vm";
	}
	
	@Command("update_quota_female")
	public String updateQuotaFemale() throws Exception {
		String accomodationId = request.getParameter("accomodation_id");
		StudentAccomodation accomodation = db.find(StudentAccomodation.class, accomodationId);
		int quotaFemale = 0;
		try {
			quotaFemale = Integer.parseInt(request.getParameter("quota_female_" + accomodationId));
			db.begin();
			accomodation.setQuotaFemale(quotaFemale);
			db.commit();
		} catch ( Exception e) {}
		return path + "empty.vm";
	}
	
	@Command("update_quota_male")
	public String updateQuotaMale() throws Exception {
		String accomodationId = request.getParameter("accomodation_id");
		StudentAccomodation accomodation = db.find(StudentAccomodation.class, accomodationId);
		int quotaMale = 0;
		try {
			quotaMale = Integer.parseInt(request.getParameter("quota_male_" + accomodationId));
			db.begin();
			accomodation.setQuotaMale(quotaMale);
			db.commit();
		} catch ( Exception e) {}
		return path + "empty.vm";
	}
	
	@Command("update_fee")
	public String updateFee() throws Exception {
		String accomodationId = request.getParameter("accomodation_id");
		StudentAccomodation accomodation = db.find(StudentAccomodation.class, accomodationId);
		double fee = 0;
		try {
			fee = Double.parseDouble(request.getParameter("fee_" + accomodationId));
			db.begin();
			accomodation.setMonthlyFee(fee);
			db.commit();
		} catch ( Exception e) {}
		return path + "empty.vm";
	}
	
	@Command("delete")
	public String deleteAccomodation() throws Exception {
		
		String accomodationId = request.getParameter("accomodation_id");
		StudentAccomodation accomodation = db.find(StudentAccomodation.class, accomodationId);
		
		List<StudentAccomodationStatus> list = db.list("select s from StudentAccomodationStatus s where s.accomodation.id = '" + accomodation.getId() + "'");
		if ( list.size() == 0 ) {
			db.begin();
			db.remove(accomodation);
			db.commit();
		} else {
			System.out.println("Can't delete " + accomodation.getName());
		}
		
		List<StudentAccomodation> accomodations = db.list("select a from StudentAccomodation a order by a.name");
		context.put("accomodations", accomodations);
		
		return path + "accomodations.vm";
	}
	
	@Command("update_code")
	public String updateCode() throws Exception {
		String accomodationId = request.getParameter("accomodation_id");
		StudentAccomodation accomodation = db.find(StudentAccomodation.class, accomodationId);
		String code = request.getParameter("code_" + accomodationId);
		db.begin();
		accomodation.setCode(code);
		db.commit();
		return path + "empty.vm";
	}
	
	@Command("update_name")
	public String updateName() throws Exception {
		String accomodationId = request.getParameter("accomodation_id");
		StudentAccomodation accomodation = db.find(StudentAccomodation.class, accomodationId);
		String name = request.getParameter("name_" + accomodationId);
		db.begin();
		accomodation.setName(name);
		db.commit();
		return path + "empty.vm";
	}

	
	@Command("update_months")
	public String updateMonths() throws Exception {
		String accomodationId = request.getParameter("accomodation_id");
		StudentAccomodation accomodation = db.find(StudentAccomodation.class, accomodationId);
		int months = 0;
		try {
			months = Integer.parseInt(request.getParameter("months_" + accomodationId));
		} catch ( Exception e ) {}
		db.begin();
		accomodation.setMonths(months);
		db.commit();
		return path + "empty.vm";
	}

}
