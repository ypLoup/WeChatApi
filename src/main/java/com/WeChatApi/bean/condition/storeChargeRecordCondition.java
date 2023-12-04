package com.WeChatApi.bean.condition;

import com.WeChatApi.controller.base.PageCondition;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class storeChargeRecordCondition extends PageCondition {
	
	private Integer r_id;
	
	private Integer r_store_id;
	
	private Integer r_type;
	
	private Integer r_discount_id;
	
	private Integer r_balances;
	
	private Integer r_point;
	
	private Integer r_status;
	
	private String r_time;
	
	private String start_time;
	
	private String end_time;
	
	private String userOpenId;

	public Integer getR_id() {
		return r_id;
	}

	public void setR_id(Integer r_id) {
		this.r_id = r_id;
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

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public Integer getR_store_id() {
		return r_store_id;
	}

	public void setR_store_id(Integer r_store_id) {
		this.r_store_id = r_store_id;
	}

}
