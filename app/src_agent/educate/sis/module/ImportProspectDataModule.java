package educate.sis.module;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.db.DbPersistence;
import educate.poi.Excel;
import educate.poi.ExcelDocument;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ImportProspectDataModule extends LebahModule {

	private DbPersistence db = new DbPersistence();
	private String path = "admission/importProspectData";

	@Override
	public String start() {
		return path + "/start.vm";
	}
	
	@Command("uploadFile")
	public String uploadFile() throws Exception {
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		
		String documentId = getParam("documentId");
		String uploadDir = "/Users/Admin/Documents/";
		
    	ResourceBundle rb;
    	try {
    		rb = ResourceBundle.getBundle("files");
    	} catch (java.util.MissingResourceException ex) {
    		throw new Exception("Unable to find files.properties file.");
    	}
    	uploadDir = rb.getString("prospectDataLocation");
    	if ( !uploadDir.endsWith("/")) uploadDir += "/";
		
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
			context.put("UploadFileName", savedName);
			request.getSession().setAttribute("UploadFileName", savedName);
			item.write(new File(savedName));
		}
		
		return path + "/uploaded.vm";
	}
	
	//Name	IC	Address	Address	postcode	city	state	Phone no.	house no.	Course	Email
	
	@Command("displayData")
	public String displayData() throws Exception {
		String fileName = (String) request.getSession().getAttribute("UploadFileName");
		
		Excel excel = ExcelDocument.getDocument(fileName);
		
		List<Map<String, String>> items = new ArrayList<Map<String, String>>();
		context.put("items", items);
		request.getSession().setAttribute("items", items);
		for ( int i=0; i < excel.getRowSize(); i++ ) {
			if ( i > 0 ) {
				Map<String, String> m = new HashMap<String, String>();
			
				
				String name = excel.getStringValue(i, 0);
				String icno = excel.getStringValue(i, 1);
				String address1 = excel.getStringValue(i, 2);
				String address2 = excel.getStringValue(i, 3);
				String postcode = excel.getPostcodeValue(i, 4);
				String city = excel.getStringValue(i, 5);
				String state = excel.getStringValue(i, 6);
				String phone = excel.getStringValue(i, 7);
				String houseNo = excel.getStringValue(i, 8);
				String course = excel.getStringValue(i, 9);
				String email = excel.getStringValue(i, 10);
				
				
				m.put("name", name);
				m.put("icno", icno);
				m.put("address1", address1);
				m.put("address2", address2);
				m.put("postcode", postcode);
				m.put("city", city);
				m.put("state", state);
				m.put("phone", phone);
				m.put("houseNo", houseNo);
				m.put("course", course);
				m.put("email", email);
				
				items.add(m);
			}
		}
		context.put("totalCnt", items.size());
		return path + "/displayData.vm";
	}
	
	public static void main(String[] args) {
		String fileName = "abcefg.xls";
		String extension = fileName.substring(fileName.lastIndexOf("."));
		System.out.println(extension);
		
		String fileName2 = "abcefg.xlsx";
		String extension2 = fileName2.substring(fileName2.lastIndexOf("."));
		System.out.println(extension2);
	}
}
