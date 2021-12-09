package educate.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.w3c.tidy.Tidy;

public class TestTidy {
	
	public static void main(String[] args) throws Exception {
		
		Tidy tidy = new Tidy();
		
		BufferedInputStream in = new BufferedInputStream(new FileInputStream("c:/temp/hello1.html"));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("c:/temp/hello2.html"));
		tidy.parse(in, out);

		in.close();
		out.close();

	}

}
