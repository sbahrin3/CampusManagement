package educate.facilities.module;

import educate.db.DbPersistence;
import educate.facilities.entity.VehicleType;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class VehicleTypeModule extends LebahModule {
	
	private String path = "facilities/vehicle_type";
	private DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		context.put("vehicles", db.list("select c from VehicleType c order by c.sequence"));
		return path + "/start.vm";
	}
	
	@Command("updateSequence")
	public String updateSequence() throws Exception {
		String[] ids = request.getParameterValues("vehicleIds");
		int i = 0;
		for ( String id : ids ) {
			i++;
			VehicleType c = db.find(VehicleType.class, id);
			db.begin();
			c.setSequence(i);
			db.commit();
		}
		context.put("vehicles", db.list("select c from VehicleType c order by c.sequence"));
		return path + "/listVehicles.vm";
	}
	
	@Command("addVehicle")
	public String addVehicle() throws Exception {
		Integer seq = (Integer) db.get("select max(c.sequence) from VehicleType c");
		int sequence = seq != null ? seq.intValue() + 1 : 1;
		db.begin();
		VehicleType c = new VehicleType();
		c.setName(getParam("name"));
		c.setSequence(sequence);
		db.persist(c);
		db.commit();
		context.put("vehicles", db.list("select c from VehicleType c order by c.sequence"));
		return path + "/listVehicles.vm";
	}
	

	@Command("changeName")
	public String changeName() throws Exception {
		String vehicleId = getParam("vehicleId");
		VehicleType vehicle = db.find(VehicleType.class, vehicleId);
		db.begin();
		vehicle.setName(getParam("name_" + vehicleId));
		db.commit();
		return path + "/empty.vm";
	}	
	 
	@Command("deleteVehicle")
	public String deleteVehicle() throws Exception {
		String vehicleId = getParam("vehicleId");
		VehicleType vehicle = db.find(VehicleType.class, vehicleId);
		db.begin();
		db.remove(vehicle);
		db.commit();
		context.put("vehicless", db.list("select c from VehicleType c order by c.sequence"));
		return path + "/listIssues.vm";
	}


}
