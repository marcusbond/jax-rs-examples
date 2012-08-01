package com.marcusbond.jaxrs.employee;

import static com.sun.jersey.api.client.ClientResponse.Status.CREATED;
import static com.sun.jersey.api.client.ClientResponse.Status.FORBIDDEN;
import static com.sun.jersey.api.client.ClientResponse.Status.OK;
import static com.sun.jersey.api.client.ClientResponse.Status.UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * JUnit4 integration test that tests the {@link EmployeeResource} service.
 * <p>
 * The main things of interest here are showing Security in action:
 * <ul>
 * <li>Proving a correct response from a request that is not authenticated.</li>
 * <li>Providing HTTP Basic Authentication.</li>
 * <li>Ensuring a correct repsonse from an authenticated but not authorised
 * request.</li>
 * </ul>
 * 
 * @author Marcus Bond
 * 
 */
public class EmployeeResourceIntegrationTest {

	private static final String WEBAPP_DIR = "src/main/webapp";
	private static final String RESOURCE_URL = "http://localhost:8080/rest/employees";

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

	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	/**
	 * Attempts to retrieve an {@link Employee} resource without authenticating
	 */
	@Test
	public void nonAuthenticatedGet() {
		WebResource resource = Client.create(clientConfig)
				.resource(RESOURCE_URL).path("1");

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
	}

	/**
	 * Attempts to retrieve an {@link Employee} resource with authentication
	 */
	@Test
	public void authenticatedGet() {
		WebResource resource = Client.create(clientConfig)
				.resource(RESOURCE_URL).path("1");
		resource.addFilter(new HTTPBasicAuthFilter("Peter", "P3ter"));
		
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		Employee employee = response.getEntity(Employee.class);
		assertNotNull(employee);
		assertEquals(1, employee.getId());
	}

	/**
	 * POSTs a new employee.. security check?
	 */
	@Test
	public void createWithUnauthorizedUser() {
		WebResource resource = Client.create(clientConfig).resource(
				RESOURCE_URL);
		resource.addFilter(new HTTPBasicAuthFilter("Peter", "P3ter"));
		
		Employee employee = new Employee(0, "Bob", "Geldof", "Comedy");
		ClientResponse response = resource.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, employee);
		assertEquals(FORBIDDEN.getStatusCode(), response.getStatus());
	}

	@Test
	public void createWithAuthorizedUser() {
		WebResource resource = Client.create(clientConfig).resource(
				RESOURCE_URL);
		resource.addFilter(new HTTPBasicAuthFilter("Sarah", "S4rah"));
		
		Employee employee = new Employee(0, "Bob", "Geldof", "Comedy");
		ClientResponse response = resource.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, employee);
		assertEquals(CREATED.getStatusCode(), response.getStatus());
	}
}
