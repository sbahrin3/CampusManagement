package educate.sis.module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.db.DbPersistence;
import educate.hostel.entity.Block;
import educate.hostel.entity.Building;
import educate.hostel.entity.Floor;
import educate.hostel.entity.Hostel;
import educate.hostel.entity.Room;
import educate.hostel.entity.RoomAllocation;
import lebah.portal.action.AjaxModule;

public class HostelAdministrationModule extends AjaxModule {
	
	String path = "apps/hostel_admin/";
	private String vm = path + "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	public String doAction() throws Exception {
		
		context.put("_formName", formName);
		session = request.getSession();
		String command = request.getParameter("command");
		System.out.println(command);
		if ( command == null || "".equals(command)) start();
		else if ( "select_hostel".equals(command)) selectHostel();
		else if ( "select_list".equals(command)) selectList();
		else if ( "update_hostel".equals(command)) updateHostel();
		
		else if ( "list_buildings".equals(command)) listBuildings();
		else if ( "new_building".equals(command)) newBuilding();
		else if ( "add_building".equals(command)) addBuilding();
		else if ( "edit_building".equals(command)) editBuilding();
		else if ( "update_building".equals(command)) updateBuilding();
		else if ( "delete_building".equals(command)) deleteBuilding();
		
		else if ( "list_blocks".equals(command)) listBlocks();
		else if ( "new_block".equals(command)) newBlock();
		else if ( "add_block".equals(command)) addBlock();
		else if ( "edit_block".equals(command)) editBlock();
		else if ( "update_block".equals(command)) updateBlock();
		else if ( "delete_block".equals(command)) deleteBlock();
		
		else if ( "list_floors".equals(command)) listFloors();
		else if ( "new_floor".equals(command)) newFloor();
		else if ( "add_floor".equals(command)) addFloor();
		else if ( "edit_floor".equals(command)) editFloor();
		else if ( "update_floor".equals(command)) updateFloor();
		else if ( "delete_floor".equals(command)) deleteFloor();
		
		else if ( "save_floor_image".equals(command)) saveFloorImage();
		
		else if ( "list_rooms".equals(command)) listRooms();
		else if ( "new_room".equals(command)) newRoom();
		else if ( "add_room".equals(command)) addRoom();
		else if ( "edit_room".equals(command)) editRoom();
		else if ( "update_room".equals(command)) updateRoom();
		else if ( "delete_room".equals(command)) deleteRoom();
		
		else if ( "new_hostel".equals(command)) newHostel();
		else if ( "add_hostel".equals(command)) addHostel();
		else if ( "list_hostels".equals(command)) listHostels();
		 

