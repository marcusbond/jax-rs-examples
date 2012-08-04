if (typeof console == "undefined") {
	this.console = {
		log : function() {
		}
	};
}

var App = {};
App.apiBaseUrl = "rest-webapp/";
// Form validation util methods
App.FormValidation = {
	updateTips : function(t) {
		var tips = $(".validateTips");
		tips.text(t).addClass("ui-state-highlight");
		setTimeout(function() {
			tips.removeClass("ui-state-highlight", 1500);
		}, 500);
	},
	checkLength : function(o, n, min, max) {
		if (o.val().length > max || o.val().length < min) {
			o.addClass("ui-state-error");
			App.FormValidation.updateTips("Length of " + n
					+ " must be between " + min + " and " + max + ".");
			return false;
		} else {
			return true;
		}
	}
};

/**
 * Handles common errors that need to be handled consistently. The checks
 * performed are for a 403 Forbidden and a 401 not authorized with a custom
 * challenge scheme. In the case of an authentication challenge the user is
 * redirected to the login page and in the case of a non permitted action a
 * simple dialog is shown.
 */
App.GlobalErrors = {
	// Returns true if the error was handled, false otherwise
	ajaxError : function(jqXHR, textStatus, errorThrown) {
		var httpStatusCode = jqXHR.status;
		if (httpStatusCode === 403) {
			$("#error-403").dialog();
			return true;
		} else if (httpStatusCode === 401
				&& jqXHR.getResponseHeader('WWW-Authentication') != null) {
			// Equivalent of a redirect
			window.location.replace("login.html");
			return true;
		}
		return false;
	}
};

/**
 * Create a JQuery dialog for the login form
 */
App.presentLogin = function() {
	var username = $("#username");
	var password = $("#password");

	var allFields = $([]).add(username).add(password);

	// Respond to Enter key by triggering button press
	$("#dialog-login-form").keypress(function(e) {
		if (e.keyCode == $.ui.keyCode.ENTER) {
			$(this).parent().find("button:eq(0)").trigger("click");
		}
	});

	/*
	 * dialogClass: 'no-close' and beforeclose : function() { return false; }
	 * attempts to prevent the user from closing the dialog
	 */
	$("#dialog-login-form").dialog(
			{
				dialogClass : 'no-close',
				beforeclose : function() {
					return false;
				},
				autoOpen : true,
				height : 260,
				width : 350,
				modal : true,
				buttons : {
					"Login" : function() {
						var bValid = true;
						allFields.removeClass("ui-state-error");

						// Ensure min / max length of login form fields just for
						// the hell of it
						bValid = bValid
								&& App.FormValidation.checkLength(username,
										"username", 3, 16);
						bValid = bValid
								&& App.FormValidation.checkLength(password,
										"password", 3, 16);

						if (bValid) {
							$("form", $("#dialog-login-form")).submit();
							$(this).dialog("close");
						}
					},
				},
			});
};

