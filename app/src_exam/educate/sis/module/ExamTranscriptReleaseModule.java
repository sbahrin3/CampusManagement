/**
 * 
 */
package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Program;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class ExamTranscriptReleaseModule extends LebahModule {
	
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/examTranscriptRelease";
	
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
	}

	/* (non-Javadoc)
	 * @see lebah.portal.action.LebahModule#start()
	 */
	@Override
	public String start() {
		
		
		
		//list programs
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		return path + "/start.vm";
	}
	
	@Command("release")
	public String release() throws Exception {
		String[] programIds = request.getParameterValues("programIds");
		if ( programIds != null ) {
			
			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
			
			for ( String programId : programIds ) {
				Program program = db.find(Program.class, programId);
				Date date = null;
				try {
					date = df.parse(getParam("transcriptReleaseDate_" + programId));
				} catch ( Exception e ) { }
				
				if ( date != null ) {
					db.begin();
					program.setTranscriptReleaseDate(date);
					db.commit();
				} else {
					db.begin();
					program.setTranscriptReleaseDate(null);
					db.commit();
				}
			}
		}
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		return path + "/listPrograms.vm";
	}
	
	/*
	@Command("hide")
	public String hide() throws Exception {
		String[] programIds = request.getParameterValues("programIds");
		if ( programIds != null ) {
			for ( String programId : programIds ) {
				Program program = db.find(Program.class, programId);
				db.begin();
				program.setReleaseTranscript(false);
				db.commit();
			}
		}
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		return path + "/listPrograms.vm";
	}
	*/
	
	

}
