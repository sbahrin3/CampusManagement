package educate.sis.exam.entity;

import java.util.*;

import educate.sis.struct.entity.*;
import javax.persistence.*;

@Entity
@Table(name="exam_coursework")
public class Coursework{
	
	@Id
	private String id;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="coursework")
	private Set<CourseworkItem> items;
	@OneToOne
	private Subject subject;
	@OneToOne
	private Session session;
	@OneToOne
	private LearningCentre centre;
	
	private int roundUpTotal;
	
	private int roundType; // 0 - no rounding, 1 - round Up, 2 - round down
	
	public Coursework() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Set<CourseworkItem> getItems() {
		return items;
	}
	public void setItems(Set<CourseworkItem> t){
		this.items = t;
	}
	public void addItem(CourseworkItem item) {
		if ( items == null ) items = new HashSet<CourseworkItem>();
		item.setCoursework(this);
		item.setSequence(items.size()+1);
		items.remove(item);
		items.add(item);
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	
	
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public List<CourseworkItem> getCourseworkItems() {
		List<CourseworkItem> list = new ArrayList<CourseworkItem>();
		list.addAll(items);
		Collections.sort(list, new SequenceComparator());
		return list;
	}
	
	
	
	
	class SequenceComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			CourseworkItem r1 = (CourseworkItem) o1;
			CourseworkItem r2 = (CourseworkItem) o2;
			return r1.getSequence() > r2.getSequence() ? 1 : -1;
		}
		
	}




	public LearningCentre getCentre() {
		return centre;
	}

	public void setCentre(LearningCentre centre) {
		this.centre = centre;
	}

	public boolean getRoundUpTotal() {
		return roundUpTotal == 1;
	}

	public void setRoundUpTotal(boolean roundUpTotal) {
		this.roundUpTotal = roundUpTotal ? 1 : 0;
	}

	public int getRoundType() {
		return roundType;
	}

	public void setRoundType(int roundType) {
		this.roundType = roundType;
	}
	
	
	
	
}
