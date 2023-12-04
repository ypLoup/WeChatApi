package com.WeChatApi.controller.base;

public class BaseServiceException extends BaseException{
	private static final long serialVersionUID = -7169994746457796701L;

	public BaseServiceException(Integer errorCode, String errorMsg) {
		super(errorCode, errorMsg);
	}

	public BaseServiceException(Integer errorCode, Throwable caused) {
		super(errorCode, caused);
	}

	public BaseServiceException(Integer errorCode, String errorMsg, Throwable caused) {
		super(errorCode, errorMsg, caused);
	}
}
