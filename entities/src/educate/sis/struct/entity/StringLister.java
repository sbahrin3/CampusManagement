package educate.sis.struct.entity;

import metadata.EntityLister;
/*
 * History
 * -------
 * #	Date		Name				Remarks
 * ----	----------	------------------	---------------------------------------------------
 * 1.	2009-08-11	Taufek				Created.
 */
public class StringLister implements EntityLister{

	private String id;
	private String value;
	
	public StringLister(String id, String value){
		this.id = id;
		this.value = value;
	}
	
	public String getId() {
		return id;
	}

	public String getValue() {
		return value;
	}
	
	
}
