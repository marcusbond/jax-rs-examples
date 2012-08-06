package com.marcusbond.jaxrs.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

/**
 * Extends {@link AccessControlFilter} to implement a check as to whether access
 * is allowed and provide an appropriate HTTP response in the event that the request
 * is made by a client that is not authenticated.
 * 
 * @author Marcus Bond
 * 
 */
public class HTTPStatusCodeAuthenticationFilter extends AccessControlFilter {

	/**
	 * This method is copied from AuthenticationFilter
	 * <p>
	 * Determines whether the current subject is authenticated.
	 * <p/>
	 * The default implementation
	 * {@link #getSubject(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 * acquires} the currently executing Subject and then returns
	 * {@link org.apache.shiro.subject.Subject#isAuthenticated()
	 * subject.isAuthenticated()};
	 * 
	 * @return true if the subject is authenticated; false if the subject is
	 *         unauthenticated
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) {
		Subject subject = getSubject(request, response);
		return subject.isAuthenticated();
	}

	/**
	 * Takes responsibility for returning an appropriate response when access is
	 * not allowed.
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		accessDeniedResponse(request, response);
		return false;
	}

	/**
	 * Provides a 401 Not Authorized HTTP status code to the client with a
	 * custom challenge scheme that the client understands and can respond to.
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void accessDeniedResponse(ServletRequest request,
			ServletResponse response) throws Exception {

		HttpServletResponse httpResponse = WebUtils.toHttp(response);
		httpResponse.addHeader("WWW-Authentication", "simple-jaxrs");
		httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}
}
