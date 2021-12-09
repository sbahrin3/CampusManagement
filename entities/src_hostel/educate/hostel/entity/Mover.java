package educate.hostel.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="hostel_mover")
public class Mover {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String name;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="mover")
	private List<HostelItem> items;	
	
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
	public List<HostelItem> getItems() {
		return items;
	}
	public void setItems(List<HostelItem> items) {
		this.items = items;
	}
	
	

}
