package com.marcusbond.jaxrs.greeting;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

/**
 * The API for {@link Greeting}s
 * @author Marcus Bond
 *
 */
@Path("/greetings")
public class GreetingResource {

	private static Map<String, Greeting> greetings = new HashMap<>();

	public GreetingResource() {
	}

	/**
	 * Provide the list of greetings in either XML or JSON depending upon the
	 * accepted media type of the request.
	 * 
	 * @return A List of Greetings if there are any, otherwise null.
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Greeting> getGreetings() {
		List<Greeting> greetingsList = new ArrayList<>(greetings.values());
		if (greetingsList != null && greetingsList.size() > 0) {
			return greetingsList;
		}
		return null;
	}

	/**
	 * Provides a single greeting corresponding to the path given as either XML
	 * or JSON depending upon the accepted media type of the request.
	 * 
	 * @param lang
	 *            The language code of the required greeting
	 * @return The greeting identified by the lang parameter if it exists
	 *         otherwise a 404 Not Found error is returned
	 */
	@GET
	@Path("{lang}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Greeting getGreeting(@PathParam("lang") String lang) {
		Greeting greeting = greetings.get(lang);
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
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response updateOrCreateGreeting(@PathParam("lang") String lang,
			Greeting greeting) {
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
	 * POST does not have to be idempotent although in this instance the request
	 * is idempotent. However POST is used here since we are posting to the URI
	 * of the list and the actual URI of the created / updated resource is
	 * different and needs to be returned to the client in the Location header.
	 * 
	 * @param greeting
	 *            The greeting to be stored
	 * @param uriInfo
	 *            Automatically provided by the implementation providing context
	 *            for the request
	 * @return
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response updateOrCreateGreeting(Greeting greeting,
			@Context UriInfo uriInfo) {

		boolean exists = exists(greeting);
		createOrUpdate(greeting);

		URI location = null;

		try {
			location = new URI(uriInfo.getAbsolutePath() + "/"
					+ greeting.getLang());
		} catch (URISyntaxException e) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}

		if (exists) {
			return Response.noContent().location(location).build();
		} else {
			return Response.created(location).build();
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
	 * DELETEs the Greeting found at the path represented by the lang param
	 * 
	 * @param lang
	 *            The language code of the greeting to be deleted, forms part of
	 *            the resource URI
	 * @return A NO Content HTTP status code in the event the resource was found
	 *         and deleted otherwise a 404 Not found
	 */
	@DELETE
	@Path("{lang}")
	public Response delete(@PathParam("lang") String lang) {
		if (greetings.get(lang) != null) {
			greetings.remove(lang);
			return Response.noContent().build();
		}
		throw new WebApplicationException(Status.NOT_FOUND);
	}

	/**
	 * Helper method to check if a greeting exists
	 * 
	 * @param greeting
	 * @return
	 */
	private static boolean exists(Greeting greeting) {
		return greetings.containsKey(greeting.getLang());
	}

	/**
	 * Helper method to create or update the greeting
	 * 
	 * @param greeting
	 */
	private static void createOrUpdate(Greeting greeting) {
		if (greeting == null) {
			throw new NullPointerException("Greeting cannot be null");
		}
		greetings.put(greeting.getLang(), greeting);
	}
}
