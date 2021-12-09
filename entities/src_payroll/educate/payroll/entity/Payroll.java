/**
 * 
 */
package educate.payroll.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */

@Entity
@Table(name="empl_payroll")
public class Payroll {

	
	@Id @Column(length=50)
	private String id;
	
	@ManyToOne @JoinColumn(name="group_id")
	private PayrollGroup payrollGroup;
	
	@ManyToOne @JoinColumn(name="employee_id")
	private Employee employee;
	
	public Payroll() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}



	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public PayrollGroup getPayrollGroup() {
		return payrollGroup;
	}

	public void setPayrollGroup(PayrollGroup payrollGroup) {
		this.payrollGroup = payrollGroup;
	}
	
	
	
}
