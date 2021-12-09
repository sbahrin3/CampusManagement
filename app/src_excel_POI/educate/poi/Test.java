package educate.poi;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test {
	
	public static void main(String[] args) throws Exception {
		
		Excel1 x = new Excel1();
		FileOutputStream fileOut = new FileOutputStream("d:/poi/poi-test.xls");
		
		List<String> headers = new ArrayList<String>();
		headers.add("title1");
		headers.add("title2");
		headers.add("title3");
		
		
		List<List> rows = new ArrayList<List>();
		List cols = new ArrayList();
		rows.add(cols);
		cols.add("a");
		cols.add("b");
		cols.add(899);
		cols.add(new Date());
		cols.add(87.34);
		
		cols = new ArrayList();
		rows.add(cols);
		cols.add("a");
		cols.add("b");
		cols.add(899);
		cols.add(new Date());
		cols.add(87.34);
		
		x.createXLS(fileOut, headers, rows);
		
		fileOut.flush();
		fileOut.close();
		
	}

}
