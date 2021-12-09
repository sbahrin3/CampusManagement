package educate.hostel.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.db.DbPersistence;
import educate.hostel.entity.Block;
import educate.hostel.entity.Building;
import educate.hostel.entity.Floor;
import educate.hostel.entity.Hostel;
import educate.hostel.entity.HostelItem;
import educate.hostel.entity.Item;
import educate.hostel.entity.ItemCondition;
import educate.hostel.entity.ItemType;
import educate.hostel.entity.Mover;
import educate.hostel.entity.Room;
import educate.hostel.entity.RoomItem;
import educate.hostel.entity.RoomType;
import educate.hostel.entity.Unit;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SetupHostelModule extends LebahModule {
	
	private String path = "apps/hostel_setup";
	private DbPersistence db = new DbPersistence();
	
	public void preProcess() {
		System.out.println(command);
	}
 
	@Override
	public String start() {
		context.put("hostels", db.list("select h from Hostel h order by h.sequence"));
		return path + "/start.vm";
	}
	
	@Command("listHostels")
	public String listHostels() throws Exception {
		context.put("hostels", db.list("select h from Hostel h order by h.sequence"));
		return path + "/divHostel.vm";
	}
	
	@Command("updateHostelSequence")
	public String updateHostelSequence() throws Exception {
		String[] ids = request.getParameterValues("hostelIds");
		int i = 0;
		for ( String id : ids ) {
			i++;
			Hostel h = db.find(Hostel.class, id);
			db.begin();
			h.setSequence(i);
			db.commit();
		}
		context.put("hostels", db.list("select h from Hostel h order by h.sequence"));
		return path + "/listHostels.vm";
	}
	
	@Command("deleteHostel")
	public String deleteHostel() throws Exception {
		String id = getParam("hostelId");
		Hostel h = db.find(Hostel.class, id);
		if ( db.list("select b from Building b where b.hostel.id = '" + id + "'").size() == 0 ) {
			db.begin();
			db.remove(h);
			db.commit();
		}
		context.put("hostels", db.list("select h from Hostel h order by h.sequence"));
		return path + "/listHostels.vm";
	}
	
	@Command("addHostel")
	public String addHostel() {
		
		System.out.println("*** addHostel ***");
		
		Integer seq = (Integer) db.get("select max(h.sequence) from Hostel h");
		int sequence = seq != null ? seq.intValue() + 1 : 1;
		
		System.out.println("add hostel: seq = " + sequence);
		
		db.begin();
		Hostel h = new Hostel();
		h.setName(getParam("hostelName"));
		h.setCode(getParam("hostelCode"));
		h.setAddress(getParam("hostelAddress"));
		h.setSequence(sequence);
		db.persist(h);
		try {
			db.commit();
		} catch (Exception e) {
			System.out.println("error: " + e.getMessage());
			e.printStackTrace();
		}
		
		context.put("hostels", db.list("select h from Hostel h order by h.sequence"));
		return path + "/listHostels.vm";
	}
	
	@Command("changeHostelCode")
	public String changeHostelCode() throws Exception {
		String id = getParam("hostelId");
		Hostel h = db.find(Hostel.class, id);
		db.begin();
		h.setCode(getParam("hostelCode_" + id));
		db.commit();
		return path + "/empty.vm";
	}

	@Command("changeHostelName")
	public String changeHostelName() throws Exception {
		String id = getParam("hostelId");
		Hostel h = db.find(Hostel.class, id);
		db.begin();
		h.setName(getParam("hostelName_" + id));
		db.commit();
		return path + "/empty.vm";
	}

	@Command("changeHostelAddress")
	public String changeHostelAddress() throws Exception {
		String id = getParam("hostelId");
		Hostel h = db.find(Hostel.class, id);
		db.begin();
		h.setAddress(getParam("hostelAddress_" + id));
		db.commit();
		return path + "/empty.vm";
	}
	
	@Command("addBuilding")
	public String addBuilding() throws Exception {
		String id = getParam("hostelId");
		Hostel h = db.find(Hostel.class, id);
		context.put("hostel", h);
		Integer seq = (Integer) db.get("select max(h.sequence) from Building h");
		int sequence = seq != null ? seq.intValue() + 1 : 1;
		db.begin();
		Building b = new Building();
		b.setCode(getParam("buildingCode"));
		b.setName(getParam("buildingName"));
		b.setSequence(sequence);
		h.addBuilding(b);
		db.commit();
		
		context.put("buildings", db.list("select b from Building b where b.hostel.id = '" + id + "' order by b.sequence"));
		return path + "/listBuildings.vm";
	}
	
	@Command("deleteBuilding")
	public String deleteBuilding() throws Exception {
		String buildingId = getParam("buildingId");
		Building b = db.find(Building.class, buildingId);
		Hostel hostel = b.getHostel();
		context.put("hostel", hostel);
		if ( db.list("select b from Block b where b.building.id = '" + buildingId + "'").size() == 0 ) {
			db.begin();
			db.remove(b);
			db.commit();
		}
		context.put("buildings", db.list("select b from Building b where b.hostel.id = '" + hostel.getId() + "' order by b.sequence"));
		return path + "/listBuildings.vm";
	}	
	
	@Command("changeBuildingCode")
	public String changeBuildingCode() throws Exception {
		String id = getParam("buildingId");
		Building b = db.find(Building.class, id);
		db.begin();
		b.setCode(getParam("buildingCode_" + id));
		db.commit();
		return path + "/empty.vm";
	}
	
	@Command("changeBuildingName")
	public String changeBuildingName() throws Exception {
		String id = getParam("buildingId");
		Building b = db.find(Building.class, id);
		db.begin();
		b.setName(getParam("buildingName_" + id));
		db.commit();
		return path + "/empty.vm";
	}	
	
	@Command("addBlock")
	public String addBlock() throws Exception {
		String id = getParam("buildingId");
		Building h = db.find(Building.class, id);
		context.put("building", h);
		Integer seq = (Integer) db.get("select max(h.sequence) from Block h");
		int sequence = seq != null ? seq.intValue() + 1 : 1;
		db.begin();
		Block b = new Block();
		b.setCode(getParam("blockCode"));
		b.setName(getParam("blockName"));
		b.setSequence(sequence);
		h.addBlock(b);
		db.commit();
		
		context.put("blocks", db.list("select b from Block b where b.building.id = '" + id + "' order by b.sequence"));
		return path + "/listBlocks.vm";
	}
	
	@Command("deleteBlock")
	public String deleteBlock() throws Exception {
		String blockId = getParam("blockId");
		Block b = db.find(Block.class, blockId);
		Building building = b.getBuilding();
		context.put("building", building);
		if ( db.list("select b from Floor b where b.block.id = '" + blockId + "'").size() == 0 ) {
			db.begin();
			db.remove(b);
			db.commit();
		}
		context.put("blocks", db.list("select b from Block b where b.building.id = '" + building.getId() + "' order by b.sequence"));
		return path + "/listBlocks.vm";
	}		
	
	@Command("changeBlockCode")
	public String changeBlockCode() throws Exception {
		String id = getParam("blockId");
		Block b = db.find(Block.class, id);
		db.begin();
		b.setCode(getParam("blockCode_" + id));
		db.commit();
		return path + "/empty.vm";
	}
	
	@Command("changeBlockName")
	public String changeBlockName() throws Exception {
		String id = getParam("blockId");
		Block b = db.find(Block.class, id);
		db.begin();
		b.setName(getParam("blockName_" + id));
		db.commit();
		return path + "/empty.vm";
	}		

	@Command("addFloor")
	public String addFloor() throws Exception {
		String id = getParam("blockId");
		Block h = db.find(Block.class, id);
		context.put("block", h);
		Integer seq = (Integer) db.get("select max(h.sequence) from Floor h");
		int sequence = seq != null ? seq.intValue() + 1 : 1;
		db.begin();
		Floor b = new Floor();
		b.setCode(getParam("floorCode"));
		b.setName(getParam("floorName"));
		b.setSequence(sequence);
		h.addFloor(b);
		db.commit();
		
		context.put("floors", db.list("select b from Floor b where b.block.id = '" + id + "' order by b.sequence"));
		return path + "/listFloors.vm";
	}
	
	@Command("deleteFloor")
	public String deleteFloor() throws Exception {
		String floorId = getParam("floorId");
		Floor b = db.find(Floor.class, floorId);
		Block block = b.getBlock();
		context.put("block", block);
		if ( db.list("select b from Unit b where b.floor.id = '" + floorId + "'").size() == 0 ) {
			db.begin();
			db.remove(b);
			db.commit();
		}
		context.put("floors", db.list("select b from Floor b where b.block.id = '" + block.getId() + "' order by b.sequence"));
		return path + "/listFloors.vm";
	}	
	
	@Command("changeFloorCode")
	public String changeFloorCode() throws Exception {
		String id = getParam("floorId");
		Floor b = db.find(Floor.class, id);
		db.begin();
		b.setCode(getParam("floorCode_" + id));
		db.commit();
		return path + "/empty.vm";
	}
	
	@Command("changeFloorName")
	public String changeFloorName() throws Exception {
		String id = getParam("floorId");
		Floor b = db.find(Floor.class, id);
		db.begin();
		b.setName(getParam("floorName_" + id));
		db.commit();
		return path + "/empty.vm";
	}	
	
	@Command("saveFloorImage")
	public String saveFloorImage() throws Exception {
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		
		String floorId = getParam("floorId");
		String documentId = getParam("documentId");
		
		//System.out.println("documentId = " + documentId);
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = upload.parseRequest(request);
		Iterator itr = items.iterator();
		List<FileItem> files = new ArrayList<FileItem>();
		while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
			if ((!(item.isFormField())) && (item.getName() != null)
					&& (!("".equals(item.getName()))))
				if (documentId.equals(item.getFieldName()))
					files.add(item);
		}
		
		context.remove("h");
		
		for (FileItem item : files) {
			if (("uploadImage_".concat(floorId)).equals(documentId)) {
				System.out.println("uploadImage_".concat(floorId));
				if (!"".equals(floorId)) {
					Floor floor = db.find(Floor.class, floorId);
					context.put("h", floor);
					
					db.begin();
					
					floor.setFloorPlanImage(item.get());
					floor.setFloorPlanImageId(lebah.db.UniqueID.getUID());
					
					db.commit();
				}
				break;
			}
		}
		
		
		return path + "/uploaded.vm";
	}

	@Command("addUnit")
	public String addUnit() throws Exception {
		String id = getParam("floorId");
		Floor h = db.find(Floor.class, id);
		context.put("floor", h);
		Integer seq = (Integer) db.get("select max(h.sequence) from Unit h");
		int sequence = seq != null ? seq.intValue() + 1 : 1;
		db.begin();
		Unit b = new Unit();
		b.setCode(getParam("unitCode"));
		b.setName(getParam("unitName"));
		b.setGender(Integer.parseInt(getParam("unitGender")));
		b.setSequence(sequence);
		h.addUnit(b);
		db.commit();
		
		context.put("units", db.list("select b from Unit b where b.floor.id = '" + id + "' order by b.sequence"));
		return path + "/listUnits.vm";
	}
	
	@Command("deleteUnit")
	public String deleteUnit() throws Exception {
		String unitId = getParam("unitId");
		Unit b = db.find(Unit.class, unitId);
		Floor floor = b.getFloor();
		context.put("floor", floor);
		if ( db.list("select b from educate.hostel.entity.Room b where b.unit.id = '" + unitId + "'").size() == 0 ) {
			db.begin();
			db.remove(b);
			db.commit();
		}
		context.put("units", db.list("select b from Unit b where b.floor.id = '" + floor.getId() + "' order by b.sequence"));
		return path + "/listUnits.vm";
	}		
	
	@Command("changeUnitCode")
	public String changeUnitCode() throws Exception {
		String id = getParam("unitId");
		Unit b = db.find(Unit.class, id);
		db.begin();
		b.setCode(getParam("unitCode_" + id));
		db.commit();
		return path + "/empty.vm";
	}
	
	@Command("changeUnitName")
	public String changeUnitName() throws Exception {
		String id = getParam("unitId");
		Unit b = db.find(Unit.class, id);
		db.begin();
		b.setName(getParam("unitName_" + id));
		db.commit();
		return path + "/empty.vm";
	}		
	
	@Command("changeUnitGender")
	public String changeUnitGender() throws Exception {
		String id = getParam("unitId");
		Unit b = db.find(Unit.class, id);
		db.begin();
		b.setGender(Integer.parseInt(getParam("unitGender_" + id)));
		db.commit();
		return path + "/empty.vm";
	}		

	@Command("addRoom")
	public String addRoom() throws Exception {
		
		String roomTypeId = getParam("roomTypeId");
		RoomType roomType = db.find(RoomType.class, roomTypeId);
		
		String id = getParam("unitId");
		Unit h = db.find(Unit.class, id);
		context.put("unit", h);
		Integer seq = (Integer) db.get("select max(h.sequence) from educate.hostel.entity.Room h");
		int sequence = seq != null ? seq.intValue() + 1 : 1;
		db.begin();
		Room b = new Room();
		b.setCode(getParam("roomCode"));
		b.setName(getParam("roomName"));
		b.setRoomType(roomType);
		try {
			b.setCapacity(Integer.parseInt(getParam("roomCapacity")));
		} catch ( Exception e ) {}
		try {
			b.setRoomDeposit(Double.parseDouble(getParam("roomDeposit")));
		} catch ( Exception e ) {}
		try {
			b.setBedRate(Double.parseDouble(getParam("bedRate")));
		} catch ( Exception e ) {}
		b.setSequence(sequence);
		h.addRoom(b);
		db.commit();
		
		context.put("rooms", db.list("select b from educate.hostel.entity.Room b where b.unit.id = '" + id + "' order by b.sequence"));
		context.put("roomTypes", db.list("select r from RoomType r order by r.sequence"));

		return path + "/listRooms.vm";
	}
	
	@Command("deleteRoom")
	public String deleteRoom() throws Exception {
		String roomId = getParam("roomId");
		Room b = db.find(Room.class, roomId);
		Unit unit = b.getUnit();
		context.put("unit", unit);
		db.begin();
		db.remove(b);
		db.commit();
		context.put("rooms", db.list("select b from educate.hostel.entity.Room b where b.unit.id = '" + unit.getId() + "' order by b.sequence"));
		return path + "/listRooms.vm";
	}		
	
	@Command("changeRoomCode")
	public String changeRoomCode() throws Exception {
		String id = getParam("roomId");
		Room b = db.find(Room.class, id);
		db.begin();
		b.setCode(getParam("roomCode_" + id));
		db.commit();
		return path + "/empty.vm";
	}
	
	@Command("changeRoomName")
	public String changeRoomName() throws Exception {
		String id = getParam("roomId");
		Room b = db.find(Room.class, id);
		db.begin();
		b.setName(getParam("roomName_" + id));
		db.commit();
		return path + "/empty.vm";
	}	
	
	@Command("changeRoomType")
	public String changeRoomType() throws Exception {
		String id = getParam("roomId");
		
		String roomTypeId = getParam("roomType_" + id);
		RoomType roomType = db.find(RoomType.class, roomTypeId);
		
		Room b = db.find(Room.class, id);
		db.begin();
		b.setRoomType(roomType);
		db.commit();
		
		return path + "/empty.vm";
	}
		
	
	@Command("changeRoomCapacity")
	public String changeRoomCapacity() throws Exception {
		String id = getParam("roomId");
		Room b = db.find(Room.class, id);
		db.begin();
		try {
			b.setCapacity(Integer.parseInt(getParam("roomCapacity_" + id)));
		} catch ( Exception e ) {}
		db.commit();
		return path + "/empty.vm";
	}
	
	@Command("changeRoomDeposit")
	public String changeRoomDeposit() throws Exception {
		String id = getParam("roomId");
		Room b = db.find(Room.class, id);
		db.begin();
		try {
			b.setRoomDeposit(Double.parseDouble(getParam("roomDeposit_" + id)));
		} catch ( Exception e ) {}
		db.commit();
		return path + "/empty.vm";
	}	
	
	@Command("changeBedRate")
	public String changeBedRate() throws Exception {
		String id = getParam("roomId");
		Room b = db.find(Room.class, id);
		db.begin();
		try {
			b.setBedRate(Double.parseDouble(getParam("bedRate_" + id)));
		} catch ( Exception e ) {}
		db.commit();
		return path + "/empty.vm";
	}		


	@Command("listBuildings")
	public String listBuildings() throws Exception {
		String hostelId = getParam("hostelId");
		Hostel h = db.find(Hostel.class, hostelId);
		context.put("hostel", h);
		context.put("buildings", db.list("select b from Building b where b.hostel.id = '" + hostelId + "'"));
		return path + "/divBuilding.vm";
	}
	
	@Command("listBlocks")
	public String listBlocks() throws Exception {
		String buildingId = getParam("buildingId");
		Building h = db.find(Building.class, buildingId);
		context.put("building", h);
		context.put("hostel", h.getHostel());
		context.put("blocks", db.list("select b from Block b where b.building.id = '" + buildingId + "'"));
		return path + "/divBlock.vm";
	}	
	
	@Command("listFloors")
	public String listFloors() throws Exception {
		String blockId = getParam("blockId");
		Block h = db.find(Block.class, blockId);
		context.put("block", h);
		context.put("building", h.getBuilding());
		context.put("hostel", h.getBuilding().getHostel());
		context.put("floors", db.list("select b from Floor b where b.block.id = '" + blockId + "'"));
		return path + "/divFloor.vm";
	}	
	
	@Command("listUnits")
	public String listUnits() throws Exception {
		String floorId = getParam("floorId");
		Floor h = db.find(Floor.class, floorId);
		context.put("floor", h);
		context.put("block", h.getBlock());
		context.put("building", h.getBlock().getBuilding());
		context.put("hostel", h.getBlock().getBuilding().getHostel());
		context.put("units", db.list("select b from Unit b where b.floor.id = '" + floorId + "'"));
		return path + "/divUnit.vm";
	}	
	
	@Command("listRooms")
	public String listRooms() throws Exception {
		String unitId = getParam("unitId");
		Unit h = db.find(Unit.class, unitId);
		context.put("unit", h);
		context.put("floor", h.getFloor());
		context.put("block", h.getFloor().getBlock());
		context.put("building", h.getFloor().getBlock().getBuilding());
		context.put("hostel", h.getFloor().getBlock().getBuilding().getHostel());
		context.put("rooms", db.list("select b from educate.hostel.entity.Room b where b.unit.id = '" + unitId + "'"));
		
		context.put("roomTypes", db.list("select r from RoomType r order by r.sequence"));
		return path + "/divRoom.vm";
	}
	

	
	@Command("updateBuildingSequence")
	public String updateBuildingSequence() throws Exception {
		String hostelId = getParam("hostelId");
		Hostel hostel = db.find(Hostel.class, hostelId);
		context.put("hostel", hostel);
		String[] ids = request.getParameterValues("buildingIds");
		int i = 0;
		for ( String id : ids ) {
			i++;
			Building h = db.find(Building.class, id);
			db.begin();
			h.setSequence(i);
			db.commit();
		}
		context.put("buildings", db.list("select h from Building h where h.hostel.id = '" + hostelId + "' order by h.sequence"));
		return path + "/listBuildings.vm";
	}
	
	@Command("updateBlockSequence")
	public String updateBlockSequence() throws Exception {
		String buildingId = getParam("buildingId");
		Building building = db.find(Building.class, buildingId);
		context.put("building", building);
		String[] ids = request.getParameterValues("blockIds");
		int i = 0;
		for ( String id : ids ) {
			i++;
			Block h = db.find(Block.class, id);
			db.begin();
			h.setSequence(i);
			db.commit();
		}
		context.put("blocks", db.list("select h from Block h where h.building.id = '" + buildingId + "' order by h.sequence"));
		return path + "/listBlocks.vm";
	}	
	
	@Command("updateFloorSequence")
	public String updateFloorSequence() throws Exception {
		String blockId = getParam("blockId");
		Block block = db.find(Block.class, blockId);
		context.put("block", block);
		String[] ids = request.getParameterValues("floorIds");
		int i = 0;
		for ( String id : ids ) {
			i++;
			Floor h = db.find(Floor.class, id);
			db.begin();
			h.setSequence(i);
			db.commit();
		}
		context.put("floors", db.list("select h from Floor h where h.block.id = '" + blockId + "' order by h.sequence"));
		return path + "/listFloors.vm";
	}		
	
	@Command("updateUnitSequence")
	public String updateUnitSequence() throws Exception {
		String floorId = getParam("floorId");
		Floor floor = db.find(Floor.class, floorId);
		context.put("floor", floor);
		String[] ids = request.getParameterValues("unitIds");
		int i = 0;
		for ( String id : ids ) {
			i++;
			Unit h = db.find(Unit.class, id);
			db.begin();
			h.setSequence(i);
			db.commit();
		}
		context.put("units", db.list("select h from Unit h where h.floor.id = '" + floorId + "' order by h.sequence"));
		return path + "/listUnits.vm";
	}		
	
	@Command("updateRoomSequence")
	public String updateRoomSequence() throws Exception {
		String unitId = getParam("unitId");
		Unit unit = db.find(Unit.class, unitId);
		context.put("unit", unit);
		String[] ids = request.getParameterValues("roomIds");
		int i = 0;
		for ( String id : ids ) {
			i++;
			Room h = db.find(Room.class, id);
			db.begin();
			h.setSequence(i);
			db.commit();
		}
		context.put("rooms", db.list("select h from educate.hostel.entity.Room h where h.unit.id = '" + unitId + "' order by h.sequence"));
		return path + "/listRooms.vm";
	}	


	@Command("listRoomItems")
	public String listRoomItems() throws Exception {
		String roomId = getParam("roomId");
		Room room = db.find(Room.class, roomId);
		context.put("room", room);
		context.put("itemTypes", db.list("select t from ItemType t"));
		context.put("itemConditions", db.list("select c from ItemCondition c order by c.sequence"));
		List<HostelItem> items = db.list("select i from HostelItem i where i.room.id = '" + room.getId() + "' order by i.item.type.name");
		context.put("items", items);
		return path + "/listRoomItems.vm";
	}
	
	
	@Command("addRoomItem")
	public String addRoomItem() throws Exception {
		String roomId = getParam("roomId");
		Room room = db.find(Room.class, roomId);
		context.put("room", room);
		String itemTypeId = getParam("itemTypeId_" + roomId);
		ItemType itemType = db.find(ItemType.class, itemTypeId);
		
		String itemConditionId = getParam("itemConditionId_" + roomId);
		ItemCondition itemCondition = db.find(ItemCondition.class, itemConditionId);
		
		db.begin();
		Item item = new Item();
		item.setType(itemType);
		item.setDescription(getParam("itemDescription_" + roomId));
		item.setTag(getParam("itemTag_" + roomId));
		try {
			item.setValue(Double.parseDouble(getParam("itemValue_" + roomId)));
		} catch ( Exception e ) {
			item.setValue(0.0d);
		}
		db.persist(item);
		db.commit();
		
		db.begin();
		RoomItem roomItem = new RoomItem();
		roomItem.setDateIn(new Date());
		roomItem.setItem(item);
		roomItem.setRoom(room);
		roomItem.setUnit(null);
		roomItem.getItem().setItemCondition(itemCondition);
		room.getItems().add(roomItem);
		db.persist(roomItem);
		db.commit();
		context.put("itemTypes", db.list("select t from ItemType t"));
		context.put("itemConditions", db.list("select c from ItemCondition c order by c.sequence"));
		List<RoomItem> items = db.list("select i from RoomItem i where i.room.id = '" + room.getId() + "' order by i.item.type.name");
		context.put("items", items);
		return path + "/listRoomItems.vm";
	}
	
	@Command("saveRoomItem")
	public String saveRoomItem() throws Exception {
		String roomItemId = getParam("roomItemId");
		RoomItem roomItem = db.find(RoomItem.class, roomItemId);
		
		String typeId = getParam("itemTypeId_" + roomItemId);
		ItemType itemType = db.find(ItemType.class, typeId);
		
		String itemConditionId = getParam("itemConditionId_" + roomItemId);
		ItemCondition itemCondition = db.find(ItemCondition.class, itemConditionId);
		
		db.begin();
		roomItem.getItem().setType(itemType);
		roomItem.getItem().setDescription(getParam("itemDescription_" + roomItemId));
		roomItem.getItem().setTag(getParam("itemTag_" + roomItemId));
		roomItem.getItem().setItemCondition(itemCondition);
		try {
			roomItem.getItem().setValue(Double.parseDouble(getParam("itemValue_" + roomItemId)));
		} catch ( Exception e ) {}
		db.commit();
		context.put("roomItem", roomItem);
		return path + "/saveRoomItem.vm";
	}
	
	@Command("deleteRoomItem")
	public String deleteRoomItem() throws Exception {
		
		String roomItemId = getParam("roomItemId");
		RoomItem roomItem = db.find(RoomItem.class, roomItemId);
		Room room = roomItem.getRoom();
		context.put("room", room);
		
		db.begin();
		room.getItems().remove(roomItem);
		db.remove(roomItem);
		db.commit();
		
		context.put("itemTypes", db.list("select t from ItemType t"));
		context.put("itemConditions", db.list("select c from ItemCondition c order by c.sequence"));
		List<HostelItem> items = db.list("select i from HostelItem i where i.room.id = '" + room.getId() + "' order by i.item.type.name");
		context.put("items", items);
		return path + "/listRoomItems.vm";
	}
	
	@Command("moveRoomItem")
	public String moveRoomItem() throws Exception {
		
		String roomItemId = getParam("roomItemId");
		RoomItem roomItem = db.find(RoomItem.class, roomItemId);
		Room room = roomItem.getRoom();
		context.put("room", room);
		
		Mover mover = db.find(Mover.class, "0");
		db.begin();
		room.getItems().remove(roomItem);
		mover.getItems().add(roomItem);
		roomItem.setRoom(null);
		roomItem.setMover(mover);
		db.commit();
		
		context.put("itemTypes", db.list("select t from ItemType t"));
		context.put("itemConditions", db.list("select c from ItemCondition c order by c.sequence"));
		List<HostelItem> items = db.list("select i from HostelItem i where i.room.id = '" + room.getId() + "' order by i.item.type.name");
		context.put("items", items);
		return path + "/listRoomItems.vm";
	}	
	
	@Command("listMovedItems")
	public String listMovedItems() throws Exception {
		String roomId = getParam("roomId");
		Room room = db.find(Room.class, roomId);
		context.put("room", room);
		
		Mover mover = db.find(Mover.class, "0");
		context.put("movedItems", db.list("select i from HostelItem i where i.mover.id = '" + mover.getId() + "'"));
		
		context.put("itemTypes", db.list("select t from ItemType t"));
		return path + "/listMovedItems.vm";
	}
	
	@Command("addMovedItems")
	public String addMovedItems() throws Exception {
		String roomId = getParam("roomId");
		Room room = db.find(Room.class, roomId);
		context.put("room", room);
		
		String[] ids = request.getParameterValues("moveItemIds");
		if ( ids != null ) {
			
			for ( String id : ids ) {
				HostelItem item = db.find(HostelItem.class, id);
				db.begin();
				item.setMover(null);
				item.setRoom(room);
				item.setDateIn(new Date());
				db.commit();
			}
			
		}
		
		context.put("itemTypes", db.list("select t from ItemType t"));
		context.put("itemConditions", db.list("select c from ItemCondition c order by c.sequence"));
		List<HostelItem> items = db.list("select i from HostelItem i where i.room.id = '" + room.getId() + "' order by i.item.type.name");
		context.put("items", items);
		return path + "/listRoomItems.vm";
	}

}
