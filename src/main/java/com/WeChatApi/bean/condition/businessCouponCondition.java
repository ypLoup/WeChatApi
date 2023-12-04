package com.WeChatApi.bean.condition;

import java.math.BigDecimal;

import com.WeChatApi.controller.base.PageCondition;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class businessCouponCondition extends PageCondition {
	
	private String userOpenId;
	
	private Integer storeId;
	
	private String cTitle;
	
	private BigDecimal cAmount;
	
	private Integer cCount;
	
	private Integer cCountReceived;
	
	private Integer cBalances;
	
	private Integer cPoint;
	
	private String cStartTime;
	
	private String cEndTime;
	
	private Integer cStatus;
	

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public String getcTitle() {
		return cTitle;
	}

	public void setcTitle(String cTitle) {
		this.cTitle = cTitle;
	}

	public BigDecimal getcAmount() {
		return cAmount;
	}

	public void setcAmount(BigDecimal cAmount) {
		this.cAmount = cAmount;
	}

	public Integer getcCount() {
		return cCount;
	}

	public void setcCount(Integer cCount) {
		this.cCount = cCount;
	}

	public String getcStartTime() {
		return cStartTime;
	}

	public void setcStartTime(String cStartTime) {
		this.cStartTime = cStartTime;
	}

	public String getcEndTime() {
		return cEndTime;
	}

	public void setcEndTime(String cEndTime) {
		this.cEndTime = cEndTime;
	}

	public Integer getcCountReceived() {
		return cCountReceived;
	}

	public void setcCountReceived(Integer cCountReceived) {
		this.cCountReceived = cCountReceived;
	}

	public Integer getcBalances() {
		return cBalances;
	}

	public void setcBalances(Integer cBalances) {
		this.cBalances = cBalances;
	}

	public Integer getcPoint() {
		return cPoint;
	}

	public void setcPoint(Integer cPoint) {
		this.cPoint = cPoint;
	}

	public Integer getcStatus() {
		return cStatus;
	}

	public void setcStatus(Integer cStatus) {
		this.cStatus = cStatus;
	}
	
	
}
