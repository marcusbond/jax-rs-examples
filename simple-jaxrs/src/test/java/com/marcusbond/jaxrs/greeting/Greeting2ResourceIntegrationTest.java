package com.marcusbond.jaxrs.greeting;

import static com.sun.jersey.api.client.ClientResponse.Status.CREATED;
import static com.sun.jersey.api.client.ClientResponse.Status.NO_CONTENT;
import static com.sun.jersey.api.client.ClientResponse.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * JUnit4 integration test that runs the {@link Greeting2Resource} service in an
 * embedded Jetty container, makes various requests on the resource and checks
 * its responses. The main difference here between the
 * {@link GreetingResourceIntegrationTest} is that the Entity class is not JAXB
 * annotated and uses POJO mapping courtesy of Jackson and the entities are
 * mapped on the client side too.
 * 
 * @author Marcus Bond
 * 
 */
public class Greeting2ResourceIntegrationTest {

	private static final String WEBAPP_DIR = "src/main/webapp";
	private static final String RESOURCE_URL = "http://localhost:8080/rest/greetings2";

	private Server server;
	private ClientConfig clientConfig;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		server = new Server(8080);
		server.addHandler(new WebAppContext(WEBAPP_DIR, "/"));
		server.start();

		clientConfig = new DefaultClientConfig();
		clientConfig.getClasses().add(JacksonJsonProvider.class);

		// DELETE all greetings
		Client.create().resource(RESOURCE_URL).type(MediaType.APPLICATION_XML)
				.delete(ClientResponse.class);

		// Always PUT one greeting, the en greeting
		Greeting2 enGreeting = new Greeting2();
		enGreeting.setLang("en");
		enGreeting.setMessage("Good day to you");

		Client.create(clientConfig).resource(RESOURCE_URL).path("en")
				.type(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, enGreeting);
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	/**
	 * Retrieves the en greeting entity
	 */
	@Test
	public void getEn() {

		WebResource resource = Client.create(clientConfig).resource(
				RESOURCE_URL);

		ClientResponse response = resource.path("en")
				.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		Greeting2 greeting = response.getEntity(Greeting2.class);
		assertNotNull(greeting);
		assertEquals("en", greeting.getLang());
		assertEquals("Good day to you", greeting.getMessage());
	}

	/**
	 * PUTs a German greeting as JSON checking the returned code for a create
	 * and update and that the item returned by a GET from the same URI as the
	 * PUT request is as expected.
	 */
	@Test
	public void put() {
		Greeting2 greeting = new Greeting2();
		greeting.setLang("de");
		greeting.setMessage("Guten tag!");

		// Create
		WebResource resource = Client.create(clientConfig)
				.resource(RESOURCE_URL).path("de");
		ClientResponse response = resource.type(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, greeting);
		assertEquals(CREATED.getStatusCode(), response.getStatus());

		// Check the resource can be retrieved on the same URI
		response = resource.get(ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		Greeting2 returnedGreeting = response.getEntity(Greeting2.class);
		assertNotNull(returnedGreeting);
		assertEquals(greeting.getLang(), returnedGreeting.getLang());
		assertEquals(greeting.getMessage(), returnedGreeting.getMessage());
	}

	/**
	 * GETs the {@link List} of Greeting2 entities, in this case there is only
	 * one item in the list.
	 */
	@Test
	public void getGreetingsEntitiesWithOneItem() {
		WebResource resource = Client.create(clientConfig).resource(
				RESOURCE_URL);

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		List<Greeting2> list = response
				.getEntity(new GenericType<List<Greeting2>>() {
				});
		assertNotNull(list);
		assertEquals(1, list.size());
	}

	/**
	 * GETs the {@link List} of Greeting2 entities, in this case there are two
	 * items in the list.
	 */
	@Test
	public void getGreetingsEntitiesWithTwoItems() {
		// Add another Greeting
		Greeting2 greeting = new Greeting2();
		greeting.setLang("fr");
		greeting.setMessage("Bonjour!");

		// Create
		WebResource resource = Client.create(clientConfig)
				.resource(RESOURCE_URL).path("fr");
		ClientResponse response = resource.type(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, greeting);
		assertEquals(CREATED.getStatusCode(), response.getStatus());

		// Get the list
		resource = Client.create(clientConfig).resource(RESOURCE_URL);

		response = resource.accept(MediaType.APPLICATION_JSON).get(
				ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		List<Greeting2> list = response
				.getEntity(new GenericType<List<Greeting2>>() {
				});
		assertNotNull(list);
		assertEquals(2, list.size());
	}

	/**
	 * GETs the {@link List} of Greeting2 entities, in this case there is only
	 * one item in the list.
	 */
	@Test
	public void getGreetingsEmptyList() {
		// DELETE all greetings
		Client.create().resource(RESOURCE_URL).type(MediaType.APPLICATION_XML)
				.delete(ClientResponse.class);
		WebResource resource = Client.create(clientConfig).resource(
				RESOURCE_URL);

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());
		
		// We expect this exception to be thrown when there is no content
		exception.expect(UniformInterfaceException.class);
		response.getEntity(new GenericType<List<Greeting2>>() {
		});
		fail("Should have thrown UniformInterfaceException because there is no content");
	}
}
