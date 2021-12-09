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


import java.util.List;
import java.util.Vector;

import educate.sis.struct.entity.Course;
import lebah.db.PersistenceManager;

/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
public class CourseData {
	private PersistenceManager pm;
	private Course course;
	public Vector<Course> getList() throws Exception {
		pm = new PersistenceManager();
		List<Course> l = pm.list("SELECT a FROM Course a order by a.code");	
		Vector<Course> v = new Vector<Course>();
		v.addAll(l);
		return v;
	}	
	
	public Course getCourse(String courseId) throws Exception {
		pm = new PersistenceManager();
		course = (Course)pm.find(Course.class,courseId);
		return course;
				
	}
	
	public Vector<Course> getCourseFaculty(String facultyId) throws Exception{
		pm = new PersistenceManager();
		List<Course> l = pm.list("SELECT a FROM Course a WHERE a.faculty.id='"+facultyId+"'");	
		Vector<Course> v = new Vector<Course>();
		v.addAll(l);
		return v;
	}
	
	public void add(Course a) throws Exception{
		pm = new PersistenceManager();
		PersistenceManager.add(a);
	}
	public void update(String id, Course a) throws Exception {
		pm = new PersistenceManager();
		course =(Course)pm.find(Course.class).whereId(id).forUpdate();
		course.setCode(a.getCode());
		course.setName(a.getName());
		course.setFaculty(a.getFaculty());
		pm.update();
	}
	public void delete(String id) throws Exception{
		pm = new PersistenceManager();
		course = (Course)pm.find(Course.class,id);
		pm.delete(course);
	}
}
