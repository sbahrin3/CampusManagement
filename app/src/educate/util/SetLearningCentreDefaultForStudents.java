package educate.util;

import educate.db.DbPersistence;
import educate.sis.struct.entity.LearningCentre;

public class SetLearningCentreDefaultForStudents {
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		LearningCentre c = (LearningCentre) db.get("select c from LearningCentre c where c.mainCampus = 1");
		if ( c == null ) return;
		db.begin();
		db.executeUpdate("update Student s set s.learningCenter.id = '" + c.getId() + "'");
		db.commit();
	}

}
