/* ************************************************************************
LEBAH PORTAL FRAMEWORK, http://lebah.sf.net
Copyright (C) 2007  Shamsul Bahrin








MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

* ************************************************************************ */
package educate.sis.billing;

/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
public class NumberWordsFormat extends BillingUtil {
	public String convert(String number) {
		toWordsEng(number);
		String ringgit = getRinggit();
		String sen = getSen();					
		return "RINGGIT MALAYSIA: " + ringgit.toUpperCase() + (!"".equals(sen) ? " AND SEN "  + sen.toUpperCase() + " ONLY" : " ONLY");
	}
	
	public String convert(String number, String currency) {
		toWordsEng(number);
		String ringgit = getRinggit();
		String sen = getSen();					
		return currency + ": " + ringgit + (!"".equals(sen) ? " and Sen "  + sen.toUpperCase() + " Only" : " Only");
	}	
	
	public String convert(float number) {
		return convert(Float.toString(number));
	}
	
	public String convert(float number, String currency) {
		return convert(Float.toString(number), currency);
	}
	
	public String convert(int number) {
		return convert(Integer.toString(number));
	}
	
	public String convert(double number) {
		return convert(Double.toString(number));
	}
	
	public static void main(String[] args) throws Exception {
		NumberWordsFormat f = new NumberWordsFormat();
		System.out.println(f.convert(4560.87));
	}
}
