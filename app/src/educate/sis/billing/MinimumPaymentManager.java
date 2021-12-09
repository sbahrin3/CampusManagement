package educate.sis.billing;

import educate.sis.billing.entity.MinimumPayment;
import lebah.db.PersistenceManager;

public class MinimumPaymentManager {
	private static MinimumPaymentManager minimum;
	private MinimumPaymentManager(){
		getMinimum();
	}
	public static synchronized  MinimumPaymentManager getInstance(){
		if(minimum == null){
			minimum = new MinimumPaymentManager();
		}
		return minimum;
	}
	private  MinimumPayment getMinimum(){
		PersistenceManager pm = new PersistenceManager();
		MinimumPayment value = null;
		try{
			value = (MinimumPayment)pm.find(MinimumPayment.class,"1");
			if(value == null){
				value = new MinimumPayment();
				value.setId("1");
				//value.setAmount(25);
				value.setAmount(500); // Minimum payment as of 2009-09-11. Shaiful Nizam.
				PersistenceManager.add(value);
			}
			
		}catch(Exception e){
			
		}
		return value;
	}
	public double getAmount()throws Exception{
		MinimumPayment mp = getMinimum();
		return mp.getAmount();
	}
	public void updateAmount(double amount)throws Exception{
		PersistenceManager pm = new PersistenceManager();
		MinimumPayment value = (MinimumPayment)pm.find(MinimumPayment.class).whereId("1").forUpdate();
		value.setAmount(amount);
		pm.update();
	}
}
