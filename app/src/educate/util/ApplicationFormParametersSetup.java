package educate.util;

import educate.db.DbPersistence;
import educate.sis.general.entity.Country;
import educate.sis.general.entity.DegreeClass;
import educate.sis.general.entity.GeneralExamGrade;
import educate.sis.general.entity.Nationality;
import educate.sis.general.entity.Religion;
import educate.sis.general.entity.SchoolSubject;
import educate.sis.general.entity.StudyLevel;

public class ApplicationFormParametersSetup {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		//addSubjects(db);
		//addLevels(db);
		//addDegreeClass(db);
		addGeneralExamGrades(db);
		//addNationalities(db);
		//addReligions(db);
		//addCountries(db);
		
	}
	
	public static void run() throws Exception {
		DbPersistence db = new DbPersistence();
		
		System.out.println("Add Subjects");
		addSubjects(db);
		System.out.println("Add Study Levels");
		addLevels(db);
		System.out.println("Add Degree Classes");
		addDegreeClass(db);
		System.out.println("Add Degree Classes");
		addGeneralExamGrades(db);
		System.out.println("Done...");
		addNationalities(db);
		addReligions(db);
		addCountries(db);
	}
	
	private static void addCountries(DbPersistence db) throws Exception {
		addCountry(db, "MY", "MALAYSIA");
		addCountry(db, "SG", "SINGAPORE");
		addCountry(db, "ID", "INDONESIA");
		addCountry(db, "IN", "INDIA");
		addCountry(db, "TH", "THAILAND");
		addCountry(db, "JP", "JAPAN");
		addCountry(db, "PH", "PHILIPPINE");
		addCountry(db, "AU", "AUSTRALIA");
	}
	
	private static void addCountry(DbPersistence db, String code, String name) throws Exception {
		db.begin();
		Country c = new Country();
		c.setId(code);
		c.setCode(code);
		c.setName(name);
		c.setAbbrev(code);
		db.persist(c);
		db.commit();
	}
	
	private static void addReligions(DbPersistence db) throws Exception {
		addReligion(db, "1", "ISLAM");
		addReligion(db, "2", "CHRISTIAN");
		addReligion(db, "3", "HINDU");
		addReligion(db, "4", "BUDDHA");
		addReligion(db, "5", "SIKH");
	}
	
	private static void addReligion(DbPersistence db, String code, String name) throws Exception {
		db.begin();
		Religion r = new Religion();
		r.setId(code);
		r.setCode(code);
		r.setName(name);
		db.persist(r);
		db.commit();
	}
	
	private static void addNationalities(DbPersistence db) throws Exception {
		addNationality(db, "MY", "MALAYSIA");
		addNationality(db, "SG", "SINGAPORE");
		addNationality(db, "ID", "INDONESIA");
		addNationality(db, "TH", "THAILAND");
		addNationality(db, "IN", "INDIA");
	}
	
	private static void addNationality(DbPersistence db, String code, String name) throws Exception {
		db.begin();
		Nationality n = new Nationality();
		n.setId(code);
		n.setCode(code);
		n.setName(name);
		db.persist(n);
		db.commit();
	}
	
	private static void addGeneralExamGrades(DbPersistence db) throws Exception {
		
		db.begin();
		db.executeUpdate("delete from GeneralExamGrade g");
		db.commit();
		
		
		
		addGrade(db, "1", "A+", 1);
		addGrade(db, "2", "A", 2);
		addGrade(db, "3", "A-", 3);
		addGrade(db, "4", "B+", 4);
		addGrade(db, "5", "B", 5);
		addGrade(db, "6", "B-", 6);
		addGrade(db, "7", "C+", 7);
		addGrade(db, "8", "C", 8);
		addGrade(db, "9", "C-", 9);
		addGrade(db, "10", "D+", 10);
		addGrade(db, "11", "D", 11);
		addGrade(db, "12", "D-", 12);
		addGrade(db, "13", "E", 13);
		addGrade(db, "14", "F", 14);
		addGrade(db, "15", "G", 15);
		
		addGrade(db, "30", "--", 16);
		
		addGrade(db, "31", "1A", 17);
		addGrade(db, "32", "2A", 18);
		addGrade(db, "33", "3B", 19);
		addGrade(db, "34", "4B", 20);
		addGrade(db, "35", "5C", 21);
		addGrade(db, "36", "6C", 22);
		addGrade(db, "37", "7D", 23);
		addGrade(db, "38", "8E", 24);
		addGrade(db, "39", "9G", 25);
		
		
		
		
		
	}
	
	private static void addGrade(DbPersistence db, String id, String name, int rank) throws Exception {
		db.begin();
		GeneralExamGrade g = new GeneralExamGrade();
		g.setId(id);
		g.setName(name);
		g.setRanking(rank);
		db.persist(g);
		db.commit();
	}
	
	private static void addLevels(DbPersistence db) throws Exception {
//		db.begin();
//		db.executeUpdate("delete from StudyLevel l");
//		db.commit();
		
		addLevel(db, "CERT", "CERTIFICATE");
		addLevel(db, "BAC", "DEGREE");
		addLevel(db, "MA", "MASTER");
		addLevel(db, "DOC", "DOCTORATE");
		
	}
	

	private static void addDegreeClass(DbPersistence db) throws Exception {
//		db.begin();
//		db.executeUpdate("delete from DegreeClass d");
//		db.commit();
		
		addDegreeClass(db, "1ST", "1ST CLASS");
		addDegreeClass(db, "2NDU", "2ND CLASS UPPER");
		addDegreeClass(db, "2NDL", "2ND CLASS LOWER");
		addDegreeClass(db, "3RD", "3RD CLASS");
	}
	

	private static void addSubjects(DbPersistence db) throws Exception {
//		db.begin();
//		db.executeUpdate("delete from SchoolSubject s");
//		db.commit();
		
		addSubject(db, "SPM-BM", "BAHASA MALAYSIA", "SPM");
		addSubject(db, "SPM-EN", "ENGLISH LANGUAGE", "SPM");
		addSubject(db, "SPM-SC", "SAINS", "SPM");
		addSubject(db, "SPM-MA", "MATEMATIK", "SPM");
		
		
		addSubject(db, "STPM-BM", "BAHASA MALAYSIA", "STPM");
		addSubject(db, "STPM-EN", "ENGLISH LANGUAGE", "STPM");
		addSubject(db, "STPM-SC", "SAINS", "STPM");
		addSubject(db, "STPM-MA", "MATEMATIK", "STPM");
	}
	
	static void addSubject(DbPersistence db, String code, String name, String type) throws Exception {
		db.begin();
		SchoolSubject s = new SchoolSubject();
		s.setId(code);
		s.setCode(code);
		s.setName(name);
		s.setType(type);
		db.persist(s);
		db.commit();
	}
	
	static void addLevel(DbPersistence db, String code, String name) throws Exception {
		db.begin();
		StudyLevel s = new StudyLevel();
		s.setId(code);
		s.setCode(code);
		s.setName(name);
		db.persist(s);
		db.commit();
	}
	
	static void addDegreeClass(DbPersistence db, String code, String name) throws Exception {
		db.begin();
		DegreeClass s = new DegreeClass();
		s.setId(code);
		s.setCode(code);
		s.setName(name);
		db.persist(s);
		db.commit();
		
	}

}
