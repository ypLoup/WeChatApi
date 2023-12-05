package com.WeChatApi.controller.base;

import org.apache.commons.lang.StringUtils;

/**
 * @description:
 * @author: youcong
 */
public class SqlUtil {

    /**
     * ���峣�õ� sql�ؼ���
     */
    public static String SQL_REGEX = "select |insert |delete |update |drop |count |exec |chr |mid |master |truncate |char |and |declare ";

    /**
     * ��֧����ĸ�����֡��»��ߡ��ո񡢶��š�С���㣨֧�ֶ���ֶ�����
     */
    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    /**
     * ����ַ�����ֹע���ƹ�
     */
    public static String escapeOrderBySql(String value) {
        if (StringUtils.isNotBlank(value) && !isValidOrderBySql(value)) {
        	throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"��������");
        }
        return value;
    }

    /**
     * ��֤ order by �﷨�Ƿ���Ϲ淶
     */
    public static boolean isValidOrderBySql(String value) {
        return value.matches(SQL_PATTERN);
    }

    /**
     * SQL�ؼ��ּ��
     */
    public static void filterKeyword(String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        value = value.toLowerCase();
        String[] sqlKeywords = StringUtils.split(SQL_REGEX, "\\|");
        for (String sqlKeyword : sqlKeywords) {
            if (StringUtils.indexOfIgnoreCase(value, sqlKeyword) > -1) {
            	throw new BaseServiceException(
    					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
    					"��������");
            }
        }
    }

}