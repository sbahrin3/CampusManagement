package educate.sis.finance.module;

import java.util.Date;

public class R {
	
	private String id;
	private String id2;
	private String matricNo;
	private String icno;
	private String name;
	private Date date;
	private String description;
	private String refNo;
	
	private double value;
	
	public R(String id, String matricNo, String name, double value) {
		this.id = id;
		this.matricNo = matricNo;
		this.name = name;
		this.value = value;
	}
	
	public R(String id, String matricNo, String icno, String name, double value) {
		this.id = id;
		this.matricNo = matricNo;
		this.icno = icno;
		this.name = name;
		this.value = value;
	}	
	
	public R(String id, String matricNo, String name, Date date, double value) {
		this.id = id;
		this.matricNo = matricNo;
		this.name = name;
		this.date = date;
		this.value = value;
	}
	
	public R(String id, String id2, String matricNo, String name, Date date, double value) {
		this.id = id + "_" + id2;
		this.matricNo = matricNo;
		this.name = name;
		this.date = date;
		this.value = value;
	}
	
	public R(Date date, String description, double value) {
		this.date = date;
		this.description = description;
		this.value = value;
	}
	
	public R(Date date, String refNo, String description, double value) {
		this.date = date;
		this.refNo = refNo;
		this.description = description;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public double getValue() {
		return value;
	}

	public String getMatricNo() {
		return matricNo;
	}

	public String getName() {
		return name;
	}

	public Date getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getIcno() {
		return icno;
	}

	public void setIcno(String icno) {
		this.icno = icno;
	}
	
	

}
