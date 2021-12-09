package educate.admission.module;

import java.text.SimpleDateFormat;
import java.util.Date;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Program;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SubjectRegistrationDateModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "admission/subject_registration_date";
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));
	}

	@Override
	public String start() {
		// List of Programs
		context.put("programs", db.list("select p from Program p"));
		
		return path + "/start.vm";
	}
	
	@Command("update")
	public String update() throws Exception {
		String programId = getParam("programId");
		String startDate = getParam("startDate_" + programId);
		String startTime = getParam("startTime_" + programId);
		String endDate = getParam("endDate_" + programId);
		String endTime = getParam("endTime_" + programId);
		

		
		Program program = db.find(Program.class, programId);
		
		db.begin();
		try {
			Date date1 = new SimpleDateFormat("dd-MM-yyyy hh:mm a").parse(startDate + " " + startTime);
			program.setStartDate(date1);
		} catch ( Exception e ) {}
		try {
			Date date2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a").parse(endDate + " " + endTime);
			program.setEndDate(date2);
		} catch ( Exception e ) {}
		db.commit();
		
		return path + "/empty.vm";
	}

}
