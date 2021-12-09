package educate.facilities.module;

import java.io.File;
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

import educate.facilities.entity.Asset;
import educate.facilities.entity.AssetCategory;
import educate.sis.struct.entity.LearningCentre;
import lebah.portal.action.Command;
import lebah.template.LebahRecordTemplateModule;
import lebah.template.OperatorEqualTo;

public class AssetRecordModule extends LebahRecordTemplateModule<Asset> {

	@Override
	public Class getIdType() {
		return String.class;
	}

	@Override
	public void afterSave(Asset arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void begin() {
		context.put("path", getPath());
		this.setReadonly(false);
		List<AssetCategory> categories = db.list("select c from AssetCategory c order by c.sequence");
		List<LearningCentre> campuses = db.list("select c from LearningCentre c");
		context.put("categories", categories);
		context.put("campuses", campuses);
	}

	@Override
	public boolean delete(Asset a) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getPath() {
		return "facilities/asset_records";
	}

	@Override
	public Class<Asset> getPersistenceClass() {
		return Asset.class;
	}

	@Override
	public void getRelatedData(Asset arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(Asset a) throws Exception {
		String categoryId = getParam("categoryId");
		AssetCategory category = db.find(AssetCategory.class, categoryId);
		String campusId = getParam("campusId");
		LearningCentre campus = db.find(LearningCentre.class, campusId);
		save(a, category, campus);
	}

	@Override
	public Map<String, Object> searchCriteria() throws Exception {
		Map map = new HashMap();
		map.put("category.id", new OperatorEqualTo(getParam("findCategoryId")));
		map.put("campus.id", new OperatorEqualTo(getParam("findCampusId")));
		map.put("description", getParam("findDescription"));
		map.put("tagNo", getParam("fintTagNo"));
		map.put("serialNo", getParam("findSerialNo"));
		map.put("brandName", getParam("findBrandName"));
		map.put("modelName", getParam("findModelName"));
		return map;
	}
	
	private void save(Asset a, AssetCategory category, LearningCentre campus) {
		
		if ( isNew ) {
			a.setCreateDate(new Date());
			a.setCreateUserId(userId);
			a.setRemoteAddress(request.getRemoteHost());
		}
		
		a.setModifyDate(new Date());
		a.setModifyUserId(userId);
		a.setModifyRemoteAddress(request.getRemoteHost());
		
		a.setCategory(category);
		a.setCampus(campus);
		a.setDescription(getParam("description"));
		a.setBrandName(getParam("brandName"));
		a.setLocationDescription(getParam("locationDescription"));
		a.setModelName(getParam("modelName"));
		try {
			a.setPurchaseDate(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("purchaseDate")));
		} catch ( Exception e ) {
			a.setPurchaseDate(null);
		}
		try {
			a.setPurchaseValue(Double.parseDouble(getParam("purchaseValue")));
		} catch ( Exception e ) {
			a.setPurchaseValue(0.0d);
		}
		try {
			a.setPurchaseYear(Integer.parseInt(getParam("purchaseYear")));
		} catch ( Exception e ) {
			a.setPurchaseYear(0);
		}
		a.setSerialNo(getParam("serialNo"));
		a.setTagNo(getParam("tagNo"));
		a.setStatus(getParam("assetStatus"));
		a.setPhotoFileName(getParam("photoFileName"));
		
		String _loanable = getParam("loanable");
		int loanable = 0;
		try {
			loanable = Integer.parseInt(_loanable);
		} catch ( Exception e ) {}
		a.setLoanable(loanable == 1);
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
		
		List<String> filenames = new ArrayList<String>();
		context.put("filenames", filenames);
		
		for ( FileItem item : files ) {
			String fileName = item.getName();
			filenames.add(fileName);
			String savedName = uploadDir + fileName;
			context.put("serverFileName", savedName);
			item.write(new File(savedName));
			
			if ( "assetPhoto".equals(documentId)) { 
				System.out.println("uploaded asset photo!");
				String assetId = getParam("assetId");
				if ( !"".equals(assetId)) {
					db.begin();
					Asset a = db.find(Asset.class, assetId);
					a.setPhotoFileName(savedName);
					db.commit();
				}
				break;
			}
		}
		
		return getPath() + "/uploaded.vm";
	}
	

}
