package educate.sis.finance.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;

public class FinanceUtil {
	
	DbPersistence db = new DbPersistence();
	
	public AccountStatement getAccountStatement(Student student) throws Exception {
		String sql = "";
		List<Record> records = new ArrayList<Record>();
		sql = "select new educate.sis.finance.module.R(i.createDate, it.description, it.amount)" +
				" from Invoice i " +
				" join i.invoiceItems it " +
				" where i.student.id = '" + student.getId() + "' " +
				" order by i.createDate";
		List<R> rs = db.list(sql);
		for ( R r : rs ) {
			records.add(new Record(r.getDate(), r.getDescription(), r.getValue(), 0));
		}
		sql = "select new educate.sis.finance.module.R(p.createDate, it.invoiceItem.description, it.amount)" +
				" from Payment p " +
				" join p.paymentItems it " +
				" where p.student.id = '" + student.getId() + "' " +
				" order by p.createDate";
		rs = db.list(sql);
		for ( R r : rs ) {
			records.add(new Record(r.getDate(), r.getDescription(), r.getValue(), 1));
		}
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
		Collections.sort(records, new RecordComparator());
		
		AccountStatement ac = new AccountStatement();
		ac.setRecords(records);
		ac.setTotalInvoiced(total1);
		ac.setTotalPaid(total2);
		ac.setTotalBalance(totalBalance);
		
		return ac;
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

}
