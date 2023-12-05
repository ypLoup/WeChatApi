package com.WeChatApi.controller.base;

	/**
	 * 
	 * �Զ����쳣���࣬�����쳣������̳�?
	 * 
	 */
	public class BaseException extends RuntimeException {

		private static final long serialVersionUID = -7339281910096541147L;

		/**
		 * �쳣������룬ʹ��?4λ�ַ���
		 * ��һλ��������쳣��ϵͳ����? 
		 * ����λ�������Ĵ�����뺬��? 
		 * ��������ɾ���ĳ�������
		 */
		
		/**
		 * δ֪�쳣
		 */
		public static final Integer CODE_UNKNOW=999;
		
		/**
		 * �����쳣
		 */
		public static final Integer CODE_NET=900;
		
		/**
		 * �����쳣
		 */
		public static final Integer CODE_INSERT=901;
		/**
		 * �����쳣
		 */
		public static final Integer CODE_UPDATE=902;
		/**
		 * ɾ���쳣
		 */
		public static final Integer CODE_DELETE=903;
		
		/**
		 * ��ѯ�쳣
		 */
		public static final Integer CODE_SELECT=904;
		
		/**
		 * ��������
		 */
		public static final Integer CODE_PARAM=800;
		
		/**
		 * ҵ���쳣
		 */
		public static final Integer CODE_BIZ=700;
		
		/**
		 * �����쳣
		 */
		public static final Integer CODE_BIZ_LOGIN=704;
		
		/**
		 * �����Ų�����
		 */
		public static final Integer ORDER_NO_UNEXIST = 600;

		/**
		 * �������Ѵ���
		 */
		public static final Integer ORDER_EXIST = 601;
		
		/**
		 * ������ȯ����Ϊ��
		 */
		public static final Integer MEAL_COUPONS_NULL = 501;
		
		/**
		 * �ŵ�ID����Ϊ��
		 */
		public static final Integer SHOP_ID_NULL = 502;
		
		/**
		 * �ŵ�������Ϊ��
		 */
		public static final Integer SHOP_NAME_NULL = 503;
		
		/**
		 * �Ź���Ų���Ϊ��?
		 */
		public static final Integer BATCH_NO_NULL = 504;
		
		/**
		 * �Ź��ܽ���Ϊ��?
		 */
		public static final Integer AMOUNT_NULL = 505;
		
		/**
		 * pay_mode����Ϊ��
		 */
		public static final Integer PAY_MODE_NULL = 506;
		
		/**
		 * ֧����ʽ����Ϊ��
		 */
		public static final Integer PAYMENT_NULL = 507;
		
		/**
		 * ������������Ϊ��
		 */
		public static final Integer QTY_NULL = 508;
		
		/**
		 * �����˲���Ϊ��
		 */
		public static final Integer CREATED_BY_NULL = 509;
		
		protected Integer errorCode;

		/** �쳣������Ϣ����ʵ���׳��쳣���ඨ�� */
		protected String errorMsg;

		/** ���쳣�������쳣�� */
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
		 * ����쳣�Ĵ������
		 * 
		 * @return the errorCode
		 */
		public Integer getErrorCode() {
			/** ����쳣�����˴������ */
			if (errorCode != null ) {
				return errorCode;
			}

			/**
			 * ���û�ж���������?,���Ҹ��쳣��һ�������? �򷵻ظ��쳣�Ĵ������?
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
		 * �����쳣�Ĵ������?
		 * 
		 * @param errorCode
		 *            the errorCode to set
		 */
		public void setErrorCode(Integer errorCode) {
			this.errorCode = errorCode;
		}

		/**
		 * ����쳣�Ĵ������?
		 * 
		 * @return the errorMsg
		 */
		public String getErrorMsg() {
			/** ����쳣�����˴������? */
			if (errorMsg != null && !"".equals(errorMsg))
				return errorMsg;

			/**
			 * ���û�ж��������Ϣ,���Ҹ��쳣��һ�������? �򷵻ظ��쳣�Ĵ�����Ϣ
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
		 * �����쳣�Ĵ�����Ϣ
		 * 
		 * @param errorMsg
		 *            the errorMsg to set
		 */
		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}

		/**
		 * ��ø���?
		 * 
		 * @return the caused
		 */
		public Throwable getCaused() {
			return caused;
		}

		/**
		 * ���ø��쳣
		 * 
		 * @param caused
		 *            the caused to set
		 */
		public void setCaused(Throwable caused) {
			this.caused = caused;
		}
	}

