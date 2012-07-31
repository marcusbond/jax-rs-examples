package com.marcusbond.jaxrs.greeting;

import static com.sun.jersey.api.client.ClientResponse.Status.CREATED;
import static com.sun.jersey.api.client.ClientResponse.Status.NOT_FOUND;
import static com.sun.jersey.api.client.ClientResponse.Status.NO_CONTENT;
import static com.sun.jersey.api.client.ClientResponse.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * JUnit4 integration test that runs the {@link GreetingResource} service in an
 * embedded Jetty container, makes variuos requests on the resource and checks
 * its responses.
 * 
 * @author Marcus Bond
 * 
 */
public class GreetingResourceIntegrationTest {

	private static final String WEBAPP_DIR = "src/main/webapp";
	private static final String RESOURCE_URL = "http://localhost:8080/rest/greetings";

	private Server server;

	@Before
	public void setUp() throws Exception {
		server = new Server(8080);
		server.addHandler(new WebAppContext(WEBAPP_DIR, "/"));
		server.start();

		// DELETE all greetings
		Client.create().resource(RESOURCE_URL).type(MediaType.APPLICATION_XML)
				.delete(ClientResponse.class);

		// Always PUT one greeting, the en greeting
		Client.create()
				.resource(RESOURCE_URL)
				.path("en")
				.type(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class,
						"{\"lang\":\"en\",\"message\":\"Good day to you\"}");
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	/**
	 * Retrieves the en greeting as XML
	 */
	@Test
	public void getEnResourceAsXML() {
		WebResource resource = Client.create().resource(RESOURCE_URL);

		ClientResponse response = resource.path("en")
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		String xml = response.getEntity(String.class);
		assertTrue(xml.indexOf("<lang>en</lang>") >= 0);
	}

	/**
	 * Retrieves the en greeting as JSON
	 */
	@Test
	public void getEnResourceAsJSON() {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
				Boolean.TRUE);

		WebResource resource = Client.create(clientConfig).resource(
				RESOURCE_URL);

		ClientResponse response = resource.path("en")
				.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		JsonNode node = response.getEntity(JsonNode.class);
		assertEquals(node.get("lang").asText(), "en");
		assertEquals(node.get("message").asText(), "Good day to you");
	}

	/**
	 * PUTs a German greeting as JSON checking the returned code for a create
	 * and update and that the item returned by a GET from the same URI as the
	 * PUT request is as expected.
	 */
	@Test
	public void putAsJSON() {
		String greetingJson = "{\"lang\":\"de\",\"message\":\"Guten tag!\"}";

		// Create
		WebResource resource = Client.create().resource(RESOURCE_URL)
				.path("de");
		ClientResponse response = resource.type(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, greetingJson);
		assertEquals(CREATED.getStatusCode(), response.getStatus());

		// Check the resource can be retrieved on the same URI
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
				Boolean.TRUE);

		resource = Client.create(clientConfig).resource(RESOURCE_URL)
				.path("de");

		response = resource.accept(MediaType.APPLICATION_JSON).get(
				ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		JsonNode node = response.getEntity(JsonNode.class);
		assertEquals(node.get("lang").asText(), "de");
		assertEquals(node.get("message").asText(), "Guten tag!");

		// Update
		resource = Client.create().resource(RESOURCE_URL).path("de");
		response = resource.type(MediaType.APPLICATION_JSON).put(
				ClientResponse.class, greetingJson);
		assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());

