package educate.hostel.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 * Types:
 * home furniture
 * office furniture 
 * consumer electronic
 * kitchen utensil
 * bathroom utensil
 * bedroom utensil
 * household utensil
 */

@Entity
@Table(name="hostel_item_type")
public class ItemType {

	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=100)
	private String name;
	private int moveable;
	
	public ItemType() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public boolean getMoveable() {
		return moveable == 1;
	}

	public void setMoveable(boolean moveable) {
		this.moveable = moveable ? 1 : 0;
	}

	
}
