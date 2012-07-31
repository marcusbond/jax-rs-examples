package com.marcusbond.jaxrs.greeting;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A simple model object representing a greeting for a given language code.
 * This version of Greeting relies on JAXB
 * @author Marcus Bond
 *
 */
@XmlRootElement
public class Greeting {

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
