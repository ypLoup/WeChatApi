package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class rechargeDto {
	
	private String userOpenId;
	
	private Integer amount;//��λ��Ԫ
	
	private  Integer realAmout;//ʵ�ʽ��
	
	private Integer scdId; //�Ż�����
	
	private Integer storeId;//�̻�id
	
	private Integer discountAmount;//�ۿ۽��
	
	private Integer userId;
	
	private Integer recordId;
	
	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getScdId() {
		return scdId;
	}

	public void setScdId(Integer scdId) {
		this.scdId = scdId;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public Integer getRealAmout() {
		return realAmout;
	}

	public void setRealAmout(Integer realAmout) {
		this.realAmout = realAmout;
	}

	public Integer getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Integer discountAmount) {
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

}
