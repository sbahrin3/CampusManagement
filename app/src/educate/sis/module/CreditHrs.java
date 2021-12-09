package educate.sis.module;

public class CreditHrs {
	
	private String id;
	private long value;
	
	public CreditHrs(String id, long value) {
		this.id = id;
		this.value = value*3;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	
	

}
