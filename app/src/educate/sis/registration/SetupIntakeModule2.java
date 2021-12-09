package educate.sis.registration;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.general.entity.GradsLevel;
import educate.sis.struct.SessionBean;
import educate.sis.struct.entity.Session;
import educate.util.SessionPathUtil;
import lebah.portal.action.AjaxModule;
import lebah.util.DateUtil;

public class SetupIntakeModule2 extends AjaxModule {
	
	private static String[] month_name = {"January", "February", "March", "April", "May", "Jun", "July", "August", "September", "October", "November", "December"};
	private static String[] day_name = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	private static String[] hour_name = {"12 AM", "1 AM", "2 AM", "3 AM", "4 AM", "5 AM", "6 AM", "7 AM", "8 AM", "9 AM", "10 AM", "11 AM",
														"12 PM", "1 PM", "2 PM", "3 PM", "4 PM", "5 PM", "6 PM", "7 PM", "8 PM", "9 PM", "10 PM", "11 PM"};

	
	String path = "apps/setup_intake/";
	private String vm = "default.vm";
	HttpSession session;
	private DbPersistence db = new DbPersistence();
	
	protected boolean modeAdmin = false;

	@Override
	public String doAction() throws Exception {
		
		context.put("modeAdmin", modeAdmin);
		
		context.put("UtilDate", new DateUtil());
		context.put("month_name", month_name);
		
		context.remove("date_overlap_with_session");
		context.remove("error_number");
		
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		
		context.put("direction", "asc");
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "list_sessions".equals(command)) listSessions();
		else if ( "add_session".equals(command)) addSession();
		else if ( "update_session".equals(command)) updateSession();
		else if ( "remove_session".equals(command)) removeSession();
		else if ( "editPathName".equals(command)) editPathName();
		else if ( "savePathName".equals(command)) savePathName();
		else if ( "copyPath".equals(command)) copyPath();
		else if ( "removeSessions".equals(command)) removeSessions();
		return vm;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	private void removeSessions() throws Exception {
		String pathTo = getParam("path_no");
		
		SessionPathUtil.clean(db, pathTo);
		
		vm = path + "removeSessions.vm";
		
	}

	/**
	 * @throws Exception 
	 * 
	 */
	private void copyPath() throws Exception {
		String pathFrom = getParam("pathFrom");
		String pathTo = getParam("pathTo");
		
		SessionPathUtil.clean(db, pathTo);
		SessionPathUtil.copy(db, pathFrom, pathTo); 
		
		vm = path + "copyPath.vm";
		
	}

	private void savePathName() throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("savePathName:");
		
		String pathNo = getParam("pathNo");
		context.put("path_no", Integer.parseInt(pathNo));
		if ( !"".equals(getParam("pathName")) ) {
			GradsLevel g = db.find(GradsLevel.class, pathNo);
			if ( g != null ) {
				
				db.begin();
				g.setName(getParam("pathName"));
				db.commit();
			} 
			else {
				
				System.out.println("Create new GradsLevel");
				
				db.begin();
				g = new GradsLevel();
				g.setId(pathNo);
				g.setCode(pathNo);
				g.setName(getParam("pathName"));
				g.setPathNo(Integer.parseInt(pathNo));
				db.persist(g);
				db.commit();
			}
		}
		
		List<GradsLevel> levels = db.list("select g from GradsLevel g order by g.pathNo");
		Map<Integer, String> pathNames = new HashMap<Integer, String>();
		context.put("pathNames", pathNames);
		for ( GradsLevel g : levels ) {
			pathNames.put(g.getPathNo(), g.getName());
		}
	
