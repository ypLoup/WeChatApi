package com.WeChatApi.bean.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 退款
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WXRefundGetRequestBean implements Serializable {

	/**
	 * @Fields serialVersionUID:对象说明
	 */
	private static final long serialVersionUID = 1L;

	private String appTp;
	private String txTp;
	private String purpPrtry;
	private String merchId;
	private String bizId;
	private String refundBizid;
	private String bizTime;
	private String orderId;
	private String feeType;
	private String bizAmount;
	private String bizRemark;
	private String refundDesc;
	private String attach;

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getRefundDesc() {
		return refundDesc;
	}

	public void setRefundDesc(String refundDesc) {
		this.refundDesc = refundDesc;
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

	public String getRefundBizid() {
		return refundBizid;
	}

	public void setRefundBizid(String refundBizid) {
		this.refundBizid = refundBizid;
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

	public String getBizAmount() {
		return bizAmount;
	}

	public void setBizAmount(String bizAmount) {
		this.bizAmount = bizAmount;
	}

	public String getBizRemark() {
		return bizRemark;
	}

	public void setBizRemark(String bizRemark) {
		this.bizRemark = bizRemark;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

}

