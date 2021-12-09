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

/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
public class Period {

	private String id;
	private String name;
	private String longName;
	private Period parent;
	private Vector child;
	private int level;	
	private int sequence;
    private String displayName;
	
	public Period() {
		child = new Vector();
		parent = null;
	}
	
	public void setParent(Period p) {
		parent = p;
	}
	
	public void setLevel(int i) {
		level = i;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setSequence(int i) {
		sequence = i;
	}
	
	public int getSequence() {
		return sequence;
	}
	
	public void setId(String s) {
		id = s;
	}
	
	public void setName(String s) {
		name = s;
	}
	
	//public void setLongName(String s) {
	//	longName = s;
	//}
	
	public void addChild(Period p) {
		p.setParent(this);
		p.setLevel(level + 1);
		p.setSequence(child.size() + 1);
		child.addElement(p);
	}
	
	public Period getParent() {
		return parent;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLongName() {
        //to determine long name
        Period p = getParent();
        String longName = (getParent() != null ? getParent().getName() + ", " : "") + name;        
		return longName;
	}
	
	public Vector<Period> getChild() {
		return child;
	}
	
	public boolean hasChild() {
		if ( child.size() > 0 ) return true;
		else return false;
	}
	
	public Period findChildById(String id) {
		Period result = null;
		for ( int i=0; i < child.size(); i++ ) {
			Period p = (Period) child.elementAt(i);
			if ( id.equals(p.getId())) {
				result = p;
				break;
			} else {
				result = p.findChildById(id);	
				if ( result != null ) return result;
			}
		}
		return result;
	}
	
	public void remove(Period period) {
		for ( int i=0; i < child.size(); i++ ) {
			Period p = (Period) child.elementAt(i);
			if ( period.equals(p)) {
				child.remove(period);
			} else {
				p.remove(period);
			}
		}		
	}
	
	public void remove(String id) {
		for ( int i=0; i < child.size(); i++ ) {
			Period p = (Period) child.elementAt(i);
			if ( p.getId().equals(id)) {
				child.remove(p);
			} else {
				p.remove(id);
			}
		}			
	}
	
	@Override
	public boolean equals(Object o) {
		Period p = (Period) o;
		if ( id.equals(p.getId())) return true;
		else return false;	
	}
    
    @Override
	public String toString() {
        //to determine long name
        //Period p = getParent();
        //String pname = (getParent() != null ? getParent().getName() : "") + " - " + name;
        //return id + ", " + name + ", " + pname;
        return getLongName();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
 
}
