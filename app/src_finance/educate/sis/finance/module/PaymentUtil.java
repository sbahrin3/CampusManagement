package educate.sis.finance.module;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.finance.entity.Invoice;
import educate.sis.finance.entity.InvoiceItem;
import educate.sis.finance.entity.Payment;
import educate.sis.finance.entity.PaymentItem;
import educate.sis.finance.entity.PaymentSchedule;
import educate.sis.finance.entity.PaymentScheduleItem;
import educate.sis.finance.entity.SponsorPayment;
import educate.sis.module.StudentStatusUtil;
import educate.sis.struct.BillingNumGenerator;
import educate.sis.struct.entity.Session;

public class PaymentUtil {
	
	DbPersistence db = new DbPersistence();
	
	public PaymentUtil() {
		
	} 
	
	public Payment createPayment(Student student, CPaymentItem i, SponsorPayment sponsorPayment) throws Exception {
		return createPayment(student, i, sponsorPayment, null);
	}
	
	public Payment createPayment(Student student, CPaymentItem i, SponsorPayment sponsorPayment, Date paymentDate) throws Exception {

		BillingNumGenerator g = new BillingNumGenerator();
		String paymentNo = g.generateNumber("RB");
		
		Payment payment = new Payment();
		payment.setSponsorPayment(sponsorPayment);
		payment.setStudent(student);
		payment.setPaymentNo(paymentNo);
		if ( paymentDate == null ) {
			payment.setCreateDate(new Date());
			payment.setCreateTime(new Date());
		}
		else {
			payment.setCreateDate(paymentDate);
			payment.setCreateTime(paymentDate);
		}
		PaymentItem pi = new PaymentItem();
		pi.setAmount(i.getPaymentAmount());
		pi.setDescription(i.getDescription());
		payment.addPaymentItems(pi);
		db.begin();
		db.persist(payment);
		db.commit();
		return payment;
	}
	
	
	
	public Payment createPayment(Student student, List<CPaymentItem> list) throws Exception {
		return createPayment(student, list, false);
	}
	
	public Payment createPayment(Student student, List<CPaymentItem> list, Date paymentDate) throws Exception {
		return createPayment(student, list, false, paymentDate);
	}	
	
	public void createCreditNote(Student student, List<CPaymentItem> list) throws Exception {
		createPayment(student, list, true);
	}
	
	public void createCreditNote(Student student, List<CPaymentItem> list, Date date) throws Exception {
		createPayment(student, list, true, date);
	}	
	
	public Payment createPayment(Student student, List<CPaymentItem> list, boolean isCreditNote) throws Exception {
		return createPayment(student, list, isCreditNote, null);
	}
	
