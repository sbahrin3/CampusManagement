package educate.sis.finance.entity;

import javax.persistence.*;

import java.util.*;

import educate.sis.struct.entity.Program;

@Entity
@Table(name="fin_fee_structure")
public class FeeStructure {
	
	@Id
	private String id;
	@ManyToOne
	private Program program;
	@Temporal(TemporalType.DATE)
	private Date sessionDate;
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="feeStructure")
	private Set<FeeStructureItem> items;
	private double amountPerCredit; //amount of fee per credit hours of subjects registered - should be deprecated
	
	public FeeStructure() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public double getAmountPerCredit() {
		return amountPerCredit;
	}
	public void setAmountPerCredit(double amountPerCredit) {
		this.amountPerCredit = amountPerCredit;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public Set<FeeStructureItem> getItems() {
		return items;
	}
	public void setItems(Set<FeeStructureItem> items) {
		this.items = items;
	}
	
	public void addItem(FeeStructureItem item) {
		if ( items == null ) items = new HashSet<FeeStructureItem>();
		item.setFeeStructure(this);
		items.add(item);
	}
	
	public void removeItem(FeeStructureItem item) {
		items.remove(item);
	}
	
	public Program getProgram() {
		return program;
	}
	public void setProgram(Program program) {
		this.program = program;
	}

	public Date getSessionDate() {
		return sessionDate;
	}

	public void setSessionDate(Date sessionDate) {
		this.sessionDate = sessionDate;
	}


}
