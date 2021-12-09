package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.PaymentSchedule;
import educate.sis.module.StudentStatusUtil;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentPaymentScheduleViewModule extends LebahModule {
	
	private String path = "apps/finance/studentPaymentScheduleView";
	private DbPersistence db = new DbPersistence();
	private String userId = "";
	

	@Override
	public String start() {
		
		userId = (String) request.getSession().getAttribute("_portal_login");
		
		if (userId.equals("none")) {
			System.out.println("user id is none");
			userId = "0002CIB0010516FT";
		}
		
		try {
			getPaymentSchedule();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return path + "/start.vm";
	}
	
	
	public void preProcess() {
		System.out.println(command);
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("numFormat", new DecimalFormat("#.00"));
	}
	
	
	@Command("getPaymentSchedule")
	public String getPaymentSchedule() throws Exception {
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + userId + "'");
		context.put("student", student);
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		context.put("studentStatus", studentStatus);
		
		PaymentSchedule paymentSchedule = (PaymentSchedule) db.get("select p from PaymentSchedule p where p.student.id = '" + student.getId() + "'");
		if ( paymentSchedule == null ) {
			return path + "/createPaymentSchedule.vm";
		}
		
		context.put("paymentSchedule", paymentSchedule);
		
		List<XPaymentScheduleItem> items = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		context.put("scheduleItems", items);
		
		return path + "/getPaymentSchedule.vm";
	}

}
