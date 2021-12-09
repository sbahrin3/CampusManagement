package portal.module;

public class Record {
	
	private String moduleId;
	private String moduleName;
	
	public Record(String id, String name) {
		moduleId = id;
		moduleName = name;
	}
	
	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	

}
