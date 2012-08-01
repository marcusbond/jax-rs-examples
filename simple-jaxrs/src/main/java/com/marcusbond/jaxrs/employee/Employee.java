package com.marcusbond.jaxrs.employee;

import java.io.Serializable;

/**
 * An entity class to represent basic employee details.
 * @author Marcus Bond
 *
 */
public class Employee implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String firstname;
	private String surname;
	private String department;
	
	public Employee() {
		super();
	}
	
	
	public Employee(long id, String firstname, String surname, String department) {
		this();
		this.id = id;
		this.firstname = firstname;
		this.surname = surname;
		this.department = department;
	}


	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
}
