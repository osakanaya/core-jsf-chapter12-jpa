package com.corejsf;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Credentials")
public class Credential implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String username;
	@Column(name="passwd")
	private String password;
	private int loginCount;
	
	public Credential() {}

	public Credential(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public int incrementLoginCount() {
		loginCount++;
		
		return loginCount;
	}
}
