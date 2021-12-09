package educate.parent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="portal_father")
public class Father {
	
	@Id @Column(length=50)
	private String id;

}
