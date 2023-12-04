package com.WeChatApi.bean.condition;

import com.WeChatApi.controller.base.PageCondition;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class couponCondition extends PageCondition{
	
	private Integer r_id;
	
	private Integer r_order_id;
	
	private Integer r_user_id;
	
	private String r_veh_plate;
	
	private Integer r_coupon_id;
	
	private String r_coupon_code;
	
	private String r_park_code;
	
	private String r_time;
	
	private Integer r_send_status;
	
	private String userOpenId;

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

	public String getR_park_code() {
		return r_park_code;
	}

	public void setR_park_code(String r_park_code) {
		this.r_park_code = r_park_code;
	}

	public String getR_time() {
		return r_time;
	}

	public void setR_time(String r_time) {
		this.r_time = r_time;
	}

	public Integer getR_send_status() {
		return r_send_status;
	}

	public void setR_send_status(Integer r_send_status) {
		this.r_send_status = r_send_status;
	}

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	

}
