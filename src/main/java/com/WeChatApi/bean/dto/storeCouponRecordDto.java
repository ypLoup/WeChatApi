package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class storeCouponRecordDto {
	
	private Integer r_id;
	
	private Integer r_order_id;
	
	private Integer r_user_id;
	
	private String 	r_veh_plate;
	
	private Integer r_coupon_id;
	
	private String r_coupon_code;
	
	private Integer r_amount;
	
	private String r_park_code;
	
	private String r_park_name;
	
	private String r_start_time;
	
	private String r_end_time;
	
	private String 	r_time;
	
	private String r_expire_time;
	
	private String r_used_time;
	
	private Integer r_send_status;
	
	private String r_remark;
	
	private Integer r_coupon_type;

	public Integer getR_id() {
		return r_id;
	}

	public void setR_id(Integer r_id) {
		this.r_id = r_id;
	}

	public Integer getR_order_id() {
		return r_order_id;
	}

	public void setR_order_id(Integer r_order_id) {
		this.r_order_id = r_order_id;
	}

	public Integer getR_user_id() {
		return r_user_id;
	}

	public void setR_user_id(Integer r_user_id) {
		this.r_user_id = r_user_id;
	}

	public String getR_veh_plate() {
		return r_veh_plate;
	}

	public void setR_veh_plate(String r_veh_plate) {
		this.r_veh_plate = r_veh_plate;
	}

	public Integer getR_coupon_id() {
		return r_coupon_id;
	}

	public void setR_coupon_id(Integer r_coupon_id) {
		this.r_coupon_id = r_coupon_id;
	}

	public String getR_coupon_code() {
		return r_coupon_code;
	}

	public void setR_coupon_code(String r_coupon_code) {
		this.r_coupon_code = r_coupon_code;
	}

	public Integer getR_amount() {
		return r_amount;
	}

	public void setR_amount(Integer r_amount) {
		this.r_amount = r_amount;
	}

	public String getR_park_code() {
		return r_park_code;
	}

	public void setR_park_code(String r_park_code) {
		this.r_park_code = r_park_code;
	}

	public String getR_park_name() {
		return r_park_name;
	}

	public void setR_park_name(String r_park_name) {
		this.r_park_name = r_park_name;
	}

	public String getR_start_time() {
		return r_start_time;
	}

	public void setR_start_time(String r_start_time) {
		this.r_start_time = r_start_time;
	}

	public String getR_end_time() {
		return r_end_time;
	}

	public void setR_end_time(String r_end_time) {
		this.r_end_time = r_end_time;
	}

	public String getR_time() {
		return r_time;
	}

	public void setR_time(String r_time) {
		this.r_time = r_time;
	}

	public String getR_expire_time() {
		return r_expire_time;
	}

	public void setR_expire_time(String r_expire_time) {
		this.r_expire_time = r_expire_time;
	}

	public String getR_used_time() {
		return r_used_time;
	}

	public void setR_used_time(String r_used_time) {
		this.r_used_time = r_used_time;
	}

	public Integer getR_send_status() {
		return r_send_status;
	}

	public void setR_send_status(Integer r_send_status) {
		this.r_send_status = r_send_status;
	}

	public String getR_remark() {
		return r_remark;
	}

	public void setR_remark(String r_remark) {
		this.r_remark = r_remark;
	}

	public Integer getR_coupon_type() {
		return r_coupon_type;
	}

	public void setR_coupon_type(Integer r_coupon_type) {
		this.r_coupon_type = r_coupon_type;
	}
	
	
}
