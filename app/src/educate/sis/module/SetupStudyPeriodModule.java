package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.PeriodScheme;
import lebah.portal.action.AjaxModule;

public class SetupStudyPeriodModule extends AjaxModule {
	
	String path = "apps/util/setup_study_period/";
	private String vm = "default.vm";
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		context.remove("period_added");
		context.remove("child_added");
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "list_periods".equals(command)) listPeriods();
		else if ( "add_new".equals(command)) addNew();
		else if ( "add_period".equals(command)) addPeriod();
		else if ( "add_child".equals(command)) addChild();
		else if ( "add_period_scheme".equals(command)) addPeriodScheme();
		else if ( "save_period_name".equals(command)) savePeriodName();
		else if ( "delete_period".equals(command)) deletePeriod();
		else if ( "delete_scheme".equals(command)) deletePeriodScheme();
		else if ( "update_scheme_code".equals(command)) updateSchemeCode();
		return vm;
	}

	private void deletePeriodScheme() throws Exception {
		context.remove("errorMessage");
		String schemeId = request.getParameter("period_scheme_id");
		PeriodScheme periodScheme = db.find(PeriodScheme.class, schemeId);
		
		long count = (Long) db.get("select count(p) from SubjectPeriod p where p.period.periodScheme.id = '" + schemeId + "'");
		if ( count == 0 ) {
			db.begin();
			db.remove(periodScheme);
			db.commit();
		}
		else {
			context.put("errorMessage", "Can't remove period scheme " + periodScheme.getCode());
			System.out.println("CANNOT remove PeriodScheme " + periodScheme.getCode());
			context.put("scheme_id", periodScheme.getId());
		}
		
		List<PeriodScheme> periodSchemes = db.list("select p from PeriodScheme p");
		context.put("period_schemes", periodSchemes);
		
		vm = path + "select_schemes.vm";
		
	}

	private void deletePeriod() throws Exception {
		String periodId = request.getParameter("period_id");
		Period period = db.find(Period.class, periodId);
		String periodSchemeId = request.getParameter("periodSchemeId");
		PeriodScheme periodScheme = null; // db.find(PeriodScheme.class, periodSchemeId); // period.getPeriodScheme();
		if ( period.getParent() == null ) {
			periodScheme = period.getPeriodScheme();
		}
		else {
			periodScheme = period.getParent().getPeriodScheme();
		}
		long count = (Long) db.get("select count(s) from StudentStatus s where s.period.id = '" + periodId + "'");
		
		if ( count == 0 ) {
			
			db.begin();
			
			if ( period.hasParent() ) {
				Period parent = period.getParent();
				parent.removeChild(period);
			}
			
			periodScheme.removePeriod(period);
			
			db.remove(period);
			db.commit();
			
		}
		else {
			System.out.println("Can NOT delete period " + period.getName());
		}
		
		listPeriods(periodScheme);
		
	}

	private void savePeriodName() throws Exception {
		String periodId = request.getParameter("period_id");
		String name = request.getParameter("period_name_" + periodId);
		
		Period period = db.find(Period.class, periodId);
		
		db.begin();
		period.setCode(name);
		period.setName(name);
		db.commit();
		
		vm = path + "empty.vm";
	}
	
	private void updateSchemeCode() throws Exception {
		String schemeId = request.getParameter("scheme_id");
		String code = request.getParameter("period_scheme_code");
		
		PeriodScheme scheme = db.find(PeriodScheme.class, schemeId);
		
		db.begin();
		scheme.setCode(code);
		db.commit();
		
		vm = path + "empty.vm";
	}
	

	private void addPeriodScheme() throws Exception {
		
		String name = request.getParameter("period_scheme_name");
		
		db.begin();
		PeriodScheme periodScheme = new PeriodScheme();
		periodScheme.setCode(name);
		db.persist(periodScheme);
		db.commit();
		
		List<PeriodScheme> periodSchemes = db.list("select p from PeriodScheme p");
		context.put("period_schemes", periodSchemes);
		vm = path + "select_schemes.vm";
		
	}

	private void addPeriod() throws Exception {
		String periodSchemeId = request.getParameter("period_scheme_id");
		PeriodScheme periodScheme = db.find(PeriodScheme.class, periodSchemeId);
		context.put("period_scheme", periodScheme);
		List<Period> periods = periodScheme.getPeriodList();
		
		String name = request.getParameter("period_name");
		
		int count = 0;
		if ( periods.size() > 0 ) {
			Period lastPeriod = periods.get(periods.size()-1);
			count = lastPeriod.getSequence() + 1;
		}
		
		db.begin();
		Period period = new Period();
		period.setCode(name);
		period.setName(name);
		period.setPeriodScheme(periodScheme);
		period.setSequence(count);
		
		periodScheme.addPeriod(period);
		db.persist(period);
		
		db.commit();
		
		context.put("period_added", true);
		listPeriods(periodScheme);
		
	}

	private void addChild() throws Exception {
		String periodId = request.getParameter("period_id");
		Period period = db.find(Period.class, periodId);
		PeriodScheme periodScheme = null; // period.getPeriodScheme();
		
		if ( period.getParent() == null ) {
			periodScheme = period.getPeriodScheme();
		}
		else {
			periodScheme = period.getParent().getPeriodScheme();
		}

		//to get sequence number
		List<Period> childs = period.getChildList();
		int count = 0;
		//go to last child
		if ( childs.size() > 0 ) {
			Period child = childs.get(childs.size()-1);
			count = child.getSequence() + 1;
		}
		
		String name = request.getParameter("child_" + period.getId());
		db.begin();
		Period child = new Period();
		//child.setPeriodScheme(periodScheme);
		child.setParent(period);
		child.setCode(name);
		child.setName(name);
		child.setSequence(count);
		period.addChild(child);
		db.persist(child);
		db.commit();
		
		
		context.put("child_added", true);
		context.put("period", period);
		listPeriods(periodScheme);
		
		
	}

	private void listPeriods(PeriodScheme periodScheme) {
		context.put("periodSchemeId", periodScheme.getId());
		context.put("period_scheme", periodScheme);
		List<Period> periods = periodScheme.getPeriodList();
		context.put("periods", periods);
		vm = path + "div_list_periods.vm";
	}

	private void addNew() {
		vm = path + "add_new_scheme.vm";
		
	}

	private void listPeriods() {
		vm = path + "div_list_periods.vm";
		String periodSchemeId = request.getParameter("period_scheme_id");
		context.put("periodSchemeId", periodSchemeId);
		PeriodScheme periodScheme = db.find(PeriodScheme.class, periodSchemeId);
		context.put("period_scheme", periodScheme);
		List<Period> periods = periodScheme.getPeriodList();
		context.put("periods", periods);
		
	}

	private void start() {
		vm = path + "start.vm";
		List<PeriodScheme> periodSchemes = db.list("select p from PeriodScheme p");
		context.put("period_schemes", periodSchemes);
	}
	
	public static void main(String[] args) {
		main2(args);
		
		
	}
	
	
	public static void main2(String[] args) {
		DbPersistence db = new DbPersistence();
		PeriodScheme periodScheme = db.find(PeriodScheme.class, "1");
		List<Period> periods = periodScheme.getPeriodList();
		System.out.println("====");
		for ( Period p : periods ) {
			System.out.println(">" + p.getCode() + " " + p.getName() + ", " + p.getLevel());
			listChilds(p);
		}
		
		
	}

	private static void listChilds(Period p) {
		List<Period> childs = p.getChildList();
		if ( childs.size() > 0 ) {
			for ( Period child : childs ) {
				System.out.println("--" + child.getName());
				listChilds(child);
			}
		}
	}

}
