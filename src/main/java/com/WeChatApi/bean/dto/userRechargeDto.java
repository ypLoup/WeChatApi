package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class userRechargeDto {
	
	private String userOpenId;
	
	private float amount;//单位：元
	
	private  float realAmout;//实际金额
	
	private Integer ucdId; //优惠类型
	
	private float discountAmount;//折扣金额
	
	private Integer type;
	
	private Integer userId;
	
	private Integer recordId;
	
	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public Integer getUcdId() {
		return ucdId;
	}

	public void setUcdId(Integer ucdId) {
		this.ucdId = ucdId;
	}


	public float getRealAmout() {
		return realAmout;
	}

	public void setRealAmout(float realAmout) {
		this.realAmout = realAmout;
	}

	public float getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(float discountAmount) {
		this.discountAmount = discountAmount;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getRecordId() {
		return recordId;
	}

	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
