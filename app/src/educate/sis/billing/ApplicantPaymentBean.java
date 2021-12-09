package educate.sis.billing;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

//import com.sun.tools.xjc.reader.dtd.bindinfo.BIInterface;
import educate.admission.entity.Applicant;
import educate.sis.billing.entity.ApplicantPayment;
import educate.sis.log.BillingLogBean;
import lebah.db.PersistenceManager;
import lebah.util.DateUtil;
import lebah.util.Util;

public class ApplicantPaymentBean {
	private PersistenceManager pm;
	
	public ApplicantPayment create(Applicant applicant,double amount,String paymentType,String referenceNo,String userId,String description)throws Exception{
		pm = new PersistenceManager();
		Calendar cal = new GregorianCalendar();
		Date time = cal.getTime();
		ApplicantPayment pay = new ApplicantPayment();
		pay.setDate(new DateUtil().getToday());
		pay.setTime(time);
		pay.setApplicant(applicant);
		pay.setAmount(amount);
		pay.setReferenceNo(referenceNo);
		pay.setPaymentType(paymentType);
		pay.setDescription(description);
		pay.setReceiptNo(new ReceiptNoBean().genReceiptNo("APP", userId));
		PersistenceManager.add(pay);
		new BillingLogBean().doLog(userId, pay.getReceiptNo(),"APPLICATION FEE");
		return pay;
	}
	public Hashtable getApplicantReceipt(String applicant_id,String receiptNo) throws Exception{
		Hashtable info = new Hashtable();
		Vector v = new Vector();
		pm = new PersistenceManager();
		List<ApplicantPayment> l = pm.list("SELECT a FROM ApplicantPayment a WHERE a.receiptNo='"+receiptNo+"' AND a.applicant.id='"+applicant_id+"'");
		int cnt = 0;
		double totalPaid = 0;
		for(int i =0 ;i<l.size();i++){
			ApplicantPayment pay = l.get(i);
			Hashtable h = new Hashtable(); 
			h.put("item", Integer.toString(++cnt));
			h.put("code", "");
			h.put("description", pay.getDescription());
			h.put("amount", Util.formatDecimal(pay.getAmount()));
			totalPaid = totalPaid+ pay.getAmount();
			v.addElement(h);
		}
		info.put("itemDate",new DateUtil().toString(l.get(0).getDate()));
		info.put("paymentMode", l.get(0).getPaymentType());
		info.put("detail", v); 
		info.put("total", Util.formatDecimal(totalPaid));
		info.put("words", Util.formatDecimal(totalPaid));
		String currency = "Ringgit Malaysia";
        info.put("amount_total_words", new NumberWordsFormat().convert(Double.toString(totalPaid), currency));
		return info;
	}
}
