package educate.hostel.module;

import educate.db.DbPersistence;
import educate.hostel.entity.RoomType;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class RoomTypeModule extends LebahModule {
	
	private String path = "apps/hostel_type";
	private DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		context.put("roomTypes", db.list("select c from RoomType c order by c.sequence"));
		return path + "/start.vm";
	}
	
	@Command("updateSequence")
	public String updateSequence() throws Exception {
		String[] ids = request.getParameterValues("roomIds");
		int i = 0;
		for ( String id : ids ) {
			i++;
			RoomType c = db.find(RoomType.class, id);
			db.begin();
			c.setSequence(i);
			db.commit();
		}
		context.put("roomTypes", db.list("select c from RoomType c order by c.sequence"));
		return path + "/listRoomTypes.vm";
	}
	
	@Command("addRoom")
	public String addRoom() throws Exception {
		Integer seq = (Integer) db.get("select max(c.sequence) from RoomType c");
		int sequence = seq != null ? seq.intValue() + 1 : 1;
		db.begin();
		RoomType c = new RoomType();
		c.setName(getParam("name"));
		c.setSequence(sequence);
		db.persist(c);
		db.commit();
		context.put("roomTypes", db.list("select c from RoomType c order by c.sequence"));
		return path + "/listRoomTypes.vm";
	}
	

	@Command("changeName")
	public String changeName() throws Exception {
		String roomId = getParam("roomId");
		RoomType room = db.find(RoomType.class, roomId);
		db.begin();
		room.setName(getParam("name_" + roomId));
		db.commit();
		return path + "/empty.vm";
	}	
	 
	@Command("deleteRoom")
	public String deleteRoom() throws Exception {
		String roomId = getParam("roomId");
		RoomType room = db.find(RoomType.class, roomId);
		db.begin();
		db.remove(room);
		db.commit();
		context.put("roomTypes", db.list("select c from RoomType c order by c.sequence"));
		return path + "/listRoomTypes.vm";
	}


}