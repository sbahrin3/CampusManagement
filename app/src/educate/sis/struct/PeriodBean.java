package educate.sis.struct;

import educate.sis.struct.entity.PeriodScheme;
import lebah.db.PersistenceManager;

public class PeriodBean {
	private PersistenceManager pm;
	private educate.sis.struct.entity.PeriodScheme period;
	
	public void add(PeriodScheme p) throws Exception{
		pm = new PersistenceManager();
		PersistenceManager.add(p);
	}
	
	public PeriodScheme get(String id)throws Exception{
		period = (PeriodScheme)pm.find(PeriodScheme.class, id);
		return period;
	}
}
