package educate.sis.tools;

import java.util.Hashtable;

import javax.servlet.http.HttpSession;

import lebah.portal.ClassLoadManager;
import lebah.portal.action.AjaxModule;

public class ClassLoadModule extends AjaxModule {
	
	String path = "apps/util/status/";
	String vm = "";

	
	@Override
	public String doAction() throws Exception {
		
		vm = path + "status.vm";
		String command = request.getParameter("command");
		if ( "clear".equals(command)) clearCache();
		else if ( "clear_me".equals(command)) clearMyCache();
		Hashtable caches = ClassLoadManager.getCaches();
		context.put("caches", caches);
		return vm;
	}

	private void clearCache() {
		String cacheId = request.getParameter("cache_id");
		if ( null == cacheId || "".equals(cacheId)) {
			ClassLoadManager.clearCache();
		}
		else {
			ClassLoadManager.clearCache(cacheId);
		}
	}
	
	private void clearMyCache() {
		HttpSession session = request.getSession();
		//request.getRequestedSessionId();
		ClassLoadManager.clearCache(session.getId());
	}
	
}
