package educate.sis.struct.entity;


import javax.persistence.*;

@Entity
@Table(name="struct_matric_num_prefix")
public class MatricNumPrefix {
	@Id
	private String id;
	private String prefix;
	private String counterScopePrefix;
	
	private int counter;
	private int digit;
	private int globalCounter;
	
	@Column(length=50)
	private String refId;
	
	public MatricNumPrefix(){
		setId(id);
	}
	public MatricNumPrefix(String id) {
		setId(id);
	} 

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public int getDigit() {
		return digit;
	}
	public void setDigit(int digit) {
		this.digit = digit;
	}
	public boolean getGlobalCounter() {
		return globalCounter == 1 ? true : false;
	}
	public void setGlobalCounter(boolean globalCounter) {
		this.globalCounter = globalCounter ? 1 : 0;
	}
	public String getCounterScopePrefix() {
		if ( counterScopePrefix != null ) {
			return counterScopePrefix;
		}
		else return prefix.replaceAll("&", "");
	}
	public void setCounterScopePrefix(String counterScopePrefix) {
		this.counterScopePrefix = counterScopePrefix;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	
	

}
	
