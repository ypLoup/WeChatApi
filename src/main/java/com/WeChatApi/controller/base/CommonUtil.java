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
     * 获取当前时间 yyyyMMddHHmmss
     * @return String
     */
    public static String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = outFormat.format(now);
        return s;
    }
    
    
    /**
     * 取出一个指定长度大小的随机正整数.
     *
     * @param length int 设定所取出随机数的长度。length小于11
     * @return int 返回生成的随机数。
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
     * 获取32位随机字符串
     *
     * @return
     */
    public static String getNonceStr() {
        Random random = new Random();
        return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "UTF-8");
    }
    
    /**
     * 微信支付sign签名
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
            /** 如果参数为key或者sign，则不参与加密签名 */
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        System.out.println("传过来的支付密钥api_key="+api_key);
        /** 支付密钥必须参与加密，放在字符串最后面 */
        sb.append("key=" + api_key);
        /** 记得最后一定要转换为大写 */
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
            /** 如果参数为key或者sign，则不参与加密签名 */
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        System.out.println("传过来的支付密钥api_key="+api_key);
        /** 支付密钥必须参与加密，放在字符串最后面 */
        sb.append("key=" + api_key);
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding);
        return sign;
    }
    
    
    /**
     * 将请求参数转换为xml格式的string
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
     * 获取系统前一天的信息（amount=-1）,当前时间（amount=0），后一天（amount=1）
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
        
        //实例化返回对象
        Map<String, Object>resultMap=new HashMap<String, Object>();

        //获得参数(微信统一下单接口生成的prepay_id )
        String prepayId = map.get("prepay_id").toString();
        //创建 时间戳
        String timeStamp = Long.valueOf(System.currentTimeMillis()).toString();
        //创建 随机串
        String nonceStr = CommonUtil.getNonceStr();
        //创建 MD5
        String signType = "MD5";

        //创建hashmap(用户获得签名)
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        //设置(小程序ID)(这块一定要是大写)
        parameters.put("appId", Appid);
        //设置(时间戳)
        parameters.put("timeStamp", timeStamp);
        //设置(随机串)
        parameters.put("nonceStr", nonceStr);
        //设置(数据包)
        parameters.put("package", "prepay_id="+prepayId);
        //设置(签名方式)
        parameters.put("signType", signType);
        String sign = CommonUtil.createSign("UTF-8", parameters,"jgkj2020jgkj2020jgkj2020jgkj2020");

      

        if(StringUtils.isNotBlank(sign)){
            //返回签名信息
        	resultMap.put("paysin", sign);
            //返回随机串(这个随机串是新创建的)
        	resultMap.put("nonceStr", nonceStr);
            //返回时间戳
        	resultMap.put("timeStamp", timeStamp);
            //返回数据包
        	resultMap.put("package", "prepay_id="+prepayId);

            //logger.info("微信 支付接口生成签名 设置返回值");
        }
        //logger.info("微信 支付接口生成签名 方法结束");
        return resultMap;
    }
    
    
    
public static Map<String, Object> generateSignature_redpack(Map<String, String> map) throws UnsupportedEncodingException {
        
        //实例化返回对象
        Map<String, Object>resultMap=new HashMap<String, Object>();

        //获得参数(微信统一下单接口生成的prepay_id )
        String prepayId = map.get("package").toString();
        //创建 时间戳
        String timeStamp = Long.valueOf(System.currentTimeMillis()).toString();
        //创建 随机串
        String nonceStr = CommonUtil.getNonceStr();
        //创建 MD5
        String signType = "MD5";

        //创建hashmap(用户获得签名)
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        //设置(小程序ID)(这块一定要是大写)
        parameters.put("appId", Appid);
        //设置(时间戳)
        parameters.put("timeStamp", timeStamp);
        //设置(随机串)
        parameters.put("nonceStr", nonceStr);
        ;
        //设置(数据包)
        parameters.put("package", URLEncoder.encode(prepayId,"UTF-8"));
        //设置(签名方式)
        //parameters.put("signType", signType);
        String sign = CommonUtil.createSign_redpack("UTF-8", parameters,"jgkj2020jgkj2020jgkj2020jgkj2020");

      

        if(StringUtils.isNotBlank(sign)){
            //返回签名信息
        	resultMap.put("paysin", sign);
            //返回随机串(这个随机串是新创建的)
        	resultMap.put("nonceStr", nonceStr);
            //返回时间戳
        	resultMap.put("timeStamp", timeStamp);
            //返回数据包
        	resultMap.put("package", URLEncoder.encode(prepayId,"UTF-8"));

            //logger.info("微信 支付接口生成签名 设置返回值");
        }
        //logger.info("微信 支付接口生成签名 方法结束");
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
