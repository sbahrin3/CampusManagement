package educate.sis.finance.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.finance.entity.FeeStructure;
import educate.sis.finance.entity.FeeStructureItem;
import educate.sis.finance.entity.Invoice;
import educate.sis.finance.entity.InvoiceItem;
import educate.sis.finance.entity.PaymentScheduleItem;
import educate.sis.finance.entity.RefundLog;
import educate.sis.finance.entity.SponsorInvoice;
import educate.sis.finance.entity.SponsorStudent;
import educate.sis.finance.entity.XInvoice;
import educate.sis.module.StudentStatusUtil;
import educate.sis.struct.BillingNumGenerator;
import educate.sis.struct.MatricNumGenerator;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.SubjectPeriod;
import educate.sis.struct.entity.SubjectReg;

public class InvoiceUtil {
	
	String invoicePrefix = "INV";
	String refundPrefix = "RF";
	String debitNotePrefix = "DN";
	DbPersistence db = new DbPersistence();
	String userId;
	
	public void setUserId(String userId) {
		if ( userId == null ) userId = "unknown";
		this.userId = userId;
	}
	
	private double getFeeTotal(SubjectPeriod sp, List<String> subjectIds) {
		double total = 0.0d;
		Set<SubjectReg> subjectRegs = sp.getSubjectRegs();
		for ( SubjectReg s : subjectRegs ) {
			
			if ( s.getSubject() != null ) {
				boolean b = false;
				for ( String id : subjectIds ) {
					if ( s.getSubject().getId().equals(id)) {
						b = true;
						break;
					}
				}
				
				if ( b ) {
					for ( FeeStructureItem item : s.getFeeItems() ) {
						if ( item.getFeeItem() != null ) total += item.getAmount();
					}
				}
			}
		}
		return total;
	}
	

	
	public List<InvoiceItem> getInvoiceItemsX(Program program, Session intake, Period period, LearningCentre centre) {
		return getInvoiceItemsX(program, intake, period, centre, false);
	}
	
	/*
	 * Prepare items for invoice
	 */
	
	public List<InvoiceItem> getInvoiceItemsX(Program program, Session intake, Period period, LearningCentre centre, boolean enableSubjectFee) {
		List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
		//get fee structure
		FeeStructure fee = null;
		List<FeeStructure> feeList = db.list("select f from FeeStructure f where f.program.id = '" + program.getId() + "'");
		if ( feeList.size() > 0 ) {
			fee = feeList.get(0);
		}
		
		if ( fee == null ) {
			System.out.println("Fee Structure NOT DEFINED!");
		}
		else {
			//get subjects fee
			double subjectFeeAmount = 0.0;
			if ( enableSubjectFee) {
				Hashtable param = new Hashtable();
				param.put("program", program);
				param.put("intake", intake);
				param.put("centre", centre);
				param.put("period", period);
				String sql = "select sp from ProgramStructure ps " +
						"join ps.subjectPeriod sp " +
						"where ps.program = :program " +
						"and ps.session = :intake " +
						"and ps.learningCenter = :centre " +
						"and sp.period = :period";
				List<SubjectPeriod> sps = db.list(sql, param);
				if ( sps.size() > 0 ) {
					SubjectPeriod sp = sps.get(0);
					subjectFeeAmount = sp.getFeeTotal();
				}
			}
			
			List<FeeStructureItem> feeItems = new ArrayList<FeeStructureItem>();
			Set<FeeStructureItem> items = fee.getItems();
			for ( FeeStructureItem item : items ) {
				if ( period.hasParent() ) 
					if ( period.getParent().getId().equals(item.getPeriod().getId())) feeItems.add(item);
				if ( period.getId().equals(item.getPeriod().getId()) ) feeItems.add(item);
			}
			double totalFeeAmount = 0.0d;
			
			//create invoice items
			InvoiceItem i = new InvoiceItem();
			
			//subjects fee, if enabled, and it has amount (if amount is zero then no need to create.)
			if ( enableSubjectFee && subjectFeeAmount > 0 ) {
				i.setAmount(subjectFeeAmount);
				i.setCourseFee(true);
				i.setDescription("Course Fee");
				i.setFeeItem(null);
				invoiceItems.add(i);
			}
			
			for ( FeeStructureItem item : feeItems ) {
				i = new InvoiceItem();
				i.setAmount(item.getAmount());
				i.setCourseFee(false);
				i.setDescription(item.getFeeItem().getDescription());
				i.setFeeItem(item.getFeeItem());
				invoiceItems.add(i);
				totalFeeAmount += item.getAmount();
			}
		}
		return invoiceItems;
	}
	


	
	public Invoice createStudentInvoice(StudentStatus s) throws FeeStructureNotAvailableException, Exception {
		try {
			List<InvoiceItem> items = new InvoiceUtil().getInvoiceItems(s.getStudent(), s.getPeriod());
			return createStudentInvoice(s.getStudent(), items, s.getSession());
		} catch ( FeeStructureNotAvailableException e ) {
			throw e;
		}
	}
	