App.bindEmployeesPage = function() {
	var EmployeesPage = {};

	EmployeesPage.addToList = function(employee) {
		var row = $('#employee-templates .employee-row').clone();
		$('.firstname', row).text(employee.firstname);
		$('.surname', row).text(employee.surname);
		$('.department', row).text(employee.department);
		// Add the employee id to the row
		$(row).attr('id', 'employee' + employee.id);
		// You could add a handler to do something like open an update / delete
		// dialog
		$(row).click(function() {
			if (console) {
				console.log('User clicked table row ' + $(this).attr('id'));
			}
		});

		row.appendTo($('#employee-list tbody'));
	};

	EmployeesPage.addAllToList = function(employeeList) {
		$.each(employeeList, function(index, employee) {
			EmployeesPage.addToList(employee);
		});
	};

	// Bind the create employee button
	$("#create-employee").button().click(function() {
		$("#dialog-create-employee-form").dialog("open");
	});

	// The create form
	var firstname = $("#firstname");
	var surname = $("#surname");
	var department = $("#department");

	var allFields = $([]).add(firstname).add(surname).add(department);

	$("#dialog-create-employee-form")
			.dialog(
					{
						autoOpen : false,
						height : 320,
						width : 350,
						modal : true,
						buttons : {
							"Create employee" : function() {
								var bValid = true;
								allFields.removeClass("ui-state-error");

								bValid = bValid
										&& App.FormValidation.checkLength(
												firstname, "firstname", 1, 50);
								bValid = bValid
										&& App.FormValidation.checkLength(
												surname, "surname", 1, 50);
								bValid = bValid
										&& App.FormValidation
												.checkLength(department,
														"department", 1, 16);

								if (bValid) {
									var employee = {
										firstname : firstname.val(),
										surname : surname.val(),
										department : department.val()
									};
									var newRecordHandler = function(newId) {
										App.EmployeeService.get(newId,
												EmployeesPage.addToList);
									};
									var errorHandler = function(error) {
										alert("Sorry, something bad happened: "
												+ error);
									};

									App.EmployeeService.create(employee,
											newRecordHandler, errorHandler);
									$(this).dialog("close");
								}
							},
							Cancel : function() {
								$(this).dialog("close");
							}
						},
						close : function() {
							allFields.val("").removeClass("ui-state-error");
						}
					});

	App.EmployeeService.getAll(EmployeesPage.addAllToList);
};

/**
 * EmployeeService is a separate object and does not deal with UI code.
 * Functions / methods require callbacks for success (mandatory) and error
 * (optional) which client provides; this is in order to allow the AJAX calls to
 * be asynchronous and decouple service (model) from view and view from the
 * implementation of API.
 */
App.EmployeeService = {};
// Return all employees, the success callback must accept a an array of employee
App.EmployeeService.getAll = function(successCallback, errorCallback) {
	$.getJSON(App.apiBaseUrl + 'employees', null, function(jsonData) {
		successCallback(jsonData);
	}).error(function(jqXHR, textStatus, errorThrown) {
		if (errorCallback) {
			errorCallback(jqXHR.status);
		}
	});
};

/**
 * Return the identified employee. Success callback is passed an employee and
 * the errorCallback can expect a 404 not found.
 */
App.EmployeeService.get = function(id, successCallback, errorCallback) {
	$.getJSON(App.apiBaseUrl + 'employees/' + id, null, function(data) {
		successCallback(data);
	}).error(function(jqXHR, textStatus, errorThrown) {
		if (errorCallback) {
			if (!App.GlobalErrors.ajaxError(jqXHR, textStatus, errorThrown)) {
				errorCallback(jqXHR.status);
			}
		}
	});
};

/**
 * Create an employee. Success callback is passed the new employee id and error
 * callback may get a 403 not authorised.
 */
App.EmployeeService.create = function(employee, successCallback, errorCallback) {
	$.ajax({
		url : App.apiBaseUrl + "employees/",
		type : "POST",
		contentType : "application/json; charset=utf-8",
		data : JSON.stringify(employee),
		success : function(data, textStatus, jqXHR) {
			var newEmployeeUrl = jqXHR.getResponseHeader('Location');
			if (newEmployeeUrl != null) {
				successCallback(newEmployeeUrl
						.charAt(newEmployeeUrl.length - 1));
			} else {
				successCallback(null);
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			if (!App.GlobalErrors.ajaxError(jqXHR, textStatus, errorThrown)) {
				errorCallback(jqXHR.status);
			}
		}
	});
};

/**
 * Update and delete are left as an exercise for the reader, a click handler is
 * already added to each row in the employee list table.
 */
App.EmployeeService.update = function(employee, successCallback, errorCallback) {
	console.log("Not implemented");
};
App.EmployeeService.remove = function(id, successCallback, errorCallback) {
	console.log("Not implemented");
};
