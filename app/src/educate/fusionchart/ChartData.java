package educate.fusionchart;

import java.util.Map;


public abstract class ChartData {
	
	protected Map<String, String> properties;

	public void useProperties(Map<String, String> rb) {
		this.properties = rb;
	}
	
	protected String get(String key) {
		return properties.get(key) != null ? properties.get(key) : "";
	}
	
	public abstract void createValues(Chart x);

}
