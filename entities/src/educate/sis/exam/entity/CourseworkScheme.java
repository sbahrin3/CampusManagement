package educate.sis.exam.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="coursework_scheme")
public class CourseworkScheme {
	
	@Id
	@Column(length=55)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=100)
	private String name;
	
//	@OneToMany(cascade=CascadeType.ALL, mappedBy="courseworkScheme")
//	private Set<CourseworkSchemeItem> items;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="courseworkScheme")
	private Set<CourseworkItem> items;

	public CourseworkScheme() {
		setId(lebah.util.UIDGenerator.getUID());
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

	
	public Set<CourseworkItem> getItems() {
		return items;
	}
	public void setItems(Set<CourseworkItem> t){
		this.items = t;
	}
	public void addItem(CourseworkItem item) {
		if ( items == null ) items = new HashSet<CourseworkItem>();
		item.setCourseworkScheme(this);
		item.setSequence(items.size()+1);
		items.remove(item);
		items.add(item);
	}
	
	public void deleteItem(String itemId) {
		int i = 0;
		CourseworkItem deleteItem = null;
		for ( CourseworkItem item : items ) {
			if ( itemId.equals(item.getId()) ) {
				deleteItem = item;
				break;
			}
			i++;
		}
		items.remove(deleteItem);
	}
	
	public List<CourseworkItem> getCourseworkItems() {
		List<CourseworkItem> list = new ArrayList<CourseworkItem>();
		list.addAll(items);
		Collections.sort(list, new SequenceComparator());
		return list;
	}
	
	
	
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}




	class SequenceComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			CourseworkItem r1 = (CourseworkItem) o1;
			CourseworkItem r2 = (CourseworkItem) o2;
			return r1.getSequence() > r2.getSequence() ? 1 : -1;
		}
		
	}


}
