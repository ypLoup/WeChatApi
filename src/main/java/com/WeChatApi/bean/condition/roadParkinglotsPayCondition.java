package com.WeChatApi.bean.condition;

import java.util.List;
import java.util.Map;

import com.WeChatApi.controller.base.PageCondition;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class roadParkinglotsPayCondition extends PageCondition{
	
    private Integer pay_id;
    
    private String order_number;
    private List<String> order_numberList;
    
    private String pkl_code;
    
    private String pkll_code;
    
    private String veh_plate;
    
    private String veh_plates;
    
    private String veh_plates_colours;
    
    private List<Map<String, String>>vehPlateMapLits;
    
    private List<String> VehPlateLits;
    
    private String pay_number;
    
    private String platform_txn_no;
    
    private String channel_txn_no;
    
    private Integer pay_receipts;
    
    //private Double pay_receipts_txt;
    
    private Integer free_reason;
    
    private Integer pay_method;
    
    //private String pay_kind;
    
    //private String pay_channel;
    
    private Integer pu_id;
    
    private String pay_time;
    
    private String pay_remark;
    
    private String pay_origin;
    
    private Integer refund_amount;
    
    private String refund_reason;
    
    private String refund_time;
    
    private Integer pay_status;
    
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

	public String getVeh_plates() {
		return veh_plates;
	}

	public void setVeh_plates(String veh_plates) {
		this.veh_plates = veh_plates;
	}

	public List<String> getVehPlateLits() {
		return VehPlateLits;
	}

	public void setVehPlateLits(List<String> vehPlateLits) {
		VehPlateLits = vehPlateLits;
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

	public String getPay_origin() {
		return pay_origin;
	}

	public void setPay_origin(String pay_origin) {
		this.pay_origin = pay_origin;
	}

	public Integer getRefund_amount() {
		return refund_amount;
	}

	public void setRefund_amount(Integer refund_amount) {
		this.refund_amount = refund_amount;
	}

	public String getRefund_reason() {
		return refund_reason;
	}

	public void setRefund_reason(String refund_reason) {
		this.refund_reason = refund_reason;
	}

	public String getRefund_time() {
		return refund_time;
	}

	public void setRefund_time(String refund_time) {
		this.refund_time = refund_time;
	}

	public Integer getPay_status() {
		return pay_status;
	}

	public void setPay_status(Integer pay_status) {
		this.pay_status = pay_status;
	}

	public List<String> getOrder_numberList() {
		return order_numberList;
	}

	public void setOrder_numberList(List<String> order_numberList) {
		this.order_numberList = order_numberList;
	}

	public String getVeh_plates_colours() {
		return veh_plates_colours;
	}

	public void setVeh_plates_colours(String veh_plates_colours) {
		this.veh_plates_colours = veh_plates_colours;
	}

	public List<Map<String, String>> getVehPlateMapLits() {
		return vehPlateMapLits;
	}

	public void setVehPlateMapLits(List<Map<String, String>> vehPlateMapLits) {
		this.vehPlateMapLits = vehPlateMapLits;
	}
}
