package com.marcusbond.jaxrs.employee;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.marcusbond.jaxrs.employee.internal.EmployeeService;

/**
 * A Spring managed web resource providing CRUD operations for {@link Employee}
 * records.
 * <p>
 * Permissions security is now the responsibility of the {@link EmployeeService}
 * however all requests to this resource still require authentication via the
 * Shiro filter.
 * 
 * @author Marcus Bond
 * 
 */
@Component
@Scope("request")
@Path("/employees")
public class EmployeeResource {

	private EmployeeService employeeService;

	/**
	 * Sets the {@link EmployeeService} implementation.
	 * 
	 * @param employeeService
	 */
	@Autowired
	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	/**
	 * 
	 * @return A {@link List} containing all employee records
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Employee> getEmployees() {
		return employeeService.listAll();
	}

	/**
	 * 
	 * @return A Single {@link Employee} record
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Employee getEmployee(@PathParam("id") long id) {
		Employee employee = employeeService.findById(id);
		if (employee != null) {
			return employee;
		}
		throw new WebApplicationException(Status.NOT_FOUND);
	}

	/**
	 * POST a new {@link Employee} to the employees, we POST because the id
	 * needs to be generated server side and the location of the new record will
	 * be returned to the client.
	 * 
	 * @param employee
	 * @param uriInfo
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Employee employee, @Context UriInfo uriInfo) {
		URI location = null;
		employeeService.create(employee);

		UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
		uriBuilder.path(Long.toString(employee.getId()));
		location = uriBuilder.build((Object[]) null);

		return Response.created(location).build();
	}

	/**
	 * Update by PUT - Only update is possible by PUT since id's are managed on
	 * server side
	 * 
	 * @param id
	 * @param employee
	 * @return
	 */
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") long id, Employee employee) {
		if (!(id == employee.getId())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		try {
			employeeService.update(employee);
			return Response.noContent().build();
		} catch (IllegalArgumentException e) {
			return Response.status(Status.NOT_FOUND)
					.entity("Record does not exist.").build();
		}
	}

	/**
	 * Deletes the {@link Employee} if found otherwise a 404 is returned.
	 * 
	 * @param id
	 * @return
	 */
	@DELETE
	@Path("{id}")
	public Response delete(@PathParam("id") long id) {
		Employee employee = employeeService.findById(id);
		if (employee == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		try {
			employeeService.delete(employee);
			return Response.noContent().build();
		} catch (IllegalArgumentException e) {
			return Response.status(Status.NOT_FOUND)
					.entity("Record does not exist.").build();
		}
	}
}
