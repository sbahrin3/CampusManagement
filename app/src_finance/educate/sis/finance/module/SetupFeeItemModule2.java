package educate.sis.finance.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.sis.finance.entity.FeeItem;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SetupFeeItemModule2 extends LebahModule {
	
	private String path = "apps/util/finance/fee_item2";
	private DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		
		List<FeeItem> fees = db.list("select f from FeeItem f where f.feeType = 'PROGRAM' order by f.code");
		context.put("fees", fees);
		
		return path + "/start.vm";
	}
	
	@Command("listFees")
	public String listFees() throws Exception {
		List<FeeItem> fees = db.list("select f from FeeItem f where f.feeType = 'PROGRAM' order by f.code");
		context.put("fees", fees);
		return path + "/listFees.vm";
	}
	
	@Command("saveFee")
	public String saveFee() throws Exception {
		String feeId = getParam("feeId");
		FeeItem fee = null;
		if ( !"".equals(feeId) ) 
			fee = db.find(FeeItem.class, feeId);
		else
			fee = new FeeItem();
		String code = getParam("code");
		String description = getParam("description");
		int subjectType = "".equals(getParam("subjectType")) ? 0 : Integer.parseInt(getParam("subjectType"));
		int partnerType = "".equals(getParam("partnerType")) ? 0 : Integer.parseInt(getParam("partnerType"));
		int payingMode = "".equals(getParam("payingMode")) ? 0 : Integer.parseInt(getParam("payingMode"));
		db.begin();
		fee.setCode(code);
		fee.setDescription(description);
		fee.setFeeType("PROGRAM");
		fee.setSubjectType(subjectType);
		fee.setPartnerType(partnerType);
		fee.setPayingMode(payingMode);
		if (  "".equals(feeId)) db.persist(fee);
		db.commit();
		
		return listFees();
	}
	
	@Command("deleteFee")
	public String deleteFee() throws Exception {
		String feeId = getParam("feeId");
		FeeItem feeItem = db.find(FeeItem.class, feeId);
		
		if ( db.list("select f from FeeStructureItem f where f.feeItem.id = '" + feeId + "'").size() == 0 ) {
			db.begin();
			db.remove(feeItem);
			db.commit();
		}
		
		return listFees();
	}
	
	@Command("getFee")
	public String getFee() throws Exception {
		String feeId = getParam("feeId");
		FeeItem feeItem = db.find(FeeItem.class, feeId);
		context.put("fee", feeItem);
		return path + "/getFee.vm";
	}

}
