package com.WeChatApi.bean.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class businessCouponDto {
	
	private Integer c_id;
	
	private String userOpenId;
	
	private Integer store_id;
	
	private String c_title;
	
	private BigDecimal c_amount;
	
	private Integer c_count;
	
	private Integer c_count_received;
	
	private Integer c_balances;
	
	private Integer c_point;
	
	private String c_start_time;
	
	private String c_end_time;
	
	private Integer c_status;
	
	private Integer c_is_balance;
	

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	

	public Integer getC_id() {
		return c_id;
	}

	public void setC_id(Integer c_id) {
		this.c_id = c_id;
	}

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}

	public String getC_title() {
		return c_title;
	}

	public void setC_title(String c_title) {
		this.c_title = c_title;
	}

	public BigDecimal getC_amount() {
		return c_amount;
	}

	public void setC_amount(BigDecimal c_amount) {
		this.c_amount = c_amount;
	}

	public Integer getC_count() {
		return c_count;
	}

	public void setC_count(Integer c_count) {
		this.c_count = c_count;
	}

	public Integer getC_count_received() {
		return c_count_received;
	}

	public void setC_count_received(Integer c_count_received) {
		this.c_count_received = c_count_received;
	}

	public Integer getC_balances() {
		return c_balances;
	}

	public void setC_balances(Integer c_balances) {
		this.c_balances = c_balances;
	}

	public Integer getC_point() {
		return c_point;
	}

	public void setC_point(Integer c_point) {
		this.c_point = c_point;
	}

	public String getC_start_time() {
		return c_start_time;
	}

	public void setC_start_time(String c_start_time) {
		this.c_start_time = c_start_time;
	}

	public String getC_end_time() {
		return c_end_time;
	}

	public void setC_end_time(String c_end_time) {
		this.c_end_time = c_end_time;
	}

	public Integer getC_status() {
		return c_status;
	}

	public void setC_status(Integer c_status) {
		this.c_status = c_status;
	}

	public Integer getC_is_balance() {
		return c_is_balance;
	}

	public void setC_is_balance(Integer c_is_balance) {
		this.c_is_balance = c_is_balance;
	}

	
	
	
}
