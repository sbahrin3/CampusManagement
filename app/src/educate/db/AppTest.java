package educate.db;

import java.util.List;

import educate.enrollment.entity.Student;

public class AppTest {
	
	static String dbUrl1 = "jdbc:mysql://127.0.0.1:3307/campus_data"; //cucms_data campus_data
	static String dbUrl2 = "jdbc:mysql://127.0.0.1:3307/cucms_data";
	static String dbUser = "root";
	static String dbPassword = "";
	
	
	public static void main(String[] args) throws Exception {
		
		test(dbUrl1);
		test(dbUrl1);
		test(dbUrl2);
		test(dbUrl1);
		test(dbUrl1);
		test(dbUrl2);
		test(dbUrl2);
		
	}
	
	static void test(String url){
		DbPersistence db = new DbPersistence(url, dbUser, dbPassword);
		
		List<Student> students = db.list("select s from Student s order by s.biodata.name");
		int i = 0;
		for ( Student s : students ) {
			i++;
			System.out.println(i + ") " + s.getBiodata().getName() + ", " + s.getMatricNo());
			if ( i == 5 ) break;

		}
		
		System.out.println("====");
	}
	


}
