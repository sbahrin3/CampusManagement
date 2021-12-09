/**
 * 
 */
package educate.sis.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.sis.general.entity.GradsLevel;
import educate.sis.struct.entity.Session;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class SetupAcademicSessionModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/setupAcademicSession";
	
	private static String[] month_name = {"January", "February", "March", "April", "May", "Jun", "July", "August", "September", "October", "November", "December"};
	private static String[] day_name = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	private static String[] hour_name = {"12 AM", "1 AM", "2 AM", "3 AM", "4 AM", "5 AM", "6 AM", "7 AM", "8 AM", "9 AM", "10 AM", "11 AM",
														"12 PM", "1 PM", "2 PM", "3 PM", "4 PM", "5 PM", "6 PM", "7 PM", "8 PM", "9 PM", "10 PM", "11 PM"};

	

	/* (non-Javadoc)
	 * @see lebah.portal.action.LebahModule#start()
	 */
	@Override
	public String start() {
		
		
		List<Session> sessionList = new ArrayList<Session>();
		try {
			sessionList = db.list("select s from Session s where s.pathNo = 0 order b s.startDate");
		} catch (Exception e) {
			e.printStackTrace();
		}
		context.put("sessionList", sessionList);
		
		//combine pathNo with GradsLevel
		List<GradsLevel> levels = db.list("select g from GradsLevel g order by g.pathNo");
		Map<Integer, String> pathNames = new HashMap<Integer, String>();
		context.put("pathNames", pathNames);
		for ( GradsLevel g : levels ) {
			pathNames.put(g.getPathNo(), g.getName());
		}
		return path + "/start.vm";
	}

}
