package howto;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogErrorToFileExample {
	
	static {
		try {
			FileOutputStream os = new FileOutputStream("error_." + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + "." + lebah.db.UniqueID.get() + ".log.txt");
			System.setErr(new PrintStream(os));
		} catch ( Exception e ) {}
	}

}
