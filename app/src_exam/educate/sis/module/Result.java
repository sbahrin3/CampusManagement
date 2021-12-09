package educate.sis.module;

public class Result {
	
	private String key = "";
	private String label = "";
	private String text = "";
	private double value;
	private long counter;
	
	
	public Result(String key, String label, String text, double value) {
		this.key = key;
		this.label = label;
		this.text = text;
		this.value = value;
	}
	
	public Result(String key, String label, double value) {
		this.key = key;
		this.label = label;
		this.value = value;
	}
	
	public Result(String key, String label, long counter) {
		this.key = key;
		this.label = label;
		this.counter = counter;
	}
	
	public Result(String key, String label, String text, long counter) {
		this.key = key;
		this.label = label;
		this.text = text;
		this.counter = counter;
	}	

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}



}
