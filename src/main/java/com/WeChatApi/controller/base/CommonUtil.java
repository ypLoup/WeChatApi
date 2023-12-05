package com.WeChatApi.controller.base;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;



public class CommonUtil {
	
	@Autowired
	private static ResourceBundle res = ResourceBundle.getBundle("rccApi");
	
	private static String mchId= res.getString("wechat_mch_id");
	private static String AppSecret= res.getString("wechat_AppSecret");
	private static String Appid= res.getString("wechat_appid");
	private static String mchName= res.getString("wechat_mch_name");
	
	/**
     * ��ȡ��ǰʱ�� yyyyMMddHHmmss
     * @return String
     */
    public static String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = outFormat.format(now);
        return s;
    }
    
    
    /**
     * ȡ��һ��ָ�����ȴ�С�����������.
     *
     * @param length int �趨��ȡ��������ĳ��ȡ�lengthС��11
     * @return int �������ɵ��������
     */
    public static int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }
    
    
    /**
     * ��ȡ32λ����ַ���
     *
     * @return
     */
    public static String getNonceStr() {
        Random random = new Random();
        return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "UTF-8");
    }
    
    /**
     * ΢��֧��signǩ��
     * @param characterEncoding
     * @param parameters
     * @return
     */
    public static String createSign(String characterEncoding, SortedMap<Object, Object> parameters,String api_key) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<Object, Object>> es = parameters.entrySet();
        Iterator<Map.Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            /** �������Ϊkey����sign���򲻲������ǩ�� */
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        System.out.println("��������֧����Կapi_key="+api_key);
        /** ֧����Կ���������ܣ������ַ�������� */
        sb.append("key=" + api_key);
        /** �ǵ����һ��Ҫת��Ϊ��д */
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }
    
    
    public static String createSign_redpack(String characterEncoding, SortedMap<Object, Object> parameters,String api_key) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<Object, Object>> es = parameters.entrySet();
        Iterator<Map.Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            /** �������Ϊkey����sign���򲻲������ǩ�� */
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        System.out.println("��������֧����Կapi_key="+api_key);
        /** ֧����Կ���������ܣ������ַ�������� */
        sb.append("key=" + api_key);
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding);
        return sign;
    }
    
    
    /**
     * ���������ת��Ϊxml��ʽ��string
     * @param parameters
     * @return
     */
    public static String getRequestXml(SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set<Map.Entry<Object, Object>> es = parameters.entrySet();
        Iterator<Map.Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
            String k = (String) entry.getKey();
            String v = entry.getValue() + "";
            if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {
                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
            } else {
                sb.append("<" + k + ">" + v + "</" + k + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }
    
    /**
     * ��ȡϵͳǰһ�����Ϣ��amount=-1��,��ǰʱ�䣨amount=0������һ�죨amount=1��
     * @param date
     * @return
     */
    public static String getPreDay(Date date,Integer amount) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        date = calendar.getTime();
        String value = sdf.format(date);
        return value.split(" ")[0];
    }
    
    
    public static Map<String, Object> generateSignature(Map<String, String> map) {
        
        //ʵ�������ض���
        Map<String, Object>resultMap=new HashMap<String, Object>();

        //��ò���(΢��ͳһ�µ��ӿ����ɵ�prepay_id )
        String prepayId = map.get("prepay_id").toString();
        //���� ʱ���
        String timeStamp = Long.valueOf(System.currentTimeMillis()).toString();
        //���� �����
        String nonceStr = CommonUtil.getNonceStr();
        //���� MD5
        String signType = "MD5";

        //����hashmap(�û����ǩ��)
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        //����(С����ID)(���һ��Ҫ�Ǵ�д)
        parameters.put("appId", Appid);
        //����(ʱ���)
        parameters.put("timeStamp", timeStamp);
        //����(�����)
        parameters.put("nonceStr", nonceStr);
        //����(���ݰ�)
        parameters.put("package", "prepay_id="+prepayId);
        //����(ǩ����ʽ)
        parameters.put("signType", signType);
        String sign = CommonUtil.createSign("UTF-8", parameters,"jgkj2020jgkj2020jgkj2020jgkj2020");

      

        if(StringUtils.isNotBlank(sign)){
            //����ǩ����Ϣ
        	resultMap.put("paysin", sign);
            //���������(�����������´�����)
        	resultMap.put("nonceStr", nonceStr);
            //����ʱ���
        	resultMap.put("timeStamp", timeStamp);
            //�������ݰ�
        	resultMap.put("package", "prepay_id="+prepayId);

            //logger.info("΢�� ֧���ӿ�����ǩ�� ���÷���ֵ");
        }
        //logger.info("΢�� ֧���ӿ�����ǩ�� ��������");
        return resultMap;
    }
    
    
    
public static Map<String, Object> generateSignature_redpack(Map<String, String> map) throws UnsupportedEncodingException {
        
        //ʵ�������ض���
        Map<String, Object>resultMap=new HashMap<String, Object>();

        //��ò���(΢��ͳһ�µ��ӿ����ɵ�prepay_id )
        String prepayId = map.get("package").toString();
        //���� ʱ���
        String timeStamp = Long.valueOf(System.currentTimeMillis()).toString();
        //���� �����
        String nonceStr = CommonUtil.getNonceStr();
        //���� MD5
        String signType = "MD5";

        //����hashmap(�û����ǩ��)
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        //����(С����ID)(���һ��Ҫ�Ǵ�д)
        parameters.put("appId", Appid);
        //����(ʱ���)
        parameters.put("timeStamp", timeStamp);
        //����(�����)
        parameters.put("nonceStr", nonceStr);
        ;
        //����(���ݰ�)
        parameters.put("package", URLEncoder.encode(prepayId,"UTF-8"));
        //����(ǩ����ʽ)
        //parameters.put("signType", signType);
        String sign = CommonUtil.createSign_redpack("UTF-8", parameters,"jgkj2020jgkj2020jgkj2020jgkj2020");

      

        if(StringUtils.isNotBlank(sign)){
            //����ǩ����Ϣ
        	resultMap.put("paysin", sign);
            //���������(�����������´�����)
        	resultMap.put("nonceStr", nonceStr);
            //����ʱ���
        	resultMap.put("timeStamp", timeStamp);
            //�������ݰ�
        	resultMap.put("package", URLEncoder.encode(prepayId,"UTF-8"));

            //logger.info("΢�� ֧���ӿ�����ǩ�� ���÷���ֵ");
        }
        //logger.info("΢�� ֧���ӿ�����ǩ�� ��������");
        return resultMap;
    }
    
    
    public static Map<String, Object> convertToMap(Object obj) {
		try {
			if (obj instanceof Map) {
				return (Map)obj;
			}
			Map<String, Object> returnMap = BeanUtils.describe(obj);
			returnMap.remove("class");
			return returnMap;
		} catch (IllegalAccessException e1) {
			e1.getMessage();
		} catch (InvocationTargetException e2) {
			e2.getMessage();
		} catch (NoSuchMethodException e3) {
			e3.getMessage();
		}
		return new HashMap();
}

}
