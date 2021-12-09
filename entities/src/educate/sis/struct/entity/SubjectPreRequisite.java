package educate.sis.struct.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="struc_subject_preq")
public class SubjectPreRequisite {
	
	@Id @Column(length=50)
	private String id;

}
