package educate.admission;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import educate.admission.entity.Applicant;
import educate.admission.entity.RefNo;
import educate.db.DbPersistence;
import educate.sis.billing.entity.Discount;
import educate.sis.general.entity.ModeOfStudy;
import educate.sis.general.entity.RegVanue;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectPeriod;
import lebah.db.PersistenceManager;
import lebah.util.DateUtil;

public class Application {

	Vector<Applicant> listOfApplicants;
	private PersistenceManager pm; 
	//private Applicant applicant;

	public static final String NEW_STATUS = "NEW";
	public static final String REJECT_STATUS = "REJECTED";
	public static final String OFFERED_STATUS = "OFFERED";
	public static final String INCOMPLETE_STATUS = "INCOMPLETE";
	public static final String TRASH_STATUS = "TRASH";
	public static final String REGISTERED_STATUS = "REGISTERED";
	public static final String PROCESS_STATUS = "PROCESSING";
	public static final String ACCEPT_STATUS = "ACCEPTED";
	public static final String CONDITIONAL_STATUS = "OFFERED_CONDITIONALLY";
	public static final String SHORTLISTED_STATUS = "SHORTLISTED";
	public static final String DECLINE_STATUS = "DECLINE";
	// Add by Shaiful Nizam, 2009-05-12.
	public static final String DEFECT_STATUS = "DEFECT";

	private final String REF_ID = "APP";
	private final int REF_LENGTH = 5;
	private final String REF_PREFIX = "REF";
	private String className = this.getClass().getName();
	
	public String[] getStatuses() {
		String[] statuses = {
				NEW_STATUS,
				INCOMPLETE_STATUS,
				OFFERED_STATUS,
				CONDITIONAL_STATUS,
				REGISTERED_STATUS,
				REJECT_STATUS,
				"DELETED"
		};
		return statuses;
	}
	
	public String[] getChangeStatuses() {
		String[] statuses = {
				OFFERED_STATUS,
				CONDITIONAL_STATUS,
				SHORTLISTED_STATUS,
				REJECT_STATUS,
				INCOMPLETE_STATUS,
				DEFECT_STATUS
		};
		return statuses;
	}

	public String save(Applicant app) throws Exception {
		String id = app.getId();
		PersistenceManager pm = null;
		try {
			pm = new PersistenceManager();
			if (isExist(id)) {
				//System.out.println("["+className+".save()] update applicant");
				Applicant applicant = (Applicant) pm.find(Applicant.class).whereId(
						app.getId()).forUpdate();
				
				applicant.setBiodata(app.getBiodata());
				applicant.setChoice1(app.getChoice1());
				applicant.setChoice2(app.getChoice2());
				applicant.setChoice3(app.getChoice3());
				applicant.setAddress(app.getAddress());
				applicant.setAlevelResults(app.getAlevelResults());
				applicant.setBioInc(app.getBioInc());
				applicant.setBmResult(app.getBmResult());
				applicant.setCertificates(app.getCertificates());
				//add accept (10-06-09)
				applicant.setAccept(app.getAccept());
				applicant.setConditional(app.getConditional());
				applicant.setCurrentEmployment(app.getCurrentEmployment());
				applicant.setDisability(app.getDisability());
				applicant.setEduBackInc(app.getEduBackInc());
				applicant.setEmpBackInc(app.getEmpBackInc());
				applicant.setEnglishProfiency(app.getEnglishProfiency());
				applicant.setEmployments(app.getEmployments());
				applicant.setGradslevel(app.getGradslevel());
				applicant.setHighestEducation(app.getHighestEducation());
				applicant.setLearningCentre(app.getLearningCentre());
				applicant.setModeOfStudy(app.getModeOfStudy());
				applicant.setNationalityType(app.getNationalityType());
				applicant.setOlevelResults(app.getOlevelResults());
				applicant.setReferenceNo(app.getReferenceNo());
				applicant.setResearchArea(app.getResearchArea());
				applicant.setResearchLocation(app.getResearchLocation());
				applicant.setResearchTitle(app.getResearchTitle());
				applicant.setSecondaries(app.getSecondaries());
				//applicant.setSpmResults(app.getSpmResults());
				applicant.setSpmYear(app.getSpmYear());
				applicant.setSponsor(app.getSponsor());
				//applicant.setStpmResults(app.getStpmResults());
				applicant.setStpmYear(app.getStpmYear());
				applicant.setTertiaries(app.getTertiaries());
				applicant.setTransactionType(app.getTransactionType());
				applicant.setSurvey(app.getSurvey());
				applicant.setSupervisorAddress(app.getSupervisorAddress());
				applicant.setSupervisorContactNo(app.getSupervisorContactNo());
				applicant.setSupervisorName(app.getSupervisorName());
				// Add by Shaiful Nizam, 2009-07-01.
				applicant.setPermanentAddress(app.getPermanentAddress());

				if (applicant.getStatus().equals(INCOMPLETE_STATUS)) {
					//System.out.println("masuk if..status");
					applicant.setStatus(NEW_STATUS);
				}
				pm.update();

				return "Record Updated Succesfully with status "
						+ applicant.getStatus();
			} else {
				//System.out.println("["+className+".save()] insert applicant");
				app.setReferenceNo(genRefNo());
				app.setStatus(NEW_STATUS);
				app.setRegisterDate(new DateUtil().getToday());
				//System.out.println("["+className+".save()] Nationality type = " + app.getNationalityType());
				//System.out.println("["+className+".save()] ICNO = " + app.getBiodata().getIcno());
				app.setLogin(app.getBiodata().getIcno());
				
				/*
				 * To capture application date
				 */
				Date date = new Date();
				app.setApplyDate(date);
				app.setApplyTime(date);
				/*
				 * 
				 */

				if (app.getNationalityType().equals("2")) {
					//System.out.println("["+className+".save()] passport No. = " + app.getBiodata().getPassport());
					app.setLogin(app.getBiodata().getPassport());
				}
				PersistenceManager.add(app);
			}
		} catch (Exception e) {
			e.printStackTrace();
			pm.rollback();
		} finally {
			if (pm != null) {
				pm.close();
			}
		}
		return "Record Inserted Successfully";

	}

