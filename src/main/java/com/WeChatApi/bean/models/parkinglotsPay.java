package com.WeChatApi.bean.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class parkinglotsPay {
	
    private Integer pay_id;
    
    private String out_trade_no;
    
    private List<String> out_trade_noList;
    
    private String order_number;
    
    private String pkl_code;
    
    private String pkll_code;
    
    private String veh_plate;
    
    private String pay_number;
    
    private String platform_txn_no;
    
    private String channel_txn_no;
    
    private Integer pay_receipts;
    
    private Double pay_receipts_txt;
    
    private Integer free_reason;
    
    private Integer pay_method;
    
    private String pay_kind;
    
    private String pay_channel;
    
    private Integer pu_id;
    
    private String pay_time;
    
    private String pay_remark;
    
    private String blue_card_return;
    
    private Integer refund_amount;
    
    private String invoice_pdf;
    
    private String invoice_datetime;
    
    private Integer invoice_status;
    
    /**********∑¢∆±–≈œ¢************/
    
    private String invoice_company_name;
    
    private String invoice_tax_number;
    
    private String invoice_address_mobile;
    
    private String invoice_bank_number;
    
    private String invoice_email;

	public Integer getPay_id() {
		return pay_id;
	}

	public void setPay_id(Integer pay_id) {
		this.pay_id = pay_id;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getOrder_number() {
		return order_number;
	}

	public void setOrder_number(String order_number) {
		this.order_number = order_number;
	}

	public String getPkl_code() {
		return pkl_code;
	}

	public void setPkl_code(String pkl_code) {
		this.pkl_code = pkl_code;
	}

	public String getPkll_code() {
		return pkll_code;
	}

	public void setPkll_code(String pkll_code) {
		this.pkll_code = pkll_code;
	}

	public String getVeh_plate() {
		return veh_plate;
	}

	public void setVeh_plate(String veh_plate) {
		this.veh_plate = veh_plate;
	}

	public String getPay_number() {
		return pay_number;
	}

	public void setPay_number(String pay_number) {
		this.pay_number = pay_number;
	}

	public String getPlatform_txn_no() {
		return platform_txn_no;
	}

	public void setPlatform_txn_no(String platform_txn_no) {
		this.platform_txn_no = platform_txn_no;
	}

	public String getChannel_txn_no() {
		return channel_txn_no;
	}

	public void setChannel_txn_no(String channel_txn_no) {
		this.channel_txn_no = channel_txn_no;
	}

	public Integer getPay_receipts() {
		return pay_receipts;
	}

	public void setPay_receipts(Integer pay_receipts) {
		this.pay_receipts = pay_receipts;
	}

	public Double getPay_receipts_txt() {
		return pay_receipts_txt;
	}

	public void setPay_receipts_txt(Double pay_receipts_txt) {
		this.pay_receipts_txt = pay_receipts_txt;
	}

	public Integer getFree_reason() {
		return free_reason;
	}

	public void setFree_reason(Integer free_reason) {
		this.free_reason = free_reason;
	}

	public Integer getPay_method() {
		return pay_method;
	}

	public void setPay_method(Integer pay_method) {
		this.pay_method = pay_method;
	}

	public String getPay_kind() {
		return pay_kind;
	}

	public void setPay_kind(String pay_kind) {
		this.pay_kind = pay_kind;
	}

	public String getPay_channel() {
		return pay_channel;
	}

	public void setPay_channel(String pay_channel) {
		this.pay_channel = pay_channel;
	}

	public Integer getPu_id() {
		return pu_id;
	}

	public void setPu_id(Integer pu_id) {
		this.pu_id = pu_id;
	}

	public String getPay_time() {
		return pay_time;
	}

	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}

	public String getPay_remark() {
		return pay_remark;
	}

	public void setPay_remark(String pay_remark) {
		this.pay_remark = pay_remark;
	}

	public String getBlue_card_return() {
		return blue_card_return;
	}

	public void setBlue_card_return(String blue_card_return) {
		this.blue_card_return = blue_card_return;
	}

	public String getInvoice_pdf() {
		return invoice_pdf;
	}

	public void setInvoice_pdf(String invoice_pdf) {
		this.invoice_pdf = invoice_pdf;
	}

	public String getInvoice_datetime() {
		return invoice_datetime;
	}

	public void setInvoice_datetime(String invoice_datetime) {
		this.invoice_datetime = invoice_datetime;
	}

	public Integer getInvoice_status() {
		return invoice_status;
	}

	public void setInvoice_status(Integer invoice_status) {
		this.invoice_status = invoice_status;
	}

	public List<String> getOut_trade_noList() {
		return out_trade_noList;
	}

	public void setOut_trade_noList(List<String> out_trade_noList) {
		this.out_trade_noList = out_trade_noList;
	}

	public Integer getRefund_amount() {
		return refund_amount;
	}

	public void setRefund_amount(Integer refund_amount) {
		this.refund_amount = refund_amount;
	}

	public String getInvoice_company_name() {
		return invoice_company_name;
	}

	public void setInvoice_company_name(String invoice_company_name) {
		this.invoice_company_name = invoice_company_name;
	}

	public String getInvoice_tax_number() {
		return invoice_tax_number;
	}

	public void setInvoice_tax_number(String invoice_tax_number) {
		this.invoice_tax_number = invoice_tax_number;
	}

	public String getInvoice_address_mobile() {
		return invoice_address_mobile;
	}

	public void setInvoice_address_mobile(String invoice_address_mobile) {
		this.invoice_address_mobile = invoice_address_mobile;
	}

	public String getInvoice_bank_number() {
		return invoice_bank_number;
	}

	public void setInvoice_bank_number(String invoice_bank_number) {
		this.invoice_bank_number = invoice_bank_number;
	}

	public String getInvoice_email() {
		return invoice_email;
	}

	public void setInvoice_email(String invoice_email) {
		this.invoice_email = invoice_email;
	}

	

}
