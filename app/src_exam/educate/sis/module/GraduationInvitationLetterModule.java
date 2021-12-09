package educate.sis.module;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.db.DbPersistence;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import onapp.entity.ApplicantOnline;

public class GraduationInvitationLetterModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/util/student_graduation2/invitation_letter/";
	
	public void preProcess() {
		System.out.println(command);
	}

	@Override
	public String start() {
		readFile();
		return path + "start.vm";
	}
	
	
	private void readFile() {
		try {
			String realPath = getServletContext().getRealPath("").replace("\\","/");
			BufferedReader in = new BufferedReader(new FileReader(realPath + "/graduationLetter/invitation.html"));
			String content = "";
			String line;
			while((line = in.readLine()) != null) 			{
				content += line;
			}
			context.put("content", content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Command("saveContent")
	public String saveContent() throws Exception {
		String content = getParam("message");
		
		String realPath = getServletContext().getRealPath("").replace("\\","/");
		File file = new File(realPath + "/graduationLetter/invitation.html");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
		
		return path + "/saveContent.vm";
	}


	@Command("uploadDocument")
	public String uploadDocument() throws Exception {
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		String applicantId = getParam("applicant_id");
		String documentId = getParam("documentId");
		ApplicantOnline applicant = db.find(ApplicantOnline.class, applicantId);
		context.put("applicant", applicant);
		
		String uploadDir = "c:/workspace/educate4/WebContent/graduationLetter/";
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
		String preName = applicantId;
		preName = preName.replace("/", "");
		preName = preName.replace("\\", "");

		List<String> filenames = new ArrayList<String>();
		context.put("filenames", filenames);
		
		for ( FileItem item : files ) {
			String fileName = preName + "." + item.getName();
			filenames.add(fileName);
			String savedName = uploadDir + fileName;
			item.write(new File(savedName));
			
			/*
			if ( "invitationLetter".equals(documentId)) { 

				break;
			}
			 */
			
		}
		
		if ( "applicant_photo".equals(documentId)) {
			return path + "photo_uploaded.vm";
		}
		return path + "uploaded.vm";
	}	

}
