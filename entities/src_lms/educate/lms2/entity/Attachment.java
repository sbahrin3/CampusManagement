package educate.lms2.entity;

import javax.persistence.*;

@Entity
@Table(name="lms2_attachment")
public class Attachment {
	
	@Id
	private String id;
	@Column(length=100)
	private String fileName;
	@Column(length=255)
	private String fullFileName;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Posting posting;
	
	public Attachment() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Posting getPosting() {
		return posting;
	}
	public void setPosting(Posting posting) {
		this.posting = posting;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFullFileName() {
		return fullFileName;
	}

	public void setFullFileName(String fullFileName) {
		this.fullFileName = fullFileName;
	}
	
	

}