	public Payment createPayment(Student student, List<CPaymentItem> paymentItems, boolean isCreditNote, Date paymentDate) throws Exception {

		BillingNumGenerator g = new BillingNumGenerator();
		String paymentNo = g.generateNumber(isCreditNote ? "CN" : "RB");
		
		Payment payment = new Payment();
		payment.setStudent(student);
		payment.setPaymentNo(paymentNo);
		if ( paymentDate == null ) {
			payment.setCreateDate(new Date());
			payment.setCreateTime(new Date());
		} else {
			payment.setCreateDate(paymentDate);
			payment.setCreateTime(paymentDate);
		}
		payment.setCreditNote(isCreditNote);
		for ( CPaymentItem i : paymentItems ) {
			PaymentItem pi = new PaymentItem();
			pi.setAmount(i.getPaymentAmount());
			pi.setDescription(i.getDescription());
			pi.setCreditNote(isCreditNote);
			pi.setFlexi(i.getFlexi());
			pi.setPtptn(i.getPtptn());
			pi.setResourceFee(i.getResourceFee());
			payment.addPaymentItems(pi);
		}
		
		db.begin();
		db.persist(payment);
		db.commit();
		
		updatePaymentScheduleItem(payment);
		
		return payment;
		
	}
	
	
	
	
	public void updatePaymentScheduleItem(Payment payment) throws Exception {
		Student student = payment.getStudent();
		PaymentSchedule paymentSchedule = (PaymentSchedule) db.get("select p from PaymentSchedule p where p.student.id = '" + student.getId() + "'");
		if ( paymentSchedule != null ) {
			List<PaymentScheduleItem> scheduleItems = paymentSchedule.getScheduleItems();
			if ( scheduleItems.size() > 0 ) {
				if ( paymentSchedule != null ) {
					Date paymentDate = payment.getCreateDate();
					Set<PaymentItem> paymentItems = payment.getPaymentItems();
					for ( PaymentItem paymentItem : paymentItems ) {
						if ( paymentItem.getFlexi() ) {
							
							PaymentScheduleItem scheduleItem = null;
							scheduleItem = getPaymentScheduleItemFlexi(scheduleItems, paymentDate);
							if ( scheduleItem != null ) {
								db.begin();
								double amountPaid = scheduleItem.getAmountPaid() + paymentItem.getAmount();
								scheduleItem.setAmountPaid(amountPaid);
								paymentItem.setScheduleItem(scheduleItem);
								db.commit();
							}
						}
						else if ( paymentItem.getPtptn() ) {
							PaymentScheduleItem scheduleItem = null;
							scheduleItem = getPaymentScheduleItemPtptn(scheduleItems, paymentDate);
							if ( scheduleItem != null ) {
								db.begin();
								double amountPaid = scheduleItem.getAmountPaid() + paymentItem.getAmount();
								scheduleItem.setAmountPaid(amountPaid);
								paymentItem.setScheduleItem(scheduleItem);
								db.commit();
							}
						}
						else if ( paymentItem.getResourceFee() ) {
							PaymentScheduleItem scheduleItem = null;
							scheduleItem = getPaymentScheduleItemResourceFee(scheduleItems, paymentDate);
							if ( scheduleItem != null ) {
								db.begin();
								double amountPaid = scheduleItem.getAmountPaid() + paymentItem.getAmount();
								scheduleItem.setAmountPaid(amountPaid);
								paymentItem.setScheduleItem(scheduleItem);
								db.commit();
							}
						}
					}
					//recalculate the whole payment schedule
					calculatePaymentSchedules(db, paymentSchedule);
				}
			}
		}
	}
	
	
	public Payment updatePayment(Student student, List<CPaymentItem> paymentItems, Date paymentDate) throws Exception {

		BillingNumGenerator g = new BillingNumGenerator();
		String paymentNo = g.generateNumber("RB");
		
		Payment payment = new Payment();
		payment.setStudent(student);
		payment.setPaymentNo(paymentNo);
		if ( paymentDate == null ) {
			payment.setCreateDate(new Date());
			payment.setCreateTime(new Date());
		} else {
			payment.setCreateDate(paymentDate);
			payment.setCreateTime(paymentDate);
		}
		payment.setCreditNote(false);
		for ( CPaymentItem i : paymentItems ) {
			PaymentItem pi = new PaymentItem();
			pi.setAmount(i.getPaymentAmount());
			pi.setDescription(i.getDescription());
			pi.setCreditNote(false);
			pi.setFlexi(i.getFlexi());
			pi.setPtptn(i.getPtptn());
			pi.setResourceFee(i.getResourceFee());
			payment.addPaymentItems(pi);
		}
		
		db.begin();
		db.persist(payment);
		db.commit();
		
		return payment;
		
	}
	
	
	public Payment updatePayment(Student student, List<CPaymentItem> paymentItems, Date paymentDate, String paymentMode, String refNo, String remark) throws Exception {

		BillingNumGenerator g = new BillingNumGenerator();
		String paymentNo = g.generateNumber("RB");
		
		Payment payment = new Payment();
		payment.setStudent(student);
		payment.setPaymentNo(paymentNo);
		payment.setPaymentMode(paymentMode);
		payment.setCheckNo(refNo);
		payment.setRemark(remark);
		if ( paymentDate == null ) {
			payment.setCreateDate(new Date());
			payment.setCreateTime(new Date());
		} else {
			payment.setCreateDate(paymentDate);
			payment.setCreateTime(paymentDate);
		}
		payment.setCreditNote(false);
		for ( CPaymentItem i : paymentItems ) {
			PaymentItem pi = new PaymentItem();
			pi.setAmount(i.getPaymentAmount());
			pi.setDescription(i.getDescription());
			pi.setCreditNote(false);
			pi.setFlexi(i.getFlexi());
			pi.setPtptn(i.getPtptn());
			pi.setResourceFee(i.getResourceFee());
			payment.addPaymentItems(pi);
		}
		
		db.begin();
		db.persist(payment);
		db.commit();
		
		return payment;
		
	}
	
	private PaymentScheduleItem getPaymentScheduleItemFlexi(List<PaymentScheduleItem> scheduleItems, Date paymentDate) throws Exception {
		PaymentScheduleItem item = null;
		for ( PaymentScheduleItem scheduleItem : scheduleItems ) {
			if ( scheduleItem.getActualAmountDue() > 0 && scheduleItem.getType() == 0 ) {
				item = scheduleItem;
				break;
			}
		}
		return item;
	}
	
