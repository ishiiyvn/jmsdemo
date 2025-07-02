package com.cei.model;

import java.io.Serializable;

public class Order implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	
	public Order() {}

	public Order(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "Order{id='" + id + "'}";
	}
}