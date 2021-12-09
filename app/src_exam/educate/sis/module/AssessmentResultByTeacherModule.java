package educate.sis.module;

public class AssessmentResultByTeacherModule extends AssessmentResultModule {
	
	@Override
	public String doAction() throws Exception {
		
		teacherId = request.getParameter("teacher_id") != null ? request.getParameter("teacher_id") : "";
		
		//teacherId = "IE098";
		
		if ( "".equals(teacherId)) {
			teacherId = (String) request.getSession().getAttribute("_portal_login");
		}
		context.put("teacherId", teacherId);
		return super.doAction();
	}

}
