package educate.sis.exam.entity;

import java.util.*;

import educate.sis.struct.entity.*;
import javax.persistence.*;

@Entity
@Table(name="exam_resultmark_part")
public class ResultMarkPart {

	@Id
	private String id;
	private String code;
	private String name;
	@OneToOne
	private Subject subject;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="resultMarkPart")
	private Set<MarkPart> markParts;
		
	public ResultMarkPart() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Set<MarkPart> getMarkParts() {
		return markParts;
	}
	public void setMarkParts(Set<MarkPart> markParts) {
		for(Iterator<MarkPart> it = markParts.iterator();it.hasNext();){
			it.next().setResultMarkPart(this);
		}
		this.markParts = markParts;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	
	
	
	
}
