package educate.sis.module;

import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.hostel.entity.Block;
import educate.hostel.entity.Building;
import educate.hostel.entity.Floor;
import educate.hostel.entity.Hostel;
import educate.hostel.entity.Room;
import lebah.portal.action.AjaxModule;

public class HostelManagementModule  extends AjaxModule {
	
	String path = "apps/hostel/";
	private String vm = path + "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) listHostels();
		
		else if ( "list_hostel".equals(command)) listHostels();
		else if ( "add_hostel".equals(command)) addHostel();
		else if ( "get_hostel".equals(command)) getHostel();
		else if ( "update_hostel".equals(command)) updateHostel();
		
		else if ( "list_buildings".equals(command)) listBuildings("");
		else if ( "add_building".equals(command)) addBuilding();
		else if ( "get_building".equals(command)) getBuilding();
		else if ( "update_building".equals(command)) updateBuilding();
		
		else if ( "list_blocks".equals(command)) listBlocks();
		else if ( "add_block".equals(command)) addBlock();
		else if ( "get_block".equals(command)) getBlock();
		else if ( "update_block".equals(command)) updateBlock();
		
		else if ( "list_floors".equals(command)) listFloors();
		else if ( "add_floor".equals(command)) addFloor();
		else if ( "get_floor".equals(command)) getFloor();
		else if ( "update_floor".equals(command)) udpateFloor();
		
		else if ( "list_rooms".equals(command)) listRooms();
		else if ( "add_room".equals(command)) addRoom();
		else if ( "get_room".equals(command)) getRoom();
		else if ( "update_room".equals(command)) updateRoom();
		else if ( "room_info".equals(command)) roomInfo();
		else if ( "save_room".equals(command)) saveRoom();
		
		else if ( "delete_hostel".equals(command)) deleteHostel();
		else if ( "delete_building".equals(command)) deleteBuilding();
		
