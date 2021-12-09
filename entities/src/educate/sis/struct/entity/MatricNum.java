package educate.sis.struct.entity;

import javax.persistence.*;

@Entity
@Table(name="struct_matric_num")
public class MatricNum {
	
	@Id
	private String id;
	@ManyToOne
	private MatricNumPrefix prefix;
	private int postfix;
	private String matricNumber;
	
	public MatricNum(){
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public MatricNumPrefix getPrefix() {
		return prefix;
	}

	public void setPrefix(MatricNumPrefix prefix) {
		this.prefix = prefix;
	}

	public int getPostfix() {
		return postfix;
	}

	public void setPostfix(int postfix) {
		this.postfix = postfix;
	}

	public String getMatricNumber() {
		return matricNumber;
	}

	public void setMatricNumber(String matricNumber) {
		this.matricNumber = matricNumber;
	}
	
}
