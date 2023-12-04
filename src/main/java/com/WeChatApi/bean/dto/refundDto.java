package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class refundDto {
	
	private String outRefundNo;//本平台退款单号
	
	private Integer refundFee;//退款金额，分，支付宝：元
	
	private Integer totalFee;//支付金额 微信 分，支付宝：元
	
	private String transactionId;//微信支付订单号，支付宝交易号
	
	private String refundId;//支付平台返回的退款单号
	
	private  String refundReason;//退款原因
	
	private Integer payType;//1:微信；2：支付宝
	
	private Integer payId;
	
	private Integer rId;
	
	private Integer refundAdminId;
	
	private String orderType;//退款类型（1停车场订单；2路边订单）

	public String getOutRefundNo() {
		return outRefundNo;
	}

	public void setOutRefundNo(String outRefundNo) {
		this.outRefundNo = outRefundNo;
	}

	public Integer getRefundFee() {
		return refundFee;
	}

	public void setRefundFee(Integer refundFee) {
		this.refundFee = refundFee;
	}

	public Integer getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(Integer totalFee) {
		this.totalFee = totalFee;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getPayId() {
		return payId;
	}

	public void setPayId(Integer payId) {
		this.payId = payId;
	}

	public Integer getrId() {
		return rId;
	}

	public void setrId(Integer rId) {
		this.rId = rId;
	}

	public String getRefundId() {
		return refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public Integer getRefundAdminId() {
		return refundAdminId;
	}

	public void setRefundAdminId(Integer refundAdminId) {
		this.refundAdminId = refundAdminId;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

}
