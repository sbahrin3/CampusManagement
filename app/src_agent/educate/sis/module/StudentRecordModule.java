/**
 * 
 */
package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.PaymentSchedule;
import educate.sis.finance.module.AccountStatement;
import educate.sis.finance.module.AccountStatementUtil;
import educate.sis.finance.module.PaymentUtil;
import educate.sis.finance.module.XPaymentScheduleItem;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.Command;
import lebah.template.LebahRecordTemplateModule;
import lebah.template.OperatorEqualTo;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class StudentRecordModule extends LebahRecordTemplateModule<Student> {

	/* (non-Javadoc)
	 * @see lebah.template.LebahRecordTemplateModule#getIdType()
	 */
	@Override
	public Class getIdType() {
		// TODO Auto-generated method stub
		return String.class;
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#afterSave(java.lang.Object)
	 */
	@Override
	public void afterSave(Student student) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#beforeSave()
	 */
	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#begin()
	 */
	@Override
	public void begin() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		this.setOrderBy("biodata.name");
		this.setReadonly(true);
		this.setRecordAddNew(false);
		this.setRecordDelete(false);
		this.setRecordEdit(false);
		this.setRecordReadonly(true);
		
		context.put("programs", db.list("select p from Program p order by p.code"));
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#delete(java.lang.Object)
	 */
	@Override
	public boolean delete(Student student) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("NOTE: Record is deleted: " + student.getMatricNo());
		return true;
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#getPath()
	 */
	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return "admission/studentRecord";
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#getPersistenceClass()
	 */
	@Override
	public Class<Student> getPersistenceClass() {
		// TODO Auto-generated method stub
		return Student.class;
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#getRelatedData(java.lang.Object)
	 */
	@Override
	public void getRelatedData(Student student) {
		
		try {
			StudentStatus studentStatus = new StudentStatusUtil().getCurrentStudentStatus(student);
			context.put("studentStatus", studentStatus);
			AccountStatement acct = new AccountStatementUtil().getAccountStatement(student);
			context.put("account_statement", acct);
			getExamTranscript(student);
			
			Calendar currentDate = Calendar.getInstance();
			int currentYear = currentDate.get(Calendar.YEAR);
			context.put("currentYear", currentYear);
			
			getPaymentSchedule(student);
			
		} catch (Exception e) {
			context.remove("studentStatus");
			context.remove("account_statement");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#save(java.lang.Object)
	 */
	@Override
	public void save(Student student) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see lebah.template.RecordTemplateModule#searchCriteria()
	 */
	@Override
	public Map<String, Object> searchCriteria() throws Exception {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("biodata.name", getParam("find_name"));
		m.put("program.id", new OperatorEqualTo(getParam("find_programId")));
		m.put("intake.id", new OperatorEqualTo(getParam("find_intakeId")));
		return m;
	}
	
	@Command("listIntakes")
	public String listIntakes() throws Exception {
		String programId = getParam("find_programId");
		if ( "".equals(programId) ) {
			context.remove("intakes");
		} else {
			Program program = db.find(Program.class, programId);
			int pathNo = program.getLevel().getPathNo();
			List<Session> intakes = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
			context.put("intakes", intakes);
		}
		return getPath() + "/listIntakes.vm";
	}
	
	@Command("getAccountStatement")
	public String getAccountStatement() throws Exception {
		String matricNo = request.getParameter("matric_no");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		
		AccountStatementUtil util = new AccountStatementUtil();
		AccountStatement acct = util.getAccountStatement(student);
		
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		context.put("student_status", studentStatus);
		
		context.put("account_statement", acct);
		return getPath() + "/account_statement.vm";
	}
	
	
	private void getExamTranscript(Student student) throws Exception {
		StudentExamTranscriptUtil u = new StudentExamTranscriptUtil(request, context, db);
		u.showExamTranscript(student);
		
		StudentStatusUtil su = new StudentStatusUtil();
		StudentStatus currentStatus = su.getCurrentStudentStatus(student);
		context.put("currentStatus", currentStatus);
		Session currentSession = currentStatus.getSession();
		context.put("current_session", currentSession);
		context.put("display_session", currentSession);
		
		List<StudentStatus> studentStatuses = su.getStudentStatuses(student);
		Session prevSession = null;
		for ( StudentStatus s : studentStatuses ) {
			if ( s.getSession().getId().equals(currentSession.getId())) {
				break;
			}
			prevSession = s.getSession();
		}
		//prevSession must not equal current session, to avoid student with only one session
		if ( prevSession == null ) {
			context.remove("previous_session");
		} else {
			
			if ( !prevSession.getId().equals(currentSession.getId()) )
				context.put("previous_session", prevSession);
			else
				context.remove("previous_session");
		
		}
		
	}
	
	@Command("getAttendanceView")
	public String getAttendanceView() throws Exception {
		Calendar currentDate = Calendar.getInstance();
		int currentYear = currentDate.get(Calendar.YEAR);
		context.put("currentYear", currentYear);
		
		return getPath() + "/classroom_attendance.vm";
	}
	
	private void getPaymentSchedule(Student student) throws Exception {

		PaymentSchedule paymentSchedule = (PaymentSchedule) db.get("select p from PaymentSchedule p where p.student.id = '" + student.getId() + "'");

		if ( paymentSchedule != null ) {
			context.put("paymentSchedule", paymentSchedule);		
			List<XPaymentScheduleItem> items = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
			context.put("scheduleItems", items);
		}
		
		
	}

}
