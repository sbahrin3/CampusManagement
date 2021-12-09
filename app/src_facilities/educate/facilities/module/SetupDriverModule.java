package educate.facilities.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.facilities.entity.Driver;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SetupDriverModule extends LebahModule {
	
	private String path = "facilities/drivers";
	DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		List<Driver> drivers = db.list("select d from Driver d order by d.staffNo");
		context.put("drivers", drivers);
		return path + "/start.vm";
	}
	
	@Command("newDriver")
	public String newDriver() throws Exception {
		context.remove("driver");
		return path + "/driver.vm";
	}
	
	@Command("getDriver")
	public String getDriver() throws Exception {
		String driverId = getParam("driverId");
		Driver driver = db.find(Driver.class, driverId);
		context.put("driver", driver);
		return path + "/driver.vm";
	}

	@Command("saveDriver")
	public String addDriver() throws Exception {
		String driverId = getParam("driverId");
		String staffNo = getParam("staffNo");
		String name = getParam("name");
		
		Driver driver = null;
		if ( !"".equals(driverId)) {
			driver = db.find(Driver.class, driverId);
		} else driver = new Driver();
		db.begin();
		driver.setStaffNo(staffNo);
		driver.setName(name);
		if ( "".equals(driverId)) db.persist(driver);
		db.commit();
		
		List<Driver> drivers = db.list("select d from Driver d order by d.staffNo");
		context.put("drivers", drivers);
		return path + "/listDrivers.vm";
	}

	@Command("deleteDriver")
	public String deleteDriver() throws Exception {
		String driverId = getParam("driverId");
		Driver driver = db.find(Driver.class, driverId);
		db.begin();
		db.remove(driver);
		db.commit();
		List<Driver> drivers = db.list("select d from Driver d order by d.staffNo");
		context.put("drivers", drivers);
		return path + "/listDrivers.vm";
	}

}
