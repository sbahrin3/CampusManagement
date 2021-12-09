package educate.sis.struct.entity;


import java.util.*;

import javax.persistence.*;

import educate.enrollment.entity.StudentSubject;
import educate.sis.finance.entity.FeeStructureItem;


@Entity
@Table(name="struc_subjectperiod")
public class SubjectPeriod implements Comparable {
	@Id
	private String id;
	@OneToOne
	private Period period;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="subjectPeriod")
	private Set<SubjectReg> subjectRegs;
	@ManyToOne
	private ProgramStructure programStructure;
		
	public SubjectPeriod() {
		setId(lebah.db.UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	public Period getPeriod() {
		return period;
	}
	
	public Set<Subject> getSubjects() {
		Set<Subject> subjects = new HashSet<Subject>();
		for ( SubjectReg s : subjectRegs ) {
			subjects.add(s.getSubject());
		}
		return subjects;
	}
	
	public Set<SubjectReg> getSubjectRegs() {
		return subjectRegs;
	}
	
	public void setSubjectRegs(Set<SubjectReg> subjectTypes) {
		this.subjectRegs = subjectTypes;
	}
	
	public List<SubjectReg> getSubjectRegsOrderByCode() {
		List<SubjectReg> list = new ArrayList<SubjectReg>();
		list.addAll(subjectRegs);
		Collections.sort(list, new SubjectRegsComparator("code"));
		return list;
	}
	
	public List<SubjectReg> getSubjectRegsOrderByName() {
		List<SubjectReg> list = new ArrayList<SubjectReg>();
		list.addAll(subjectRegs);
		Collections.sort(list, new SubjectRegsComparator(""));
		return list;
	}

	
	public void setSubjects(Set<Subject> subjects) {
		subjectRegs = new HashSet<SubjectReg>();
		for ( Subject subject : subjects ) {
			SubjectReg s = new SubjectReg();
			s.setSubject(subject);
			s.setSubjectPeriod(this);
			subjectRegs.add(s);
		}
	}
	
	public void addSubject(Subject subject) {
		if ( subjectRegs == null ) subjectRegs = new HashSet<SubjectReg>();
		SubjectReg s = new SubjectReg();
		s.setSubject(subject);
		s.setSubjectPeriod(this);
		subjectRegs.add(s);
	}
	
	public void addSubject(Subject subject, SubjectCategory category) {
		if ( subjectRegs == null ) subjectRegs = new HashSet<SubjectReg>();
		SubjectReg s = new SubjectReg();
		s.setSubject(subject);
		s.setCategory(category);
		s.setSubjectPeriod(this);
		subjectRegs.add(s);
	}
	
	@Override
	public boolean equals(Object o) {
		SubjectPeriod result = (SubjectPeriod)o;
		System.out.println(result.id);
		if(result.id.equals(id))
			return true;
		else
			return false;
	}
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	public int compareTo(Object o) {
		SubjectPeriod p = (SubjectPeriod) o;
		if ( this.getPeriod() == null || p.getPeriod() == null ) return 0;
		if ( this.getPeriod().getSequence() < p.getPeriod().getSequence() ) return -1;
		else if ( this.getPeriod().getSequence() > p.getPeriod().getSequence() ) return 1;
		else return 0;
	}
	public void setProgramStructure(ProgramStructure programStructure) {
		this.programStructure = programStructure;
	}
	public ProgramStructure getProgramStructure() {
		return programStructure;
	}
	public void delete(Subject subject) {
		if ( subjectRegs == null ) return;
		SubjectReg r = null;
		for ( SubjectReg s : subjectRegs ) {
			if ( s.getSubject().getId().equals(subject.getId())) {
				r = s;
			}
		}
		if ( r != null ) subjectRegs.remove(r);
	}
	public void delete(SubjectReg subject) {
		if ( subjectRegs == null ) return;
		subjectRegs.remove(subject);
	}
	
	public double getFeeTotal(String feeId) {
		double total = 0.0d;
		for ( SubjectReg s : subjectRegs ) {
			for ( FeeStructureItem item : s.getFeeItems() ) {
				if ( item.getFeeItem() != null && item.getFeeItem().getId().equals(feeId)) {
					total += s.getFeeItem(feeId).getAmount();
				}
			}
		}
		return total;
	}
	
	public double getFeeTotal() {
		double total = 0.0d;
		for ( SubjectReg s : subjectRegs ) {
			for ( FeeStructureItem item : s.getFeeItems() ) {
				if ( item.getFeeItem() != null ) total += item.getAmount();
			}
		}
		return total;
	}
	
	
	class SubjectRegsComparator extends educate.util.MyComparator {
		
		String orderBy = "code";
		
		public SubjectRegsComparator(String s) {
			orderBy = s;
		}

		public int compare(Object o1, Object o2) {
			SubjectReg r1 = (SubjectReg) o1;
			SubjectReg r2 = (SubjectReg) o2;
			if ( "code".equals(orderBy))
				return r1.getSubject().getCode().compareTo(r2.getSubject().getCode());
			else
				return r1.getSubject().getName().compareTo(r2.getSubject().getName());
		}
		
	}
	
	
}
