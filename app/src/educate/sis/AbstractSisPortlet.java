/* ************************************************************************
LEBAH PORTAL FRAMEWORK, http://lebah.sf.net
Copyright (C) 2007  Shamsul Bahrin






This program is distributed in the hope that it will be useful,

MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

* ************************************************************************ */

package educate.sis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import lebah.portal.ErrorMsg;
import lebah.portal.MerakConfig;
import lebah.portal.MerakRequest;
import lebah.portal.MerakResponse;
import lebah.portal.ModuleTitle;
import lebah.portal.SectionInfoModule;
import lebah.portal.velocity.VTemplate;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 * @version 0.1
 */
public abstract class AbstractSisPortlet  extends GenericPortlet {
	
	VelocityEngine engine = new VelocityEngine();
	VelocityContext context = new VelocityContext();		
	private static long icnt = 0;
	
	protected String section;
	
	@Override
	public void doView(RenderRequest req, RenderResponse res) throws PortletException, IOException {
		try {		
			PrintWriter out = res.getWriter();
			HttpServletRequest request = ((MerakRequest) req).getHttpServletRequest();
			HttpServletResponse response = ((MerakResponse) res).getHttpServletResponse();
			HttpSession session = request.getSession();
			
			ServletContext servletContext = session.getServletContext();

			//-- GET VELOCITY FROM SESSION (INITIALIZED IN THE PORTAL'S CONTROLLER SERVLET
			context = (VelocityContext) session.getAttribute("_VELOCITY_CONTEXT");
			engine = (VelocityEngine) session.getAttribute("_VELOCITY_ENGINE");
			//--			
			
			session.setAttribute("asModule", "true");
				
			PortletConfig pc = getPortletConfig();
			String pId = "";
			if ( pc instanceof MerakConfig ) pId = ((MerakConfig) pc).getId();			
	
			String appname = (String) session.getAttribute("_portal_appname");
			context.put("appname", appname);
			String module = "";
							  		
			if ( request.getParameter("module") != null ) {
				module = request.getParameter("module");	
			}
			context.put("session", session);
			doPost(request, response, session, out, module);	
			session.setAttribute("asModule", "false");
		} catch ( ServletException sex ) {
			throw new PortletException(sex.getMessage());	
		}
	}
	
    public void initialize(ServletContext ctx) throws ServletException {
        try { 
	        System.out.println("initialize engine..");
	        Properties p = loadConfigurationSimple(ctx);
        	engine.init(p);
        	
        } catch ( Exception e ) {
	        System.out.println( e.getMessage() );
	        throw new ServletException( e.getMessage() );
        }
    }	
	

	
    private Properties loadConfigurationSimple(ServletContext ctx) throws IOException, FileNotFoundException {
   		String path = ctx.getRealPath("/");
        Properties p = new Properties();
        p.setProperty( RuntimeConstants.FILE_RESOURCE_LOADER_PATH,  path );
        //p.setProperty( "runtime.log", path + "velocity.log" );  
        return p;	    	
    }	
    
	void doPost(HttpServletRequest req, HttpServletResponse res, HttpSession session, 
				PrintWriter out, String module) throws ServletException, IOException  {
		res.setContentType("text/html");
		//System.setErr(new PrintStream(new FileOutputStream("c:/error_portal.txt")));
		

		
		//to handle browser's refresh that might trigger double submission
		//get previous token
		String prev_token = session.getAttribute("form_token") != null ? (String) session.getAttribute("form_token") : "";
		//get form token
		String form_token = req.getParameter("form_token") != null ? req.getParameter("form_token") : "empty";
		//pre_token equals form_token if not refresh
		
		if ( prev_token.equals(form_token) ) session.setAttribute("doPost", "true");
		else if ( "empty".equals(form_token) ) session.setAttribute("doPost", "true");
		else session.setAttribute("doPost", "false");
		//create a new form token
		form_token = Long.toString(System.currentTimeMillis());
		session.setAttribute("form_token", form_token);
		
		 
		session.setAttribute("_portal_module_section", section);
		
		//if this is section info, set module to empty
		if ( req.getParameter("section") != null ) module = "";
		
		//put session into velocity context
		context.put("session", session);		
		
		//module title
		String _title = req.getParameter("_title") != null ? 
					    req.getParameter("_title") : 
					    "";
		context.put("moduleTitle", _title);
		context.put("_url", module + "&_title=" + _title);
		
		//login - as a portlet always true
		context.put("isLogin", new Boolean(true));		
		
		//out.println("<html>");
		//out.println("<link href=\"../styles.css\" rel=\"stylesheet\" type=\"text/css\">");
		//out.println("<body>");
		
		out.println("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">");

		out.println("<tr><td width=\"150\" class=\"sidemenubar\" valign=\"top\">");
		
		SisMenu sisMenu = new SisMenu(engine, context, req, res);
		sisMenu.setSection(section);
		try {
			sisMenu.print();
		} catch ( Exception ex ) {
			out.println(ex.getMessage());	
		}		
		
		out.println("</td><td valign=\"top\">");
		out.println("<table width=\"98%\" cellspacing=\"2\" cellpadding=\"0\">");
		
		if ( !"".equals(_title) ) {
			out.println("<tr><td class=\"module_title\">");
			//module title
			ModuleTitle mt = new ModuleTitle(engine, context, req, res);
			try {
				mt.print();
			} catch ( Exception ex ) {
				out.println(ex.getMessage());	
			}		
			out.println("</td></tr>");
		}
		
		out.println("<tr><td>");
		if ( !"".equals(module) ) {
			try {
			    /*
				//module tracker
				if ( !"".equals(module) ) 
					UserTrackerLog.save2(req, (String) session.getAttribute("_portal_login"), _title, module);			    
				*/
				VTemplate content = null;
				try {
					//Class klazz = Class.forName(module);
					
					Class klazz = SisClassLoader.load(module);
					content = ((VTemplate) klazz.newInstance());			
					content.setEnvironment(engine, context, req, res);	
					
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
				
				try {
					if ( content != null ) {
						//content.print(true);
						content.setShowVM(true);
						content.print();
					}
				} catch ( Exception ex ) {
					out.println( ex.getMessage() );
				}				
				
			} catch ( Exception ex ) {
				System.out.println( ex.getMessage() );	
			} finally {
				/*
				long totalMem = Runtime.getRuntime().totalMemory();
				long freeMem = Runtime.getRuntime().freeMemory();
				System.gc();
				icnt++;
				System.out.println(icnt + ") free memory = " + freeMem + "/" + totalMem);	
				*/
			}
		}
		else {
			SectionInfoModule sectionInfo = new SectionInfoModule(engine, context, req, res);
			sectionInfo.setSection(section);
			try {
				sectionInfo.print();
			} catch ( Exception ex ) {
				out.println(ex.getMessage());	
			}			
		}
		out.println("</td></tr></table>");
		out.println("</td></tr>");
		out.println("</table>");
		//out.println("</body>");
		//out.println("</html>");
	} 
}
