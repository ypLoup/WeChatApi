package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class prePayDto {
	
	private String userOpenId;
	
	private String outTradeNo;
	
	private Integer orderReceivable;
	
	private String couponRecordId;

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
	
	public prePayDto(String a,String b,Integer c){ 
		userOpenId=a;
		outTradeNo=b;
		orderReceivable=c;
    }

	public String getCouponRecordId() {
		return couponRecordId;
	}

	public void setCouponRecordId(String couponRecordId) {
		this.couponRecordId = couponRecordId;
	}

}
