package educate.sis.finance.module;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;

public class AccountStatementUtil {
	
	DbPersistence db = new DbPersistence();
	
	
	public AccountStatement getAccountStatement(Student student) {
		return getAccountStatement(student, new Date());
	}
	
	public AccountStatement getAccountStatement(Student student, Date date) {
		Hashtable h = new Hashtable();
		if ( date != null ) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DATE, 1);
			h.put("date", c.getTime());
		}
		String sql = "";
		List<Record> records = new ArrayList<Record>();
		sql = "select new educate.sis.finance.module.R(i.createDate, i.invoiceNo, it.description, it.amount)" +
				" from Invoice i " +
				" join i.invoiceItems it " +
				" where i.student.id = '" + student.getId() + "' ";
		
		if ( date != null ) {
			sql += "and i.createDate < :date ";
		}
				
		sql += " order by i.createDate";
		List<R> rs = date == null ? db.list(sql) : db.list(sql, h);
		for ( R r : rs ) {
			records.add(new Record(r.getDate(), r.getRefNo(), r.getDescription(), r.getValue(), 0));
		}
		//
		sql = "select new educate.sis.finance.module.R(p.createDate, p.paymentNo, it.description, it.amount)" +
				" from Payment p " +
				" join p.paymentItems it " +
				" where p.student.id = '" + student.getId() + "' ";
		
		if ( date != null ) {
			sql += "and p.createDate < :date ";
		}
		
		sql += " order by p.createDate";
		rs = date == null ? db.list(sql) : db.list(sql, h);
		for ( R r : rs ) {
			records.add(new Record(r.getDate(), r.getRefNo(), r.getDescription(), r.getValue(), 1));
		}
		
		Collections.sort(records, new RecordComparator());
		
		
		double balance = 0.0d;
		double total1 = 0.0d; //debit
		double total2 = 0.0d; //credit
		for ( Record r : records ) {
			double amt = r.getAmount();
			if ( r.getSide() == 0 ) {
				balance += r.getAmount();
				total1 += r.getAmount();
			}
			else {
				balance -= r.getAmount();
				total2 += r.getAmount();
			}
			r.setBalance(balance);
		}
		
		double totalBalance = total1 - total2;
		double negate = totalBalance < 0.0d ? -1*totalBalance : totalBalance;
		if ( negate < 0.009 ) {
			totalBalance = 0.0;
		}
		
		
		AccountStatement as = new AccountStatement();
		as.setRecords(records);
		as.setTotalInvoiced(total1);
		as.setTotalPaid(total2);
		as.setTotalBalance(totalBalance);
		return as;
	}
	
	public double getAccountBalance(Student student) {
		boolean invoiceNull = false, paymentNull = false;
		double invoiceAmount = 0.0d;
		double paymentAmount = 0.0d;
		String sql = "";
		sql = "select SUM(i.amount) " +
				" from Invoice i " +
				" where i.student.id = '" + student.getId() + "' ";
		List<Double> list1 = db.list(sql);
		if ( list1.get(0) != null ) {
			invoiceAmount = list1.get(0);
		} else {
			System.out.println("Invoice Amount is NULL for " + student.getBiodata().getName());
			invoiceNull = true;
		}
		
		//
		sql = "select SUM(p.amount) " +
				" from Payment p " +
				" where p.student.id = '" + student.getId() + "' ";
		List<Double> list2 = db.list(sql);
		if ( list2.get(0) != null ) {
			paymentAmount = list2.get(0);
		} else {
			paymentNull = true;
		}
		
		double totalBalance = 0.0d;
		if ( !invoiceNull && !paymentNull ) {
			totalBalance = invoiceAmount - paymentAmount;
		}
		
		double negate = totalBalance < 0.0d ? -1*totalBalance : totalBalance;
		if ( negate < 0.009 ) {
			totalBalance = 0.0;
		}

		return totalBalance;
	}
	
	
	
	
	public static class RecordComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			Record r1 = (Record) o1;
			Record r2 = (Record) o2;
			int i = r1.getDate().compareTo(r2.getDate());

			if ( i == 0 ) {
				if ( r1.getSide() == r2.getSide()) return 0;
				else
					return r1.getSide() > r2.getSide() ? 1 : -1;
			}
			else
				return i;
		}
		
	}
	
	
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		Student student = (Student) db.get("select s from Student s where s.matricNo = '10080015'");
		AccountStatementUtil util = new AccountStatementUtil();
		AccountStatement acct = util.getAccountStatement(student);
		List<Record> records = acct.getRecords();
		
		for ( Record r : records ) {
			System.out.println(r.getSide() + " " + r.getAmount() + " - " + r.getBalance());
		}
		
	}
		

}
