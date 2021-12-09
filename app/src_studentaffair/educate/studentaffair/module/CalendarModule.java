package educate.studentaffair.module;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.studentaffair.entity.ClubActivity;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class CalendarModule extends LebahModule {
	
	private String path = "studentaffair/calendar";
	private DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		try {
			getCalendar(2013, 11);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path + "/start.vm";
	}
	
	@Command("getCalendar")
	public String getNextCalendar() throws Exception {
		String direction = getParam("direction");
		String _month = getParam("month");
		String _year = getParam("year");
		int month = Integer.parseInt(_month);
		int year = Integer.parseInt(_year);
		if ( "next".equals(direction)) {
			month++;
			if ( month == 13 ) {
				month = 1;
				year++;
			}
		} 
		else if ( "previous".equals(direction)) {
			month--;
			if ( month == 0 ) {
				month = 12;
				year--;
			}
		}
		getCalendar(year, month);
		return path + "/calendar.vm";
	}
	
	public void getCalendar(int year, int month) throws Exception {
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, 1);
		c.set(Calendar.MONTH, month-1);
		c.set(Calendar.YEAR, year);
		
		int weekDay = c.get(Calendar.DAY_OF_WEEK);
		context.put("weekDay", weekDay);
		
		int maxDay = c.getMaximum(Calendar.DAY_OF_MONTH);
		context.put("maxDay", maxDay);
		context.put("calendar", new CalendarValue(year, month, 1));
		
		Map<Integer, List<Event>> events = new HashMap<Integer, List<Event>>();
		context.put("events", events);
		Hashtable h = new Hashtable();
		Calendar ac = Calendar.getInstance();
		ac.set(Calendar.MONDAY, month-1);
		ac.set(Calendar.YEAR, year);
		for ( int i = 1; i < maxDay+1; i++ ) {
			ac.set(Calendar.DAY_OF_MONTH, i);
			h.put("date", ac.getTime());
			List<ClubActivity> activities = db.list("select c from ClubActivity c where c.proposalStatus = 'approve' and :date between c.startDate and c.endDate2", h);
			List<Event> el = new ArrayList<Event>();
			for ( ClubActivity a : activities ) {
				Event ev = new Event();
				ev.setName(a.getName());
				ev.setDescription(a.getDescription());
				el.add(ev);
			}
			events.put(new Integer(i), el);
		}
		
	}
	
	public static void main(String[] args) throws Exception {

		int month = 11;
		int year = 2013;
		DbPersistence db = new DbPersistence();
		Map<Integer, List<Event>> events = new HashMap<Integer, List<Event>>();
		Hashtable h = new Hashtable();
		Calendar ac = Calendar.getInstance();
		ac.set(Calendar.MONDAY, month-1);
		ac.set(Calendar.YEAR, year);
		for ( int i = 1; i < 32; i++ ) {
			ac.set(Calendar.DAY_OF_MONTH, i);
			h.put("date", ac.getTime());
			List<ClubActivity> activities = db.list("select c from ClubActivity c where :date between c.startDate and c.endDate2", h);
			List<Event> el = new ArrayList<Event>();
			for ( ClubActivity a : activities ) {
				Event ev = new Event();
				ev.setName(a.getName());
				ev.setDescription(a.getDescription());
				el.add(ev);
				
				System.out.println(i + ". " + ev.getName());
			}

			events.put(new Integer(i), el);
			
			
		}
		
		

		
	}

}
