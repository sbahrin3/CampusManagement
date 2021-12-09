package educate.sis.module;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.sis.struct.entity.GraduationForm;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import lebah.template2.DbPersistence;
import onapp.module.FileNameUtil;

public class GraduationFileUploadModule extends LebahModule {
	
	private String path = "apps/util/graduation_file_upload";
	private DbPersistence db = new DbPersistence();
	
	public void preProcess() {
		System.out.println(command);
		context.put("path", path);
		context.put("util", new lebah.util.Util());
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("fileNameUtil", new FileNameUtil());
	}	

	@Override
	public String start() {

		GraduationForm g = db.find(GraduationForm.class, "1");
		if ( g == null ) {
			try {
				db.begin();
				g = new GraduationForm();
				g.setId("1");
				db.persist(g);
				db.commit();
			} catch ( Exception e ) { 
				e.printStackTrace();
			}
		}
		context.put("graduationForm", g);
		
		return path + "/start.vm";
	}
	
	@Command("deleteDocument")
	public String deleteDocument() throws Exception {
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		String documentId = getParam("documentId");
		GraduationForm g = db.find(GraduationForm.class, "1");
		context.put("graduationForm", g);
		if ( g != null ) {
			db.begin();
			if ( "document1".equals(documentId)) g.setDocument1("");
			else if ( "document2".equals(documentId)) g.setDocument2("");
			else if ( "document3".equals(documentId)) g.setDocument3("");
			else if ( "document4".equals(documentId)) g.setDocument4("");
			else if ( "document5".equals(documentId)) g.setDocument5("");
			else if ( "document6".equals(documentId)) g.setDocument6("");
			else if ( "document7".equals(documentId)) g.setDocument7("");
			else if ( "document8".equals(documentId)) g.setDocument8("");
			else if ( "document9".equals(documentId)) g.setDocument9("");
			else if ( "document10".equals(documentId)) g.setDocument10("");
			else if ( "document11".equals(documentId)) g.setDocument11("");
			else if ( "document12".equals(documentId)) g.setDocument12("");
			else if ( "document13".equals(documentId)) g.setDocument13("");
			else if ( "document14".equals(documentId)) g.setDocument14("");
			else if ( "document15".equals(documentId)) g.setDocument15("");
			else if ( "document16".equals(documentId)) g.setDocument16("");
			else if ( "document17".equals(documentId)) g.setDocument17("");
			else if ( "document18".equals(documentId)) g.setDocument18("");
			else if ( "document19".equals(documentId)) g.setDocument19("");
			else if ( "document20".equals(documentId)) g.setDocument20("");
			db.commit();
			return path + "/deleted.vm";
		}
		return path + "/delete_failed.vm";
	}
	
	@Command("uploadDocument")
	public String uploadDocument() throws Exception {
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		String documentId = getParam("documentId");
		GraduationForm g = db.find(GraduationForm.class, "1");
		context.put("graduationForm", g);
		String uploadDir = "c:/graduation_form/";
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
		
		String preName = "GF";
		//preName = preName.replace("/", "");
		//preName = preName.replace("\\", "");
		//preName = preName.replace(" ", "_");

		List<String> filenames = new ArrayList<String>();
		context.put("filenames", filenames);
		for ( FileItem item : files ) {
			String fileName = preName + "." + item.getName();
			fileName = fileName.replace(" ", "_"); //replace all space with underscore
			filenames.add(fileName);
			String savedName = uploadDir + fileName;
			item.write(new File(savedName));
			
			db.begin();
			if ( "document1".equals(documentId)) g.setDocument1(savedName);
			else if ( "document2".equals(documentId)) g.setDocument2(savedName);
			else if ( "document3".equals(documentId)) g.setDocument3(savedName);
			else if ( "document4".equals(documentId)) g.setDocument4(savedName);
			else if ( "document5".equals(documentId)) g.setDocument5(savedName);
			else if ( "document6".equals(documentId)) g.setDocument6(savedName);
			else if ( "document7".equals(documentId)) g.setDocument7(savedName);
			else if ( "document8".equals(documentId)) g.setDocument8(savedName);
			else if ( "document9".equals(documentId)) g.setDocument9(savedName);
			else if ( "document10".equals(documentId)) g.setDocument10(savedName);
			else if ( "document11".equals(documentId)) g.setDocument11(savedName);
			else if ( "document12".equals(documentId)) g.setDocument12(savedName);
			else if ( "document13".equals(documentId)) g.setDocument13(savedName);
			else if ( "document14".equals(documentId)) g.setDocument14(savedName);
			else if ( "document15".equals(documentId)) g.setDocument15(savedName);
			else if ( "document16".equals(documentId)) g.setDocument16(savedName);
			else if ( "document17".equals(documentId)) g.setDocument17(savedName);
			else if ( "document18".equals(documentId)) g.setDocument18(savedName);
			else if ( "document19".equals(documentId)) g.setDocument19(savedName);
			else if ( "document20".equals(documentId)) g.setDocument20(savedName);
			db.commit();
			
		}
		return path + "/uploaded.vm";
	}

}
