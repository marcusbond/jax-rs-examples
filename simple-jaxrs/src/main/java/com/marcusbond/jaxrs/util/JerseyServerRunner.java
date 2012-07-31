package com.marcusbond.jaxrs.util;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Provides a convenient way to launch the Jetty server so that the JAX-RS
 * resources can be accessed by a client of your choice.
 * 
 * @author Marcus Bond
 * 
 */
public class JerseyServerRunner {

	private static final String WEBAPP_DIR = "src/main/webapp";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		server.addHandler(new WebAppContext(WEBAPP_DIR, "/"));
		server.start();

		final Object o = new Object();
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (o) {
					try {
						o.wait();
					} catch (InterruptedException e) {
						System.exit(1);
					}
				}
			}
		});
		t.start();
		t.join();
	}
}
