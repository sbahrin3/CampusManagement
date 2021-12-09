package educate.enrollment.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import onapp.entity.ApplicantOnline;
import edu.emory.mathcs.backport.java.util.Collections;
import educate.admission.entity.Address;
import educate.admission.entity.Applicant;
import educate.admission.entity.Biodata;
import educate.admission.entity.PermanentAddress;
import educate.sis.general.entity.Partner;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.StudentEnrollmentIntake;
import educate.sis.struct.entity.StudyMode;

@Entity
@Table(name="enrl_student")
public class Student {
	@Id 
	private String id;
	@Column(length=50)
	private String matricNo;
	@Temporal(TemporalType.DATE)
	private Date registerDate;
	@Embedded
	private Biodata biodata;
	@Embedded
	private Address address;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Applicant applicant;

	@ManyToOne(fetch=FetchType.LAZY)
	private ApplicantOnline applicant2;

	@ManyToOne
	private Session intake;
	
	@ManyToOne
	private StudentEnrollmentIntake enrollmentIntake;
	
	@ManyToOne
	private Program program;
	@ManyToOne
	private Period entryLevel;

	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="student")
	private Set<StudentStatus> status;
	private String statuses;
	private String photoFileName;
	private String avatarFileName;
	@Temporal(TemporalType.TIME)
	private Date registerTime;
	private String fakeStudent;
	@Column(length=2)
	private String nationalityType;
	@ManyToOne
	private LearningCentre learningCenter;
	@OneToOne
	private Partner partner;
	@ManyToOne
	private StudyMode studyMode;
	@Embedded
	private PermanentAddress permanentAddress;
	private int flag;

	@Column(name="studentType", length=1)
	private String studentType;

	@Column(name="citizenship_status", length=2)
	private String citizenshipStatus;

	private String studyType; //general or specialization
	private int partTime;
	private int international;

	@Lob @Basic(fetch = FetchType.LAZY)
	private byte[] photo;
	//@Column(length=50)
	//private String photoId;
	@Lob
	private byte[] avatar;
	
