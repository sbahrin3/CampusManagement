/* ************************************************************************
LEBAH PORTAL FRAMEWORK
Copyright (C) 2007  Shamsul Bahrin

* ************************************************************************ */

package lebah.portal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import lebah.db.DbException;
import lebah.portal.db.CustomClass;
import lebah.portal.db.UserPage;
import lebah.portal.velocity.VServlet;
import lebah.portal.velocity.VTemplate;
import lebah.util.Util;

public class ViewerServlet extends VServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)  throws ServletException, IOException    {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
		res.setContentType("text/html");
		//res.setCharacterEncoding("UTF-8");
		PrintWriter out = res.getWriter();
		HttpSession session = req.getSession();
		
		context = (org.apache.velocity.VelocityContext) session.getAttribute("VELOCITY_CONTEXT");
		engine = (org.apache.velocity.app.VelocityEngine) session.getAttribute("VELOCITY_ENGINE");
		if (context == null || engine == null) {
			if (context == null) {
				System.out.println("[ControllerServlet3] Velocity context is Null.");
			}
			if (engine == null) {
				System.out.println("[ControllerServlet3] Velocity engine is Null.");
			}
			initVelocity(getServletConfig());
			session.setAttribute("VELOCITY_CONTEXT", context);
			session.setAttribute("VELOCITY_ENGINE", engine);
		}
		String uri = req.getRequestURI();
		String s1 = uri.substring(1);
		context.put("appname", s1.substring(0, s1.indexOf("/")));		
		session.setAttribute("_portal_appname", s1.substring(0, s1.indexOf("/")));		
		//get pathinfo
        String pathInfo = req.getPathInfo();
        pathInfo = pathInfo.substring(1); //get rid of the first '/'
        int slash = pathInfo.indexOf("/");
        boolean allowed = true;
        
        boolean hasSecurityToken = true;
        if ( slash == -1 ) hasSecurityToken = false;

        if ( hasSecurityToken ) {
	        //securityToken in PATH
	        String securityTokenURI = pathInfo.substring(0, pathInfo.indexOf("/"));
	        //security token in SESSION
	        String securityToken =(String) session.getAttribute("securityToken");
	        context.put("securityToken", securityToken);
	        //security token in PATH must equal to securityToken in SESSION
	        if ( !securityTokenURI.equals(securityToken)) {
	        	securityTokenDenied(engine, context, req, res);
	        	allowed = false;
	        }
	        
        }
        
        
        System.out.println("In viewer servlet... " + allowed);
        
        if ( allowed ){
        	
	        //pathInfo only contains action
	        pathInfo = pathInfo.substring(pathInfo.indexOf("/") + 1);
			String module = pathInfo != null ? pathInfo : "";	
	        String remoteAddr = req.getRemoteAddr();
	        
	        System.out.println("remote addr: " + remoteAddr);
	        
	        boolean localAccess = false;
	        if ( "127.0.0.1".equals(remoteAddr) ) localAccess = true;
	        
	        //--
	        if ( !localAccess ) {
	        	res.sendRedirect("../access_denied.html");
	        }
	        
	        
			context.put("session", session);	
			try {
				Object content = null;
				try {
					String className = "";
					if (( module.indexOf(".") > 0 )) {
						className = module;
						//content = ClassLoadManager.load(className, req.getRequestedSessionId());
						content = ClassLoadManager.load(className);
						((VTemplate) content).setId(className);
					}
					else{
						className = CustomClass.getName(module);
						content = ClassLoadManager.load(className, module, req.getRequestedSessionId());
						((VTemplate) content).setId(module);
					}
					
					if ( content instanceof GenericPortlet ) {
					    PortletInfo portletInfo = new PortletInfo();
						portletInfo.id = "test_id";
						portletInfo.title = "Test Title";						    
						Hashtable portletState = getPortletState(getServletConfig(), req, res, out, portletInfo);
						RenderRequest renderRequest = (RenderRequest) portletState.get("renderRequest");
						RenderResponse renderResponse = (RenderResponse) portletState.get("renderResponse");
						PortletConfig config = (PortletConfig) portletState.get("config");
						GenericPortlet portlet = (GenericPortlet) content;
						portlet.init(config);
						portlet.render(renderRequest, renderResponse);
					} else {	
						((VTemplate) content).setEnvironment(engine, context, req, res);	
						((VTemplate) content).setServletContext(getServletConfig().getServletContext());	
						((VTemplate) content).setServletConfig(getServletConfig());
						((VTemplate) content).setDiv(false);
						
						if ( content instanceof Attributable ) {
							Hashtable h = UserPage.getValuesForAttributable(module);
							if ( h != null ) {
								((Attributable) content).setValues(h);
							}	
						}
						
						try {
							if ( content != null ) {
								((VTemplate) content).setShowVM(true);
								((VTemplate) content).print(session);
							}
						} catch ( Exception ex ) {
							out.println( ex.getMessage() );
						}						
					}
					//out.println("----------");
					
				} catch ( ClassNotFoundException cnfex ) {
					content = new ErrorMsg(engine, context, req, res);
					((ErrorMsg) content).setError("ClassNotFoundException : " + cnfex.getMessage());				
				} catch ( InstantiationException iex ) {
					content = new ErrorMsg(engine, context, req, res);
					((ErrorMsg) content).setError("InstantiationException : " + iex.getMessage());			
				} catch ( IllegalAccessException illex ) {
					content = new ErrorMsg(engine, context, req, res);
					((ErrorMsg) content).setError("IllegalAccessException : " + illex.getMessage());			
				} catch ( Exception ex ) {
					content = new ErrorMsg(engine, context, req, res);
					((ErrorMsg) content).setError("Other Exception during class initiation : " + ex.getMessage());	
					ex.printStackTrace();					
				}	
				
			} catch ( Exception ex ) {
				System.out.println( ex.getMessage() );	
			} finally {
				//long totalMem = Runtime.getRuntime().totalMemory();
				//System.out.println("total memory = " + totalMem);	
			}
        }
	}
	
	private static Hashtable getPortletState(ServletConfig svtCfg,
			HttpServletRequest req,
				HttpServletResponse res,
				PrintWriter out,
				PortletInfo portletInfo) throws Exception {
		Hashtable h = new Hashtable();
		
		MerakContext context = new MerakContext();
		context.httpServletRequest = req;
		
		MerakConfig config = new MerakConfig();
		config.portletInfo = portletInfo;
		config.portletContext = context;
		
		MerakResponse renderResponse = new MerakResponse();
		MerakRequest renderRequest = new MerakRequest();	
		renderRequest.windowState = WindowState.NORMAL;
		renderRequest.portletMode = PortletMode.VIEW;
		renderResponse.printWriter = out;
		renderRequest.httpServletRequest = req;
		renderResponse.httpServletResponse = res;
		h.put("renderRequest", renderRequest);
		h.put("renderResponse", renderResponse);
		h.put("config", config);
		return h;	
	}
	
	private static void securityTokenDenied(VelocityEngine engine, VelocityContext context, HttpServletRequest req, HttpServletResponse res)  {
		try {
			VTemplate content = new SecurityTokenDenied(engine, context, req, res);
			content.print();
		} catch ( Exception e) {
			System.out.println(e.getMessage());
		} finally {
			
		}
	}
}
