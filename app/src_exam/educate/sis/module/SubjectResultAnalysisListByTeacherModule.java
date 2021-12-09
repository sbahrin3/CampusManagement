package educate.sis.module;

public class SubjectResultAnalysisListByTeacherModule extends SubjectResultAnalysisListModule {
	
	@Override
	public String doAction() throws Exception {
		teacherId = request.getParameter("teacher_id") != null ? request.getParameter("teacher_id") : "";
		if ( "".equals(teacherId)) {
			teacherId = (String) request.getSession().getAttribute("_portal_login");
		}
		context.put("teacherId", teacherId);
		return super.doAction();
	}


}