//	@Lob
//	private byte[] matricCardImageFront;
//	@Lob
//	private byte[] matricCardImageBack;

	@Column(length=50)
	private String recordFlag;
	@Column(length=50)
	private String statusTag;


	private String matricImageFrontName;
	private String matricImageBackName;
	private String matricPdfName;  
	
	private String institutionSPM;
	private String institutionSTPM;
	private String institutionTypeSPM;
	private String institutionTypeSTPM;	
	
	@Column(length=100)
	private String previousInstitutionName;
	@Column(length=50)
	private String previousQualificationType;
	@Column(length=100)
	private String previousQualificationName;
	
	
	private int barredExamTranscript;
	
	public Student(){
		setId(lebah.db.UniqueID.getUID());
		biodata = new Biodata();
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Object d) {
		if ( d instanceof Date ) registerDate = (Date) d;
		else if ( d instanceof String) {
			setRegisterDate((String) d);
		}
	}

	public void setRegisterDate(Date d) {
		this.registerDate = d;
	}

	public void setRegisterDate(String date) {
		if (date == null || "".equals(date)) return;
		String separator = "-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setRegisterDate(new GregorianCalendar(year, month, day).getTime());
	}

	public Biodata getBiodata() {
		if ( biodata == null ) biodata = new Biodata();
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

	public String getMatricNo() {
		return matricNo;
	}

	public void setMatricNo(String matricNo) {
		this.matricNo = matricNo;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}
	


	public Period getEntryLevel() {
		return entryLevel;
	}


	public void setEntryLevel(Period entryLevel) {
		this.entryLevel = entryLevel;
	}


	public Session getIntake() {
		return intake;
	}

	public void setIntake(Session intake) {
		this.intake = intake;
	}

	public Set<StudentStatus> getStatus() {
		if ( status == null ) { 
			status = new HashSet<StudentStatus>();
		}
		return status;
	}

	public void setStatus(Set<StudentStatus> status) {
		for ( Iterator<StudentStatus> it = status.iterator(); it.hasNext();  ) {
			it.next().setStudent(this);
		}
		this.status = status;
	}

	public List<StudentStatus> listStatus() {
		List<StudentStatus> list = new ArrayList<StudentStatus>();
		list.addAll(status);
		Collections.sort(list, new SessionComparator());
		return list;
	}
	
	class SessionComparator extends educate.util.MyComparator2<StudentStatus> {
		public int compare(StudentStatus s1, StudentStatus s2) {
			return s1.getSession().getStartDate().compareTo(s2.getSession().getStartDate());
		}
	}		


	/*
	public StudentStatus getStatus(String sessionCode) {
		StudentStatus s = null;
		for ( Iterator<StudentStatus> it = status.iterator(); it.hasNext();  ) {

		}		
		return s;
	}
	 */



	public String getStatuses() {
		return statuses;
	}

	public void setStatuses(String statuses) {
		this.statuses = statuses;
	}

	public StudentStatus getStatus(String sessionId) {
		StudentStatus s = null;
		for ( StudentStatus st : status  ) {
			if ( st.getSession().getId().equals(sessionId)) {
				s = st;
				break;
			}
		}		
		return s;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public Applicant getApplicant() {
		return applicant;
	}

	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}

	public String getPhotoFileName() {
		return photoFileName;
	}

	public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registrationTime) {
		this.registerTime = registrationTime;
	}

	public String getFakeStudent() {
		return fakeStudent;
	}

	public void setFakeStudent(String fakeStudent) {
		this.fakeStudent = fakeStudent;
	}

	@Override
	public boolean equals(Object o) {
		Student result = (Student)o;
		if(result.id.equals(id))
			return true;
		else
			return false;
	}
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public LearningCentre getLearningCenter() {
		return learningCenter;
	}

	public void setLearningCenter(LearningCentre learningCenter) {
		this.learningCenter = learningCenter;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public String getNationalityType() {
		return nationalityType;
	}

	public void setNationalityType(String nationalityType) {
		this.nationalityType = nationalityType;
	}



	public StudyMode getStudyMode() {
		return studyMode;
	}


	public void setStudyMode(StudyMode studyMode) {
		this.studyMode = studyMode;
	}


	public PermanentAddress getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(PermanentAddress permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	public String getCitizenshipStatus() {
		if (citizenshipStatus == null) {
			citizenshipStatus = "";
		}
		return citizenshipStatus;
	}

	public void setCitizenshipStatus(String citizenshipStatus) {
		if (citizenshipStatus == null) {
			this.citizenshipStatus = "";
		} else {
			this.citizenshipStatus = citizenshipStatus;
		}
	}

	public void copyBiodata(Applicant a) {
		biodata = a.getBiodata();
	}

	public String getStudyType() {
		return studyType;
	}

	public void setStudyType(String studyType) {
		this.studyType = studyType;
	}



	public int getFlag() {
		return flag;
	}



	public void setFlag(int flag) {
		this.flag = flag;
	}



	public String getStudentType() {
		return studentType;
	}

	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}

	public ApplicantOnline getApplicant2() {
		return applicant2;
	}

	public void setApplicant2(ApplicantOnline applicant2) {
		this.applicant2 = applicant2;
	}

	public String getAvatarFileName() {
		return avatarFileName;
	}

	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}


	public String getRecordFlag() {
		return recordFlag;
	}

	public void setRecordFlag(String recordFlag) {
		this.recordFlag = recordFlag;
	}


	public String getMatricImageFrontName() {
		return matricImageFrontName;
	}


	public void setMatricImageFrontName(String matricImageFrontName) {
		this.matricImageFrontName = matricImageFrontName;
	}


	public String getMatricImageBackName() {
		return matricImageBackName;
	}


	public void setMatricImageBackName(String matricImageBackName) {
		this.matricImageBackName = matricImageBackName;
	}


	public String getMatricPdfName() {
		return matricPdfName;
	}


	public void setMatricPdfName(String matricPdfName) {
		this.matricPdfName = matricPdfName;
	}


	public String getInstitutionSPM() {
		return institutionSPM;
	}


	public void setInstitutionSPM(String institutionSPM) {
		this.institutionSPM = institutionSPM;
	}


	public String getInstitutionSTPM() {
		return institutionSTPM;
	}


	public void setInstitutionSTPM(String institutionSTPM) {
		this.institutionSTPM = institutionSTPM;
	}


	public String getInstitutionTypeSPM() {
		return institutionTypeSPM;
	}


	public void setInstitutionTypeSPM(String institutionTypeSPM) {
		this.institutionTypeSPM = institutionTypeSPM;
	}


	public String getInstitutionTypeSTPM() {
		return institutionTypeSTPM;
	}


	public void setInstitutionTypeSTPM(String institutionTypeSTPM) {
		this.institutionTypeSTPM = institutionTypeSTPM;
	}


	public String getPreviousQualificationType() {
		return previousQualificationType;
	}


	public void setPreviousQualificationType(String previousQualificationType) {
		this.previousQualificationType = previousQualificationType;
	}


	public String getPreviousQualificationName() {
		return previousQualificationName;
	}


	public void setPreviousQualificationName(String previousQualificationName) {
		this.previousQualificationName = previousQualificationName;
	}


	public String getPreviousInstitutionName() {
		return previousInstitutionName;
	}


	public void setPreviousInstitutionName(String previousInstitutionName) {
		this.previousInstitutionName = previousInstitutionName;
	}


	public boolean getBarredExamTranscript() {
		return barredExamTranscript == 1;
	}


	public void setBarredExamTranscript(boolean barredExamTranscript) {
		this.barredExamTranscript = barredExamTranscript ? 1 : 0;
	}


	public boolean getPartTime() {
		return partTime == 1;
	}


	public void setPartTime(boolean partTime) {
		this.partTime = partTime ? 1 : 0;
	}


	public boolean getInternational() {
		return international == 1;
	}


	public void setInternational(boolean international) {
		this.international = international ? 1 : 0;
	}


	public String getStatusTag() {
		return statusTag;
	}


	public void setStatusTag(String statusTag) {
		this.statusTag = statusTag;
	}


	public StudentEnrollmentIntake getEnrollmentIntake() {
		return enrollmentIntake;
	}


	public void setEnrollmentIntake(StudentEnrollmentIntake enrollmentIntake) {
		this.enrollmentIntake = enrollmentIntake;
	}
	
	


}
