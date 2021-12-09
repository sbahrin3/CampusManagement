package educate.admission.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import onapp.entity.ApplicantOnline;
import educate.sis.general.entity.Disability;
import educate.sis.general.entity.EducationLevel;
import educate.sis.general.entity.GradsLevel;
import educate.sis.general.entity.ModeOfStudy;
import educate.sis.general.entity.PaymentMethod;
import educate.sis.general.entity.RegVanue;
import educate.sis.general.entity.ResearchArea;
import educate.sis.general.entity.Sponsor;
import educate.sis.general.entity.Survey;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;

@Entity
@Table(name="app_applicant")
public class Applicant {
	
	@Id
	private String id;
	@OneToOne(fetch= FetchType.LAZY) @JoinColumn(name="applicant_id2")
	private ApplicantOnline applicantOnline;
	@Column(length=20)
	private String referenceNo;
	@Column(length=50)
	private String password;
	@Column(length=50)
	private String status;
	
	/*
	 * Added by SAM, 17/12/2009
	 * To capture when this applicant submit this application
	 */
	@Temporal(TemporalType.DATE)
	private Date applyDate;
	@Temporal(TemporalType.TIME)
	private Date applyTime;
	
	@Temporal(TemporalType.DATE)
	private Date registerDate;
	@Embedded
	private Biodata biodata;
	@Embedded
	private Address address;
	@Embedded
	private EnglishProfiency englishProfiency;
	@Embedded
	private PaymentInfo paymentInfo;
	@Column(precision=4)
	private int spmYear;
	private int spmCredits;
	@Column(precision=4)
	private int stpmYear;
	@Column(length=2)
	private String nationalityType; 
	@ManyToOne
	private Program choice1;
	@ManyToOne
	private Program choice2;
	@ManyToOne
	private Program choice3;
	@ManyToOne
	private Program  programOffered;
	@Temporal(TemporalType.DATE)
	private Date offeredDate;
	
	@ManyToOne
	private GradsLevel gradslevel;
		
	@OneToMany(cascade=CascadeType.ALL)
	private Set<SpmResult> spmResults;
	@OneToMany(cascade=CascadeType.ALL)
	private Set<StpmResult> stpmResults;
	@OneToMany(cascade=CascadeType.ALL)
	private Set<SecondaryEducation> secondaries;
	@OneToMany(cascade=CascadeType.ALL)
	private Set<TertiaryEducation> tertiaries;
	@OneToMany(cascade=CascadeType.ALL)
	private Set<Employment> employments;
	@OneToMany(cascade=CascadeType.ALL)
	private Set<Certificate> certificates;
	@OneToMany(cascade=CascadeType.ALL)
	private Set<AlevelResult> alevelResults;
	@OneToMany(cascade=CascadeType.ALL)
	private Set<OlevelResult> olevelResults;
	@ManyToOne
	private Disability disability;
	@ManyToOne
	private Sponsor sponsor;
	@ManyToOne
	private Survey survey;
	
	private String bmResult;
	private String conditional;
	
	@Embedded
	private CurrentEmployment currentEmployment;
	@ManyToOne
	private LearningCentre learningCentre;
	@ManyToOne
	private PaymentMethod paymentMethod;
	@Temporal(TemporalType.DATE)
	private Date registrationDate;
	private String registrationTime;
	private String registrationHour;
	@ManyToOne
	private ModeOfStudy modeOfStudy;
	@OneToMany
	private Set<Subject> registerSubjects;
	private double registrationFeeAmount;
	@ManyToOne
	private RegVanue regVenue;
	private String login;
	@ManyToOne
	private EducationLevel highestEducation;
	
	@ManyToOne
	private ResearchArea researchArea;
	private String researchTitle;
	private String researchLocation;
	private String supervisorName;
	private String supervisorAddress;
	private String supervisorContactNo;
	private String transactionType;
	private String accept;
	//batch printing
	private String print;
	
	// utk IncompleteStatus
	private String bioInc;
	private String eduBackInc;
	private String empBackInc;
	@ManyToOne
	private Session applyIntake;
	
	@ManyToOne
	private Session intake;
	
	
	@Embedded
	private PermanentAddress permanentAddress;
	

	//MUET
	private int muetYear;
	private int muetBand;
	
	//EXTRA
	private String guardianName;
	private String guardianICNo;
	private String guardianTelephoneNo;
	private String guardianOccupation;
	private String guardianRelationship;
	
	//PHOTO INFORMATION
	private String photoFileName;
	
	//DOCUMENT SUBMISSION
	private int submitPhotos;
	private int submitIC;
	private int submitBirthCert;
	private int submitSPM;
	private int submitSTPM;
	private int submitMUET;
	private int submitDiploma;
	private int submitSijilBerhenti;
	private int submitMoney;
	
	
	
	
	public Applicant() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	

