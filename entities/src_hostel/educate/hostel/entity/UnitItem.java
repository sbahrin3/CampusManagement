package educate.hostel.entity;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="unititem")
@DiscriminatorValue("U")
public class UnitItem extends HostelItem {

	public UnitItem() {
		setId(lebah.db.UniqueID.getUID());
	}

	
}
