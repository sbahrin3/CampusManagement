package educate.sis.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import educate.db.DbPersistence;
import educate.sis.exam.entity.Grade;
import educate.sis.exam.entity.MarkingGrade;

public class ExamResultUtil {
	
	
	public static Grade getGradeByPoint(String id, double point) {
		DbPersistence db = new DbPersistence();
		MarkingGrade marking = db.find(MarkingGrade.class, id);
		Set<Grade> grades = marking.getGrades();
		Grade grade = null;
		for ( Grade g : grades ) {
			if ( point == g.getPoint() ) {
				grade = g;
				break;
			}
		}
		//if above failed, make an assumption
		if ( grade == null ) {
			List<Grade> list = new ArrayList<Grade>();
			list.addAll(grades);
			Collections.sort(list, new GradeComparator());
			int i = 0;
			for ( Grade g : list ) {
				if ( point > g.getPoint()) {
					break;
				}
				i++;
			}
			if ( i > 0 ) grade = list.get(i-1);
		}
		return grade;		
	}
	
	public static Grade getGrade(String id, double value) {
		DbPersistence db = new DbPersistence();
		MarkingGrade marking = db.find(MarkingGrade.class, id);

		Set<Grade> grades = marking.getGrades();
		Grade grade = null;
		for ( Grade g : grades ) {
			if ( value >= g.getMinMark() && value < g.getMaxMark() ) {
				grade = g;
				break;
			}
		}
		//if above failed, make an assumption
		if ( grade == null ) {
			List<Grade> list = new ArrayList<Grade>();
			list.addAll(grades);
			Collections.sort(list, new GradeComparator());
			int i = 0;
			for ( Grade g : list ) {
				//System.out.println(value + " " + g.getMinMark() + ", " + g.getMaxMark());
				if ( value > g.getMaxMark() ) {
					break;
				}
				i++;
			}
			if ( i > 0 ) grade = list.get(i-1);
		}
		return grade;
	}
	
	
	public static Grade getDefaultGrade(double value) {
		DbPersistence db = new DbPersistence();
		MarkingGrade marking = (MarkingGrade) db.get("select m from MarkingGrade m where m.name = 'DEFAULT'");

		Set<Grade> grades = marking.getGrades();
		Grade grade = null;
		for ( Grade g : grades ) {
			if ( value >= g.getMinMark() && value < g.getMaxMark() ) {
				grade = g;
				break;
			}
		}
		//if above failed, make an assumption
		if ( grade == null ) {
			List<Grade> list = new ArrayList<Grade>();
			list.addAll(grades);
			Collections.sort(list, new GradeComparator());
			int i = 0;
			for ( Grade g : list ) {
				//System.out.println(value + " " + g.getMinMark() + ", " + g.getMaxMark());
				if ( value > g.getMaxMark() ) {
					break;
				}
				i++;
			}
			if ( i > 0 ) grade = list.get(i-1);
		}
		return grade;
	}
	
	public static Grade getGrade(String id, String gradeLetter) {
		DbPersistence db = new DbPersistence();
		MarkingGrade marking = db.find(MarkingGrade.class, id);
		Set<Grade> grades = marking.getGrades();
		Grade grade = null;
		for ( Grade g : grades ) {
			if ( gradeLetter.equals(g.getLetter()) ) {
				grade = g;
				break;
			}
		}
		return grade;
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
	
	public static void main(String[] args) {
		String id = "04513abd2b0529f18c8c224012df524467ac2ee9";
		System.out.println(getGrade(id, 85).getLetter());
		System.out.println(getGrade(id, 67).getLetter());
		System.out.println(getGrade(id, 47).getLetter());
		System.out.println(getGrade(id, 58).getLetter());
		System.out.println(getGrade(id, 44).getLetter());
	}
}
