package com.unifiprojects.app.appichetto.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class User {

	@Id
	@GeneratedValue
	Long id;

	public Long getId() {
		return id;
	}

	private String username;
	private String password;
	
	@OneToMany(mappedBy="buyer", cascade = CascadeType.ALL)
	private List<Receipt> boughtReceipts;
	
	public User() {
		
	}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public void setBoughtReceipts(List<Receipt> boughtReceipts) {
		if(this.boughtReceipts == null) {
			boughtReceipts = new ArrayList<>();
		}
		this.boughtReceipts.clear();
		this.boughtReceipts.addAll(boughtReceipts);
	}

	public void addReceipt(Receipt r) {
		if (boughtReceipts == null) {
			boughtReceipts = new ArrayList<Receipt>();
		}
		boughtReceipts.add(r);
	}
	
  public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.username;
	}

}
