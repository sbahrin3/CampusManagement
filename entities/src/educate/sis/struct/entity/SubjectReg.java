package educate.sis.struct.entity;

import javax.persistence.*;

import educate.sis.finance.entity.FeeStructureItem;

import java.util.*;


@Entity
@Table(name="struc_subject_reg")
public class SubjectReg {
	
	@Id
	private String id;
	@OneToOne
	private Subject subject;
	@OneToOne
	private SubjectCategory category;
	@ManyToOne
	private SubjectPeriod subjectPeriod;
	
	private int offered;
	
	//Fee Information
	@OneToMany(cascade=CascadeType.PERSIST)
	Set<FeeStructureItem> feeItems;
	
	public SubjectReg() {
		setId(lebah.db.UniqueID.getUID());
	}

	public SubjectCategory getCategory() {
		return category;
	}

	public void setCategory(SubjectCategory category) {
		this.category = category;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public SubjectPeriod getSubjectPeriod() {
		return subjectPeriod;
	}

	public void setSubjectPeriod(SubjectPeriod subjectPeriod) {
		this.subjectPeriod = subjectPeriod;
	}

	public Set<FeeStructureItem> getFeeItems() {
		return feeItems;
	}

	public void setFeeItems(Set<FeeStructureItem> feeItems) {
		this.feeItems = feeItems;
	}
	
	public void addFeeItem(FeeStructureItem item) {
		if ( feeItems == null ) feeItems = new HashSet<FeeStructureItem>();
		feeItems.add(item);
	}
	
	public void removeFeeItem(FeeStructureItem item) {
		feeItems.remove(item);
	}
	
	public FeeStructureItem getFeeItem(String feeId) {
		if ( feeItems == null ) return null;
		for ( FeeStructureItem item : feeItems ) {
			if ( item.getFeeItem() != null && item.getFeeItem().getId().equals(feeId)) {
				return item;
			}
		}
		return null;
	}
	
	public double getFeeTotal() {
		double total = 0.0d;
		if ( feeItems == null ) return 0.0d;
		for ( FeeStructureItem item : feeItems ) {
			if ( item.getFeeItem() != null ) {
				total += item.getAmount();
			}
		}
		return total;
	}

	public boolean getOffered() {
		return offered == 1;
	}

	public void setOffered(boolean offered) {
		this.offered = offered ? 1 : 0;
	}



}
