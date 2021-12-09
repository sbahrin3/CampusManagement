/* ************************************************************************
LEBAH PORTAL FRAMEWORK, http://lebah.sf.net
Copyright (C) 2007  Shamsul Bahrin








MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

* ************************************************************************ */

package educate.sis.struct;

import javax.servlet.http.HttpSession;

import org.apache.velocity.Template;

public class PeriodDisplayNameModule extends lebah.portal.velocity.VTemplate {
    
    @Override
	public Template doTemplate() throws Exception {
        HttpSession session = request.getSession();
        
        String submit = getParam("command");
        //String template_name = prepareTemplate(session, submit);
        String template_name = "";
        
        Template template = engine.getTemplate(template_name);  
        return template;        
    }
    
    
//    String prepareTemplate(HttpSession session, String submit) throws Exception {
//        String template_name = "vtl/sis/period/display_name.vm";
//        Vector<Period> allPeriod = new Vector<Period>();
//        Vector periods = new Vector();
//        String scheme = getParam("scheme"); 
//        context.put("allPeriod", new Vector());
//
//        if ( "getPeriodList".equals(submit)) {
//            
//            preparePeriod(allPeriod, scheme);
//        }
//        else if ( "saveDisplayName".equals(submit)) {
//        	preparePeriod(allPeriod, scheme);
//            for ( Period p : allPeriod) {
//                String displayName = getParam(p.getId());
//                PeriodData.saveDisplayName(p.getId(), displayName);
//            }
//            periods = PeriodData.getPeriodData(scheme);
//            allPeriod = new Vector<Period>();
//            getAllPeriod(periods, allPeriod);
//            context.put("allPeriod", allPeriod);
//        }
//
//        
//        
//        
//        Vector schemeList = PeriodData.getSchemeList();
//        context.put("schemeList", schemeList);
//        
//        return template_name;
//    }
//
//
//	private void preparePeriod(Vector<Period> allPeriod, String scheme) throws Exception {
//		Vector periods;
//		context.put("scheme", scheme);
//		periods = PeriodData.getPeriodData(scheme);
//		context.put("periods", periods);
//		
//		getAllPeriod(periods, allPeriod);
//		context.put("allPeriod", allPeriod);
//	}
//    
//    private void getAllPeriod(Vector periods, Vector<Period> v) {
//        for ( int i=0; i < periods.size(); i++ ) {
//            Period period = (Period) periods.elementAt(i);
//
//            v.addElement(period);
//            if ( period.hasChild() ) {
//                getAllPeriod(period.getChild(), v);     
//            }
//        }
//    }

}
