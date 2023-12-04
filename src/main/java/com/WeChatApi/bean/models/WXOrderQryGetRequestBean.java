package com.WeChatApi.bean.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 订单查询
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WXOrderQryGetRequestBean implements Serializable {

	/**
	 * @Fields serialVersionUID:对象说明
	 */
	private static final long serialVersionUID = -6550181778591862295L;

	private String appTp;
	private String txTp;
	private String merchId;
	private String bizId;
	private String bizTime;
	private String orderId;
	private String bizRemark;
	private String operFlag;

	public String getOperFlag() {
		return operFlag;
	}

	public void setOperFlag(String operFlag) {
		this.operFlag = operFlag;
	}

	public String getAppTp() {
		return appTp;
	}

	public void setAppTp(String appTp) {
		this.appTp = appTp;
	}

	public String getTxTp() {
		return txTp;
	}

	public void setTxTp(String txTp) {
		this.txTp = txTp;
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

	public String getBizTime() {
		return bizTime;
	}

	public void setBizTime(String bizTime) {
		this.bizTime = bizTime;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getBizRemark() {
		return bizRemark;
	}

	public void setBizRemark(String bizRemark) {
		this.bizRemark = bizRemark;
	}

}
