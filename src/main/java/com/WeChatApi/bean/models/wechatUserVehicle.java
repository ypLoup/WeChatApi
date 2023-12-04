package com.WeChatApi.bean.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class wechatUserVehicle {
	
	private Integer uvId;
	
	private Integer userId;
	
	private String userOpenId;
	
	private String uvPlate;
	
	private String plateColor;
	
	private Integer uvStatus;
	
	private Integer orderStatus;//车辆是否在停车场内；
	private Integer order_type;//1场内2泊位
	
	private String pkl_code;
	private String pkll_code;
	
	private String order_entry_time;
	
	private String order_exit_time;
	
	private String sub_expire_time;
	
	private Date userVehicleDisabledTime;
	
	private Date createTime;
	
	private Date updateTime;

	public Integer getUvId() {
		return uvId;
	}

	public void setUvId(Integer uvId) {
		this.uvId = uvId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUvPlate() {
		return uvPlate;
	}

	public void setUvPlate(String uvPlate) {
		this.uvPlate = uvPlate;
	}

	public String getPlateColor() {
		return plateColor;
	}

	public void setPlateColor(String plateColor) {
		this.plateColor = plateColor;
	}

	public Integer getUvStatus() {
		return uvStatus;
	}

	public void setUvStatus(Integer uvStatus) {
		this.uvStatus = uvStatus;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	public Date getUserVehicleDisabledTime() {
		return userVehicleDisabledTime;
	}

	public void setUserVehicleDisabledTime(Date userVehicleDisabledTime) {
		this.userVehicleDisabledTime = userVehicleDisabledTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getPkl_code() {
		return pkl_code;
	}

	public void setPkl_code(String pkl_code) {
		this.pkl_code = pkl_code;
	}

	public String getOrder_entry_time() {
		return order_entry_time;
	}

	public void setOrder_entry_time(String order_entry_time) {
		this.order_entry_time = order_entry_time;
	}

	public String getOrder_exit_time() {
		return order_exit_time;
	}

	public void setOrder_exit_time(String order_exit_time) {
		this.order_exit_time = order_exit_time;
	}

	public Integer getOrder_type() {
		return order_type;
	}

	public void setOrder_type(Integer order_type) {
		this.order_type = order_type;
	}

	public String getPkll_code() {
		return pkll_code;
	}

	public void setPkll_code(String pkll_code) {
		this.pkll_code = pkll_code;
	}

	public String getSub_expire_time() {
		return sub_expire_time;
	}

	public void setSub_expire_time(String sub_expire_time) {
		this.sub_expire_time = sub_expire_time;
	}

}
