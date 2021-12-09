package educate.admission.module;

import java.util.List;

import educate.admission.entity.StudentMoheData;
import educate.db.DbPersistence;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentMoheDataReportModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "admission/mohe_list";

	@Override
	public String start() {
		return path + "/start.vm";
	}
	
	@Command("report")
	public String report() throws Exception {
		List<StudentMoheData> students = db.list("select s from StudentMoheData s order by s.nama_penuh");
		context.put("students", students);		
		return path + "/report.vm";
	}

}
