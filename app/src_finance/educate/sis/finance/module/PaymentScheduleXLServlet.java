package educate.sis.finance.module;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.poi.Excel1;
import educate.sis.finance.entity.PaymentSchedule;
import lebah.servlets.IServlet;

public class PaymentScheduleXLServlet  implements IServlet {
	private DbPersistence db = new DbPersistence();
	List<XPaymentScheduleItem> scheduleItems;
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
	
		String paymentScheduleId = request.getParameter("paymentScheduleId");
		try {
			getPaymentSchedule(paymentScheduleId, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void getPaymentSchedule(String paymentScheduleId, HttpServletResponse response) throws Exception {
		
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		DecimalFormat nf = new DecimalFormat("#,###,###.00");
        
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		Student student = paymentSchedule.getStudent();
		
		String matricNo = paymentSchedule.getStudent().getMatricNo();
		
		String id = paymentSchedule.getStudent().getId();
		
		scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		List<String> headers = new ArrayList<String>();
		headers.add("Type");
		headers.add("Due Date");
		headers.add("Amount Due");
		headers.add("Payment Date");
		headers.add("Paid");
		headers.add("Balance");
		headers.add("Outstanding");
		
		List<List> rows =  new ArrayList<List>();
		for ( XPaymentScheduleItem i : scheduleItems ) {
			List cols = new ArrayList();
			
			
			if ( i.getType() == 0 )
				cols.add("MONTHLY/FLEXI");
			else if ( i.getType() == 1 )	
				cols.add("PTPTN");
			else if ( i.getType() == 2 )	
				cols.add("RSRC FEE");
			else if ( i.getType() == 3 )	
				cols.add("SEMESTER");
			else if ( i.getType() == 4 )	
				cols.add("MISC");
			else if ( i.getType() == 6 )	
				cols.add("CREDIT NOTE");
			
			
			cols.add(df.format(i.getPaymentDate()));
			cols.add(i.getAmountDue());
			if ( i.getPaidDate() != null ) {
				cols.add(df.format(i.getPaidDate()));
			} else {
				cols.add("");
			}
			cols.add(i.getAmountPaid());
			cols.add(i.getCurrentBalance());
			cols.add(i.getOutstandingBalance());
			rows.add(cols);
		}
		
		
		String filename = "paymentschedule_" + id;
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
		
		ServletOutputStream os = response.getOutputStream();
		Excel1 x = new Excel1();
		try {
			
			List<String> txts = new ArrayList<String>();
			txts.add("Student ID: " + student.getMatricNo());
			txts.add("Name: " + student.getBiodata().getName());
			txts.add("");
			x.createXLS(os, headers, null, null, txts, null, rows, null);
			//x.createXLS(os, headers, rows);
		} catch (Exception e) {
			e.printStackTrace();
		}
		os.flush();
		os.close();
		
	}
	
	/*
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
		List<PtptnPaymentScheduleItem> ptptnScheduleItems = ptptnPaymentSchedule.getItems();
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
		List<ResourceFeePaymentScheduleItem> resourceFeeScheduleItems = resourceFeePaymentSchedule.getItems();
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
		List<SemesterPaymentScheduleItem> semesterItems = semesterPaymentSchedule.getItems();
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
		List<HostelPaymentScheduleItem> hostelItems = hostelPaymentSchedule.getItems();
	}
	*/

}
