package howto;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerExample {
	
	public void sample() throws Exception {
		
	    Logger logger = Logger.getLogger("MyErrorLog");  
	    FileHandler fh;
	    
        fh = new FileHandler("C:/MyProjects/logs/MyErrorLog.log");  
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);  
        
        
        logger.info("My Error Message .... bla bla bla");
	}

}
