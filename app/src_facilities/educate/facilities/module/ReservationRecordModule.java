package educate.facilities.module;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.facilities.entity.LoanEquipment;
import educate.facilities.entity.Reservation;
import educate.timetabling.entity.Room;
import lebah.portal.action.Command;
import lebah.portal.db.User;
import lebah.portal.db.UserData;
import lebah.template.LebahRecordTemplateModule;
import lebah.template.OperatorEqualTo;

public class ReservationRecordModule extends LebahRecordTemplateModule<Reservation> {

	@Override
	public Class getIdType() {
		return String.class;
	}

	@Override
	public void afterSave(Reservation r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void begin() {
		context.put("path", getPath());
		this.setReadonly(true);
		this.setOrderBy("dateCreate");
		this.setOrderType("DESC");
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy hh:mm a"));
		
		List<Room> rooms = db.list("select r from educate.timetabling.entity.Room r where r.combined = 0 order by r.sequence");
		context.put("rooms", rooms);
	}

	@Override
	public boolean delete(Reservation a) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getPath() {
		return "facilities/reservation_records";
	}

	@Override
	public Class<Reservation> getPersistenceClass() {
		return Reservation.class;
	}

	@Override
	public void getRelatedData(Reservation r) {
		try {
			User user = UserData.getUser(r.getUserId());
			context.put("userName", user.getName());
		} catch (Exception e) {
			context.remove("userName");
			e.printStackTrace();
		}
		
	}

	@Override
	public void save(Reservation r) throws Exception {

	}

	@Override
	public Map<String, Object> searchCriteria() throws Exception {
		Map map = new HashMap();
		map.put("userId", new OperatorEqualTo(getParam("findUserId")));
		map.put("room.id", new OperatorEqualTo(getParam("findRoomId")));
		return map;
	}
	
	@Command("approveReservation")
	public String approveReservation() throws Exception {
		String reservationId = getParam("reservationId");
		Reservation r = db.find(Reservation.class, reservationId);
		db.begin();
		r.setStatus("approved");
		db.commit();
		return getPath() + "/empty.vm";
	}

	@Command("disApproveReservation")
	public String disApproveReservation() throws Exception {
		String reservationId = getParam("reservationId");
		Reservation r = db.find(Reservation.class, reservationId);
		db.begin();
		r.setStatus("disapproved");
		db.commit();
		
		return getPath() + "/empty.vm";
	}

	@Command("pendingReservation")
	public String pendingReservation() throws Exception {
		String reservationId = getParam("reservationId");
		Reservation r = db.find(Reservation.class, reservationId);
		db.begin();
		r.setStatus("pending");
		db.commit();

		return getPath() + "/empty.vm";
	}
	
	@Command("approveLoanEquipment")
	public String approveLoanEquipment() throws Exception {
		String loanEquipmentId = getParam("loanEquipmentId");
		LoanEquipment e = db.find(LoanEquipment.class, loanEquipmentId);
		db.begin();
		e.setStatus("approved");
		db.commit();
		return getPath() + "/empty.vm";
	}

	@Command("disApproveLoanEquipment")
	public String disApproveLoanEquipment() throws Exception {
		String loanEquipmentId = getParam("loanEquipmentId");
		LoanEquipment e = db.find(LoanEquipment.class, loanEquipmentId);
		db.begin();
		e.setStatus("disapproved");
		db.commit();
		return getPath() + "/empty.vm";
	}

	@Command("pendingLoanEquipment")
	public String pendingLoanEquipment() throws Exception {
		String loanEquipmentId = getParam("loanEquipmentId");
		LoanEquipment e = db.find(LoanEquipment.class, loanEquipmentId);
		db.begin();
		e.setStatus("pending");
		db.commit();
		return getPath() + "/empty.vm";
	}
	

}
