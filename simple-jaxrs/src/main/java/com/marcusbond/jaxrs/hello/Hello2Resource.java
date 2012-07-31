package com.marcusbond.jaxrs.hello;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * A very simple JAX-RS service that shows how to set up a resource that
 * responds to a GET and POST request at a given resource URL.
 * 
 * @author Marcus Bond
 * 
 */
@Path("/hello/2")
public class Hello2Resource {

	private static String greeting = "Hello!";

	
	/**
	 * @return A {@link Response} containing the greeting if it exists otherwise a 404 Not Found
	 */
	@GET
	public Response getGreeting() {
		if(greeting != null) {
			return Response.ok(Hello2Resource.greeting).build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	
	/**
	 * POSTs a new greeting
	 * @param greeting The new greeting
	 * @return A {@link Response} with a 200 OK code and the greeting
	 */
	@POST
	public Response postGreeting(String greeting) {
		Hello2Resource.greeting = greeting;
		return Response.ok(Hello2Resource.greeting).build();
	}

	/**
	 * PUTs a new greeting
	 * @param greeting The new greeting
	 * @return A {@link Response} with a 200 OK code and the greeting
	 */
	@PUT
	public Response putGreeting(String greeting) {
		Hello2Resource.greeting = greeting;
		return Response.ok(Hello2Resource.greeting).build();
	}

	/**
	 * DELETEs the greeting
	 * @return A 204 No Content response
	 */
	@DELETE
	public Response deleteGreeting() {
		Hello2Resource.greeting = null;
		return Response.noContent().build();
	}

}
