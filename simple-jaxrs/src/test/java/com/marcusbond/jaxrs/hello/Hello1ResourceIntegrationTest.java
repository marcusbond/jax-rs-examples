package com.marcusbond.jaxrs.hello;

import static com.sun.jersey.api.client.ClientResponse.Status.OK;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import com.marcusbond.jaxrs.hello.Hello1Resource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

/**
 * JUnit4 integration test that runs the {@link Hello1Resource} service in an
 * embedded Jetty container and checks the response is as expected.
 * 
 * @author Marcus Bond
 * 
 */
public class Hello1ResourceIntegrationTest {

	private static final String WEBAPP_DIR = "src/main/webapp";
	private static final String HELLO_RESOURCE_URL = "http://localhost:8080/rest/hello/1";

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

	@Test
	public void testResource() {
		ClientResponse response = Client.create().resource(HELLO_RESOURCE_URL)
				.get(ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		assertEquals("Hello!", response.getEntity(String.class));
	}

}
