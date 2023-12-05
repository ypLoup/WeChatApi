package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class refundDto {
	
	private String outRefundNo;//��ƽ̨�˿��
	
	private Integer refundFee;//�˿���֣�֧������Ԫ
	
	private Integer totalFee;//֧����� ΢�� �֣�֧������Ԫ
	
	private String transactionId;//΢��֧�������ţ�֧�������׺�
	
	private String refundId;//֧��ƽ̨���ص��˿��
	
	private  String refundReason;//�˿�ԭ��
	
	private Integer payType;//1:΢�ţ�2��֧����
	
	private Integer payId;
	
	private Integer rId;
	
	private Integer refundAdminId;
	
	private String orderType;//�˿����ͣ�1ͣ����������2·�߶�����

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
