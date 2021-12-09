package portal.module;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.db.DbPersistence;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import portal.module.entity.AdminRole;
import portal.module.entity.Announcement;
import portal.module.entity.AnnouncementAdmin;

public class AnnouncementModule extends LebahModule {
	
	private String path = "apps/portal/announcement/";
	private DbPersistence db = new DbPersistence();
	private String moduleId;
	private String userId;
	private String userName;
	private String userRole;
	
	@Override
	public void preProcess() {
		
		System.out.println(command);
		
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("date_now", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

		context.put("_moduleId", getId());
		
		userId = (String) request.getSession().getAttribute("_portal_login");
		userName = (String) request.getSession().getAttribute("_portal_username");
		userRole = (String) request.getSession().getAttribute("_portal_role");
		
		context.put("userRole", userRole);
		
		context.put("_today", new Date());
		context.put("_util", new portal.module.Util());
		
		moduleId = getId();
		context.put("_moduleId", moduleId);

	}

	@Override
	public String start() {
		getAdmin();
		
		listAnnouncements();
		return path + "start.vm";
	}

	private boolean getAdmin() {
		AnnouncementAdmin announcementAdmin = db.find(AnnouncementAdmin.class, moduleId);
		userRole = (String) request.getSession().getAttribute("_portal_role");
		boolean isAnnouncementAdmin = false;
		if ( announcementAdmin != null ) {
			if ( "root".equals(userRole)) {
				isAnnouncementAdmin = true;
			} else {
				if ( announcementAdmin.getAdminRoles() != null && announcementAdmin.getAdminRoles().size() > 0 ) {
					for ( AdminRole a : announcementAdmin.getAdminRoles() ) {
						if ( a.getUserRole().equals(userRole)) {
							isAnnouncementAdmin = true;
							break;
						}
					}
				}
			}
		}
		context.put("is_announcement_admin", isAnnouncementAdmin ?  true : false);
		return isAnnouncementAdmin;
	}
	
	private Date requestParameterDate(String name) {
		String _date = request.getParameter(name);
		Date date = null;
		try {
			date = new SimpleDateFormat("dd-MM-yyyy").parse(_date);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		return date;
	}
	
	@Command("create_announcement")
	public String createAnnouncement() throws Exception {
		
		return path + "create.vm";
	}
	
	
	@Command("add_announcement")
	public String addAnnouncement() throws Exception {
		String title = request.getParameter("announcement_title");
		String text = request.getParameter("announcement_text");
		Date dateExpired = requestParameterDate("announcement_expired");
		if ( dateExpired == null ) {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.MONTH, 12);
			dateExpired = c.getTime();
		}
		db.begin();
		Announcement a = new Announcement();
		a.setDatePosted(new Date());
		a.setDateExpired(dateExpired);
		a.setModuleId(moduleId);
		a.setTitle(title);
		a.setText(text);
		db.persist(a);
		db.commit();
		context.put("announcement", a);
		
		listAnnouncements();

		return path + "list.vm";
	}
	
	@Command("view_announcement")
	public String viewAnnouncement() throws Exception {
		String id = request.getParameter("announcement_id");
		Announcement a = db.find(Announcement.class, id);
		context.put("announcement", a);
		return path + "view.vm";
	}
	
	@Command("edit_announcement")
	public String editAnnouncement() throws Exception {
		String id = request.getParameter("announcement_id");
		Announcement a = db.find(Announcement.class, id);
		context.put("announcement", a);
		return path + "edit.vm";
	}
	
	@Command("save_announcement")
	public String saveAnnouncement() throws Exception {
		String id = request.getParameter("announcement_id");
		Announcement a = db.find(Announcement.class, id);

		String title = request.getParameter("announcement_title");
		String text = request.getParameter("announcement_text");
		Date dateExpired = requestParameterDate("announcement_expired");
		
		if ( dateExpired == null ) {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.MONTH, 12);
			dateExpired = c.getTime();
		}
		
		db.begin();
		a.setDateExpired(dateExpired);
		a.setTitle(title);
		a.setText(text);
		db.commit();
		context.put("announcement", a);
		//listAnnouncements();
		return path + "edit.vm";
	}
	
	@Command("list_announcements")
	public String list_Announcements() throws Exception {
		listAnnouncements();
		return path + "list.vm";
	}

	private void listAnnouncements() {
		if ( getAdmin() ) {
			List<Announcement> announcements = db.list("select a from Announcement a " +
					"where a.moduleId = '" + getId() + "' " +
					"order by a.datePosted desc");
				context.put("announcements", announcements);
		}
		else {
			Hashtable h = new Hashtable();
			h.put("date", new Date());
			List<Announcement> announcements = db.list("select a from Announcement a " +
					"where a.moduleId = '" + getId() + "' " +
					"and a.dateExpired >= :date " +
					"order by a.datePosted desc", h);
				context.put("announcements", announcements);
		}
	}
	
	@Command("delete_announcements")
	public String deleteAnnouncement() throws Exception {
		String[] ids = request.getParameterValues("announcement_ids");
		if ( ids != null ) {
			for ( String id : ids ) {
				Announcement a = db.find(Announcement.class, id);
				if ( a != null ) {
					db.begin();
					db.remove(a);
					db.commit();
				}
			}
		}
		listAnnouncements();
		return path + "list.vm";
	}
	
	@Command("uploadFile")
	public String uploadFile() throws Exception {
		
		String uploadDir = "";
    	ResourceBundle rb = null;
    	try {
    		rb = ResourceBundle.getBundle("files");
    		uploadDir = rb.getString("documentUpload");
    	} catch (java.util.MissingResourceException ex) {
    		uploadDir = "c:/temp/";
    		ex.printStackTrace();
    	}
    	
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		
		String documentId = getParam("documentId");
		String announcementId = getParam("announcementId");
		
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
			if ( !uploadDir.endsWith("/")) uploadDir = uploadDir + "/";
			String savedName = uploadDir + fileName;
			context.put("serverFileName", savedName);
			item.write(new File(savedName));
			if ( "document".equals(documentId)) { 
				Announcement ann = db.find(Announcement.class, announcementId);
				context.put("announcement", ann);
				db.begin();
				ann.setFileName(fileName);
				ann.setServerFileName(savedName);
				db.commit();
				break;
			}
		}
		
		return path + "/uploaded.vm";
	}

}
