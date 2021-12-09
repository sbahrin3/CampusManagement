package educate.sis.module;

public class HostelRoomAllocationByStudentModule extends HostelRoomAllocationModule {
	
	public String doAction() throws Exception {
		
		studentId = request.getParameter("student_id") != null ? request.getParameter("student_id") : "";
		
		//studentId = "PHARM 1308-3373";
		
		if ( "".equals(studentId)) {
			studentId = (String) request.getSession().getAttribute("_portal_login");
		}
		context.put("studentId", studentId);
		return super.doAction();
	}
	

}
