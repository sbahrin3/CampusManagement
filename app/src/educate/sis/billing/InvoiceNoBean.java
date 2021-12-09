package educate.sis.billing;

import educate.db.DbPersistence;
import educate.sis.billing.entity.InvoiceNo;
import lebah.db.PersistenceManager;

public class InvoiceNoBean {
	private PersistenceManager pm;
	private InvoiceNo no;
	
	public InvoiceNoBean() throws Exception {
		pm = new PersistenceManager();
		no = (InvoiceNo)pm.find(InvoiceNo.class,"1");
		if(no==null){
			no = new InvoiceNo();
			no.setId("1");
			no.setPrefix("INV");
			no.setSize(4);
			no.setCurrent(0);
			PersistenceManager.add(no);
		}
	}
	
//	public String getInvoiceNo(String programCode,String userId)throws Exception{
//		pm = new PersistenceManager();
//		no = (InvoiceNo)pm.find(InvoiceNo.class,"1");
//		return no.getPrefix()+"/"+programCode+"/"+invoiceNoFormat(no.getSize(), no.getCurrent()+1)+"/"+userId;
//	}
	public String invoiceNoFormat(int num_size,int current_num){
		 char[] cf = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',};
		 return new java.text.DecimalFormat(new String(cf, 0, num_size)).format(current_num);
	}
	public InvoiceNo get(String id) throws Exception{
		pm = new PersistenceManager();
		no =(InvoiceNo) pm.find(InvoiceNo.class,id);
		return no;
	}
	public String genInvoiceNo(String programCode, String userId)throws Exception{
		pm = new PersistenceManager();
		no = get("1");
		int currentNo = no.getCurrent()+1;
		String gen = no.getPrefix() + invoiceNoFormat(no.getSize(),currentNo);
		try{
			no = (InvoiceNo) pm.find(InvoiceNo.class).whereId("1").forUpdate();
			no.setCurrent(currentNo);
			pm.update();
		}
		catch(Exception e){
			System.err.println(e.getMessage());
			pm.rollback();
		}
		return gen;		
		
	}
	
	public String genInvoiceNo(String programCode, String userId, String prefix)throws Exception{
		pm = new PersistenceManager();
		no = get("1");
		int currentNo = no.getCurrent()+1;
		String gen = prefix + invoiceNoFormat(no.getSize(),currentNo);
		try{
			no = (InvoiceNo) pm.find(InvoiceNo.class).whereId("1").forUpdate();
			no.setCurrent(currentNo);
			pm.update();
		}
		catch(Exception e){
			System.err.println(e.getMessage());
			pm.rollback();
		}
		return gen;		
		
	}
	
	public String genInvoiceNo(DbPersistence pm,String programCode,String userId)throws Exception{
		no = pm.find(InvoiceNo.class,"1");
		int currentNo = no.getCurrent()+1;
		String gen = no.getPrefix()+"/"+programCode+"/"+invoiceNoFormat(no.getSize(),currentNo)+"/"+userId;
		try{
			no = pm.find(InvoiceNo.class,"1");
			no.setCurrent(currentNo);
		
		}
		catch(Exception e){
			System.err.println(e.getMessage());
			pm.rollback();
		}
		return gen;		
		
	}
}
