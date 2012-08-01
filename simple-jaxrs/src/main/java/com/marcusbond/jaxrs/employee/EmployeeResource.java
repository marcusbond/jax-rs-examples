package com.marcusbond.jaxrs.employee;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.ws.rs.core.UriInfo;

import org.apache.shiro.SecurityUtils;

/**
 * A web resource providing CRUD operations for {@link Employee} records. This
 * illustrates the basic use of Apache Shiro security.
 * <p>
 * All requests to this resource must be authenticated (handled by filter) and
 * to add, update and delete a user requires the employee:add employee:update
 * and employee:remove permissions respectively
 * 
 * @author Marcus Bond
 * 
 */
@Path("/employees")
public class EmployeeResource {

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

	/**
	 * 
	 * @return A {@link List} containing all employee records
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Employee getEmployee(@PathParam("id") long id) {
		// This will throw an Authorization exception if the current "Subject"
		// does not have the required permission
		SecurityUtils.getSubject().checkPermission("employee:get");
		
		Employee employee = employees.get(id);
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
		SecurityUtils.getSubject().checkPermission("employee:add");

		URI location = null;
		employee.setId(nextEmployeeId());
		employees.put(employee.getId(), employee);

		try {
			location = new URI(uriInfo.getAbsolutePath() + "/"
					+ employee.getId());
		} catch (URISyntaxException e) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}

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
		SecurityUtils.getSubject().checkPermission("employee:update");
		
		if (employees.get(id) == null) {
			return Response.status(Status.NOT_FOUND)
					.entity("Record does not exist.").build();
		}
		if (!(id == employee.getId())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		employees.remove(id);
		employees.put(id, employee);
		return Response.noContent().build();
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
		SecurityUtils.getSubject().checkPermission("employee:remove");

		if (employees.get(id) == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		employees.remove(id);
		return Response.noContent().build();
	}

	private synchronized static long nextEmployeeId() {
		lastEmployeeId++;
		return lastEmployeeId;
	}

}
