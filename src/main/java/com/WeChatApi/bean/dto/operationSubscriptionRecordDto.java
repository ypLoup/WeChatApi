package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class operationSubscriptionRecordDto {
	
	private String r_id;
	
	private String pkl_code;
	
	private Integer r_sub_id;
	
	private Integer r_type;
	
	private Integer sub_amount;
	
	private String sub_effective_time;
	
	private String sub_expire_time;
	
	private Integer pu_id;
	
	private String sub_create_datetime;
	
	private String sub_remark;
	
	private Integer record_status;
	
	private String days;
	
    private String userOpenId;
    
    private String outTradeNo;
    
    private String plate;

	public String getR_id() {
		return r_id;
	}

	public void setR_id(String r_id) {
		this.r_id = r_id;
	}

	public String getPkl_code() {
		return pkl_code;
	}

	public void setPkl_code(String pkl_code) {
		this.pkl_code = pkl_code;
	}

	public Integer getR_sub_id() {
		return r_sub_id;
	}

	public void setR_sub_id(Integer r_sub_id) {
		this.r_sub_id = r_sub_id;
	}

	public Integer getR_type() {
		return r_type;
	}

	public void setR_type(Integer r_type) {
		this.r_type = r_type;
	}

	public Integer getSub_amount() {
		return sub_amount;
	}

	public void setSub_amount(Integer sub_amount) {
		this.sub_amount = sub_amount;
	}

	public String getSub_effective_time() {
		return sub_effective_time;
	}

	public void setSub_effective_time(String sub_effective_time) {
		this.sub_effective_time = sub_effective_time;
	}

	public String getSub_expire_time() {
		return sub_expire_time;
	}

	public void setSub_expire_time(String sub_expire_time) {
		this.sub_expire_time = sub_expire_time;
	}

	public Integer getPu_id() {
		return pu_id;
	}

	public void setPu_id(Integer pu_id) {
		this.pu_id = pu_id;
	}

	public String getSub_create_datetime() {
		return sub_create_datetime;
	}

	public void setSub_create_datetime(String sub_create_datetime) {
		this.sub_create_datetime = sub_create_datetime;
	}

	public String getSub_remark() {
		return sub_remark;
	}

	public void setSub_remark(String sub_remark) {
		this.sub_remark = sub_remark;
	}

	public Integer getRecord_status() {
		return record_status;
	}

	public void setRecord_status(Integer record_status) {
		this.record_status = record_status;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

}
