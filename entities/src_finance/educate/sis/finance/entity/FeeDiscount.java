package educate.sis.finance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="fin_discount")
public class FeeDiscount {
	
	@Id @Column(length=50)
	private String id;
	

}
