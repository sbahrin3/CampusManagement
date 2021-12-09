package educate.facilities.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.facilities.entity.WorkCategory;
import educate.facilities.entity.WorkOrder;
import lebah.template.LebahRecordTemplateModule;
import lebah.template.OperatorEqualTo;

public class WorkOrderRecordModule extends LebahRecordTemplateModule<WorkOrder> {

	@Override
	public Class getIdType() {
		return String.class;
	}

	@Override
	public void afterSave(WorkOrder arg0) {
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
//		List<WorkOrderIssue> issues = db.list("select c from WorkOrderIssue c order by c.sequence");
//		context.put("issues", issues);
		List<WorkCategory> categories = db.list("select c from WorkCategory c order by c.sequence");
		context.put("categories", categories);		
		
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
	}

	@Override
	public boolean delete(WorkOrder a) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getPath() {
		return "facilities/workorder_records";
	}

	@Override
	public Class<WorkOrder> getPersistenceClass() {
		return WorkOrder.class;
	}

	@Override
	public void getRelatedData(WorkOrder arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(WorkOrder w) throws Exception {
		//String issueId = getParam("workOrderIssueId");
		//WorkOrderIssue issue = db.find(WorkOrderIssue.class, issueId);
		String categoryId = getParam("workCategoryId");
		WorkCategory category = db.find(WorkCategory.class, categoryId);
		
		String issueDescription = getParam("issueDescription");
		
		//w.setIssue(issue);
		w.setCategory(category);
		w.setIssueDescription(issueDescription);
		w.setOfficerName(getParam("officerName"));
		w.setOfficerEmail(getParam("officerEmail"));
		try {
			w.setStartDate(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("startDate")));
		} catch ( Exception e ) { w.setStartDate(null); }
		try {
			w.setCloseDate(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("closeDate")));
		} catch ( Exception e ) { w.setCloseDate(null); }
		w.setActionRemark(getParam("actionRemark"));
		w.setStatus(getParam("status"));
		
	}

	@Override
	public Map<String, Object> searchCriteria() throws Exception {
		Map map = new HashMap();
		//map.put("issue.id", new OperatorEqualTo(getParam("findWorkOrderIssueId")));
		map.put("category.id", new OperatorEqualTo(getParam("findWorkCategoryId")));
		map.put("issueDescription", getParam("findIssueDescription"));
		map.put("officerName", getParam("findOfficerName"));
		map.put("status", new OperatorEqualTo(getParam("findStatus")));
		return map;
	}
	
}
