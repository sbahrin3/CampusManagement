/**
 * 
 */
package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import educate.db.DbPersistence;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class StudentMakePaymentModule extends LebahModule {
	
	private String path = "apps/finance/studentMakePayment";
	private DbPersistence db = new DbPersistence();


	/* (non-Javadoc)
	 * @see lebah.portal.action.LebahModule#start()
	 */
	@Override
	public String start() {
		return path + "/start.vm";
	}
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("numFormat", new DecimalFormat("#.00"));
	}

}
