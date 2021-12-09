package portal;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.db.DbPersistence;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import upload.entity.Document;

public class UploadDocumentModule extends LebahModule {
	
	private String path = "uploadDocument";
	private DbPersistence db = new DbPersistence();

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
		String description = getParam("description");
		String uploadDir = "/Users/Admin/Documents/uploaded/";
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
			context.put("serverFileName", savedName);
			item.write(new File(savedName));
			System.out.println("... " + documentId);
			if ( "document".equals(documentId)) { 
				System.out.println("uploaded document!");
				System.out.println("description = " + description);
				Document d = new Document();
				db.begin();
				d.setFileName(fileName);
				db.commit();
				

				break;
			}
		}
		
		return path + "/uploaded.vm";
	}

}
