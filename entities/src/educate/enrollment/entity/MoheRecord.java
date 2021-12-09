package educate.enrollment.entity;

import java.util.Date;

import javax.persistence.*;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0 
 * @date 27.8.2011
 */
@Entity
@Table(name="mohe_record")
public class MoheRecord {
	
	@Id
	private String id;
	private int reportType;
	private String generatorId;
	private String name;
	private String icno;
	private String passport;
	@Temporal(TemporalType.DATE)
	private Date dob;
	private int birthYear;
	private String postcode;
	private String genderName;
	private String genderCode;
	private String religionName;
	private String religionCode;
	private String raceName;
	private String raceCode;
	private String marriageStatus;
	private String stateName;
	private String stateCode;
	
	private String nationalityCode;
	private String nationalityName;
	private String nationalityStatus;
	
	private String currentSemester;
	private String totalSemester;
	
	private int intakeMonth;
	private int intakeYear;
	private int expectedStudyEndMonth;
	private int expectedStudyEndYear;
	
	private String codeCourse;
	private String cgpa;
	
	private String entryQualification;
	private String disabilityStatus;
	private String disabilityType;
	
	private String sponsorType;
	private String statusStudent;
	@Temporal(TemporalType.DATE)
	private Date dateRegister;

	
	
	public MoheRecord() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public int getBirthYear() {
		return birthYear;
	}
	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}

	public String getIcno() {
		return icno;
	}
	public void setIcno(String icno) {
		this.icno = icno;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassport() {
		return passport;
	}
	public void setPassport(String passport) {
		this.passport = passport;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getStateName() {
		return stateName;
	}
	public void setStateName(String state) {
		this.stateName = state;
	}
	public String getMarriageStatus() {
		return marriageStatus;
	}
	public void setMarriageStatus(String statusMarriage) {
		this.marriageStatus = statusMarriage;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getCgpa() {
		return cgpa;
	}

	public void setCgpa(String cgpa) {
		this.cgpa = cgpa;
	}

	public String getCodeCourse() {
		return codeCourse;
	}

	public void setCodeCourse(String codeCourse) {
		this.codeCourse = codeCourse;
	}

	public String getCurrentSemester() {
		return currentSemester;
	}

	public void setCurrentSemester(String currentSemester) {
		this.currentSemester = currentSemester;
	}

	public Date getDateRegister() {
		return dateRegister;
	}

	public void setDateRegister(Date dateRegister) {
		this.dateRegister = dateRegister;
	}

	public String getEntryQualification() {
		return entryQualification;
	}

	public void setEntryQualification(String entryQualification) {
		this.entryQualification = entryQualification;
	}

	public int getExpectedStudyEndMonth() {
		return expectedStudyEndMonth;
	}

	public void setExpectedStudyEndMonth(int expectedStudyEndMonth) {
		this.expectedStudyEndMonth = expectedStudyEndMonth;
	}

	public int getExpectedStudyEndYear() {
		return expectedStudyEndYear;
	}

	public void setExpectedStudyEndYear(int expectedStudyEndYear) {
		this.expectedStudyEndYear = expectedStudyEndYear;
	}

	public int getIntakeMonth() {
		return intakeMonth;
	}

	public void setIntakeMonth(int intakeMonth) {
		this.intakeMonth = intakeMonth;
	}

	public int getIntakeYear() {
		return intakeYear;
	}

	public void setIntakeYear(int intakeYear) {
		this.intakeYear = intakeYear;
	}

	public String getNationalityCode() {
		return nationalityCode;
	}

	public void setNationalityCode(String nationality) {
		this.nationalityCode = nationality;
	}

	public String getSponsorType() {
		return sponsorType;
	}

	public void setSponsorType(String sponsorType) {
		this.sponsorType = sponsorType;
	}

	public String getNationalityStatus() {
		return nationalityStatus;
	}

	public void setNationalityStatus(String statusNationality) {
		this.nationalityStatus = statusNationality;
	}

	public String getDisabilityStatus() {
		return disabilityStatus;
	}

	public void setDisabilityStatus(String statusOKU) {
		this.disabilityStatus = statusOKU;
	}

	public String getStatusStudent() {
		return statusStudent;
	}

	public void setStatusStudent(String statusStudent) {
		this.statusStudent = statusStudent;
	}

	public String getTotalSemester() {
		return totalSemester;
	}

	public void setTotalSemester(String totalSemester) {
		this.totalSemester = totalSemester;
	}

	public String getDisabilityType() {
		return disabilityType;
	}

	public void setDisabilityType(String typeOKU) {
		this.disabilityType = typeOKU;
	}

	public String getGeneratorId() {
		return generatorId;
	}

	public void setGeneratorId(String generatorId) {
		this.generatorId = generatorId;
	}

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	public String getGenderCode() {
		return genderCode;
	}

	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	public String getGenderName() {
		return genderName;
	}

	public void setGenderName(String genderName) {
		this.genderName = genderName;
	}

	public String getRaceCode() {
		return raceCode;
	}

	public void setRaceCode(String raceCode) {
		this.raceCode = raceCode;
	}

	public String getRaceName() {
		return raceName;
	}

	public void setRaceName(String raceName) {
		this.raceName = raceName;
	}

	public String getReligionCode() {
		return religionCode;
	}

	public void setReligionCode(String religionCode) {
		this.religionCode = religionCode;
	}

	public String getReligionName() {
		return religionName;
	}

	public void setReligionName(String religionName) {
		this.religionName = religionName;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getNationalityName() {
		return nationalityName;
	}

	public void setNationalityName(String nationalityName) {
		this.nationalityName = nationalityName;
	}
	
	

}