		else if ( "close".equals(command)) divClose();
		return vm;
	}
	
	private void saveRoom() throws Exception {
		vm = path + "room.vm";
		String roomId = request.getParameter("room_id");
		Room room = (Room) db.find(Room.class, roomId);
		context.put("room", room);
		
		String code = request.getParameter("code_" + room.getId());
		String name = request.getParameter("name_" + room.getId());
		String capacity_ = request.getParameter("capacity_" + room.getId());
		int capacity = 1;
		try {
			capacity = Integer.parseInt(capacity_);
		} catch ( Exception e ) {}
		db.begin();
		room.setCode(code);
		room.setName(name);
		room.setCapacity(capacity);
		db.commit();
	}
	
	private void roomInfo() {
		vm = path + "room_info.vm";
		String roomId = request.getParameter("room_id");
		Room room = (Room) db.find(Room.class, roomId);
		context.put("room", room);		
	}
	private void deleteBuilding() throws Exception {
		String buildingId = request.getParameter("building_id");
		Building building = (Building) db.find(Building.class, buildingId);
		Hostel hostel = building.getHostel();
		db.begin();
		hostel.removeBuilding(building);
		db.remove(building);
		db.commit();
		listBuildings(buildingId);
		
	}
	private void deleteHostel() throws Exception {

		String hostelId = request.getParameter("hostel_id");
		Hostel hostel = (Hostel) db.find(Hostel.class, hostelId);
		db.begin();
		db.remove(hostel);
		db.commit();
		
		listHostels();
	}
	private void divClose() {
		vm = path + "none.vm";
		
	}
	private void updateRoom() {
		String roomId = request.getParameter("room_id");
		Room room = (Room) db.find(Room.class, roomId);
		context.put("room", room);
		
		listRooms();
		
	}
	private void getRoom() {
		vm = path + "room.vm";
		String roomId = request.getParameter("room_id");
		Room room = (Room) db.find(Room.class, roomId);
		context.put("room", room);
	}
	private void addRoom() throws Exception {
		vm = path + "rooms.vm";
		String floorId = request.getParameter("floor_id");
		Floor floor = (Floor) db.find(Floor.class, floorId);
		
		String code = request.getParameter("code_" + floorId);
		String name = request.getParameter("name_" + floorId);
		
		db.begin();
		Room room = new Room();
		room.setCode(code);
		room.setName(name);
		//floor.addRoom(room);
		db.commit();

	}
	private void listRooms() {
		vm = path + "rooms.vm";
		String floorId = request.getParameter("floor_id");
		Floor floor = (Floor) db.find(Floor.class, floorId);
		context.put("floor", floor);
		
	}
	private void udpateFloor() throws Exception {
		vm = path + "floors.vm";
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		
		String floorId = request.getParameter("floor_id");
		Floor floor = (Floor) db.find(Floor.class, floorId);
		
		db.begin();
		floor.setCode(code);
		floor.setName(name);
		db.commit();
		
		listFloors();
		
	}
	private void getFloor() {
		vm = path + "floor.vm";
		String floorId = request.getParameter("floor_id");
		Floor floor = (Floor) db.find(Floor.class, floorId);
		context.put("floor", floor);
		
	}
	private void addFloor() throws Exception {
		vm = path + "floors.vm";
		String blockId = request.getParameter("block_id");
		Block block = (Block) db.find(Block.class, blockId);
		context.put("block", block);
		String code = request.getParameter("code_" + blockId);
		String name = request.getParameter("name_" + blockId);
		
		db.begin();
		Floor floor = new Floor();
		floor.setCode(code);
		floor.setName(name);
		block.addFloor(floor);
		db.commit();
		
		
	}
	private void listFloors() {
		vm = path + "floors.vm";
		String blockId = request.getParameter("block_id");
		Block block = (Block) db.find(Block.class, blockId);
		context.put("block", block);
		
	}
	private void updateBlock() throws Exception {
		String blockId = request.getParameter("block_id");
		Block block = (Block) db.find(Block.class, blockId);
		context.put("block", block);
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		db.begin();
		block.setCode(code);
		block.setName(name);
		db.commit();
		listBlocks();
		
	}
	private void getBlock() {
		vm = path + "block.vm";
		
		String blockId = request.getParameter("block_id");
		Block block = (Block) db.find(Block.class, blockId);
		context.put("block", block);
		
	}
	private void addBlock() throws Exception {
		vm = path + "blocks.vm";
		
		String buildingId = request.getParameter("building_id");
		Building building = (Building) db.find(Building.class, buildingId);
		context.put("building", building);
		
		String code = request.getParameter("code_" + buildingId);
		String name = request.getParameter("name_" + buildingId);
		
		db.begin();
		Block block = new Block();
		block.setCode(code);
		block.setName(name);
		building.addBlock(block);
		db.commit();
		
		
	}
	private void listBlocks() {
		vm = path + "blocks.vm";
		
		String buildingId = request.getParameter("building_id");
		Building building = (Building) db.find(Building.class, buildingId);
		context.put("building", building);
	
	}
	private void updateBuilding() throws Exception {
		
		String buildingId = request.getParameter("building_id");
		Building building = (Building) db.find(Building.class, buildingId);
		context.put("building", building);
		
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		
		db.begin();
		building.setCode(code);
		building.setName(name);
		db.commit();
		
		listBuildings(building.getHostel().getId());		
	}
	private void getBuilding() {
		vm = path + "building.vm";
		String buildingId = request.getParameter("building_id");
		Building building = (Building) db.find(Building.class, buildingId);
		context.put("building", building);
		
	}
	private void addBuilding() throws Exception {
		vm = path + "buildings.vm";
		String hostelId = request.getParameter("hostel_id");
		Hostel hostel = (Hostel) db.find(Hostel.class, hostelId);
		context.put("hostel", hostel);
		String code = request.getParameter("code_" + hostelId);
		String name = request.getParameter("name_" + hostelId);
		
		db.begin();
		Building building = new Building();
		building.setCode(code);
		building.setName(name);
		hostel.addBuilding(building);
		db.commit();
		
		
	}
	private void listBuildings(String id) {
		vm = path + "buildings.vm";
		String hostelId = request.getParameter("hostel_id");
		Hostel hostel = (Hostel) db.find(Hostel.class, hostelId != null ? hostelId : id);
		context.put("hostel", hostel);
	}
	private void updateHostel() throws Exception {
		String id = request.getParameter("hostel_id");
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		
		Hostel hostel = (Hostel) db.find(Hostel.class, id);
		db.begin();
		hostel.setCode(code);
		hostel.setName(name);
		db.commit();
		
		listHostels();

	}
	private void getHostel() {
		vm = path + "hostel.vm";
		String id = request.getParameter("hostel_id");
		Hostel hostel = (Hostel) db.find(Hostel.class, id);
		context.put("hostel", hostel);
		
	}
	private void addHostel() throws Exception {
		vm = path + "hostels.vm";
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		
		db.begin();
		Hostel hostel = new Hostel();
		hostel.setCode(code);
		hostel.setName(name);
		db.persist(hostel);
		db.commit();
		
		listHostels();
		
	}
	private void listHostels() {
		vm = path + "hostels.vm";
		String sql ="";
		sql = "select h from Hostel h order by h.name";
		List<Hostel> hostels = db.list(sql);
		context.put("hostels", hostels);
	}

}
