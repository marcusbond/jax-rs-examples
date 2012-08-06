package com.marcusbond.jaxrs.employee.internal;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.marcusbond.jaxrs.employee.Employee;

/**
 * Defines the {@link Employee} service API along with annotations for the
 * permissions required to execute the methods. This illustrates the basic use
 * of Apache Shiro security via annotations.
 * <p>
 * In order to view, add, update and delete a user requires the employee:view,
 * employee:add, employee:update, and employee:remove permissions respectively
 * as declared in the {@link RequiresPermissions} annotations.
 * 
 * @author Marcus Bond
 * 
 */
public interface EmployeeService {

	/**
	 * @return A {@link List} containing all employee records
	 */
	@RequiresPermissions("employee:view")
	abstract List<Employee> listAll();

	/**
	 * 
	 * @return A Single {@link Employee} record
	 */
	@RequiresPermissions("employee:view")
	abstract Employee findById(long id);

	/**
	 * Creates a new {@link Employee}
	 * 
	 * @param employee
	 */
	@RequiresPermissions("employee:create")
	abstract void create(Employee employee);

	/**
	 * Updates an {@link Employee}
	 * 
	 * @param employee
	 */
	@RequiresPermissions("employee:update")
	abstract void update(Employee employee);

	@RequiresPermissions("employee:remove")
	/**
	 * Deletes an {@link Employee}
	 * 
	 * @param employee
	 */
	abstract void delete(Employee employee);
}
