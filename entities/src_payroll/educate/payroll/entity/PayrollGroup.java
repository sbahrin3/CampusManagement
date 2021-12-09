/**
 * 
 */
package educate.payroll.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */

@Entity
@Table(name="empl_payroll_group")
public class PayrollGroup {
	
	@Id @Column(length=50)
	private String id;
	
	@Column(length=100)
	private String name;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="payrollGroup")
	private List<Payroll> payrolls;
	
	public PayrollGroup() {
		setId(lebah.db.UniqueID.getUID());
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

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public List<Payroll> getPayrolls() {
		return payrolls;
	}

	public void setPayrolls(List<Payroll> payrolls) {
		this.payrolls = payrolls;
	}
	
	

}
