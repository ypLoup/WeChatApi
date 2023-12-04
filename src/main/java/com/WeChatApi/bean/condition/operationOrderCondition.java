package com.WeChatApi.bean.condition;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.WeChatApi.controller.base.PageCondition;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class operationOrderCondition extends PageCondition  {
	
	private String userOpenId;
	
	private	Integer	order_id;
	private	String	order_number;
	private	String	pkl_code;
	private	String	pkll_code;
	private	String	pkls_code;
	private	String	order_entry_pklc_code;
	private	String	order_entry_pklc_name;
	private	Date	order_entry_tried_time;
	private	Date	order_entry_time;
	private	String	order_entry_photo;
	private	String	order_entry_photo_plate;
	private	String	order_entry_etc;
	private	String	order_exit_pklc_code;
	private	String	order_exit_pklc_name;
	private	Date	order_exit_tried_time;
	private	Date	order_exit_time;
	private	String	order_duration_desc;
	private	String	order_exit_photo;
	private	String	order_exit_photo_plate;
	private	String	order_exit_etc;
	private	String	out_trade_no;
	private	Date	post_entry_time;
	private	Date	post_exit_time;
	private	Integer	veh_type;
	private	Integer	veh_color;
	private	String	veh_plate;
	private String  veh_plates;
	private String  veh_auth_id;
	private List<String>vehPlateLits;
	private	Integer	veh_plate_color;
	private	String	veh_plate_color_txt;
	private	Integer	veh_etc;
	private	Integer	veh_uncpay;
	private	String	car_type;
	private	Integer	pu_id;
	private	Integer	order_receivable;
	private	Integer	order_discount;
	private	Integer	order_discount_type;
	private	Integer	order_receipts;
	private	Integer	on_line_charge;
	private	Integer	off_line_charge;
	private	Integer	order_free_reason;
	private	Integer	order_pay_method;
	private	Date	order_pay_time;
	private	Integer	order_arrears;
	private	Integer	order_inner_pay;
	private	String	order_cancel_reason;
	private	Integer	order_cancel_pu_id;
	private	Date	order_cancel_time;
	private	String	order_cancel_arrears_reason;
	private	Integer	order_cancel_arrears_pu_id;
	private	Date	order_cancel_arrears_time;
	private	String	visit_reason;
	private	String	open_gate_mode;
	private	String	match_mode;
	private	String	id_card;
	private	Integer	confidence;
	private	String	barrior_open;
	private	String	cost_time;
	private	String	user_id_card;
	private	String	user_name;
	private	String	user_phone;
	private	String	user_address;
	private	Integer	area_id;
	private	String	area_name;
	private	String	place_number;
	private	String	memo;
	private	Integer	operator_id;
	private	String	operator_name;
	private	String	invoice_no;
	private	String	cost_time_exit;
	private	String	veh_plate_exit;
	private	String	veh_plate_color_txt_exit;
	private	Integer	confidence_exit;
	private	String	car_type_exit;
	private	String	user_id_card_exit;
	private	String	open_gate_mode_exit;
	private	String	match_mode_exit;
	private	Integer	area_id_exit;
	private	String	area_name_exit;
	private	String	place_number_exit;
	private	String	memo_exit;
	private	String	order_remark;
	private	Integer	order_status;
	private List<Map<String, String>>vehPlateMapLits;
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
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
	public String getPkls_code() {
		return pkls_code;
	}
	public void setPkls_code(String pkls_code) {
		this.pkls_code = pkls_code;
	}
	public String getOrder_entry_pklc_code() {
		return order_entry_pklc_code;
	}
	public void setOrder_entry_pklc_code(String order_entry_pklc_code) {
		this.order_entry_pklc_code = order_entry_pklc_code;
	}
	public String getOrder_entry_pklc_name() {
		return order_entry_pklc_name;
	}
	public void setOrder_entry_pklc_name(String order_entry_pklc_name) {
		this.order_entry_pklc_name = order_entry_pklc_name;
	}
	public Date getOrder_entry_tried_time() {
		return order_entry_tried_time;
	}
	public void setOrder_entry_tried_time(Date order_entry_tried_time) {
		this.order_entry_tried_time = order_entry_tried_time;
	}
	public Date getOrder_entry_time() {
		return order_entry_time;
	}
	public void setOrder_entry_time(Date order_entry_time) {
		this.order_entry_time = order_entry_time;
	}
	public String getOrder_entry_photo() {
		return order_entry_photo;
	}
	public void setOrder_entry_photo(String order_entry_photo) {
		this.order_entry_photo = order_entry_photo;
	}
	public String getOrder_entry_photo_plate() {
		return order_entry_photo_plate;
	}
	public void setOrder_entry_photo_plate(String order_entry_photo_plate) {
		this.order_entry_photo_plate = order_entry_photo_plate;
	}
	public String getOrder_entry_etc() {
		return order_entry_etc;
	}
	public void setOrder_entry_etc(String order_entry_etc) {
		this.order_entry_etc = order_entry_etc;
	}
	public String getOrder_exit_pklc_code() {
		return order_exit_pklc_code;
	}
	public void setOrder_exit_pklc_code(String order_exit_pklc_code) {
		this.order_exit_pklc_code = order_exit_pklc_code;
	}
	public String getOrder_exit_pklc_name() {
		return order_exit_pklc_name;
	}
	public void setOrder_exit_pklc_name(String order_exit_pklc_name) {
		this.order_exit_pklc_name = order_exit_pklc_name;
	}
	public Date getOrder_exit_tried_time() {
		return order_exit_tried_time;
	}
	public void setOrder_exit_tried_time(Date order_exit_tried_time) {
		this.order_exit_tried_time = order_exit_tried_time;
	}
	public Date getOrder_exit_time() {
		return order_exit_time;
	}
	public void setOrder_exit_time(Date order_exit_time) {
		this.order_exit_time = order_exit_time;
	}
	public String getOrder_duration_desc() {
		return order_duration_desc;
	}
	public void setOrder_duration_desc(String order_duration_desc) {
		this.order_duration_desc = order_duration_desc;
	}
	public String getOrder_exit_photo() {
		return order_exit_photo;
	}
	public void setOrder_exit_photo(String order_exit_photo) {
		this.order_exit_photo = order_exit_photo;
	}
	public String getOrder_exit_photo_plate() {
		return order_exit_photo_plate;
	}
	public void setOrder_exit_photo_plate(String order_exit_photo_plate) {
		this.order_exit_photo_plate = order_exit_photo_plate;
	}
	public String getOrder_exit_etc() {
		return order_exit_etc;
	}
	public void setOrder_exit_etc(String order_exit_etc) {
		this.order_exit_etc = order_exit_etc;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public Date getPost_entry_time() {
		return post_entry_time;
	}
	public void setPost_entry_time(Date post_entry_time) {
		this.post_entry_time = post_entry_time;
	}
	public Date getPost_exit_time() {
		return post_exit_time;
	}
	public void setPost_exit_time(Date post_exit_time) {
		this.post_exit_time = post_exit_time;
	}
	public Integer getVeh_type() {
		return veh_type;
	}
	public void setVeh_type(Integer veh_type) {
		this.veh_type = veh_type;
	}
	public Integer getVeh_color() {
		return veh_color;
	}
	public void setVeh_color(Integer veh_color) {
		this.veh_color = veh_color;
	}
	public String getVeh_plate() {
		return veh_plate;
	}
	public void setVeh_plate(String veh_plate) {
		this.veh_plate = veh_plate;
	}
	public Integer getVeh_plate_color() {
		return veh_plate_color;
	}
	public void setVeh_plate_color(Integer veh_plate_color) {
		this.veh_plate_color = veh_plate_color;
	}
	public String getVeh_plate_color_txt() {
		return veh_plate_color_txt;
	}
	public void setVeh_plate_color_txt(String veh_plate_color_txt) {
		this.veh_plate_color_txt = veh_plate_color_txt;
	}
	public Integer getVeh_etc() {
		return veh_etc;
	}
	public void setVeh_etc(Integer veh_etc) {
		this.veh_etc = veh_etc;
	}
	public Integer getVeh_uncpay() {
		return veh_uncpay;
	}
	public void setVeh_uncpay(Integer veh_uncpay) {
		this.veh_uncpay = veh_uncpay;
	}
	public String getCar_type() {
		return car_type;
	}
	public void setCar_type(String car_type) {
		this.car_type = car_type;
	}
	public Integer getPu_id() {
		return pu_id;
	}
	public void setPu_id(Integer pu_id) {
		this.pu_id = pu_id;
	}
	public Integer getOrder_receivable() {
		return order_receivable;
	}
	public void setOrder_receivable(Integer order_receivable) {
		this.order_receivable = order_receivable;
	}
	public Integer getOrder_discount() {
		return order_discount;
	}
	public void setOrder_discount(Integer order_discount) {
		this.order_discount = order_discount;
	}
	public Integer getOrder_discount_type() {
		return order_discount_type;
	}
	public void setOrder_discount_type(Integer order_discount_type) {
		this.order_discount_type = order_discount_type;
	}
	public Integer getOrder_receipts() {
		return order_receipts;
	}
	public void setOrder_receipts(Integer order_receipts) {
		this.order_receipts = order_receipts;
	}
	public Integer getOn_line_charge() {
		return on_line_charge;
	}
	public void setOn_line_charge(Integer on_line_charge) {
		this.on_line_charge = on_line_charge;
	}
	public Integer getOff_line_charge() {
		return off_line_charge;
	}
	public void setOff_line_charge(Integer off_line_charge) {
		this.off_line_charge = off_line_charge;
	}
	public Integer getOrder_free_reason() {
		return order_free_reason;
	}
	public void setOrder_free_reason(Integer order_free_reason) {
		this.order_free_reason = order_free_reason;
	}
	public Integer getOrder_pay_method() {
		return order_pay_method;
	}
	public void setOrder_pay_method(Integer order_pay_method) {
		this.order_pay_method = order_pay_method;
	}
	public Date getOrder_pay_time() {
		return order_pay_time;
	}
	public void setOrder_pay_time(Date order_pay_time) {
		this.order_pay_time = order_pay_time;
	}
	public Integer getOrder_arrears() {
		return order_arrears;
	}
	public void setOrder_arrears(Integer order_arrears) {
		this.order_arrears = order_arrears;
	}
	public Integer getOrder_inner_pay() {
		return order_inner_pay;
	}
	public void setOrder_inner_pay(Integer order_inner_pay) {
		this.order_inner_pay = order_inner_pay;
	}
	public String getOrder_cancel_reason() {
		return order_cancel_reason;
	}
	public void setOrder_cancel_reason(String order_cancel_reason) {
		this.order_cancel_reason = order_cancel_reason;
	}
	public Integer getOrder_cancel_pu_id() {
		return order_cancel_pu_id;
	}
	public void setOrder_cancel_pu_id(Integer order_cancel_pu_id) {
		this.order_cancel_pu_id = order_cancel_pu_id;
	}
	public Date getOrder_cancel_time() {
		return order_cancel_time;
	}
	public void setOrder_cancel_time(Date order_cancel_time) {
		this.order_cancel_time = order_cancel_time;
	}
	public String getOrder_cancel_arrears_reason() {
		return order_cancel_arrears_reason;
	}
	public void setOrder_cancel_arrears_reason(String order_cancel_arrears_reason) {
		this.order_cancel_arrears_reason = order_cancel_arrears_reason;
	}
	public Integer getOrder_cancel_arrears_pu_id() {
		return order_cancel_arrears_pu_id;
	}
	public void setOrder_cancel_arrears_pu_id(Integer order_cancel_arrears_pu_id) {
		this.order_cancel_arrears_pu_id = order_cancel_arrears_pu_id;
	}
	public Date getOrder_cancel_arrears_time() {
		return order_cancel_arrears_time;
	}
	public void setOrder_cancel_arrears_time(Date order_cancel_arrears_time) {
		this.order_cancel_arrears_time = order_cancel_arrears_time;
	}
	public String getVisit_reason() {
		return visit_reason;
	}
	public void setVisit_reason(String visit_reason) {
		this.visit_reason = visit_reason;
	}
	public String getOpen_gate_mode() {
		return open_gate_mode;
	}
	public void setOpen_gate_mode(String open_gate_mode) {
		this.open_gate_mode = open_gate_mode;
	}
	public String getMatch_mode() {
		return match_mode;
	}
	public void setMatch_mode(String match_mode) {
		this.match_mode = match_mode;
	}
	public String getId_card() {
		return id_card;
	}
	public void setId_card(String id_card) {
		this.id_card = id_card;
	}
	public Integer getConfidence() {
		return confidence;
	}
	public void setConfidence(Integer confidence) {
		this.confidence = confidence;
	}
	public String getBarrior_open() {
		return barrior_open;
	}
	public void setBarrior_open(String barrior_open) {
		this.barrior_open = barrior_open;
	}
	public String getCost_time() {
		return cost_time;
	}
	public void setCost_time(String cost_time) {
		this.cost_time = cost_time;
	}
	public String getUser_id_card() {
		return user_id_card;
	}
	public void setUser_id_card(String user_id_card) {
		this.user_id_card = user_id_card;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_phone() {
		return user_phone;
	}
	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}
	public String getUser_address() {
		return user_address;
	}
	public void setUser_address(String user_address) {
		this.user_address = user_address;
	}
	public Integer getArea_id() {
		return area_id;
	}
	public void setArea_id(Integer area_id) {
		this.area_id = area_id;
	}
	public String getArea_name() {
		return area_name;
	}
	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}
	public String getPlace_number() {
		return place_number;
	}
	public void setPlace_number(String place_number) {
		this.place_number = place_number;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Integer getOperator_id() {
		return operator_id;
	}
	public void setOperator_id(Integer operator_id) {
		this.operator_id = operator_id;
	}
	public String getOperator_name() {
		return operator_name;
	}
	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}
	public String getInvoice_no() {
		return invoice_no;
	}
	public void setInvoice_no(String invoice_no) {
		this.invoice_no = invoice_no;
	}
	public String getCost_time_exit() {
		return cost_time_exit;
	}
	public void setCost_time_exit(String cost_time_exit) {
		this.cost_time_exit = cost_time_exit;
	}
	public String getVeh_plate_exit() {
		return veh_plate_exit;
	}
	public void setVeh_plate_exit(String veh_plate_exit) {
		this.veh_plate_exit = veh_plate_exit;
	}
	public String getVeh_plate_color_txt_exit() {
		return veh_plate_color_txt_exit;
	}
	public void setVeh_plate_color_txt_exit(String veh_plate_color_txt_exit) {
		this.veh_plate_color_txt_exit = veh_plate_color_txt_exit;
	}
	public Integer getConfidence_exit() {
		return confidence_exit;
	}
	public void setConfidence_exit(Integer confidence_exit) {
		this.confidence_exit = confidence_exit;
	}
	public String getCar_type_exit() {
		return car_type_exit;
	}
	public void setCar_type_exit(String car_type_exit) {
		this.car_type_exit = car_type_exit;
	}
	public String getUser_id_card_exit() {
		return user_id_card_exit;
	}
	public void setUser_id_card_exit(String user_id_card_exit) {
		this.user_id_card_exit = user_id_card_exit;
	}
	public String getOpen_gate_mode_exit() {
		return open_gate_mode_exit;
	}
	public void setOpen_gate_mode_exit(String open_gate_mode_exit) {
		this.open_gate_mode_exit = open_gate_mode_exit;
	}
	public String getMatch_mode_exit() {
		return match_mode_exit;
	}
	public void setMatch_mode_exit(String match_mode_exit) {
		this.match_mode_exit = match_mode_exit;
	}
	public Integer getArea_id_exit() {
		return area_id_exit;
	}
	public void setArea_id_exit(Integer area_id_exit) {
		this.area_id_exit = area_id_exit;
	}
	public String getArea_name_exit() {
		return area_name_exit;
	}
	public void setArea_name_exit(String area_name_exit) {
		this.area_name_exit = area_name_exit;
	}
	public String getPlace_number_exit() {
		return place_number_exit;
	}
	public void setPlace_number_exit(String place_number_exit) {
		this.place_number_exit = place_number_exit;
	}
	public String getMemo_exit() {
		return memo_exit;
	}
	public void setMemo_exit(String memo_exit) {
		this.memo_exit = memo_exit;
	}
	public String getOrder_remark() {
		return order_remark;
	}
	public void setOrder_remark(String order_remark) {
		this.order_remark = order_remark;
	}
	public Integer getOrder_status() {
		return order_status;
	}
	public void setOrder_status(Integer order_status) {
		this.order_status = order_status;
	}
	public String getVeh_plates() {
		return veh_plates;
	}
	public void setVeh_plates(String veh_plates) {
		this.veh_plates = veh_plates;
	}
	public List<String> getVehPlateLits() {
		return vehPlateLits;
	}
	public void setVehPlateLits(List<String> vehPlateLits) {
		this.vehPlateLits = vehPlateLits;
	}
	public String getVeh_auth_id() {
		return veh_auth_id;
	}
	public void setVeh_auth_id(String veh_auth_id) {
		this.veh_auth_id = veh_auth_id;
	}
	public String getUserOpenId() {
		return userOpenId;
	}
	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}
	public List<Map<String, String>> getVehPlateMapLits() {
		return vehPlateMapLits;
	}
	public void setVehPlateMapLits(List<Map<String, String>> vehPlateMapLits) {
		this.vehPlateMapLits = vehPlateMapLits;
	}
	
}
