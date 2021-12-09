package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.GraduanItemCheck;
import educate.sis.struct.entity.Program;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class GraduationItemCheckListModule extends LebahModule {

	private String path = "apps/util/graduation_item_list";
	private DbPersistence db = new DbPersistence();
	
	public void preProcess() {
		context.put("df", new SimpleDateFormat("dd-MM-yyyy"));
	}

	@Override
	public String start() {
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		return path + "/start.vm";
	}
	
	@Command("getList")
	public String getList() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		
		List<GraduanItemCheck> graduans = db.list("select g from GraduanItemCheck g where g.student.program.id = '" + programId + "' order by g.student.matricNo");
		context.put("graduans", graduans);
		request.getSession().setAttribute("graduans", graduans);
		
		return path + "/list.vm";
	}
	
	@Command("getDetail")
	public String getDetail() throws Exception {
		String graduanId = getParam("graduanId");
		GraduanItemCheck graduan = db.find(GraduanItemCheck.class, graduanId);
		context.put("g", graduan);
		return path + "/detail.vm";
	}

}
