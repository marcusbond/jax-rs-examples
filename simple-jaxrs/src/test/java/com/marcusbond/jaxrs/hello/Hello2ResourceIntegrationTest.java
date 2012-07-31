package com.marcusbond.jaxrs.hello;

import static com.sun.jersey.api.client.ClientResponse.Status.NOT_FOUND;
import static com.sun.jersey.api.client.ClientResponse.Status.NO_CONTENT;
import static com.sun.jersey.api.client.ClientResponse.Status.OK;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

/**
 * JUnit4 integration test that runs the {@link Hello2Resource} service in an
 * embedded Jetty container and checks the response is as expected.
 * <p>
 * The testResource method runs through each RESTful service method and
 * subsequently performs a check on the returned status code and message body
 * along with folow up checks to prove the state on the server is as expected.
 * 
 * @author Marcus Bond
 * 
 */
public class Hello2ResourceIntegrationTest {

	private static final String WEBAPP_DIR = "src/main/webapp";
	private static final String HELLO_RESOURCE_URL = "http://localhost:8080/rest/hello/2";

	private Server server;

	@Before
	public void setUp() throws Exception {
		server = new Server(8080);
		server.addHandler(new WebAppContext(WEBAPP_DIR, "/"));
		server.start();
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	/**
	 * Retrieves the original greeting, updates the greeting and retrieves
	 * again.
	 */
	@Test
	public void testResource() {
		// Code blocks are used to make each part of the process clear

		// GET
		{
			ClientResponse response = Client.create()
					.resource(HELLO_RESOURCE_URL).get(ClientResponse.class);
			assertEquals(OK.getStatusCode(), response.getStatus());
			assertEquals("Hello!", response.getEntity(String.class));
		}

		// POST
		{
			ClientResponse response = Client.create()
					.resource(HELLO_RESOURCE_URL)
					.post(ClientResponse.class, "Goodbye!");
			assertEquals(OK.getStatusCode(), response.getStatus());
			assertEquals("Goodbye!", response.getEntity(String.class));
		}

		// GET (Check)
		{
			ClientResponse response = Client.create()
					.resource(HELLO_RESOURCE_URL).get(ClientResponse.class);
			assertEquals(OK.getStatusCode(), response.getStatus());
			assertEquals("Goodbye!", response.getEntity(String.class));
		}

		// PUT
		{
			ClientResponse response = Client.create()
					.resource(HELLO_RESOURCE_URL)
					.put(ClientResponse.class, "Hello Again!");
			assertEquals(OK.getStatusCode(), response.getStatus());
			assertEquals("Hello Again!", response.getEntity(String.class));
		}

		// GET (Check)
		{
			ClientResponse response = Client.create()
					.resource(HELLO_RESOURCE_URL).get(ClientResponse.class);
			assertEquals(OK.getStatusCode(), response.getStatus());
			assertEquals("Hello Again!", response.getEntity(String.class));
		}

		// DELETE
		{
			ClientResponse response = Client.create()
					.resource(HELLO_RESOURCE_URL).delete(ClientResponse.class);
			assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());
		}

		// GET (Check)
		{
			ClientResponse response = Client.create()
					.resource(HELLO_RESOURCE_URL).get(ClientResponse.class);
			assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
		}
	}

}
