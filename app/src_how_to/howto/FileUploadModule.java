package howto;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import lebah.template2.DbPersistence;
import onapp.entity.OnappProgram;
import onapp.module.FileNameUtil;

public class FileUploadModule extends LebahModule {

	private String path = "onapp/setups/program";
	private DbPersistence db = new DbPersistence();
	
	public void preProcess() {
		context.put("path", path);
		context.put("util", new lebah.util.Util());
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("fileNameUtil", new FileNameUtil());
	}

	@Override
	public String start() {
		// TODO Auto-generated method stub
		return path + "/start.vm";
	}
	
	@Command("deleteDocument")
	public String deleteDocument() throws Exception {
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		String programId = getParam("program_id");
		String documentId = getParam("documentId");
		OnappProgram program = db.find(OnappProgram.class, programId);
		if ( program != null ) {
			db.begin();
			if ( "document1".equals(documentId)) program.setDocument1("");
			else if ( "document2".equals(documentId)) program.setDocument2("");
			else if ( "document3".equals(documentId)) program.setDocument3("");
			else if ( "document4".equals(documentId)) program.setDocument4("");
			else if ( "document5".equals(documentId)) program.setDocument5("");
			else if ( "document6".equals(documentId)) program.setDocument6("");
			else if ( "document7".equals(documentId)) program.setDocument7("");
			else if ( "document8".equals(documentId)) program.setDocument8("");
			db.commit();
			return path + "/deleted.vm";
		}
		return path + "/delete_failed.vm";
	}
	
	@Command("uploadDocument")
	public String uploadDocument() throws Exception {
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		String programId = getParam("program_id");
		String documentId = getParam("documentId");
		OnappProgram program = db.find(OnappProgram.class, programId);

		String uploadDir = "c:/temp/";
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
		
		String preName = program.getCode();
		preName = preName.replace("/", "");
		preName = preName.replace("\\", "");
		preName = preName.replace(" ", "_");

		List<String> filenames = new ArrayList<String>();
		context.put("filenames", filenames);
		for ( FileItem item : files ) {
			String fileName = preName + "." + item.getName();
			fileName = fileName.replace(" ", "_"); //replace all space with underscore
			filenames.add(fileName);
			String savedName = uploadDir + fileName;
			item.write(new File(savedName));
			
			db.begin();
			if ( "document1".equals(documentId)) program.setDocument1(savedName);
			else if ( "document2".equals(documentId)) program.setDocument2(savedName);
			else if ( "document3".equals(documentId)) program.setDocument3(savedName);
			else if ( "document4".equals(documentId)) program.setDocument4(savedName);
			else if ( "document5".equals(documentId)) program.setDocument5(savedName);
			else if ( "document6".equals(documentId)) program.setDocument6(savedName);
			else if ( "document7".equals(documentId)) program.setDocument7(savedName);
			else if ( "document8".equals(documentId)) program.setDocument8(savedName);
			else if ( "document9".equals(documentId)) program.setDocument9(savedName);
			else if ( "document10".equals(documentId)) program.setDocument10(savedName);
			else if ( "document11".equals(documentId)) program.setDocument11(savedName);
			else if ( "document12".equals(documentId)) program.setDocument12(savedName);
			else if ( "document13".equals(documentId)) program.setDocument13(savedName);
			else if ( "document14".equals(documentId)) program.setDocument14(savedName);
			else if ( "document15".equals(documentId)) program.setDocument15(savedName);
			else if ( "document16".equals(documentId)) program.setDocument16(savedName);
			else if ( "document17".equals(documentId)) program.setDocument17(savedName);
			else if ( "document18".equals(documentId)) program.setDocument18(savedName);
			else if ( "document19".equals(documentId)) program.setDocument19(savedName);
			else if ( "document20".equals(documentId)) program.setDocument20(savedName);
			db.commit();
			
		}
		return path + "/uploaded.vm";

	}		

}
