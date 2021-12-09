package educate.sis.finance.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.sis.finance.entity.Invoice;
import educate.sis.finance.entity.Payment;

public class X {
	
	private Map<String, List> paymentMap = new HashMap<String, List>();
	private Map<String, List> invoiceMap = new HashMap<String, List>();
	private Map<String, Double> paid = new HashMap<String, Double>();
	private Map<String, Double> invoiced = new HashMap<String, Double>();
	
	public X() {
		
	}
	
	public void putPayment(Payment p) {
		if ( paymentMap.containsKey(p.getStudent().getId()) ) {
			List<Payment> list = paymentMap.get(p.getStudent().getId());
			list.add(p);
		} 
		else {
			List<Payment> list = new ArrayList<Payment>();
			list.add(p);
			paymentMap.put(p.getStudent().getId(), list);
		}
	}
	
	public void putInvoice(Invoice i) {
		if ( i.getStudent() != null ) {
			if ( invoiceMap.containsKey(i.getStudent().getId()) ) {
				List<Invoice> list = invoiceMap.get(i.getStudent().getId());
				list.add(i);
			} 
			else {
				List<Invoice> list = new ArrayList<Invoice>();
				list.add(i);
				invoiceMap.put(i.getStudent().getId(), list);
			}
		}
	}

	public Map<String, List> getInvoiceMap() {
		return invoiceMap;
	}

	public Map<String, List> getPaymentMap() {
		return paymentMap;
	}

	
	

}
