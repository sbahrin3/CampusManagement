package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.exam.entity.Grade;
import educate.sis.exam.entity.MarkingGrade;
import lebah.portal.action.AjaxModule;

public class GradeSchemeSetupModule extends AjaxModule {
	
	String path = "apps/util/grade_setup/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "list_marking".equals(command)) listMarking();
		else if ( "get_marking".equals(command)) getMarking();
		else if ( "add_grade".equals(command)) addGrade();
		else if ( "delete_grade".equals(command)) deleteGrade();
		else if ( "save_grades".equals(command)) saveGrades();
		else if ( "empty".equals(command)) empty();
		else if ( "new_marking".equals(command)) newMarking();
		else if ( "create_marking".equals(command)) createMarking();
		else if ( "delete_marking".equals(command)) deleteMarking();
		return vm;
	}

	private void listMarking() {
		context.remove("grade_error");
		getMarking();
	}

	private void deleteMarking() throws Exception {
		vm = path + "select_marking.vm";
		String markingId = request.getParameter("marking_id");
		MarkingGrade marking = db.find(MarkingGrade.class, markingId);
		db.begin();
		db.remove(marking);
		db.commit();
		
		String sql = "select m from MarkingGrade m";
		List<MarkingGrade> markings = db.list(sql);
		context.put("markings", markings);	
		
	}

	private void createMarking() throws Exception {
		vm = path + "select_marking.vm";
		String markingName = request.getParameter("marking_name");
		MarkingGrade marking = new MarkingGrade();
		marking.setName(markingName);
		db.begin();
		db.persist(marking);
		db.commit();
		
		String sql = "select m from MarkingGrade m";
		List<MarkingGrade> markings = db.list(sql);
		context.put("markings", markings);		
		
	}

	private void newMarking() {
		vm = path + "new_marking.vm";
		
	}

	private void empty() {
		vm = path + "empty.vm";
		
	}

	private void saveGrades() throws Exception {
		String markingId = request.getParameter("marking_id");
		MarkingGrade marking = db.find(MarkingGrade.class, markingId);
		Set<Grade> grades = marking.getGrades();
		
		
		
		for ( Grade g : grades ) {
			String gradeId = g.getId();
			String min = request.getParameter("min_" + gradeId);
			String max = request.getParameter("max_" + gradeId);
			String point = request.getParameter("point_" + gradeId);
			String letter = request.getParameter("letter_" + gradeId);
			letter = letter.replaceAll("%2B", "+");
			double _min = 0.0, _max = 0.0, _point = 0.0;
			try {
				_min = Double.parseDouble(min);
			} catch ( Exception e ) {}
			try {
				_max = Double.parseDouble(max);
			} catch ( Exception e ) {}
			try {
				_point = Double.parseDouble(point);
			} catch ( Exception e ) {}
			
			//System.out.println(letter);
			
			g.setLetter(letter);
			g.setMinMark(_min);
			g.setMaxMark(_max);
			g.setPoint(_point);
			g.setMarkingGrade(marking);
		}

		if ( checkOverlappingGrade(grades) ) {
			db.begin();
			db.commit();
			context.remove("grade_error");
		}
		getMarking();
		
	}

	private boolean checkOverlappingGrade(Set<Grade> grades) {
		boolean ok = true;
		List<Grade> list = new ArrayList<Grade>();
		list.addAll(grades);
		if ( list.size() > 0 ) {
			for ( int i = 0; i < list.size(); i++ ) {
				Grade g1 = list.get(i);
				for ( int j = 0; j < list.size(); j++ ) {
					if ( i != j ) {
						Grade g2 = list.get(j);
						if ( g1.getMinMark() >= g2.getMinMark() && g1.getMaxMark() <= g2.getMaxMark()) {
							ok = false;
							context.put("grade_error", "Grade Overlapping Error");
							context.put("g1", g1);
							context.put("g2", g2);
							break;
						}
						if ( g1.getMinMark() > g2.getMinMark() && g1.getMinMark() < g2.getMaxMark()) {
							ok = false;
							context.put("grade_error", "Grade Overlapping Error");
							context.put("g1", g1);
							context.put("g2", g2);
							break;
						}						
					}
				}
				if ( !ok ) break;
			}
		}
		return ok;
	}

	private void deleteGrade() throws Exception {
		String markingId = request.getParameter("marking_id");
		MarkingGrade marking = db.find(MarkingGrade.class, markingId);
		
		String gradeId = request.getParameter("grade_id");
		Grade grade = db.find(Grade.class, gradeId);
		
		db.begin();
		marking.getGrades().remove(grade);
		db.remove(grade);
		db.commit();
		context.remove("grade_error");
		getMarking();
		
	}

	private void addGrade() throws Exception {
		String markingId = request.getParameter("marking_id");
		MarkingGrade marking = db.find(MarkingGrade.class, markingId);

		String min = request.getParameter("min");
		String max = request.getParameter("max");
		String point = request.getParameter("point");
		String letter = request.getParameter("letter");
		letter = letter.replaceAll("%2B", "+");
		
		
		double _min = 0.0, _max = 0.0, _point = 0.0;
		try {
			_min = Double.parseDouble(min);
		} catch ( Exception e ) {}
		try {
			_max = Double.parseDouble(max);
		} catch ( Exception e ) {}
		try {
			_point = Double.parseDouble(point);
		} catch ( Exception e ) {}
		Grade g = new Grade();
		g.setLetter(letter);
		g.setMinMark(_min);
		g.setMaxMark(_max);
		g.setPoint(_point);
		g.setMarkingGrade(marking);
		
		db.begin();
		marking.getGrades().add(g);
		db.commit();
		
		checkOverlappingGrade(marking.getGrades());

		getMarking();
	}

	private void getMarking() {
		vm = path + "grades.vm";
		String markingId = request.getParameter("marking_id");
		MarkingGrade marking = db.find(MarkingGrade.class, markingId);
		context.put("marking", marking);
		Set<Grade> grades = marking.getGrades();
		List<Grade> list = new ArrayList<Grade>();
		list.addAll(grades);
		Collections.sort(list, new GradeComparator());
		context.put("grades", list);
		checkOverlappingGrade(grades);
	}

	private void start() {
		vm = path + "markings.vm";
		String sql = "select m from MarkingGrade m";
		List<MarkingGrade> markings = db.list(sql);
		context.remove("grade_error");
		context.put("markings", markings);
		
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

}
