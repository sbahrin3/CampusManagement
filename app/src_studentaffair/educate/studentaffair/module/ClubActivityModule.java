package educate.studentaffair.module;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.db.DbPersistence;
import educate.studentaffair.entity.Club;
import educate.studentaffair.entity.ClubActivity;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ClubActivityModule extends LebahModule {
	
	protected String path = "studentaffair/clubActivity";
	protected DbPersistence db = new DbPersistence();
	protected boolean studentMode = false;
	protected String studentId = null;

	@Override
	public String start() {
		if ( !studentMode )
			context.put("clubs", db.list("select c from Club c"));
		else
			context.put("clubs", db.list("select c from StudentClub s Join s.club c where s.student.id = '" + studentId + "'"));
		return path + "/start.vm";
	}
	
	public void preProcess() {
		context.put("studentMode", studentMode);
		if ( studentId != null ) context.put("studentId", studentId);
		else context.remove("studentId");
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
	}
	
	public void setStudentMode(boolean b) {
		studentMode = b;
	}
	
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	
	@Command("getClub")
	public String getClub() throws Exception {
		String clubId = getParam("clubId");
		Club club = db.find(Club.class, clubId);
		context.put("club", club);
		
		context.put("activities",db.list("select a from ClubActivity a where a.club.id = '" + clubId + "' order by a.startDate desc"));
		
		return path + "/getClub.vm";
	}
	
	@Command("newActivity")
	public String newActivity() throws Exception {
		String clubId = getParam("clubId");
		Club club = db.find(Club.class, clubId);
		context.put("club", club);
		return path + "/newActivity.vm";
	}
	
	@Command("addActivity")
	public String addActivity() throws Exception {
		String clubId = getParam("clubId");
		Club club = db.find(Club.class, clubId);
		context.put("club", club);
		
		db.begin();
		ClubActivity activity = new ClubActivity();
		activity.setClub(club);
		activity.setName(getParam("activityName"));
		activity.setStartDate(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("startDate")));
		activity.setEndDate(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("endDate")));
		activity.setLocationRemark(getParam("locationRemark"));
		activity.setDescription(getParam("description"));
		activity.setProposalDocumentName(getParam("proposalDocumentName"));
		activity.setProposalStatus("in-process");
		db.persist(activity);
		db.commit();
		
		context.put("activities",db.list("select a from ClubActivity a where a.club.id = '" + clubId + "' order by a.startDate desc"));
		return path + "/activities.vm";
	}
	
	@Command("editActivity")
	public String editActivity() throws Exception {
		String activityId = getParam("activityId");
		ClubActivity activity = db.find(ClubActivity.class, activityId);
		context.put("activity", activity);
		return path + "/editActivity.vm";
	}
	
	@Command("saveActivity")
	public String saveActivity() throws Exception {
		String activityId = getParam("activityId");
		ClubActivity activity = db.find(ClubActivity.class, activityId);
		context.put("club", activity.getClub());
		
		db.begin();
		activity.setName(getParam("activityName_" + activityId));
		activity.setStartDate(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("startDate_" + activityId)));
		activity.setEndDate(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("endDate_" + activityId)));
		activity.setLocationRemark(getParam("locationRemark_" + activityId));
		activity.setDescription(getParam("description_" + activityId));
		activity.setProposalStatus(getParam("proposalStatus_" + activityId));
		activity.setAdminRemark(getParam("adminRemark_" + activityId));
		if ( "approve".equals(activity.getProposalStatus())) {
			
		}
		db.commit();
		
		context.put("activities",db.list("select a from ClubActivity a where a.club.id = '" + activity.getClub().getId() + "' order by a.startDate desc"));
		return path + "/activities.vm";
	}	
	
	@Command("deleteActivity")
	public String deleteActivity() throws Exception {
		String activityId = getParam("activityId");
		ClubActivity activity = db.find(ClubActivity.class, activityId);
		context.put("club", activity.getClub());
		String clubId = activity.getClub().getId();
		context.put("club", activity.getClub());
		db.begin();
		db.remove(activity);
		db.commit();
		
		context.put("activities",db.list("select a from ClubActivity a where a.club.id = '" + clubId + "' order by a.startDate desc"));
		return path + "/activities.vm";
	}		
	

	@Command("uploadDocument")
	public String uploadDocument() throws Exception {
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		
		String documentId = getParam("documentId");
		String uploadDir = "c:/activities/";
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
			
			
			if ( "proposalDocument".equals(documentId)) { 
				System.out.println("uploaded proposal document!");
				break;
			}
			
		}
		
		
		return path + "/uploaded.vm";
	}
	
	
	

}
