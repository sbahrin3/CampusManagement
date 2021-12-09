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
import educate.enrollment.entity.StudentStatus;
import educate.poi.Excel1;
import educate.sis.finance.entity.FeeItem;
import educate.sis.finance.entity.Invoice;
import educate.sis.finance.entity.InvoiceItem;
import educate.sis.struct.entity.Session;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ImportInvoiceModule  extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "migration/invoice/xl";

	
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
				String sessionCode = excel.getString(i, 1);
				String invoiceNo = excel.getString(i, 2);
				String invoiceDate = excel.getString(i, 3);
				String feeCode = excel.getString(i, 4);
				String itemDescription = excel.getString(i, 5);
				double amount = 0.0d;
				try {
					amount = excel.getDouble(i, 6);
				} catch ( Exception e ) {
					e.printStackTrace();
				}
				
				Item d = new Item(matricNo, sessionCode, invoiceNo, invoiceDate, feeCode, itemDescription, amount);
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
		String sessionCode = item.getSessionCode();
		String invoiceNo = item.getItemNo();
		String invoiceDate = item.getItemDate();
		String feeCode = item.getItemCode();
		String itemDescription = item.getItemDescription();
		double amount = item.getAmount();
		
		Student student = null;
		Session session = null;
		FeeItem feeItem = null;
		
		boolean proceed = true;
		student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( student == null ) {
			System.out.println("Can't find student: " + matricNo);
			context.put("import_status", "ERR: matricNo");
			proceed = false;
		}
		if ( proceed ) {
			feeItem = (FeeItem) db.get("select s from FeeItem s where s.code = '" + feeCode + "'");
			if ( feeItem == null ) {
				System.out.println("Can't find fee item: " + feeCode);
				//context.put("import_status", "ERR: feeCode");
				//ignore, just proceed
			}
		}
		if ( proceed ) {
			session = (Session) db.get("select s from Session s where s.code = '" + sessionCode + "'");
			if ( session == null ) {
				System.out.println("Can't find session: " + sessionCode);
				context.put("import_status", "ERR: sessionCode");
				proceed = false;
			}
		}
		if ( proceed ) {
			StudentStatus studentStatus = (StudentStatus) db.get("select s from StudentStatus s where s.student.id = '" + student.getId() + "' and s.session.id = '" + session.getId() + "'");
			if ( studentStatus == null ) {
				System.out.println("Can't find student status: " + matricNo + ", " + sessionCode);
				context.put("import_status", "ERR: studentStatus");
				proceed = false;
			}
		}
		
		
		
		if ( proceed ) {
			
			String sep = !"".equals(getParam("dateSeparator")) ? getParam("dateSeparator") : "-";
			String dateStyle = "dd" + sep + "MM" + sep + "yyyy";
			
			Date date = null;
			try {
				date = new SimpleDateFormat(dateStyle).parse(invoiceDate);
			} catch ( Exception e ) {
				//
			}
			if ( date == null ) date = new Date();
			
			Invoice invoice = null;
			//find invoice given invoice no
			invoice = (Invoice) db.get("select i from Invoice i where i.invoiceNo = '" + invoiceNo + "' and i.student.id = '" + student.getId() + "'");
			
			String clearData = getParam("clearData");
			if ( "yes".equals(clearData)) {
				db.begin();
				db.remove(invoice);
				db.commit();
				invoice = null;
			}
			
			if ( invoice == null ) {
				db.begin();
				invoice = new Invoice();
				invoice.setStudent(student);
				invoice.setInvoiceNo(invoiceNo);
				invoice.setCreateDate(date);
				invoice.setSession(session);
				db.persist(invoice);
				db.commit();
			}
			
			
			db.begin();
			InvoiceItem invoiceItem = new InvoiceItem();
			invoiceItem.setAmount(amount);
			invoiceItem.setDescription(itemDescription);
			invoiceItem.setFeeItem(feeItem);
			invoiceItem.setInvoice(invoice);
			if ( feeItem != null ) invoiceItem.setFeeItem(feeItem);
			invoice.addInvoiceItem(invoiceItem);
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
