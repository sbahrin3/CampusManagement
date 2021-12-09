package educate.intercept;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;

import lebahlic.Dmn;

public class LoginIntercept extends lebah.portal.LoginIntercept {

	@Override
	public void process(HttpServletRequest req, HttpServletResponse res, VelocityContext context) throws ServletException, IOException {
		
		
		System.out.println(">> Inside here...");
		
		req.getSession().setAttribute("__expired_", "");
		int days = 100;
		Dmn dmn = Dmn.getInstance();
		if (  dmn.isExpired() ) {
			try {
				days = dmn.getDays();
				req.getSession().setAttribute("__expired_", "yes");
				PrintWriter out = res.getWriter();
				res.setContentType("text/html");
				out.print("UNABLE TO ALLOW PERMISSION TO RUN THIS APPLICATION!!!");
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else if ( dmn.isWarning() ) {
			days = dmn.getDays();
			req.getSession().setAttribute("__days", days + " ");
			req.getSession().setAttribute("__expired_", "yes");
		}
		
		
		String _days = req.getSession().getAttribute("__days") != null ? (String) req.getSession().getAttribute("__days") : "100";
		
		/*
		try {
			days = Integer.parseInt(_days.trim());
			System.out.println(days);
		} catch ( Exception e ) { 
			
		}
		*/
		
		System.out.println(">> " + days);
		context.put("__expired", req.getSession().getAttribute("__expired_"));
		context.put("__days", days);
		
		
		String expired = req.getSession().getAttribute("__expired_") != null ? (String) req.getSession().getAttribute("__expired_") : "";
		if ( "yes".equals(expired) ) {
			//res.sendRedirect("../x.jsp");
			System.out.println("This copy has expired..." + days);
			if ( days < 0 ) {
				res.sendRedirect("../x.jsp");
			}
		}
		
		/*
		UserAttributes attr = UserAttributes.getInstance();
		
		//initialized activity logger
		String userId = (String) req.getSession().getAttribute("_portal_login");
		if ( userId == null ) userId = "none";
		String remoteAddr = req.getRemoteAddr();
		ActivityLogger.getInstance().init(userId, remoteAddr);
		
		
		if ( "student".equals(attr.getRole())) {
		
			String strStudentStatus = "";
			String strStudentCategory = "";

			StudentInformation studentInformation = new StudentInformation();
			Student student;
			try {
				student = studentInformation.getStudentInfo(attr.getLogin());
				StudentStatus studentStatus = null;
				if (student == null) {
					System.out.println("[DesktopController.createPortalPage] No Student found.");
				} else {
					studentStatus = studentInformation.getCurrentStatus(student);
				}
				if (studentStatus == null) {
					System.out.println("[DesktopController.createPortalPage] student status is NULL.");				
				} else {
					StatusType statusType = studentStatus.getType();
					if (statusType == null) {
						System.out.println("[DesktopController.createPortalPage] status type is NULL.");					
					} else {
						strStudentStatus = statusType.getName();
						strStudentCategory = statusType.getCategory();
					}
				}

				context.put("studentStatus", strStudentStatus);
				context.put("studentCategory", strStudentCategory);	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
	}


}
