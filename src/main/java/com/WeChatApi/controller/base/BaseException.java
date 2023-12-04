package com.WeChatApi.controller.base;

	/**
	 * 
	 * 锟皆讹拷锟斤拷锟届常锟斤拷锟洁，锟斤拷锟斤拷锟届常锟斤拷锟斤拷锟斤拷坛锟�
	 * 
	 */
	public class BaseException extends RuntimeException {

		private static final long serialVersionUID = -7339281910096541147L;

		/**
		 * 锟届常锟斤拷锟斤拷锟斤拷耄癸拷锟�4位锟街凤拷锟斤拷
		 * 锟斤拷一位锟斤拷锟斤拷锟斤拷锟斤拷斐ｏ拷锟较低筹拷锟斤拷锟� 
		 * 锟斤拷锟斤拷位锟斤拷锟斤拷锟斤拷锟侥达拷锟斤拷锟斤拷牒拷锟� 
		 * 锟斤拷锟斤拷锟斤拷锟斤拷删锟斤拷锟侥筹拷锟斤拷锟斤拷锟斤拷
		 */
		
		/**
		 * 未知锟届常
		 */
		public static final Integer CODE_UNKNOW=999;
		
		/**
		 * 锟斤拷锟斤拷锟届常
		 */
		public static final Integer CODE_NET=900;
		
		/**
		 * 锟斤拷锟斤拷锟届常
		 */
		public static final Integer CODE_INSERT=901;
		/**
		 * 锟斤拷锟斤拷锟届常
		 */
		public static final Integer CODE_UPDATE=902;
		/**
		 * 删锟斤拷锟届常
		 */
		public static final Integer CODE_DELETE=903;
		
		/**
		 * 锟斤拷询锟届常
		 */
		public static final Integer CODE_SELECT=904;
		
		/**
		 * 锟斤拷锟斤拷锟斤拷锟斤拷
		 */
		public static final Integer CODE_PARAM=800;
		
		/**
		 * 业锟斤拷锟届常
		 */
		public static final Integer CODE_BIZ=700;
		
		/**
		 * 锟斤拷锟斤拷锟届常
		 */
		public static final Integer CODE_BIZ_LOGIN=704;
		
		/**
		 * 锟斤拷锟斤拷锟脚诧拷锟斤拷锟斤拷
		 */
		public static final Integer ORDER_NO_UNEXIST = 600;

		/**
		 * 锟斤拷锟斤拷锟斤拷锟窖达拷锟斤拷
		 */
		public static final Integer ORDER_EXIST = 601;
		
		/**
		 * 锟斤拷锟斤拷锟斤拷券锟斤拷锟斤拷为锟斤拷
		 */
		public static final Integer MEAL_COUPONS_NULL = 501;
		
		/**
		 * 锟脚碉拷ID锟斤拷锟斤拷为锟斤拷
		 */
		public static final Integer SHOP_ID_NULL = 502;
		
		/**
		 * 锟脚碉拷锟斤拷锟斤拷锟斤拷为锟斤拷
		 */
		public static final Integer SHOP_NAME_NULL = 503;
		
		/**
		 * 锟脚癸拷锟斤拷挪锟斤拷锟轿拷锟�
		 */
		public static final Integer BATCH_NO_NULL = 504;
		
		/**
		 * 锟脚癸拷锟杰斤拷畈伙拷锟轿拷锟�
		 */
		public static final Integer AMOUNT_NULL = 505;
		
		/**
		 * pay_mode锟斤拷锟斤拷为锟斤拷
		 */
		public static final Integer PAY_MODE_NULL = 506;
		
		/**
		 * 支锟斤拷锟斤拷式锟斤拷锟斤拷为锟斤拷
		 */
		public static final Integer PAYMENT_NULL = 507;
		
		/**
		 * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷为锟斤拷
		 */
		public static final Integer QTY_NULL = 508;
		
		/**
		 * 锟斤拷锟斤拷锟剿诧拷锟斤拷为锟斤拷
		 */
		public static final Integer CREATED_BY_NULL = 509;
		
		protected Integer errorCode;

		/** 锟届常锟斤拷锟斤拷锟斤拷息锟斤拷锟斤拷实锟斤拷锟阶筹拷锟届常锟斤拷锟洁定锟斤拷 */
		protected String errorMsg;

		/** 锟斤拷锟届常锟斤拷锟斤拷锟斤拷锟届常锟斤拷 */
		protected Throwable caused;

		public BaseException(Integer errorCode, String errorMsg) {
			super(errorMsg);
			this.errorCode = errorCode;
			this.errorMsg = errorMsg;
		}

		public BaseException(Integer errorCode, Throwable caused) {
			super(caused);
			this.errorCode = errorCode;
			this.caused = caused;
		}

		public BaseException(Integer errorCode, String errorMsg, Throwable caused) {
			super(errorMsg, caused);
			this.errorCode = errorCode;
			this.errorMsg = errorMsg;
			this.caused = caused;
		}

		/**
		 * 锟斤拷锟斤拷斐ｏ拷拇锟斤拷锟斤拷锟斤拷
		 * 
		 * @return the errorCode
		 */
		public Integer getErrorCode() {
			/** 锟斤拷锟斤拷斐ｏ拷锟斤拷锟斤拷舜锟斤拷锟斤拷锟斤拷 */
			if (errorCode != null ) {
				return errorCode;
			}

			/**
			 * 锟斤拷锟矫伙拷卸锟斤拷锟斤拷锟斤拷锟斤拷锟�,锟斤拷锟揭革拷锟届常锟斤拷一锟斤拷锟斤拷锟斤拷斐� 锟津返回革拷锟届常锟侥达拷锟斤拷锟斤拷锟�
			 */
			if (caused != null) {
				if (caused instanceof BaseException) {
					BaseException causedException = (BaseException) caused;
					return causedException.getErrorCode();
				} else {
					return errorCode;
				}
			}

			return errorCode;
		}

		/**
		 * 锟斤拷锟斤拷锟届常锟侥达拷锟斤拷锟斤拷锟�
		 * 
		 * @param errorCode
		 *            the errorCode to set
		 */
		public void setErrorCode(Integer errorCode) {
			this.errorCode = errorCode;
		}

		/**
		 * 锟斤拷锟斤拷斐ｏ拷拇锟斤拷锟斤拷锟较�
		 * 
		 * @return the errorMsg
		 */
		public String getErrorMsg() {
			/** 锟斤拷锟斤拷斐ｏ拷锟斤拷锟斤拷舜锟斤拷锟斤拷锟较� */
			if (errorMsg != null && !"".equals(errorMsg))
				return errorMsg;

			/**
			 * 锟斤拷锟矫伙拷卸锟斤拷锟斤拷锟斤拷锟斤拷息,锟斤拷锟揭革拷锟届常锟斤拷一锟斤拷锟斤拷锟斤拷斐� 锟津返回革拷锟届常锟侥达拷锟斤拷锟斤拷息
			 */
			if (caused != null) {
				if (caused instanceof BaseException) {
					BaseException causedException = (BaseException) caused;
					return causedException.getErrorMsg();
				} else {
					return errorMsg;
				}
			}

			return errorMsg;
		}

		/**
		 * 锟斤拷锟斤拷锟届常锟侥达拷锟斤拷锟斤拷息
		 * 
		 * @param errorMsg
		 *            the errorMsg to set
		 */
		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}

		/**
		 * 锟斤拷酶锟斤拷斐�
		 * 
		 * @return the caused
		 */
		public Throwable getCaused() {
			return caused;
		}

		/**
		 * 锟斤拷锟矫革拷锟届常
		 * 
		 * @param caused
		 *            the caused to set
		 */
		public void setCaused(Throwable caused) {
			this.caused = caused;
		}
	}

