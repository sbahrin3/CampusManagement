package educate.sis.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import educate.hostel.entity.Room;

public class OccupiedRoomSlot {

	private Room room;
	private List<Date> days;
	private List<Occupier> occupiers;
	
	//optional
	private int year;
	private int month;
	
	public OccupiedRoomSlot() {
		
	}
	
	public OccupiedRoomSlot(Room room) {
		this.room = room;
	}

	public List<Date> getDays() {
		return days;
	}

	public void setDays(List<Date> days) {
		this.days = days;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	public void addDay(Date date) {
		if ( days == null ) days = new ArrayList<Date>();
		days.add(date);
	}

	public List<Occupier> getOccupiers() {
		return occupiers;
	}

	public void setOccupiers(List<Occupier> occupiers) {
		this.occupiers = occupiers;
	}

	public void addOccupier(Occupier occupier) {
		if ( occupiers == null ) occupiers = new ArrayList<Occupier>();
		occupiers.add(occupier);
	}
	
	
	
	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Occupier getOccupant(int year, int month, int day) {
		Date date = new GregorianCalendar(year, month, day).getTime(); 
		if ( occupiers != null ) {
			for ( Occupier o : occupiers ) {
				if ( date.equals(o.getDate())) {
					return o;
				}
			}
		}
		return null;
	}
	
	public Occupier getOccupant(int day) {
		return getOccupant(year, month, day);
	}
	
	public List<Occupier> getOccupants(int year, int month, int day) {
		List<Occupier> list = new ArrayList<Occupier>(); 
		Date date = new GregorianCalendar(year, month, day).getTime(); 
		if ( occupiers != null ) {
			for ( Occupier o : occupiers ) {
				if ( date.equals(o.getDate())) {
					list.add(o);
				}
			}
		}
		return list;
	}
	
	public List<Occupier> getOccupants(int day) {
		return getOccupants(year, month, day);
	}
	
}
