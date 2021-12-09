package sbahrin;

import java.sql.ResultSet;
import java.util.concurrent.RecursiveTask;

import lebah.db.Database;

public class DataCounterTask extends RecursiveTask<Integer>{
	
	Database db = null;
	int side = 0;
	
	public DataCounterTask(Database db) {
		this.db = db;
	}
	
	public DataCounterTask(Database db, int side) {
		this.side = side;
		this.db = db;
	}

	@Override
	protected Integer compute() {
		int cnt = 0;
		try {
		if ( side == 1 ) {
			
			ResultSet rs = db.getStatement().executeQuery("select s.matricNo from enrl_student s");
			while ( rs.next() ) {
				cnt++;
				System.out.println("side = " + side + " - " + cnt);
			}
			return cnt;
		} else if (side == 2 ) {
			ResultSet rs = db.getStatement().executeQuery("select s.matricNo from enrl_student s");
			while ( rs.next() ) {
				cnt++;
				System.out.println("side = " + side + " - " + cnt);
			}
			return cnt;
		}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		final DataCounterTask task1 = new DataCounterTask(db, 1);
		task1.fork();
		
		final DataCounterTask task2 = new DataCounterTask(db, 2);
		int total = task2.compute() + task1.join();
		
		return total;
	}

}