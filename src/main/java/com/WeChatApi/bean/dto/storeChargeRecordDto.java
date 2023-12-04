package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class storeChargeRecordDto {
	
	private Integer r_id;
	
	private Integer r_store_id;
	
	private Integer r_type;
	
	private Integer r_discount_id;
	
	private Integer r_balances;
	
	private Integer r_point;
	
	private String fpUrl;
	
	private Integer charge_channel;
	

	
	private String r_time;

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



	public String getR_time() {
		return r_time;
	}

	public void setR_time(String r_time) {
		this.r_time = r_time;
	}

	public Integer getR_store_id() {
		return r_store_id;
	}

	public void setR_store_id(Integer r_store_id) {
		this.r_store_id = r_store_id;
	}

	public String getFpUrl() {
		return fpUrl;
	}

	public void setFpUrl(String fpUrl) {
		this.fpUrl = fpUrl;
	}

	public Integer getCharge_channel() {
		return charge_channel;
	}

	public void setCharge_channel(Integer charge_channel) {
		this.charge_channel = charge_channel;
	}
	
	

}