	private String passwordSetup(String ic, String passport) {
		if (ic.equals("") || ic == null) {
			return passport;
		}

		return ic;
	}

	public String genRefNo() throws Exception {
		pm = new PersistenceManager();
		char[] cf = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
				'0', '0', '0', '0', '0', '0', '0', '0', '0', };
		RefNo refNo = (RefNo) pm.find(RefNo.class, REF_ID);
		if (refNo == null) {
			//System.out.println("["+className+".getRefNo()] refNo is null.");
			pm = new PersistenceManager();
			refNo = new RefNo();
			refNo.setCode(REF_ID);
			refNo.setNumber(0);
			refNo.setDigitLength(REF_LENGTH);
			refNo.setPrefix(REF_PREFIX);
			PersistenceManager.add(refNo);
		} else {
			//System.out.println("["+className+".getRefNo()] refNo available.");
		}
		
		boolean isExist = true;
		int number = refNo.getNumber();
		int length = refNo.getDigitLength();
		String prefix = refNo.getPrefix();
		String strRefNo = "";
		String query = "";
		// Check if reference no. already assigned to an applicant.
		// If refNo exist increment the running number until no match is found.
		while (isExist) {
			number = number + 1;
			strRefNo = prefix + new java.text.DecimalFormat(new String(cf, 0, length)).format(number);
			//System.out.println("["+className+".getRefNo()] refNo = "+strRefNo);
			
			query = "SELECT a FROM Applicant a WHERE a.referenceNo = '"+strRefNo+"'";
			List<Applicant> appList = pm.list(query);
			if (appList.isEmpty()) {
				//System.out.println("["+className+".getRefNo()] refNo is NOT Exist.");
				isExist = false;
			} else {
				//System.out.println("["+className+".getRefNo()] refNo is Exist.");
			}
		}
		
