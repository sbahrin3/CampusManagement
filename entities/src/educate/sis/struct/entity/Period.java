package educate.sis.struct.entity;

import javax.persistence.*;

import metadata.EntityLister;

import java.util.*;

@Entity
@Table(name="struc_periodchild")
public class Period implements Comparable, EntityLister  {
	@Id
	private String id;
	private String name;
	private int level;
	private int sequence;
	private String display;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private PeriodScheme periodScheme;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="parent")
	private Set<Period> periods;
	@ManyToOne
	private Period parent;
	private String code;
	
	
	
	public Period() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public Period(String name) {
		setId(lebah.db.UniqueID.getUID());
		this.name = name;
	}
	
	public Period(String id, String name) {
		this.id = id;
		this.name = name;
	}	
	
	public Period(PeriodScheme ps, String name) {
		this.id = ps.getId() + "_" + name;
		this.name = name;
	}
	
	public Period(String id, String name, int seq) {
		this.id = id;
		this.name = name;
		this.sequence = seq;
	}
	
	public Period(String name, int seq) {
		setId(lebah.db.UniqueID.getUID());
		this.name = name;
		this.sequence = seq;
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
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public Set<Period> getChilds() {
		return periods;
	}
	public List<Period> getChildList() {
		List<Period> list = new ArrayList();
		list.addAll(periods);
		Collections.sort(list);
		return list;
	}	
	public boolean hasChild() {
		return periods != null ? periods.size() > 0 : false;
	}
	public void addChild(Period period) {
		if ( periods == null ) periods = new HashSet<Period>();
		period.setSequence(periods.size() + 1);
		period.setParent(this);
		periods.add(period);
	}
	public int compareTo(Object o) {
		Period p = (Period) o;
		if ( sequence < p.getSequence() ) return -1;
		else if ( sequence > p.getSequence() ) return 1;
		else return 0;
	}
	public Period getParent() {
		return parent;
	}
	public void setParent(Period parent) {
		this.parent = parent;
	}
	public boolean hasParent() {
		return parent != null;
	}
	public void removeChilds() {
		
	}
	public void removeChild(Period p) {
		periods.remove(p);
	}
	public void removeAll() {
		periods.clear();
	}
	@Override
	public boolean equals(Object o) {
		if ( ((Period) o).getId().equals(getId()) ) return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}
	public String getValue() {
		return getName();
	}
	public void getLongName() {
		String longname = "";
		Period p = getParent(this);
	}
	
	Period getParent(Period p) {
		return p.getParent();
	}

	public PeriodScheme getPeriodScheme() {
		return periodScheme;
	}

	public void setPeriodScheme(PeriodScheme periodScheme) {
		this.periodScheme = periodScheme;
	}

	public String getCode() {
		if (code == null) {
			code = "";
		}
		return code;
	}

	public void setCode(String code) {
		if (code == null) {
			this.code = "";
		} else {
			this.code = code;
		}
	}
	
	

}
