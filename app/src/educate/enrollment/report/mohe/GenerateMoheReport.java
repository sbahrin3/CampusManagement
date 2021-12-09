package educate.enrollment.report.mohe;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.enrollment.entity.MoheRecord;
import educate.enrollment.entity.MoheRecordStatus;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.exam.entity.FinalResult;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0 
 * @date 27.8.2011
 */
public class GenerateMoheReport {
	
	public static void main(String[] args) throws Exception {
		createReport("");
	}
	
	public static long createReport(String userid) throws Exception {
		
		System.out.println("Generating MOHE report, by " + userid);
		
		long timeStart = System.currentTimeMillis();
		
		DbPersistence db = new DbPersistence();
		
		db.begin();
		db.executeUpdate("delete from MoheRecord r");
		db.commit();
		
		MoheRecordStatus mrs = (MoheRecordStatus) db.get("select mrs from MoheRecordStatus mrs");
		if ( mrs == null ) {
			db.begin();
			mrs = new MoheRecordStatus();
			mrs.setCreatedBy(userid);
			mrs.setDateCreated(new Date());
			db.persist(mrs);
			db.commit();
		}
		else {
			db.begin();
			mrs.setCreatedBy(userid);
			mrs.setDateCreated(new Date());
			db.commit();
		}
		
		System.out.println("Collecting CGPA information..");
		Map<String, String> exams = resultInfo(db);
		
		create(1, new Date(), userid, exams);
		create(2, new Date(), userid, exams);
		
		long timeEnd = System.currentTimeMillis();
		
		long timeTaken = timeEnd - timeStart; //in milliseconds
		
		Runtime r = Runtime.getRuntime();
		r.gc();

		
		return timeTaken;
	}
	
	public static Map<String, String> resultInfo(DbPersistence db) throws Exception {
		String sql = "";
		//Collect exam result information first
		sql = "select f from FinalResult f order by f.student.id, f.session.startDate desc";
		List<FinalResult> exams = db.list(sql);
		String tmp = "";
		Map<String, String> m = new HashMap<String, String>();
		NumberFormat formatter = new DecimalFormat(".00");

		for ( FinalResult f : exams ) {
			if ( !tmp.equals(f.getStudent().getId()) ) {
				tmp = f.getStudent().getId();
				m.put(f.getStudent().getId(), formatter.format(f.getCgpa()));
			}
		}
		return m;
	}
	
	public static String getCGPA(DbPersistence db, String studentId) throws Exception {
		String sql = "";
		//Collect exam result information first
		sql = "select f from FinalResult f where f.student.id = '" + studentId + "' order by f.session.startDate desc";
		List<FinalResult> exams = db.list(sql);
		String tmp = "";
		NumberFormat formatter = new DecimalFormat(".00");
		if ( exams.size() > 0 ) {
			FinalResult result = exams.get(0);
			return formatter.format(result.getCgpa());
		}
		return "";
	}
	
	public static Date expectedEndDate(DbPersistence db, String studentId) throws Exception {
		String sql = "";
		sql = "select MAX(s.session.endDate) from StudentStatus s where s.student.id = '" + studentId + "'";
		Date endDate = (Date) db.get(sql);
		return endDate;
	}
	
