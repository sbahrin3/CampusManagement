/**
 * 
 */
package educate.util;

import educate.enrollment.entity.Student;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class StudentNameComparator extends educate.util.MyComparator {

	public int compare(Object o1, Object o2) {
		Student s1 = (Student) o1;
		Student s2 = (Student) o2;
		return s1.getBiodata().getName().compareTo(s2.getBiodata().getName());
	}

}
