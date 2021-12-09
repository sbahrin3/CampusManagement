package educate.sis.teacher;

import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.TeacherSubject;

public class TestTeacherSubject {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		List<TeacherSubject> teachers = db.list("select t from TeacherSubject t");
		for ( TeacherSubject t : teachers ) {
			System.out.print(t.getTeacher().getName() + " ");
			System.out.print(t.getSubject().getCode() + " ");
			System.out.print(t.getSection().getCode());
			System.out.println();
		}
		
		
	}

}
