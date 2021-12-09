package educate.lms.entity;

import javax.persistence.*;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.0
 */

@Entity
@Table(name="lms_attachment")
public class Attachment {
	
	@Id
	@Column(length=50)
	private String id;
	@Column(length=255)
	private String fileName;
	
	public Attachment() { 
		setId(lebah.db.UniqueID.getUID());
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	

}