	private PaymentScheduleItem getPaymentScheduleItemPtptn(List<PaymentScheduleItem> scheduleItems, Date paymentDate) throws Exception {
		PaymentScheduleItem item = null;
		for ( PaymentScheduleItem scheduleItem : scheduleItems ) {
			if ( scheduleItem.getActualAmountDue() > 0 && scheduleItem.getType() == 1 ) {
				item = scheduleItem;
				break;
			}
		}
		return item;
	}
	
	private PaymentScheduleItem getPaymentScheduleItemResourceFee(List<PaymentScheduleItem> scheduleItems, Date paymentDate) throws Exception {
		PaymentScheduleItem item = null;
		for ( PaymentScheduleItem scheduleItem : scheduleItems ) {
			if ( scheduleItem.getActualAmountDue() > 0 && scheduleItem.getType() == 2 ) {
				item = scheduleItem;
				break;
			}
		}
		return item;
	}
	
	public static void calculatePaymentSchedules(DbPersistence db, PaymentSchedule paymentSchedule) throws Exception {
		double totalAmount = paymentSchedule.getTotalAmount();
		Date startPaymentDate = paymentSchedule.getStartPaymentDate();
		Calendar c = Calendar.getInstance();
		c.setTime(startPaymentDate);
		Date paymentDate = startPaymentDate;
		int dayNo = c.get(Calendar.DATE);
		
		List<PaymentScheduleItem> scheduleItems = paymentSchedule.getScheduleItems();
		
		double sumBalanceDue = 0.0d;
		double totalAmountDue = 0.0d;
		boolean finished = false;
		double previousBalance = 0.0d;
		double previousOutstandingBalance = paymentSchedule.getTotalAmount();
		int n = 0;
		
		double monthlyAmount = 0.0d;
		double cummulativeAmount = 0.0d;
		db.begin();
		for ( PaymentScheduleItem item : scheduleItems ) {
			
			monthlyAmount = item.getAmountDue();
			cummulativeAmount += monthlyAmount;
			
			sumBalanceDue += monthlyAmount;
			
			if ( sumBalanceDue > totalAmount ) {
				sumBalanceDue = sumBalanceDue - monthlyAmount;
				monthlyAmount = totalAmount - sumBalanceDue;
			}
			
			double amountPaid = item.getAmountPaid();
			double currentBalance = previousBalance + monthlyAmount - amountPaid;
			previousBalance = currentBalance;
			
			double outstandingBalance = previousOutstandingBalance - amountPaid;
			previousOutstandingBalance = outstandingBalance;
				
			item.setCumulativeAmountDue(cummulativeAmount);
			item.setCurrentBalance(currentBalance);
			item.setOutstandingBalance(outstandingBalance);
						
			if ( currentBalance > 0 && currentBalance < monthlyAmount ) {
				item.setActualAmountDue(currentBalance);
			}
			else if ( currentBalance <= 0 ) {
				item.setActualAmountDue(0.0d);
			}
			else {
				item.setActualAmountDue(monthlyAmount);
			}
			
			totalAmountDue += monthlyAmount;


			n++;
		}
		db.commit();
	}
	
	public static void calculatePaymentFlexiSchedules(DbPersistence db, PaymentSchedule paymentSchedule) throws Exception {
		double totalAmount = paymentSchedule.getTotalFlexiAmount();
		Date startPaymentDate = paymentSchedule.getStartPaymentDate();
		Calendar c = Calendar.getInstance();
		c.setTime(startPaymentDate);
		Date paymentDate = startPaymentDate;
		int dayNo = c.get(Calendar.DATE);
		
		List<PaymentScheduleItem> scheduleItems = paymentSchedule.getScheduleItems();
		
		double sumBalanceDue = 0.0d;
		double totalAmountDue = 0.0d;
		boolean finished = false;
		double previousBalance = 0.0d;
		double previousOutstandingBalance = paymentSchedule.getTotalFlexiAmount();
		int n = 0;
		
		double monthlyAmount = 0.0d;
		double cummulativeAmount = 0.0d;
		
		for ( PaymentScheduleItem item : scheduleItems ) {
			
			if ( item.getType() == 0 ) {
				db.begin();
				monthlyAmount = item.getAmountDue();
				cummulativeAmount += monthlyAmount;
				
				sumBalanceDue += monthlyAmount;
				
				if ( sumBalanceDue > totalAmount ) {
					sumBalanceDue = sumBalanceDue - monthlyAmount;
					monthlyAmount = totalAmount - sumBalanceDue;
				}
				
				double amountPaid = item.getAmountPaid();
				double currentBalance = previousBalance + monthlyAmount - amountPaid;
				previousBalance = currentBalance;
				
				double outstandingBalance = previousOutstandingBalance - amountPaid;
				previousOutstandingBalance = outstandingBalance;
					
				item.setCumulativeAmountDueFlexi(cummulativeAmount);
				item.setCurrentBalanceFlexi(currentBalance);
				item.setOutstandingBalanceFlexi(outstandingBalance);
				if ( currentBalance > 0 && currentBalance < monthlyAmount ) {
					item.setActualAmountDueFlexi(currentBalance);
				}
				else if ( currentBalance <= 0 ) {
					item.setActualAmountDueFlexi(0.0d);
				}
				else {
					item.setActualAmountDueFlexi(monthlyAmount);
				}

				
				totalAmountDue += monthlyAmount;
				n++;
				db.commit();
			}
			
		}
	}
	
