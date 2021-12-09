package educate.sis.struct;

import java.text.SimpleDateFormat;
import java.util.Date;

import educate.db.DbPersistence;
import educate.sis.general.entity.GradsLevel;
import educate.sis.struct.entity.BillingNumPrefix;
import educate.sis.struct.entity.Course;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;

public class BillingNumGenerator {
	
	
	DbPersistence db = new DbPersistence();
	boolean isSample = false;
	private String billingPrefix = "";
	private int billingDigit = 5;
	
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
	
	public synchronized String generateNumber(Program program, Session intake, LearningCentre centre) throws Exception {
		//get the prefix from template
		BillingNumPrefix template = db.find(BillingNumPrefix.class, "template");
		String prefix = template.getPrefix();
		int digit = template.getDigit();

		//TODO: WHAT IF PREFIX NULL?
		
		prefix = createPrefix(prefix, program, intake, centre);
		
		BillingNumPrefix matric = db.find(BillingNumPrefix.class, prefix);
		if ( matric == null ) {
			db.begin();
			matric = new BillingNumPrefix(prefix);
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
		
		int currentCounter = matric.getCounter();
		
		
		return generateNumber(prefix);
	}

	private String createPrefix(String prefix, Program program, Session intake, LearningCentre centre) {
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
		    String m = intake.getMatricCode();	
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
		return prefix;
	}
	

	
	public synchronized String generateNumber(String pre) throws Exception {
		
		
		
		BillingNumPrefix num = db.find(BillingNumPrefix.class, pre);
		if ( num == null ) {
			db.begin();
			num = new BillingNumPrefix(pre);
			num.setPrefix(pre);
			num.setCounter(1);
			num.setDigit(billingDigit);
			db.persist(num);
			db.commit();
		}

		int count = num.getCounter();
		int digit = num.getDigit();
		char[] cf = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',};

		String billingNumber = num.getPrefix() + new java.text.DecimalFormat(new String(cf, 0, digit)).format(count);

		if ( !isSample ) {
			db.begin();
			num.setCounter(++count);
			db.commit();
		}

		System.out.println("Generated billing number: " + billingNumber);
		
		setBillingPrefix(pre);
		setBillingDigit(digit);
		
		return billingNumber;

	}
	

	public boolean isSample() {
		return isSample;
	}

	public void setSample(boolean isSample) {
		this.isSample = isSample;
	}

	public String getBillingPrefix() {
		return billingPrefix;
	}

	public void setBillingPrefix(String matricPrefix) {
		this.billingPrefix = matricPrefix;
	}

	public int getBillingDigit() {
		return billingDigit;
	}

	public void setBillingDigit(int matricDigit) {
		this.billingDigit = matricDigit;
	}
	
	public void validateCounter(String type, String prefix, int digit) throws Exception {
		
		BillingNumPrefix billingNumPrefix = db.find(BillingNumPrefix.class, prefix);
		if ( billingNumPrefix == null ) {
			db.begin();
			billingNumPrefix = new BillingNumPrefix(prefix);
			billingNumPrefix.setPrefix(prefix);
			billingNumPrefix.setCounter(1);
			billingNumPrefix.setDigit(digit);
			db.persist(billingNumPrefix);
			db.commit();
		}
		else {
			if ( billingNumPrefix.getDigit() != digit ) {
				db.begin();
				billingNumPrefix.setDigit(digit);
				db.commit();
			}
		}
		
		int currentCounter = billingNumPrefix.getCounter();
		int runningCounter = 0;
		int billingDigit = billingNumPrefix.getDigit();
		String billingPrefix = billingNumPrefix.getPrefix();
		
		String str = "";
		for ( int i=0;i<billingDigit;i++) str += "_";
		
		String max = null;
		String sql = "";
		
		if ( "invoice".equals(type)) sql = "select max(s.invoiceNo) from Invoice s where s.invoiceNo LIKE '" + billingPrefix + str + "'";
		if ( "debitnote".equals(type)) sql = "select max(s.invoiceNo) from Invoice s where s.invoiceNo LIKE '" + billingPrefix + str + "'";
		if ( "refund".equals(type)) sql = "select max(s.invoiceNo) from Invoice s where s.invoiceNo LIKE '" + billingPrefix + str + "'";

		if ( "payment".equals(type)) sql = "select max(s.paymentNo) from Payment s where s.paymentNo LIKE '" + billingPrefix + str + "'";
		if ( "creditnote".equals(type)) sql = "select max(s.paymentNo) from Payment s where s.paymentNo LIKE '" + billingPrefix + str + "'";

		max = (String) db.get(sql);

		if ( max != null ) {
			System.out.println(max);
			String counterPart = max.substring(billingPrefix.length());
			System.out.println(counterPart);
			try {
				runningCounter = Integer.parseInt(counterPart) + 1;
			} catch ( Exception e ) {}
		}
		
		System.out.println("Current Counter = " + currentCounter);
		System.out.println("Real Running Counter = " + runningCounter);
		
		if ( runningCounter > currentCounter ) {
			db.begin();
			billingNumPrefix.setCounter(runningCounter);
			db.commit();
		}
		
	}
	
	public void validateBillingNumbers() throws Exception {
		validateCounter("invoice", "INV", billingDigit);
		validateCounter("debitnote", "DN", billingDigit);
		validateCounter("refund", "RF", billingDigit);
		validateCounter("payment", "RB", billingDigit);
		validateCounter("creditnote", "CN", 4);
	}


	public static void main(String[] args) throws Exception {
		BillingNumGenerator g = new BillingNumGenerator();
		int digit = 5;
		g.validateCounter("invoice", "INV", digit);
		g.validateCounter("debitnote", "DN", digit);
		g.validateCounter("refund", "RF", digit);
		g.validateCounter("payment", "RB", digit);
		g.validateCounter("creditnote", "CN", 4);
	}


}
