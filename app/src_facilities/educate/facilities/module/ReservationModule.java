package educate.facilities.module;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.facilities.entity.Asset;
import educate.facilities.entity.LoanEquipment;
import educate.facilities.entity.Reservation;
import educate.timetabling.entity.Room;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import lebah.portal.db.User;
import lebah.portal.db.UserData;

public class ReservationModule extends LebahModule {
	
	DbPersistence db = new DbPersistence();
	String path = "facilities/venue_reservation";
	String userId = "";
	String role;
	
	public void preProcess() {
		userId = (String) request.getSession().getAttribute("_portal_login");
		role = (String) request.getSession().getAttribute("_portal_role");
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy hh:mm a"));
	}

	@Override
	public String start() {
		try {
			User user = UserData.getUser(userId);
			context.put("userName", user.getName());
			context.put("userId", userId);
		} catch (Exception e) {
			context.remove("userName");
			e.printStackTrace();
		}
		List<Room> rooms = db.list("select r from educate.timetabling.entity.Room r order by r.sequence");
		context.put("rooms", rooms);
		List<Asset> items = db.list("select a from Asset a order by a.category.sequence");
		context.put("items", items);
		request.getSession().setAttribute("_item_list", new ArrayList<Asset>());
		return path + "/start.vm";
	}
	
	@Command("addItem")
	public String addItem() throws Exception {
		String itemId = getParam("itemId");
		Asset asset = db.find(Asset.class, itemId);
		context.put("asset", asset);
		
		List<Asset> itemList = (List<Asset>) request.getSession().getAttribute("_item_list");
		if ( itemList == null ) {
			itemList = new ArrayList<Asset>();
			request.getSession().setAttribute("_item_list", itemList);
		}
		boolean found = false;
		for ( Asset a : itemList ) {
			if ( a.getId().equals(asset.getId())) {
				found = true;
				break;
			}
		}
		if ( !found ) itemList.add(asset);
		
		context.put("itemList", itemList);
		return path + "/itemList.vm";
	}
	
	@Command("removeItem")
	public String removeItem() throws Exception {
		String itemId = getParam("removeItemId");
		List<Asset> itemList = (List<Asset>) request.getSession().getAttribute("_item_list");
		if ( itemList == null ) {
			itemList = new ArrayList<Asset>();
			request.getSession().setAttribute("_item_list", itemList);
		}
		boolean found = false;
		int i = 0;
		for ( Asset a : itemList ) {
			if ( itemId.equals(a.getId())) {
				found = true;
				break;
			}
			i++;
		}
		if ( found ) {
			itemList.remove(i);
		}
		context.put("itemList", itemList);
		return path + "/itemList.vm";
	}
	
	@Command("getRoomList")
	public String getRoomList() throws Exception {
		List<Room> rooms = db.list("select r from educate.timetabling.entity.Room r where r.combined = 0 order by r.sequence");
		context.put("rooms", rooms);
		return path + "/roomList.vm";
	}
	
	@Command("getItemList")
	public String getItemList() throws Exception {
		List<Asset> items = db.list("select a from Asset a where a.loanable = 1 order by a.category.sequence");
		context.put("items", items);
		return path + "/equipmentList.vm";
	}	
	
	@Command("chooseEquipments")
	public String chooseEquipments() throws Exception {
		
//		List<Asset> itemList = (List<Asset>) request.getSession().getAttribute("_item_list");
//		if ( itemList == null ) {
//			itemList = new ArrayList<Asset>();
//			request.getSession().setAttribute("_item_list", itemList);
//		}
		List<Asset> itemList = new ArrayList<Asset>();
		request.getSession().setAttribute("_item_list", itemList);
		context.put("itemList", itemList);
		String[] itemIds = request.getParameterValues("itemIds");
		if ( itemIds != null ) {
			for ( String itemId : itemIds ) {
				
				boolean found = false;
				for ( Asset a : itemList ) {
					if ( a.getId().equals(itemId)) {
						found = true;
						break;
					}
				}
				if ( !found ) {
					Asset asset = db.find(Asset.class, itemId);
					itemList.add(asset);
				}
			}
		}
		
		return path + "/itemList.vm";
	}
	
	@Command("reservation")
	public String reservation() throws Exception {
		String borrowerEmail = getParam("borrowerEmail");
		String borrowerContactNo = getParam("borrowerContactNo");
		String programmeName = getParam("programmeName");
		String roomId = getParam("roomId");
		String dateFrom = getParam("dateFrom");
		String hourFrom = getParam("hourFrom");
		String minFrom = getParam("minFrom");
		String dateTo = getParam("dateTo");
		String hourTo = getParam("hourTo");
		String minTo = getParam("minTo");
		
		String _date1 = dateFrom + " " + hourFrom + ":" + minFrom;
		String _date2 = dateTo + " " + hourTo + ":" + minTo;
		Date date1 = new SimpleDateFormat("dd-MM-yyyy H:m").parse(_date1);
		Date date2 = new SimpleDateFormat("dd-MM-yyyy H:m").parse(_date2);
		
		Room room = db.find(Room.class, roomId);
		List<Asset> itemList = (List<Asset>) request.getSession().getAttribute("_item_list");

		
		db.begin();
		Reservation r = new Reservation();
		r.setUserId(userId);
		r.setContactNo(borrowerContactNo);
		r.setEmail(borrowerEmail);
		r.setProgrammeName(programmeName);
		r.setDateFrom(date1);
		r.setDateTo(date2);
		r.setDateCreate(new Date());
		r.setRoom(room);
		
		if ( itemList != null ) {
			for ( Asset a : itemList ) {
				LoanEquipment e = new LoanEquipment();
				e.setAsset(a);
				e.setReservation(r);
				e.setStatus("pending");
				r.getEquipments().add(e);
			}
		}
		
		r.setStatus("pending");
		db.persist(r);
		db.commit();
		
		context.put("reservation", r);
		
		return path + "/reservation.vm";
	}
	
	@Command("getReservation")
	public String getReservation() throws Exception {
		String reservationId = getParam("reservationId");
		Reservation r = db.find(Reservation.class, reservationId);
		context.put("reservation", r);
		return path + "/reservation.vm";
	}
	
	
	public static void main(String[] args) throws Exception {
		String d = "08-09-2013 16:22";
		
		Date date = new SimpleDateFormat("dd-MM-yyyy H:m").parse(d);
		System.out.println(new SimpleDateFormat("MMM dd, yyyy hh:mm a").format(date));
	}

}
