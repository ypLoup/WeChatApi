package com.WeChatApi.bean.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class userStore {
	
	private Integer suId;
	
	private String mchAppId;
	
	private String suName;
	
	private String suFullName;
	
	private String suMobile;
	
	private String suIdCard;
	
	private String suPassword;

	private Integer suBalances;
	
	private Integer suPoint;

	private String suParkCode;

	private String suCreateTime;
	
	private String suDisabledTime;
	
	private String suSession;
	
	private String suSessionExpire;
	
	private String suLastloginIpaddr;
	
	private String suLastloginTime;
	
	private Integer suStatus;
	
	private Integer isBind;
	
	
	//-------------------newadd
    private Integer suType;
	
	private Integer suCouponLimitTime;
	
	private Integer suPointLimitTime;
	
	private String suCouponLimit;
	
	private String suPointExpireTime;
	
	private String suParkName;
	

	public Integer getSuId() {
		return suId;
	}

	public void setSuId(Integer suId) {
		this.suId = suId;
	}

	public String getMchAppId() {
		return mchAppId;
	}

	public void setMchAppId(String mchAppId) {
		this.mchAppId = mchAppId;
	}

	public String getSuName() {
		return suName;
	}

	public void setSuName(String suName) {
		this.suName = suName;
	}

	public String getSuFullName() {
		return suFullName;
	}

	public void setSuFullName(String suFullName) {
		this.suFullName = suFullName;
	}

	public String getSuMobile() {
		return suMobile;
	}

	public void setSuMobile(String suMobile) {
		this.suMobile = suMobile;
	}

	public String getSuIdCard() {
		return suIdCard;
	}

	public void setSuIdCard(String suIdCard) {
		this.suIdCard = suIdCard;
	}

	public String getSuPassword() {
		return suPassword;
	}

	public void setSuPassword(String suPassword) {
		this.suPassword = suPassword;
	}

	public Integer getSuBalances() {
		return suBalances;
	}

	public void setSuBalances(Integer suBalances) {
		this.suBalances = suBalances;
	}

	public Integer getSuPoint() {
		return suPoint;
	}

	public void setSuPoint(Integer suPoint) {
		this.suPoint = suPoint;
	}

	public String getSuParkCode() {
		return suParkCode;
	}

	public void setSuParkCode(String suParkCode) {
		this.suParkCode = suParkCode;
	}

	public String getSuCreateTime() {
		return suCreateTime;
	}

	public void setSuCreateTime(String suCreateTime) {
		this.suCreateTime = suCreateTime;
	}

	public String getSuDisabledTime() {
		return suDisabledTime;
	}

	public void setSuDisabledTime(String suDisabledTime) {
		this.suDisabledTime = suDisabledTime;
	}

	public String getSuSession() {
		return suSession;
	}

	public void setSuSession(String suSession) {
		this.suSession = suSession;
	}

	public String getSuSessionExpire() {
		return suSessionExpire;
	}

	public void setSuSessionExpire(String suSessionExpire) {
		this.suSessionExpire = suSessionExpire;
	}

	public String getSuLastloginIpaddr() {
		return suLastloginIpaddr;
	}

	public void setSuLastloginIpaddr(String suLastloginIpaddr) {
		this.suLastloginIpaddr = suLastloginIpaddr;
	}

	public String getSuLastloginTime() {
		return suLastloginTime;
	}

	public void setSuLastloginTime(String suLastloginTime) {
		this.suLastloginTime = suLastloginTime;
	}

	public Integer getSuStatus() {
		return suStatus;
	}

	public void setSuStatus(Integer suStatus) {
		this.suStatus = suStatus;
	}

	public Integer getIsBind() {
		return isBind;
	}

	public void setIsBind(Integer isBind) {
		this.isBind = isBind;
	}

	public Integer getSuType() {
		return suType;
	}

	public void setSuType(Integer suType) {
		this.suType = suType;
	}

	public Integer getSuCouponLimitTime() {
		return suCouponLimitTime;
	}

	public void setSuCouponLimitTime(Integer suCouponLimitTime) {
		this.suCouponLimitTime = suCouponLimitTime;
	}

	public Integer getSuPointLimitTime() {
		return suPointLimitTime;
	}

	public void setSuPointLimitTime(Integer suPointLimitTime) {
		this.suPointLimitTime = suPointLimitTime;
	}

	/*public String getSuPointLimit() {
		return suPointLimit;
	}

	public void setSuPointLimit(String suPointLimit) {
		this.suPointLimit = suPointLimit;
	}*/

	public String getSuPointExpireTime() {
		return suPointExpireTime;
	}

	public void setSuPointExpireTime(String suPointExpireTime) {
		this.suPointExpireTime = suPointExpireTime;
	}

	public String getSuParkName() {
		return suParkName;
	}

	public void setSuParkName(String suParkName) {
		this.suParkName = suParkName;
	}

	public String getSuCouponLimit() {
		return suCouponLimit;
	}

	public void setSuCouponLimit(String suCouponLimit) {
		this.suCouponLimit = suCouponLimit;
	}
	
	

}
