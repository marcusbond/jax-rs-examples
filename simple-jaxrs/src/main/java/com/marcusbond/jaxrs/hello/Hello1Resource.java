package com.marcusbond.jaxrs.hello;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * A very simple JAX-RS service that shows how to set up a resource that
 * responds to a GET request at a given resource URL.
 * 
 * @author Marcus Bond
 * 
 */
@Path("/hello/1")
public class Hello1Resource {

	@GET
	public Response sayHello() {
		return Response.ok("Hello!").build();

	}
}
