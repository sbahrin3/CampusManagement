package educate.sis.module;

import educate.sis.struct.entity.Subject;
import lebah.portal.action.Command;

public class SetupFacultySubjectsClassroomModule extends SetupFacultySubjectsModule {
	
	public void preProcess() {
		classroomMode = true;
		super.preProcess();
	}
	
	@Command("getClassroomTemplates")
	public String getClassroomTemplates() throws Exception {
		String subjectId = getParam("templateSubjectId");
		context.put("subjectId", subjectId);
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		return path + "/divClassroomTemplates.vm";
	}
	

}