	public static int create(int type, Date date, String userid, Map<String, String> exams) throws Exception {
		DbPersistence db = new DbPersistence();
		System.out.println("MOHE report generated on " + new SimpleDateFormat("dd-MM-yyyy").format(date));

		Hashtable h = new Hashtable();
		h.put("date", date);
		
		String sql = "";
		
		sql = "select ss from StudentStatus ss join ss.student s ";
		sql += "where :date between ss.session.startDate and ss.session.endDate ";
		
		if ( type > 0 ) {
			if ( type == 1 )      sql += "and s.biodata.icno <> '' and s.biodata.nationality is null ";
			else if ( type == 2 ) sql += "and s.biodata.icno = '' and s.biodata.nationality is not null ";
		}
		
		sql += "AND s.fakeStudent IS NULL AND ss.type.code <> 'QUIT' AND ss.type.code <> 'DEC' AND ss.type.code <> 'GRAD' AND ss.type.code <> 'DMIS' ";
		sql += "ORDER BY s.intake.startDate, s.biodata.name";
		
		List<StudentStatus> list = db.list(sql, h);
		
		db.begin();
		for ( StudentStatus ss : list ) {
			Student s = ss.getStudent();
			MoheRecord r = new MoheRecord();
			
			if ( type == 0 ) {
				if ( !"".equals(s.getBiodata().getIcno()) ) r.setReportType(1);
				else r.setReportType(2);
			}
			else
				r.setReportType(type);
			
			r.setName(s.getBiodata().getName());
			r.setIcno(s.getBiodata().getIcno() != null ?s.getBiodata().getIcno() : "");
			r.setPassport(s.getBiodata().getPassport());
			if ( s.getBiodata().getDob() != null ) {
				r.setDob(s.getBiodata().getDob());
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(s.getBiodata().getDob());
				r.setBirthYear(calendar.get(Calendar.YEAR));
			}
			else {
				//trying getting this year from mykad
				if ( !"".equals(r.getIcno()) && r.getIcno().length() == 12 ) {  //it must be 12 chars length
					String yearPart = r.getIcno().substring(0, 2);
				
					int y = Integer.parseInt(yearPart);
					if ( y > 9 && y < 20 ) yearPart = "20" + yearPart;
					else yearPart = "19" + yearPart; 
					
					r.setBirthYear(Integer.parseInt(yearPart));
				}
			}
			
			r.setPostcode(s.getPermanentAddress().getPostcode());
			r.setGenderCode(s.getBiodata().getGender() != null ? s.getBiodata().getGender().getCode() : "");
			r.setGenderName(s.getBiodata().getGender() != null ? s.getBiodata().getGender().getName() : "");
			r.setRaceCode(s.getBiodata().getRace() != null ? s.getBiodata().getRace().getCode() : "");
			r.setRaceName(s.getBiodata().getRace() != null ? s.getBiodata().getRace().getName() : "");
			
			r.setStateCode(s.getAddress() != null && s.getAddress().getState() != null ? s.getAddress().getState().getCode() : "");
			r.setStateName(s.getAddress() != null && s.getAddress().getState() != null ? s.getAddress().getState().getName() : "");
			r.setNationalityName(s.getBiodata().getNationality() != null ? getNationality(s.getBiodata().getNationality().getName()) : "");
			r.setNationalityCode(s.getBiodata().getNationality() != null ? s.getBiodata().getNationality().getCode() : "");
			r.setNationalityStatus(s.getBiodata().getNationality() != null ? getNationalityStatus(s.getBiodata().getNationality().getCode()) : "");
			
			Calendar intake = Calendar.getInstance();
			intake.setTime(s.getIntake().getStartDate());
			r.setIntakeMonth(intake.get(Calendar.MONTH) + 1);
			r.setIntakeYear(intake.get(Calendar.YEAR));
		
			//mohe course code
			r.setCodeCourse(s.getProgram() != null ? s.getProgram().getMoheCode() : "");
			//status pelajar
			r.setStatusStudent(ss.getType() != null ? ss.getType().getMoheCode() : "");
			//disability code
			r.setDisabilityStatus(s.getApplicant() != null ? s.getApplicant().getDisability() != null ? getDisability(s.getApplicant().getDisability().getCode()) : "no" : "no");
			r.setDisabilityType(s.getApplicant() != null ? 
					s.getApplicant().getDisability() != null ? "yes".equals(getDisability(s.getApplicant().getDisability().getCode())) ?
							s.getApplicant().getDisability().getCode() : "" : "" : "" );
			//sponsor code
			r.setSponsorType(s.getApplicant().getSponsor() != null ? s.getApplicant().getSponsor().getMoheCode() : "");
			//register date
			r.setDateRegister(s.getRegisterDate() != null ? s.getRegisterDate() : null);
			//highest education
			r.setEntryQualification(s.getApplicant().getHighestEducation() != null ? s.getApplicant().getHighestEducation().getMoheCode() : "");
			//semester semasa
			r.setCurrentSemester(ss.getPeriod() != null ? ss.getPeriod().getName() : "NULL");
			//jumlah semester
			if ( ss.getPeriod() != null && s.getProgram() != null && s.getProgram().getPeriodScheme() != null && s.getProgram().getPeriodScheme().getFunctionalPeriodList() != null ) {
				int totalSemester = s.getProgram().getPeriodScheme().getFunctionalPeriodList().size();
				r.setTotalSemester(Integer.toString(totalSemester));
			}
			else 
				r.setTotalSemester("");
			
			//s.getProgram().getPeriodScheme();
			
			String cgpa = exams.get(s.getId());
			//String cgpa = getCGPA(db, s.getId()); // <-- can cause OutOfMemoryError
			if ( cgpa != null ) r.setCgpa(cgpa);
			
			
			
			Date endDate = expectedEndDate(db, s.getId());
			if ( endDate != null ) {
				Calendar d = Calendar.getInstance();
				d.setTime(endDate);
				r.setExpectedStudyEndMonth(d.get(Calendar.MONDAY) + 1);
				r.setExpectedStudyEndYear(d.get(Calendar.YEAR));
			}

			
			db.persist(r);
		}
		db.commit();
		System.out.println("DONE..");
		
		return list.size();
		
	}
	
	private static String getNationalityStatus(String nationalityCode) {
		if (nationalityCode == null) {
			return "1"; 
		} else if ("103".equals(nationalityCode)) {
			return "1"; // Citizen.
		} else {
			return "2"; // Not Citizen.
		}
	}
	
	private static String getNationality(String nationality) {
		if (nationality == null || "".equals(nationality)) {
			return "MALAYSIA";
		} else {
			return nationality;
		}
	}
	
	public static String getDisability(String disability) {
		if (disability == null) {
			return "";
		} else {
			if("12".equals(disability) ||
				"3".equals(disability) || 
				"4".equals(disability) ||
				"7".equals(disability)) {
				return "no";
			} else {
				return "yes";
			}
		}
	}

}
