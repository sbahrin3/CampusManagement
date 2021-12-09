package educate.sis.struct.entity;

import javax.persistence.*;

@Entity
@Table(name="struc_cat_subject_count")

public class CategoryCount {
	
	@Id
	private String id;
	@ManyToOne
	private SubjectCategory category;
	private int counter;
	
	public CategoryCount() {
		setId(lebah.db.UniqueID.getUID());
	}

	public SubjectCategory getCategory() {
		return category;
	}

	public void setCategory(SubjectCategory category) {
		this.category = category;
	}

	public int getCount() {
		return counter;
	}

	public void setCount(int count) {
		this.counter = count;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	
}
