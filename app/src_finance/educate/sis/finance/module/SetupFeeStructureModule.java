package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.finance.entity.FeeItem;
import educate.sis.finance.entity.FeePaymentType;
import educate.sis.finance.entity.FeeStructure;
import educate.sis.finance.entity.FeeStructureItem;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class SetupFeeStructureModule  extends AjaxModule {
	
	String path = "apps/util/finance/fee_structure/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "fee_structure".equals(command)) feeStructure();
		else if ( "gee_fee_structure".equals(command)) getFeeStructure();
		else if ( "select_other".equals(command)) selectOther();
		else if ( "add_fee".equals(command)) addFee();
		else if ( "remove_fee".equals(command)) removeFee();
		else if ( "update_course_fee".equals(command)) updateCourseFee();
		else if ( "create_session".equals(command)) createSession();
		else if ( "delete_fee_structure".equals(command)) deleteFeeStructure();
		return vm;
	}

	private void deleteFeeStructure() throws Exception {
		String feeStructureId = request.getParameter("fee_structure_id");
		System.out.println("fee structure id = " + feeStructureId);
		
		
		//db.executeUpdate("delete from FeeStructureItem i where i.feeStructure.id = '" + feeStructureId + "'");
		
		FeeStructure feeStructure = db.find(FeeStructure.class, feeStructureId);
		db.begin();
		db.remove(feeStructure);
		db.commit();
		
		feeStructure();
		
	}

	private void createSession() throws Exception {
		vm = path + "div_fee_items.vm";
		String sessionDate = getParam("session_date");
		Date date = new SimpleDateFormat("dd-MM-yyyy").parse(sessionDate);
		
		String programId = request.getParameter("program_id");
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		
		FeeStructure feeStructure = null;
		Hashtable h = new Hashtable();
		h.put("date", date);
		
		String sql = "select f from FeeStructure f where f.program.id = '" + program.getId() + "' and f.sessionDate = :date ";
		List<FeeStructure> list = db.list(sql, h);

		if ( list.size() > 0 ) {
			//already got.. so get it
			feeStructure = (FeeStructure) list.get(0);
		}
		else {
			//create
			db.begin();
			feeStructure = new FeeStructure();
			feeStructure.setProgram(program);
			feeStructure.setSessionDate(date);
			db.persist(feeStructure);
			db.commit();
		}
		
		context.put("fee_structure", feeStructure);

		List<Period> periods = program.getPeriodScheme().getAllPeriodList();
		context.put("periods", periods);
		
		List<FeeItem> fees = db.list("select f from FeeItem f where f.feeType = 'PROGRAM'  order by f.code");
		context.put("fees", fees);
		
		prepareFeeItems(feeStructure.getItems());

		
		
	}
	
	private void selectOther() throws Exception {
		vm = path + "div_fee_items.vm";

		String feeStructureId = request.getParameter("fee_structure_list");
		FeeStructure feeStructure = (FeeStructure) db.find(FeeStructure.class, feeStructureId);
		context.put("fee_structure", feeStructure);
		Program program = feeStructure.getProgram();
		context.put("program", program);

		List<Period> periods = program.getPeriodScheme().getAllPeriodList();
		context.put("periods", periods);
		
		List<FeeItem> fees = db.list("select f from FeeItem f where f.feeType = 'PROGRAM'  order by f.code");
		context.put("fees", fees);
		
		prepareFeeItems(feeStructure.getItems());
	}
	
	private void getFeeStructure() throws Exception {
		vm = path + "div_fee_items.vm";

		String feeStructureId = request.getParameter("fee_structure_id");
		FeeStructure feeStructure = (FeeStructure) db.find(FeeStructure.class, feeStructureId);
		context.put("fee_structure", feeStructure);
		Program program = feeStructure.getProgram();
		context.put("program", program);

		List<Period> periods = program.getPeriodScheme().getAllPeriodList();
		context.put("periods", periods);
		
		List<FeeItem> fees = db.list("select f from FeeItem f where f.feeType = 'PROGRAM'  order by f.code");
		context.put("fees", fees);
		
		prepareFeeItems(feeStructure.getItems());
	}
	
	private void feeStructure() throws Exception {
		vm = path + "fee_structure.vm";
		String programId = request.getParameter("program_id");
		Program program = (Program) db.find(Program.class, programId);
		context.put("program", program);
		
		FeeStructure feeStructure = null;
		String sql = "select f from FeeStructure f where f.program.id = '" + program.getId() + "' order by f.sessionDate desc";
		
		List<FeeStructure> list = db.list(sql);
		context.put("fee_structures", list);
		
		System.out.println("number of fee structures = " + list.size());
		
		
		//if else below is redundant... sajer jer buat mcm ni
		if ( list.size() == 1 ) {
			System.out.println("Got one fee structure for program " + program.getCode());
			feeStructure = list.get(0);
		}
		else if ( list.size() > 1 ) {
			System.out.println("Got many fee structures");
			feeStructure = list.get(0);
		}
		
		if ( feeStructure == null ) {
			feeStructure = new FeeStructure();
			try {
				db.begin();
				feeStructure.setId(program.getCode());
				feeStructure.setProgram(program);
				db.persist(feeStructure);
				db.commit();
			} catch ( Exception e ) {
				System.out.println("Get fee structure - " + e.getMessage());
				db.begin();
				feeStructure.setProgram(program);
				db.persist(feeStructure);
				db.commit();
			}
			//reload
			feeStructure = (FeeStructure) db.get(sql);
			
			
		}
		
		//check session date
		if ( feeStructure.getSessionDate() == null ) {
			System.out.println("session date is null");
			//get first date from sessions
			Session session = (Session) db.get("select s from Session s order by s.startDate asc");
			Date startDate = session.getStartDate();
			System.out.println("date = " + startDate);
			db.begin();
			feeStructure.setSessionDate(startDate);
			db.commit();
			
		}
		else {
			System.out.println("This fee structure session date is " + feeStructure.getSessionDate());
		}
		

		context.put("fee_structure", feeStructure);

		if ( program.getPeriodScheme() == null ) {
			context.put("errorMessage", "This program does not have a Period Scheme.");
			return;
		}
		List<Period> periods = program.getPeriodScheme().getAllPeriodList();
		context.put("periods", periods);
		List<FeeItem> fees = db.list("select f from FeeItem f where f.feeType = 'PROGRAM'  order by f.code");
		context.put("fees", fees);
		prepareFeeItems(feeStructure.getItems());

	
	}


	private void updateCourseFee() throws Exception {
		vm = path + "div_fee_items.vm";
		String programId = request.getParameter("program_id");
		Program program = (Program) db.find(Program.class, programId);
		context.put("program", program);
		String sql = "select f from FeeStructure f where f.program.id = '" + program.getId() + "'";
		FeeStructure feeStructure = (FeeStructure) db.get(sql);
		context.put("fee_structure", feeStructure);
		String amount = request.getParameter("course_fee_amount");
		double amountValue = 0.0d;
		try {
			amountValue = Double.parseDouble(amount);
		} catch ( Exception e ) {}
		db.begin();
		feeStructure.setAmountPerCredit(amountValue);
		db.commit();
		
		List<Period> periods = program.getPeriodScheme().getAllPeriodList();
		context.put("periods", periods);
		
		prepareFeeItems(feeStructure.getItems());
		
	}

	private void removeFee() throws Exception {
		vm = path + "div_fee_items.vm";

		String feeStructureId = request.getParameter("fee_structure_id");
		FeeStructure feeStructure = (FeeStructure) db.find(FeeStructure.class, feeStructureId);
		context.put("fee_structure", feeStructure);
		Program program = feeStructure.getProgram();
		context.put("program", program);
		
		String itemId = request.getParameter("item_id");
		FeeStructureItem item = (FeeStructureItem) db.find(FeeStructureItem.class, itemId);
		db.begin();
		feeStructure.removeItem(item);
		db.remove(item);
		db.commit();
		
		List<Period> periods = program.getPeriodScheme().getAllPeriodList();
		context.put("periods", periods);
		
		prepareFeeItems(feeStructure.getItems());
		
	}

	private void addFee() throws Exception {
		vm = path + "div_fee_items.vm";
		
		String feeStructureId = request.getParameter("fee_structure_id");
		FeeStructure feeStructure = (FeeStructure) db.find(FeeStructure.class, feeStructureId);
		context.put("fee_structure", feeStructure);
		Program program = feeStructure.getProgram();
		context.put("program", program);
		
		String amount = request.getParameter("amount");
		double feeAmt = 0.0d;
		try {
			feeAmt = Double.parseDouble(amount);
		} catch ( Exception e ) {}
		
		String periodId = request.getParameter("period_id");
		if ( !"".equals(periodId)) {
			Period period = (Period) db.find(Period.class, periodId);
			FeeItem fee = null;
			String feeId = request.getParameter("fee_id");
			if ( "".equals(feeId)) {
				String code = request.getParameter("fee_code");
				String description = request.getParameter("fee_description");

				if ( !"".equals(code) && !"".equals(description)) {
					db.begin();
					fee = new FeeItem();
					fee.setCode(code);
					fee.setDescription(description);
					fee.setFeeType("PROGRAM");
					FeeStructureItem item = new FeeStructureItem();
					item.setFeePaymentType(FeePaymentType.PERIOD_BASED);
					item.setFeeItem(fee);
					item.setPeriod(period);
					item.setAmount(feeAmt);
					feeStructure.addItem(item);
					db.commit();
				}
			}
			else {
				fee = (FeeItem) db.find(FeeItem.class, feeId);
				db.begin();
				FeeStructureItem item = new FeeStructureItem();
				item.setFeePaymentType(FeePaymentType.PERIOD_BASED);
				item.setFeeItem(fee);
				item.setPeriod(period);
				item.setAmount(feeAmt);
				feeStructure.addItem(item);
				db.commit();
			}

		}
		
		List<Period> periods = program.getPeriodScheme().getAllPeriodList();
		context.put("periods", periods);
		
		prepareFeeItems(feeStructure.getItems());
		
	}



	private void prepareFeeItems(Set<FeeStructureItem> items) {
		Hashtable<String, List<FeeStructureItem>> itemByPeriod = new Hashtable<String, List<FeeStructureItem>>();
		context.put("items_byPeriod", itemByPeriod);
		for ( FeeStructureItem i : items ) {
			//itemByPeriod.put(item.getPeriod().getId(), i);
			List<FeeStructureItem> _items = itemByPeriod.get(i.getPeriod().getId());
			if ( _items == null ) _items = new ArrayList<FeeStructureItem>();
			_items.add(i);
			itemByPeriod.put(i.getPeriod().getId(), _items);
		}
	}	

	private void start() {
		vm = path + "select_program.vm";
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
	}
	
}
