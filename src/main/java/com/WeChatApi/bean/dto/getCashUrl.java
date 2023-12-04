package com.WeChatApi.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class getCashUrl {
	
	private String parkpotid;//停车场编号
	
	/*private String outtradeNo;//订单编号
	
	private String  vehPlate;//车牌号
*/	
	private String orderno_atm;//现金机订单编号(outtradeNo)
	
	private Integer paymenttotal;//应缴费总金额
	
	private Integer payment;//现金收入金额
	
	private String portname;
	
	private String access_token;
	
	private String extbusinessid;//(蓝卡id)

	public String getParkpotid() {
		return parkpotid;
	}

	public void setParkpotid(String parkpotid) {
		this.parkpotid = parkpotid;
	}

	public String getOrderno_atm() {
		return orderno_atm;
	}

	public void setOrderno_atm(String orderno_atm) {
		this.orderno_atm = orderno_atm;
	}

	public Integer getPaymenttotal() {
		return paymenttotal;
	}

	public void setPaymenttotal(Integer paymenttotal) {
		this.paymenttotal = paymenttotal;
	}

	public Integer getPayment() {
		return payment;
	}

	public void setPayment(Integer payment) {
		this.payment = payment;
	}

	public String getPortname() {
		return portname;
	}

	public void setPortname(String portname) {
		this.portname = portname;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getExtbusinessid() {
		return extbusinessid;
	}

	public void setExtbusinessid(String extbusinessid) {
		this.extbusinessid = extbusinessid;
	}

	
	

}
