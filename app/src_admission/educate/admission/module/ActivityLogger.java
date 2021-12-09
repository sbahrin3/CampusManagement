package educate.admission.module;

import java.util.Date;

import educate.admission.entity.UserActivityLog;
import educate.db.DbPersistence;

public class ActivityLogger {
	
	DbPersistence db = new DbPersistence();
	static ActivityLogger instance = null;
	String userId = "";
	String remoteAddr = "";
	
	private ActivityLogger() {
		System.out.println("Activity Logger Initialized.");
	}
	
	public static ActivityLogger getInstance() {
		if ( instance == null )  {
			instance = new ActivityLogger();
		}
		return instance;
	}
	
	public void init(String userId, String remoteAddr) {
		this.userId = userId;
		this.remoteAddr = remoteAddr;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	
	public static void log(String module, String logDescription) throws Exception {
		if ( instance != null )
			instance.logInfo(module, logDescription);
		else {
			System.out.println("ERROR ACTIVITY LOGGER NOT INITIALIZED!");
		}
	}
	
	public void logInfo(String module, String logDescription) throws Exception {
		try {
			db.begin();
			UserActivityLog log = new UserActivityLog();
			log.setDateTime(new Date());
			log.setUserId(userId);
			log.setRemoteAddress(remoteAddr);
			log.setDescription(logDescription);
			log.setModule(module);
			db.persist(log);
			db.commit();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	
	public static void log(String module, String logDescription, String remark) throws Exception {
		if ( instance != null )
			instance.logInfo(module, logDescription, remark);
		else {
			System.out.println("ERROR ACTIVITY LOGGER NOT INITIALIZED!");
		}
	}	
	
	public void logInfo(String module, String logDescription, String remark) throws Exception {
		try {
			db.begin();
			UserActivityLog log = new UserActivityLog();
			log.setDateTime(new Date());
			log.setUserId(userId);
			log.setRemoteAddress(remoteAddr);
			log.setDescription(logDescription);
			log.setRemark(remark);
			log.setModule(module);
			db.persist(log);
			db.commit();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void log(String userId, String remoteAddr, String module, String logDescription, String remark) throws Exception {
		try {
			db.begin();
			UserActivityLog log = new UserActivityLog();
			log.setDateTime(new Date());
			log.setUserId(userId);
			log.setRemoteAddress(remoteAddr);
			log.setDescription(logDescription);
			log.setRemark(remark);
			log.setModule(module);
			db.persist(log);
			db.commit();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	

}
