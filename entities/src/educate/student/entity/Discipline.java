package educate.student.entity;

import javax.persistence.*;

@Entity
@Table(name="student_discipline")
public class Discipline {
	
	@Id
	private String id;
	
	public Discipline() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	

}
