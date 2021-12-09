package educate.hostel.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="hostel_items")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING, name="locationId", length=5)
public abstract class HostelItem {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="room_id")
	private Room room;
	@ManyToOne @JoinColumn(name="unit_id")
	private Unit unit;	
	@ManyToOne @JoinColumn(name="mover_id")
	private Mover mover;		
	@ManyToOne @JoinColumn(name="item_id")
	private Item item;
	@Temporal(TemporalType.DATE)
	private Date dateIn;
	@Temporal(TemporalType.DATE)
	private Date dateOut;

	
	public HostelItem() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public Date getDateIn() {
		return dateIn;
	}
	public void setDateIn(Date dateIn) {
		this.dateIn = dateIn;
	}
	public Date getDateOut() {
		return dateOut;
	}
	public void setDateOut(Date dateOut) {
		this.dateOut = dateOut;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	
	public String getDescription() {
		return item.getDescription();
	}
	
	public String getTag() {
		return item.getTag();
	}
	
	public ItemType getType() {
		return item.getType();
	}
	
	public boolean moveable() {
		return item.getType().getMoveable();
	}
	
	public double value() {
		return item.getValue();
	}

	public Mover getMover() {
		return mover;
	}

	public void setMover(Mover mover) {
		this.mover = mover;
	}


}
