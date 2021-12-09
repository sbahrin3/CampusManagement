/**
 * 
 */
package educate.util;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.general.entity.GradsLevel;
import educate.sis.struct.entity.Program;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class ProgramPathMigrationModule extends LebahModule {

	private String path = "path_migration";
	private DbPersistence db = new DbPersistence();
	private String userId = "";
	
	public void preProcess() {
		userId = (String) request.getSession().getAttribute("_portal_login");
	}
	
	
	@Override
	public String start() {
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		List<GradsLevel> levels = db.list("select l from GradsLevel l order by l.pathNo");
		context.put("levels", levels);
		return path + "/start.vm";
	}
	
	@Command("migrateProgramPath")
	public String migrateProgramPath() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		String pathTo = getParam("pathNo");
		int pathNo = Integer.parseInt(pathTo);
		
		if ( pathNo != program.getLevel().getPathNo() ) {
			
			educate.util.Log.fout("Migrate Program Path. User: " + userId + ", Program: " + program.getCode() + " " + program.getName() + "(" + program.getLevel().getPathNo() + "), Path To:" + pathNo);
			
			SessionPathMigration m = new SessionPathMigration(db, program, pathTo);
			m.migratePath();
			
			//get list of students under this program
			List<Student> students = db.list("select s from Student s where s.program.id = '" + program.getId() + "'");
			context.put("students", students);
			//int i = 0;
			for ( Student student : students ) {
				if ( student.getIntake() != null && student.getIntake().getPathNo() != pathNo ) {
					//i++;
					//System.out.println(i + ") " + student.getMatricNo() + " " + student.getBiodata().getName());
					m.migrateStudent(student);
				}
			}
		} else {
			return path + "/nomigrate.vm";
		}
		
		return path + "/migrate.vm";
	}

}
