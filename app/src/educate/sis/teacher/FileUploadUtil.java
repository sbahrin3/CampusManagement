package educate.sis.teacher;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletRequestContext;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0
 */
public class FileUploadUtil {
	
	
    public static List<String> uploadByServletRequest(String uploadDir, HttpServletRequest request) throws Exception {
    	try {
			java.io.File dir = new java.io.File(uploadDir);
			if ( !dir.exists() ) dir.mkdirs();
	    	List items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);  	
	    	return doUpload(uploadDir, items);
		} catch ( Exception e ) {
			System.out.println("Exception in FileUploadUtil...");
			e.printStackTrace();
		}
		return null;
    }	
	
	public static List<String> uploadByPortletRequest(String uploadDir, ActionRequest request) throws Exception {
		try {
			java.io.File dir = new java.io.File(uploadDir);
			if ( !dir.exists() ) dir.mkdirs();
			List items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(new PortletRequestContext(request));
			return doUpload(uploadDir, items);
		} catch ( Exception e ) {
			System.out.println("Exception in FileUploadUtil...");
			e.printStackTrace();
		}
		return null;
	}

	private static List<String> doUpload(String uploadDir, List items) throws Exception {
		List<String> uploadedFiles = new ArrayList<String>();
		Iterator itr = items.iterator();
		while(itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
			if(!item.isFormField()) {
				if ( item.getName() != null && !"".equals(item.getName())) {
					File fullFile  = new File(item.getName());
					String filename = (!uploadDir.endsWith("/") ? uploadDir += "/": uploadDir) + fullFile.getName();
					filename = filename.replaceAll(" ", "_");
					File savedFile = new File(filename);
					item.write(savedFile);
					uploadedFiles.add(filename);
					System.out.println("Finished uploading... " + item.getName());
				}
			}
		}
		return uploadedFiles;
	}	
	

}
