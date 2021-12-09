package educate.sis.struct;
import java.text.SimpleDateFormat;
import java.util.Date;

import educate.db.DbPersistence;
import educate.sis.general.entity.GradsLevel;
import educate.sis.struct.entity.Course;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.MatricNumPrefix;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.ProgramSessionMatricCode;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.StudentCounter;
import educate.sis.struct.entity.StudyMode;

/**
 * Highly modified version by SAM, from the original written by Jundak
 *  
 */
public class MatricNumGenerator {
	
	DbPersistence db = new DbPersistence();
	boolean isSample = false;
	private String matricPrefix = "";
	private int matricDigit = 0;
	
    static String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();
    
        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e+pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }
	
	public synchronized String generateMatricNum(Program program, Session intake, LearningCentre centre, StudyMode mode) throws Exception {
		//get the prefix from template
		MatricNumPrefix template = db.find(MatricNumPrefix.class, "template");
		boolean globalCounter = template.getGlobalCounter();
		int digit = template.getDigit();
		
		String prefix = template.getPrefix();
		prefix = createPrefix(prefix, program, intake, centre, mode).trim();
		
		String counterScopePrefix = template.getCounterScopePrefix();
		counterScopePrefix = createPrefix(counterScopePrefix, program, intake, centre, mode).trim();
		
		MatricNumPrefix matric = db.find(MatricNumPrefix.class, prefix);
		if ( matric == null ) {
			db.begin();
			matric = new MatricNumPrefix(prefix);
			matric.setPrefix(prefix);
			matric.setCounter(0);
			matric.setDigit(digit);
			matric.setGlobalCounter(globalCounter);
			db.persist(matric);
			db.commit();
		}
		else {
			if ( matric.getDigit() != digit ) {
				db.begin();
				matric.setDigit(digit);
				matric.setGlobalCounter(globalCounter);
				db.commit();
			}
		}
		
		MatricNumPrefix counterScope = db.find(MatricNumPrefix.class, counterScopePrefix);
		if ( counterScope == null ) {
			db.begin();
			counterScope = new MatricNumPrefix(counterScopePrefix);
			counterScope.setPrefix(counterScopePrefix);
			counterScope.setCounter(0);
			counterScope.setDigit(digit);
			counterScope.setGlobalCounter(globalCounter);
			db.persist(counterScope);
			db.commit();
		}
		else {
			if ( counterScope.getDigit() != digit ) {
				db.begin();
				counterScope.setDigit(digit);
				counterScope.setGlobalCounter(globalCounter);
				db.commit();
			}
		}
		
		
		return saveMatricNum(prefix, counterScope, globalCounter, digit);
	}

	private String createPrefix(String prefix, Program program, Session intake, LearningCentre centre, StudyMode mode) {
		//System.out.println(prefix);
		Date date = new Date();
		if ( prefix.indexOf("{YY}") > -1 ) {
		    String yr = new SimpleDateFormat("yy").format(date);	
		    prefix = replace(prefix, "{YY}", yr);
		}
		if ( prefix.indexOf("{YYYY}") > -1 ) {
		    String yr = new SimpleDateFormat("yyyy").format(date);	
		    prefix = replace(prefix, "{YYYY}", yr);
		}
		if ( prefix.indexOf("{MM}") > -1 ) {
		    String m = new SimpleDateFormat("MM").format(date);	
		    prefix = replace(prefix, "{MM}", m);
		}
		if ( prefix.indexOf("{M}") > -1 ) {
		    String m = new SimpleDateFormat("M").format(date);	
		    prefix = replace(prefix, "{M}", m);
		}	
		if ( prefix.indexOf("{LC}") > -1 ) { //LEARNING CENTRE
		    String m = centre.getMatricCode();	
		    prefix = replace(prefix, "{LC}", m);
		}
		if ( prefix.indexOf("{P}") > -1 ) { //PROGRAM OF STUDY
		    String m = program.getMatricCode();	
		    prefix = replace(prefix, "{P}", m);
		}
		if ( prefix.indexOf("{S}") > -1 ) { //INTAKE SESSION
			//System.out.println("intake session. " + intake.getCode());
		    String m = intake.getMatricCode();	
		    //System.out.println("matric code = " + m);
		    prefix = replace(prefix, "{S}", m);
		}
		if ( prefix.indexOf("{C}") > -1 ) { //COURSE
			Course c = program.getCourse();
		    String m = c.getMatricCode();	
		    prefix = replace(prefix, "{C}", m);
		}
		if ( prefix.indexOf("{F}") > -1 ) { //FACULTY
			Faculty f = program.getCourse().getFaculty();
		    String m = f.getMatricCode();	
		    prefix = replace(prefix, "{F}", m);
		}
		if ( prefix.indexOf("{L}") > -1 ) { //LEVEL
			GradsLevel f = program.getLevel();
		    String m = f.getMatricCode();	
		    prefix = replace(prefix, "{L}", m);
		}
		if ( prefix.indexOf("{PS}") > -1 ) { //PROGRAM + SESSION
			String programId = program.getId();
			String sessionId = intake.getId();
			ProgramSessionMatricCode f = (ProgramSessionMatricCode) db.get("select c from ProgramSessionMatricCode c where c.program.id = '" + programId + "' and c.session.id = '" + sessionId + "'");
		    String m = f != null && f.getMatricCode() != null ? f.getMatricCode() : "";	
		    prefix = replace(prefix, "{PS}", m);
		}
		if ( prefix.indexOf("{SM}") > -1 ) { //STUDY MODE
		    String m = mode.getMatricCode();	
		    prefix = replace(prefix, "{SM}", m);
		}
		return prefix;
	}
	
	//always use local counter
	public synchronized String generateRefNumber(String pre) throws Exception {
		return "";
	}
	
	private synchronized String saveMatricNum(String prefix, MatricNumPrefix matric, boolean globalCounter, int digit) throws Exception {
		
		int count = 1;
		if ( !globalCounter ){
			count = matric.getCounter();
			if ( !isSample ) {
				db.begin();
				matric.setCounter(++count);
				db.commit();
			} else {
				count++;
			}
			
		} else {
			//get master counter
			StudentCounter masterCounter = db.find(StudentCounter.class, "master");
			if ( masterCounter == null ) {
				db.begin();
				masterCounter = new StudentCounter();
				masterCounter.setId("master");
				masterCounter.setCounter(1);
				db.persist(masterCounter);
				try {
					db.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			count = masterCounter.getCounter();
			if ( !isSample ) {
				db.begin();
				count++;
				masterCounter.setCounter(count);
				db.commit();
			}
		}

		digit = matric.getDigit();
		char[] cf = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',};

		//String matricNumber = matric.getPrefix();
		String matricNumber = prefix;
		String counterPart = new java.text.DecimalFormat(new String(cf, 0, digit)).format(count);
		
		matricNumber = matricNumber.replaceAll("&", counterPart);
		
		setMatricPrefix(prefix);
		setMatricDigit(digit);
		

		return matricNumber;

	}
	
	
	

	public synchronized String generateMatricNum(String pre) throws Exception {
		
		boolean globalCounter = true;

		String prefix = createPrefix(pre, null, null, null, null).trim();

		MatricNumPrefix matric = db.find(MatricNumPrefix.class, prefix);
		if ( matric == null ) {
			db.begin();
			matric = new MatricNumPrefix(prefix);
			matric.setPrefix(prefix);
			matric.setCounter(1);
			matric.setDigit(5);
			matric.setGlobalCounter(true);
			db.persist(matric);
			db.commit();
		}
		else {
			globalCounter = matric.getGlobalCounter();
		}

		int count = 1;
		
		if ( !globalCounter ){
			count = matric.getCounter();
			if ( !isSample ) {
				db.begin();
				matric.setCounter(++count);
				db.commit();
			}
			
		} else {
			//get master counter
			StudentCounter masterCounter = db.find(StudentCounter.class, "master");
			if ( masterCounter == null ) {
				db.begin();
				masterCounter = new StudentCounter();
				masterCounter.setId("master");
				masterCounter.setCounter(1);
				db.persist(masterCounter);
				try {
					db.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			count = masterCounter.getCounter();
			if ( !isSample ) {
				db.begin();
				count++;
				masterCounter.setCounter(count);
				db.commit();
			}
		}

		System.out.println("global counter = " + globalCounter);
		System.out.println("counter = " + count);
		
		int digit = matric.getDigit();
		char[] cf = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',};

		String matricNumber = matric.getPrefix();
		String counterPart = new java.text.DecimalFormat(new String(cf, 0, digit)).format(count);
		
		matricNumber = matricNumber.replaceAll("&", counterPart);
		
		setMatricPrefix(pre);
		setMatricDigit(digit);
		
		return matricNumber;

	}
	
	
	public synchronized String generateReferenceNo(String templateName) throws Exception {
		//get the prefix from template
		MatricNumPrefix template = db.find(MatricNumPrefix.class, templateName);
		String prefix = template.getPrefix();
		int digit = template.getDigit();

		//TODO: WHAT IF PREFIX NULL?
		
		prefix = createPrefix(prefix).trim();
		
		MatricNumPrefix matric = db.find(MatricNumPrefix.class, prefix);
		if ( matric == null ) {
			db.begin();
			matric = new MatricNumPrefix(prefix);
			matric.setPrefix(prefix);
			matric.setCounter(1);
			matric.setDigit(digit);
			db.persist(matric);
			db.commit();
		}
		else {
			if ( matric.getDigit() != digit ) {
				db.begin();
				matric.setDigit(digit);
				db.commit();
			}
		}
		
		return generateReferenceNo2(prefix);
	}
	
	private String createPrefix(String prefix) {
		Date date = new Date();
		if ( prefix.indexOf("{YY}") > -1 ) {
		    String yr = new SimpleDateFormat("yy").format(date);	
		    prefix = replace(prefix, "{YY}", yr);
		}
		if ( prefix.indexOf("{YYYY}") > -1 ) {
		    String yr = new SimpleDateFormat("yyyy").format(date);	
		    prefix = replace(prefix, "{YYYY}", yr);
		}
		if ( prefix.indexOf("{MM}") > -1 ) {
		    String m = new SimpleDateFormat("MM").format(date);	
		    prefix = replace(prefix, "{MM}", m);
		}
		if ( prefix.indexOf("{M}") > -1 ) {
		    String m = new SimpleDateFormat("M").format(date);	
		    prefix = replace(prefix, "{M}", m);
		}	

		return prefix;
	}
		
	
	public synchronized String generateReferenceNo2(String pre) throws Exception {
		
		String prefix = createPrefix(pre).trim();
		MatricNumPrefix matric = db.find(MatricNumPrefix.class, prefix);
		if ( matric == null ) {
			db.begin();
			matric = new MatricNumPrefix(prefix);
			matric.setPrefix(prefix);
			matric.setCounter(1);
			matric.setDigit(5);
			db.persist(matric);
			db.commit();
		}

		int count = matric.getCounter();
		int digit = matric.getDigit();
		char[] cf = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',};

		String matricNumber = matric.getPrefix();
		String counterPart = new java.text.DecimalFormat(new String(cf, 0, digit)).format(count);
		
		matricNumber = matricNumber.replaceAll("&", counterPart);
		
		db.begin();
		matric.setCounter(++count);
		db.commit();
		
		setMatricPrefix(pre);
		setMatricDigit(digit);
		return matricNumber;

	}	
	



	public boolean isSample() {
		return isSample;
	}

	public void setSample(boolean isSample) {
		this.isSample = isSample;
	}

	public String getMatricPrefix() {
		return matricPrefix;
	}

	public void setMatricPrefix(String matricPrefix) {
		this.matricPrefix = matricPrefix;
	}

	public int getMatricDigit() {
		return matricDigit;
	}

	public void setMatricDigit(int matricDigit) {
		this.matricDigit = matricDigit;
	}
	
	public void validateCounter(Program program, Session intake, LearningCentre centre, StudyMode mode) throws Exception {
		//get the prefix from template
		MatricNumPrefix template = db.find(MatricNumPrefix.class, "template");
		String prefix = template.getPrefix();
		boolean globalCounter = template.getGlobalCounter();
		
		int digit = template.getDigit();

		//TODO: WHAT IF PREFIX NULL?
		prefix = createPrefix(prefix, program, intake, centre, mode);
		
		MatricNumPrefix matric = db.find(MatricNumPrefix.class, prefix);
		if ( matric == null ) {
			db.begin();
			matric = new MatricNumPrefix(prefix);
			matric.setPrefix(prefix);
			matric.setCounter(1);
			matric.setDigit(digit);
			matric.setGlobalCounter(globalCounter);
			db.persist(matric);
			db.commit();
		}
		else {
			if ( matric.getDigit() != digit ) {
				db.begin();
				matric.setDigit(digit);
				matric.setGlobalCounter(globalCounter);
				db.commit();
			}
		}
		
		int currentCounter = matric.getCounter();
		int runningCounter = 0;
		int matricDigit = matric.getDigit();
		String matricPrefix = matric.getPrefix();
		
		String str = "";
		for ( int i=0;i<matricDigit;i++) str += "_";
		
		String sql = "select max(s.matricNo) from Student s where s.matricNo LIKE '" + matricPrefix + str + "'";
		String max = (String) db.get(sql);
		if ( max != null ) {
			String counterPart = max.substring(matricPrefix.length());
			try {
				runningCounter = Integer.parseInt(counterPart) + 1;
			} catch ( Exception e ) {}
		}
		
		//System.out.println("Current Counter = " + currentCounter);
		//System.out.println("Real Running Counter = " + runningCounter);
		
		if ( runningCounter > currentCounter ) {
			db.begin();
			matric.setCounter(runningCounter);
			db.commit();
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		//String ref = new MatricNumGenerator().generateReferenceNo("convocation");
		//System.out.println(ref);
		
		String prefix = "{P}&/{S}";
		System.out.println(prefix.replaceAll("&", ""));
	}
	
	

}
