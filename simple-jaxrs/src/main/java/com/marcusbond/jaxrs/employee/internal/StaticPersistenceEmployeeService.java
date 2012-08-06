/**
 * 
 */
package com.marcusbond.jaxrs.employee.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.marcusbond.jaxrs.employee.Employee;

/**
 * Implements the {@link EmployeeService} interface with a static Map for persistence.
 * @author Marcus Bond
 *
 */
@Service
public class StaticPersistenceEmployeeService implements EmployeeService {

	private static Map<Long, Employee> employees;
	private static Long lastEmployeeId;

	static {
		lastEmployeeId = 0L;
		// Initialise the employees
		employees = new HashMap<>();
		Employee employee = new Employee(nextEmployeeId(), "Bruce",
				"Springsteen", "Bosses");
		employees.put(employee.getId(), employee);
		employee = new Employee(nextEmployeeId(), "Com", "Truise", "Audio");
		employees.put(employee.getId(), employee);
		employee = new Employee(nextEmployeeId(), "Iggy", "Pop",
				"Pharmaceuticals");
		employees.put(employee.getId(), employee);
	}
	
	/* (non-Javadoc)
	 * @see com.marcusbond.jaxrs.employee.internal.EmployeeService#getAll()
	 */
	@Override
	public List<Employee> listAll() {
		List<Employee> employeeList = new ArrayList<>(employees.values());
		if (employeeList != null && employeeList.size() > 0) {
			return employeeList;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.marcusbond.jaxrs.employee.internal.EmployeeService#findById(long)
	 */
	@Override
	public Employee findById(long id) {
		Employee employee = employees.get(id);
		if (employee != null) {
			return employee;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.marcusbond.jaxrs.employee.internal.EmployeeService#create(com.marcusbond.jaxrs.employee.Employee)
	 */
	@Override
	public void create(Employee employee) {
		employee.setId(nextEmployeeId());
		employees.put(employee.getId(), employee);
	}

	/* (non-Javadoc)
	 * @see com.marcusbond.jaxrs.employee.internal.EmployeeService#update(com.marcusbond.jaxrs.employee.Employee)
	 */
	@Override
	public void update(Employee employee) {
		if (employees.get(employee.getId()) == null) {
			throw new IllegalArgumentException("Employee does not exist.");
		}
		employees.remove(employee.getId());
		employees.put(employee.getId(), employee);
	}

	/* (non-Javadoc)
	 * @see com.marcusbond.jaxrs.employee.internal.EmployeeService#delete(com.marcusbond.jaxrs.employee.Employee)
	 */
	@Override
	public void delete(Employee employee) {
		if (employees.get(employee.getId()) == null) {
			throw new IllegalArgumentException("Employee does not exist.");
		}
		employees.remove(employee.getId());
	}

	private synchronized static long nextEmployeeId() {
		lastEmployeeId++;
		return lastEmployeeId;
	}
	
}
