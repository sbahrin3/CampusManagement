package educate.sis.struct.entity;

import java.util.*;

import javax.persistence.*;
import lebah.db.*;
import metadata.EntityLister;
 
@Entity
@Table(name="struc_period")
public class PeriodScheme implements EntityLister {
	@Id
	private String id;
	private String code;
	
	//should be deprecated
	@Deprecated
	private int pathNo;  //
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="periodScheme")
	private List<Period> periods;
	
	
	public PeriodScheme() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public PeriodScheme(String id, String code) {
		this.id = id;
		this.code = code;
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
	//will be deprecated
	@Deprecated
	public int getPathNo() {
		return pathNo;
	}
	//will be deprecated
	@Deprecated
	public void setPathNo(int pathNo) {
		this.pathNo = pathNo;
	}

	public List<Period> getPeriods() {
		return periods;
	}
	
	public List<Period> getPeriodList() {
		List<Period> list = new ArrayList();
		if ( periods != null ) {
			list.addAll(periods);
			Collections.sort(list);
		}
		return list;
	}
	
	public List<Period> getAllPeriodList() {
		List<Period> list = new LinkedList();
		doAllPeriodList(getPeriodList(), list);
		return list;
	}
	
	private void doAllPeriodList(List<Period> periods, List<Period> f) {
		for ( Iterator<Period> it = periods.iterator(); it.hasNext();) {
			Period p = it.next();
			f.add(p);
			if ( p.hasChild() ) {
				List<Period> childs = p.getChildList();
				doPeriodList(childs, f);
			}
			
		}
	}	
	
	public List<Period> getFunctionalPeriodList() {
		List<Period> list = new LinkedList();
		doPeriodList(getPeriodList(), list);
		return list;
	}
	
	public List<Period> getParentPeriodList() {
		List<Period> periods = getFunctionalPeriodList();
		List<Period> parentPeriods = new ArrayList<Period>();
		for ( Period p : periods ) {
			if ( p.hasParent() ) {
				if ( !parentPeriods.contains(p.getParent())) parentPeriods.add(p.getParent());
			}
		}
		return parentPeriods;
	}
	
	private void doPeriodList(List<Period> periods, List<Period> f) {
		for ( Iterator<Period> it = periods.iterator(); it.hasNext();) {
			Period p = it.next();
			if ( p.hasChild() ) {
				List<Period> childs = p.getChildList();
				doPeriodList(childs, f);
			}
			else {
				f.add(p);
			}
		}
	}

	public void setPeriods(List<Period> periods) {
		this.periods = periods;
	}
	public void addPeriod(Period period) {
		if ( periods == null ) periods = new ArrayList<Period>();
		period.setSequence(periods.size() + 1);
		periods.add(period);
	}
	public void removePeriod(Period period) {
		periods.remove(period);
	}
	public String getValue() {
		return getCode();
	}
	
	public static PeriodScheme findByCode(String code) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		List<PeriodScheme> list = pm.list("select s from PeriodScheme s where s.code = '" + code + "'");
		if ( list.size() == 1 ) return list.get(0);
		else return null;
	}

	
}
