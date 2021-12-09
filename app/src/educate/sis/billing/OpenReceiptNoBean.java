package educate.sis.billing;

import educate.sis.billing.entity.OpenReceiptNo;
import lebah.db.PersistenceManager;

public class OpenReceiptNoBean {
	private OpenReceiptNo receipt;
	public String genNo()throws Exception{
		PersistenceManager pm = new PersistenceManager();
		receipt = (OpenReceiptNo)pm.find(OpenReceiptNo.class,"1");
		if(receipt == null){
			newReceiptNo();
		}
		int no = receipt.getCurrent()+1;
		String gen = receipt.getPrefix()+"/"+noFormat(receipt.getSize(), no);
		try{
			receipt = (OpenReceiptNo)pm.find(OpenReceiptNo.class).whereId("1").forUpdate();
			receipt.setCurrent(no);
			pm.update();
		}
		catch(Exception e){
			pm.rollback();
		}
		return gen;
		
	}
	private String noFormat(int num_size,int current_num){
		 char[] cf = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',};
		 return new java.text.DecimalFormat(new String(cf, 0, num_size)).format(current_num);
	}
	private void newReceiptNo() throws Exception {
		PersistenceManager pm = new PersistenceManager();
		receipt = new OpenReceiptNo();
		receipt.setId("1");
		receipt.setPrefix("OR");
		receipt.setSize(6);
		receipt.setCurrent(0);
		PersistenceManager.add(receipt);
		
	}
}