	public static void calculatePaymentPtpnSchedules(DbPersistence db, PaymentSchedule paymentSchedule) throws Exception {
		double totalAmount = paymentSchedule.getTotalPtptnAmount();
		Date startPaymentDate = paymentSchedule.getStartPaymentDate();
		Calendar c = Calendar.getInstance();
		c.setTime(startPaymentDate);
		Date paymentDate = startPaymentDate;
		int dayNo = c.get(Calendar.DATE);
		
		List<PaymentScheduleItem> scheduleItems = paymentSchedule.getScheduleItems();
		
		double sumBalanceDue = 0.0d;
		double totalAmountDue = 0.0d;
		boolean finished = false;
		double previousBalance = 0.0d;
		double previousOutstandingBalance = paymentSchedule.getTotalPtptnAmount();
		int n = 0;
		
		double monthlyAmount = 0.0d;
		double cummulativeAmount = 0.0d;
		
		for ( PaymentScheduleItem item : scheduleItems ) {
			
			if ( item.getType() == 1 ) {
				db.begin();
				monthlyAmount = item.getAmountDue();
				cummulativeAmount += monthlyAmount;
				
				sumBalanceDue += monthlyAmount;
				
				if ( sumBalanceDue > totalAmount ) {
					sumBalanceDue = sumBalanceDue - monthlyAmount;
					monthlyAmount = totalAmount - sumBalanceDue;
				}
				
				double amountPaid = item.getAmountPaid();
				double currentBalance = previousBalance + monthlyAmount - amountPaid;
				previousBalance = currentBalance;
				
				double outstandingBalance = previousOutstandingBalance - amountPaid;
				previousOutstandingBalance = outstandingBalance;
					
				item.setCumulativeAmountDuePtptn(cummulativeAmount);
				item.setCurrentBalancePtptn(currentBalance);
				item.setOutstandingBalancePtptn(outstandingBalance);
				if ( currentBalance > 0 && currentBalance < monthlyAmount ) {
					item.setActualAmountDuePtptn(currentBalance);
				}
				else if ( currentBalance <= 0 ) {
					item.setActualAmountDuePtptn(0.0d);
				}
				else {
					item.setActualAmountDuePtptn(monthlyAmount);
				}

				
				totalAmountDue += monthlyAmount;
	
	
				n++;
				db.commit();
			}
			
		}
	}	
	
	public static List<XPaymentScheduleItem> adjustedPaymentScheduleItems(PaymentSchedule paymentSchedule) throws Exception {
		
		List<XPaymentScheduleItem> adjustedItems = new ArrayList<XPaymentScheduleItem>();
		List<PaymentScheduleItem> scheduleItems = paymentSchedule.getScheduleItems();
		
		double totalAmount = paymentSchedule.getTotalAmount();
		
		double cumulativeAmountDue = 0.0d;
		double outstanding = totalAmount;
		double currentBalance = 0.0d;
		
		System.out.println("scheduleItems = " + scheduleItems.size());
		for ( PaymentScheduleItem i : scheduleItems ) {
			currentBalance = i.getAmountDue() - i.getAmountPaid();
			
			if ( i.getCumulativeAmountDue() < totalAmount ) {
				
				outstanding = outstanding - i.getAmountPaid();
				
				XPaymentScheduleItem xi = new XPaymentScheduleItem(i);
				xi.setOutstandingBalance(outstanding);
				xi.setCurrentBalance(currentBalance);
				adjustedItems.add(xi);
				cumulativeAmountDue = i.getCumulativeAmountDue();
				
			} else {
				
				double amountDue = totalAmount - cumulativeAmountDue;
				cumulativeAmountDue += amountDue;
				
				outstanding = outstanding - i.getAmountPaid();
				
				XPaymentScheduleItem xi = new XPaymentScheduleItem(i);
				xi.setAmountDue(amountDue);
				xi.setCumulativeAmountDue(cumulativeAmountDue);
				xi.setOutstandingBalance(outstanding);
				xi.setCurrentBalance(currentBalance);
				
				adjustedItems.add(xi);
				
				//break;
			}
		}
		
		return adjustedItems;
	}
	