		// Check the resource can be retrieved on the same URI
		clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
				Boolean.TRUE);

		resource = Client.create(clientConfig).resource(RESOURCE_URL)
				.path("de");

		response = resource.accept(MediaType.APPLICATION_JSON).get(
				ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		node = response.getEntity(JsonNode.class);
		assertEquals(node.get("lang").asText(), "de");
		assertEquals(node.get("message").asText(), "Guten tag!");
	}

	/**
	 * POSTs a French greeting as XML checking the returned code for a create
	 * and update and that the item returned by a GET from the returned Location
	 * header is as expected.
	 */
	@Test
	public void postAsXML() {
		String greetingXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><greeting><lang>fr</lang><message>Bonjour!</message></greeting>";

		// Create
		WebResource resource = Client.create().resource(RESOURCE_URL);
		ClientResponse response = resource.type(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, greetingXml);
		assertEquals(CREATED.getStatusCode(), response.getStatus());

		// Check the location returned for the new resource
		URI resourceUri = response.getLocation();
		assertEquals(RESOURCE_URL + "/fr", resourceUri.toString());

		// Get the item at the returned location
		resource = Client.create().resource(resourceUri);
		response = resource.accept(MediaType.APPLICATION_XML).get(
				ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		String xml = response.getEntity(String.class);
		assertTrue(xml.indexOf("<lang>fr</lang>") >= 0);

		// Update
		resource = Client.create().resource(RESOURCE_URL);
		response = resource.type(MediaType.APPLICATION_XML).post(
				ClientResponse.class, greetingXml);
		assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());

		// Check the location returned for the updated resource
		resourceUri = response.getLocation();
		assertEquals(RESOURCE_URL + "/fr", resourceUri.toString());

		// Get the item at the returned location
		resource = Client.create().resource(resourceUri);
		response = resource.accept(MediaType.APPLICATION_XML).get(
				ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		xml = response.getEntity(String.class);
		assertTrue(xml.indexOf("<lang>fr</lang>") >= 0);
	}

	/**
	 * DELETEs all greetings and checks that a NO CONTENT reponse is returned on
	 * a subsequent GET request.
	 */
	@Test
	public void deleteAllGreetings() {
		// Check we can get the en greeting
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
				Boolean.TRUE);

		WebResource resource = Client.create(clientConfig)
				.resource(RESOURCE_URL).path("en");

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		JsonNode node = response.getEntity(JsonNode.class);
		assertEquals(node.get("lang").asText(), "en");

		resource = Client.create().resource(RESOURCE_URL);
		response = resource.delete(ClientResponse.class);
		assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());

		// Try and get the en resource again
		resource = Client.create(clientConfig).resource(RESOURCE_URL);

		response = resource.accept(MediaType.APPLICATION_JSON).get(
				ClientResponse.class);
		assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());
	}

	/**
	 * DELETEs the en greeting and checks that a NOT FOUND response is returned
	 * by a subsequent GET request
	 */
	@Test
	public void deleteGreeting() {

		// Check we can get the en greeting
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
				Boolean.TRUE);

		WebResource resource = Client.create(clientConfig)
				.resource(RESOURCE_URL).path("en");

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		JsonNode node = response.getEntity(JsonNode.class);
		assertEquals(node.get("lang").asText(), "en");

		// Delete the en resource
		resource = Client.create().resource(RESOURCE_URL).path("en");
		response = resource.delete(ClientResponse.class);
		assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());

		// Try and get the en resource again
		resource = Client.create(clientConfig).resource(RESOURCE_URL)
				.path("en");

		response = resource.accept(MediaType.APPLICATION_JSON).get(
				ClientResponse.class);
		assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
	}

	/**
	 * Retrieves the list of greetings with a single entry
	 */
	@Test
	public void getGreetingsWithOneItemXML() {
		WebResource resource = Client.create().resource(RESOURCE_URL);

		ClientResponse response = resource.accept(MediaType.APPLICATION_XML)
				.get(ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		String xml = response.getEntity(String.class);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><greetings><greeting><lang>en</lang><message>Good day to you</message></greeting></greetings>",
				xml);
	}

	/**
	 * Retrieves the list of greetings with a two entries
	 */
	@Test
	public void getGreetingsWithTwoItemsXML() {
		// Add another Greeting
		Client.create()
				.resource(RESOURCE_URL)
				.path("fr")
				.type(MediaType.APPLICATION_XML)
				.put(ClientResponse.class,
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?><greeting><lang>fr</lang><message>Bonjour!</message></greeting>");

		WebResource resource = Client.create().resource(RESOURCE_URL);

		ClientResponse response = resource.accept(MediaType.APPLICATION_XML)
				.get(ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());
		String xml = response.getEntity(String.class);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><greetings><greeting><lang>fr</lang><message>Bonjour!</message></greeting><greeting><lang>en</lang><message>Good day to you</message></greeting></greetings>",
				xml);
	}

	/**
	 * Retrieves the list of greetings.. By default this is not as expected so
	 * the test will always fail
	 * <p>
	 * You would expect a response containing something along the lines of
	 * [{"lang":"en","message":"Good day to you"}] but instead there is no array
	 * and {"lang":"en","message":"Good day to you"} is returned.
	 */
	@Test
	public void getGreetingsWithOneItemJSON() {
		WebResource resource = Client.create().resource(RESOURCE_URL);

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());

		String json = response.getEntity(String.class);
		assertEquals("[{\"lang\":\"en\",\"message\":\"Good day to you\"}]",
				json);
	}

	/**
	 * Retrieves the list of greetings.. This behaves as expected and the
	 * returned data is an array.
	 */
	@Test
	public void getGreetingsWithTwoItemsJSON() {
		// Add another Greeting
		Client.create()
				.resource(RESOURCE_URL)
				.path("fr")
				.type(MediaType.APPLICATION_XML)
				.put(ClientResponse.class,
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?><greeting><lang>fr</lang><message>Bonjour!</message></greeting>");

		WebResource resource = Client.create().resource(RESOURCE_URL);

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		assertEquals(OK.getStatusCode(), response.getStatus());

		String json = response.getEntity(String.class);
		assertEquals(
				"[{\"lang\":\"fr\",\"message\":\"Bonjour!\"},{\"lang\":\"en\",\"message\":\"Good day to you\"}]",
				json);
	}
}
