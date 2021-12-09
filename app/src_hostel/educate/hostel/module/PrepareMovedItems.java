package educate.hostel.module;

import educate.db.DbPersistence;
import educate.hostel.entity.Mover;

public class PrepareMovedItems {
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		db.begin();
		db.executeUpdate("delete from Mover m");
		db.commit();
		
		db.begin();
		Mover m = new Mover();
		m.setId("0");
		m.setName("Moved Items");
		db.persist(m);
		db.commit();
		
		
	}

}
