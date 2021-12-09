package educate.sis.struct.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="struc_degree_scheme")
public class DegreeScheme {
	@Id
	private String id;
	private String name;
	@OneToMany
	private Set<Degree> degrees;
	
	public DegreeScheme() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public DegreeScheme(String id) {
		setId(id);
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

	public Set<Degree> getDegrees() {
		return degrees;
	}

	public void setDegrees(Set<Degree> degrees) {
		this.degrees = degrees;
	}
	
	public void addDegree(Degree degree) {
		if ( degrees == null ) degrees = new HashSet();
		degrees.add(degree);
	}

}
