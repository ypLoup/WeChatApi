package com.WeChatApi.bean.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class feedback {
	
	private String userOpenId;
	
    private Integer f_id;
	
	private Integer f_type;
	
	private Integer f_user_id;
	
	private String f_title;
	
	private String f_content;
	
	private String f_image_url_1;
	
	private String f_image_url_2;
	
	private String f_create_time;
	
	private String f_last_update_time;
	
	private Integer f_level;
	
	private String f_remark;
	
	private Integer f_status;
	
	private List<feedbackOperation> detailList;

	public Integer getF_id() {
		return f_id;
	}

	public void setF_id(Integer f_id) {
		this.f_id = f_id;
	}

	public Integer getF_type() {
		return f_type;
	}

	public void setF_type(Integer f_type) {
		this.f_type = f_type;
	}

	public Integer getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(Integer f_user_id) {
		this.f_user_id = f_user_id;
	}

	public String getF_title() {
		return f_title;
	}

	public void setF_title(String f_title) {
		this.f_title = f_title;
	}

	public String getF_content() {
		return f_content;
	}

	public void setF_content(String f_content) {
		this.f_content = f_content;
	}

	public String getF_image_url_1() {
		return f_image_url_1;
	}

	public void setF_image_url_1(String f_image_url_1) {
		this.f_image_url_1 = f_image_url_1;
	}

	public String getF_image_url_2() {
		return f_image_url_2;
	}

	public void setF_image_url_2(String f_image_url_2) {
		this.f_image_url_2 = f_image_url_2;
	}

	public String getF_create_time() {
		return f_create_time;
	}

	public void setF_create_time(String f_create_time) {
		this.f_create_time = f_create_time;
	}

	public String getF_last_update_time() {
		return f_last_update_time;
	}

	public void setF_last_update_time(String f_last_update_time) {
		this.f_last_update_time = f_last_update_time;
	}

	public Integer getF_level() {
		return f_level;
	}

	public void setF_level(Integer f_level) {
		this.f_level = f_level;
	}

	public String getF_remark() {
		return f_remark;
	}

	public void setF_remark(String f_remark) {
		this.f_remark = f_remark;
	}

	public Integer getF_status() {
		return f_status;
	}

	public void setF_status(Integer f_status) {
		this.f_status = f_status;
	}

	public List<feedbackOperation> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<feedbackOperation> detailList) {
		this.detailList = detailList;
	}

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	
}