	public static void createInvoicesOnPaymentSchedule(DbPersistence db, PaymentSchedule paymentSchedule) throws Exception {
		createInvoicesOnPaymentSchedule(db, paymentSchedule, null);
	}
	
	public static void createInvoicesOnPaymentSchedule(DbPersistence db, PaymentSchedule paymentSchedule, Date toDate) throws Exception {
		
		/*
		Calendar cDate = Calendar.getInstance();
		Calendar ctoDate = Calendar.getInstance();
		ctoDate.setTime(toDate);
		ctoDate.add(Calendar.DATE, 1);
		*/
		
		Student student = paymentSchedule.getStudent();
		int pathNo = student.getProgram().getLevel().getPathNo();
		
		StudentStatusUtil su = new StudentStatusUtil();
		InvoiceUtil iu = new InvoiceUtil();
		List<PaymentScheduleItem> scheduleItems = paymentSchedule.getScheduleItems();
		for ( PaymentScheduleItem scheduleItem : scheduleItems ) {
			Date dueDate = scheduleItem.getPaymentDate();
			
			
			
			double amountDue = scheduleItem.getActualAmountDue();
			Calendar c = Calendar.getInstance();
			c.setTime(dueDate);
			c.add(Calendar.DATE, -30);
			Date invoiceDate = c.getTime();
		
			List<InvoiceItem> items = new ArrayList<InvoiceItem>();
			InvoiceItem item = new InvoiceItem();
			item.setDescription("MONTHLY FLEXI PAYMENT");
			item.setAmount(amountDue);
			items.add(item);
			
			Session invoiceSession = su.getSessionByDate(invoiceDate, pathNo);
			iu.createFlexiPaymentInvoice(student, items, invoiceSession, invoiceDate, scheduleItem);
		}
		
		db.begin();
		paymentSchedule.setInvoiceCreated(true);
		db.commit();
		
	}
	
	public static void createInvoicesOnPaymentScheduleItem(DbPersistence db, PaymentScheduleItem paymentScheduleItem) throws Exception {
		createInvoicesOnPaymentScheduleItem(db, paymentScheduleItem, null);
	}
	
	public static void createInvoicesOnPaymentScheduleItem(DbPersistence db, PaymentScheduleItem paymentScheduleItem, String flag) throws Exception {
		
		Student student = paymentScheduleItem.getPaymentSchedule().getStudent();
		int pathNo = student.getProgram().getLevel().getPathNo();
		
		StudentStatusUtil su = new StudentStatusUtil();
		InvoiceUtil iu = new InvoiceUtil();
		
		
		Date dueDate = paymentScheduleItem.getPaymentDate();
		double amountDue = paymentScheduleItem.getActualAmountDue();
		Calendar c = Calendar.getInstance();
		c.setTime(dueDate);
		c.add(Calendar.DATE, -30);
		Date invoiceDate = c.getTime();
	
		List<InvoiceItem> items = new ArrayList<InvoiceItem>();
		InvoiceItem item = new InvoiceItem();
		item.setDescription("MONTHLY FLEXI PAYMENT" + (flag != null ?  "(" + flag + ")" : ""));
		item.setAmount(amountDue);
		items.add(item);
		
		Session invoiceSession = su.getSessionByDate(invoiceDate, pathNo);
		iu.createFlexiPaymentInvoice(student, items, invoiceSession, invoiceDate, paymentScheduleItem);
		
	}
	
	
	public static void deleteInvoicesOnPaymentScheduleItem(DbPersistence db, PaymentScheduleItem paymentScheduleItem) throws Exception {
		
		Student student = paymentScheduleItem.getPaymentSchedule().getStudent();
		
		Invoice invoice = (Invoice) db.get("select i from Invoice i where i.scheduleItemId = '" + paymentScheduleItem.getId() + "'");
		if ( invoice != null ) {
			db.begin();
			db.remove(invoice);
			db.commit();
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		
		String id = "1433904986035";
		DbPersistence db = new DbPersistence();
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, id);
		System.out.println("Payment Schedule for " + paymentSchedule.getStudent().getBiodata().getName());
		createInvoicesOnPaymentSchedule(db, paymentSchedule);
		
		
	}
	
	

}
