package com.marcusbond.jaxrs.greeting;


/**
 * A simple model object representing a greeting for a given language code.
 * Note that there are no JAXB mappings.
 * @author Marcus Bond
 *
 */
public class Greeting2 {

	private String lang;
	private String message;
	
	
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
