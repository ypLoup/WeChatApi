package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class userRechargeRecordDto {
	
	private Integer id;
	
	private Integer userChargeRId;
	
	private Integer userId;
	
	private Integer type;
	
	private String outTradeNo;
	
	private String balances;
	
	private String userTime;
	
	private String unUserTime;
	
	private Integer status;
	
	private String creatTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserChargeRId() {
		return userChargeRId;
	}

	public void setUserChargeRId(Integer userChargeRId) {
		this.userChargeRId = userChargeRId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getBalances() {
		return balances;
	}

	public void setBalances(String balances) {
		this.balances = balances;
	}

	public String getUserTime() {
		return userTime;
	}

	public void setUserTime(String userTime) {
		this.userTime = userTime;
	}

	public String getUnUserTime() {
		return unUserTime;
	}

	public void setUnUserTime(String unUserTime) {
		this.unUserTime = unUserTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(String creatTime) {
		this.creatTime = creatTime;
	}

}
