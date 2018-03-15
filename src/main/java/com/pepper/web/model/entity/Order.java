package com.pepper.web.model.entity;

import java.io.Serializable;

public class Order implements Serializable{
	
	private static final long serialVersionUID = 825319002868700221L;
	
	private Integer orderId;
	
	private UserInfo customer;
	
	private Double cost;
	
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public UserInfo getCustomer() {
		return customer;
	}
	public void setCustomer(UserInfo customer) {
		this.customer = customer;
	}
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	
	
}
