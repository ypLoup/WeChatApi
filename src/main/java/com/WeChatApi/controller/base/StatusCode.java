package com.WeChatApi.controller.base;


/**
 * Œƒµµ±‡¬Î
 */
public enum StatusCode {

  SUCESS(200, "Success", ""),
  SUCESSLAYUI(0, "Success", ""),
  SUCESSWECHAT(0, "Success", ""),
  NOTFOUND(404, "", ""),
  FAILUER(901, "Failur", ""),
  SESSION_TIMEOUT(902, "SESSION_TIMEOUT", ""),
  SYSTEM_ERROR(900, "SYSTEM_ERROR", ""),
  SYSTEM_ILLEGAL(903, "SYSTEM_ILLEGAL", ""),
  DATA_NOT_EXISTS(706, "DATA_NOT_EXISTS", ""),
  DATA_IS_EXISTS(707, "DATA_IS_EXISTS", ""),
  DATA_NOT_MATCH(708, "DATA_NOT_MATCH", ""),

  SKU_IS_EXISTS(707, "SKU_IS_EXISTS", ""),
  SHOP_ID_NOT_EXISTS(717, "SHOP_ID_NOT_EXISTS", ""),
  MISSING_PARAMETER_ERROR(701, "MISSING_PARAMETER_ERROR", ""),
  USER_LOGIN_ERROR(702, "USER_LOGIN_ERROR", ""),
  USER_DOES_NOT_EXIST(703, "USER_DOES_NOT_EXIST", ""),
  NO_PERMISSION_ERROR(704, "NO_PERMISSION_ERROR", ""),
  PARAMETER_FORMATE_RROR(705, "PARAMETER_FORMATE_RROR", ""),
  PASSWORD_DIFFERENT(706, "PASSWORD_DIFFERENT", ""),
  OLDPASSWORD_RROR(709, "OLDPASSWORD_RROR", ""),
  USER_EXIST_RROR(710, "USER_EXIST_RROR", ""),
  API_FREQUENTLY_ERROR(711, "API_FREQUENTLY_ERROR", ""),
  PICTURE_FORMAT_ERROR(712, "PICTURE_FORMAT_ERROR", ""),
  TIME_FORMAT_ERROR(713, "TIME_FORMAT_ERROR", ""),
  CREDITAMOUNT_ERROR(714, "CREDITAMOUNT_ERROR", ""),
  PUSH_DATA_FAILURE(715, "PUSH_DATA_FAILURE", ""),
  ORDER_TYPE_FAILURE(716, "ORDER_TYPE_FAILURE", ""),
  ORDERID_NOT_EXIST(717, "ORDERID_NOT_EXIST", ""),
  DEPT_HAS_CHILD_DELETE_ERROR(801, "DEPT_HAS_CHILD_DELETE_ERROR", ""),
  DEPT_HAS_USER_DELETE_ERROR(802, "DEPT_HAS_USER_DELETE_ERROR", ""),
  RULID_NOT_FIND_ERROR(803, "RULID_NOT_FIND_ERROR", ""),
  DELETE_ERROR(804, "DELETE_ERROR", ""),
  MISSING_OPENID_ERROR(718,"MISSING_OPENID_ERROR","");




  
  private int code;

  /**
   * ‰ø°ÊÅØ
   */
  private String errorMsg;

  /**
   * ËØ¥Êòé
   */
  private String remark;

  private StatusCode(int code, String errorMsg, String remark) {
    this.code = code;
    this.errorMsg = errorMsg;
    this.remark = remark;
  }


  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

}
