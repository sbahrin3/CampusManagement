package educate.sis.tools;

import javax.servlet.http.HttpSession;

import lebah.db.DbException;
import lebah.portal.action.AjaxModule;
import lebah.portal.db.AuthenticateUser;
import lebah.portal.db.UserPage;

public class LoginAjaxModule extends AjaxModule {
	
	String path = "apps/login/";
	private String vm = "default.vm";
	HttpSession session;

	
	@Override
	public String doAction() throws Exception {
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "login".equals(command)) login();
		return vm;
	}

	private void login() {
		vm = path + "login_result.vm";
		String usrlogin = request.getParameter("login");
		String password = request.getParameter("password");
		try {
			AuthenticateUser auth = new AuthenticateUser(request);
			if ( auth.lookup(usrlogin, password) ) {
				context.put("message", "Login Successfull");
				session.setAttribute("nickname", usrlogin);
				session.setAttribute("_portal_role", auth.getRole());
				session.setAttribute("_portal_username", auth.getUserName());
				session.setAttribute("_portal_login", auth.getUserLogin());
				session.setAttribute("_portal_islogin", "true");
				//CSS
				try {
					String css = UserPage.getCSS((String) session.getAttribute("_portal_login") );
					session.setAttribute("_portal_css", css);
				} catch ( DbException cssex ) {
					System.out.println("[DesktopController] Can't get CSS");
				}
			} else {
				context.put("message", "Login Failed");
				session.setAttribute("_portal_role", "anon");
				session.setAttribute("_portal_username", "Anonymous");
				session.setAttribute("_portal_login", "anon");
				session.setAttribute("_portal_islogin", "false");
			}
		} catch ( Exception ex ) {
			context.put("message", ex.getMessage());
		}
		
	}

	private void start() {
		vm = path + "login.vm";
		
	}

}
