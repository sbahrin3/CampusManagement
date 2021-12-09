package educate.sis.finance.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.finance.module.StudentAcctStatementModule.RecordComparator;

public class StatementAccountUtil {
	
	private double totalInvoiced = 0.0d; //debit
	private double totalPaid = 0.0d; //credit
	private double totalBalance = 0.0d;
	List<Record> records = new ArrayList<Record>();
	
	public void prepare(Student student) {
		DbPersistence db = new DbPersistence();
		String sql = "";
		
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
		for ( Record r : records ) {
			//double amt = r.getAmount();
			if ( r.getSide() == 0 ) {
				balance += r.getAmount();
				totalInvoiced += r.getAmount();
			}
			else {
				balance -= r.getAmount();
				totalPaid += r.getAmount();
			}
			r.setBalance(balance);
		}
		totalBalance = totalInvoiced - totalPaid;
		Collections.sort(records, new RecordComparator());		
	}

	public double getTotalBalance() {
		return totalBalance;
	}

	public double getTotalInvoiced() {
		return totalInvoiced;
	}

	public double getTotalPaid() {
		return totalPaid;
	}

	public List<Record> getRecords() {
		return records;
	}
	
	

}
