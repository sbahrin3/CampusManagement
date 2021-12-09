package educate.sis.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.poi.Excel1;
import educate.sis.finance.entity.Payment;
import lebah.servlets.IServlet;

public class PaymentReportXLServlet implements IServlet {

	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	
			String filename = "payment_report";
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
			
			List<Payment> payments = (List<Payment>) request.getSession().getAttribute("payment_report");
			
			List<String> headers = new ArrayList<String>();
			List<List> rows =  new ArrayList<List>();
			headers.add("Name");
			headers.add("Receipt No.");
			headers.add("Date");
			headers.add("Payment Type");
			headers.add("Amount"); 		
			
			for ( Payment payment : payments ) {
				List cols = new ArrayList();
				cols.add(payment.getStudent().getBiodata().getName());
				cols.add(payment.getPaymentNo());
				cols.add(payment.getCreateDate());
				
				String type = payment.getPaymentMode();
				if ( type.equals("Check")) type = "Cheque";
				
				cols.add(type);
				cols.add(payment.getAmount());
				rows.add(cols);
			}

			ServletOutputStream os = response.getOutputStream();
			Excel1 x = new Excel1();
			try {
				x.createXLS(os, headers, rows);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			os.flush();
			os.close();
		
	}


}
