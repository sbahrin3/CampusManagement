package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.velocity.Template;

import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.Payment;
import educate.sis.finance.entity.PaymentItem;
import educate.sis.finance.module.MoneyInWord;
import lebah.portal.velocity.VTemplate;
import lebah.template2.DbPersistence;

public class PrintStudentReceiptModule2 extends VTemplate {
	
	String path = "apps/util/finance/payment_custom/";
	DbPersistence db = new DbPersistence();
	
	public Template doTemplate() throws Exception {
		setShowVM(false);
		context.put("today", new Date());
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("df", new SimpleDateFormat("dd MMM, yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.put("util", new lebah.util.Util());
		
		Template template = engine.getTemplate(getReceipt());	
		return template;		
	}

	private String getReceipt() throws Exception {
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
        String http = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("://") + 3);
        String serverUrl = http + server;
        context.put("serverUrl", serverUrl);
        String uri = request.getRequestURI();
        String appname = uri.substring(1);
        appname = appname.substring(0, appname.indexOf("/"));
        context.put("appUrl", serverUrl.concat("/").concat(appname));      
        context.put("currentDate", new Date());
        
		String paymentNo = request.getParameter("paymentNo");
		
		System.out.println("PAYMENT NO = " + paymentNo);
		
		//Payment payment = db.find(Payment.class, paymentId);
		Payment payment = (Payment) db.get("select p from Payment p where p.paymentNo = '" + paymentNo + "'");
		
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus currentStatus = u.getCurrentStudentStatus(payment.getStudent());
		context.put("student_status", currentStatus);
		context.put("student", payment.getStudent());
		context.put("payment", payment);
		context.put("payment_items", payment.getPaymentItems());
		
		
		double totalPaid = 0.0d;
		
		for ( PaymentItem p : payment.getPaymentItems()) {
			totalPaid += p.getAmount();
		}
		context.put("totalPaid", totalPaid);
		MoneyInWord w = new MoneyInWord();
		context.put("totalPaidInWord", w.toWord(totalPaid));		
		
		return path + "print_template.vm";
	}


}
