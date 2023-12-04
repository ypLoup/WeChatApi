package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class storeChargeDiscountDto {
	
	private Integer scd_id;
	
	private String scd_title;
	
	private Integer scd_type;
	
	private Integer scd_discount;
	
	private Integer scd_condition_amount;
	
	private Integer scd_present_amount;
	
	private String scd_effective_time;
	
	private String scd_expire_time;
	
	private Integer scd_status;

	public Integer getScd_id() {
		return scd_id;
	}

	public void setScd_id(Integer scd_id) {
		this.scd_id = scd_id;
	}

	public String getScd_title() {
		return scd_title;
	}

	public void setScd_title(String scd_title) {
		this.scd_title = scd_title;
	}

	public Integer getScd_type() {
		return scd_type;
	}

	public void setScd_type(Integer scd_type) {
		this.scd_type = scd_type;
	}

	public Integer getScd_discount() {
		return scd_discount;
	}

	public void setScd_discount(Integer scd_discount) {
		this.scd_discount = scd_discount;
	}

	public Integer getScd_condition_amount() {
		return scd_condition_amount;
	}

	public void setScd_condition_amount(Integer scd_condition_amount) {
		this.scd_condition_amount = scd_condition_amount;
	}

	public Integer getScd_present_amount() {
		return scd_present_amount;
	}

	public void setScd_present_amount(Integer scd_present_amount) {
		this.scd_present_amount = scd_present_amount;
	}

	public String getScd_effective_time() {
		return scd_effective_time;
	}

	public void setScd_effective_time(String scd_effective_time) {
		this.scd_effective_time = scd_effective_time;
	}

	public String getScd_expire_time() {
		return scd_expire_time;
	}

	public void setScd_expire_time(String scd_expire_time) {
		this.scd_expire_time = scd_expire_time;
	}

	public Integer getScd_status() {
		return scd_status;
	}

	public void setScd_status(Integer scd_status) {
		this.scd_status = scd_status;
	}

}
