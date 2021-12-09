package educate.hostel.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import educate.db.DbPersistence;
import educate.facilities.entity.UtilityType;
import educate.hostel.entity.BillUtility;
import educate.hostel.entity.Hostel;
import educate.hostel.entity.Unit;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class BillUtilitiesModule extends LebahModule {
	
	private String path = "apps/hostel_bill_utilities";
	private DbPersistence db = new DbPersistence();
	
	public void preProcess() {
		context.put("df", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("nf", new DecimalFormat("#.00"));
		context.put("mf", new DecimalFormat("#.00"));
	}

	@Override
	public String start() {
		context.put("hostels", db.list("select h from Hostel h order by h.sequence"));
		return path + "/start.vm";
	}
	
	@Command("getUnits")
	public String getUnits() throws Exception {
		String hostelId = getParam("hostelId");
		Hostel hostel = db.find(Hostel.class, hostelId);
		
		List<Unit> units = db.list("select u from Unit u where u.floor.block.building.hostel.id = '" + hostelId + "' order by u.floor.block.building.sequence, u.floor.block.sequence, u.sequence");
		context.put("units", units);
		
		return path + "/getUnits.vm";
	}
	
	@Command("getBillUtilities")
	public String getBillUtilities() throws Exception {
		String unitId = getParam("unitId");
		Unit unit = db.find(Unit.class, unitId);
		context.put("unit", unit);
		
		List<BillUtility> billUtilities = db.list("select b from BillUtility b where b.unit.id = '" + unitId + "' order by b.dateMeterReading desc");
		context.put("billUtilities", billUtilities);
		
		return path + "/getBillUtilities.vm";
	}
	
	@Command("addBillUtility")
	public String addBillUtility() throws Exception {
		String unitId = getParam("unitId");
		Unit unit = db.find(Unit.class, unitId);
		context.put("unit", unit);
		
		List<UtilityType> types = db.list("select t from UtilityType t order by t.sequence");
		context.put("types", types);
		
		
		return path + "/billUtility.vm";
	}
	
	@Command("editBillUtility")
	public String editBillUtility() throws Exception {
		String unitId = getParam("unitId");
		Unit unit = db.find(Unit.class, unitId);
		context.put("unit", unit);
		
		String billId = getParam("billId");
		BillUtility bill = db.find(BillUtility.class, billId);
		context.put("bill", bill);
		
		List<UtilityType> types = db.list("select t from UtilityType t order by t.sequence");
		context.put("types", types);
		
		
		return path + "/billUtility.vm";
	}	
	
	@Command("saveBillUtility")
	public String saveBillUtility() throws Exception {
		String unitId = getParam("unitId");
		Unit unit = db.find(Unit.class, unitId);
		
		db.begin();
		
		BillUtility bill = null;
		String billId = getParam("billId");
		if ( !"".equals(billId)) {
			bill = db.find(BillUtility.class, billId);
		} else {
			bill = new BillUtility();
		}
		
		bill.setDateMeterReading(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("dateMeterReading")));
		
		UtilityType utility = db.find(UtilityType.class, getParam("utilityTypeId"));
		bill.setUnit(unit);
		bill.setType(utility);
		bill.setPreviousMeterReading(getParam("previousMeterReading"));
		bill.setLatestMeterReading(getParam("latestMeterReading"));
		bill.setUnitRead(getParam("unitRead"));
		
		bill.setOutstandingAmount(parseDouble(getParam("outstandingAmount")));
		bill.setPreviousAmountPaid(parseDouble(getParam("previousAmountPaid")));
		bill.setLatestAmountPayable(parseDouble(getParam("latestAmountPayable")));
		bill.setTotalAmountPayable(parseDouble(getParam("totalAmountPayable")));
		
		if ( "".equals(billId)) {
			db.persist(bill);
		}
		
		db.commit();
		
		return getBillUtilities();
	}
	
	private double parseDouble(String d) {
		try {
			return Double.parseDouble(d);
		} catch ( Exception e ) { }
		return 0.0d;
	}

}
