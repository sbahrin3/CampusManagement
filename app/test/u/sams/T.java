package u.sams;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class T {
	
	public static void main(String[] args) throws Exception {
		rndICNo();
	}
	
	private static void rndICNo() throws Exception, SQLException {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3309/sams_demo";
		String user = "root";
		String password = "root";
		List<String> ids = new ArrayList<String>();
		Hashtable<String, String> h = new Hashtable<String, String>();
		Database db = new Database(driver, url, user, password);
		String sql = "select id, icno from enrl_student";
		ResultSet rs = db.getStatement().executeQuery(sql);
		while ( rs.next() ) {
			String id = rs.getString(1);
			String icno = rs.getString(2);
			ids.add(id);
			h.put(id, icno);
		}
		String ic = "";
		for ( String id : ids ) {
			ic = h.get(id);
			try {
				if ( ic.length() == 12 ) ic = getICNO(ic);
			} catch ( Exception e ) { ic = lebah.db.UniqueID.getUID();}
			if ( "".equals(ic) ) ic = lebah.db.UniqueID.getUID();
			
			sql = "update enrl_student set icno = '" + ic + "' where id = '" + id + "'";
			System.out.println(sql);
			db.getStatement().executeUpdate(sql);
		}
	}


	private static void rndMatricNo() throws Exception, SQLException {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3309/sams_demo";
		String user = "root";
		String password = "root";
		List<String> ids = new ArrayList<String>();
		Database db = new Database(driver, url, user, password);
		String sql = "select id from enrl_student";
		ResultSet rs = db.getStatement().executeQuery(sql);
		while ( rs.next() ) {
			ids.add(rs.getString(1));
		}
		
		for ( String id : ids ) {
			sql = "update enrl_student set matricNo = '" + lebah.db.UniqueID.getUID() + "' where id = '" + id + "'";
			System.out.println(sql);
			db.getStatement().executeUpdate(sql);
		}
	}
	
	public static String getRandom() {
		String s = "";
		int[] i = createRandom(9,10);
		for ( int l : i ) s += l;
		return s;
	}
	
	private static String getICNO(String icno) throws Exception {
		String y = icno.substring(0,2);
		String m = icno.substring(2,4);
		String d = icno.substring(4,6);
		String p = icno.substring(6,8);
		String g = icno.substring(11);
		
		int xy = Integer.parseInt(y);
		int xm = Integer.parseInt(m);
		int xd = Integer.parseInt(d);
		
		xy = xy - 4;
		xm = xm < 10 ? xm + 3 : xm > 4 ? xm - 3 : xm;
		xd = xd < 28 ? xd + 3 : xd > 4 ? xd - 3 : xd;
		
		String date = ""+ xy + ((xm < 10 ) ? "0" + xm : xm) + ((xd < 10 ) ? "0" + xd : xd);
		String ic = date + p;
		
		int[] t = createRandom(9,3);
		for ( int k : t) ic += k;
		ic = ic + g;
		return ic;
	}
	
	public static int[] createRandom(int high, int count) {
		//make sure count NEVER exceeds high
		if ( count > high ) count = high;
		int[] numbers = new int[count];
		Random random = new Random();
		for ( int k=0; k < count; k++ ) {
			boolean looping = true;
			int questionNo = 0;
			while ( isDuplicate(questionNo, numbers, k) ) questionNo = random.nextInt(high) + 1;
			numbers[k] = questionNo;
		}
		return numbers;
	}
	
	static boolean isDuplicate(int num, int[] numbers, int position) {
		boolean b = false;
		for (int i=position; i > -1; i-- ) {
			if ( num == numbers[i]) {
				b = true;
				break;
			}
		}
		return b;
	}


}
