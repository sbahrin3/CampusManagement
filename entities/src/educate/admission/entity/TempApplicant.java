package educate.admission.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
@Table(name="applicant_migration")
public class TempApplicant  {

	
	@Id
	private String id;
	@Column(length=20)
	private String referenceNo;
	@Column(length=50)
	private String password;
	@Column(length=50)
	private String status;
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

	public TempApplicant() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
		return biodata;
	}

	public void setBiodata(Biodata biodata) {
		this.biodata = biodata;
	}

	public Address getAddress() {
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

	public PaymentInfo getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(PaymentInfo paymentInfo) {
		this.paymentInfo = paymentInfo;
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

	public String getNationalityType() {
		return nationalityType;
	}

	public void setNationalityType(String nationalityType) {
		this.nationalityType = nationalityType;
	}

	public Program getChoice1() {
		return choice1;
	}

	public void setChoice1(Program choice1) {
		this.choice1 = choice1;
	}

	public Program getChoice2() {
		return choice2;
	}

	public void setChoice2(Program choice2) {
		this.choice2 = choice2;
	}

	public Program getChoice3() {
		return choice3;
	}

	public void setChoice3(Program choice3) {
		this.choice3 = choice3;
	}

	public Program getProgramOffered() {
		return programOffered;
	}

	public void setProgramOffered(Program programOffered) {
		this.programOffered = programOffered;
	}

	public Date getOfferedDate() {
		return offeredDate;
	}

	public void setOfferedDate(Date offeredDate) {
		this.offeredDate = offeredDate;
	}

	public GradsLevel getGradslevel() {
		return gradslevel;
	}

	public void setGradslevel(GradsLevel gradslevel) {
		this.gradslevel = gradslevel;
	}

	public Set<SpmResult> getSpmResults() {
		return spmResults;
	}

	public void setSpmResults(Set<SpmResult> spmResults) {
		this.spmResults = spmResults;
	}

	public Set<StpmResult> getStpmResults() {
		return stpmResults;
	}

	public void setStpmResults(Set<StpmResult> stpmResults) {
		this.stpmResults = stpmResults;
	}

	public Set<SecondaryEducation> getSecondaries() {
		return secondaries;
	}

	public void setSecondaries(Set<SecondaryEducation> secondaries) {
		this.secondaries = secondaries;
	}

	public Set<TertiaryEducation> getTertiaries() {
		return tertiaries;
	}

	public void setTertiaries(Set<TertiaryEducation> tertiaries) {
		this.tertiaries = tertiaries;
	}

	public Set<Employment> getEmployments() {
		return employments;
	}

	public void setEmployments(Set<Employment> employments) {
		this.employments = employments;
	}

	public Set<Certificate> getCertificates() {
		return certificates;
	}

	public void setCertificates(Set<Certificate> certificates) {
		this.certificates = certificates;
	}

	public Set<AlevelResult> getAlevelResults() {
		return alevelResults;
	}

	public void setAlevelResults(Set<AlevelResult> alevelResults) {
		this.alevelResults = alevelResults;
	}

	public Set<OlevelResult> getOlevelResults() {
		return olevelResults;
	}

	public void setOlevelResults(Set<OlevelResult> olevelResults) {
		this.olevelResults = olevelResults;
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

	public String getBmResult() {
		return bmResult;
	}

	public void setBmResult(String bmResult) {
		this.bmResult = bmResult;
	}

	public String getConditional() {
		return conditional;
	}

	public void setConditional(String conditional) {
		this.conditional = conditional;
	}

	public CurrentEmployment getCurrentEmployment() {
		return currentEmployment;
	}

	public void setCurrentEmployment(CurrentEmployment currentEmployment) {
		this.currentEmployment = currentEmployment;
	}

	public LearningCentre getLearningCentre() {
		return learningCentre;
	}

	public void setLearningCentre(LearningCentre learningCentre) {
		this.learningCentre = learningCentre;
	}


	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
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

	public double getRegistrationFeeAmount() {
		return registrationFeeAmount;
	}

	public void setRegistrationFeeAmount(double registrationFeeAmount) {
		this.registrationFeeAmount = registrationFeeAmount;
	}

	public RegVanue getRegVenue() {
		return regVenue;
	}

	public void setRegVenue(RegVanue regVenue) {
		this.regVenue = regVenue;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public EducationLevel getHighestEducation() {
		return highestEducation;
	}

	public void setHighestEducation(EducationLevel highestEducation) {
		this.highestEducation = highestEducation;
	}

	public ResearchArea getResearchArea() {
		return researchArea;
	}

	public void setResearchArea(ResearchArea researchArea) {
		this.researchArea = researchArea;
	}

	public String getResearchTitle() {
		return researchTitle;
	}

	public void setResearchTitle(String researchTitle) {
		this.researchTitle = researchTitle;
	}

	public String getResearchLocation() {
		return researchLocation;
	}

	public void setResearchLocation(String researchLocation) {
		this.researchLocation = researchLocation;
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

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
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

	public Session getIntake() {
		return intake;
	}

	public void setIntake(Session intake) {
		this.intake = intake;
	}


	public PermanentAddress getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(PermanentAddress permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	

}
