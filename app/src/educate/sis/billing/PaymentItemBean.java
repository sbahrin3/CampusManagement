package educate.sis.billing;

import java.util.List;
import java.util.Vector;

import educate.sis.billing.entity.PaymentItem;
import lebah.db.PersistenceManager;

public class PaymentItemBean {
	private PersistenceManager pm;
	public PaymentItem  getPaymentItem(String code)throws Exception{
		pm = new PersistenceManager();
		List<PaymentItem> l = pm.list("SELECT a FROM educate.sis.billing.entity.PaymentItem a WHERE a.code ='"+code+"'");
		if(l.size() > 0){
			return l.get(0);
		}
		return null;
	}
	public Vector<PaymentItem> getPaymentType(String type1,String type2)throws Exception{
		Vector<PaymentItem> v = new Vector<PaymentItem>();
		pm = new PersistenceManager();
		List<PaymentItem> l = pm.list("SELECT a FROM educate.sis.billing.entity.PaymentItem a WHERE a.type ='"+type1+"' OR a.type='"+type2+"'");
		v.addAll(l);
		return v;
	}
	public PaymentItem getPaymentItem(String code,String description,String type) throws Exception{
		pm = new PersistenceManager();
		List<PaymentItem>l = pm.list("SELECT a FROM educate.sis.billing.entity.PaymentItem a WHERE a.code ='"+code+"'");
		if(l.size() > 0){
			return l.get(0);
		}else{
			PaymentItem item = new PaymentItem();
			item.setCode(code);
			item.setDescription(description);
			item.setType(type);
			PersistenceManager.add(item);
			return item;
		}
	}
}
