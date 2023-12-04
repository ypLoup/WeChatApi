package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class userChargeDiscountDto {
	
	private Integer ucd_id;
	
	private String ucd_title;
	
	private Integer ucd_type;
	
	private Integer ucd_discount;
	
	private Integer ucd_condition_amount;
	
	private Integer ucd_present_amount;
	
	private String ucd_effective_time;
	
	private String ucd_expire_time;
	
	private Integer ucd_status;

	public Integer getUcd_id() {
		return ucd_id;
	}

	public void setUcd_id(Integer ucd_id) {
		this.ucd_id = ucd_id;
	}

	public String getUcd_title() {
		return ucd_title;
	}

	public void setUcd_title(String ucd_title) {
		this.ucd_title = ucd_title;
	}

	public Integer getUcd_type() {
		return ucd_type;
	}

	public void setUcd_type(Integer ucd_type) {
		this.ucd_type = ucd_type;
	}

	public Integer getUcd_discount() {
		return ucd_discount;
	}

	public void setUcd_discount(Integer ucd_discount) {
		this.ucd_discount = ucd_discount;
	}

	public Integer getUcd_condition_amount() {
		return ucd_condition_amount;
	}

	public void setUcd_condition_amount(Integer ucd_condition_amount) {
		this.ucd_condition_amount = ucd_condition_amount;
	}

	public Integer getUcd_present_amount() {
		return ucd_present_amount;
	}

	public void setUcd_present_amount(Integer ucd_present_amount) {
		this.ucd_present_amount = ucd_present_amount;
	}

	public String getUcd_effective_time() {
		return ucd_effective_time;
	}

	public void setUcd_effective_time(String ucd_effective_time) {
		this.ucd_effective_time = ucd_effective_time;
	}

	public String getUcd_expire_time() {
		return ucd_expire_time;
	}

	public void setUcd_expire_time(String ucd_expire_time) {
		this.ucd_expire_time = ucd_expire_time;
	}

	public Integer getUcd_status() {
		return ucd_status;
	}

	public void setUcd_status(Integer ucd_status) {
		this.ucd_status = ucd_status;
	}

}
