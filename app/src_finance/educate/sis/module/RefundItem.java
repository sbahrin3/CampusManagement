package educate.sis.module;

import java.util.ArrayList;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.StatusType;
import educate.enrollment.entity.Student;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Session;

public class RefundItem {
	
	private Student student;
	private Period period;
	private Session session;
	private StatusType status;
	private double amount;
	private double agingPaymentAmount1;
	private double agingPaymentAmount2;
	private double agingPaymentAmount3;
	
	
	public RefundItem(Student s, Period p, Session ses, StatusType t, double d) {
		this.student = s;
		this.period = p;
		this.amount = d;
		this.session = ses;
		this.status = t;
	}
	
	public RefundItem(Student s, Period p, Session ses, StatusType t, double d, double agingPaymentAmount) {
		this.student = s;
		this.period = p;
		this.amount = d;
		this.session = ses;
		this.status = t;
		this.agingPaymentAmount1 = agingPaymentAmount;
	}
	
	public RefundItem(Student s, Period p, Session ses, StatusType t, double d, double agingPaymentAmount1, double agingPaymentAmount2, double agingPaymentAmount3) {
		this.student = s;
		this.period = p;
		this.amount = d;
		this.session = ses;
		this.status = t;
		this.agingPaymentAmount1 = agingPaymentAmount1;
		this.agingPaymentAmount2 = agingPaymentAmount2;
		this.agingPaymentAmount3 = agingPaymentAmount3;
	}

	public double getAmount() {
		return amount;
	}


	public Student getStudent() {
		return student;
	}
	
	public Period getPeriod() {
		return period;
	}

	public Session getSession() {
		return session;
	}

	public StatusType getStatus() {
		return status;
	}

	public double getAgingPaymentAmount() {
		return agingPaymentAmount1;
	}

	public void setAgingPaymentAmount(double agingPaymentAmount) {
		this.agingPaymentAmount1 = agingPaymentAmount;
	}

	public double getAgingPaymentAmount1() {
		return agingPaymentAmount1;
	}

	public void setAgingPaymentAmount1(double agingPaymentAmount1) {
		this.agingPaymentAmount1 = agingPaymentAmount1;
	}

	public double getAgingPaymentAmount2() {
		return agingPaymentAmount2;
	}

	public void setAgingPaymentAmount2(double agingPaymentAmount2) {
		this.agingPaymentAmount2 = agingPaymentAmount2;
	}

	public double getAgingPaymentAmount3() {
		return agingPaymentAmount3;
	}

	public void setAgingPaymentAmount3(double agingPaymentAmount3) {
		this.agingPaymentAmount3 = agingPaymentAmount3;
	}
	
	public static List<RefundItem> getRefundList(DbPersistence db, String programId, String intakeId) {
		String sql1 = "select new educate.sis.module.SAmount(s.matricNo, SUM(i.amount), i.invoice.student) " +
		"from educate.sis.finance.entity.InvoiceItem i join i.invoice.student s ";
		String w = "";
		//if ( !"".equals(sessionId)) w += ("".equals(w) ? " where " : " and ") + " st.session.id = '" + sessionId + "' ";
		if ( !"".equals(programId)) w += ("".equals(w) ? " where " : " and ") + " s.program.id = '" + programId + "' ";
		if ( !"".equals(intakeId)) w += ("".equals(w) ? " where " : " and ") + " s.intake.id = '" + intakeId + "' ";
		//if ( "active".equals(listType)) w += ("".equals(w) ? " where " : " and ") + " st.type.inActive = 0 ";
		sql1 = sql1 + w + " group by s.id";
		
		String sql2 = "select new educate.sis.module.SAmount(s.matricNo, SUM(i.amount), i.payment.student) " +
		"from educate.sis.finance.entity.PaymentItem i join i.payment.student s ";
		w = "";
		//if ( !"".equals(sessionId)) w += ("".equals(w) ? " where " : " and ") + " st.session.id = '" + sessionId + "' ";
		if ( !"".equals(programId)) w += ("".equals(w) ? " where " : " and ") + " s.program.id = '" + programId + "' ";
		if ( !"".equals(intakeId)) w += ("".equals(w) ? " where " : " and ") + " s.intake.id = '" + intakeId + "' ";
		//if ( "active".equals(listType)) w += ("".equals(w) ? " where " : " and ") + " st.type.inActive = 0 ";
		sql2 = sql2 + w + " group by s.id";
		
		List<SAmount> list1 = db.list(sql1);
		//System.out.println("Invoice List: " + list1.size());
		List<SAmount> list2 = db.list(sql2);
		//System.out.println("Payment List: " + list2.size());
		List<RefundItem> students = new ArrayList<RefundItem>();
		for (SAmount s2 : list2 ) {
			if ( list1.contains(s2)) {
				int i = list1.indexOf(s2);
				SAmount s1 = list1.get(i);
				double balance = s1.getAmount() - s2.getAmount();
				//System.out.println(s1.getStudent().getBiodata().getName() + ", " + s1.getAmount() + ", " + s2.getAmount());
				if ( balance < 0 &&  -1*balance < 0.009 ) balance = 0; //to ignore very small negative amount
				if ( balance < 0 ) {
					students.add(new RefundItem(s1.getStudent(), s1.getPeriod(), s1.getSession(), s1.getStatus(), -1*balance));
				}
			}
		}
		return students;
	}	
	

}