		return vm;
	}
	
	private void saveFloorImage() throws Exception {
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		String documentId = getParam("documentId");
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
		for (FileItem item : files) {
			if ("uploadImage".equals(documentId)) {
				String floorId = getParam("floorId");
				if (!"".equals(floorId)) {
					db.begin();
					Floor floor = db.find(Floor.class, floorId);
					floor.setFloorPlanImage(item.get());
					floor.setFloorPlanImageId(lebah.db.UniqueID.getUID());
					db.commit();
				}
				break;
			}
		}
	}

	private void listHostels() {
		List<Hostel> hostels = db.list("select h from Hostel h order by h.name");
		context.put("hostels", hostels);
		
		vm = path + "select_hostels.vm";
		
	}

	private void addHostel() throws Exception {
		
		String code = request.getParameter("new_hostel_code");
		String name = request.getParameter("new_hostel_name");
		
		Hostel hostel = new Hostel();
		hostel.setCode(code);
		hostel.setName(name);
		
		db.begin();
		db.persist(hostel);
		db.commit();
		
		List<Hostel> hostels = db.list("select h from Hostel h order by h.name");
		context.put("hostels", hostels);
		
		vm = path + "select_hostels.vm";
		
	}

	private void newHostel() {
		vm = path + "new_hostel.vm";
		
	}

	private void deleteRoom() throws Exception {
		vm = path + "list_rooms.vm";
		
		String floorId = request.getParameter("floor_id");
		Floor floor = (Floor) db.find(Floor.class, floorId);
		context.put("floor", floor);
		
		String roomId = request.getParameter("room_id");
		Room room = (Room) db.find(Room.class, roomId);
		
		//check whether this room in use or not
		List<RoomAllocation> rooms = db.list("select a from RoomAllocation a where a.room.id = '" + room.getId() + "'");
		if ( rooms.size() == 0 ) {
			db.begin();
			db.remove(room);
			db.commit();
		}
		else {
			System.out.println("Can't remove this room!");
		}
		
	}

	private void updateRoom() throws Exception {
		vm = path + "list_rooms_item.vm";
		String roomId = request.getParameter("room_id");
		Room room = (Room) db.find(Room.class, roomId);
		context.put("room", room);
		int cnt = Integer.parseInt(request.getParameter("cnt"));
		context.put("cnt", cnt);
		
		String code = request.getParameter("room_code");
		String name = request.getParameter("room_name");
		
		db.begin();
		room.setCode(code);
		room.setName(name);
		db.commit();
		
	}

	private void editRoom() {
		vm = path + "list_rooms_item_edit.vm";
		String roomId = request.getParameter("room_id");
		Room room = (Room) db.find(Room.class, roomId);
		context.put("room", room);
		int cnt = Integer.parseInt(request.getParameter("cnt"));
		context.put("cnt", cnt);
		
	}

	private void addRoom() throws Exception {
		vm = path + "list_rooms.vm";
		String floorId = request.getParameter("floor_id");
		Floor floor = (Floor) db.find(Floor.class, floorId);
		String code = request.getParameter("room_code");
		String name = request.getParameter("room_name");
		Room room = new Room();
		room.setCode(code);
		room.setName(name);
		
		db.begin();
		//floor.addRoom(room);
		db.commit();

		context.put("floor", floor);
		
	}

	private void newRoom() {
		vm = path + "new_room.vm";
		
	}

	private void listRooms() {
		vm = path + "list_rooms.vm";
		String floorId = request.getParameter("floor_id");
		Floor floor = (Floor) db.find(Floor.class, floorId);
		context.put("floor", floor);
		
	}


	
	private void deleteFloor() throws Exception {
		vm = path + "list_floors.vm";
		
		String blockId = request.getParameter("block_id");
		Block block = (Block) db.find(Block.class, blockId);
		context.put("block", block);
		
		String floorId = request.getParameter("floor_id");
		Floor floor = (Floor) db.find(Floor.class, floorId);
//		
//		if ( floor.getRooms().size() == 0 ) {
//			db.begin();
//			block.removeFloor(floor);
//			db.remove(floor);
//			db.commit();
//		}
		
	}

	private void updateFloor() throws Exception {
		vm = path + "list_floors_item.vm";
		String floorId = request.getParameter("floor_id");
		Floor floor = (Floor) db.find(Floor.class, floorId);
		context.put("floor", floor);
		int cnt = Integer.parseInt(request.getParameter("cnt"));
		context.put("cnt", cnt);
		
		String code = request.getParameter("floor_code");
		String name = request.getParameter("floor_name");
		
		db.begin();
		floor.setCode(code);
		floor.setName(name);
		db.commit();
		
	}

	private void editFloor() {
		vm = path + "list_floors_item_edit.vm";
		String floorId = request.getParameter("floor_id");
		Floor floor = (Floor) db.find(Floor.class, floorId);
		context.put("floor", floor);
		int cnt = Integer.parseInt(request.getParameter("cnt"));
		context.put("cnt", cnt);
		
	}

	private void addFloor() throws Exception {
		vm = path + "list_floors.vm";
		String blockId = request.getParameter("block_id");
		Block block = (Block) db.find(Block.class, blockId);
		String code = request.getParameter("floor_code");
		String name = request.getParameter("floor_name");
		Floor floor = new Floor();
		floor.setCode(code);
		floor.setName(name);
		
		db.begin();
		block.addFloor(floor);
		db.commit();

		context.put("block", block);
		
	}

	private void newFloor() {
		vm = path + "new_floor.vm";
		
	}

	private void listFloors() {
		vm = path + "list_floors.vm";
		String blockId = request.getParameter("block_id");
		Block block = (Block) db.find(Block.class, blockId);
		context.put("block", block);
		
	}


	private void deleteBlock() throws Exception {
		vm = path + "list_blocks.vm";
		
		String buildingId = request.getParameter("building_id");
		Building building = (Building) db.find(Building.class, buildingId);
		context.put("building", building);
		
		String blockId = request.getParameter("block_id");
		Block block = (Block) db.find(Block.class, blockId);
		
		if ( block.getFloors().size() == 0 ) {
			db.begin();
			building.removeBlock(block);
			db.remove(block);
			db.commit();
		}
		
	}

	private void updateBlock() throws Exception {
		vm = path + "list_blocks_item.vm";
		String blockId = request.getParameter("block_id");
		Block block = (Block) db.find(Block.class, blockId);
		context.put("block", block);
		int cnt = Integer.parseInt(request.getParameter("cnt"));
		context.put("cnt", cnt);
		
		String code = request.getParameter("block_code");
		String name = request.getParameter("block_name");
		
		db.begin();
		block.setCode(code);
		block.setName(name);
		db.commit();
		
	}

	private void editBlock() {
		vm = path + "list_blocks_item_edit.vm";
		String blockId = request.getParameter("block_id");
		Block block = (Block) db.find(Block.class, blockId);
		context.put("block", block);
		int cnt = Integer.parseInt(request.getParameter("cnt"));
		context.put("cnt", cnt);
		
	}

	private void addBlock() throws Exception {
		vm = path + "list_blocks.vm";
		String buildingId = request.getParameter("building_id");
		Building building = (Building) db.find(Building.class, buildingId);
		String code = request.getParameter("block_code");
		String name = request.getParameter("block_name");
		Block block = new Block();
		block.setCode(code);
		block.setName(name);
		
		db.begin();
		building.addBlock(block);
		db.commit();

		context.put("building", building);
		
	}

	private void newBlock() {
		vm = path + "new_block.vm";
		
	}

	private void listBlocks() {
		vm = path + "list_blocks.vm";
		String buildingId = request.getParameter("building_id");
		Building building = (Building) db.find(Building.class, buildingId);
		context.put("building", building);
		
	}

	private void deleteBuilding() throws Exception {
		vm = path + "list_buildings.vm";
		
		String hostelId = request.getParameter("hostel_id");
		Hostel hostel = (Hostel) db.find(Hostel.class, hostelId);
		context.put("hostel", hostel);
		
		String buildingId = request.getParameter("building_id");
		Building building = (Building) db.find(Building.class, buildingId);
		
		if ( building.getBlocks().size() == 0 ) {
			db.begin();
			hostel.removeBuilding(building);
			db.remove(building);
			db.commit();
		}

		context.put("hostel", hostel);
	}

	private void updateBuilding() throws Exception {
		vm = path + "list_buildings_item.vm";
		String buildingId = request.getParameter("building_id");
		Building building = (Building) db.find(Building.class, buildingId);
		context.put("building", building);
		int cnt = Integer.parseInt(request.getParameter("cnt"));
		context.put("cnt", cnt);
		
		String code = request.getParameter("building_code");
		String name = request.getParameter("building_name");
		
		db.begin();
		building.setCode(code);
		building.setName(name);
		db.commit();
		
	}

	private void editBuilding() {
		vm = path + "list_buildings_item_edit.vm";
		String buildingId = request.getParameter("building_id");
		Building building = (Building) db.find(Building.class, buildingId);
		context.put("building", building);
		int cnt = Integer.parseInt(request.getParameter("cnt"));
		context.put("cnt", cnt);
	}

	private void addBuilding() throws Exception {
		vm = path + "list_buildings.vm";
		String hostelId = request.getParameter("hostel_id");
		Hostel hostel = (Hostel) db.find(Hostel.class, hostelId);
		String code = request.getParameter("building_code");
		String name = request.getParameter("building_name");
		Building building = new Building();
		building.setCode(code);
		building.setName(name);
		
		db.begin();
		hostel.addBuilding(building);
		db.commit();

		context.put("hostel", hostel);
		
	}

	private void newBuilding() {
		vm = path + "new_building.vm";
	}

	private void updateHostel() throws Exception {
		vm = path + "hostel.vm";
		String hostelId = request.getParameter("hostel_edit_id");
		Hostel hostel = (Hostel) db.find(Hostel.class, hostelId);
		context.put("hostel", hostel);
		String name = request.getParameter("hostel_name");
		db.begin();
		hostel.setName(name);
		db.commit();
		
	}

	private void selectList() {
		vm = path + "select_hostels.vm";
		List<Hostel> hostels = db.list("select h from Hostel h order by h.name");
		context.put("hostels", hostels);
		
	}

	private void selectHostel() {
		vm = path + "hostel.vm";
		String hostelId = request.getParameter("select_hostel_id");
		Hostel hostel = (Hostel) db.find(Hostel.class, hostelId);
		context.put("hostel", hostel);
		
	}

	private void listBuildings() {
		vm = path + "list_buildings.vm";
		String hostelId = request.getParameter("hostel_id");
		Hostel hostel = (Hostel) db.find(Hostel.class, hostelId);
		context.put("hostel", hostel);
	}

	private void start() {
		vm = path + "start.vm";
		List<Hostel> hostels = db.list("select h from Hostel h order by h.name");
		context.put("hostels", hostels);
		
	}

}
