package migration.module;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.poi.Excel1;
import educate.sis.finance.entity.Payment;
import educate.sis.finance.entity.PaymentItem;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ImportPaymentModule  extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "migration/payment/xl";



	
	public void preProcess() {
		System.out.println(command);
	}

	@Override
	public String start() {
		// TODO Auto-generated method stub
		return path + "/start.vm";
	}
	
	@Command("uploadFile")
	public String uploadFile() throws Exception {
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		
		String documentId = getParam("documentId");
		String uploadDir = "/Users/Admin/Documents/xls/";
		
    	ResourceBundle rb;
    	try {
    		rb = ResourceBundle.getBundle("files");
    	} catch (java.util.MissingResourceException ex) {
    		throw new Exception("Unable to find files.properties file.");
    	}
    	uploadDir = rb.getString("examResultLocation");
		
		File dir = new File(uploadDir);
		if ( !dir.exists() ) dir.mkdir();
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = upload.parseRequest(request);
		Iterator itr = items.iterator();
		List<FileItem> files = new ArrayList<FileItem>();
		while (itr.hasNext()) {
			FileItem item = (FileItem)itr.next();
			if ((!(item.isFormField())) && (item.getName() != null) && (!("".equals(item.getName())))) {
				if ( documentId.equals(item.getFieldName())) {
					files.add(item);
				}
			}
		}
		
		List<String> filenames = new ArrayList<String>();
		context.put("filenames", filenames);
		
		for ( FileItem item : files ) {
			String fileName = item.getName();
			filenames.add(fileName);
			String savedName = uploadDir + fileName;
			context.put("XLSFileName", savedName);
			request.getSession().setAttribute("XLSFileName", savedName);
			item.write(new File(savedName));
		}
		
		return path + "/uploaded.vm";
	}	
	
	@Command("displayData")
	public String displayData() throws Exception {
		String fileName = (String) request.getSession().getAttribute("XLSFileName");
		Excel1 excel = new Excel1(fileName);
		List<Item> items = new ArrayList<Item>();
		context.put("items", items);
		request.getSession().setAttribute("items", items);
		for ( int i=0; i < excel.getRowSize(); i++ ) {
			if ( i > 0 ) {
				String matricNo = excel.getString(i, 0);
				String paymentNo = excel.getString(i, 1);
				String paymentDate = excel.getString(i, 2);
				String paymentDescription = excel.getString(i, 3);
				String paymentMode = excel.getString(i, 4);
				String checkNo = excel.getString(i, 5);
				String bankName = excel.getString(i, 6);
				double amount = 0.0d;
				try {
					amount = excel.getDouble(i, 7);
				} catch ( Exception e ) {
					e.printStackTrace();
				}
				
				Item d = new Item(matricNo, paymentNo, paymentDate, paymentDescription, paymentMode, checkNo, bankName, amount);
				items.add(d);
			}
		}
		context.put("totalCnt", items.size());
		return path + "/displayData.vm";
	}
	

	@Command("saveItem")
	public String saveItem() throws Exception {
		int cnt = Integer.parseInt(getParam("cnt"));
		int totalCnt = 0;
		try {
			totalCnt = Integer.parseInt(getParam("totalCnt"));
		} catch ( Exception e ) { }
		
		
		List<Item> results = (List<Item>) request.getSession().getAttribute("items");
		Item item = results.get(cnt-1);
		String matricNo = item.getMatricNo();
		String receiptNo = item.getItemNo();
		String receiptDate = item.getItemDate();
		String itemDescription = item.getItemDescription();
		String paymentMode = item.getPaymentMode();
		String checkNo = item.getCheckNo();
		String bankName = item.getBankName();
		double amount = item.getAmount();
		
		Student student = null;
		
		boolean proceed = true;
		student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( student == null ) {
			System.out.println("Can't find student: " + matricNo);
			context.put("import_status", "ERR: matricNo");
			proceed = false;
		}

		
		if ( proceed ) {
			
			String sep = !"".equals(getParam("dateSeparator")) ? getParam("dateSeparator") : "-";
			String dateStyle = "dd" + sep + "MM" + sep + "yyyy";
			
			
			Date date = null;
			try {
				date = new SimpleDateFormat(dateStyle).parse(receiptDate);
			} catch ( Exception e ) {
				//
			}
			if ( date == null ) date = new Date();
			
			Payment payment = null;
			//find invoice given invoice no
			payment = (Payment) db.get("select i from Payment i where i.paymentNo = '" + receiptNo + "' and i.student.id = '" + student.getId() + "'");
			
			String clearData = getParam("clearData");
			if ( "yes".equals(clearData)) {
				db.begin();
				db.remove(payment);
				db.commit();
				payment = null;
			}
			
			if ( payment == null ) {
				db.begin();
				payment = new Payment();
				payment.setStudent(student);
				payment.setPaymentNo(receiptNo);
				payment.setCreateDate(date);
				payment.setBankName(bankName);
				payment.setPaymentMode(paymentMode);
				payment.setCheckNo(checkNo);
				db.persist(payment);
				db.commit();
			}
			
			
			db.begin();
			PaymentItem paymentItem = new PaymentItem();
			paymentItem.setAmount(amount);
			paymentItem.setDescription(itemDescription);
			paymentItem.setPayment(payment);
			payment.addPaymentItems(paymentItem);
			db.commit();
			
			context.put("import_status", "OK");
		}
		
		if ( cnt < totalCnt ) {
			int nextCnt = cnt + 1;
			context.put("nextCnt", nextCnt);
			return path + "/saveItem.vm";
		} else {
			return path + "/saveItemDone.vm";
		}
	}
	


}
