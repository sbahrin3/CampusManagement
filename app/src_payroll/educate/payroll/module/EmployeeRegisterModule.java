/**
 * 
 */
package educate.payroll.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.payroll.entity.Employee;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class EmployeeRegisterModule extends LebahModule {
	
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/payroll/employees";

	/* (non-Javadoc)
	 * @see lebah.portal.action.LebahModule#start()
	 */
	@Override
	public String start() {
		try {
			listEmployees();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path + "/start.vm";
	}
	
	@Command("listEmployees")
	public String listEmployees() throws Exception {
		List<Employee> employees = db.list("select e from Employee e order by e.name");
		context.put("employees", employees);
		return path + "/listEmployees.vm";
	}
	
	private boolean isNumber(String no) {
		try {
			Double.parseDouble(no);
			return true;
		} catch ( Exception e ) {
			return false;
		}
	}
	
	@Command("addNewEmployee")
	public String addNewEmployee() throws Exception {
		context.remove("employee");
		return path + "/dataInput.vm";
	}
	
	@Command("addEmployee")
	public String addEmployee() throws Exception {
		
		if ( getParam("name").equals("") || getParam("icNo").equals("") ) return listEmployees();
		
		Employee employee = new Employee();
		employee.setName(getParam("name"));
		employee.setIcNo(getParam("icNo"));
		employee.setBasicSalary(isNumber(getParam("basicSalary")) ? Double.parseDouble(getParam("basicSalary")) : 0.0d);
		db.begin();
		db.persist(employee);
		db.commit();
		return listEmployees();
	}
	
	@Command("editEmployee")
	public String editEmployee() throws Exception {
		Employee employee = db.find(Employee.class, getParam("employeeId"));
		context.put("employee", employee);
		return path + "/dataInput.vm";
	}
	
	@Command("saveEmployee")
	public String saveEmployee() throws Exception {
		Employee employee = db.find(Employee.class, getParam("employeeId"));
		if ( employee != null ) {
			employee.setName(getParam("name"));
			employee.setIcNo(getParam("icNo"));
			employee.setBasicSalary(Double.parseDouble(getParam("basicSalary")));
			db.begin();
			db.persist(employee);
			db.commit();
		}
		return listEmployees();
	}
	
	@Command("deleteEmployee")
	public String deleteEmployee() throws Exception {
		Employee employee = db.find(Employee.class, getParam("employeeId"));
		if ( employee != null ) {
			db.begin();
			db.remove(employee);
			db.commit();
		}
		return listEmployees();
	}

}
