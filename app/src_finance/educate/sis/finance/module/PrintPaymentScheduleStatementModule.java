/**
 * 
 */
package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.velocity.Template;

import educate.enrollment.entity.Student;
import educate.sis.finance.entity.HostelPaymentSchedule;
import educate.sis.finance.entity.HostelPaymentScheduleItem;
import educate.sis.finance.entity.PaymentSchedule;
import educate.sis.finance.entity.PtptnPaymentSchedule;
import educate.sis.finance.entity.PtptnPaymentScheduleItem;
import educate.sis.finance.entity.ResourceFeePaymentSchedule;
import educate.sis.finance.entity.ResourceFeePaymentScheduleItem;
import educate.sis.finance.entity.SemesterPaymentSchedule;
import educate.sis.finance.entity.SemesterPaymentScheduleItem;
import lebah.portal.velocity.VTemplate;
import lebah.template2.DbPersistence;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class PrintPaymentScheduleStatementModule extends VTemplate {
	
	String path = "apps/finance/studentPaymentSchedule/";
	DbPersistence db = new DbPersistence();
	
	public Template doTemplate() throws Exception {
		setShowVM(false);
		context.put("today", new Date());
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("df", new SimpleDateFormat("dd MMM, yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.put("util", new lebah.util.Util());
		
		Template template = engine.getTemplate(getPaymentSchedule());	
		return template;		
	}

	private String getPaymentSchedule() throws Exception {
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
        
		String paymentScheduleId = request.getParameter("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		context.put("paymentSchedule", paymentSchedule);

		
		getPtptnPaymentSchedule(paymentSchedule);
		getResourceFeePaymentSchedule(paymentSchedule);
		
		getSemesterPaymentSchedule(paymentSchedule);
		getHostelPaymentSchedule(paymentSchedule);
		
		
		List<XPaymentScheduleItem> items = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		context.put("scheduleItems", items);
		
		Student student = paymentSchedule.getStudent();
		context.put("student", student);
		
		return path + "print_paymentSchedule.vm";
	}
	
	
	private void getPtptnPaymentSchedule(PaymentSchedule paymentSchedule) throws Exception {
		PtptnPaymentSchedule ptptnPaymentSchedule = (PtptnPaymentSchedule) db.get("select p from PtptnPaymentSchedule p where p.student.id = '" + paymentSchedule.getStudent().getId() + "'");
		if ( ptptnPaymentSchedule == null ) {
			db.begin();
			ptptnPaymentSchedule = new PtptnPaymentSchedule();
			ptptnPaymentSchedule.setStudent(paymentSchedule.getStudent());
			ptptnPaymentSchedule.setPtptnAmount(paymentSchedule.getPtptnAmount());
			List<PtptnPaymentScheduleItem> ptptnScheduleItems = new ArrayList<PtptnPaymentScheduleItem>();
			ptptnPaymentSchedule.setItems(ptptnScheduleItems);
			db.persist(ptptnPaymentSchedule);
			db.commit();
		}
		context.put("ptptnPaymentSchedule", ptptnPaymentSchedule);
		List<PtptnPaymentScheduleItem> ptptnScheduleItems = ptptnPaymentSchedule.getItems();
		context.put("ptptnItems", ptptnScheduleItems);
	}

	private void getResourceFeePaymentSchedule(PaymentSchedule paymentSchedule) throws Exception {
		ResourceFeePaymentSchedule resourceFeePaymentSchedule = (ResourceFeePaymentSchedule) db.get("select p from ResourceFeePaymentSchedule p where p.student.id = '" + paymentSchedule.getStudent().getId() + "'");
		if ( resourceFeePaymentSchedule == null ) {
			db.begin();
			resourceFeePaymentSchedule = new ResourceFeePaymentSchedule();
			resourceFeePaymentSchedule.setStudent(paymentSchedule.getStudent());
			
			List<ResourceFeePaymentScheduleItem> resourceFeeScheduleItems = new ArrayList<ResourceFeePaymentScheduleItem>();
			resourceFeePaymentSchedule.setItems(resourceFeeScheduleItems);
			db.persist(resourceFeePaymentSchedule);
			db.commit();
		}
		context.put("resourceFeePaymentSchedule", resourceFeePaymentSchedule);
		List<ResourceFeePaymentScheduleItem> resourceFeeScheduleItems = resourceFeePaymentSchedule.getItems();
		context.put("resourceFeeItems", resourceFeeScheduleItems);
	}
	
	private void getSemesterPaymentSchedule(PaymentSchedule paymentSchedule) throws Exception {
		SemesterPaymentSchedule semesterPaymentSchedule = (SemesterPaymentSchedule) db.get("select p from SemesterPaymentSchedule p where p.student.id = '" + paymentSchedule.getStudent().getId() + "'");
		if ( semesterPaymentSchedule == null ) {
			db.begin();
			semesterPaymentSchedule = new SemesterPaymentSchedule();
			semesterPaymentSchedule.setStudent(paymentSchedule.getStudent());
			
			List<SemesterPaymentScheduleItem> resourceFeeScheduleItems = new ArrayList<SemesterPaymentScheduleItem>();
			semesterPaymentSchedule.setItems(resourceFeeScheduleItems);
			db.persist(semesterPaymentSchedule);
			db.commit();
		}
		context.put("semesterPaymentSchedule", semesterPaymentSchedule);
		List<SemesterPaymentScheduleItem> semesterItems = semesterPaymentSchedule.getItems();
		context.put("semesterItems", semesterItems);
	}
	
	private void getHostelPaymentSchedule(PaymentSchedule paymentSchedule) throws Exception {
		HostelPaymentSchedule hostelPaymentSchedule = (HostelPaymentSchedule) db.get("select p from HostelPaymentSchedule p where p.student.id = '" + paymentSchedule.getStudent().getId() + "'");
		if ( hostelPaymentSchedule == null ) {
			db.begin();
			hostelPaymentSchedule = new HostelPaymentSchedule();
			hostelPaymentSchedule.setStudent(paymentSchedule.getStudent());
			
			List<HostelPaymentScheduleItem> resourceFeeScheduleItems = new ArrayList<HostelPaymentScheduleItem>();
			hostelPaymentSchedule.setItems(resourceFeeScheduleItems);
			db.persist(hostelPaymentSchedule);
			db.commit();
		}
		context.put("hostelPaymentSchedule", hostelPaymentSchedule);
		List<HostelPaymentScheduleItem> hostelItems = hostelPaymentSchedule.getItems();
		context.put("hostelItems", hostelItems);
	}
}
