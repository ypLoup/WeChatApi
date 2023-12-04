package com.WeChatApi.bean.models;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.WeChatApi.bean.dto.userStoreDto;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class wechatUser {
	
	private Integer userId;
	
	private String userOpenId;
	
	private String userOpenIdZfb;
	
	private String userMobile;
	
    private String uvPlate;
	
    private String plateColor;
	
	private Date userCreateTime;
	
	private Date userlastloginTime;
	
	private Date userdisableTime;
	
	private String userSession;
	
	private Date userSessionExpire;
	
	private Integer userStatus;
	
	private Integer userVehiclePlateStatus;
	
	private Integer userFrom;
	
	private List<wechatUserVehicle>  userVehicleList;
	
	private List<zfbUserVehicle>  zfbUserVehicleList;
	
	private List<userStore> userStoreList;
	
    private Integer roleType;
	
	private Integer storeId;
	
	private Integer balances;
	
	private Integer point;
	
	private Integer pkl_balances;
	
	private Integer pkl_point;
	
	private List<Map<String, Object>> payList;

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

	public List<wechatUserVehicle> getUserVehicleList() {
		return userVehicleList;
	}

	public void setUserVehicleList(List<wechatUserVehicle> userVehicleList) {
		this.userVehicleList = userVehicleList;
	}

	public Integer getUserVehiclePlateStatus() {
		return userVehiclePlateStatus;
	}

	public void setUserVehiclePlateStatus(Integer userVehiclePlateStatus) {
		this.userVehiclePlateStatus = userVehiclePlateStatus;
	}

	public Integer getUserFrom() {
		return userFrom;
	}

	public void setUserFrom(Integer userFrom) {
		this.userFrom = userFrom;
	}

	public List<zfbUserVehicle> getZfbUserVehicleList() {
		return zfbUserVehicleList;
	}

	public void setZfbUserVehicleList(List<zfbUserVehicle> zfbUserVehicleList) {
		this.zfbUserVehicleList = zfbUserVehicleList;
	}

	public Integer getRoleType() {
		return roleType;
	}

	public void setRoleType(Integer roleType) {
		this.roleType = roleType;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public List<userStore> getUserStoreList() {
		return userStoreList;
	}

	public void setUserStoreList(List<userStore> userStoreList) {
		this.userStoreList = userStoreList;
	}

	public Integer getBalances() {
		return balances;
	}

	public void setBalances(Integer balances) {
		this.balances = balances;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public String getUserOpenIdZfb() {
		return userOpenIdZfb;
	}

	public void setUserOpenIdZfb(String userOpenIdZfb) {
		this.userOpenIdZfb = userOpenIdZfb;
	}

	public Integer getPkl_balances() {
		return pkl_balances;
	}

	public void setPkl_balances(Integer pkl_balances) {
		this.pkl_balances = pkl_balances;
	}

	public Integer getPkl_point() {
		return pkl_point;
	}

	public void setPkl_point(Integer pkl_point) {
		this.pkl_point = pkl_point;
	}

	public List<Map<String, Object>> getPayList() {
		return payList;
	}

	public void setPayList(List<Map<String, Object>> payList) {
		this.payList = payList;
	}

}
