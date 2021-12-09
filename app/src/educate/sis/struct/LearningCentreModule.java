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

import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.velocity.Template;

import educate.sis.struct.entity.Institution;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.tools.Token;
import lebah.db.PersistenceManager;


/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.00
 */
public class LearningCentreModule extends lebah.portal.velocity.VTemplate  {
	private PersistenceManager pm;
	@Override
	public Template doTemplate() throws Exception {
		
		HttpSession session = request.getSession();
		
		String template_name = "vtl/sis/struct/learning_centre.vm";
		String submit = getParam("command");
		
		Vector institutionList = new InstitutionData().getList();
		context.put("institutionList", institutionList);
		
		if ( "getCentre".equals(submit) ) {
			prepare(session);
		}
		else if ( "addCentre".equals(submit) ) {
			addCentre(session);
			prepare(session);
		}
		else if ( "updateCentre".equals(submit) ) {
			updateCentre(session);
			prepare(session);
		}
		else if ( "delete".equals(submit) ) {
			deleteCentre(session);
			prepare(session);
		}
		
		
		//prepare token
		session.setAttribute("token", Token.get());
		Template template = engine.getTemplate(template_name);	
		return template;
		
	}
	
	void prepare(HttpSession session) throws Exception {
		String institution_id = getParam("institution_list");
		context.put("institution_id", institution_id);
		Vector centreList = new LearningCentreData().getCentreList(institution_id);
		context.put("centreList", centreList);
	}
	
	void addCentre(HttpSession session) throws Exception {
		String centre_code = getParam("centre_code");
		String centre_name = getParam("centre_name");
		String centre_address = getParam("centre_address");
		String institution_id = getParam("institution_list");
		
		LearningCentre cen = new LearningCentre();
		cen.setCode( getParam("centre_code"));
		cen.setName(getParam("centre_name"));
		cen.setAddress(getParam("centre_address"));
		pm = new PersistenceManager();
		Institution ins = (Institution)pm.find(Institution.class, institution_id);
		cen.setInstitution(ins);
		new LearningCentreData().addCentre(cen);
	}
	
	void updateCentre(HttpSession session) throws Exception {
		String centre_id = getParam("centre_id");
		String centre_code = getParam("centre_code");
		String centre_name = getParam("centre_name");
		String centre_address = getParam("centre_address");
		String institution_id = getParam("institution_list");
		pm = new PersistenceManager();
		Institution ins = (Institution)pm.find(Institution.class, institution_id);
		LearningCentre cen = new LearningCentre();
		cen.setId(centre_id);
		cen.setInstitution(ins);
		cen.setCode(centre_code);
		cen.setName(centre_name);
		cen.setAddress(centre_address);
		new LearningCentreData().updateCentre(centre_id,cen);
	}	
	
	void deleteCentre(HttpSession session) throws Exception {
		String centre_id = getParam("centre_id");
		new LearningCentreData().deleteCentre(centre_id);
	}


}