		vm = path + "listPath.vm";
	}

	private void editPathName() {
		// TODO Auto-generated method stub
		String pathNo = getParam("path_no");
		context.put("pathNo", pathNo);
		GradsLevel g = db.find(GradsLevel.class, pathNo);
		if ( g != null ) context.put("pathName", g.getName());
		else context.remove("pathName");
		vm = path + "editPathName.vm";
		
	}

	private void listSessions() throws Exception {
		int path_no = !"".equals(getParam("path_no")) ? Integer.parseInt(getParam("path_no")) : 0;
		context.put("path_no", new Integer(path_no));
		
		String direction = getParam("direction");
		System.out.println("direction=" + direction);
		if ( "".equals(direction) ) direction = "asc";
		context.put("direction", direction);		
		
		//Vector sessionList = new SessionBean().getList(path_no, direction);
		List<Session> sessionList = db.list("select s from Session s where s.pathNo = " + path_no + " order by s.startDate asc");
		context.put("sessionList", sessionList);
		
		vm = path + "list_sessions.vm";
		
	}
	
	private int sessionOverlaps(Session session) throws Exception {
		int path_no = session.getPathNo();
		//Vector<Session> sessionList = new SessionBean().getList(path_no, "desc");
		List<Session> sessionList = db.list("select s from Session s where s.pathNo = " + path_no + " order by s.startDate desc");
		Date d1 = session.getStartDate();
		Date d2 = session.getEndDate();
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		//return fmt.format(date1).equals(fmt.format(date2));

		for ( Session s : sessionList ) {
			Date d3 = s.getStartDate();
			Date d4 = s.getEndDate();
			
			if ( fmt.format(d1).equals(fmt.format(d3)) || fmt.format(d1).equals(fmt.format(d4)) ) {
				System.out.println("date exists");
				context.put("date_overlap_with_session", s);
				return 1;
			}
			
			if ( fmt.format(d2).equals(fmt.format(d3)) || fmt.format(d2).equals(fmt.format(d4)) ) {
				System.out.println("date exists");
				context.put("date_overlap_with_session", s);
				return 2;
			}

			if ( d1.after(d3) && d1.before(d4) ) {
				System.out.println("date start overlap with session " + s.getName());
				context.put("date_overlap_with_session", s);
				return 3;
			}
			if ( d2.after(d3) && d2.before(d4) ) {
				System.out.println("date end overlap with session " + s.getName());
				context.put("date_overlap_with_session", s);
				return 4;
			}
			if ( d1.before(d3) && d2.after(d4) ) {
				System.out.println("both date start and end are out of range with session " + s.getName());
				context.put("date_overlap_with_session", s);
				return 5;
			}
			
		}
		return 0;
	}

	private void start() throws Exception {
		

	    Calendar now = new GregorianCalendar();
	    now.setTime(new java.util.Date());
	    String now_day = "" + now.get(Calendar.DAY_OF_MONTH);
	    int now_month = now.get(Calendar.MONTH);
	    int year = now.get(Calendar.YEAR);
	    
		context.put("year", new Integer(year));
		int path_no = !"".equals(getParam("path_no")) ? Integer.parseInt(getParam("path_no")) : 0;
		context.put("path_no", new Integer(path_no));
		
		String direction = getParam("direction");
		if ( "".equals(direction) ) direction = "asc";
		context.put("direction", direction);		
		
		Vector sessionList = new SessionBean().getList(path_no, direction);
		context.put("sessionList", sessionList);
		
		//combine pathNo with GradsLevel
		List<GradsLevel> levels = db.list("select g from GradsLevel g order by g.pathNo");
		Map<Integer, String> pathNames = new HashMap<Integer, String>();
		context.put("pathNames", pathNames);
		for ( GradsLevel g : levels ) {
			pathNames.put(g.getPathNo(), g.getName());
		}
	
		vm =  path + "setup_intake.vm";
	}

	private void updateSession()throws Exception{
		int path_no = !"".equals(getParam("path_no")) ? Integer.parseInt(getParam("path_no")) : 0;
		String id = getParam("session_id");
		String session_code = getParam("session_code");
		String session_name = getParam("session_name");
		String year1 = getParam("year1");
		String month1 = getParam("month1");
		String day1 = getParam("day1");
		String year2 = getParam("year2");
		String month2 = getParam("month2");
		String day2 = getParam("day2");		
		
		if ( "".equals(session_name) ) throw new Exception("Empty field");
		
		String start_date = day1 + "-" + month1 + "-" + year1;
		String end_date = day2 + "-" + month2 + "-" + year2;
		
		Session session = new Session();
		session.setCode(session_code);
		session.setName(session_name);
		session.setStartDate(start_date);
		session.setEndDate(end_date);
		session.setStartDateDay(Integer.parseInt(day1));
		session.setStartDateMonth(Integer.parseInt(month1));
		session.setStartDateYear(Integer.parseInt(year1));
		session.setEndDateDay(Integer.parseInt(day2));
		session.setEndDateMonth(Integer.parseInt(month2));
		session.setEndDateYear(Integer.parseInt(year2));
		session.setPathNo(path_no);
		
		new SessionBean().update(id,session);
		
		listSessions();
	}
	private void addSession() throws Exception {
		int path_no = !"".equals(getParam("path_no")) ? Integer.parseInt(getParam("path_no")) : 0;
	    String session_code = getParam("session_code");
		String session_name = getParam("session_name");
		String year1 = getParam("year1");
		String month1 = getParam("month1");
		String day1 = getParam("day1");
		String year2 = getParam("year2");
		String month2 = getParam("month2");
		String day2 = getParam("day2");		
		
		if ( "".equals(session_name) ) throw new Exception("Empty field");
		
		String start_date = day1 + "-" + month1 + "-" + year1;
		String end_date = day2 + "-" + month2 + "-" + year2;
		
		Session session = new Session();
		session.setCode(session_code);
		session.setName(session_name);
		session.setStartDate(start_date);
		session.setEndDate(end_date);
		session.setStartDateDay(Integer.parseInt(day1));
		session.setStartDateMonth(Integer.parseInt(month1));
		session.setStartDateYear(Integer.parseInt(year1));
		session.setEndDateDay(Integer.parseInt(day2));
		session.setEndDateMonth(Integer.parseInt(month2));
		session.setEndDateYear(Integer.parseInt(year2));
		session.setPathNo(path_no);
		
		int i = sessionOverlaps(session);
		if ( i == 0 ) {
			new SessionBean().save(session);
		}
		context.put("error_number", i);
		
		listSessions();
				
	}
	
	private void removeSession() throws Exception {
		String id = getParam("session_id");
		new SessionBean().delete(id);
		
		listSessions();
		
	}
	
	public static void main(String[] args) {
		
		int day = 1;
		int month = 2;
		int year = 2011;
		
		System.out.println("start date = " + day + " - " + month + " - " + year);
		Date d = new GregorianCalendar(year, month, day).getTime();
		System.out.println("date = " + d);
	}
	

}
