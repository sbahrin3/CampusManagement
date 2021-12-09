/* ************************************************************************
LEBAH PORTAL FRAMEWORK, http://lebah.sf.net
Copyright (C) 2007  Shamsul Bahrin








MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

* ************************************************************************ */

package portal.module;

import java.util.Date;
import java.util.TimeZone;

/**
 * 
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 * @version 0.1
 */
public class Util {
	
	public static int getNumber(String num) {
		if ( num == null || "".equals(num)) return 0;
		try {
			return Integer.parseInt(num);
		} catch ( Exception e ) {
			return 0;
		}
	}
	
	public static String displayText(String str) {
		if ( str.length() > 300 )
			return str.substring(0, 299);
		else
			return str;
	}
	
	public static String displayText(String str, int i) {
		if ( str.length() > i)
			return str.substring(0, i - 1);
		else
			return str;
	}
	
    public static String putLineBreak(String str) {
        if (str != null) {
            StringBuffer txt = new StringBuffer(str);
            char c = '\n';
            while (txt.toString().indexOf(c) > -1) {
                int pos = txt.toString().indexOf(c);
                txt.replace(pos, pos + 1, "<br>");
            }
            return txt.toString();
        } else {
            return "";
        }
    }
    
    public static String getDateTime(Date date, String format) {
    	return new java.text.SimpleDateFormat (format).format(date);
    }
    
    public boolean isImageFile(String fileName) {
    	return isTypeOfFile(fileName, ".jpg .jpeg .gif .png .JPG .JPEG .GIF .PNG");
    }
    
    public boolean isHtmlFile(String fileName) {
    	return isTypeOfFile(fileName, ".htm .html .HTM .HTML");
    }
    
    public boolean isXMLFile(String fileName) {
    	return isTypeOfFile(fileName, ".xml .XML");
    }
    
    public boolean isFlashFile(String fileName) {
    	return isTypeOfFile(fileName, ".swf .SWF");
    }

	private boolean isTypeOfFile(String fileName, String allowedExt) {
        try {
            String ext = fileName.substring(fileName.lastIndexOf('.'));
        	if ( allowedExt.indexOf(ext) > -1 ) return true;
        	else return false;
        } catch ( Exception e ) {
            return false;
        }
	}
    
   
    public static String[] getTimeZoneIds() {
        return TimeZone.getAvailableIDs();
    }
    

	public static String formatDecimal(float number) {
		try {
			return new java.text.DecimalFormat("#,###,###.00").format(number);
		} catch ( Exception e ) {
			return Float.toString(number);
		}
	}
	
	public static String formatDecimal(Float number) {
		if ( number == null ) return  "";
		try {
			return new java.text.DecimalFormat("#,###,###.00").format(number);
		} catch ( Exception e ) {
			return Float.toString(number);
		}
	}	
	
	public static String formatDecimal(double number) {
		try {
			return new java.text.DecimalFormat("#,###,###.00").format(number);
		} catch ( Exception e ) {
			return Double.toString(number);
		}
	}	
	
	public static String formatDecimal(Double number) {
		if ( number == null ) return  "";
		try {
			return new java.text.DecimalFormat("#,###,###.00").format(number);
		} catch ( Exception e ) {
			return Double.toString(number);
		}
	}
	
	
	public static String capitalizedFirstCharacter(String str) {
		boolean cap = true;
		StringBuffer s = new StringBuffer();
		for ( int i=0; i < str.length(); i++ ) {
			char c = str.charAt(i);
			if ( cap ) s.append(String.valueOf(c).toUpperCase());
			else s.append(String.valueOf(c).toLowerCase()); 
			cap = false;
			if ( c == ' ' ) cap = true;
		}
		return s.toString();
	}
	
   
	public static String urlEncode(String str) {
		try {
			return java.net.URLEncoder.encode(str, "UTF-8");
		} catch ( Exception e ) {
			return str;
		}
	}
	
	public static boolean dateAfter(Date date1, Date date2) {
		return date1.after(date2);
	}
	
	public static boolean dateBefore(Date date1, Date date2) {
		return date1.before(date2);
	}
  

}
