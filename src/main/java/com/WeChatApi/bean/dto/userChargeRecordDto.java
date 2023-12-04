package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class userChargeRecordDto {
	
	private Integer r_id;
	
	private Integer r_user_id;
	
	private Integer r_type;
	
	private Integer r_discount_id;
	
	private Integer r_balances;
	
	private Integer r_point;
	
	private Integer r_status;
	
	private String r_time;
	
	private String r_out_trade_no;
	
	private Integer r_left_balances;
	
	private Integer r_left_point;

	public Integer getR_id() {
		return r_id;
	}

	public void setR_id(Integer r_id) {
		this.r_id = r_id;
	}

	public Integer getR_user_id() {
		return r_user_id;
	}

	public void setR_user_id(Integer r_user_id) {
		this.r_user_id = r_user_id;
	}

	public Integer getR_type() {
		return r_type;
	}

	public void setR_type(Integer r_type) {
		this.r_type = r_type;
	}

	public Integer getR_discount_id() {
		return r_discount_id;
	}

	public void setR_discount_id(Integer r_discount_id) {
		this.r_discount_id = r_discount_id;
	}

	public Integer getR_balances() {
		return r_balances;
	}

	public void setR_balances(Integer r_balances) {
		this.r_balances = r_balances;
	}

	public Integer getR_point() {
		return r_point;
	}

	public void setR_point(Integer r_point) {
		this.r_point = r_point;
	}

	public Integer getR_status() {
		return r_status;
	}

	public void setR_status(Integer r_status) {
		this.r_status = r_status;
	}

	public String getR_time() {
		return r_time;
	}

	public void setR_time(String r_time) {
		this.r_time = r_time;
	}

	public String getR_out_trade_no() {
		return r_out_trade_no;
	}

	public void setR_out_trade_no(String r_out_trade_no) {
		this.r_out_trade_no = r_out_trade_no;
	}

	public Integer getR_left_balances() {
		return r_left_balances;
	}

	public void setR_left_balances(Integer r_left_balances) {
		this.r_left_balances = r_left_balances;
	}

	public Integer getR_left_point() {
		return r_left_point;
	}

	public void setR_left_point(Integer r_left_point) {
		this.r_left_point = r_left_point;
	}
	
	

}
