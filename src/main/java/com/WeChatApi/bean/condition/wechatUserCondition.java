package com.WeChatApi.bean.condition;

import java.util.Date;
import java.util.List;

import com.WeChatApi.bean.models.wechatUserVehicle;
import com.WeChatApi.controller.base.PageCondition;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class wechatUserCondition extends PageCondition  {
	
	private Integer userId;
	
	private String userOpenId;
	
	private String userMobile;
	
	private List<wechatUserVehicle> vehicleList;
	
	private Date userCreateTime;
	
	private Date userlastloginTime;
	
	private Date userdisableTime;
	
	private String userSession;
	
	private Date userSessionExpire;
	
	private Integer userStatus;
	
	private Integer userFrom;
	
	private String userOpenIdZfb;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public Date getUserCreateTime() {
		return userCreateTime;
	}

	public void setUserCreateTime(Date userCreateTime) {
		this.userCreateTime = userCreateTime;
	}

	public Date getUserlastloginTime() {
		return userlastloginTime;
	}

	public void setUserlastloginTime(Date userlastloginTime) {
		this.userlastloginTime = userlastloginTime;
	}

	public Date getUserdisableTime() {
		return userdisableTime;
	}

	public void setUserdisableTime(Date userdisableTime) {
		this.userdisableTime = userdisableTime;
	}

	public String getUserSession() {
		return userSession;
	}

	public void setUserSession(String userSession) {
		this.userSession = userSession;
	}

	public Date getUserSessionExpire() {
		return userSessionExpire;
	}

	public void setUserSessionExpire(Date userSessionExpire) {
		this.userSessionExpire = userSessionExpire;
	}

	public Integer getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(Integer userStatus) {
		this.userStatus = userStatus;
	}


	public List<wechatUserVehicle> getVehicleList() {
		return vehicleList;
	}

	public void setVehicleList(List<wechatUserVehicle> vehicleList) {
		this.vehicleList = vehicleList;
	}

	public Integer getUserFrom() {
		return userFrom;
	}

	public void setUserFrom(Integer userFrom) {
		this.userFrom = userFrom;
	}

	public String getUserOpenIdZfb() {
		return userOpenIdZfb;
	}

	public void setUserOpenIdZfb(String userOpenIdZfb) {
		this.userOpenIdZfb = userOpenIdZfb;
	}

}
