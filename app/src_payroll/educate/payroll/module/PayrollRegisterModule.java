/**
 * 
 */
package educate.payroll.module;

import educate.db.DbPersistence;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class PayrollRegisterModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/payroll/payrolls";

	/* (non-Javadoc)
	 * @see lebah.portal.action.LebahModule#start()
	 */
	@Override
	public String start() {
		return path + "/start.vm";
	}
	
	@Command("listPayrolls")
	public String listPayrolls() throws Exception {
		
		return path + "/listPayrolls.vm";
	}

}
