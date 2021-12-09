package educate.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	
	static {
		try {
			FileOutputStream os = new FileOutputStream("error_." +  new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + "." +  ".log.txt");
			System.setErr(new PrintStream(os));
		} catch ( Exception e ) {}
	}
	
	public static void out(String str) {
		System.out.println(str);
	}
	
	public static PrintStream out() {
		return System.out;
	}
	
	public static void fout(String str) {
		try {
			File file = new File("log." +  new SimpleDateFormat("dd.MM.yyyy").format(new Date()) + ".txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream os = new FileOutputStream(file, true);
			str = str + "\n";
			byte[] contentInBytes = str.getBytes();
			os.write(contentInBytes);
			os.flush();
			os.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void err(String str) {
		System.err.println(str);
	}

}
