package com.pulan.dialogserver.shiro.entity;

public class Role {
	private int id;
	private String role_name;
	public Role(int id, String role_name) {
		this.id = id;
		this.role_name = role_name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	@Override
	public String toString() {
		return "role [id=" + id + ", role_name=" + role_name + "]";
	}
	
}
