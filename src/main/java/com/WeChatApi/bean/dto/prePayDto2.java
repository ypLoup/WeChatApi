package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class prePayDto2 {
	
	private String userOpenId;
	
	private String outTradeNo;
	
	private String orderId;
	
	private String orderIds;
	
	private String pklCode;
	
	private Integer orderReceivable;
	
	private Integer beforeAmount;
	
	private String couponRecordId;
	private String couponType;

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public Integer getOrderReceivable() {
		return orderReceivable;
	}

	public void setOrderReceivable(Integer orderReceivable) {
		this.orderReceivable = orderReceivable;
	}
	
	

	public String getCouponRecordId() {
		return couponRecordId;
	}

	public void setCouponRecordId(String couponRecordId) {
		this.couponRecordId = couponRecordId;
	}

	public Integer getBeforeAmount() {
		return beforeAmount;
	}

	public void setBeforeAmount(Integer beforeAmount) {
		this.beforeAmount = beforeAmount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderIds() {
		return orderIds;
	}

	public void setOrderIds(String orderIds) {
		this.orderIds = orderIds;
	}

	public String getPklCode() {
		return pklCode;
	}

	public void setPklCode(String pklCode) {
		this.pklCode = pklCode;
	}

	public String getCouponType() {
		return couponType;
	}

	public void setCouponType(String couponType) {
		this.couponType = couponType;
	}

}
