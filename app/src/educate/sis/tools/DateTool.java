/* ************************************************************************
LEBAH PORTAL FRAMEWORK, http://lebah.sf.net
Copyright (C) 2007  Shamsul Bahrin








MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

* ************************************************************************ */
package educate.sis.tools;

/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
public class DateTool {
	static String[] month_short = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	static String[] month_long = {"January", "February", "Marc", "April", "May", "Jun", "July", "August", "September", "October", "November", "December"};
	
	public static String asShortMonth(int year, int month, int day) {
		return day + " " + month_short[month] + ", " + year;		
	}
	
	public static String asLongMonth(int year, int month, int day) {
		return day + " " + month_long[month] + ", " + year;		
	}	
	
	public static String asShortMonth(Integer year, Integer month, Integer day) {
		return day.intValue() + " " + month_short[month.intValue()] + ", " + year.intValue();		
	}	
	
	public static String asLongMonth(Integer year, Integer month, Integer day) {
		return day.intValue() + " " + month_long[month.intValue()] + ", " + year.intValue();		
	}
	
	public static String getDateFormatted(java.util.Date date) {
		return new java.text.SimpleDateFormat ("d MMM, yyyy").format(date);
 	}
 	
	public static String getDateString(java.util.Date date) {
		return new java.text.SimpleDateFormat ("yyyy-MM-dd").format(date);
 	} 			
}
