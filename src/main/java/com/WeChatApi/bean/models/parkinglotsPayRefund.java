package com.WeChatApi.bean.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class parkinglotsPayRefund {
	
	private Integer r_id;
	
	private Integer r_pay_id;
	
	private String out_refund_no;
	
	private Integer refund_fee;
	
	private String refund_id;
	
	private String refund_time;
	
	private String last_update_time;
	
	private Integer refund_status;
	
	private String order_type;

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public Integer getR_id() {
		return r_id;
	}

	public void setR_id(Integer r_id) {
		this.r_id = r_id;
	}

	public Integer getR_pay_id() {
		return r_pay_id;
	}

	public void setR_pay_id(Integer r_pay_id) {
		this.r_pay_id = r_pay_id;
	}

	public String getOut_refund_no() {
		return out_refund_no;
	}

	public void setOut_refund_no(String out_refund_no) {
		this.out_refund_no = out_refund_no;
	}

	public Integer getRefund_fee() {
		return refund_fee;
	}

	public void setRefund_fee(Integer refund_fee) {
		this.refund_fee = refund_fee;
	}

	public String getRefund_id() {
		return refund_id;
	}

	public void setRefund_id(String refund_id) {
		this.refund_id = refund_id;
	}

	public String getRefund_time() {
		return refund_time;
	}

	public void setRefund_time(String refund_time) {
		this.refund_time = refund_time;
	}

	public String getLast_update_time() {
		return last_update_time;
	}

	public void setLast_update_time(String last_update_time) {
		this.last_update_time = last_update_time;
	}

	public Integer getRefund_status() {
		return refund_status;
	}

	public void setRefund_status(Integer refund_status) {
		this.refund_status = refund_status;
	}

}
