package educate.sis.billing;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import educate.enrollment.entity.Student;
import educate.sis.billing.entity.PaymentDetail;
import educate.sis.billing.entity.PaymentItem;
import educate.sis.billing.entity.StudentPayment;
import educate.sis.log.BillingLogBean;
import lebah.db.PersistenceManager;
import lebah.util.DateUtil;

public class StudentPaymentBean {
	private PersistenceManager pm;
	private StudentPayment payment;
	
	public Vector<StudentPayment> getStudentPayment(String id)throws Exception{
		pm = new PersistenceManager();
		List<StudentPayment> l = pm.list("SELECT a FROM StudentPayment a WHERE a.student.id='"+id+"'");
		Vector<StudentPayment> c = new Vector<StudentPayment>();
		c.addAll(l);
		return c;
	}
	public Vector<StudentPayment> getValidStudentPayment(String id)throws Exception{
		pm = new PersistenceManager();
		List<StudentPayment> l = pm.list("SELECT a FROM StudentPayment a WHERE a.student.id='"+id+"' AND a.voids IS NULL");
		Vector<StudentPayment> c = new Vector<StudentPayment>();
		c.addAll(l);
		return c;
	}
	public StudentPayment createPayment(String paymentId,StudentPayment apayment,String userId,String programCode,Set details)throws Exception{
		System.out.println("payment no "+paymentId);
		
		StudentPayment pay = getRecieptNo(paymentId);
		if(pay != null ){
			System.out.println("updating receipt");
			pm = new PersistenceManager();
			try{
				
				payment = getRecieptNo(paymentId);
				payment = (StudentPayment)pm.find(StudentPayment.class).whereId(pay.getId()).forUpdate();
				payment.setPaymentType(apayment.getPaymentType());
				payment.setPaymentNo(apayment.getPaymentNo());
				payment.setPaymentsDetails(details);
				pm.update();
				
			}
			catch(Exception e){
				pm.rollback();
			}
			
		}else{
			System.out.println("creating receipt");
			pm = new PersistenceManager();
			apayment.setPaymentsDetails(details);
			apayment.setReceiptNo(new ReceiptNoBean().genReceiptNo(programCode, userId));
			PersistenceManager.add(apayment);
			new BillingLogBean().doLog(userId, apayment.getReceiptNo(), BillingLogBean.PAYMENT_TYPE);
			return apayment;
		}
		return payment;
	}
	public StudentPayment getRecieptNo(String receiptNo)throws Exception{ 
		pm = new PersistenceManager();
		List<StudentPayment> l = pm.list("SELECT a FROM StudentPayment a WHERE a.receiptNo='"+receiptNo+"'");
		if(l.size()>0){
			return payment = l.get(0);
		}else{
			return null;
		}
	}
	
	public StudentPayment depostiPayment(double amount,String paymentType,String paymentNo,Student student,String userID)throws Exception{
		StudentPayment payment = new StudentPayment();
		payment.setStudent(student);
		payment.setPaymentDate(new DateUtil().getToday());
		payment.setPaymentType(paymentType);
		payment.setPaymentNo(paymentNo);
		PaymentDetail detail = new PaymentDetail();
		detail.setItem(new PaymentItemBean().getPaymentItem(PaymentItem.PAYMENT_CODE));
		detail.setAmount(amount);
		detail.setDescription(new PaymentItemBean().getPaymentItem(PaymentItem.PAYMENT_CODE).getDescription()+" "+student.getIntake().getName().toUpperCase());
		LinkedHashSet<PaymentDetail> details = new LinkedHashSet<PaymentDetail>();
		details.add(detail);
		payment =  createPayment("", payment, userID, student.getProgram().getCode(), details);
		System.out.println("receipt generated");
		return payment;
	}
	public void voidReceipt(String receipt_no, String user_id)throws Exception {
		pm = new PersistenceManager();
		payment = getRecieptNo(receipt_no);
		try{
			if(payment != null){
				payment = (StudentPayment)pm.find(StudentPayment.class).whereId(payment.getId()).forUpdate();
				payment.setVoids("Y");
				pm.update();
				new BillingLogBean().doLog(user_id, payment.getReceiptNo(), "VOID RECEIPT");
			}
		}
		catch(Exception e){
			System.err.println(e.getMessage());
			pm.rollback();
		}
		
	}
	public StudentPayment onlinePayment(String user_id,String program_code,String paymentType,String paymentNo,LinkedHashSet<PaymentDetail> details,Student student)throws Exception{
		System.out.println("online  payment receipt generation");
		pm = new PersistenceManager();
		StudentPayment payment = new StudentPayment();
		payment.setReceiptNo(new ReceiptNoBean().genReceiptNo(program_code, user_id));
		payment.setPaymentType(paymentType);
		payment.setPaymentNo(paymentNo);
		payment.setPaymentsDetails(details);
		payment.setStudent(student);
		payment.setPaymentDate(new DateUtil().getToday());
		PersistenceManager.add(payment);
		return payment;
	}
}
