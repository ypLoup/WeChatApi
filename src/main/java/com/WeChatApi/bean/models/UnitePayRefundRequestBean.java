package com.WeChatApi.bean.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 退款申请
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnitePayRefundRequestBean implements Serializable{

	/**
     * @Fields serialVersionUID:对象说明
     */
    private static final long serialVersionUID = -6550181778591862295L;
    
    private String appTp;
    private String purpPrtry;
    private String merchId;
    private String bizId;
    private String bizTime;
    private String orderId;
    private String refundBizid;
    private String bizCurrency;
    private String bizAmount;
    private String refundDesc;
    private String bizRemark;
    private String attach;
    private String operatorId;
    private String storeId;
    private String deviceNo;
    private BusinessParams businessParams;
    private GoodsDetail goodsDetail;
    
    public static BusinessParams newBusinessParams() {
    	return new BusinessParams();
    }
    public static GoodsDetail newGoodsDetail() {
    	return new GoodsDetail();
    }
    
	public BusinessParams getBusinessParams() {
		return businessParams;
	}
	public void setBusinessParams(BusinessParams businessParams) {
		this.businessParams = businessParams;
	}
	
	public GoodsDetail getGoodsDetail() {
		return goodsDetail;
	}
	public void setGoodsDetail(GoodsDetail goodsDetail) {
		this.goodsDetail = goodsDetail;
	}
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
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public String getBizTime() {
		return bizTime;
	}
	public void setBizTime(String bizTime) {
		this.bizTime = bizTime;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getRefundBizid() {
		return refundBizid;
	}
	public void setRefundBizid(String refundBizid) {
		this.refundBizid = refundBizid;
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
	public String getRefundDesc() {
		return refundDesc;
	}
	public void setRefundDesc(String refundDesc) {
		this.refundDesc = refundDesc;
	}
	public String getBizRemark() {
		return bizRemark;
	}
	public void setBizRemark(String bizRemark) {
		this.bizRemark = bizRemark;
	}
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
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
	public String getDeviceNo() {
		return deviceNo;
	}
	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}
    
	public static class BusinessParams{
		
	}
    public static class GoodsDetail{
    	private String goodsId;
    	private String thirdPayGoodsId;
    	private String goodsName;
    	private String quantity;
    	private String price;
    	private String goodsCategory;
    	private String categoriesTree;
    	private String body;
    	private String showUrl;
		public String getGoodsId() {
			return goodsId;
		}
		public void setGoodsId(String goodsId) {
			this.goodsId = goodsId;
		}
		public String getThirdPayGoodsId() {
			return thirdPayGoodsId;
		}
		public void setThirdPayGoodsId(String thirdPayGoodsId) {
			this.thirdPayGoodsId = thirdPayGoodsId;
		}
		public String getGoodsName() {
			return goodsName;
		}
		public void setGoodsName(String goodsName) {
			this.goodsName = goodsName;
		}
		public String getQuantity() {
			return quantity;
		}
		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getGoodsCategory() {
			return goodsCategory;
		}
		public void setGoodsCategory(String goodsCategory) {
			this.goodsCategory = goodsCategory;
		}
		public String getCategoriesTree() {
			return categoriesTree;
		}
		public void setCategoriesTree(String categoriesTree) {
			this.categoriesTree = categoriesTree;
		}
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		public String getShowUrl() {
			return showUrl;
		}
		public void setShowUrl(String showUrl) {
			this.showUrl = showUrl;
		}
	}
	
}