	public ApplicantOnline getApplicantOnline() {
		return applicantOnline;
	}

	public void setApplicantOnline(ApplicantOnline applicant2) {
		this.applicantOnline = applicant2;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public Biodata getBiodata() {
		if ( biodata == null ) biodata = new Biodata();
		return biodata;
	}

	public void setBiodata(Biodata biodata) {
		this.biodata = biodata;
	}

	public Address getAddress() {
		if ( address == null ) address = new Address();
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public EnglishProfiency getEnglishProfiency() {
		return englishProfiency;
	}

	public void setEnglishProfiency(EnglishProfiency englishProfiency) {
		this.englishProfiency = englishProfiency;
	}

	public int getSpmYear() {
		return spmYear;
	}

	public void setSpmYear(int spmYear) {
		this.spmYear = spmYear;
	}

	public int getStpmYear() {
		return stpmYear;
	}

	public void setStpmYear(int stpmYear) {
		this.stpmYear = stpmYear;
	}

	public Set<SpmResult> getSpmResults2() {
		if ( spmResults == null ) spmResults = new HashSet<SpmResult>();
		return spmResults;
	}
	
	public List<SpmResult> getSpmResults() {
		if ( spmResults == null ) spmResults = new HashSet<SpmResult>();
		List<SpmResult> list = new ArrayList<SpmResult>();
		list.addAll(spmResults);
		Collections.sort(list);
		return list;
	}

	public void setSpmResults(Set<SpmResult> spmResults) {
		this.spmResults = spmResults;
	}

	public Set<StpmResult> getStpmResults2() {
		if ( stpmResults == null ) stpmResults = new HashSet<StpmResult>();
		return stpmResults;
	}
	
	public List<StpmResult> getStpmResults() {
		if ( stpmResults == null ) stpmResults = new HashSet<StpmResult>();
		List<StpmResult> list = new ArrayList<StpmResult>();
		list.addAll(stpmResults);
		Collections.sort(list);
		return list;
	}

	public void setStpmResults(Set<StpmResult> stpmResults) {
		this.stpmResults = stpmResults;
	}

	public Set<SecondaryEducation> getSecondaries() {
		if ( secondaries == null ) secondaries = new HashSet<SecondaryEducation>();
		return secondaries;
	}

	public void setSecondaries(Set<SecondaryEducation> secondaries) {
		this.secondaries = secondaries;
	}

	public Set<TertiaryEducation> getTertiaries() {
		if ( tertiaries == null ) tertiaries = new HashSet<TertiaryEducation>();
		return tertiaries;
	}

	public void setTertiaries(Set<TertiaryEducation> tertiaries) {
		this.tertiaries = tertiaries;
	}

	public Set<Employment> getEmployments() {
		if ( employments == null ) employments = new HashSet<Employment>();
		return employments;
	}

	public void setEmployments(Set<Employment> employments) {
		this.employments = employments;
	}

	public Set<Certificate> getCertificates() {
		if ( certificates == null ) certificates = new HashSet<Certificate>();
		return certificates;
	}

	public void setCertificates(Set<Certificate> certificates) {
		this.certificates = certificates;
	}

	public Disability getDisability() {
		return disability;
	}

	public void setDisability(Disability disability) {
		this.disability = disability;
	}

	public Sponsor getSponsor() {
		return sponsor;
	}

	public void setSponsor(Sponsor sponsor) {
		this.sponsor = sponsor;
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setNationalityType(String nationalityType) {
		this.nationalityType = nationalityType;
	}

	public String getNationalityType() {
		return nationalityType;
	}

	public void setChoice1(Program choice1) {
		this.choice1 = choice1;
	}

	public Program getChoice1() {
		return choice1;
	}

	public void setChoice2(Program choice2) {
		this.choice2 = choice2;
	}

	public Program getChoice2() {
		return choice2;
	}

	public void setChoice3(Program choice3) {
		this.choice3 = choice3;
	}

	public Program getChoice3() {
		return choice3;
	}

	public void setBmResult(String bmResult) {
		this.bmResult = bmResult;
	}

	public String getBmResult() {
		return bmResult;
	}

	public void setCurrentEmployment(CurrentEmployment currentEmployment) {
		this.currentEmployment = currentEmployment;
	}

	public CurrentEmployment getCurrentEmployment() {
		return currentEmployment;
	}

	public void setLearningCentre(LearningCentre learningCentre) {
		this.learningCentre = learningCentre;
	}

	public LearningCentre getLearningCentre() {
		return learningCentre;
	}

	public void setProgramOffered(Program programOffered) {
		this.programOffered = programOffered;
	}

	public Program getProgramOffered() {
		return programOffered;
	}

	public void setAlevelResults(Set<AlevelResult> alevelResults) {
		this.alevelResults = alevelResults;
	}

	public Set<AlevelResult> getAlevelResults() {
		return alevelResults;
	}

	public void setOlevelResults(Set<OlevelResult> olevelResults) {
		this.olevelResults = olevelResults;
	}

	public Set<OlevelResult> getOlevelResults() {
		return olevelResults;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public PaymentInfo getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(PaymentInfo paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String date) {
		if (date == null || "".equals(date)) return;
		String separator = "-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setRegistrationDate(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setRegistrationDate(Object d) {
		if ( d instanceof Date ) registrationDate = (Date) d;
		else if ( d instanceof String) {
			setRegistrationDate((String) d);
		}
	}
	
	public Date getOfferedDate() {
		return offeredDate;
	}
	
	public void setOfferedDate(String date) {
		if (date == null || "".equals(date)) return;
		String separator = "-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setOfferedDate(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setOfferedDate(Object d) {
		if ( d instanceof Date ) offeredDate = (Date) d;
		else if ( d instanceof String) {
			setOfferedDate((String) d);
		}
	}
	
	public String getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(String registrationTime) {
		this.registrationTime = registrationTime;
	}

	public String getRegistrationHour() {
		return registrationHour;
	}

	public void setRegistrationHour(String registrationHour) {
		this.registrationHour = registrationHour;
	}

	public ModeOfStudy getModeOfStudy() {
		return modeOfStudy;
	}

	public void setModeOfStudy(ModeOfStudy modeOfStudy) {
		this.modeOfStudy = modeOfStudy;
	}

	public Set<Subject> getRegisterSubjects() {
		return registerSubjects;
	}

	public void setRegisterSubjects(Set<Subject> registerSubjects) {
		this.registerSubjects = registerSubjects;
	}

	public void setRegistrationFeeAmount(double registrationFeeAmount) {
		this.registrationFeeAmount = registrationFeeAmount;
	}

	public double getRegistrationFeeAmount() {
		return registrationFeeAmount;
	}

	public void setRegVenue(RegVanue regVenue) {
		this.regVenue = regVenue;
	}

	public RegVanue getRegVenue() {
		return regVenue;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setGradslevel(GradsLevel gradslevel) {
		this.gradslevel = gradslevel;
	}

	public GradsLevel getGradslevel() {
		return gradslevel;
	}

	public void setHighestEducation(EducationLevel highestEducation) {
		this.highestEducation = highestEducation;
	}

	public EducationLevel getHighestEducation() {
		return highestEducation;
	}

	public String getResearchTitle() {
		return researchTitle;
	}

	public void setResearchTitle(String researchTitle) {
		this.researchTitle = researchTitle;
	}
	
	public String getSupervisorName() {
		return supervisorName;
	}

	public void setSupervisorName(String supervisorName) {
		this.supervisorName = supervisorName;
	}

	public String getSupervisorAddress() {
		return supervisorAddress;
	}

	public void setSupervisorAddress(String supervisorAddress) {
		this.supervisorAddress = supervisorAddress;
	}

	public String getSupervisorContactNo() {
		return supervisorContactNo;
	}

	public void setSupervisorContactNo(String supervisorContactNo) {
		this.supervisorContactNo = supervisorContactNo;
	}

	public void setResearchArea(ResearchArea researchArea) {
		this.researchArea = researchArea;
	}

	public ResearchArea getResearchArea() {
		return researchArea;
	}

	public String getResearchLocation() {
		return researchLocation;
	}

	public void setResearchLocation(String researchLocation) {
		this.researchLocation = researchLocation;
	}

	public void setIntake(Session intake) {
		this.intake = intake;
	}

	public Session getIntake() {
		return intake;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getConditional() {
		return conditional;
	}

	public void setConditional(String conditional) {
		this.conditional = conditional;
	}

	public String getAccept() {
		return accept;
	}

	public void setAccept(String accept) {
		this.accept = accept;
	}

	public String getBioInc() {
		return bioInc;
	}

	public void setBioInc(String bioInc) {
		this.bioInc = bioInc;
	}

	public String getEduBackInc() {
		return eduBackInc;
	}

	public void setEduBackInc(String eduBackInc) {
		this.eduBackInc = eduBackInc;
	}

	public String getEmpBackInc() {
		return empBackInc;
	}

	public void setEmpBackInc(String empBackInc) {
		this.empBackInc = empBackInc;
	}

	public Session getApplyIntake() {
		return applyIntake;
	}

	public void setApplyIntake(Session applyIntake) {
		this.applyIntake = applyIntake;
	}

//	public Set<ApplicantDiscount> getDiscounts() {
//		return discounts;
//	}
//
//	public void setDiscounts(Collection discounts) {
//		for(ApplicantDiscount d : this.discounts){
//			if(!discounts.contains(d)){
//				removeDiscount(d);
//			}
//		}
//		for(ApplicantDiscount d : (Collection<ApplicantDiscount>)discounts){
//			addDiscount(d);
//		}
//	}

//	public void addDiscount(ApplicantDiscount d) {
//		if(!this.discounts.contains(d)){
//			this.discounts.add(d);
//			d.setApplicant(this);
//		}		
//	}
//
//	public void removeDiscount(ApplicantDiscount d) {
//		if(this.discounts.contains(d)){
//			this.discounts.remove(d);
//			d.removeApplicant();
//		}
//		
//	}
//	public double getTotalDiscount(){
//		double total = 0;
//		if(discounts != null){
//			for(Iterator<ApplicantDiscount> it = discounts.iterator();it.hasNext();){
//				ApplicantDiscount ad = it.next();
//				total += ad.getAmount();
//			}
//		}
//		return total;
//	}

	
	//status printing offer letter
	public String getPrint() {
		return print;
	}

	public void setPrint(String print) {
		this.print = print;
	}

	public PermanentAddress getPermanentAddress() {
		if ( permanentAddress == null ) permanentAddress = new PermanentAddress();
		return permanentAddress;
	}

	public void setPermanentAddress(PermanentAddress permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

//	public Session getDeferredIntake() {
//		return deferredIntake;
//	}
//
//	public void setDeferredIntake(Session deferredIntake) {
//		this.deferredIntake = deferredIntake;
//	}


	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	
	
	


	public String getGuardianICNo() {
		return guardianICNo;
	}

	public void setGuardianICNo(String guardianICNo) {
		this.guardianICNo = guardianICNo;
	}

	public String getGuardianName() {
		return guardianName;
	}

	public void setGuardianName(String guardianName) {
		this.guardianName = guardianName;
	}

	public String getGuardianOccupation() {
		return guardianOccupation;
	}

	public void setGuardianOccupation(String guardianOccupation) {
		this.guardianOccupation = guardianOccupation;
	}

	public String getGuardianRelationship() {
		return guardianRelationship;
	}

	public void setGuardianRelationship(String guardianRelationship) {
		this.guardianRelationship = guardianRelationship;
	}

	public String getGuardianTelephoneNo() {
		return guardianTelephoneNo;
	}

	public void setGuardianTelephoneNo(String guardianTelephoneNo) {
		this.guardianTelephoneNo = guardianTelephoneNo;
	}

	public int getMuetBand() {
		return muetBand;
	}

	public void setMuetBand(int muetBand) {
		this.muetBand = muetBand;
	}

	public int getMuetYear() {
		return muetYear;
	}

	public void setMuetYear(int muetYear) {
		this.muetYear = muetYear;
	}

	public String getPhotoFileName() {
		return photoFileName;
	}

	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}

	public boolean getSubmitPhotos() {
		return submitPhotos == 1;
	}

	public void setSubmitPhotos(boolean b) {
		this.submitPhotos = b ? 1 : 0;
	}

	public boolean getSubmitIC() {
		return submitIC == 1;
	}

	public void setSubmitIC(boolean b) {
		this.submitIC = b ? 1 : 0;
	}

	public boolean getSubmitBirthCert() {
		return submitBirthCert == 1;
	}

	public void setSubmitBirthCert(boolean b) {
		this.submitBirthCert = b ? 1 : 0;
	}

	public boolean getSubmitSPM() {
		return submitSPM == 1;
	}

	public void setSubmitSPM(boolean b) {
		this.submitSPM = b ? 1 : 0;
	}

	public boolean getSubmitSTPM() {
		return submitSTPM == 1;
	}

	public void setSubmitSTPM(boolean b) {
		this.submitSTPM = b ? 1 : 0;
	}

	public boolean getSubmitMUET() {
		return submitMUET == 1;
	}

	public void setSubmitMUET(boolean b) {
		this.submitMUET = b ? 1 : 0;
	}

	public boolean getSubmitDiploma() {
		return submitDiploma == 1;
	}

	public void setSubmitDiploma(boolean b) {
		this.submitDiploma = b ? 1 : 0;
	}

	public boolean getSubmitSijilBerhenti() {
		return submitSijilBerhenti == 1;
	}

	public void setSubmitSijilBerhenti(boolean b) {
		this.submitSijilBerhenti = b ? 1 : 0;
	}

	public boolean getSubmitMoney() {
		return submitMoney == 1;
	}

	public void setSubmitMoney(boolean b) {
		this.submitMoney = b ? 1 : 0;
	}

	public int getSpmCredits() {
		return spmCredits;
	}

	public void setSpmCredits(int spmCredits) {
		this.spmCredits = spmCredits;
	}

	

}
