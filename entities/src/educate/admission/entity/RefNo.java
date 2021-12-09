package educate.admission.entity;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.persistence.*;

@Entity
@Table(name="refno")
public class RefNo {
	@Id
	private String code;
	private int number; //this is current running number
	private int digitLength;
	private String prefix;
	//@Transient
	//private String result;
	
	public RefNo() {
		
	}
	
	public int getDigitLength() {
		return digitLength;
	}
	public void setDigitLength(int digitLength) {
		this.digitLength = digitLength;
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getResult() {
		String format = "0";
		for ( int i=1; i < digitLength; i++ ) {
			format += "0";
		}
	    NumberFormat formatter = new DecimalFormat(format);
	    return getPrefix() + formatter.format(getNumber());
	}

	
	@Override
	public String toString() {
		return getResult();
	}
	
}
