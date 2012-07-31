package com.marcusbond.jaxrs.greeting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * The API for {@link Greeting2}s.
 * <p>
 * Note that this resource only supports JSON since we are using the json
 * POJOMappingFeature without any JAXB annotations on the {@link Greeting2}
 * class.
 * 
 * @author Marcus Bond
 * 
 */
@Path("/greetings2")
public class Greeting2Resource {

	private static Map<String, Greeting2> greetings = new HashMap<>();

	public Greeting2Resource() {
	}

	/**
	 * Provide the list of greetings
	 * 
	 * @return A List of Greetings if there are any, otherwise null.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Greeting2> getGreetings() {
		List<Greeting2> greetingsList = new ArrayList<>(greetings.values());
		if (greetingsList != null && greetingsList.size() > 0) {
			return greetingsList;
		}
		return null;
	}

	/**
	 * Provides a single greeting corresponding to the path given as JSON.
	 * 
	 * @param lang
	 *            The language code of the required greeting
	 * @return The greeting identified by the lang parameter if it exists
	 *         otherwise a 404 Not Found error is returned
	 */
	@GET
	@Path("{lang}")
	@Produces(MediaType.APPLICATION_JSON)
	public Greeting2 getGreeting(@PathParam("lang") String lang) {
		Greeting2 greeting = greetings.get(lang);
		if (greeting != null) {
			return greeting;
		}
		// Note if null is returned then Jersey returns a 204, since the
		// resource does not exist we throw a WebApplicationException with the
		// 404 code
		throw new WebApplicationException(Status.NOT_FOUND);
	}

	/**
	 * PUT is idempotent and the URI given is expected to be the location that
	 * the resource can be later retrieved by a GET, as such ony the status code
	 * is returned.
	 * 
	 * @param lang
	 *            The language code of the greeting, this forms part of the
	 *            resource path
	 * @param greeting
	 *            The greeting to be stored
	 * @return The HTTP Status code of CREATED for a create and NO Content for
	 *         an update
	 */
	@PUT
	@Path("{lang}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateOrCreateGreeting(@PathParam("lang") String lang,
			Greeting2 greeting) {
		greeting.setLang(lang);
		boolean exists = exists(greeting);
		createOrUpdate(greeting);

		if (exists) {
			return Response.noContent().build();
		} else {
			return Response.status(Status.CREATED).build();
		}
	}

	/**
	 * DELETEs all greetings
	 * 
	 * @return
	 */
	@DELETE
	public Response delete() {
		greetings.clear();
		return Response.noContent().build();
	}

	/**
	 * Helper method to check if a greeting exists
	 * 
	 * @param greeting
	 * @return
	 */
	private static boolean exists(Greeting2 greeting) {
		return greetings.containsKey(greeting.getLang());
	}

	/**
	 * Helper method to create or update the greeting
	 * 
	 * @param greeting
	 */
	private static void createOrUpdate(Greeting2 greeting) {
		if (greeting == null) {
			throw new NullPointerException("Greeting cannot be null");
		}
		greetings.put(greeting.getLang(), greeting);
	}
}
