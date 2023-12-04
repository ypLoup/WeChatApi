package com.WeChatApi.bean.condition;

import java.util.Date;

import com.WeChatApi.controller.base.PageCondition;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class zfbUserVehicleCondition extends PageCondition  {
	
    private Integer uvId;
	
	private Integer userId;
	
	private String userOpenId;
	
	
	private String uvPlate;
	
	private String plateColor;
	
	private Integer uvStatus;
	
	private Integer orderStatus;//车辆是否在停车场内；
	
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

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

}
