package educate.sis.struct.entity;

import java.util.Date;

import javax.persistence.*;

import educate.sis.general.entity.GradsLevel;
import educate.timetabling.entity.AIMDepartment;
import metadata.EntityLister;
/*
 */
@Entity
@Table(name="struc_program")
public class Program implements EntityLister{
	@Id
	private String id;
	private String code;
	private String name;
	@Column(length=10)
	private String matricCode;
	
	private String displayName;
	
	@ManyToOne
	private Course course;
	
	//requirement for timetabling
	@ManyToOne @JoinColumn(name="dept_id")
	private AIMDepartment department;
	
	@ManyToOne
	private PeriodScheme periodScheme;
	private String registerNo;
	private String abbrev;
	@ManyToOne
	private GradsLevel level;
	@Column(name="mohe_code", length=8)
	private String moheCode;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@Column(length=50)
	private String programLevelType;
	
	@Column(length=20)
	private String matricTemplateName;
	
	private int isNoneSessionType;
	
	
	//release releaseTranscript?
	@Transient
	private int releaseTranscript;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date transcriptReleaseDate;
	
	//VERSION OF PROGRAM
	@Column(length=10)
	private String version;
	
	//==========================
	public Program() {
		setId(lebah.db.UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return getCode()+" "+getName();
	}
	public void setCourse(Course course) {
		this.course = course;
	}
	public Course getCourse() {
		return course;
	}
	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}
	public String getAbbrev() {
		return abbrev;
	}
	public void setRegisterNo(String registerNo) {
		this.registerNo = registerNo;
	}
	public String getRegisterNo() {
		return registerNo;
	}
	public PeriodScheme getPeriodScheme() {
		return periodScheme;
	}
	public void setPeriodScheme(PeriodScheme periodScheme) {
		this.periodScheme = periodScheme;
	}
	public void setLevel(GradsLevel level) {
		this.level = level;
	}
	public GradsLevel getLevel() {
		return level;
	}
	public String getMoheCode() {
		if (moheCode == null) {
			moheCode = "";
		}
		return moheCode;
	}
	public void setMoheCode(String moheCode) {
		if (moheCode == null) {
			this.moheCode = "";
		} else {
			this.moheCode = moheCode;
		}
	}
	public String getMatricCode() {
		if ( matricCode != null && !"".equals(matricCode)) return matricCode;
		else return "";
	}
	public void setMatricCode(String matricCode) {
		this.matricCode = matricCode;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getDisplayName() {
		return displayName != null && !"".equals(displayName) ? displayName : name;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getProgramLevelType() {
		return programLevelType;
	}
	public void setProgramLevelType(String programLevelType) {
		this.programLevelType = programLevelType;
	}
	public String getMatricTemplateName() {
		return matricTemplateName == null || "".equals(matricTemplateName) ? course.getMatricTemplateName() : matricTemplateName;
	}
	public void setMatricTemplateName(String matricTemplateName) {
		this.matricTemplateName = matricTemplateName;
	}
	public boolean getIsNoneSessionType() {
		return isNoneSessionType == 1;
	}
	public void setIsNoneSessionType(boolean isNoneSessionType) {
		this.isNoneSessionType = isNoneSessionType ? 1 : 0;
	}
	public boolean getReleaseTranscript() {
		
		releaseTranscript = 0;
		
		if ( transcriptReleaseDate == null ) {
			releaseTranscript = 0;
		} else {
			Date currentDate = new Date();
			if ( currentDate.after(transcriptReleaseDate) ) {
				releaseTranscript = 1;
			}
		}
		
		return releaseTranscript == 1;
	}
	public void setReleaseTranscript(boolean releaseTranscript) {
		this.releaseTranscript = releaseTranscript ? 1 : 0;
		
		this.releaseTranscript = 0;
		
		if ( transcriptReleaseDate == null ) {
			this.releaseTranscript = 0;
		} else {
			Date currentDate = new Date();
			if ( currentDate.after(transcriptReleaseDate) ) {
				this.releaseTranscript = 1;
			}
		}
	}
	public Date getTranscriptReleaseDate() {
		return transcriptReleaseDate;
	}
	public void setTranscriptReleaseDate(Date transcriptReleaseDate) {
		this.transcriptReleaseDate = transcriptReleaseDate;
	}
	public AIMDepartment getDepartment() {
		return department;
	}
	public void setDepartment(AIMDepartment department) {
		this.department = department;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}


	
}
