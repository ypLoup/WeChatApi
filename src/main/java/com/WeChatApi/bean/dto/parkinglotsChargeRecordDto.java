package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class parkinglotsChargeRecordDto {
	
	private Integer cr_id;
	
	private Integer cr_charge_id;
	
	private Integer cr_c_balances;
	
	private Integer cr_c_point;
	
	private Integer cr_user_id;
	
	private Integer cr_status;
	
	private Integer charge_type;
	
	private String userOpenId;

	public Integer getCr_id() {
		return cr_id;
	}

	public void setCr_id(Integer cr_id) {
		this.cr_id = cr_id;
	}

	public Integer getCr_charge_id() {
		return cr_charge_id;
	}

	public void setCr_charge_id(Integer cr_charge_id) {
		this.cr_charge_id = cr_charge_id;
	}

	public Integer getCr_c_balances() {
		return cr_c_balances;
	}

	public void setCr_c_balances(Integer cr_c_balances) {
		this.cr_c_balances = cr_c_balances;
	}

	public Integer getCr_c_point() {
		return cr_c_point;
	}

	public void setCr_c_point(Integer cr_c_point) {
		this.cr_c_point = cr_c_point;
	}

	public Integer getCr_user_id() {
		return cr_user_id;
	}

	public void setCr_user_id(Integer cr_user_id) {
		this.cr_user_id = cr_user_id;
	}

	public Integer getCr_status() {
		return cr_status;
	}

	public void setCr_status(Integer cr_status) {
		this.cr_status = cr_status;
	}

	public Integer getCharge_type() {
		return charge_type;
	}

	public void setCharge_type(Integer charge_type) {
		this.charge_type = charge_type;
	}

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}
	
	

}
