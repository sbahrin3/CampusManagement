/**
 * 
 */
package educate.sis.finance.module; 

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.DebitNoteSchedule;
import educate.sis.finance.entity.DebitNoteScheduleItem;
import educate.sis.finance.entity.HostelPaymentSchedule;
import educate.sis.finance.entity.HostelPaymentScheduleItem;
import educate.sis.finance.entity.Payment;
import educate.sis.finance.entity.PaymentSchedule;
import educate.sis.finance.entity.PaymentScheduleItem;
import educate.sis.finance.entity.PaymentScheduleItem2;
import educate.sis.finance.entity.PtptnPaymentSchedule;
import educate.sis.finance.entity.PtptnPaymentScheduleItem;
import educate.sis.finance.entity.ResourceFeePaymentSchedule;
import educate.sis.finance.entity.ResourceFeePaymentScheduleItem;
import educate.sis.finance.entity.SemesterPaymentSchedule;
import educate.sis.finance.entity.SemesterPaymentScheduleItem;
import educate.sis.module.StudentStatusUtil;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class StudentPaymentScheduleModule extends LebahModule {
	
	private String path = "apps/finance/studentPaymentSchedule";
	private DbPersistence db = new DbPersistence();

	/* (non-Javadoc)
	 * @see lebah.portal.action.LebahModule#start()
	 */
	@Override
	public String start() {
		return path + "/start.vm";
	}
	
	public void preProcess() { 
		System.out.println(command);
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("numFormat", new DecimalFormat("#.00"));
		
		String selectedTab = getParam("selectedTab");
		if ( selectedTab.equals("") ) selectedTab = "1";
		context.put("selectedTab", selectedTab);
	}
	
	@Command("getPaymentSchedule")
	public String getPaymentSchedule() throws Exception {
		
		String matricNo = getParam("matric_no");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		context.put("studentStatus", studentStatus);
			
		System.out.println("GET PAYMENT SCHEDULE FOR: " + student.getBiodata().getName());
		
		PaymentSchedule paymentSchedule = (PaymentSchedule) db.get("select p from PaymentSchedule p where p.student.id = '" + student.getId() + "'");
		System.out.println(paymentSchedule);
		if ( paymentSchedule == null ) {
			return path + "/createPaymentSchedule.vm";
		}
		
		System.out.println("checkTotalAmount");
		checkTotalAmount(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);

		
		getPtptnPaymentSchedule(paymentSchedule);
		getResourceFeePaymentSchedule(paymentSchedule);
		getSemesterPaymentSchedule(paymentSchedule);
		getHostelPaymentSchedule(paymentSchedule);
		getDebitNoteSchedule(paymentSchedule);
	
		
		List<XPaymentScheduleItem> items = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		System.out.println("items = " + items.size());
		context.put("scheduleItems", items);
				
		return path + "/getPaymentSchedule.vm";
	}
	


	private void getDebitNoteSchedule(PaymentSchedule paymentSchedule) throws Exception {

		DebitNoteSchedule debitNoteSchedule = (DebitNoteSchedule) db.get("select p from DebitNoteSchedule p where p.student.id = '" + paymentSchedule.getStudent().getId() + "'");
		if ( debitNoteSchedule == null ) {
			db.begin();
			debitNoteSchedule = new DebitNoteSchedule();
			debitNoteSchedule.setStudent(paymentSchedule.getStudent());
			
			List<DebitNoteScheduleItem> items = new ArrayList<DebitNoteScheduleItem>();
			debitNoteSchedule.setItems(items);
			db.persist(debitNoteSchedule);
			db.commit();
		}
		context.put("debitNoteSchedule", debitNoteSchedule);
		List<DebitNoteScheduleItem> debitNoteScheduleItems = debitNoteSchedule.getItems();
		context.put("debitNoteScheduleItems", debitNoteScheduleItems);
		
	}

	@Command("listPaymentSchedules")
	public String listPaymentSchedules() throws Exception {
		
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		
		context.put("student", student);
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		context.put("studentStatus", studentStatus);
		
		PaymentSchedule paymentSchedule = (PaymentSchedule) db.get("select p from PaymentSchedule p where p.student.id = '" + student.getId() + "'");
		if ( paymentSchedule == null ) {
			return path + "/createPaymentSchedule.vm";
		}
		
		context.put("paymentSchedule", paymentSchedule);

		
		getPtptnPaymentSchedule(paymentSchedule);
		getResourceFeePaymentSchedule(paymentSchedule);
		
		List<XPaymentScheduleItem> items = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		context.put("scheduleItems", items);
		return path + "/getPaymentSchedule.vm";
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
			
			List<SemesterPaymentScheduleItem> scheduleItems = new ArrayList<SemesterPaymentScheduleItem>();
			semesterPaymentSchedule.setItems(scheduleItems);
			
			db.persist(semesterPaymentSchedule);
			db.commit();
		}
		context.put("semesterPaymentSchedule", semesterPaymentSchedule);
		
		/*
		List<PaymentScheduleItem> paymentScheduleItems = paymentSchedule.getScheduleItems();
		if ( semesterPaymentSchedule.getItems().size() != paymentScheduleItems.size() ) {
			System.out.println("semester payment schedule is not tally");
			if ( semesterPaymentSchedule.getItems().size() == 0 ) {
				for ( PaymentScheduleItem i : paymentScheduleItems ) {
					
					if ( i.getType() == 3 ) {
						
						db.begin();
						SemesterPaymentScheduleItem item = new SemesterPaymentScheduleItem();
						item.setPaymentSchedule(semesterPaymentSchedule);
						item.setAmountDue(i.getAmountDue());
						item.setDateDue(i.getPaymentDate());
						semesterPaymentSchedule.getItems().add(item);
						
						i.setPtptnItemId(item.getId());
						
						db.commit();

					}
					
				}
			} else {
				
				List<SemesterPaymentScheduleItem> currentItems = new ArrayList<SemesterPaymentScheduleItem>();
				currentItems.addAll(semesterPaymentSchedule.getItems());
				
				for ( SemesterPaymentScheduleItem i : currentItems ) {
					db.begin();
					semesterPaymentSchedule.getItems().remove(i);
					db.remove(i);
					db.commit();
				}
				
				for ( PaymentScheduleItem i : paymentScheduleItems ) {
					
					if ( i.getType() == 3 ) {
						
						db.begin();
						SemesterPaymentScheduleItem item = new SemesterPaymentScheduleItem();
						item.setPaymentSchedule(semesterPaymentSchedule);
						item.setAmountDue(i.getAmountDue());
						item.setDateDue(i.getPaymentDate());
						semesterPaymentSchedule.getItems().add(item);
						
						i.setPtptnItemId(item.getId());
						
						db.commit();
					
			
					}
					
				}
				
			}
			
		}
		*/
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
	
	@Command("createPaymentSchedule")
	public String createPaymentSchedule() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		List<PaymentScheduleItem> scheduleItems = createScheduleItems(null);
		context.put("items", scheduleItems);
		return path + "/generatePaymentSchedule.vm";
		
	}
	
	
	@Command("createHostelPaymentSchedule")
	public String createHostelPaymentSchedule() throws Exception {
		String hostelPaymentScheduleId = getParam("hostelPaymentScheduleId");
		HostelPaymentSchedule hostelPaymentSchedule = db.find(HostelPaymentSchedule.class, hostelPaymentScheduleId);
		context.put("hostelPaymentSchedule", hostelPaymentSchedule);
		context.put("student", hostelPaymentSchedule.getStudent());
		return path + "/createHostelPaymentSchedule.vm";
	}
	
	
	@Command("saveHostelPaymentSchedule")
	public String saveHostelPaymentSchedule() throws Exception {
		String hostelPaymentScheduleId = getParam("hostelPaymentScheduleId");
		HostelPaymentSchedule hostelPaymentSchedule = db.find(HostelPaymentSchedule.class, hostelPaymentScheduleId);
		
		db.begin();
		
		List<HostelPaymentScheduleItem> scheduleItems = createHostelScheduleItems(hostelPaymentSchedule);
		hostelPaymentSchedule.setItems(scheduleItems);
		
		db.commit();
		
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		
		for ( HostelPaymentScheduleItem item : scheduleItems ) {
		db.begin();
			PaymentScheduleItem paymentScheduleItem = new PaymentScheduleItem();
			paymentScheduleItem.setPaymentSchedule(paymentSchedule);
			paymentScheduleItem.setType(4);
			paymentScheduleItem.setPtptnItemId(item.getId());
			paymentScheduleItem.setPaymentDate(item.getDateDue());
			paymentScheduleItem.setAmountDue(item.getAmountDue());
			paymentSchedule.getItems().add(paymentScheduleItem);
			db.commit();
		}
		
		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);

		//PaymentUtil.createInvoicesOnPaymentScheduleItem(db, paymentScheduleItem, "SEMESTER FEE");

		context.put("hostelPaymentSchedule", hostelPaymentSchedule);
		List<HostelPaymentScheduleItem> hostelScheduleItems = hostelPaymentSchedule.getItems();
		context.put("hostelItems", hostelScheduleItems);
		
		List<XPaymentScheduleItem> adjustedScheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", adjustedScheduleItems);
		
		return path + "/saveHostelPaymentSchedule.vm";
	}
	
	private List<HostelPaymentScheduleItem> createHostelScheduleItems(HostelPaymentSchedule paymentSchedule) throws Exception {
		List<HostelPaymentScheduleItem> scheduleItems = new ArrayList<HostelPaymentScheduleItem>();
		
		double amountDue = Double.parseDouble(getParam("hostelAmountDue"));
		int totalMonths = Integer.parseInt(getParam("totalMonths"));
		totalMonths++;
		int monthlySpan = Integer.parseInt(getParam("monthlySpan"));
		Date startPaymentDate = new SimpleDateFormat("dd-MM-yyyy").parse(getParam("startPaymentDate"));
		Calendar c = Calendar.getInstance();
		c.setTime(startPaymentDate);
		Date paymentDate = startPaymentDate;
		
		for ( int n = 0; n < totalMonths - 1; n++ ) {

			HostelPaymentScheduleItem item = new HostelPaymentScheduleItem();
			item.setPaymentSchedule(paymentSchedule);
			item.setDateDue(paymentDate);
			item.setAmountDue(amountDue);
			
			Calendar c2 = Calendar.getInstance();
			c2.setTime(paymentDate);
			c2.add(Calendar.MONTH, monthlySpan);
			paymentDate = c2.getTime();
			
			System.out.println(item.getDateDue());
			
			scheduleItems.add(item);
		}
		
		return scheduleItems;
	}
	
	@Command("savePaymentSchedule")
	public String savePaymentSchedule() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		context.put("studentStatus", studentStatus);
		
		
		boolean payMonth = "1".equals(getParam("payMonth"));
		boolean paySemester = "1".equals(getParam("paySemester"));

		
		String _tuitionFees = getParam("tuitionFees");
		double tuitionFees = Double.parseDouble(_tuitionFees);
		
		String _rebateFees = getParam("rebateFees");
		if ( "".equals(_rebateFees) ) _rebateFees = "0";
		double rebateFees = Double.parseDouble(_rebateFees);
		
		String _ptptnAmount = getParam("ptptnAmount");
		if ( "".equals(_ptptnAmount) ) _ptptnAmount = "0";
		double ptptnAmount = Double.parseDouble(_ptptnAmount);
				
		double totalAmountAll = tuitionFees - rebateFees;
		double totalFlexiAmount = totalAmountAll - ptptnAmount;
		
		String _startPaymentDate = getParam("startPaymentDate");
		Date startPaymentDate = new SimpleDateFormat("dd-MM-yyyy").parse(_startPaymentDate);
		
		int totalMonths = 0;
		
		String _monthlyAmount = getParam("monthlyAmount");
		double monthlyAmount = 0.0d;
		
		//to calculate monthly - based on TOTAL FLEXI AMOUN
		
		if ( payMonth ) {
			if ( !"".equals(_monthlyAmount)) {
				monthlyAmount = Double.parseDouble(_monthlyAmount);
				totalMonths = ((int) (totalFlexiAmount/monthlyAmount)) + 5; //buffer
			} else {
				String _numberOfMonths = getParam("numberOfMonths");
				totalMonths = Integer.parseInt(_numberOfMonths);
				monthlyAmount = totalFlexiAmount/totalMonths;
				totalMonths = totalMonths + 5; //buffer
			}
		} else if ( paySemester ) {
			
			totalMonths = Integer.parseInt(getParam("numberOfSemesters")); //buffer
			monthlyAmount = totalFlexiAmount/(double) totalMonths;
			totalMonths++;
			
		}
		
		db.begin();
		PaymentSchedule paymentSchedule = new PaymentSchedule();
		paymentSchedule.setStudent(student);
		paymentSchedule.setExpectedStartPaymentDate(startPaymentDate);
		paymentSchedule.setStartPaymentDate(startPaymentDate);
		paymentSchedule.setMonthlyAmount(monthlyAmount);
		paymentSchedule.setTotalAmount(totalAmountAll);
		
		paymentSchedule.setTuitionFees(tuitionFees);
		paymentSchedule.setPtptnAmount(ptptnAmount);
		
		paymentSchedule.setTotalFlexiAmount(totalFlexiAmount);
		paymentSchedule.setTotalPtptnAmount(ptptnAmount);		
		
		List<PaymentScheduleItem> scheduleItems = createScheduleItems(paymentSchedule);
		paymentSchedule.setItems(scheduleItems);
		
		db.persist(paymentSchedule);
		db.commit();
		
		
		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		
		List<XPaymentScheduleItem> items = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		context.put("scheduleItems", items);
				
		getPtptnPaymentSchedule(paymentSchedule);
		getResourceFeePaymentSchedule(paymentSchedule);
		getSemesterPaymentSchedule(paymentSchedule);
		getHostelPaymentSchedule(paymentSchedule);
		getDebitNoteSchedule(paymentSchedule);

		
		return path + "/getPaymentSchedule.vm";
		
	}
	
	

	private List<PaymentScheduleItem> createScheduleItems(PaymentSchedule paymentSchedule) throws ParseException {
		
		
		
		boolean payMonth = "1".equals(getParam("payMonth"));
		boolean paySemester = "1".equals(getParam("paySemester"));

		
		String _tuitionFees = getParam("tuitionFees");
		double tuitionFees = Double.parseDouble(_tuitionFees);
		
		String _rebateFees = getParam("rebateFees");
		if ( "".equals(_rebateFees) ) _rebateFees = "0";
		double rebateFees = Double.parseDouble(_rebateFees);
		
		String _ptptnAmount = getParam("ptptnAmount");
		if ( "".equals(_ptptnAmount)) _ptptnAmount = "0";
		double ptptnAmount = Double.parseDouble(_ptptnAmount);
		
		//double totalAmount = tuitionFees - rebateFees;
		double totalAmount = tuitionFees - rebateFees - ptptnAmount;
		
		int totalMonths = 0;
		double monthlyAmount = 0.0d;
		
		if ( payMonth ) {
			String _monthlyAmount = getParam("monthlyAmount");
			if ( !"".equals(_monthlyAmount)) {
				monthlyAmount = Double.parseDouble(_monthlyAmount);
				totalMonths = ((int) (totalAmount/monthlyAmount)) + 5; //buffer
			} else {
				String _numberOfMonths = getParam("numberOfMonths");
				totalMonths = Integer.parseInt(_numberOfMonths);
				monthlyAmount = totalAmount/totalMonths;
				totalMonths = totalMonths + 5; //buffer
			}
		} else if ( paySemester ) {
			
			totalMonths = Integer.parseInt(getParam("numberOfSemesters")); //buffer
			monthlyAmount = totalAmount/(double) totalMonths;
			totalMonths++;
			
		}
		
		
		
		String _startPaymentDate = getParam("startPaymentDate");
		Date startPaymentDate = new SimpleDateFormat("dd-MM-yyyy").parse(_startPaymentDate);
		Calendar c = Calendar.getInstance();
		c.setTime(startPaymentDate);
		Date paymentDate = startPaymentDate;
		int dayNo = c.get(Calendar.DATE);
		
		int monthlySpan = 1;
		if ( paySemester ) {
			monthlySpan = Integer.parseInt(getParam("monthlySpan"));
		}
		
		List<PaymentScheduleItem> scheduleItems = new ArrayList<PaymentScheduleItem>();
		
		
		double sumBalanceDue = 0.0d;
		double totalAmountDue = 0.0d;
		boolean finished = false;
		double previousBalance = 0.0d;
		for ( int n = 0; n < totalMonths - 1; n++ ) {
			
			sumBalanceDue += monthlyAmount;
			
			if ( sumBalanceDue > totalAmount ) {
				sumBalanceDue = sumBalanceDue - monthlyAmount;
				monthlyAmount = totalAmount - sumBalanceDue;
			}
			
			double amountPaid = 0.0d;
			double currentBalance = previousBalance + monthlyAmount - amountPaid;
			previousBalance = currentBalance;
			if ( !finished ) {
				PaymentScheduleItem item = new PaymentScheduleItem();
				item.setPaymentSchedule(paymentSchedule);
				item.setSeq(n);
				item.setAmountDue(monthlyAmount);
				item.setAmountPaid(amountPaid);
				item.setCurrentBalance(currentBalance);
				item.setOutstandingBalance(totalAmount);
				if ( payMonth ) item.setType(0);
				else if ( paySemester ) item.setType(3);
				
				if ( n > 0 ) {
					Calendar c1 = Calendar.getInstance();
					c1.setTime(paymentDate);
					int m1 = c1.get(Calendar.MONTH);
					int d1 = dayNo;
					int y1 = c1.get(Calendar.YEAR);
					
					
					
					if ( payMonth ) {
						int d2 = 0, y2 = 0, m2 = 0;
						m1++;
						if ( m1 <= 12 ) {
							d2 = d1;
							y2 = y1; 
							m2 = m1;
						}
						else {
							d2 = d1;
							y2 = y1++;
							m2 = 0;
						}
						
						Calendar c2 = Calendar.getInstance();
						c2.set(Calendar.YEAR, y2);
						c2.set(Calendar.MONTH, m2);
						c2.set(Calendar.DATE, 1);
						
						int days = c2.getActualMaximum(Calendar.DAY_OF_MONTH); 
						if ( d2 > days ) d2 = days;
						
						c2.set(Calendar.DATE, d2);
			
						paymentDate = c2.getTime();
						
					} else if ( paySemester ) {
						
						Calendar c2 = Calendar.getInstance();
						c2.setTime(paymentDate);
						c2.add(Calendar.MONTH, monthlySpan);
						
						paymentDate = c2.getTime();
					}
	
					
				}
	
				item.setPaymentDate(paymentDate);
				scheduleItems.add(item);
			}
			
			totalAmountDue += monthlyAmount;
			if ( totalAmountDue >= totalAmount ) {
				finished = true;
			}

		}
		
		return scheduleItems;
		
		
	}
	
	@Command("listPaymentSchedule")
	public String listPaymentSchedule() throws Exception {
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		
		checkTotalAmount(paymentSchedule);
		
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		return path + "/listPaymentSchedule.vm";
	}
	
	private void checkTotalAmount(PaymentSchedule paymentSchedule) throws Exception {
		
		List<PaymentScheduleItem> paymentScheduleItems = paymentSchedule.getItems();
		double totalAmount = 0.0d;
		for ( PaymentScheduleItem it : paymentScheduleItems ) {
			totalAmount += it.getAmountDue();
		}
		if ( paymentSchedule.getTotalAmount() != totalAmount ) {
			db.begin();
			paymentSchedule.setTotalAmount(totalAmount);
			db.commit();
		}
		
	}

	@Command("listPaymentScheduleFlexi")
	public String listPaymentScheduleFlexi() throws Exception {
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		return path + "/listPaymentSchedule_Flexi.vm";
	}
	
	@Command("listPaymentSchedulePtptn")
	public String listPaymentSchedulePtptn() throws Exception {
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		return path + "/listPaymentSchedule_Ptptn.vm";
	}
	
	@Command("makeFirstPayment")
	public String makeFirstPayment() throws Exception {
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		context.put("paymentSchedule", paymentSchedule);
		
		
		context.put("today", new Date());
		return path + "/makeFirstPayment.vm";
	}
	
	private String getValue(String paramName) {
		if ( "".equals(getParam(paramName))) return "0";
		return getParam(paramName);
	}
	
	@Command("saveFirstPayment")
	public String saveFirstPayment() throws Exception {
		
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		context.put("paymentSchedule", paymentSchedule);
		
		double applicationFee = 0.0d, registrationFee = 0.0d, resourceFee = 0.0d, firstPayment = 0.0d;
		
		applicationFee = Double.parseDouble(getValue("applicationFee"));
		registrationFee = Double.parseDouble(getValue("registrationFee"));
		resourceFee = Double.parseDouble(getValue("resourceFee"));
		firstPayment = Double.parseDouble(getValue("firstPayment"));
		
		Date date = new Date();
		String paidDate = getParam("paidDate");
		if ( !"".equals(paidDate)) {
			date = new SimpleDateFormat("dd-MM-yyyy").parse(paidDate);
		}
		
		
		db.begin();
		paymentSchedule.setApplicationFeePaid(applicationFee);
		paymentSchedule.setRegistrationFeePaid(registrationFee);
		paymentSchedule.setResourceFeePaid(resourceFee);
		paymentSchedule.setFirstPaymentPaid(firstPayment);
		paymentSchedule.setApplicationFeePaidDate(applicationFee != 0 ? date : null);
		paymentSchedule.setRegistrationFeePaidDate(registrationFee != 0 ? date : null);
		paymentSchedule.setResourceFeePaidDate(resourceFee != 0 ? date : null);
		paymentSchedule.setFirstPaymentPaidDate(firstPayment != 0 ? date : null);
		db.commit();
		
		List<CPaymentItem> paymentItems = new ArrayList<CPaymentItem>();
		paymentItems.add(new CPaymentItem("PAYMENT FOR APPLICATION FEE", applicationFee, false, false, false));
		paymentItems.add(new CPaymentItem("PAYMENT FOR REGISTRATION FEE", registrationFee, false, false, false));
		paymentItems.add(new CPaymentItem("PAYMENT FOR RESOURCE FEE", resourceFee, false, false, false));
		paymentItems.add(new CPaymentItem("FIRST PAYMENT", firstPayment, false, false, false));
		
		String paymentMode = getParam("payment_mode");
		String remark = getParam("remark");
		String checkNo = getParam("checkNo");
		
		PaymentUtil pu = new PaymentUtil();
		Payment payment = pu.updatePayment(paymentSchedule.getStudent(), paymentItems, date, paymentMode, checkNo, remark);
		context.put("firstPayment", payment);
		String paymentNo = payment.getPaymentNo();
		
		db.begin();
		paymentSchedule.setFirstPaymentNo(paymentNo);
		db.commit();
		
		return path + "/firstPayment.vm";
	
	}
	
	@Command("makeFlexiPayment")
	public String makeFlexiPayment() throws Exception {
		
		String paymentType = getParam("paymentType");
		context.put("paymentType", paymentType);
		
		String scheduleItemId = getParam("scheduleItemId");
		PaymentScheduleItem paymentScheduleItem = db.find(PaymentScheduleItem.class, scheduleItemId);
		context.put("item", paymentScheduleItem);
		
		Student student = paymentScheduleItem.getPaymentSchedule().getStudent();
		context.put("student", student);
		
		List<PaymentScheduleItem2> items = paymentScheduleItem.getItems();
		context.put("paymentItems", items);
		
		return path + "/makeFlexiPayment.vm";
	}
	
	

	
	
	
	@Command("makePayment")
	public String makePayment() throws Exception {
		String scheduleItemId = getParam("scheduleItemId");
		PaymentScheduleItem item = db.find(PaymentScheduleItem.class, scheduleItemId);
		context.put("item", item);
		
		System.out.println("made payment for type = " + item.getType());
		
		String paymentType = getParam("paymentType");
		context.put("paymentType", paymentType);
		
		context.put("student", item.getPaymentSchedule().getStudent());
		
		context.put("today", new Date());
		return path + "/makePayment.vm";
	}
	
	
	@Command("clearFlexiPayment")
	public String clearFlexiPayment() throws Exception {
		
		String itemId = getParam("itemId");
		//PaymentScheduleItem2 item2 = db.find(PaymentScheduleItem2.class, itemId);
		//PaymentScheduleItem item = item2.getPaymentScheduleItem();
		
		PaymentScheduleItem item = db.find(PaymentScheduleItem.class, itemId);
		context.put("item", item);
		context.put("paymentType", "Flexi");
		context.put("student", item.getPaymentSchedule().getStudent());
		
		//remove all
		List<PaymentScheduleItem2> items = item.getItems();
		for ( PaymentScheduleItem2 i : items ) {
			db.begin();
			db.remove(i);
			db.commit();
		}
		
		
		db.begin();
		item.setAmountPaid(0.0);
		item.setItems(null);
		db.commit();
		
		
		PaymentSchedule paymentSchedule = item.getPaymentSchedule();
		
		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		context.put("today", new Date());
		//List<PaymentScheduleItem2> items = item.getItems();
		context.remove("paymentItems");
		
		return path + "/makeFlexiPayment.vm";
		
	}
	
	
	@Command("deleteFlexiPayment")
	public String deleteFlexiPayment() throws Exception {
		
		String itemId = getParam("itemId");
		PaymentScheduleItem2 item2 = db.find(PaymentScheduleItem2.class, itemId);
		PaymentScheduleItem item = item2.getPaymentScheduleItem();
		context.put("item", item);
		context.put("paymentType", "Flexi");
		context.put("student", item.getPaymentSchedule().getStudent());
		
		
		db.begin();
		item.getItems().remove(item2);
		double paymentAmount = item.getItemsTotal();
		item.setAmountPaid(paymentAmount);
		db.remove(item2);
		db.commit();
		
		
		PaymentSchedule paymentSchedule = item.getPaymentSchedule();
		
		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		context.put("today", new Date());
		List<PaymentScheduleItem2> items = item.getItems();
		context.put("paymentItems", items);
		
		return path + "/makeFlexiPayment.vm";
	}
	
	@Command("deletePtptnPayment")
	public String deletePtptnPayment() throws Exception {
		
		
		
		return path + "/makeFlexiPayment.vm";
	}
	
	
	@Command("getFlexiReceipt")
	public String getFlexiReceipt() throws Exception {
		String itemId = getParam("itemId");
		PaymentScheduleItem2 item2 = db.find(PaymentScheduleItem2.class, itemId);
		PaymentScheduleItem item = item2.getPaymentScheduleItem();
		context.put("item", item);
		context.put("paymentType", "Flexi");
		context.put("student", item.getPaymentSchedule().getStudent());
		
		context.put("payment", item2);
		
		
		return path + "/flexiReceipt.vm";
	}
	
	
	@Command("saveFlexiPayment")
	public String saveFlexiPayment() throws Exception {
		String scheduleItemId = getParam("scheduleItemId");
		PaymentScheduleItem item = db.find(PaymentScheduleItem.class, scheduleItemId);
		String _paymentAmount = getParam("paymentAmount");
		
		double paymentAmount = Double.parseDouble(_paymentAmount);
		double currentPaymentAmount = paymentAmount;
		
		Date date = new Date();
		String paidDate = getParam("paidDate");
		if ( !"".equals(paidDate)) {
			date = new SimpleDateFormat("dd-MM-yyyy").parse(paidDate);
		}
		
		String paymentMode = getParam("payment_mode");
		String checkNo = getParam("checkNo");
		String remark = getParam("remark");

		PaymentScheduleItem2 item2 = new PaymentScheduleItem2();
		item2.setAmountPaid(paymentAmount);
		item2.setDate(date);
		item2.setPaymentMode(paymentMode);
		item2.setPaymentRef(checkNo);
		item2.setRemark(remark);
		item2.setPaymentScheduleItem(item);
		
		//add to list first
		db.begin();
		db.persist(item2);
		item.getItems().add(item2);
		db.commit();
		
		//then get total paid (sum with previous payments)
		paymentAmount = item.getItemsTotal();

		db.begin();
		item.setAmountPaid(paymentAmount);
		item.setPaidDate(paymentAmount != 0 ? date : null);
		db.commit();
				
		PaymentSchedule paymentSchedule = item.getPaymentSchedule();
		
		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		
		String paymentType = getParam("paymentType");
		boolean flexi = false, ptptn = false, resourceFee = false;
		String description = "";
		
		flexi = true;
		description = "FEE PAYMENT";
		
		List<CPaymentItem> paymentItems = new ArrayList<CPaymentItem>();
		paymentItems.add(new CPaymentItem(description, currentPaymentAmount, flexi, ptptn, resourceFee));
		
		PaymentUtil pu = new PaymentUtil();
		Payment payment = pu.updatePayment(item.getPaymentSchedule().getStudent(), paymentItems, date, paymentMode, checkNo, remark);
		String paymentNo = payment.getPaymentNo();
		System.out.println("payment no = " + paymentNo);
		
		db.begin();
		item2.setPaymentNo(paymentNo);
		db.commit();
		
		return path + "/listPaymentSchedule_Flexi.vm";
	
	}
	
	

	
	@Command("savePayment")
	public String savePayment() throws Exception {
		String scheduleItemId = getParam("scheduleItemId");
		PaymentScheduleItem item = db.find(PaymentScheduleItem.class, scheduleItemId);
		String _paymentAmount = getParam("paymentAmount");
		
		double paymentAmount = Double.parseDouble(_paymentAmount);
		
		Date date = new Date();
		String paidDate = getParam("paidDate");
		if ( !"".equals(paidDate)) {
			date = new SimpleDateFormat("dd-MM-yyyy").parse(paidDate);
		}
		
		
		db.begin();
		item.setAmountPaid(paymentAmount);
		item.setPaidDate(paymentAmount != 0 ? date : null);
		db.commit();
				
		PaymentSchedule paymentSchedule = item.getPaymentSchedule();
		
		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		
		String paymentType = getParam("paymentType");
		boolean flexi = false, ptptn = false, resourceFee = false;
		String description = "";
		if ( "Ptptn".equals(paymentType)) {
			ptptn = true;
			description = "PAYMENT (PTPTN)";
		}
		else if ( "ResourceFee".equals(paymentType)) {
			resourceFee = true;
			description = "PAYMENT (RESOURCE FEE)";
		}
		else {
			flexi = true;
			description = "PAYMENT (FLEXI)";
		}
		List<CPaymentItem> paymentItems = new ArrayList<CPaymentItem>();
		paymentItems.add(new CPaymentItem(description, paymentAmount, flexi, ptptn, resourceFee));
		
		PaymentUtil pu = new PaymentUtil();
		Payment payment = pu.updatePayment(item.getPaymentSchedule().getStudent(), paymentItems, date);
		//update payment mode
		
		String paymentMode = getParam("payment_mode");
		String remark = getParam("remark");
		String checkNo = getParam("checkNo");
		
		
		db.begin();
		payment.setPaymentMode(paymentMode);
		payment.setCheckNo(checkNo);
		payment.setRemark(remark);
		db.commit();
		
		
		String vm = "/listPaymentSchedule_Flexi.vm";
		if ( "Ptptn".equals(paymentType)) vm = "/listPtptnPaymentScheduleItems.vm";
		else if ( "ResourceFee".equals(paymentType)) vm = "/listResourceFeePaymentScheduleItems.vm";
		return path + vm;
	
	}
	
	@Command("deletePaymentSchedule")
	public String deletePaymentSchedule() throws Exception {
		String paymentScheduleId = getParam("paymentScheduleId");
		
		//delete all invoices associated with this payment schedule
		
		
		//delete
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		db.begin();
		db.remove(paymentSchedule);
		db.commit();

		return getPaymentSchedule();
	}
	
	@Command("createInvoices")
	public String createInvoices() throws Exception {
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		PaymentUtil.createInvoicesOnPaymentSchedule(db, paymentSchedule);
		return path + "/createInvoices.vm";
	}
	
	@Command("listPtptnItems")
	public String listPtptnItems() throws Exception {
		String ptptnScheduleId = getParam("ptptnScheduleId");
		PtptnPaymentSchedule ptptnPaymentSchedule = db.find(PtptnPaymentSchedule.class, ptptnScheduleId);
		context.put("ptptnPaymentSchedule", ptptnPaymentSchedule);
		List<PtptnPaymentScheduleItem> ptptnScheduleItems = ptptnPaymentSchedule.getItems();
		context.put("ptptnItems", ptptnScheduleItems);
		return path + "/ptptnItems.vm";
	}
	
	public String listPtptnItems(PtptnPaymentSchedule ptptnPaymentSchedule) throws Exception {
		context.put("ptptnPaymentSchedule", ptptnPaymentSchedule);
		List<PtptnPaymentScheduleItem> ptptnScheduleItems = ptptnPaymentSchedule.getItems();
		context.put("ptptnItems", ptptnScheduleItems);
		return path + "/ptptnItems.vm";
	}
	
	public String listResourceFeePaymentItems(ResourceFeePaymentSchedule resourceFeePaymentSchedule) throws Exception {
		context.put("resourceFeePaymentSchedule", resourceFeePaymentSchedule);
		List<ResourceFeePaymentScheduleItem> resourceFeeScheduleItems = resourceFeePaymentSchedule.getItems();
		context.put("resourceFeeItems", resourceFeeScheduleItems);
		return path + "/resourceFeeItems.vm";
	}
	
	
	
	@Command("addPaymentItemPtptn")
	public String addPaymentItemPtptn() throws Exception {
		String ptptnScheduleId = getParam("ptptnScheduleId");
		PtptnPaymentSchedule ptptnPaymentSchedule = db.find(PtptnPaymentSchedule.class, ptptnScheduleId);
		
		String _amountDue = getParam("ptptnAmountDue");
		double amountDue = Double.parseDouble(_amountDue);
		
		String _dateDue = getParam("ptptnDateDue");
		Date dateDue = new SimpleDateFormat("dd-MM-yyyy").parse(_dateDue);
		
		db.begin();
		PtptnPaymentScheduleItem item = new PtptnPaymentScheduleItem();
		item.setPaymentSchedule(ptptnPaymentSchedule);
		item.setAmountDue(amountDue);
		item.setDateDue(dateDue);
		ptptnPaymentSchedule.getItems().add(item);
		db.commit();
		
		
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		
		db.begin();
		PaymentScheduleItem paymentScheduleItem = new PaymentScheduleItem();
		paymentScheduleItem.setPaymentSchedule(paymentSchedule);
		paymentScheduleItem.setType(1);
		paymentScheduleItem.setPtptnItemId(item.getId());
		paymentScheduleItem.setPaymentDate(item.getDateDue());
		paymentScheduleItem.setAmountDue(item.getAmountDue());
		paymentSchedule.getItems().add(paymentScheduleItem);
		
		paymentSchedule.setTotalAmount(paymentSchedule.getTotalAmount() + item.getAmountDue());
		
		db.commit();

		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		
		PaymentUtil.createInvoicesOnPaymentScheduleItem(db, paymentScheduleItem, "PTPTN");
		
		context.put("ptptnPaymentSchedule", ptptnPaymentSchedule);
		List<PtptnPaymentScheduleItem> ptptnScheduleItems = ptptnPaymentSchedule.getItems();
		context.put("ptptnItems", ptptnScheduleItems);
		
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		
		return path + "/paymentSchedulePTPTN.vm";
	}
	
	
	@Command("deletePaymentItemPtptn")
	public String deletePaymentItemPtptn() throws Exception {
		String ptptnScheduleId = getParam("ptptnScheduleId");
		PtptnPaymentSchedule ptptnPaymentSchedule = db.find(PtptnPaymentSchedule.class, ptptnScheduleId);
		
		String itemId = getParam("itemId");
		PtptnPaymentScheduleItem item = db.find(PtptnPaymentScheduleItem.class, itemId);
		
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		
		
		
		PaymentScheduleItem paymentScheduleItem = (PaymentScheduleItem) db.get("select i from PaymentScheduleItem i where i.ptptnItemId = '" + item.getId() + "'");
		if ( paymentScheduleItem != null ) {
			
			PaymentUtil.deleteInvoicesOnPaymentScheduleItem(db, paymentScheduleItem);
			
			db.begin();
			db.remove(paymentScheduleItem);
			paymentSchedule.getItems().remove(paymentScheduleItem);
			db.commit();
		}
		
		db.begin();
		db.remove(item);
		ptptnPaymentSchedule.getItems().remove(item);
		db.commit();
		
		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		context.put("ptptnPaymentSchedule", ptptnPaymentSchedule);
		List<PtptnPaymentScheduleItem> ptptnScheduleItems = ptptnPaymentSchedule.getItems();
		context.put("ptptnItems", ptptnScheduleItems);
		
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		
		return path + "/paymentSchedulePTPTN.vm";
	}
	
	
	@Command("addPaymentItemResourceFee")
	public String addPaymentItemResourceFee() throws Exception {
		String resourceFeePaymentScheduleId = getParam("resourceFeePaymentScheduleId");
		ResourceFeePaymentSchedule resourceFeePaymentSchedule = db.find(ResourceFeePaymentSchedule.class, resourceFeePaymentScheduleId);
		
		String _amountDue = getParam("amountDue");
		double amountDue = Double.parseDouble(_amountDue);
		
		String _dateDue = getParam("dateDue");
		Date dateDue = new SimpleDateFormat("dd-MM-yyyy").parse(_dateDue);
		
		db.begin();
		ResourceFeePaymentScheduleItem item = new ResourceFeePaymentScheduleItem();
		item.setPaymentSchedule(resourceFeePaymentSchedule);
		item.setAmountDue(amountDue);
		item.setDateDue(dateDue);
		resourceFeePaymentSchedule.getItems().add(item);
		db.commit();
		
		
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		
		db.begin();
		PaymentScheduleItem paymentScheduleItem = new PaymentScheduleItem();
		paymentScheduleItem.setPaymentSchedule(paymentSchedule);
		paymentScheduleItem.setType(2);
		paymentScheduleItem.setPtptnItemId(item.getId());
		paymentScheduleItem.setPaymentDate(item.getDateDue());
		paymentScheduleItem.setAmountDue(item.getAmountDue());
		paymentSchedule.getItems().add(paymentScheduleItem);
		
		paymentSchedule.setTotalAmount(paymentSchedule.getTotalAmount() + item.getAmountDue());
		
		db.commit();
		
		System.out.println("added to total amount = " + paymentSchedule.getTotalAmount());

		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		PaymentUtil.createInvoicesOnPaymentScheduleItem(db, paymentScheduleItem, "RESOURCE FEE");
		
		context.put("resourceFeePaymentSchedule", resourceFeePaymentSchedule);
		List<ResourceFeePaymentScheduleItem> resourceFeeScheduleItems = resourceFeePaymentSchedule.getItems();
		context.put("resourceFeeItems", resourceFeeScheduleItems);
		
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		
		return path + "/paymentScheduleResourceFee.vm";
		
		
	}
	
	
	
	@Command("deletePaymentItemResourceFee")
	public String deletePaymentItemResourceFee() throws Exception {
		String resourceFeePaymentScheduleId = getParam("resourceFeePaymentScheduleId");
		ResourceFeePaymentSchedule resourceFeePaymentSchedule = db.find(ResourceFeePaymentSchedule.class, resourceFeePaymentScheduleId);
		
		String itemId = getParam("itemId");
		ResourceFeePaymentScheduleItem item = db.find(ResourceFeePaymentScheduleItem.class, itemId);
		
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		
		
		
		PaymentScheduleItem paymentScheduleItem = (PaymentScheduleItem) db.get("select i from PaymentScheduleItem i where i.ptptnItemId = '" + item.getId() + "'");
		if ( paymentScheduleItem != null ) {
			
			PaymentUtil.deleteInvoicesOnPaymentScheduleItem(db, paymentScheduleItem);
			
			db.begin();
			db.remove(paymentScheduleItem);
			paymentSchedule.getItems().remove(paymentScheduleItem);
			db.commit();
		}
		
		db.begin();
		db.remove(item);
		resourceFeePaymentSchedule.getItems().remove(item);
		db.commit();
		
		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		context.put("resourceFeePaymentSchedule", resourceFeePaymentSchedule);
		List<ResourceFeePaymentScheduleItem> resourceFeeScheduleItems = resourceFeePaymentSchedule.getItems();
		context.put("resourceFeeItems", resourceFeeScheduleItems);
		
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		
		return path + "/paymentScheduleResourceFee.vm";
		
	}
	
	
	
	
	
	/*
	 * CREDIT NOTE
	 */
	
	
	@Command("addPaymentItemDebitNote")
	public String addPaymentItemDebitNote() throws Exception {
		String resourceFeePaymentScheduleId = getParam("resourceFeePaymentScheduleId");
		ResourceFeePaymentSchedule resourceFeePaymentSchedule = db.find(ResourceFeePaymentSchedule.class, resourceFeePaymentScheduleId);
		
		String _amountDue = getParam("amountDue");
		double amountDue = Double.parseDouble(_amountDue);
		
		String _dateDue = getParam("dateDue");
		Date dateDue = new SimpleDateFormat("dd-MM-yyyy").parse(_dateDue);
		
		db.begin();
		ResourceFeePaymentScheduleItem item = new ResourceFeePaymentScheduleItem();
		item.setPaymentSchedule(resourceFeePaymentSchedule);
		item.setAmountDue(amountDue);
		item.setDateDue(dateDue);
		resourceFeePaymentSchedule.getItems().add(item);
		db.commit();
		
		
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		
		db.begin();
		PaymentScheduleItem paymentScheduleItem = new PaymentScheduleItem();
		paymentScheduleItem.setPaymentSchedule(paymentSchedule);
		paymentScheduleItem.setType(2);
		paymentScheduleItem.setPtptnItemId(item.getId());
		paymentScheduleItem.setPaymentDate(item.getDateDue());
		paymentScheduleItem.setAmountDue(item.getAmountDue());
		paymentSchedule.getItems().add(paymentScheduleItem);
		
		paymentSchedule.setTotalAmount(paymentSchedule.getTotalAmount() + item.getAmountDue());
		
		db.commit();
		
		System.out.println("added to total amount = " + paymentSchedule.getTotalAmount());

		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		PaymentUtil.createInvoicesOnPaymentScheduleItem(db, paymentScheduleItem, "RESOURCE FEE");
		
		context.put("resourceFeePaymentSchedule", resourceFeePaymentSchedule);
		List<ResourceFeePaymentScheduleItem> resourceFeeScheduleItems = resourceFeePaymentSchedule.getItems();
		context.put("resourceFeeItems", resourceFeeScheduleItems);
		
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		
		return path + "/paymentScheduleResourceFee.vm";
		
		
	}
	
	
	@Command("addPaymentItemSemester")
	public String addPaymentItemSemester() throws Exception {
		String semesterPaymentScheduleId = getParam("semesterPaymentScheduleId");
		SemesterPaymentSchedule semesterPaymentSchedule = db.find(SemesterPaymentSchedule.class, semesterPaymentScheduleId);
		
		String _amountDue = getParam("amountDueSemester");
		double amountDue = Double.parseDouble(_amountDue);
		
		String _dateDue = getParam("dateDueSemester");
		Date dateDue = new SimpleDateFormat("dd-MM-yyyy").parse(_dateDue);
		
		db.begin();
		SemesterPaymentScheduleItem item = new SemesterPaymentScheduleItem();
		item.setPaymentSchedule(semesterPaymentSchedule);
		item.setAmountDue(amountDue);
		item.setDateDue(dateDue);
		semesterPaymentSchedule.getItems().add(item);
		db.commit();
		
		
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		
		db.begin();
		PaymentScheduleItem paymentScheduleItem = new PaymentScheduleItem();
		paymentScheduleItem.setPaymentSchedule(paymentSchedule);
		paymentScheduleItem.setType(3);
		paymentScheduleItem.setPtptnItemId(item.getId());
		paymentScheduleItem.setPaymentDate(item.getDateDue());
		paymentScheduleItem.setAmountDue(item.getAmountDue());
		paymentSchedule.getItems().add(paymentScheduleItem);
		
		paymentSchedule.setTotalAmount(paymentSchedule.getTotalAmount() + item.getAmountDue());
		
		db.commit();

		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		
		
		//PaymentUtil.createInvoicesOnPaymentScheduleItem(db, paymentScheduleItem, "SEMESTER FEE");
		
		
		
		context.put("semesterPaymentSchedule", semesterPaymentSchedule);
		List<SemesterPaymentScheduleItem> semesterScheduleItems = semesterPaymentSchedule.getItems();
		context.put("semesterItems", semesterScheduleItems);
		
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		
		return path + "/paymentScheduleSemester.vm";

	}
	
	
	@Command("deletePaymentItemSemester")
	public String deletePaymentItemSemester() throws Exception {
		String semesterPaymentScheduleId = getParam("semesterPaymentScheduleId");
		SemesterPaymentSchedule semesterPaymentSchedule = db.find(SemesterPaymentSchedule.class, semesterPaymentScheduleId);
		
		String itemId = getParam("itemId");
		SemesterPaymentScheduleItem item = db.find(SemesterPaymentScheduleItem.class, itemId);
		
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		
		PaymentScheduleItem paymentScheduleItem = (PaymentScheduleItem) db.get("select i from PaymentScheduleItem i where i.ptptnItemId = '" + item.getId() + "'");
		if ( paymentScheduleItem != null ) {
			
			PaymentUtil.deleteInvoicesOnPaymentScheduleItem(db, paymentScheduleItem);
			
			db.begin();
			db.remove(paymentScheduleItem);
			paymentSchedule.getItems().remove(paymentScheduleItem);
			db.commit();
		}
		
		db.begin();
		db.remove(item);
		semesterPaymentSchedule.getItems().remove(item);
		db.commit();
		
		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		context.put("semesterPaymentSchedule", semesterPaymentSchedule);
		List<SemesterPaymentScheduleItem> semesterItems = semesterPaymentSchedule.getItems();
		context.put("semesterItems", semesterItems);
		
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		
		return path + "/paymentScheduleSemester.vm";
		
	}
	
	
	@Command("addPaymentItemHostel")
	public String addPaymentItemHostel() throws Exception {
		String hostelPaymentScheduleId = getParam("hostelPaymentScheduleId");
		HostelPaymentSchedule hostelPaymentSchedule = db.find(HostelPaymentSchedule.class, hostelPaymentScheduleId);
		
		System.out.println("hostelPaymentScheduleId = " + hostelPaymentScheduleId);
		
		String _amountDue = getParam("amountDueHostel");
		double amountDue = Double.parseDouble(_amountDue);
		
		String _dateDue = getParam("dateDueHostel");
		Date dateDue = new SimpleDateFormat("dd-MM-yyyy").parse(_dateDue);
		
		db.begin();
		HostelPaymentScheduleItem item = new HostelPaymentScheduleItem();
		item.setPaymentSchedule(hostelPaymentSchedule);
		item.setAmountDue(amountDue);
		item.setDateDue(dateDue);
		hostelPaymentSchedule.getItems().add(item);
		db.commit();
		
		
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		
		db.begin();
		PaymentScheduleItem paymentScheduleItem = new PaymentScheduleItem();
		paymentScheduleItem.setPaymentSchedule(paymentSchedule);
		paymentScheduleItem.setType(4);
		paymentScheduleItem.setPtptnItemId(item.getId());
		paymentScheduleItem.setPaymentDate(item.getDateDue());
		paymentScheduleItem.setAmountDue(item.getAmountDue());
		paymentSchedule.getItems().add(paymentScheduleItem);
		
		paymentSchedule.setTotalAmount(paymentSchedule.getTotalAmount() + item.getAmountDue());
		
		db.commit();

		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		
		
		//PaymentUtil.createInvoicesOnPaymentScheduleItem(db, paymentScheduleItem, "SEMESTER FEE");
		
		
		
		context.put("hostelPaymentSchedule", hostelPaymentSchedule);
		List<HostelPaymentScheduleItem> hostelScheduleItems = hostelPaymentSchedule.getItems();
		context.put("hostelItems", hostelScheduleItems);
		
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		
		return path + "/paymentScheduleHostel.vm";

	}
	
	@Command("deletePaymentItemHostel")
	public String deletePaymentItemHostel() throws Exception {
		String hostelPaymentScheduleId = getParam("hostelPaymentScheduleId");
		HostelPaymentSchedule hostelPaymentSchedule = db.find(HostelPaymentSchedule.class, hostelPaymentScheduleId);
		
		String itemId = getParam("itemId");
		HostelPaymentScheduleItem item = db.find(HostelPaymentScheduleItem.class, itemId);
		
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		
		
		
		PaymentScheduleItem paymentScheduleItem = (PaymentScheduleItem) db.get("select i from PaymentScheduleItem i where i.ptptnItemId = '" + item.getId() + "'");
		if ( paymentScheduleItem != null ) {
			
			PaymentUtil.deleteInvoicesOnPaymentScheduleItem(db, paymentScheduleItem);
			
			db.begin();
			db.remove(paymentScheduleItem);
			paymentSchedule.getItems().remove(paymentScheduleItem);
			db.commit();
		}
		
		db.begin();
		db.remove(item);
		hostelPaymentSchedule.getItems().remove(item);
		db.commit();
		
		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		context.put("hostelPaymentSchedule", hostelPaymentSchedule);
		List<HostelPaymentScheduleItem> hostelItems = hostelPaymentSchedule.getItems();
		context.put("hostelItems", hostelItems);
		
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		
		return path + "/paymentScheduleHostel.vm";
		
	}
	
	@Command("listResourceFeePaymentScheduleItems")
	public String listResourceFeePaymentScheduleItems() throws Exception {
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		return path + "/listResourceFeePaymentScheduleItems.vm";
	}
	
	@Command("listPtptnPaymentScheduleItems")
	public String listPtptnPaymentScheduleItems() throws Exception {
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		return path + "/listPtptnPaymentScheduleItems.vm";
	}
	

	            
	
	@Command("addPaymentDebitNote")
	public String addPaymentDebitNote() throws Exception {
		String debitNoteScheduleId = getParam("debitNoteScheduleId");
		DebitNoteSchedule debitNoteSchedule = db.find(DebitNoteSchedule.class, debitNoteScheduleId);
		
		System.out.println("debitNoteSchedule = " + debitNoteSchedule);
		
		context.put("debitNoteSchedule", debitNoteSchedule);
		
		String _amountDue = getParam("debitNoteAmount");
		double amountDue = Double.parseDouble(_amountDue) * -1;
		
		String _dateDue = getParam("debitNoteDate");
		Date dateDue = new SimpleDateFormat("dd-MM-yyyy").parse(_dateDue);
		
		db.begin();
		DebitNoteScheduleItem item = new DebitNoteScheduleItem();
		item.setPaymentSchedule(debitNoteSchedule);
		item.setAmountDue(amountDue);
		item.setDateDue(dateDue);
		debitNoteSchedule.getItems().add(item);
		db.commit();
		
		
		System.out.println("Items in DebitNoteSchedule");
		for ( DebitNoteScheduleItem it : debitNoteSchedule.getItems() ) {
			System.out.println(it.getAmountDue() + ", " + it.getDateDue());
		}
		
		
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		//
		System.out.println("Payment Schedule is " + paymentSchedule);
		
		db.begin();
		System.out.println("Create new payment schedule item");
		PaymentScheduleItem paymentScheduleItem = new PaymentScheduleItem();
		paymentScheduleItem.setPaymentSchedule(paymentSchedule);
		paymentScheduleItem.setType(6);  //6 - debit note
		paymentScheduleItem.setPtptnItemId(item.getId());
		paymentScheduleItem.setPaymentDate(item.getDateDue());
		paymentScheduleItem.setAmountDue(item.getAmountDue());
		
		paymentSchedule.getItems().add(paymentScheduleItem);
		paymentSchedule.setTotalAmount(paymentSchedule.getTotalAmount() + item.getAmountDue());
		
		db.commit();
		
		System.out.println("total items in Payment Schdeule = " + paymentSchedule.getItems().size());
		
		System.out.println("added to total amount = " + paymentSchedule.getTotalAmount());

		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		//PaymentUtil.createInvoicesOnPaymentScheduleItem(db, paymentScheduleItem, "RESOURCE FEE");
		
		
		/*
		List<DebitNoteScheduleItem> debitNoteScheduleItems = debitNoteSchedule.getItems();
		context.put("debitNoteItems", debitNoteScheduleItems);
		
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		
		*/
		return path + "/paymentDebitNote.vm";
		
		
	}
	
	
	@Command("deletePaymentDebitNoteX")
	public String deletePaymentDebitNoteX() throws Exception {
		String ptptnScheduleId = getParam("ptptnScheduleId");
		PtptnPaymentSchedule ptptnPaymentSchedule = db.find(PtptnPaymentSchedule.class, ptptnScheduleId);
		
		String itemId = getParam("itemId");
		PtptnPaymentScheduleItem item = db.find(PtptnPaymentScheduleItem.class, itemId);
		
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		
		
		
		PaymentScheduleItem paymentScheduleItem = (PaymentScheduleItem) db.get("select i from PaymentScheduleItem i where i.ptptnItemId = '" + item.getId() + "'");
		if ( paymentScheduleItem != null ) {
			
			PaymentUtil.deleteInvoicesOnPaymentScheduleItem(db, paymentScheduleItem);
			
			db.begin();
			db.remove(paymentScheduleItem);
			paymentSchedule.getItems().remove(paymentScheduleItem);
			db.commit();
		}
		
		db.begin();
		db.remove(item);
		ptptnPaymentSchedule.getItems().remove(item);
		db.commit();
		
		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		context.put("ptptnPaymentSchedule", ptptnPaymentSchedule);
		List<PtptnPaymentScheduleItem> ptptnScheduleItems = ptptnPaymentSchedule.getItems();
		context.put("ptptnItems", ptptnScheduleItems);
		
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		
		return path + "/paymentSchedulePTPTN.vm";
	}
	
	@Command("deletePaymentDebitNote")
	public String deletePaymentDebitNote() throws Exception {
		String debitNoteScheduleId = getParam("debitNoteScheduleId");
		DebitNoteSchedule debitNoteSchedule = db.find(DebitNoteSchedule.class, debitNoteScheduleId);
		
		System.out.println("debit note = " + debitNoteSchedule);
		
		String itemId = getParam("itemId");
		DebitNoteScheduleItem item = db.find(DebitNoteScheduleItem.class, itemId);
		
				
		String paymentScheduleId = getParam("paymentScheduleId");
		PaymentSchedule paymentSchedule = db.find(PaymentSchedule.class, paymentScheduleId);
		
		System.out.println("payment schedule = " + paymentSchedule);
		
		
		PaymentScheduleItem paymentScheduleItem = (PaymentScheduleItem) db.get("select i from PaymentScheduleItem i where i.ptptnItemId = '" + item.getId() + "'");
		if ( paymentScheduleItem != null ) {
			
			db.begin();
			db.remove(paymentScheduleItem);
			paymentSchedule.getItems().remove(paymentScheduleItem);
			db.commit();
		}
		
		db.begin();
		db.remove(item);
		debitNoteSchedule.getItems().remove(item);
		db.commit();

		
		System.out.println("added to total amount = " + paymentSchedule.getTotalAmount());

		PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentFlexiSchedules(db, paymentSchedule);
		PaymentUtil.calculatePaymentPtpnSchedules(db, paymentSchedule);
		
		
		context.put("debitNoteSchedule", debitNoteSchedule);
		List<DebitNoteScheduleItem> debitNoteScheduleItems = debitNoteSchedule.getItems();
		context.put("debitNoteItems", debitNoteScheduleItems);
		
		List<XPaymentScheduleItem> scheduleItems = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
		
		context.put("paymentSchedule", paymentSchedule);
		context.put("scheduleItems", scheduleItems);
		
		return path + "/paymentDebitNote.vm";
		
		
	}

}
