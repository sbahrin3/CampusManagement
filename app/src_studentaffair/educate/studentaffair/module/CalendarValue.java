package educate.studentaffair.module;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarValue {
	
	private int year;
	private int month;
	private int day;
	
	private List<Event> events = new ArrayList<Event>();
	
	public CalendarValue() {
		
	}
	
	public CalendarValue(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getTotalDays() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, day);
		c.set(Calendar.MONTH, month-1);
		c.set(Calendar.YEAR, year);
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	public int getWeekday() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, day);
		c.set(Calendar.MONTH, month-1);
		c.set(Calendar.YEAR, year);
		return c.get(Calendar.DAY_OF_WEEK);
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
	
	public String getMonthName() {
		switch (month) {
			case 1: return "JANUARY";
			case 2: return "FEBRUARY";
			case 3: return "MARCH";
			case 4: return "APRIL";
			case 5: return "MAY";
			case 6: return "JUNE";
			case 7: return "JULY";
			case 8: return "AUGUST";
			case 9: return "SEPTEMBER";
			case 10: return "OCTOBER";
			case 11: return "NOVEMBER";
			case 12: return "DECEMBER";
		}
		return "";
	}
	
	public static void main(String[] args) {
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, 1);
		c.set(Calendar.MONTH, 10);
		c.set(Calendar.YEAR, 2013);
		System.out.println(c.getTime());
		System.out.println(c.getActualMaximum(Calendar.DAY_OF_MONTH));
		
	}

}
