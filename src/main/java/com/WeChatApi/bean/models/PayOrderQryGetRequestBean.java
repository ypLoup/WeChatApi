package com.WeChatApi.bean.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
/**
 * 被扫四码聚合-订单查询
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayOrderQryGetRequestBean implements Serializable{
	
	private static final long serialVersionUID = -6550181778591862295L;

	private String appTp;
    private String purpPrtry;
    private String merchId;
    private String bizId;
    private String orderId;
    private String bizTime;
    private String bizRemark;
	public String getAppTp() {
		return appTp;
	}
	public void setAppTp(String appTp) {
		this.appTp = appTp;
	}
	public String getPurpPrtry() {
		return purpPrtry;
	}
	public void setPurpPrtry(String purpPrtry) {
		this.purpPrtry = purpPrtry;
	}
	public String getMerchId() {
		return merchId;
	}
	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getBizTime() {
		return bizTime;
	}
	public void setBizTime(String bizTime) {
		this.bizTime = bizTime;
	}
	public String getBizRemark() {
		return bizRemark;
	}
	public void setBizRemark(String bizRemark) {
		this.bizRemark = bizRemark;
	}
}
