package com.easemob.chatuidemo.domain;

import java.util.HashSet;
import java.util.Set;


public class Role {
	private Long id;//id
	private String role;//角色名
	private Set<Permission> permissions=new HashSet<Permission>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Set<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}
	
}
