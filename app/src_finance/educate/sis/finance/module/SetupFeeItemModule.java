package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.finance.entity.FeeItem;
import lebah.portal.action.AjaxModule;

public class SetupFeeItemModule extends AjaxModule {
	
	String path = "apps/util/finance/item/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.remove("fee");
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "edit".equals(command)) edit();
		else if ( "update".equals(command)) update();
		else if ( "add".equals(command)) add();
		return vm;
	}

	private void update() throws Exception {
		vm = path + "items.vm";
		String feeId = request.getParameter("fee_id");
		FeeItem fee = db.find(FeeItem.class, feeId);
		String code = request.getParameter("code");
		String description = request.getParameter("description");
		
		db.begin();
		fee.setCode(code);
		fee.setDescription(description);
		db.commit();
		
		List<FeeItem> fees = db.list("select f from FeeItem f where f.feeType = 'PROGRAM' order by f.code");
		context.put("fees", fees);
		
	}
	
	private void add() throws Exception {
		vm = path + "items.vm";
		String code = request.getParameter("code");
		String description = request.getParameter("description");
		
		List<FeeItem> fees = db.list("select f from FeeItem f where f.feeType = 'PROGRAM'");
		
		//get the highest seq
		int seq = 0;
		for ( FeeItem f : fees ) seq = f.getSeq() > seq ? f.getSeq() : seq;
		seq++;
		
		db.begin();
		FeeItem fee = new FeeItem();
		fee.setCode(code);
		fee.setDescription(description);
		fee.setFeeType("PROGRAM");
		fee.setSeq(seq);
		db.persist(fee);
		db.commit();
		
		fees = db.list("select f from FeeItem f where f.feeType = 'PROGRAM' order by f.code");
		context.put("fees", fees);
		
	}	
	
	private void edit() {
		vm = path + "items.vm";
		String feeId = request.getParameter("fee_id");
		FeeItem fee = db.find(FeeItem.class, feeId);
		context.put("fee", fee);
		
		List<FeeItem> fees = db.list("select f from FeeItem f where f.feeType = 'PROGRAM' order by f.code");
		context.put("fees", fees);
	}

	private void start() {
		vm = path + "items.vm";
		List<FeeItem> fees = db.list("select f from FeeItem f where f.feeType = 'PROGRAM' order by f.code");
		context.put("fees", fees);
		
		
	}

}
