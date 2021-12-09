package educate.hostel.module;

import educate.db.DbPersistence;
import educate.hostel.entity.ItemCondition;

public class PrepareItemCondition {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		db.begin();
		db.executeUpdate("delete from ItemCondition c");
		db.commit();
		
		ItemCondition c = null;
		db.begin();
		c = new ItemCondition();
		c.setId("good");
		c.setCode("GC");
		c.setName("Good Condition");
		c.setSequence(1);
		db.persist(c);
		db.commit();
		
		db.begin();
		c = new ItemCondition();
		c.setId("faulty");
		c.setCode("FC");
		c.setName("Faulty Condition");
		c.setSequence(2);
		db.persist(c);
		db.commit();

		db.begin();
		c = new ItemCondition();
		c.setId("under_maintenance");
		c.setCode("UM");
		c.setName("");
		c.setSequence(3);
		db.persist(c);
		db.commit();

		db.begin();
		c = new ItemCondition();
		c.setId("dispose");
		c.setCode("DI");
		c.setName("Dispose");
		c.setSequence(4);
		db.persist(c);
		db.commit();

		
		
	}

}