	/*
	 * Prepare items for a primary invoice
	 * An item for the primary invoice is fetched from fee setup (for a period of study)
	 */
	
	public List<InvoiceItem> getInvoiceItems(Student student, Period period) throws FeeStructureNotAvailableException {
		return getInvoiceItems(student, period, false);
	}
	
	public List<InvoiceItem> getInvoiceItems(Student student, Period period, boolean enableSubjectFee) throws FeeStructureNotAvailableException {
		Program program = student.getProgram();
		Session intake = student.getIntake();
		LearningCentre centre = student.getLearningCenter();
		StudentStatusUtil u = new StudentStatusUtil();
	
		//get registered subjects for this student in this period
		List<StudentStatus> statuses = u.getStudentStatuses(student);
		StudentStatus status = null;
		for ( StudentStatus st : statuses ) {
			if ( st.getPeriod() == null ) {
				throw new FeeStructureNotAvailableException("Period not defined.");			}
			if ( st.getPeriod().getId().equals(period.getId())) {
				status = st;
				break;
			}
		}
		
		List<String> subjectIds = new ArrayList<String>();
		if ( status != null ) {
			Set<StudentSubject> studentSubjects = status.getStudentSubjects();
			for ( StudentSubject s : studentSubjects ) {
				if ( s.getSubject() != null ) subjectIds.add(s.getSubject().getId());
			}
		}
		
		List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
		
		FeeStructure fee = getFeeStructureByIntake(student);
		
		if ( fee == null ) {
			System.out.println("Fee Structure NOT DEFINED!");
			throw new FeeStructureNotAvailableException("Fee Structure NOT DEFINED for program " + program.getCode() + " " + program.getName());
		}
		else {
			//get subjects fee
			double subjectFeeAmount = 0.0;
			Hashtable param = new Hashtable();
			param.put("program", program);
			param.put("intake", intake);
			param.put("centre", centre);
			param.put("period", period);
			String sql = "select sp from ProgramStructure ps " +
					"join ps.subjectPeriod sp " +
					"where ps.program = :program " +
					"and ps.session = :intake " +
					"and ps.learningCenter = :centre " +
					"and sp.period = :period";
			List<SubjectPeriod> sps = db.list(sql, param);
			if ( sps.size() > 0 ) {
				SubjectPeriod sp = sps.get(0);
				subjectFeeAmount = getFeeTotal(sp, subjectIds);
			}

			
			List<FeeStructureItem> feeItems = new ArrayList<FeeStructureItem>();
			Set<FeeStructureItem> items = fee.getItems();
			for ( FeeStructureItem item : items ) {
				if ( period.hasParent() ) 
					if ( period.getParent().getId().equals(item.getPeriod().getId())) feeItems.add(item);
				if ( period.getId().equals(item.getPeriod().getId()) ) feeItems.add(item);
			}
			double totalFeeAmount = 0.0d;
			
			//create invoice items
			InvoiceItem i = new InvoiceItem();
			
			//based on subjects fee, if enabled
			if ( enableSubjectFee && subjectFeeAmount > 0 ) {
				i.setAmount(subjectFeeAmount);
				i.setCourseFee(true);
				i.setDescription("SUBJECTS FEE");
				i.setFeeItem(null);
				invoiceItems.add(i);
			}

			//based on fee structure setup
			for ( FeeStructureItem item : feeItems ) {
				i = new InvoiceItem();
				i.setAmount(item.getAmount());
				i.setCourseFee(false);
				i.setDescription(item.getFeeItem().getDescription());
				i.setFeeItem(item.getFeeItem());
				invoiceItems.add(i);
				totalFeeAmount += item.getAmount();
			}
		}
		return invoiceItems;
	}

	
	public List<InvoiceItem> getInvoiceMonthlyItems(Student student, Period period) throws FeeStructureNotAvailableException {
		Program program = student.getProgram();
		Session intake = student.getIntake();
		LearningCentre centre = student.getLearningCenter();
		StudentStatusUtil u = new StudentStatusUtil();
	
		//get registered subjects for this student in this period
		List<StudentStatus> statuses = u.getStudentStatuses(student);
		StudentStatus status = null;
		for ( StudentStatus st : statuses ) {
			if ( st.getPeriod() == null ) {
				throw new FeeStructureNotAvailableException("Period not defined.");			}
			if ( st.getPeriod().getId().equals(period.getId())) {
				status = st;
				break;
			}
		}
		
		List<String> subjectIds = new ArrayList<String>();
		if ( status != null ) {
			Set<StudentSubject> studentSubjects = status.getStudentSubjects();
			for ( StudentSubject s : studentSubjects ) {
				if ( s.getSubject() != null ) subjectIds.add(s.getSubject().getId());
			}
		}
		
		List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
		
		FeeStructure fee = getFeeStructureByIntake(student);
		
		if ( fee == null ) {
			System.out.println("Fee Structure NOT DEFINED!");
			throw new FeeStructureNotAvailableException("Fee Structure NOT DEFINED for program " + program.getCode() + " " + program.getName());
		}
		else {
			//get subjects fee
			double subjectFeeAmount = 0.0;
			Hashtable param = new Hashtable();
			param.put("program", program);
			param.put("intake", intake);
			param.put("centre", centre);
			param.put("period", period);
			String sql = "select sp from ProgramStructure ps " +
					"join ps.subjectPeriod sp " +
					"where ps.program = :program " +
					"and ps.session = :intake " +
					"and ps.learningCenter = :centre " +
					"and sp.period = :period";
			List<SubjectPeriod> sps = db.list(sql, param);
			if ( sps.size() > 0 ) {
				SubjectPeriod sp = sps.get(0);
				subjectFeeAmount = getFeeTotal(sp, subjectIds);
			}

			
			List<FeeStructureItem> feeItems = new ArrayList<FeeStructureItem>();
			Set<FeeStructureItem> items = fee.getItems();
			for ( FeeStructureItem item : items ) {
				if ( period.hasParent() ) 
					if ( period.getParent().getId().equals(item.getPeriod().getId())) feeItems.add(item);
				if ( period.getId().equals(item.getPeriod().getId()) ) feeItems.add(item);
			}
			double totalFeeAmount = 0.0d;
			
			//create invoice items
			InvoiceItem i = new InvoiceItem();
			for ( FeeStructureItem item : feeItems ) {
				if ( item.getFeeItem().getPayingMode() == 3 ) {
					i = new InvoiceItem();
					i.setAmount(item.getAmount());
					i.setCourseFee(false);
					i.setDescription(item.getFeeItem().getDescription());
					i.setFeeItem(item.getFeeItem());
					invoiceItems.add(i);
					totalFeeAmount += item.getAmount();
				}
			}
		}
		return invoiceItems;
	}
	private FeeStructure getFeeStructureByIntake(Student student) {
		Date date = student.getIntake().getStartDate();
		String programId = student.getProgram().getId();
		FeeStructure fee = null;
		List<FeeStructure> feeList = db.list("select f from FeeStructure f where f.program.id = '" + programId + "' order by f.sessionDate desc");
		for ( FeeStructure f : feeList ) {
			if ( f.getSessionDate() != null && date.after(f.getSessionDate())) {
				fee = f;
				break;
			}
			fee = f;
		}
		return fee;
	}
	
	
	/*
	 * Create Inoive for current student's Session
	 */
	public Invoice createStudentInvoice(Student student) throws Exception {
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus status = u.getCurrentStudentStatus(student);
		Period period = status.getPeriod();
		List<InvoiceItem> items = new InvoiceUtil().getInvoiceItems(student, period);
		return createStudentInvoice(student, items, status.getSession());
	}
	
