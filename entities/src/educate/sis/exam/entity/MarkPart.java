package educate.sis.exam.entity;

import javax.persistence.*;

@Entity
@Table(name="exam_mark_part")
public class MarkPart {
	@Id
	private String id;
	private String name;
	private int sequence;
	private double mark;
	
	@ManyToOne
	private ResultMarkPart resultMarkPart;
		
	public MarkPart() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getMark() {
		return mark;
	}
	public void setMark(double mark) {
		this.mark = mark;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ResultMarkPart getResultMarkPart() {
		return resultMarkPart;
	}

	public void setResultMarkPart(ResultMarkPart resultMarkPart) {
		this.resultMarkPart = resultMarkPart;
	}
	
	
}
