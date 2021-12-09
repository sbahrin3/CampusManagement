package educate.sis.billing;

import educate.db.DbPersistence;
import educate.sis.billing.entity.ReceiptNo;
import lebah.db.PersistenceManager;

public class ReceiptNoBean {
	private PersistenceManager pm;
	private ReceiptNo no;
	
	public ReceiptNoBean() throws Exception {
		pm = new PersistenceManager();
		no = (ReceiptNo)pm.find(ReceiptNo.class,"1");
		if(no==null){
			no = new ReceiptNo();
			no.setId("1");
			no.setPrefix("RB");
			no.setSize(4);
			no.setCurrent(0);
			PersistenceManager.add(no);
		}
	}
	
	public String getReceiptNo(String programCode,String userId)throws Exception{
		pm = new PersistenceManager();
		no = (ReceiptNo)pm.find(ReceiptNo.class,"1");
		return no.getPrefix()+invoiceNoFormat(no.getSize(), no.getCurrent()+1)+"/"+userId;
	}
	public String invoiceNoFormat(int num_size,int current_num){
		 char[] cf = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',};
		 return new java.text.DecimalFormat(new String(cf, 0, num_size)).format(current_num);
	}
	public ReceiptNo get(String id) throws Exception{
		pm = new PersistenceManager();
		no =(ReceiptNo) pm.find(ReceiptNo.class,id);
		return no;
	}
	public String genReceiptNo(String programCode, String userId)throws Exception{
		pm = new PersistenceManager();
		no =(ReceiptNo) pm.find(ReceiptNo.class, "1");
		int currentNo = no.getCurrent()+1;
		String gen = no.getPrefix() + invoiceNoFormat(no.getSize(),currentNo);
		try{
			no = (ReceiptNo) pm.find(ReceiptNo.class).whereId("1").forUpdate();
			no.setCurrent(currentNo);
			pm.update();
		}catch(Exception e){
			System.err.println(e.getMessage());
			pm.rollback();
		}
		System.err.println("generating receipt no success:"+gen);
		return gen;		
		
	}
	
	public synchronized String genReceiptNo(String prefix, String programCode, String userId)throws Exception{
		pm = new PersistenceManager();
		no =(ReceiptNo) pm.find(ReceiptNo.class, "1");
		int currentNo = no.getCurrent()+1;
		String gen = prefix + invoiceNoFormat(no.getSize(),currentNo);
		try{
			no = (ReceiptNo) pm.find(ReceiptNo.class).whereId("1").forUpdate();
			no.setCurrent(currentNo);
			pm.update();
		}catch(Exception e){
			System.err.println(e.getMessage());
			pm.rollback();
		}
		System.err.println("generating receipt no success:"+gen);
		return gen;		
		
	}
	
	public String genReceiptNo(DbPersistence pm,String programCode,String userId){
		no =pm.find(ReceiptNo.class,"1");
		int currentNo = no.getCurrent()+1;
		String gen = no.getPrefix()+"/"+programCode+"/"+invoiceNoFormat(no.getSize(),currentNo)+"/"+userId;
		no.setCurrent(currentNo);
		return gen;
	}
	
	public static void main(String[] args) throws Exception {
		ReceiptNoBean no = new ReceiptNoBean();
		
	}
	
}
