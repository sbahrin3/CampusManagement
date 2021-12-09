package educate.sis.general.entity;
import educate.sis.struct.entity.*;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name="cm_academiccalendareventb")
public class AcademicCalendarEventB {


	@Id
	private String id;
	private int groupNum;
	
	@Temporal(TemporalType.DATE)
	private Date groupRegistration;
	
	@Temporal(TemporalType.DATE)
	private Date groupRegistrationEnd;
	
	@Temporal(TemporalType.DATE)
	private Date creditTransfer;
	
	@Temporal(TemporalType.DATE)
	private Date creditTransferEnd;
	
	@Temporal(TemporalType.DATE)
	private Date chProgram;
	
	@Temporal(TemporalType.DATE)
	private Date chProgramEnd;
		
	@Temporal(TemporalType.DATE)
	private Date addSubject;
	
	@Temporal(TemporalType.DATE)
	private Date addSubjectEnd;
	
	@Temporal(TemporalType.DATE)
	private Date dropSubject;
	
	@Temporal(TemporalType.DATE)
	private Date dropSubjectEnd;
	
	@Temporal(TemporalType.DATE)
	private Date subWithdraw;
	
	@Temporal(TemporalType.DATE)
	private Date subWithdrawEnd;
	
	@Temporal(TemporalType.DATE)
	private Date chLearnCentre;
	
	@Temporal(TemporalType.DATE)
	private Date chLearnCentreEnd;
	
	@Temporal(TemporalType.DATE)
	private Date deferStudy;
	
	@Temporal(TemporalType.DATE)
	private Date deferStudyEnd;
	
	@Temporal(TemporalType.DATE)
	private Date studyWithdraw;
	
	@Temporal(TemporalType.DATE)
	private Date studyWithdrawEnd;
	
	@Temporal(TemporalType.DATE)
	private Date submitAssignment;
	
	@Temporal(TemporalType.DATE)
	private Date examDefer;
	
	@Temporal(TemporalType.DATE)
	private Date examDeferEnd;
	
	@Temporal(TemporalType.DATE)
	private Date revisionWeek;
	
	@Temporal(TemporalType.DATE)
	private Date revisionWeekEnd;
		
	@Temporal(TemporalType.DATE)
	private Date keyInMark;
	
	@Temporal(TemporalType.DATE)
	private Date keyInMarkEnd;
	
	@Temporal(TemporalType.DATE)
	private Date viewStandingMark;
	
	@Temporal(TemporalType.DATE)
	private Date viewStandingMarkEnd;
	
	@Temporal(TemporalType.DATE)
	private Date preRegNextSem;
	
	@Temporal(TemporalType.DATE)
	private Date preRegNextSemEnd;
	
	@Temporal(TemporalType.DATE)
	private Date regSem;
	
	@Temporal(TemporalType.DATE)
	private Date regSemEnd;
	
	@Temporal(TemporalType.DATE)
	private Date examWeek;
	
	@Temporal(TemporalType.DATE)
	private Date examWeekEnd;
	
	@Temporal(TemporalType.DATE)
	private Date keyInMarkFinal;
	
	@Temporal(TemporalType.DATE)
	private Date keyInMarkFinalEnd;
	
	@Temporal(TemporalType.DATE)
	private Date supExamWeek;
	
	@Temporal(TemporalType.DATE)
	private Date supExamWeekEnd;
	
	@Temporal(TemporalType.DATE)
	private Date keyInMarkSupExm;
	
	@Temporal(TemporalType.DATE)
	private Date keyInMarkSupExmEnd;
	
	@OneToOne
	private Session session;
	