	/*
	 * Create Invoice given specific Session
	 */
	public Invoice createStudentInvoice(Student student, Session session) throws Exception {
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus status = u.getStudentStatus(student, session);
		Period period = status.getPeriod();
		List<InvoiceItem> items = new InvoiceUtil().getInvoiceItems(student, period);
		return createStudentInvoice(student, items, session);
	}
	
	
	/*
	 * Create Invoice given specific Session
	 */
	
	public Invoice createStudentInvoice(SponsorInvoice sponsorInvoice, Student student, Session session) throws Exception {
		return this.createStudentInvoice(sponsorInvoice, student, session, true);
	}
	
	public Invoice createStudentInvoice(SponsorInvoice sponsorInvoice, Student student, Session session, boolean deleteExist) throws Exception {
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus status = u.getStudentStatus(student, session);
		Period period = status.getPeriod();
		List<InvoiceItem> items = new InvoiceUtil().getInvoiceItems(student, period);
		return createStudentInvoice(sponsorInvoice, student, items, session, null, deleteExist);
	}
	
	public Invoice createStudentInvoice(SponsorInvoice sponsorInvoice, SponsorStudent s, Session session, boolean deleteExist) throws Exception {
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus status = u.getStudentStatus(s.getStudent(), session);
		Period period = status.getPeriod();
		List<InvoiceItem> items = new InvoiceUtil().getInvoiceItems(s.getStudent(), period);
		return createStudentInvoice(sponsorInvoice, s, items, session, null, deleteExist);
	}	
	
