package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class meetingCodeDto {
	
	private String cId;
	
	private String userId;
	
	private String userOpenId;
	
	private String userName;
	
	private String userMobile;
	
	private String vehPlate;
	
	private String vehPlateColor;

	public String getcId() {
		return cId;
	}

	public void setcId(String cId) {
		this.cId = cId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getVehPlate() {
		return vehPlate;
	}

	public void setVehPlate(String vehPlate) {
		this.vehPlate = vehPlate;
	}

	public String getVehPlateColor() {
		return vehPlateColor;
	}

	public void setVehPlateColor(String vehPlateColor) {
		this.vehPlateColor = vehPlateColor;
	}

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}	
	
	
}
