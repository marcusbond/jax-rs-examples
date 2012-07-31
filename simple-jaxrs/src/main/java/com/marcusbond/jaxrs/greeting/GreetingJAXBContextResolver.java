package com.marcusbond.jaxrs.greeting;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

/**
 * This class provides the magic to ensure that the JSON output from the
 * serialized {@link Greeting} class is as expected both in terms of providing
 * an array for a collection containing a single item and ensuring that only the
 * required JSON is returned instead of an unwanted JSON object with a greeting
 * property for the collection. To enable it uncomment the {@link Provider} annotation.
 * 
 * @author Marcus Bond
 * 
 */
//@Provider
public class GreetingJAXBContextResolver implements
		ContextResolver<JAXBContext> {

	private JAXBContext context;

	public GreetingJAXBContextResolver() throws JAXBException {
		context = new JSONJAXBContext(JSONConfiguration.natural().build(),
				Greeting.class);
	}

	@Override
	public JAXBContext getContext(Class<?> type) {
		return context;
	}
}
