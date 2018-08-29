package com.pepper.web.model.entity;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable{
	
	private static final long serialVersionUID = 825319002868700221L;

	/*--------------------------- 订单信息 ---------------------------------*/
	private Long orderId;//订单号

	private String seqNum;//订单流水号

	private Date orderTime;//订单时间

	private Integer orderStatus;//订单状态

	private String address;//订单寄送地址

	private double cost;//实际支付价格

	/*--------------------------- 客户信息 ---------------------------------*/

	private UserInfo customer;//客户信息

	/*--------------------------- 商品信息 ---------------------------------*/

	private String goodsName;//商品名称

	private double goodsPrice;//商品价格

	private double discount;//优惠价格

	private String provider;//商品提供人

	private String providerPhone;//商品提供人手机

	private String providerCompany;//商品提供公司

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(String seqNum) {
		this.seqNum = seqNum;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public UserInfo getCustomer() {
		return customer;
	}

	public void setCustomer(UserInfo customer) {
		this.customer = customer;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public double getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(double goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProviderPhone() {
		return providerPhone;
	}

	public void setProviderPhone(String providerPhone) {
		this.providerPhone = providerPhone;
	}

	public String getProviderCompany() {
		return providerCompany;
	}

	public void setProviderCompany(String providerCompany) {
		this.providerCompany = providerCompany;
	}
}
