package com.github.gitproject1.one.controller.view;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class LoginMB {

	private String login;

	private String password;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
