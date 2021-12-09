package educate.sis.struct.entity;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="struct_letter_template")
public class LetterTemplate {
	
	@Id
	@Column(length=55)
	private String id;
	@Lob
	private String text;
	@Temporal(TemporalType.DATE)
	private Date modifyDate;
	private String userId;
	
	public LetterTemplate() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	

}
