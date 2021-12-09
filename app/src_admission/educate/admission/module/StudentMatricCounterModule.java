package educate.admission.module;

import educate.db.DbPersistence;
import educate.sis.struct.entity.StudentCounter;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentMatricCounterModule extends LebahModule {
	
	DbPersistence db = new DbPersistence();
	String path = "admission/student_counter";

	@Override
	public String start() {
		StudentCounter counter = db.find(StudentCounter.class, "master");
		if ( counter == null ) {
			db.begin();
			counter = new StudentCounter();
			counter.setId("master");
			counter.setCounter(1);
			db.persist(counter);
			try {
				db.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		context.put("counter", counter);
		return path + "/start.vm";
	}
	
	@Command("updateCounter")
	public String updateCounter() throws Exception {
		String counter = getParam("counter");
		int c = Integer.parseInt(counter);
		StudentCounter studentCounter = db.find(StudentCounter.class, "master");
		db.begin();
		studentCounter.setCounter(c);
		db.commit();
		return path + "/empty.vm";
	}

}
