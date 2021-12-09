package educate.sis.exam.module;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.sis.exam.entity.TranscriptEndorsedDate;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SetupTranscriptEndorsedDateModule extends LebahModule {

	private String path = "apps/setup_transcript_endorsed_date";
	private DbPersistence db = new DbPersistence();	
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
	}

	@Override
	public String start() {
		List<Program> programs = db.list("select p from Program p order by p.name");
		context.put("programs", programs);
		return path + "/start.vm";
	}
	
	@Command("back")
	public String back() {
		List<Program> programs = db.list("select p from Program p order by p.name");
		context.put("programs", programs);		
		return path + "/select_params.vm";
	}
	
	@Command("getProgram")
	public String getProgram() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		
		return listEndorsedDates(programId, program.getLevel().getPathNo());
	}

	private String listEndorsedDates(String programId, int pathNo) {
		
		List<Session> sessions = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
		context.put("sessions", sessions);
		
		Map<String, TranscriptEndorsedDate> endorsedDateMap = new HashMap<String, TranscriptEndorsedDate>();
		context.put("endorsedDateMap", endorsedDateMap);
		List<TranscriptEndorsedDate> endorsedDates = db.list("select d from TranscriptEndorsedDate d where d.program.id = '" + programId + "' order by d.session.id");
		for ( TranscriptEndorsedDate d : endorsedDates ) {
			endorsedDateMap.put(d.getSession().getId(), d);
		}
		
		return path + "/endorsed_dates.vm";
	}	
	
	@Command("update")
	public String update() throws Exception {
		
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		
		String sessionId = getParam("sessionId");
		Session session = db.find(Session.class, sessionId);
		
		String date = getParam("endorsedDate_" + sessionId);
		Date endorsedDate = new Date();
		try {
			endorsedDate = new SimpleDateFormat("dd-MM-yyyy").parse(date);
		} catch ( Exception e ) {}
		
		TranscriptEndorsedDate d = (TranscriptEndorsedDate) db.get("select d from TranscriptEndorsedDate d where d.program.id = '" + programId + "' and d.session.id = '" + sessionId + "'");
		if ( d == null ) {
			d = new TranscriptEndorsedDate();
			db.begin();
			d.setProgram(program);
			d.setSession(session);
			d.setEndorsedDate(endorsedDate);
			db.persist(d);
			db.commit();
		}
		else {
			db.begin();
			d.setEndorsedDate(endorsedDate);
			db.commit();
		}
		return path + "/empty.vm";
	}

}
