package educate.questionnare.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import edu.emory.mathcs.backport.java.util.Collections;

@Entity
@Table(name="te_set")
public class TESet {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=100)
	private String name;
	@Column(length=255)
	private String description;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="set")
	private List<TEPart> parts;
	@Column(length=50)
	private String userId;
	
	private int typeQuestionBank;
	private int typeQuestionnare;
	
	public TESet() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public List<TEPart> getParts() {
		if ( parts == null ) parts = new ArrayList<TEPart>();
		Collections.sort(parts, new SequenceComparator());
		return parts;
	}
	public void setParts(List<TEPart> parts) {
		this.parts = parts;
	}
	
	
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean getTypeQuestionBank() {
		return typeQuestionBank == 1;
	}

	public void setTypeQuestionBank(boolean typeQuestionBank) {
		this.typeQuestionBank = typeQuestionBank ? 1 : 0;
	}

	public boolean getTypeQuestionnare() {
		return typeQuestionnare == 1;
	}

	public void setTypeQuestionnare(boolean typeQuestionnare) {
		this.typeQuestionnare = typeQuestionnare ? 1 : 0;
	}



	static class SequenceComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			TEPart r1 = (TEPart) o1;
			TEPart r2 = (TEPart) o2;
			return r1.getSequence() > r2.getSequence() ? 1 : -1;
		}
		
	}	

}
