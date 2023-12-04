package com.WeChatApi.bean.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class feedbackOperation {
	
	private String userOpenId;
	
	private Integer fo_id;
	
	private Integer fo_f_id;
	
	private Integer fo_type;

	private Integer fo_operator_id;
	
	private String fo_operator_name;
	
	private String fo_operator_content;
	
	private String fo_operator_image_1;
	
	private String fo_operator_image_2;
	
	private String fo_operator_time;

	public Integer getFo_id() {
		return fo_id;
	}

	public void setFo_id(Integer fo_id) {
		this.fo_id = fo_id;
	}

	public Integer getFo_f_id() {
		return fo_f_id;
	}

	public void setFo_f_id(Integer fo_f_id) {
		this.fo_f_id = fo_f_id;
	}

	public Integer getFo_type() {
		return fo_type;
	}

	public void setFo_type(Integer fo_type) {
		this.fo_type = fo_type;
	}

	public Integer getFo_operator_id() {
		return fo_operator_id;
	}

	public void setFo_operator_id(Integer fo_operator_id) {
		this.fo_operator_id = fo_operator_id;
	}

	public String getFo_operator_name() {
		return fo_operator_name;
	}

	public void setFo_operator_name(String fo_operator_name) {
		this.fo_operator_name = fo_operator_name;
	}

	public String getFo_operator_content() {
		return fo_operator_content;
	}

	public void setFo_operator_content(String fo_operator_content) {
		this.fo_operator_content = fo_operator_content;
	}

	public String getFo_operator_image_1() {
		return fo_operator_image_1;
	}

	public void setFo_operator_image_1(String fo_operator_image_1) {
		this.fo_operator_image_1 = fo_operator_image_1;
	}

	public String getFo_operator_image_2() {
		return fo_operator_image_2;
	}

	public void setFo_operator_image_2(String fo_operator_image_2) {
		this.fo_operator_image_2 = fo_operator_image_2;
	}

	public String getFo_operator_time() {
		return fo_operator_time;
	}

	public void setFo_operator_time(String fo_operator_time) {
		this.fo_operator_time = fo_operator_time;
	}

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}
}
