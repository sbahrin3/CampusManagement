package my.test;

import java.util.ArrayList;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.poi.Excel;
import educate.poi.ExcelDocument;

public class ReadDummyNames {
	
	public static void main(String[] args) throws Exception {
		
		String fileName = "/Users/Admin/Documents/namesdata.xls";
		Excel excel = ExcelDocument.getDocument(fileName);
		List<String> names = new ArrayList<String>();
		for ( int i=0; i < excel.getRowSize(); i++ ) {
			String name = excel.getStringValue(i, 0).toUpperCase();
			System.out.println(name);
			names.add(name);
		}
		
		
		DbPersistence db = new DbPersistence();
		List<Student> students = db.list("select s from Student s order by s.biodata.name");
		int i = 0;
		for ( Student s : students ) {
			if ( i > 1000 ) i = 0;
			System.out.println(i + ") " + s.getBiodata().getName() + ", " + s.getMatricNo());
			s.getBiodata().setName(names.get(i));
			i++;
			
		}
		
	

		
	}

}
