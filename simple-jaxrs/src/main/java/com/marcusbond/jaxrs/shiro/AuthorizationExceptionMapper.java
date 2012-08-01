package com.marcusbond.jaxrs.shiro;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.shiro.authz.AuthorizationException;

/**
 * Provides a means for the JAX-RS implementation to handle unchecked Shiro
 * {@link AuthorizationException}s and instead of returning an internal error
 * return a 403 Forbidden response code.
 * 
 * @author Marcus Bond
 * 
 */
@Provider
public class AuthorizationExceptionMapper implements
		ExceptionMapper<AuthorizationException> {

	@Override
	public Response toResponse(AuthorizationException exception) {
		return Response.status(Status.FORBIDDEN).build();
	}

}
