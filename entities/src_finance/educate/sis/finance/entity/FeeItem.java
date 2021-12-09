package educate.sis.finance.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="fin_fee_item")
public class FeeItem {
	
	@Id
	private String id;
	private String code;
	private String description;
	private int seq;
	private String feeType; //values are "PROGRAM", "SUBJECT"
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="feeItem")
	private Set<FeeStructureItem> feeStructureItems;
	
	private int subjectType; //0=none, 1=credit hours, 2=subjects
	private int partnerType; //0=none, 1=yes
	private int payingMode; //0=not defined, 1=one time payment, 2=flexi payment, 3=monthly payment,
	
	public FeeItem() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public Set<FeeStructureItem> getFeeStructureItems() {
		return feeStructureItems;
	}

	public void setFeeStructureItems(Set<FeeStructureItem> feeStructureItems) {
		this.feeStructureItems = feeStructureItems;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(int subjectType) {
		this.subjectType = subjectType;
	}

	public int getPartnerType() {
		return partnerType;
	}

	public void setPartnerType(int partnerType) {
		this.partnerType = partnerType;
	}

	public int getPayingMode() {
		return payingMode;
	}

	public void setPayingMode(int payingMode) {
		this.payingMode = payingMode;
	}
	
	
	
	

}
