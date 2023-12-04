package com.WeChatApi.bean.dto;

import com.WeChatApi.bean.models.invoice;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class doInvoiceDto extends invoice {
	
	private String userOpenId;
	
	private String outTradeNos;
	
    private String rId;
	
	private String rIds;
	
	private String vehPlate;
	
	private String type;
	
	private String invoicePrice;

	public String getOutTradeNos() {
		return outTradeNos;
	}

	public void setOutTradeNos(String outTradeNos) {
		this.outTradeNos = outTradeNos;
	}

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	public String getrId() {
		return rId;
	}

	public void setrId(String rId) {
		this.rId = rId;
	}

	public String getrIds() {
		return rIds;
	}

	public void setrIds(String rIds) {
		this.rIds = rIds;
	}

	public String getVehPlate() {
		return vehPlate;
	}

	public void setVehPlate(String vehPlate) {
		this.vehPlate = vehPlate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInvoicePrice() {
		return invoicePrice;
	}

	public void setInvoicePrice(String invoicePrice) {
		this.invoicePrice = invoicePrice;
	}
	
	
}
