/**
 * 
 */
package educate.sis.finance.module;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.Invoice;
import educate.sis.finance.entity.InvoiceItem;
import educate.sis.module.StudentInvoice;
import educate.sis.module.StudentStatusUtil;
import educate.sis.struct.entity.Session;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class GenerateMonthlyFeeInvoiceModule {
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		String matricNo = "0056DTM00100115FT";
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus currentStudentStatus = u.getCurrentStudentStatus(student);
		System.out.println(currentStudentStatus.getPeriod().getCode());
		System.out.println("===");
		
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(new Date());
		
		int currentMonth = currentDate.get(Calendar.MONTH);
		System.out.println("current month = " + currentMonth);
		
		
		List<StudentStatus> studentStatuses = u.getStudentStatuses(student);
		for ( StudentStatus studentStatus : studentStatuses ) {
			System.out.println(studentStatus.getPeriod().getCode() + ", " + studentStatus.getSession().getName());
			Session session = studentStatus.getSession();
			Date startDate = session.getStartDate();
			Date endDate = session.getEndDate();
			Calendar date1 = Calendar.getInstance();
			date1.setTime(startDate);
			Calendar date2 = Calendar.getInstance();
			date2.setTime(endDate);
			int month1 = date1.get(Calendar.MONTH);
			int year1 = date1.get(Calendar.YEAR);
			int month2 = date2.get(Calendar.MONTH);
			int year2 = date2.get(Calendar.YEAR);
			System.out.println(month1 + " - " + month2);
			
			
			int m = month1;
			int y = year1;
			while ( true ) {
				if ( m == 12 ) {
					m = 0;
					y = year2;
				}
				createInvoice(studentStatus, m, y);
				if ( m == month2 || m == currentMonth) break;
				m++;
			}
			
			if ( studentStatus.getId().equals(currentStudentStatus.getId())) break;
		}
		
	}

	/**
	 * @param studentStatus
	 * @param m
	 * @throws Exception 
	 */
	private static void createInvoice(StudentStatus studentStatus, int m, int y) throws Exception {
		System.out.println("create invoice for month " + m + ", " + y);
		
		Calendar date = Calendar.getInstance();
		date.set(y, m, 1);
		
		System.out.println("date = " + new SimpleDateFormat("dd-MM-yyyy").format(date.getTime()));
		
		/*
		InvoiceUtil util = new InvoiceUtil();
		List<InvoiceItem> items = util.getInvoiceMonthlyItems(studentStatus.getStudent(), studentStatus.getPeriod());
		if ( items.size() > 0 ) {
			System.out.println(studentStatus.getStudent().getMatricNo() + ", " + studentStatus.getSession().getName() + ", " + studentStatus.getPeriod().getCode());
			util.createStudentInvoice(studentStatus.getStudent(), items, studentStatus.getSession(), new Date());
		}
		*/
	}

	private static void testMonthlyInvoice() throws Exception, FeeStructureNotAvailableException {
		String programId = "1401960689912";
		DbPersistence db = new DbPersistence();
		List<Student> students = db.list("select s from Student s where s.program.id = '" + programId + "'");
		createMonthlyInvoice(db, students);
	}

	public static void createMonthlyInvoice(DbPersistence db, List<Student> students) throws Exception, FeeStructureNotAvailableException {
		StudentStatusUtil u = new StudentStatusUtil();
		InvoiceUtil util = new InvoiceUtil();
		for ( Student student : students ) {
			StudentStatus studentStatus = u.getCurrentStudentStatus(student);
			List<InvoiceItem> items = util.getInvoiceMonthlyItems(student, studentStatus.getPeriod());
			if ( items.size() > 0 ) {
				System.out.println(student.getMatricNo() + ", " + studentStatus.getSession().getName() + ", " + studentStatus.getPeriod().getCode());
				util.createStudentInvoice(student, items, studentStatus.getSession(), new Date());
			}
		}
	}

	public static StudentInvoice getInvoice(DbPersistence db, StudentStatus studentStatus, List<InvoiceItem> items) {
		StudentInvoice invoice = new StudentInvoice();
		invoice.setStudentStatus(studentStatus);
		invoice.setInvoiceItems(items);
		List<Invoice> invoices = db.list("select i from Invoice i where i.student.id = '" + studentStatus.getStudent().getId() + "' " +
		"and i.session.id = '" + studentStatus.getSession().getId() + "' order by i.createDate, i.createTime");
		invoice.setInvoices(invoices);
		return invoice;
	}

}
