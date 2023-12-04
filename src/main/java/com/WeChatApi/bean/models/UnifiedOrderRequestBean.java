package com.WeChatApi.bean.models;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 涓绘壂鍥涚爜鑱氬悎-璁㈠崟鍒涘缓璇锋眰鍙傛暟Bean瀵硅薄
 *
 * @version v1.0.0
 * @author ruanmin
 * @Date: 2020骞�09鏈�01鏃�
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnifiedOrderRequestBean implements Serializable {

	
	/**
	 * @Fields serialVersionUID:瀵硅薄璇存槑
	 */
	private static final long serialVersionUID = 8410446766333181753L;
	
	private String appTp;//应用类型，01-电脑端WEB接入，02-POS接入，03-移动应用接入，04-移动端H5接入
	private String purpPrtry;
	private String merchId;
	private String tradeType;
	private String channelType;
	private String subAppid;
	private String bizId;
	private String merchantOrderNo;
	private String bizTime;
	private String bizRemark;
	private String bizCurrency;
	private String bizAmount;
	private String discountableAmount;
	private String undiscountableAmount;
	private String settleCurrency ;
	private String ordTitle;
	private String ordDesc;
	private String subOpenid;
	private String sellerId;
	private String deviceNo ;
	private String deviceInfo;
	private String operatorId ;
	private String storeId ;
	private String thirdStoreId ;
	private String attach;
	private String timeStart;
	private String timeExpire;
	private String limitTunnel;
	private String limitPay;
	private String needReceipt;
	private String goodsTag;
	private String advancePaymentType;
	private String industrySepcDetail;
	private Map businessParams;
	private Map royaltyInfo ;
	private Map proDetail;
	private Map reIdentity;
	private Map sceneInfo;
	private Map extendParams ;
	private Map agreementParams;
	public String getAppTp() {
		return appTp;
	}
	public void setAppTp(String appTp) {
		this.appTp = appTp;
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
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	public String getSubAppid() {
		return subAppid;
	}
	public void setSubAppid(String subAppid) {
		this.subAppid = subAppid;
	}
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public String getMerchantOrderNo() {
		return merchantOrderNo;
	}
	public void setMerchantOrderNo(String merchantOrderNo) {
		this.merchantOrderNo = merchantOrderNo;
	}
	public String getBizTime() {
		return bizTime;
	}
	public void setBizTime(String bizTime) {
		this.bizTime = bizTime;
	}
	public String getBizRemark() {
		return bizRemark;
	}
	public void setBizRemark(String bizRemark) {
		this.bizRemark = bizRemark;
	}
	public String getBizCurrency() {
		return bizCurrency;
	}
	public void setBizCurrency(String bizCurrency) {
		this.bizCurrency = bizCurrency;
	}
	public String getBizAmount() {
		return bizAmount;
	}
	public void setBizAmount(String bizAmount) {
		this.bizAmount = bizAmount;
	}
	public String getDiscountableAmount() {
		return discountableAmount;
	}
	public void setDiscountableAmount(String discountableAmount) {
		this.discountableAmount = discountableAmount;
	}
	public String getUndiscountableAmount() {
		return undiscountableAmount;
	}
	public void setUndiscountableAmount(String undiscountableAmount) {
		this.undiscountableAmount = undiscountableAmount;
	}
	public String getSettleCurrency() {
		return settleCurrency;
	}
	public void setSettleCurrency(String settleCurrency) {
		this.settleCurrency = settleCurrency;
	}
	public String getOrdTitle() {
		return ordTitle;
	}
	public void setOrdTitle(String ordTitle) {
		this.ordTitle = ordTitle;
	}
	public String getOrdDesc() {
		return ordDesc;
	}
	public void setOrdDesc(String ordDesc) {
		this.ordDesc = ordDesc;
	}
	public String getSubOpenid() {
		return subOpenid;
	}
	public void setSubOpenid(String subOpenid) {
		this.subOpenid = subOpenid;
	}
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
	public String getDeviceNo() {
		return deviceNo;
	}
	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}
	public String getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public String getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getThirdStoreId() {
		return thirdStoreId;
	}
	public void setThirdStoreId(String thirdStoreId) {
		this.thirdStoreId = thirdStoreId;
	}
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
	}
	public String getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(String timeStart) {
		this.timeStart = timeStart;
	}
	public String getTimeExpire() {
		return timeExpire;
	}
	public void setTimeExpire(String timeExpire) {
		this.timeExpire = timeExpire;
	}
	public String getLimitTunnel() {
		return limitTunnel;
	}
	public void setLimitTunnel(String limitTunnel) {
		this.limitTunnel = limitTunnel;
	}
	public String getLimitPay() {
		return limitPay;
	}
	public void setLimitPay(String limitPay) {
		this.limitPay = limitPay;
	}
	public String getNeedReceipt() {
		return needReceipt;
	}
	public void setNeedReceipt(String needReceipt) {
		this.needReceipt = needReceipt;
	}
	public String getGoodsTag() {
		return goodsTag;
	}
	public void setGoodsTag(String goodsTag) {
		this.goodsTag = goodsTag;
	}
	public String getAdvancePaymentType() {
		return advancePaymentType;
	}
	public void setAdvancePaymentType(String advancePaymentType) {
		this.advancePaymentType = advancePaymentType;
	}
	public String getIndustrySepcDetail() {
		return industrySepcDetail;
	}
	public void setIndustrySepcDetail(String industrySepcDetail) {
		this.industrySepcDetail = industrySepcDetail;
	}
	public Map getBusinessParams() {
		return businessParams;
	}
	public void setBusinessParams(Map businessParams) {
		this.businessParams = businessParams;
	}
	public Map getRoyaltyInfo() {
		return royaltyInfo;
	}
	public void setRoyaltyInfo(Map royaltyInfo) {
		this.royaltyInfo = royaltyInfo;
	}
	public Map getProDetail() {
		return proDetail;
	}
	public void setProDetail(Map proDetail) {
		this.proDetail = proDetail;
	}
	public Map getReIdentity() {
		return reIdentity;
	}
	public void setReIdentity(Map reIdentity) {
		this.reIdentity = reIdentity;
	}
	public Map getSceneInfo() {
		return sceneInfo;
	}
	public void setSceneInfo(Map sceneInfo) {
		this.sceneInfo = sceneInfo;
	}
	public Map getExtendParams() {
		return extendParams;
	}
	public void setExtendParams(Map extendParams) {
		this.extendParams = extendParams;
	}
	public Map getAgreementParams() {
		return agreementParams;
	}
	public void setAgreementParams(Map agreementParams) {
		this.agreementParams = agreementParams;
	}

}
