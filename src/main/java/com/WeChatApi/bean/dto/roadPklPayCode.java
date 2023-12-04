package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class roadPklPayCode {
	
	private String id;
	
	private String road_pkl_code;
	
	private String four_pay_code;
	private String four_pay_code_new;
	
	private String wechat_pay_code;
	
	private Integer pay_code_status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoad_pkl_code() {
		return road_pkl_code;
	}

	public void setRoad_pkl_code(String road_pkl_code) {
		this.road_pkl_code = road_pkl_code;
	}

	public String getFour_pay_code() {
		return four_pay_code;
	}

	public void setFour_pay_code(String four_pay_code) {
		this.four_pay_code = four_pay_code;
	}

	public String getWechat_pay_code() {
		return wechat_pay_code;
	}

	public void setWechat_pay_code(String wechat_pay_code) {
		this.wechat_pay_code = wechat_pay_code;
	}

	public Integer getPay_code_status() {
		return pay_code_status;
	}

	public void setPay_code_status(Integer pay_code_status) {
		this.pay_code_status = pay_code_status;
	}

	public String getFour_pay_code_new() {
		return four_pay_code_new;
	}

	public void setFour_pay_code_new(String four_pay_code_new) {
		this.four_pay_code_new = four_pay_code_new;
	}

}
