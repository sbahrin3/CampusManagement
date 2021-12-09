package educate.facilities.module;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.facilities.entity.Driver;
import educate.facilities.entity.Vehicle;
import educate.facilities.entity.VehicleMovementLog;
import educate.facilities.entity.VehicleRoadtaxLog;
import educate.facilities.entity.VehicleType;
import lebah.portal.action.Command;
import lebah.template.LebahRecordTemplateModule;
import lebah.template.OperatorEqualTo;

public class VehicleRecordModule  extends LebahRecordTemplateModule<Vehicle> {

	@Override
	public Class getIdType() {
		return String.class;
	}

	@Override
	public void afterSave(Vehicle v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void begin() {
		System.out.println(command);
		context.put("path", getPath());
		context.put("util", new lebah.util.Util());
		this.setReadonly(false);
		List<VehicleType> vehicleTypes = db.list("select c from VehicleType c order by c.sequence");
		context.put("vehicleTypes", vehicleTypes);
	}

	@Override
	public boolean delete(Vehicle v) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getPath() {
		return "facilities/vehicle_records";
	}

	@Override
	public Class<Vehicle> getPersistenceClass() {
		return Vehicle.class;
	}

	@Override
	public void getRelatedData(Vehicle v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(Vehicle a) throws Exception {
		String vehicleTypeId = getParam("vehicleTypeId");
		VehicleType category = db.find(VehicleType.class, vehicleTypeId);
		save(a, category);
	}

	@Override
	public Map<String, Object> searchCriteria() throws Exception {
		Map map = new HashMap();
		map.put("type.id", new OperatorEqualTo(getParam("findVehicleTypeId")));
		map.put("registrationNo", getParam("findRegistrationNo"));
		map.put("brandName", getParam("findBrandName"));
		map.put("modelName", getParam("findModelName"));
		return map;
	}
	
	private void save(Vehicle a, VehicleType vehicleType) {
		a.setType(vehicleType);
		a.setRegistrationNo(getParam("registrationNo"));
		a.setBrandName(getParam("brandName"));
		a.setModelName(getParam("modelName"));
		a.setPhotoFileName(getParam("photoFileName"));
		a.setChasisNo(getParam("chasisNo"));
		a.setEngineNo(getParam("engineNo"));
	}
	
	@Command("uploadFile")
	public String uploadFile() throws Exception {
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		
		String documentId = getParam("documentId");
		String uploadDir = "c:/uploaded/";
		File dir = new File(uploadDir);
		if ( !dir.exists() ) dir.mkdir();
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = upload.parseRequest(request);
		Iterator itr = items.iterator();
		List<FileItem> files = new ArrayList<FileItem>();
		while (itr.hasNext()) {
			FileItem item = (FileItem)itr.next();
			if ((!(item.isFormField())) && (item.getName() != null) && (!("".equals(item.getName())))) {
				if ( documentId.equals(item.getFieldName())) {
					files.add(item);
				}
			}
		}
		
		List<String> filenames = new ArrayList<String>();
		context.put("filenames", filenames);
		
		for ( FileItem item : files ) {
			String fileName = item.getName();
			filenames.add(fileName);
			String savedName = uploadDir + fileName;
			context.put("serverFileName", savedName);
			item.write(new File(savedName));
			
			if ( "vehiclePhoto".equals(documentId)) { 
				System.out.println("uploaded vehicle photo!");
				String vehicleId = getParam("vehicleId");
				if ( !"".equals(vehicleId)) {
					db.begin();
					Vehicle a = db.find(Vehicle.class, vehicleId);
					a.setPhotoFileName(savedName);
					db.commit();
				}
				break;
			}
		}
		
		return getPath() + "/uploaded.vm";
	}
	

	@Command("vehicleMovementLog")
	public String vehicleMovementLog() throws Exception {
		String vehicleId = getParam("vehicleId");
		context.put("vehicleId", vehicleId);
		List<VehicleMovementLog> movementLogs = db.list("select m from VehicleMovementLog m where m.vehicle.id = '" + vehicleId + "' order by m.departDateTime");
		context.put("movementLogs", movementLogs);
		return getPath() + "/vehicleMovementLog.vm";
	}
	
	@Command("saveMovementLog")
	public String saveMovementLog() throws Exception {
		String vehicleId = getParam("vehicleId");
		Vehicle vehicle = db.find(Vehicle.class, vehicleId);
		String departDateTime = getParam("departDateTime");
		String departDateTimeHour = getParam("departDateTimeHour");
		String departDateTimeMinute = getParam("departDateTimeMinute");
		String expectedReturnDateTime = getParam("expectedReturnDateTime");
		String expectedReturnDateTimeHour = getParam("expectedReturnDateTimeHour");
		String expectedReturnDateTimeMinute = getParam("expectedReturnDateTimeMinute");
		
		String _date1 = departDateTime + " " + departDateTimeHour + ":" + departDateTimeMinute;
		Date date1 = new SimpleDateFormat("dd-MM-yyyy H:m").parse(_date1);
		String _date2 = expectedReturnDateTime + " " + expectedReturnDateTimeHour + ":" + expectedReturnDateTimeMinute;
		Date date2 = new SimpleDateFormat("dd-MM-yyyy H:m").parse(_date2);
		
		//String driverName = getParam("driverName");
		String driverId = getParam("driverId");
		Driver driver = db.find(Driver.class, driverId);
		
		String destinationName = getParam("destinationName");
		String destinationRemark = getParam("destinationRemark");
		
		String _odometerDepart = getParam("odometerDepart");
		int odometerDepart = 0;
		try {
			odometerDepart = Integer.parseInt(_odometerDepart);
		} catch ( Exception e ) {}
		
		db.begin();
		VehicleMovementLog log = new VehicleMovementLog();
		log.setVehicle(vehicle);
		log.setDepartDateTime(date1);
		log.setExpectedReturnDateTime(date2);
		//log.setDriverName(driverName);
		log.setDriver(driver);
		
		
		log.setDestinationName(destinationName);
		log.setDestinationRemark(destinationRemark);
		log.setOdometerDepart(odometerDepart);
		db.persist(log);
		db.commit();
		
		List<VehicleMovementLog> movementLogs = db.list("select m from VehicleMovementLog m where m.vehicle.id = '" + vehicleId + "' order by m.departDateTime");
		context.put("movementLogs", movementLogs);
		return getPath() + "/vehicleMovementList.vm";
	}
	
	@Command("addVehicleLog")
	public String addVehicleLog() throws Exception {
		String vehicleId = getParam("vehicleId");
		context.put("vehicleId", vehicleId);
		context.put("drivers", db.list("select d from Driver d order by d.staffNo"));
		return getPath() + "/addVehicleLog.vm";
	}
	
	@Command("updateMovementLog")
	public String updateMovementLog() throws Exception {
		String movementLogId = getParam("movementLogId");
		VehicleMovementLog m = db.find(VehicleMovementLog.class, movementLogId);
		
		String returnDateTime = getParam("returnDateTime");
		String returnDateTimeHour = getParam("returnDateTimeHour");
		String returnDateTimeMinute = getParam("returnDateTimeMinute");
		
		String _odometerDepart = getParam("odometerDepart2");
		String _odometerReturn = getParam("odometerReturn");
		
		int odometerDepart = 0;
		int odometerReturn = 0;
		try {
			odometerDepart = Integer.parseInt(_odometerDepart);
		} catch ( Exception e ) {}
		try {
			odometerReturn = Integer.parseInt(_odometerReturn);
		} catch ( Exception e ) {}
		
		String date = returnDateTime + " " + returnDateTimeHour + ":" + returnDateTimeMinute;
		Date returnDate = new SimpleDateFormat("dd-MM-yyyy H:m").parse(date);
		
		db.begin();
		m.setReturnDateTime(returnDate);
		m.setDestinationRemark(getParam("destinationRemark"));
		m.setOdometerDepart(odometerDepart);
		m.setOdometerReturn(odometerReturn);
		db.commit();
		
		
		List<VehicleMovementLog> movementLogs = db.list("select m from VehicleMovementLog m where m.vehicle.id = '" + m.getVehicle().getId() + "' order by m.departDateTime");
		context.put("movementLogs", movementLogs);
		return getPath() + "/vehicleMovementList.vm";
	}
	
	@Command("deleteMovementLog")
	public String deleteMovementLog() throws Exception {
		String movementLogId = getParam("movementLogId");
		VehicleMovementLog m = db.find(VehicleMovementLog.class, movementLogId);
		String vehicleId = m.getVehicle().getId();
		
		db.begin();
		db.remove(m);
		db.commit();
		
		List<VehicleMovementLog> movementLogs = db.list("select m from VehicleMovementLog m where m.vehicle.id = '" + vehicleId + "' order by m.departDateTime");
		context.put("movementLogs", movementLogs);
		return getPath() + "/vehicleMovementList.vm";
	}
	
	@Command("getRemark")
	public String getRemark() throws Exception {
		String movementLogId = getParam("movementLogId");
		VehicleMovementLog m = db.find(VehicleMovementLog.class, movementLogId);
		context.put("movementLog", m);
		return getPath() + "/remark.vm";
	}
	
	public static void main(String[] args) throws ParseException {
		String d = "29-06-2013 14:05";
		Date date1 = new SimpleDateFormat("dd-MM-yyyy H:m").parse(d);
		System.out.println(new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(date1));
		
	}
	
	@Command("roadtaxRenewalLog")
	public String roadTaxRenewalLog() throws Exception {
		String vehicleId = getParam("vehicleId");
		context.put("vehicleId", vehicleId);
		List<VehicleRoadtaxLog> roadtaxLogs = db.list("select m from VehicleRoadtaxLog m where m.vehicle.id = '" + vehicleId + "' order by m.renewalDate desc");
		context.put("roadtaxLogs", roadtaxLogs);
		return getPath() + "/vehicleRoadtaxLog.vm";
	}	
	
	@Command("addRoadtaxLog")
	public String addRoadtaxLog() throws Exception {
		String vehicleId = getParam("vehicleId");
		Vehicle vehicle = db.find(Vehicle.class, vehicleId);
		context.put("vehicle", vehicle);
		context.remove("roadtaxLog");
		return getPath() + "/roadtaxLog.vm";
	}	
	
	@Command("getRoadtaxLog")
	public String getRoadtaxLog() throws Exception {
		String roadtaxLogId = getParam("roadtaxLogId");
		
		VehicleRoadtaxLog log = db.find(VehicleRoadtaxLog.class, roadtaxLogId);
		context.put("roadtaxLog", log);
		return getPath() + "/roadtaxLog.vm";
	}
	
	@Command("updateRoadtaxLog")
	public String updateRoadtaxLog() throws Exception {
		String vehicleId = getParam("vehicleId");
		Vehicle vehicle = db.find(Vehicle.class, vehicleId);
		String roadtaxLogId = getParam("roadtaxLogId");
		VehicleRoadtaxLog log = null;
		
		log = db.find(VehicleRoadtaxLog.class, roadtaxLogId);
		context.put("roadtaxLog", log);
		if ( "".equals(roadtaxLogId) ) log = new VehicleRoadtaxLog();
		
		String _renewalDate = getParam("renewalDate");
		String _expiryDate = getParam("expiryDate");
		String _insuredValue = getParam("insuredValue");
		
		Date renewalDate = null;
		Date expiryDate = null;
		int insuredValue = 0;
		
		try { renewalDate = new SimpleDateFormat("dd-MM-yyyy").parse(_renewalDate); } catch ( Exception e ) {}
		try { expiryDate = new SimpleDateFormat("dd-MM-yyyy").parse(_expiryDate); } catch ( Exception e ) {}
		try { insuredValue = Integer.parseInt(_insuredValue); } catch ( Exception e ) {}
		
		db.begin();
		log.setRenewalDate(renewalDate);
		log.setExpiryDate(expiryDate);
		log.setInsuredValue(insuredValue);
		log.setVehicle(vehicle);
		if ( "".equals(roadtaxLogId)) db.persist(log);
		db.commit();
		
		log.setRenewalDate(renewalDate);
		
		
		
		List<VehicleRoadtaxLog> roadtaxLogs = db.list("select m from VehicleRoadtaxLog m where m.vehicle.id = '" + vehicleId + "' order by m.renewalDate desc");
		context.put("roadtaxLogs", roadtaxLogs);
		return getPath() + "/vehicleRoadtaxLog.vm";
	}
	
	@Command("deleteRoadtaxLog")
	public String deleteRoadtaxLog() throws Exception {
		String roadtaxLogId = getParam("roadtaxLogId");
		VehicleRoadtaxLog m = db.find(VehicleRoadtaxLog.class, roadtaxLogId);
		String vehicleId = m.getVehicle().getId();
		
		db.begin();
		db.remove(m);
		db.commit();
		
		List<VehicleRoadtaxLog> roadtaxLogs = db.list("select m from VehicleRoadtaxLog m where m.vehicle.id = '" + vehicleId + "' order by m.renewalDate");
		context.put("roadtaxLogs", roadtaxLogs);
		return getPath() + "/vehicleRoadtaxList.vm";
	}

}
