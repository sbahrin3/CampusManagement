package educate.sis.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Program;
import lebah.portal.action.LebahModule;

public class GraduationItemCheckReportModule extends LebahModule {
	
	private String path = "apps/util/graduation_item_list_report";
	private DbPersistence db = new DbPersistence();	

	@Override
	public String start() {
		
		//List<Program> programs = db.list("select p from Graduate g join g.student.program p group by g.student.program.code");
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		
		List<ItemCheckStat> robeStat = (List<ItemCheckStat>) db.list("select new educate.sis.module.ItemCheckStat(g.student.program.code, g.student.program.name, count(g)) from GraduanItemCheck g where g.robeStatus = 1 group by g.student.program.code");
		List<ItemCheckStat> scrollStat = (List<ItemCheckStat>) db.list("select new educate.sis.module.ItemCheckStat(g.student.program.code, g.student.program.name, count(g)) from GraduanItemCheck g where g.scrollStatus = 1 group by g.student.program.code");
		List<ItemCheckStat> transcriptStat = (List<ItemCheckStat>) db.list("select new educate.sis.module.ItemCheckStat(g.student.program.code, g.student.program.name, count(g)) from GraduanItemCheck g where g.transcriptStatus = 1 group by g.student.program.code");
		List<ItemCheckStat> photoStat = (List<ItemCheckStat>) db.list("select new educate.sis.module.ItemCheckStat(g.student.program.code, g.student.program.name, count(g)) from GraduanItemCheck g where g.photoStatus = 1 group by g.student.program.code");
		List<ItemCheckStat> alumniCardStat = (List<ItemCheckStat>) db.list("select new educate.sis.module.ItemCheckStat(g.student.program.code, g.student.program.name, count(g)) from GraduanItemCheck g where g.alumniCardStatus = 1 group by g.student.program.code");
		List<ItemCheckStat> tracerStudyStat = (List<ItemCheckStat>) db.list("select new educate.sis.module.ItemCheckStat(g.student.program.code, g.student.program.name, count(g)) from GraduanItemCheck g where g.tracerStudyStatus = 1 group by g.student.program.code");
		
		Map<String, Long> robeStatusStat = new HashMap<String, Long>();
		context.put("robeStatusStat", robeStatusStat);
		for ( ItemCheckStat s : robeStat ) robeStatusStat.put(s.getProgramCode(), s.getCount());

		Map<String, Long> scrollStatusStat = new HashMap<String, Long>();
		context.put("scrollStatusStat", scrollStatusStat);
		for ( ItemCheckStat s : scrollStat ) scrollStatusStat.put(s.getProgramCode(), s.getCount());
		
		Map<String, Long> transcriptStatusStat = new HashMap<String, Long>();
		context.put("transcriptStatusStat", transcriptStatusStat);
		for ( ItemCheckStat s : transcriptStat ) transcriptStatusStat.put(s.getProgramCode(), s.getCount());
		
		Map<String, Long> photoStatusStat = new HashMap<String, Long>();
		context.put("photoStatusStat", photoStatusStat);
		for ( ItemCheckStat s : photoStat ) photoStatusStat.put(s.getProgramCode(), s.getCount());
		
		Map<String, Long> alumniCardStatusStat = new HashMap<String, Long>();
		context.put("alumniCardStatusStat", alumniCardStatusStat);
		for ( ItemCheckStat s : alumniCardStat ) alumniCardStatusStat.put(s.getProgramCode(), s.getCount());
		
		Map<String, Long> tracerStudyStatusStat = new HashMap<String, Long>();
		context.put("tracerStudyStatusStat", tracerStudyStatusStat);
		for ( ItemCheckStat s : tracerStudyStat ) tracerStudyStatusStat.put(s.getProgramCode(), s.getCount());

		return path + "/start.vm";
	}
	
	public static void main(String [] args) throws Exception {
		DbPersistence db = new DbPersistence();
		
		List<Program> programs = db.list("select p from Graduate g join g.student.program p group by g.student.program.code");
		for ( Program p : programs ) {
			System.out.println(p.getCode());
		}
		
	}

}
