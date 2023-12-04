package com.WeChatApi.bean.models;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WXOrderCreateGetRequestBean implements Serializable {

	
	private static final long serialVersionUID = -6550181778591862295L;

	private String appTp;
	private String txTp;
	private String purpPrtry;
	private String merchId;
	private String bizId;
	private String bizRemark;
	private String feeType;
	private String payCtAmount;
	private String productDesc;
	private String channelType;
	private String deviceInfo;
	private String proDetail;
	private String attach;
	private String userIp;
	private String userMac;
	private String goodsTag;
	private String tradeType;
	private String limitPay;
	private String subOpenid;
	private String subAppid;
	private String needReceipt;

	private List<reIdentity> reIdentity;

	public static reIdentity newReIdentity() {
		return new reIdentity();
	}

	public static sceneInfo newSceneInfo() {
		return new sceneInfo();
	}

	private List<sceneInfo> sceneInfo;

	public String getAppTp() {
		return appTp;
	}

	public void setAppTp(String appTp) {
		this.appTp = appTp;
	}

	public String getTxTp() {
		return txTp;
	}

	public void setTxTp(String txTp) {
		this.txTp = txTp;
	}

	public String getPurpPrtry() {
		return purpPrtry;
	}

	public void setPurpPrtry(String purpPrtry) {
		this.purpPrtry = purpPrtry;
	}

	public String getMerchId() {
		return merchId;
	}

	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public String getBizRemark() {
		return bizRemark;
	}

	public void setBizRemark(String bizRemark) {
		this.bizRemark = bizRemark;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getPayCtAmount() {
		return payCtAmount;
	}

	public void setPayCtAmount(String payCtAmount) {
		this.payCtAmount = payCtAmount;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getProDetail() {
		return proDetail;
	}

	public void setProDetail(String proDetail) {
		this.proDetail = proDetail;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public String getUserMac() {
		return userMac;
	}

	public void setUserMac(String userMac) {
		this.userMac = userMac;
	}

	public String getGoodsTag() {
		return goodsTag;
	}

	public void setGoodsTag(String goodsTag) {
		this.goodsTag = goodsTag;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getLimitPay() {
		return limitPay;
	}

	public void setLimitPay(String limitPay) {
		this.limitPay = limitPay;
	}

	public String getSubOpenid() {
		return subOpenid;
	}

	public void setSubOpenid(String subOpenid) {
		this.subOpenid = subOpenid;
	}

	public String getSubAppid() {
		return subAppid;
	}

	public void setSubAppid(String subAppid) {
		this.subAppid = subAppid;
	}

	public String getNeedReceipt() {
		return needReceipt;
	}

	public void setNeedReceipt(String needReceipt) {
		this.needReceipt = needReceipt;
	}

	public List<reIdentity> getReIdentity() {
		return reIdentity;
	}

	public void setReIdentity(List<reIdentity> reIdentity) {
		this.reIdentity = reIdentity;
	}

	public List<sceneInfo> getSceneInfo() {
		return sceneInfo;
	}

	public void setSceneInfo(List<sceneInfo> sceneInfo) {
		this.sceneInfo = sceneInfo;
	}

	public static class sceneInfo {
		private String id;
		private String name;
		private String areaCode;
		private String address;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAreaCode() {
			return areaCode;
		}

		public void setAreaCode(String areaCode) {
			this.areaCode = areaCode;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

	}

	public static class reIdentity {
		private String type;
		private String number;
		private String name;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}