	public Invoice createStudentInvoice(Student student, List<InvoiceItem> items, Session session) throws Exception {
		return createStudentInvoice((SponsorInvoice) null, student, items, session, null);
	}
	
	public Invoice createStudentInvoice(Student student, List<InvoiceItem> items, Session session, Date invoiceDate) throws Exception {
		return createStudentInvoice((SponsorInvoice) null, student, items, session, invoiceDate);
	}	
	
	public Invoice createStudentInvoice(SponsorInvoice sponsorInvoice, Student student, List<InvoiceItem> items, Session session, Date invoiceDate) throws Exception {
		return this.createStudentInvoice(sponsorInvoice, student, items, session, invoiceDate, true);
	}
	
	public Invoice createStudentInvoice(SponsorInvoice sponsorInvoice, Student student, List<InvoiceItem> items, Session session, Date invoiceDate, boolean deleteExist) throws Exception {
		
		//this is to create a primary invoice
		//check whether an invoice has already been created for this session
		//note: each session shall only have one primary invoice
		String invoiceNo = "";
		boolean exist = false;
		
		Date date = invoiceDate != null ? invoiceDate : new Date();

		List<Invoice> list = db.list("select i from Invoice i where i.student.id = '" + student.getId() + "' and i.session.id = '" + session.getId() + "' and i.invoiceType = 1");
		if ( list.size() > 0 ) {
			exist = true;
			if ( deleteExist ) {
				System.out.println("[WARNING] " + list.size() + " invoice(s) will be deleted for " + student.getMatricNo());
				//let's capture it's invoice number first for recycle
				Invoice i = list.get(0);
				invoiceNo = i.getInvoiceNo();
				//delete the invoice(s)
				for ( Invoice in : list ) {
					try {
						deleteInvoice(in, "", false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		
		if ( exist && !deleteExist ) {
			return null;
		} else {
			if ( invoiceNo == null || "".equals(invoiceNo)) {
				BillingNumGenerator g = new BillingNumGenerator();
				invoiceNo = g.generateNumber(invoicePrefix);
			}
			
			MatricNumGenerator g = new MatricNumGenerator();
			
			//INVOICE REFERENCE NUMBER
			String pre = "KPT(T)CKT/812/& Jil.85(4)"; //use only by CUCMS
			
			String referenceNo = g.generateRefNumber(pre);
			
			db.begin();
			Invoice invoice = new Invoice();
			if ( sponsorInvoice != null ) {
				invoice.setSponsorInvoice(sponsorInvoice);
			}
			invoice.setInvoiceNo(invoiceNo);
			invoice.setReferenceNo(referenceNo);
			invoice.setCreateDate(date);
			invoice.setCreateTime(date);
			invoice.setSession(session);
			invoice.setStudent(student);
			invoice.setInvoiceType(1); //primary
			for ( InvoiceItem it : items ) {
				invoice.addInvoiceItem(it);
			}
			db.persist(invoice);
			db.commit();
			
			return invoice;
		}
	}
	
	public Invoice createStudentInvoice(SponsorInvoice sponsorInvoice, SponsorStudent sp, List<InvoiceItem> items, Session session, Date invoiceDate, boolean deleteExist) throws Exception {
		
		//this is to create a primary invoice
		//check whether an invoice has already been created for this session
		//note: each session shall only have one primary invoice
		String invoiceNo = "";
		boolean exist = false;
		
		Date date = invoiceDate != null ? invoiceDate : new Date();

		List<Invoice> list = db.list("select i from Invoice i where i.student.id = '" + sp.getStudent().getId() + "' and i.session.id = '" + session.getId() + "' and i.invoiceType = 1");
		if ( list.size() > 0 ) {
			exist = true;
			if ( deleteExist ) {
				System.out.println("[WARNING] " + list.size() + " invoice(s) will be deleted for " + sp.getStudent().getMatricNo());
				//let's capture it's invoice number first for recycle
				Invoice i = list.get(0);
				invoiceNo = i.getInvoiceNo();
				//delete the invoice(s)
				for ( Invoice in : list ) {
					try {
						deleteInvoice(in, "", false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		
		if ( exist && !deleteExist ) {
			return null;
		} else {
			if ( invoiceNo == null || "".equals(invoiceNo)) {
				BillingNumGenerator g = new BillingNumGenerator();
				invoiceNo = g.generateNumber(invoicePrefix);
			}
		
			db.begin();
			Invoice invoice = new Invoice();
			if ( sponsorInvoice != null ) {
				invoice.setSponsorInvoice(sponsorInvoice);
			}
			invoice.setInvoiceNo(invoiceNo);
			invoice.setReferenceNo(sp.getSponsorRefNo());
			invoice.setCreateDate(date);
			invoice.setCreateTime(date);
			invoice.setSession(session);
			invoice.setStudent(sp.getStudent());
			invoice.setInvoiceType(1); //primary
			for ( InvoiceItem it : items ) {
				invoice.addInvoiceItem(it);
			}
			db.persist(invoice);
			db.commit();
			
			return invoice;
		}
	}
	
	
	public Invoice createCustomStudentInvoice(SponsorInvoice sponsorInvoice, Student student, List<InvoiceItem> items, Session session) throws Exception {
		return createCustomStudentInvoice(sponsorInvoice, student, items, session, null, false);
	}
	
	public Invoice createCustomStudentInvoice(SponsorInvoice sponsorInvoice, Student student, List<InvoiceItem> items, Session session, Date invoiceDate) throws Exception {
		return createCustomStudentInvoice(sponsorInvoice, student, items, session, invoiceDate, false);
	}
	
	public Invoice createDebitNote(Student student, List<InvoiceItem> items, Session session, Date invoiceDate) throws Exception {
		return createCustomStudentInvoice((SponsorInvoice) null, student, items, session, invoiceDate, true);
	}
	
	public Invoice createDebitNote(Student student, List<InvoiceItem> items, Session session) throws Exception {
		return createCustomStudentInvoice((SponsorInvoice) null, student, items, session, null, true);
	}
	

	public Invoice createCustomStudentInvoice(SponsorInvoice sponsorInvoice, Student student, List<InvoiceItem> items, Session session, Date invoiceDate, boolean isDebitNote) throws Exception {
	
		BillingNumGenerator g = new BillingNumGenerator();
		String invoiceNo = isDebitNote ? g.generateNumber(debitNotePrefix) : g.generateNumber(invoicePrefix);
		
		db.begin();
		Invoice invoice = new Invoice();
		if ( sponsorInvoice != null ) {
			invoice.setSponsorInvoice(sponsorInvoice);
		}
		invoice.setInvoiceNo(invoiceNo);
		if ( invoiceDate != null ) {
			invoice.setCreateDate(invoiceDate);
		} else {
			invoice.setCreateDate(new Date());
		}
		invoice.setCreateTime(new Date());
		invoice.setSession(session);
		invoice.setStudent(student);
		invoice.setInvoiceType(2); //secondary
		invoice.setDebitNote(isDebitNote);
		for ( InvoiceItem i : items ) {
			invoice.addInvoiceItem(i);
		}
		db.persist(invoice);
		db.commit();
		
		return invoice;
	}
	
	
	public Invoice createFlexiPaymentInvoice(Student student, List<InvoiceItem> items, Session session, Date invoiceDate, PaymentScheduleItem scheduleItem) throws Exception {
		
		BillingNumGenerator g = new BillingNumGenerator();
		String invoiceNo = g.generateNumber(invoicePrefix);
		
		db.begin();
		Invoice invoice = new Invoice();
		invoice.setInvoiceNo(invoiceNo);
		if ( invoiceDate != null ) {
			invoice.setCreateDate(invoiceDate);
		} else {
			invoice.setCreateDate(new Date());
		}
		invoice.setCreateTime(new Date());
		invoice.setSession(session);
		invoice.setStudent(student);
		invoice.setInvoiceType(2); //secondary
		invoice.setDebitNote(false);
		invoice.setIsFlexi(true);
		invoice.setScheduleItemId(scheduleItem.getId());
		for ( InvoiceItem i : items ) {
			invoice.addInvoiceItem(i);
		}
		db.persist(invoice);
		db.commit();
		
		return invoice;
	}
	
	public Invoice createRefund(Student student, Session session, double refundAmount, String remark) throws Exception {
		
//		InvoiceNoBean ib = new InvoiceNoBean();
//		String invoiceNo = ib.genInvoiceNo(student.getProgram().getCode(), userId, "RF");
		
		BillingNumGenerator g = new BillingNumGenerator();
		String invoiceNo = g.generateNumber(refundPrefix);
		
		db.begin();
		Invoice invoice = new Invoice();
		invoice.setInvoiceNo(invoiceNo);
		invoice.setCreateDate(new Date());
		invoice.setCreateTime(new Date());
		invoice.setSession(session);
		invoice.setStudent(student);
		invoice.setInvoiceType(2); //secondary
		invoice.setDebitNote(false);
		invoice.setRemark(remark);
		invoice.setRefund(true);
		
		InvoiceItem i = new InvoiceItem();
		i.setAmount(refundAmount);
		i.setDescription("REFUND");
		
		invoice.addInvoiceItem(i);
		db.persist(invoice);
		db.commit();
		
		return invoice;
	}
	
	/*
	 * remove invoices for list of student for specific Session
	 */
	public void deleteInvoices(List<Student> students, Session session) throws Exception {
		
		for ( Student student : students ) {
			Invoice invoice = (Invoice) db.get("select i from Invoice i where i.student.id = '" + student.getId() + "' and i.session.id = '" + session.getId() + "'");
			deleteInvoice(invoice, "Deleted by list of students!");
		}
		
	}
	
	public void deleteInvoice(Invoice invoice, String deleteReason) throws Exception {
		deleteInvoice(invoice, deleteReason, true);
	}
	
	public void deleteInvoice(Invoice invoice, String deleteReason, boolean keepOld) throws Exception {
		
		if ( keepOld ) createXInvoice(invoice, deleteReason);
		
		if ( invoice != null ) {
			
			if ( invoice.getRefund() ) {
				//look in refund log
				RefundLog refundLog = (RefundLog) db.get("select r from RefundLog r where r.invoiceId = '" + invoice.getId() + "'");
				if ( refundLog != null ) {
					db.begin();
					refundLog.setCancelled(true);
					db.commit();
				}
			}
			
			Set<InvoiceItem> items = invoice.getInvoiceItems();
			for ( InvoiceItem item : items ) {
				db.begin();
				db.remove(item);
				db.commit();
			}
			db.begin();
			db.remove(invoice);
			db.commit();
		}
	}
	
	public void createXInvoice(Invoice i, String reason) throws Exception {
		//
		XInvoice x = new XInvoice();
		
		x.setDeleteDate(new Date());
		x.setDeleteTime(new Date());
		
		x.setUserId(userId);
		
		x.setDeleteReason(reason);
		x.setAmount(i.getAmount());
		x.setBalance(i.getBalance());
		x.setCreateDate(i.getCreateDate());
		x.setCreateTime(i.getCreateTime());
		x.setInvoiceItems(new HashSet<InvoiceItem>());

		for ( InvoiceItem it : i.getInvoiceItems() ) {
			InvoiceItem m = new InvoiceItem();
			m.setAmount(it.getAmount());
			m.setBalance(it.getBalance());
			m.setCourseFee(it.getCourseFee());
			m.setDescription(it.getDescription());
			if ( it.getFeeItem() != null ) m.setFeeItem(it.getFeeItem());
			x.getInvoiceItems().add(m);
		}
		x.setInvoiceNo(i.getInvoiceNo());
		x.setInvoiceType(i.getInvoiceType());
		x.setSession(i.getSession());
		if ( i.getSponsorInvoice() != null ) x.setSponsorInvoice(i.getSponsorInvoice());
		x.setStudent(i.getStudent());
		
		db.begin();
		db.persist(x);
		db.commit();
		
	}
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		
		String matricNo = "01060107";
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		StudentStatus currentStatus = new StudentStatusUtil().getCurrentStudentStatus(student);
		System.out.println("current status = " + currentStatus.getSession().getName() + ", " + currentStatus.getPastStatus());
		
		List<Invoice> invoices = db.list("select i from Invoice i where i.student.matricNo = '" + matricNo + "'");
		System.out.println(invoices.size());
		for ( Invoice i : invoices ) {
			if ( i.getSession().getStartDate().after(currentStatus.getSession().getEndDate())) {
				System.out.println(i.getCreateDate() + ", " + i.getSession().getName() + ", " + i.getAmount());
			}
		}
		
		
		
	}
	


}
