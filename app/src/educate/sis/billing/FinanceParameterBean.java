package educate.sis.billing;
import java.util.List;

import educate.sis.billing.entity.FinanceParameter;
import lebah.db.PersistenceManager;

public class FinanceParameterBean {

	private FinanceParameter financeParameter;
	private PersistenceManager pm;
	
	public void addNew(FinanceParameter fp)throws Exception{
		pm = new PersistenceManager();
		PersistenceManager.add(fp);
	}
	
	public void delete(String id)throws Exception{
		pm = new PersistenceManager();
		financeParameter = (FinanceParameter)pm.find(FinanceParameter.class, id);
		pm.delete(financeParameter);
	}
	
	public List<FinanceParameter> getList()throws Exception{
		pm = new PersistenceManager();
		List<FinanceParameter>list=pm.list("select a from FinanceParameter a");
		return list;
	}
	
	public void update(String id, FinanceParameter fp)throws Exception{
		pm = new PersistenceManager();
		financeParameter = (FinanceParameter)pm.find(FinanceParameter.class).whereId(id).forUpdate();
		financeParameter.setDicountExpiryDate(fp.getDicountExpiryDate());
		financeParameter.setSession(fp.getSession());
		financeParameter.setRebateAmount(fp.getRebateAmount());
		financeParameter.setDiscountPercentage(fp.getDiscountPercentage());
		pm.update();
	}
	
	public FinanceParameter getData(String id)throws Exception{
		pm = new PersistenceManager();
		financeParameter = (FinanceParameter)pm.find(FinanceParameter.class, id);
		return financeParameter;
	}
	

	
	public FinanceParameter getParameter(String session_id)throws Exception{
		pm = new PersistenceManager();
		List<FinanceParameter> param = pm.list("SELECT a FROM FinanceParameter a WHERE a.session.id='"+session_id+"'");
		
		if(param.size()>0){
			return param.get(0);
		}else{
			throw new Exception("Finance Parameter not setup for session id"+session_id);
		}
	
	}
	

}
