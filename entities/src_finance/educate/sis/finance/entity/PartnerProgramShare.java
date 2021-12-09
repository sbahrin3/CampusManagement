package educate.sis.finance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="fin_partner_share")
public class PartnerProgramShare {
	
	@Id @Column(length=50)
	private String id;
	

}
