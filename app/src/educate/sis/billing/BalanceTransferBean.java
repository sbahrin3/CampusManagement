package educate.sis.billing;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.billing.entity.InvoiceDetail;
import educate.sis.billing.entity.PaymentDetail;
import educate.sis.billing.entity.PaymentItem;
import educate.sis.billing.entity.StudentInvoice;
import educate.sis.billing.entity.StudentPayment;
import educate.sis.log.BillingLogBean;
import educate.sis.struct.entity.Session;

public class BalanceTransferBean {
	public void transferInvoiceAccount(String studentID,String studentID2,String itemId[],String userID)throws Exception{
		DbPersistence pm = new DbPersistence();
		LinkedHashSet<InvoiceDetail> invoiceDetailsTo = new LinkedHashSet<InvoiceDetail>();
		LinkedHashSet<InvoiceDetail> invoiceDetailsFrom = new LinkedHashSet<InvoiceDetail>();
		Student student2 = pm.find(Student.class,studentID2);
		Student student = pm.find(Student.class,studentID);
		for(String item : itemId){
			InvoiceDetail detail = pm.find(InvoiceDetail.class,item);
			if(detail != null){
				double amount = detail.getAmount();
				String desc = detail.getDescription();
				PaymentItem pItem = detail.getItem();
				InvoiceDetail newItem = new InvoiceDetail();
				newItem.setAmount(amount);
				if(detail.getInvoice() != null)
					newItem.setDescription(desc+"(ITEM TRANSFERED FROM "+student.getMatricNo()+")");
				else
					newItem.setDescription(desc+"(ITEM TRANSFERED FROM OTHER INVOICE)");
				newItem.setItem(pItem);
				newItem.setInvoice(detail.getInvoice());
				invoiceDetailsTo.add(newItem);
				
				newItem = new InvoiceDetail();
				newItem.setAmount(amount * -1);
				if(detail.getInvoice() != null)
					newItem.setDescription(desc+"(ITEM TRANSFERED TO "+student2.getMatricNo()+")");
				else
					newItem.setDescription(desc+"(ITEM TRANSFERED TO OTHER INVOICE)");
				newItem.setItem(pItem);
				invoiceDetailsFrom.add(newItem);
				
			}
		}
		if(!invoiceDetailsTo.isEmpty()){
			setInvoice(studentID,studentID2, userID, invoiceDetailsTo,invoiceDetailsFrom);
		}
		
	}
	public void transferPaymentAccount(String studentID,String studentID2,String itemId[],String userID)throws Exception{
		DbPersistence pm = new DbPersistence();
		LinkedHashSet<PaymentDetail> paymentDetailsTo = new LinkedHashSet<PaymentDetail>();
		LinkedHashSet<PaymentDetail> paymentDetailsFrom = new LinkedHashSet<PaymentDetail>();
		for(String item : itemId){
			PaymentDetail detail = pm.find(PaymentDetail.class,item);
			if(detail != null){
				double amount = detail.getAmount();
				String desc = detail.getDescription();
				PaymentDetail newDetail = new PaymentDetail();
				newDetail.setAmount(amount);
				newDetail.setDescription(desc+"(ITEM TRANSFERED FROM "+detail.getPayment().getReceiptNo()+")");
				newDetail.setItem(detail.getItem());
				paymentDetailsTo.add(newDetail);
				
				newDetail = new PaymentDetail();
				newDetail.setAmount(amount * -1);
				newDetail.setDescription(desc+"(ITEM TRANSFERED TO "+detail.getPayment().getReceiptNo()+")");
				newDetail.setItem(detail.getItem());
				paymentDetailsFrom.add(newDetail);
			}
		}
		if(!paymentDetailsTo.isEmpty()){
			setReceipt(studentID,studentID2, userID, paymentDetailsTo,paymentDetailsFrom);
		}
	}
	private void setReceipt(String studentID,String studentID2,String userId,LinkedHashSet<PaymentDetail> detail,LinkedHashSet<PaymentDetail> detailFrom)throws Exception{
		DbPersistence pm = new DbPersistence();
		pm.begin();
		Student student2 = pm.find(Student.class,studentID2);
		Student student = pm.find(Student.class,studentID);
		StudentPayment payment = new StudentPayment();
		payment.setReceiptNo(new ReceiptNoBean().genReceiptNo(pm,student.getProgram().getCode(), userId));
		payment.setPaymentDate(new Date());
		payment.setStudent(student2);
		payment.setPaymentsDetails(detail);
		pm.persist(payment);
		
		StudentPayment payment2 = new StudentPayment();
		payment2.setReceiptNo(payment.getReceiptNo());
		payment2.setPaymentDate(new Date());
		payment2.setStudent(student);
		payment2.setPaymentsDetails(detailFrom);
		payment2.setVoids("Y");
		pm.persist(payment2);
		pm.commit();
		
		new BillingLogBean().doLog(userId,payment.getReceiptNo(), BillingLogBean.PAYMENT_TYPE);
	}
	private void setInvoice(String studentID,String studentID2,String userId,LinkedHashSet<InvoiceDetail> detail,LinkedHashSet<InvoiceDetail> detailFrom)throws Exception{
		DbPersistence pm = new DbPersistence();
		pm.begin();
		Student student2 = pm.find(Student.class,studentID2);
		Student student = pm.find(Student.class,studentID);
		StudentInvoice invoice = new StudentInvoice();
		invoice.setInvoiceNo("");
		invoice.setInvoiceDate(new Date());
		invoice.setStudent(student2);
		invoice.setSession(getInvoiceSession(pm, student2.getProgram().getLevel().getPathNo()));
		invoice.setInvoiceDetails(detail);
		pm.persist(invoice);
		StudentInvoice invoice2 = new StudentInvoice();
		invoice2.setInvoiceNo(invoice.getInvoiceNo());
		invoice2.setInvoiceDate(new Date());
		invoice2.setStudent(student);
		invoice2.setSession(getInvoiceSession(pm, student.getProgram().getLevel().getPathNo()));
		invoice2.setInvoiceDetails(detailFrom);
		invoice2.setVoids("Y");
		pm.persist(invoice2);
		pm.commit();
		new BillingLogBean().doLog(userId,invoice.getInvoiceNo(), BillingLogBean.INVOICE_TYPE);
	}
	private Session getInvoiceSession(DbPersistence pm,int pathNo) throws Exception {
		Session session = null;
		Calendar cal = new GregorianCalendar();
		Date date = cal.getTime();		
	    Hashtable h = new Hashtable();
	    h.put("date", date);
	    List<Session> list = pm.list("select s from Session s where " +
	    		"(:date BETWEEN s.startDate AND s.endDate) AND s.pathNo="+pathNo, h);
		if ( list.size() == 0 ) return null;
		session = list.get(0);
		return session;
		
	}
}
