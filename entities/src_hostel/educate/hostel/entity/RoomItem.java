package educate.hostel.entity;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="roomitem")
@DiscriminatorValue("R")
public class RoomItem extends HostelItem {

	public RoomItem() {
		setId(lebah.db.UniqueID.getUID());
	}

	
}
