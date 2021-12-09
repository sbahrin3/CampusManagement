package educate.sis.struct.entity;

import javax.persistence.*;

@Entity
public class MatricNumPrefix2 {
	@Id
	private String id;
	private int number;
	
	public MatricNumPrefix2(String id) {
		setId(id);
	}
	

	public String getId() {
		return id;
	} 

	public void setId(String id) {
		this.id = id;
	}


	public int getNumber() {
		return number;
	}


	public void setNumber(int number) {
		this.number = number;
	}
	
	

}
