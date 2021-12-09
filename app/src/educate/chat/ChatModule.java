package educate.chat;

import javax.servlet.http.HttpSession;

import org.apache.velocity.Template;

public class ChatModule extends lebah.portal.velocity.VTemplate {
	
	@Override
	public Template doTemplate() throws Exception {
		HttpSession session = request.getSession();
		
		String vm = "vtl/chat/chat_box.vm";
		
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
		String uri = request.getRequestURI();
		String s1 = uri.substring(1);
		String app = s1.substring(0, s1.indexOf("/"));  
		String http = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("://") + 3);
		context.put("serverUrl", http + server + "/" + app);
		
		String subjectId = getParam("subjectId");
		context.put("roomId", subjectId);
		if ( !subjectId.equals("")) {
			context.put("goto", true);
		} else {
			context.put("goto", false);
		}
		String nickname = (String) session.getAttribute("nickname");
		context.put("nickname", nickname);
		
		Template template = engine.getTemplate(vm);	
		return template;		
	}

}
