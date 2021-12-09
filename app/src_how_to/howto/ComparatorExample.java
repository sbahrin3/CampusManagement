package howto;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;
import educate.enrollment.entity.Student;
import educate.sis.exam.entity.AssessmentResult;
import educate.sis.exam.entity.Grade;

public class ComparatorExample {
	
	static class TestComparator extends educate.util.MyComparator {
		
		public int compare(Object o1, Object o2) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			return s1.compareTo(s2);
		}
		
	}
	
	class StudentByIntakeComparator extends educate.util.MyComparator {
		public int compare(Object o1, Object o2) {
			Student s1 = (Student) o1;
			Student s2 = (Student) o2;
			if ( s1.getIntake() == null || s2.getIntake() == null ) return 0;
			if ( s1.getIntake().getStartDate().before(s2.getIntake().getStartDate())) return 1;
			else if ( s1.getIntake().getStartDate().after(s2.getIntake().getStartDate())) return -1;
			return 0;
		}
	}	
	
	class AssessmentSequenceComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			AssessmentResult r1 = (AssessmentResult) o1;
			AssessmentResult r2 = (AssessmentResult) o2;
			if ( r1.getCourseworkItem() != null )
				return r1.getCourseworkItem().getSequence() > r2.getCourseworkItem().getSequence() ? 1 : -1;
			return 0;
		}
		
	}
	
	static class GradeComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			Grade g1 = (Grade) o1;
			Grade g2 = (Grade) o2;
			if ( g1.getMinMark() > g2.getMinMark() ) return -1;
			else if ( g1.getMinMark() < g2.getMinMark() ) return 1;
			return 0;
		}
		
	}
	

	
	public static void main(String[] args) throws Exception {
		
		List<String> list = new ArrayList<String>();
		list.add("V");
		list.add("C");
		list.add("F");
		System.out.println("Before");
		for ( String s : list ) {
			System.out.println(s);
		}
		Collections.sort(list, new TestComparator());
		System.out.println("After");
		for ( String s : list ) {
			System.out.println(s);
		}
	}

}