		pm = new PersistenceManager();
		refNo = (RefNo) pm.find(RefNo.class).whereId(REF_ID).forUpdate();
		try {
			refNo.setNumber(number);
			pm.update();
			return prefix
					+ new java.text.DecimalFormat(new String(cf, 0, length))
							.format(number);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			pm.rollback();
		}
		return null;
	}

	public boolean isExist(String id) throws Exception {
		pm = new PersistenceManager();

		Applicant applicant = (Applicant) pm.find(Applicant.class, id);
		if (applicant == null)
			return false;
		else
			return true;
	}

	public Applicant getApplicant(String id) throws Exception {
		pm = new PersistenceManager();
		Applicant applicant = (Applicant) pm.find(Applicant.class, id);
		if (applicant == null)
			return null;
		return applicant;
	}

	public Vector<String> getApplicationStatus() {
		Vector<String> v = new Vector<String>();
		v.addElement(NEW_STATUS);
		v.addElement(REJECT_STATUS);
		v.addElement(OFFERED_STATUS);
		v.addElement(REGISTERED_STATUS);
		v.addElement(INCOMPLETE_STATUS);
		v.addElement(TRASH_STATUS);
		v.addElement(ACCEPT_STATUS);
		v.addElement(CONDITIONAL_STATUS);
		
		return v;

	}

	public List<Applicant> getListByStatus(String status) throws Exception {
		pm = new PersistenceManager();
		
		List<Applicant> v = pm.list("SELECT a FROM Applicant a WHERE a.status='" + status
						+ "' ORDER BY a.intake,a.referenceNo");
			return v;

	}
	public List<Applicant> getListByStatus1(String status, String intake ) throws Exception {
		pm = new PersistenceManager();
		
		
		if (status.equals("0") && intake.equals("0)")){			
		List <Applicant> v1 = pm.list("SELECT b FROM Applicant b WHERE b.status='" + status
							+ "' ORDER BY b.intake,b.referenceNo");
			
		}else if(status.equals("0") && !intake.equals("0)")){
			List <Applicant> v1 = pm.list("SELECT b FROM Applicant b WHERE b.intake='" + intake
					+ "' ORDER BY b.intake,b.referenceNo");
	
		}
		
	
	return listOfApplicants;
	}
	
	
	public Applicant getByRefNo(String refId, String password) throws Exception {
		pm = new PersistenceManager();
		Applicant a;
		List<Applicant> l = pm.list("SELECT a FROM Applicant a WHERE a.referenceNo='" + refId
						+ "' AND a.password='" + password + "'");
		if (l.size() < 1) {
			return null;
		} else {
			a = l.get(0);
			return a;
		}
	}

	public Applicant login(String id) throws Exception {
		pm = new PersistenceManager();
		Applicant a;
		List<Applicant> l = pm.list("SELECT a FROM Applicant a WHERE a.login='"	+ id + "'");
		if (l.size() < 1) {
			return null;
		} else {
			a = l.get(0);
			return a;
		}
	}

	// update status and program offered to applicant
	public void updateStatus(String id, String status, String program,
			String registerDate, String registerTime, String registerHour,
			ModeOfStudy mode, RegVanue venue, Session ses) throws Exception {
		PersistenceManager pm1 = new PersistenceManager();

		Program prog = (Program) pm1.find(Program.class, program);
		pm1 = new PersistenceManager();
		Applicant applicant = (Applicant) pm1.find(Applicant.class).whereId(id).forUpdate();
		try {
			applicant.setStatus(status);
			if (OFFERED_STATUS.equals(status)) {
				applicant.setProgramOffered(prog);
				applicant.setRegistrationDate(registerDate);
				applicant.setRegistrationTime(registerTime);
				applicant.setRegistrationHour(registerHour);
				applicant.setModeOfStudy(mode);
				applicant.setRegVenue(venue);
				applicant.setIntake(ses);
			}
			pm1.update();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			pm1.rollback();
		}
	}

	public void updateStatus(String id, String status, String program,
			String registerDate, String registerTime, String registerHour,
			String mode, String venue, String ses, String learningCentre, String today)
			throws Exception {
		PersistenceManager pm1 = new PersistenceManager();
		Program prog = (Program) pm1.find(Program.class, program);
		Session session = (Session) pm1.find(Session.class, ses);
		ModeOfStudy modeStdy = (ModeOfStudy) pm1.find(ModeOfStudy.class, mode);
		RegVanue v = (RegVanue) pm1.find(RegVanue.class, venue);
		LearningCentre centre = (LearningCentre) pm1.find(LearningCentre.class,
				learningCentre);
		Applicant applicant = (Applicant) pm1.find(Applicant.class).whereId(id)
				.forUpdate();
		try {
			applicant.setStatus(status);
			//if (OFFERED_STATUS.equals(status)) {
				applicant.setProgramOffered(prog);
				applicant.setRegistrationDate(registerDate);
				applicant.setRegistrationTime(registerTime);
				applicant.setRegistrationHour(registerHour);
				applicant.setModeOfStudy(modeStdy);
				applicant.setRegVenue(v);
				applicant.setIntake(session);
				applicant.setLearningCentre(centre);
				applicant.setOfferedDate(today);
			//}
			pm1.update();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			pm1.rollback();
		}
	}

	public void cancelOffer(String id) throws Exception {
		pm = new PersistenceManager();
		Applicant applicant = (Applicant) pm.find(Applicant.class).whereId(id)
				.forUpdate();
		try {
			applicant.setStatus(NEW_STATUS);
			applicant.setProgramOffered(null);
			applicant.setRegistrationDate(null);
			applicant.setRegistrationTime(null);
			applicant.setRegistrationHour(null);
			applicant.setModeOfStudy(null);
			applicant.setRegVenue(null);
			pm.update();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			pm.rollback();
		}

	}

	public void updateStatus(String id, String status) throws Exception {
		pm = new PersistenceManager();
		Applicant applicant = (Applicant) pm.find(Applicant.class).whereId(id)
				.forUpdate();
		try {
			applicant.setStatus(status);
			pm.update();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			pm.rollback();
		}
	}
	//for accepted (10-06-09)
	public void updateAccept(String id, String accept) throws Exception {
		pm = new PersistenceManager();
		Applicant applicant = (Applicant) pm.find(Applicant.class).whereId(id)
				.forUpdate();
		try {
			applicant.setAccept(accept);
			pm.update();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			pm.rollback();
		}
	}


	public void deleteApplicant(String id) throws Exception {
		pm = new PersistenceManager();
		try {
			Applicant applicant = (Applicant) pm.find(Applicant.class, id);
			if (applicant != null) {
				pm.delete(applicant);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			pm.rollback();
		}
	}

	public Applicant getApplicantByIc(String icno) throws Exception {
		pm = new PersistenceManager();
		Applicant applicant = null;
		List<Applicant> l = pm.list("SELECT a FROM Applicant a WHERE a.biodata.icno='" + icno
						+ "'");
		if(l.size() > 0)
			applicant = l.get(0);
		return applicant;
	}
	
	public Applicant getApplicantByPassport(String passportNo)throws Exception{
		pm = new PersistenceManager();
		Applicant applicant = null;
		List<Applicant> l = pm.list("SELECT a FROM Applicant a WHERE a.biodata.passport='" +passportNo
						+ "'");
		if(l.size() > 0)
			applicant = l.get(0);
		return applicant;
	}
	


	/*
	 * Added by Shaiful Nizam, 2009-04-28.
	 * This method is used as a filter in ApplicationManagerModule. 
	 */
	public List<Applicant> getListByStatusAndAppliedIntake(String status, String intake) throws Exception {
		pm = new PersistenceManager();
		List<Applicant> v = pm
				.list("SELECT a FROM Applicant a WHERE a.status='" + status
						+ "' AND a.applyIntake.code = '" + intake 
						+ "' ORDER BY a.referenceNo");
		return v;

	}

	/*
	 * Added by Shaiful Nizam, 2009-04-28.
	 * This method is used as a filter in ApplicationManagerModule. 
	 */
	
	public List<Applicant> getListByStatusAndIntake(String status, String intake) throws Exception {
		pm = new PersistenceManager();
		List<Applicant> v = pm
				.list("SELECT a FROM Applicant a WHERE a.status='" + status
						+ "' AND a.intake.code = '" + intake 
						+ "' ORDER BY a.referenceNo");
		return v;

	}
	public boolean isSameDiscount(String applicant_id,String discount_id)throws Exception{
		pm = new PersistenceManager();
		List l = pm.list("SELECT a FROM ApplicantDiscount a WHERE a.discount.id='"+discount_id+"' AND a.applicant.id='"+applicant_id+"'");
		if(l.size() > 0){
			return true;
		}
		return false;
	}
	
	public void addDiscount(Applicant app,Discount discount,double tuitionFee)throws Exception{
//		if(!isSameDiscount(app.getId(), discount.getId())){
//			double amount = new BillingUtilities().calculateDiscount(discount.getAmount(),tuitionFee, discount.getType());
//			pm = new PersistenceManager();
//			ApplicantDiscount ad = new ApplicantDiscount();
//			ad.setAmount(amount);
//			ad.setApplicant(app);
//			ad.setDiscount(discount);
//			pm.add(ad);
//		}
	}
	
	public Vector<Subject> getSubjectSemester1(Applicant applicant) throws Exception{
		String intakeId = applicant.getIntake().getId();
		String progOfferedId = applicant.getProgramOffered().getId();
		String learningCentreId = applicant.getLearningCentre().getId();
		
		Session intake = applicant.getIntake();
		Program program = applicant.getProgramOffered();
		
		System.out.println("Intake = " + intake.getName());
		System.out.println("Program = " + program.getName());
		
		//ProgramStructure progStruct = new ProgramStructureBean().getBySession(intakeId, progOfferedId, learningCentreId);
		ProgramStructure progStruct = null;
		DbPersistence db = new DbPersistence();
		progStruct = (ProgramStructure) db.get("select p from ProgramStructure p where p.session.id = '" + intakeId + "' and p.program.id = '" + progOfferedId + "' and p.learningCenter.id = '" + learningCentreId + "'");
		List<SubjectPeriod> spList = progStruct.getSubjectPeriodList();
		if(spList.size() <= 0 )
			throw new Exception("Error on Program Structure ");
		
		Vector<Subject> subjects = new Vector<Subject>();
		
		if(progStruct.hasChild() == true){
			for(SubjectPeriod subjectPeriod : spList){
				if ( subjectPeriod.getPeriod() != null ) {
					if(subjectPeriod.getPeriod().getSequence() == 1 && subjectPeriod.getPeriod().getParent().getSequence() == 1){
						Set<Subject>  subjectList = subjectPeriod.getSubjects();
						subjects.addAll(subjectList);
						break;
					}
				}
				
			}
		}
		return subjects;
		
	}
	
	public static void main(String[] args) {
		Calendar cal = new GregorianCalendar();
		cal.get(Calendar.HOUR);
		cal.get(Calendar.MINUTE);
		
	}

}


	
