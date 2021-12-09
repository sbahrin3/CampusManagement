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

package educate.sis.billing.statement;

import javax.servlet.http.HttpSession;

import org.apache.velocity.Template;

import educate.enrollment.StudentInformation;
import educate.enrollment.entity.Student;

/**
 * 
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 * @version 0.1
 */
public class StudentAccountModule extends lebah.portal.velocity.VTemplate {
    
    @Override
	public Template doTemplate() throws Exception {
        HttpSession session = request.getSession();
        
        String submit = getParam("command");
        String template_name = prepareTemplate(session, submit);
        
        Template template = engine.getTemplate(template_name);  
        return template;        
    }
    
    
    String prepareTemplate(HttpSession session, String submit) throws Exception {
        String template_name = "vtl/sis/billing/account/acc_statement.vm";
        
        String studentId = getParam("student_id");
        context.put("student_id", studentId);
        context.put("hasData", Boolean.FALSE);
        if ( "getAccountStatement".equals(submit)) {
            //AccountStatement acc = AccountData.getStatements(studentId);
            //context.put("accountStatement", acc);
            context.put("hasData", Boolean.TRUE);
            
            Student studentInfo = new StudentInformation().getStudentInfo(studentId);
            context.put("student", studentInfo);
            
        }
        
        return template_name;
    }


}