	public AcademicCalendarEventB(){
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getGroupNum() {
		return groupNum;
	}

	public void setGroupNum(int groupNum) {
		this.groupNum = groupNum;
	}

	public Date getCreditTransfer() {
		return creditTransfer;
	}

	public void setCreditTransfer(String date) {
		if (date == null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setCreditTransfer(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void  setCreditTransfer(Object d){
		if (d instanceof Date) creditTransfer=(Date) d;
		else if (d instanceof String){
			setCreditTransfer((String) d);
		}
	}

	public Date getCreditTransferEnd() {
		return creditTransferEnd;
	}

	public void setCreditTransferEnd(String date) {
		if (date == null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setCreditTransferEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setCreditTransferEnd(Object d){
		if (d instanceof Date) creditTransferEnd=(Date) d;
		else if (d instanceof String){
			setCreditTransferEnd((String) d);
		}
	}

	public Date getChProgram() {
		return chProgram;
	}

	public void setChProgram(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setChProgram(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setChProgram(Object d){
		if (d instanceof Date) chProgram=(Date) d;
		else if (d instanceof String){
			setChProgram((String) d);
		}
	}

	public Date getChProgramEnd() {
		return chProgramEnd;
	}

	public void setChProgramEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setChProgramEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setChProgramEnd(Object d){
		if (d instanceof Date) chProgramEnd=(Date) d;
		else if (d instanceof String){
			setChProgramEnd((String) d);
		}
	}

	public Date getAddSubject() {
		return addSubject;
	}

	public void setAddSubject(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setAddSubject(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setAddSubject(Object d){
		if (d instanceof Date) addSubject=(Date) d;
		else if (d instanceof String){
			setAddSubject((String) d);
		}
	}

	public Date getAddSubjectEnd() {
		return addSubjectEnd;
	}

	public void setAddSubjectEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setAddSubjectEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setAddSubjectEnd(Object d){
		if (d instanceof Date) addSubjectEnd=(Date) d;
		else if (d instanceof String){
			setAddSubjectEnd((String) d);
		}
	}

	public Date getDropSubject() {
		return dropSubject;
	}

	public void setDropSubject(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setDropSubject(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setDropSubject(Object d){
		if (d instanceof Date) dropSubject=(Date) d;
		else if (d instanceof String){
			setDropSubject((String) d);
		}
	}

	public Date getDropSubjectEnd() {
		return dropSubjectEnd;
	}

	public void setDropSubjectEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setDropSubjectEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setDropSubjectEnd(Object d){
		if (d instanceof Date) dropSubjectEnd=(Date) d;
		else if (d instanceof String){
			setDropSubjectEnd((String) d);
		}
	}

	public Date getSubWithdraw() {
		return subWithdraw;
	}

	public void setSubWithdraw(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setSubWithdraw(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setSubWithdraw(Object d){
		if (d instanceof Date) subWithdraw=(Date) d;
		else if (d instanceof String){
			setSubWithdraw((String) d);
		}
	}

	public Date getSubWithdrawEnd() {
		return subWithdrawEnd;
	}

	public void setSubWithdrawEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setSubWithdrawEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setSubWithdrawEnd(Object d){
		if (d instanceof Date) subWithdrawEnd=(Date) d;
		else if (d instanceof String){
			setSubWithdrawEnd((String) d);
		}
	}

	public Date getChLearnCentre() {
		return chLearnCentre;
	}

	public void setChLearnCentre(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setChLearnCentre(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setChLearnCentre(Object d){
		if (d instanceof Date) chLearnCentre=(Date) d;
		else if (d instanceof String){
			setChLearnCentre((String) d);
		}
	}

	public Date getChLearnCentreEnd() {
		return chLearnCentreEnd;
	}

	public void setChLearnCentreEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setChLearnCentreEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setChLearnCentreEnd(Object d){
		if (d instanceof Date) chLearnCentreEnd=(Date) d;
		else if (d instanceof String){
			setChLearnCentreEnd((String) d);
		}
	}

	public Date getDeferStudy() {
		return deferStudy;
	}

	public void setDeferStudy(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setDeferStudy(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setDeferStudy(Object d){
		if (d instanceof Date) deferStudy=(Date) d;
		else if (d instanceof String){
			setDeferStudy((String) d);
		}
	}

	public Date getDeferStudyEnd() {
		return deferStudyEnd;
	}

	public void setDeferStudyEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setDeferStudyEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setDeferStudyEnd(Object d){
		if (d instanceof Date) deferStudyEnd=(Date) d;
		else if (d instanceof String){
			setDeferStudyEnd((String) d);
		}
	}

	public Date getStudyWithdraw() {
		return studyWithdraw;
	}

	public void setStudyWithdraw(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setStudyWithdraw(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setStudyWithdraw(Object d){
		if (d instanceof Date) studyWithdraw=(Date) d;
		else if (d instanceof String){
			setStudyWithdraw((String) d);
		}
	}

	public Date getStudyWithdrawEnd() {
		return studyWithdrawEnd;
	}

	public void setStudyWithdrawEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setStudyWithdrawEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setStudyWithdrawEnd(Object d){
		if (d instanceof Date) studyWithdrawEnd=(Date) d;
		else if (d instanceof String){
			setStudyWithdrawEnd((String) d);
		}
	}

	public Date getSubmitAssignment() {
		return submitAssignment;
	}

	public void setSubmitAssignment(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setSubmitAssignment(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setSubmitAssignment(Object d){
		if (d instanceof Date) submitAssignment=(Date) d;
		else if (d instanceof String){
			setSubmitAssignment((String) d);
		}
	}

	public Date getExamDefer() {
		return examDefer;
	}

	public void setExamDefer(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setExamDefer(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setExamDefer(Object d){
		if (d instanceof Date) examDefer=(Date) d;
		else if (d instanceof String){
			setExamDefer((String) d);
		}
	}

	public Date getExamDeferEnd() {
		return examDeferEnd;
	}

	public void setExamDeferEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setExamDeferEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setExamDeferEnd(Object d){
		if (d instanceof Date) examDeferEnd=(Date) d;
		else if (d instanceof String){
			setExamDeferEnd((String) d);
		}
	}

	public Date getRevisionWeek() {
		return revisionWeek;
	}

	public void setRevisionWeek(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setRevisionWeek(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setRevisionWeek(Object d){
		if (d instanceof Date) revisionWeek=(Date) d;
		else if (d instanceof String){
			setRevisionWeek((String) d);
		}
	}

	public Date getKeyInMark() {
		return keyInMark;
	}

	public void setKeyInMark(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setKeyInMark(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setKeyInMark(Object d){
		if (d instanceof Date) keyInMark=(Date) d;
		else if (d instanceof String){
			setKeyInMark((String) d);
		}
	}

	public Date getKeyInMarkEnd() {
		return keyInMarkEnd;
	}

	public void setKeyInMarkEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setKeyInMarkEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setKeyInMarkEnd(Object d){
		if (d instanceof Date) keyInMarkEnd=(Date) d;
		else if (d instanceof String){
			setKeyInMarkEnd((String) d);
		}
	}

	public Date getViewStandingMark() {
		return viewStandingMark;
	}

	public void setViewStandingMark(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setViewStandingMark(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setViewStandingMark(Object d){
		if (d instanceof Date) viewStandingMark=(Date) d;
		else if (d instanceof String){
			setViewStandingMark((String) d);
		}
	}

	public Date getPreRegNextSem() {
		return preRegNextSem;
	}

	public void setPreRegNextSem(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setPreRegNextSem(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setPreRegNextSem(Object d){
		if (d instanceof Date) preRegNextSem=(Date) d;
		else if (d instanceof String){
			setPreRegNextSem((String) d);
		}
	}

	public Date getPreRegNextSemEnd() {
		return preRegNextSemEnd;
	}

	public void setPreRegNextSemEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setPreRegNextSemEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setPreRegNextSemEnd(Object d){
		if (d instanceof Date) preRegNextSemEnd=(Date) d;
		else if (d instanceof String){
			setPreRegNextSemEnd((String) d);
		}
	}

	public Date getExamWeek() {
		return examWeek;
	}

	public void setExamWeek(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setExamWeek(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setExamWeek(Object d){
		if (d instanceof Date) examWeek=(Date) d;
		else if (d instanceof String){
			setExamWeek((String) d);
		}
	}

	public Date getExamWeekEnd() {
		return examWeekEnd;
	}

	public void setExamWeekEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setExamWeekEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setExamWeekEnd(Object d){
		if (d instanceof Date) examWeekEnd=(Date) d;
		else if (d instanceof String){
			setExamWeekEnd((String) d);
		}
	}

	public Date getKeyInMarkFinal() {
		return keyInMarkFinal;
	}

	public void setKeyInMarkFinal(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setKeyInMarkFinal(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setKeyInMarkFinal(Object d){
		if (d instanceof Date) keyInMarkFinal=(Date) d;
		else if (d instanceof String){
			setKeyInMarkFinal((String) d);
		}
	}

	public Date getKeyInMarkFinalEnd() {
		return keyInMarkFinalEnd;
	}

	public void setKeyInMarkFinalEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setKeyInMarkFinalEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setKeyInMarkFinalEnd(Object d){
		if (d instanceof Date) keyInMarkFinalEnd=(Date) d;
		else if (d instanceof String){
			setKeyInMarkFinalEnd((String) d);
		}
	}
	
	public Date getSupExamWeek() {
		return supExamWeek;
	}

	public void setSupExamWeek(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setSupExamWeek(new GregorianCalendar(year, month, day).getTime());
	}

	public void setSupExamWeek(Object d){
		if (d instanceof Date) supExamWeek=(Date) d;
		else if (d instanceof String){
			setSupExamWeek((String) d);
		}
	}
	
	public Date getSupExamWeekEnd() {
		return supExamWeekEnd;
	}

	public void setSupExamWeekEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setSupExamWeekEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setSupExamWeekEnd(Object d){
		if (d instanceof Date) supExamWeekEnd=(Date) d;
		else if (d instanceof String){
			setSupExamWeekEnd((String) d);
		}
	}

	public Date getKeyInMarkSupExm() {
		return keyInMarkSupExm;
	}

	public void setKeyInMarkSupExm(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setKeyInMarkSupExm(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setKeyInMarkSupExm(Object d){
		if (d instanceof Date) keyInMarkSupExm=(Date) d;
		else if (d instanceof String){
			setKeyInMarkSupExm((String) d);
		}
	}

	public Date getKeyInMarkSupExmEnd() {
		return keyInMarkSupExmEnd;
	}

	public void setKeyInMarkSupExmEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setKeyInMarkSupExmEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setKeyInMarkSupExmEnd(Object d){
		if (d instanceof Date) keyInMarkSupExmEnd=(Date) d;
		else if (d instanceof String){
			setKeyInMarkSupExmEnd((String) d);
		}
	}
	
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	

	public Date getGroupRegistration() {
		return groupRegistration;
	}

	public void setGroupRegistration(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setGroupRegistration(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setGroupRegistration(Object d){
		if (d instanceof Date) groupRegistration=(Date) d;
		else if (d instanceof String){
			setGroupRegistration((String) d);
		}
	}

	public Date getGroupRegistrationEnd() {
		return groupRegistrationEnd;
	}

	public void setGroupRegistrationEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setGroupRegistrationEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setGroupRegistrationEnd(Object d){
		if (d instanceof Date) groupRegistrationEnd=(Date) d;
		else if (d instanceof String){
			setGroupRegistrationEnd((String) d);
		}
	}

	public Date getRevisionWeekEnd() {
		return revisionWeekEnd;
	}

	public void setRevisionWeekEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setRevisionWeekEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setRevisionWeekEnd(Object d){
		if (d instanceof Date) revisionWeekEnd=(Date) d;
		else if (d instanceof String){
			setRevisionWeekEnd((String) d);
		}
	}

	public Date getViewStandingMarkEnd() {
		return viewStandingMarkEnd;
	}

	public void setViewStandingMarkEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setViewStandingMarkEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setViewStandingMarkEnd(Object d){
		if (d instanceof Date) viewStandingMarkEnd=(Date) d;
		else if (d instanceof String){
			setViewStandingMarkEnd((String) d);
		}
	}

	public Date getRegSem() {
		return regSem;
	}

	public void setRegSem(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setRegSem(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setRegSem(Object d){
		if (d instanceof Date) regSem=(Date) d;
		else if (d instanceof String){
			setRegSem((String) d);
		}
	}

	public Date getRegSemEnd() {
		return regSemEnd;
	}

	public void setRegSemEnd(String date) {
		if (date==null || "".equals(date))return;
		String separator="-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setRegSemEnd(new GregorianCalendar(year, month, day).getTime());
	}
	
	public void setRegSemEnd(Object d){
		if (d instanceof Date) regSemEnd=(Date) d;
		else if (d instanceof String){
			setRegSemEnd((String) d);
		}
	}
	
}
