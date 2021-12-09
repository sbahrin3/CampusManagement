package u.sams;

import java.sql.ResultSet;

import lebah.db.Db;

public class DbConnectionsNumber implements Runnable {

	public static void main(String[] args) {
		
		Thread t = new Thread(new DbConnectionsNumber());
		t.start();
	}

	private int getNumberOfActiveConnections(Db db) throws Exception {
		ResultSet rs = db.getStatement().executeQuery("show status where variable_name = 'threads_connected'");
		if (rs.next())
			return rs.getInt(2);
		return -1;
	}
	
	

	@Override
	public void run() {

		Db db = null;
		try {
			db = new Db();
			int num = 0;
			while (true) {

				num = getNumberOfActiveConnections(db);
				System.out.println("Active Connections: " + num);
				try {
					Thread.sleep(800);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
		}
	}

}
