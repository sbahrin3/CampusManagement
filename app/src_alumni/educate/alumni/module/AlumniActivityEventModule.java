package educate.alumni.module;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.alumni.entity.AlumniActivityEvent;
import educate.alumni.entity.EventPhoto;
import lebah.portal.action.Command;
import lebah.repository.Thumbnail;
import lebah.template.LebahRecordTemplateModule;

public class AlumniActivityEventModule extends LebahRecordTemplateModule<AlumniActivityEvent> {

	@Override
	public Class getIdType() {
		return String.class;
	}

	@Override
	public void afterSave(AlumniActivityEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void begin() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		System.out.println(command);
	}

	@Override
	public boolean delete(AlumniActivityEvent e) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPath() {
		return "alumni/event_records";
	}

	@Override
	public Class<AlumniActivityEvent> getPersistenceClass() {
		return AlumniActivityEvent.class;
	}

	@Override
	public void getRelatedData(AlumniActivityEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(AlumniActivityEvent e) throws Exception {
		e.setName(getParam("name"));
		e.setDescription(getParam("description"));
		Date startDate = null, endDate = null;
		try {
			startDate = new SimpleDateFormat("dd-MM-yyyy").parse(getParam("startDate"));
		} catch ( Exception ex ) { }
		try {
			endDate = new SimpleDateFormat("dd-MM-yyyy").parse(getParam("endDate"));
		} catch ( Exception ex ) { }
		e.setStartDate(startDate);
		e.setEndDate(endDate);
		e.setRemark(getParam("remark"));
	}

	@Override
	public Map<String, Object> searchCriteria() throws Exception {
		Map m = new HashMap();
		m.put("name", getParam("findName"));
		return m;
	}
	
	@Command("uploadFile")
	public String uploadFile() throws Exception {
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		String documentId = getParam("documentId");
		String uploadDir = "c:/uploaded/";
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
		for ( FileItem item : files ) {
			String fileName = item.getName();
			String savedName = uploadDir + fileName;
			context.put("serverFileName", savedName);
			item.write(new File(savedName));
			
			if ( "eventPhoto".equals(documentId)) { 
				System.out.println("uploaded photo!");
				
				String avatarName = savedName.substring(0, savedName.lastIndexOf(".")) + "_avatar" + savedName.substring(savedName.lastIndexOf("."));
				lebah.repository.Thumbnail.create(savedName, avatarName, 200, 160, 100);
				//lebah.repository.Thumbnail.create(savedName, avatarName, 50, 40, 100);
				context.put("avatarFileName", avatarName);
				
				String eventId = getParam("eventId");
				context.put("eventId", eventId);
				if ( !"".equals(eventId)) {
					AlumniActivityEvent event = db.find(AlumniActivityEvent.class, eventId);
					db.begin();
					EventPhoto photo = new EventPhoto();
					photo.setEvent(event);
					photo.setPhotoFileName(savedName);
					photo.setAvatarFileName(avatarName);
					event.getPhotos().add(photo);
					db.commit();
				}
				break;
			}
		}
		return getPath() + "/uploaded.vm";
	}	
	
	@Command("listPhotos")
	public String listPhotos() throws Exception {
		String eventId = getParam("eventId");
		AlumniActivityEvent event = db.find(AlumniActivityEvent.class, eventId);
		context.put("r", event);
		return getPath() + "/listPhotos.vm";
	}
	

}
