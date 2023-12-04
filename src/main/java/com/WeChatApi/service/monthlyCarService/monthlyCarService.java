package com.WeChatApi.service.monthlyCarService;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.CommonUtil;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.invoiceMapper;
import com.WeChatApi.dao.monthlyCarMapper;
import com.WeChatApi.dao.userInfoMapper;
import com.WeChatApi.dao.wechatUserMapper;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;

import com.WeChatApi.bean.condition.userInfoCondition;
import com.WeChatApi.bean.dto.doInvoiceDto;
import com.WeChatApi.bean.dto.operationSubscriptionRecordDto;
import com.WeChatApi.bean.dto.prePayDto2;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.parkinglotsPayRefund;
import com.WeChatApi.bean.models.redPack;
import com.WeChatApi.bean.models.userInfo;
import com.WeChatApi.bean.models.wechatUser;

import sun.misc.BASE64Decoder;

@Service
@Transactional
public class monthlyCarService {
	
	@Autowired
	private invoiceMapper invoicemapper;
	
	@Autowired
	private monthlyCarMapper monthlycarmapper;

	private static Logger log = Logger.getLogger(redPack.class.getName());

	@Autowired
	private wechatUserMapper wechatUsermapper;



	public List<Map<String, String>> findSubscriptionTypeByPlate(Map<String, String> findTypeMap) {
		List<Map<String, String>> list = new ArrayList<>();
		if(StringUtils.isBlank(findTypeMap.get("userOpenId").toString())){
			throw new BaseException(StatusCode.MISSING_OPENID_ERROR.getCode(),"小程序userOpenId不能为空！");
		}
		
		if(StringUtils.isBlank(findTypeMap.get("plate").toString())){
			throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"车牌号不能为空！");
		}
		String plate = findTypeMap.get("plate").toString();
		List<String> plkCodeList =  new ArrayList<>();
		plkCodeList = monthlycarmapper.findSubscriptionByPlate(plate);
		if(plkCodeList.size()==0){
			throw new BaseException(StatusCode.DATA_NOT_EXISTS.getCode(),"车辆无包月记录，无法续包！");
		}
		
		return monthlycarmapper.findSubscriptionTypeByPlkCodeList(plkCodeList);
	}







	public Map<String, Object> monthlyCarPayBywechat(Map<String, String> wechatPayMap, HttpServletRequest request) throws ParseException, IOException {
		
		String userOpenId=wechatPayMap.get("userOpenId").toString();
		
		String pklCode = wechatPayMap.get("pklCode").toString();
		
		String plate = wechatPayMap.get("plate").toString();
		
		String amount=wechatPayMap.get("amount").toString();
		
		String days=wechatPayMap.get("days").toString();
		
		if(StringUtils.isBlank(userOpenId)){
			throw new BaseException(StatusCode.MISSING_OPENID_ERROR.getCode(),"小程序userOpenId不能为空！");
		}
		
		if(StringUtils.isBlank(pklCode)){
			throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"停车场编号不能为空！");
		}
		
		
		if(StringUtils.isBlank(plate)){
			throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"车牌号不能为空！");
		}
		
		if(StringUtils.isBlank(amount)){
			throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"包期金额不能为空！");
		}
		
		if(StringUtils.isBlank(days)){
			throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"包期天数不能为空！");
		}
		
		Map<String, Object> monthlyCarMap = new HashMap<>();
		monthlyCarMap=monthlycarmapper.findMonthlyCarByPlate(pklCode,plate);
		if(monthlyCarMap==null){
			throw new BaseException(StatusCode.DATA_NOT_EXISTS.getCode(),"车辆无包月记录，无法续包！");
		}
		LocalDate localDate = LocalDate.now();
		/*System.out.println("localDate: " + localDate);
		 LocalDateTime nowTime= LocalDateTime.now();
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 Date date1 = formatter.parse(monthlyCarMap.get("sub_expire_time").toString());
		 Date date2 = formatter.parse(nowTime.toLocalDate().toString());
		 System.out.println(date1+"========="+date1);*/
		 LocalDate beginDateTime = LocalDate.parse(monthlyCarMap.get("sub_expire_time").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		 operationSubscriptionRecordDto dto = new operationSubscriptionRecordDto();
		 if(beginDateTime.compareTo(localDate)>0){//date1>date2
			 LocalDate Sub_effective_time=beginDateTime.plusDays(Long.valueOf(1));
			 dto.setSub_effective_time(Sub_effective_time.toString()+" 00:00:00");
		 }else if (beginDateTime.compareTo(localDate) < 0) {//date1<date2
			 dto.setSub_effective_time(localDate.toString()+" 00:00:00");
		 }else if (beginDateTime.compareTo(localDate) == 0) {//date1=date2
			 dto.setSub_effective_time(localDate.toString()+" 00:00:00");
		 }
		 dto.setPkl_code(pklCode);
		 dto.setPu_id(1);
		 dto.setR_sub_id(Integer.valueOf(monthlyCarMap.get("sub_id").toString()));
		 dto.setR_type(2);
		 dto.setSub_amount(Integer.valueOf(amount));
		 dto.setDays(days);
		 dto.setRecord_status(2);
		 dto.setUserOpenId(userOpenId);
		 dto.setPlate(plate);
		 String currTime = CommonUtil.getCurrTime();
		 String strRandom = CommonUtil.buildRandom(4) + "";
		 dto.setOutTradeNo(currTime+strRandom);
		 monthlycarmapper.insertSubscriptionRecord(dto);
		 return getPrepayIdMonthlyCar(dto,request);
	}
	
	
public Map<String, Object> getPrepayIdMonthlyCar(operationSubscriptionRecordDto dto, HttpServletRequest request) throws IOException {
		
		try{

            //公众号的appid
            String appid = "wx606d7fcc6d1402c9";
            String openid = dto.getUserOpenId();
            //开始发送红包
            //logger.info("++++++++++++++开始发送红包++++++++++++++++++");
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            /** 当前时间 yyyyMMddHHmmss */
            String currTime = CommonUtil.getCurrTime();
            /** 8位日期 */
            String strTime = currTime.substring(8, currTime.length());
            /** 四位随机数 */
            String strRandom = CommonUtil.buildRandom(4) + "";
            //商户订单号
            //parameters.put("mch_billno",strTime + strRandom);
            
            
            /** 公众号APPID */
            parameters.put("appid", appid);
            parameters.put("body", dto.getPlate()+"包月缴费"+dto.getDays());
            /** 商户号 */
            String mch_id = "1604629952";
            parameters.put("mch_id", mch_id);
            /** 随机字符串 */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/monthlyCar/payCallback");
            parameters.put("out_trade_no", dto.getOutTradeNo());
            parameters.put("openid", openid);
            parameters.put("spbill_create_ip", request.getRemoteAddr());
            parameters.put("total_fee", dto.getSub_amount());
            parameters.put("trade_type", "JSAPI");
            parameters.put("attach", dto.getR_id());//优惠券recordId

            
            
            /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);
            String requestJsonStr = new ObjectMapper().writeValueAsString(parameters);
            //logger.info("发送的信息是"+requestJsonStr);
            parameters.put("sign", sign);//
            /** 生成xml结构的数据，用于统一下单接口的请求 */
            String requestXML = CommonUtil.getRequestXml(parameters);
            /**
             * 读取证书
             * 
             */
            CloseableHttpClient httpclient = null;
            Map<String,String> result = new HashMap<String,String>();
           try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                String pathname = "/tomcat/apiclient_cert.p12";//这里填你们的证书的地址，我这里放在linux服务器的/usr下面
                //String pathname="D:\\home\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //此处为证书所放的绝对路径
                try {
                    keyStore.load(instream, mch_id.toCharArray());
                } finally {
                    instream.close();
                }
                // Trust own CA and all self-signed certs
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mch_id.toCharArray())
                        .build();
                // Allow TLSv1 protocol only
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        sslcontext,
                        new String[]{"TLSv1"},
                        null,
                        SSLConnectionSocketFactory.getDefaultHostnameVerifier());//SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER
                httpclient = HttpClients.custom()
                        .setSSLSocketFactory(sslsf)
                        .build();
            }
            catch (Exception e){
            	log.info("读取证书信息的时候发生异常异常信息是："+e.getMessage());
                e.printStackTrace();
            }
            try {
                String requestUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
                HttpPost httpPost = new HttpPost(requestUrl);
                StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
                // 设置类型
                reqEntity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(reqEntity);
                log.info("executing request" + httpPost.getRequestLine());
                CloseableHttpResponse response = httpclient.execute(httpPost);
                try {
                    HttpEntity entity = response.getEntity();
                    System.out.println(response.getStatusLine());
                    if (entity != null) {
                        // 从request中取得输入流
                        InputStream inputStream = entity.getContent();
                        // 读取输入流
                        SAXReader reader = new SAXReader();
                        Document document = reader.read(inputStream);
                        // 得到xml根元素
                        Element root = document.getRootElement();
                        // 得到根元素的所有子节点
                        List<Element> elementList = root.elements();
                        // 遍历所有子节点
                        for (Element e : elementList)
                        {
                            result.put(e.getName(), e.getText());
                        }
                        // 释放资源
                        inputStream.close();
                    }
                    EntityUtils.consume(entity);
                }
                finally {
                    if(response!=null) {
                        response.close();
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    httpclient.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            
            log.info("发获取PrepayId："+new ObjectMapper().writeValueAsString(result));
            String refundjson=new ObjectMapper().writeValueAsString(dto);//关键
            
            if(result.get("result_code").equals("SUCCESS")) {
            	Map<String, Object> map =CommonUtil.generateSignature(result);
            	wechatUsermapper.insertApiLogs("统一下单接口_车辆自主包月", refundjson, "success", new ObjectMapper().writeValueAsString(result));
            	
            	map.put("price", dto.getSub_amount());
                return map;
                }
            else {
            	log.info("获取PrepayId="+openid+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
            	wechatUsermapper.insertApiLogs("统一下单接口_车辆自主包月", refundjson, "fail", new ObjectMapper().writeValueAsString(result));
            	throw new BaseServiceException(
    					StatusCode.SYSTEM_ERROR.getCode(),
    					"统一下单接口获取失败，请重新联系管理员！"+"["+result.get("err_code_des")+"]");
            }
        }
        catch (Exception e){
        	log.info("统一下单接口_车辆自主包月异常，异常信息是："+e.getMessage());
        	throw new BaseServiceException(
					StatusCode.SYSTEM_ERROR.getCode(),
					e.getMessage());
        }
	
	
	}







public void callBackNotify(String rId) throws IOException {
	PostMethod postMethod = null;
	try{

    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
    String unlicensedCarOutUrl = res.getString("modifySubscription_url");
	
    postMethod = new PostMethod(unlicensedCarOutUrl) ;
    /*int newCouponFee=Integer.valueOf(couponFee)*100;
    String totalFee=String.valueOf(newCouponFee);*/
    NameValuePair[] Senddata = {
            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
            new NameValuePair("sub_r_id",rId)
            
            
    };
    
    
    postMethod.setRequestBody(Senddata);
    postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;

   
    HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
    int response = client.executeMethod(postMethod); // POST
    
    if(response==200){
    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
    	log.info("++++++++++++++++++包期车续包++++++++++++++++++++++"+dataJson);
        String errorCode = dataJson.getString("error_code");
        if(errorCode.equals("0")){
        	log.info("sub_r_id"+rId+"回调成功！");
        	wechatUsermapper.insertApiLogs("包期车续包接口", "sub_r_id:"+rId, "success", "");

        	
        }else{
        	log.info("sub_r_id"+rId+"回调失败！");
        	wechatUsermapper.insertApiLogs("包期车续包接口", "sub_r_id:"+rId, "fail", dataJson.getString("error_msg"));
        	throw new BaseServiceException(
        			Integer.parseInt(errorCode),
        			dataJson.getString("error_msg"));
        }
       
        
    }else{
    	throw new BaseServiceException(
				StatusCode.API_FREQUENTLY_ERROR.getCode(),
				"请查看包期车续包接口");
    }
	}finally {
		postMethod.releaseConnection();
	}
	
}







public void updateOperationSubscriptionRecordByRid(String rId) {
	
	monthlycarmapper.updateOperationSubscriptionRecordByRid(rId);
}







public List<Map<String, String>> findSubscriptionRecordByUserOpenId(Map<String, String> findRecordMap) {
	
	if(StringUtils.isBlank(findRecordMap.get("userOpenId").toString())&&StringUtils.isBlank(findRecordMap.get("userOpenIdZfb").toString())){
		throw new BaseException(StatusCode.MISSING_OPENID_ERROR.getCode(),"openId不能为空！");
	}
	
	return monthlycarmapper.findSubscriptionRecordByUserOpenId(findRecordMap.get("userOpenId").toString(),findRecordMap.get("userOpenIdZfb").toString());
}







public Map<String, Object> monthlyCarPayBywechat_new(Map<String, String> wechatPayMap, HttpServletRequest request) throws IOException {
	String userOpenId=wechatPayMap.get("userOpenId").toString();
	
	String pklCode = wechatPayMap.get("pklCode").toString();
	
	String plate = wechatPayMap.get("plate").toString();
	
	String amount=wechatPayMap.get("amount").toString();
	
	String subEffectiveTime=wechatPayMap.get("subEffectiveTime").toString();
	String subExpireTime=wechatPayMap.get("subExpireTime").toString();
	
	if(StringUtils.isBlank(userOpenId)){
		throw new BaseException(StatusCode.MISSING_OPENID_ERROR.getCode(),"小程序userOpenId不能为空！");
	}
	
	if(StringUtils.isBlank(pklCode)){
		throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"停车场编号不能为空！");
	}
	
	
	if(StringUtils.isBlank(plate)){
		throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"车牌号不能为空！");
	}
	
	if(StringUtils.isBlank(amount)){
		throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"包期金额不能为空！");
	}
	
	if(StringUtils.isBlank(subEffectiveTime)){
		throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"包期起始时间不能为空！");
	}
	
	if(StringUtils.isBlank(subExpireTime)){
		throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"包期结束时间不能为空！");
	}
	
	Map<String, Object> monthlyCarMap = new HashMap<>();
	monthlyCarMap=monthlycarmapper.findMonthlyCarByPlate(pklCode,plate);
	if(monthlyCarMap==null){
		throw new BaseException(StatusCode.DATA_NOT_EXISTS.getCode(),"车辆无包月记录，无法续包！");
	}
	String subSelfHelp = monthlyCarMap.get("sub_self_help").toString();
	if(subSelfHelp.equals("0")){
		throw new BaseException(StatusCode.DATA_NOT_EXISTS.getCode(),"自助续包未开启！");
	}
	//LocalDate localDate = LocalDate.now();
	 LocalDate beginDateTime = LocalDate.parse(monthlyCarMap.get("sub_expire_time").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	 LocalDate startTime = LocalDate.parse(subEffectiveTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	 operationSubscriptionRecordDto dto = new operationSubscriptionRecordDto();
	 if(beginDateTime.compareTo(startTime)>0){//date1>date2
		 LocalDate Sub_effective_time=beginDateTime.plusDays(Long.valueOf(1));
		 dto.setSub_effective_time(Sub_effective_time.toString());
	 }else if (beginDateTime.compareTo(startTime) < 0) {//date1<date2
		 dto.setSub_effective_time(startTime.toString());
	 }else if (beginDateTime.compareTo(startTime) == 0) {//date1=date2
		 dto.setSub_effective_time(startTime.toString());
	 }
	 dto.setPkl_code(pklCode);
	 dto.setPu_id(1);//daiti  pay_method
	 dto.setR_sub_id(Integer.valueOf(monthlyCarMap.get("sub_id").toString()));
	 dto.setR_type(2);
	 dto.setSub_amount(Integer.valueOf(amount));
	 dto.setSub_expire_time(subExpireTime);
	 //dto.setDays(days);
	 dto.setRecord_status(2);
	 dto.setUserOpenId(userOpenId);
	 dto.setPlate(plate);
	 String currTime = CommonUtil.getCurrTime();
	 String strRandom = CommonUtil.buildRandom(4) + "";
	 dto.setOutTradeNo(currTime+strRandom);
	 monthlycarmapper.insertSubscriptionRecord_new(dto);
	 return getPrepayIdMonthlyCar(dto,request);
}







public List<Map<String, String>> findMonthlyPklRecord(Map<String, String> findRecordMap) {
	if(StringUtils.isBlank(findRecordMap.get("userOpenId").toString())&&StringUtils.isBlank(findRecordMap.get("userOpenIdZfb").toString())){
		throw new BaseException(StatusCode.MISSING_OPENID_ERROR.getCode(),"openId不能为空！");
	}
	if(findRecordMap.containsKey("origin")){
		return monthlycarmapper.findMonthlyPklRecord_origin(findRecordMap.get("r_sub_id").toString(),findRecordMap.get("origin").toString());
	}else {
		return monthlycarmapper.findMonthlyPklRecord(findRecordMap.get("r_sub_id").toString());
	}
	
	
	
}







public Map<String, String> monthlyCarPayByalipay_new(Map<String, String> wechatPayMap, HttpServletRequest request) {
String userOpenId=wechatPayMap.get("userOpenId").toString();
	
	String pklCode = wechatPayMap.get("pklCode").toString();
	
	String plate = wechatPayMap.get("plate").toString();
	
	String amount=wechatPayMap.get("amount").toString();
	
	String subEffectiveTime=wechatPayMap.get("subEffectiveTime").toString();
	String subExpireTime=wechatPayMap.get("subExpireTime").toString();
	
	if(StringUtils.isBlank(userOpenId)){
		throw new BaseException(StatusCode.MISSING_OPENID_ERROR.getCode(),"小程序userOpenId不能为空！");
	}
	
	if(StringUtils.isBlank(pklCode)){
		throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"停车场编号不能为空！");
	}
	
	
	if(StringUtils.isBlank(plate)){
		throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"车牌号不能为空！");
	}
	
	if(StringUtils.isBlank(amount)){
		throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"包期金额不能为空！");
	}
	
	if(StringUtils.isBlank(subEffectiveTime)){
		throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"包期起始时间不能为空！");
	}
	
	if(StringUtils.isBlank(subExpireTime)){
		throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"包期结束时间不能为空！");
	}
	
	Map<String, Object> monthlyCarMap = new HashMap<>();
	monthlyCarMap=monthlycarmapper.findMonthlyCarByPlate(pklCode,plate);
	if(monthlyCarMap==null){
		throw new BaseException(StatusCode.DATA_NOT_EXISTS.getCode(),"车辆无包月记录，无法续包！");
	}
	String subSelfHelp = monthlyCarMap.get("sub_self_help").toString();
	if(subSelfHelp.equals("0")){
		throw new BaseException(StatusCode.DATA_NOT_EXISTS.getCode(),"自助续包未开启！");
	}
	
	//LocalDate localDate = LocalDate.now();
	 LocalDate beginDateTime = LocalDate.parse(monthlyCarMap.get("sub_expire_time").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	 LocalDate startTime = LocalDate.parse(subEffectiveTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	 operationSubscriptionRecordDto dto = new operationSubscriptionRecordDto();
	 if(beginDateTime.compareTo(startTime)>0){//date1>date2
		 LocalDate Sub_effective_time=beginDateTime.plusDays(Long.valueOf(1));
		 dto.setSub_effective_time(Sub_effective_time.toString());
	 }else if (beginDateTime.compareTo(startTime) < 0) {//date1<date2
		 dto.setSub_effective_time(startTime.toString());
	 }else if (beginDateTime.compareTo(startTime) == 0) {//date1=date2
		 dto.setSub_effective_time(startTime.toString());
	 }
	 dto.setPkl_code(pklCode);
	 dto.setPu_id(2); //daiti  pay_method
	 dto.setR_sub_id(Integer.valueOf(monthlyCarMap.get("sub_id").toString()));
	 dto.setR_type(2);
	 dto.setSub_amount(Integer.valueOf(amount));
	 dto.setSub_expire_time(subExpireTime);
	 //dto.setDays(days);
	 dto.setRecord_status(2);
	 dto.setUserOpenId(userOpenId);
	 dto.setPlate(plate);
	 String currTime = CommonUtil.getCurrTime();
	 String strRandom = CommonUtil.buildRandom(4) + "";
	 dto.setOutTradeNo(currTime+strRandom);
	 monthlycarmapper.insertSubscriptionRecord_new(dto);
	 Map<String, String>dateMap= new HashMap<>();
	 dateMap.put("alipayReturn", getPrepayIdMonthlyCar_alipay(dto,request));
	 return dateMap;
}







private String getPrepayIdMonthlyCar_alipay(operationSubscriptionRecordDto dto,
		HttpServletRequest request) {
	//获得初始化的AlipayClient
			AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
	        //设置请求参数
	        AlipayTradeCreateRequest request2 = new AlipayTradeCreateRequest();
	        
	        JSONObject json=new JSONObject();
	        //订单号
	        json.put("out_trade_no",dto.getOutTradeNo());
	        //金额 这里的金额是以元为单位的可以不转换但必须是字符串
	        json.put("total_amount",dto.getSub_amount()/100.0f);
	        //描述
	        json.put("subject","停车场["+dto.getPkl_code()+"]包月");
	        //用户唯一标识id 这里必须使用buyer_id 参考文档
	        json.put("buyer_id",dto.getUserOpenId());
	        
	        json.put("passback_params", dto.getR_id());
	        //对象转化为json字符串
	        String jsonStr=json.toString();
	        //商户通过该接口进行交易的创建下单
	        request2.setBizContent(jsonStr);
	        //回调地址 是能够访问到的域名加上方法名
	        request2.setNotifyUrl("https://jiashan.iparking.tech/mp/WeChatApi/monthlyCar/payCallback_alipay");
	        try {
	        	Map<String, Object>returnMap=new HashMap<String, Object>();
	            AlipayTradeCreateResponse response = alipayClient.execute(request2);
	            if(response.isSuccess()){
	            	log.info(response.getBody());
	            	wechatUsermapper.insertApiLogs("支付宝统一下单包月接口","用户id:"+dto.getUserOpenId()+"订单号:"+dto.getOutTradeNo()+":金额:"+dto.getSub_amount()/100.0f+"支付宝交易号"+response.getTradeNo(), "success", response.getTradeNo());
	            	return response.getTradeNo();
	            }else{
	            	wechatUsermapper.insertApiLogs("支付宝统一下单包月接口","用户id:"+dto.getUserOpenId()+"订单号:"+dto.getOutTradeNo()+":金额:"+dto.getSub_amount()/100.0f, "fail", response.getSubCode()+"_"+response.getSubMsg());
	            	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),response.getSubCode()+"_"
									+ response.getSubMsg()
							);
	            }
	        } catch (AlipayApiException e) {
	            e.printStackTrace();
	        }
	        return null;

}







			public void doInvoice(doInvoiceDto invoiceDto) throws IOException {
				 String userOpenId = invoiceDto.getUserOpenId();
					
					if(StringUtils.isBlank(invoiceDto.getUserOpenId())){
						throw new BaseServiceException(
								StatusCode.MISSING_OPENID_ERROR.getCode(),
								"用户userOpenId不能为空！");
					}
					
					invoice invoice = invoicemapper.findInvoiceInfoByTaxNumber(invoiceDto.getTaxNumber(),invoiceDto.getUserOpenId());
					if(invoice==null){
						/*if(StringUtils.isBlank(invoiceDto.getBankNumber())){
							throw new BaseServiceException(
									StatusCode.PARAMETER_FORMATE_RROR.getCode(),
									"开户行账号不能为空！");
						}*/
						
						if(StringUtils.isBlank(invoiceDto.getCompanyName())){
							throw new BaseServiceException(
									StatusCode.PARAMETER_FORMATE_RROR.getCode(),
									"发票抬头不能为空！");
						}
						/*if(StringUtils.isBlank(invoiceDto.getTaxNumber())){
							throw new BaseServiceException(
									StatusCode.PARAMETER_FORMATE_RROR.getCode(),
									"发票税号不能为空！");
						}*/
						if(StringUtils.isBlank(invoiceDto.getEmail())){
							throw new BaseServiceException(
									StatusCode.PARAMETER_FORMATE_RROR.getCode(),
									"邮箱地址不能为空！");
						}
						/*if(StringUtils.isBlank(invoiceDto.getAddressMobile())){
							throw new BaseServiceException(
									StatusCode.PARAMETER_FORMATE_RROR.getCode(),
									"联系地址电话不能为空！");
						}*/
						invoice invoice2 = new  invoice();
						invoice2.setUserOpenId(userOpenId);
						invoice2.setCompanyName(invoiceDto.getCompanyName());
						invoice2.setTaxNumber(invoiceDto.getTaxNumber());
						invoice2.setBankNumber(invoiceDto.getBankNumber());
						invoice2.setEmail(invoiceDto.getEmail());
						invoice2.setAddressMobile(invoiceDto.getAddressMobile());
						List<String> rIdList = new ArrayList<>();
						for(String outTradeNo : invoiceDto.getrIds().toString().split(",")){
						
							rIdList.add(outTradeNo);
						}
						Map<String, Object> sumPriceMap = new HashMap<>();
						if(rIdList.size()>0){
							if(invoiceDto.getType().equals("1")){
								sumPriceMap = monthlycarmapper.getSumPriceByRIdList(rIdList);
							}else{
								sumPriceMap = monthlycarmapper.getRoadSumPriceByRIdList(rIdList);
							}
							
							sumPriceMap.put("vehPlate", invoiceDto.getVehPlate());
							//String vehiclePates=invoicemapper.getVehiclesByOutTradeNoList(outTradeNoList);
							this.doInvoiceSumPrice_monthlyCar(rIdList,invoice2,sumPriceMap);
						}
						
						
						invoicemapper.addInvoiceInfo(invoice2);
					}else{
						List<String> rIdList = new ArrayList<>();
						for(String outTradeNo : invoiceDto.getrIds().toString().split(",")){
							
							rIdList.add(outTradeNo);
						}
						Map<String, Object> sumPriceMap = new HashMap<>();
						if(rIdList.size()>0){
							if(invoiceDto.getType().equals("1")){
								sumPriceMap = monthlycarmapper.getSumPriceByRIdList(rIdList);
							}else{
								sumPriceMap = monthlycarmapper.getRoadSumPriceByRIdList(rIdList);
							}
							invoice.setCompanyName(invoiceDto.getCompanyName());
							invoice.setEmail(invoiceDto.getEmail());
							sumPriceMap.put("vehPlate", invoiceDto.getVehPlate());
							this.doInvoiceSumPrice_monthlyCar(rIdList,invoice,sumPriceMap);
						}
						
					}
					
					
					
					
				
			}







			private void doInvoiceSumPrice_monthlyCar(List<String> outTradeNoList, invoice invoice2, Map<String, Object> sumPriceMap) throws IOException {
				
				PostMethod postMethod = null;
				try{
				// 设置上传文件目录
			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String doInvoiceUrl = res.getString("doInvoice_url");
		    	
			    postMethod = new PostMethod(doInvoiceUrl) ;

		        NameValuePair[] Senddata = {
			            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
			            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
			            new NameValuePair("invoiceAmount",sumPriceMap.get("price")+""),
			            new NameValuePair("companyName",invoice2.getCompanyName()),
			            new NameValuePair("taxNumber",invoice2.getTaxNumber()),
			            new NameValuePair("addressAndMobile",invoice2.getAddressMobile()),
			            new NameValuePair("bankAndNumber",invoice2.getBankNumber()),
			            new NameValuePair("email",invoice2.getEmail()),
			            new NameValuePair("remark",sumPriceMap.get("vehPlate").toString())
			            
			    };
		        
		        
		        postMethod.setRequestBody(Senddata);
		        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;

		       
		        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
			    int response = client.executeMethod(postMethod); // POST
			    
			    if(response==200){
			    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
		            String errorCode = dataJson.getString("error_code");
		            if(errorCode.equals("0")){
		            String data = dataJson.getString("data");
		            JSONObject  dataJsonR = JSONObject.fromObject(data);
		                
		            	log.info("订单号："+outTradeNoList.toString()+"开票成功");
		            	wechatUsermapper.insertApiLogs("开票接口", "订单号:"+outTradeNoList.toString()+";税号:"+invoice2.getTaxNumber()+";开票金额:"+sumPriceMap, "success", "发票地址:"+dataJsonR.getString("pdfUrl"));
		            if(sumPriceMap.get("type").toString().equals("1")){
		            	monthlycarmapper.updateSubscriptionRecordByList(outTradeNoList,dataJsonR.getString("pdfUrl"),invoice2.getCompanyName(),invoice2.getTaxNumber(),invoice2.getAddressMobile(),invoice2.getBankNumber(),invoice2.getEmail());
		            }else{
		            	monthlycarmapper.updateRoadSubscriptionRecordByList(outTradeNoList,dataJsonR.getString("pdfUrl"),invoice2.getCompanyName(),invoice2.getTaxNumber(),invoice2.getAddressMobile(),invoice2.getBankNumber(),invoice2.getEmail());
		            }
		            	

		            }else{
		            	log.info("开票号："+outTradeNoList.toString()+"开票失败");

		            	wechatUsermapper.insertApiLogs("开票接口", "订单号:"+outTradeNoList.toString()+";税号:"+invoice2.getTaxNumber()+";开票金额:"+sumPriceMap, "fail", dataJson.getString("error_msg"));
		            	throw new BaseServiceException(
		            			Integer.parseInt(errorCode),
		            			dataJson.getString("error_msg"));
		            }
		           
		            
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"请查看开票接口");
			    }
				}finally {
					postMethod.releaseConnection();
				}
				
			}
			
			
			
			public void payRefund(refundDto dto, HttpServletRequest request) throws  IOException {
				
				if(dto.getOrderType().equals("1")){//monthlyCar_inParking
					List<Map<String, Object>>recordList= monthlycarmapper.findSubscriptionRecordByRId(dto.getrId().toString());
					if(recordList.size()==0){
						throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"无场内包期支付记录，请查询！");
					}
					//String refundAmount=  recordList.get(0).get("refund_amount").toString();
					int refund_fee =Integer.valueOf(recordList.get(0).get("refund_amount").toString());
					int sum_refund_fee=refund_fee+dto.getRefundFee();
					if(sum_refund_fee>Integer.valueOf(recordList.get(0).get("sub_amount").toString())){
						throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"退款总金额大于包期金额无需退款");
					}
					log.info("场内包期退款开始========================"+new ObjectMapper().writeValueAsString(dto));
					String result =this.refundByWechat(dto);
					log.info("场内包期退款结束========================"+new ObjectMapper().writeValueAsString(dto)+"结果："+result);
					if(result.equals("SUCCESS")){
						monthlycarmapper.insertMonthlyCarRefund(dto);
					}
					
				}
				
				if(dto.getOrderType().equals("2")){//monthlyCar_outParking
					
					List<Map<String, Object>>recordList= monthlycarmapper.findSubscriptionRecordByRId_road(dto.getrId().toString());
					if(recordList.size()==0){
						throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"无路边包期支付记录，请查询！");
					}
					int refund_fee =Integer.valueOf(recordList.get(0).get("refund_amount").toString());
					int sum_refund_fee=refund_fee+dto.getRefundFee();
					if(sum_refund_fee>Integer.valueOf(recordList.get(0).get("sub_amount").toString())){
						throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"退款总金额大于包期金额无需退款");
					}
					log.info("场外包期退款开始========================"+new ObjectMapper().writeValueAsString(dto));
					String result =this.refundByWechat(dto);
					log.info("场外包期退款结束========================"+new ObjectMapper().writeValueAsString(dto)+"结果："+result);
					if(result.equals("SUCCESS")){
						monthlycarmapper.insertMonthlyCarRefund(dto);
					}
					
				}
				
			}
			
			
			
			private String refundByWechat(refundDto dto) {
		         try{

		            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
		            /** 当前时间 yyyyMMddHHmmss */
		            String currTime = CommonUtil.getCurrTime();
		            /** 8位日期 */
		            String strTime = currTime.substring(8, currTime.length());
		            /** 四位随机数 */
		            String strRandom = CommonUtil.buildRandom(4) + "";
		            //公众号的appid
		            String appid = "wx606d7fcc6d1402c9";
		            /** 公众号APPID */
		            parameters.put("appid", appid);
		            /** 商户号 */
		            String mch_id = "1604629952";
		            parameters.put("mch_id", mch_id);
		            /** 随机字符串 */
		            parameters.put("nonce_str", CommonUtil.getNonceStr());
		            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/monthlyCar/refundCallback_monthlyCar");
		            parameters.put("out_refund_no", dto.getOutRefundNo());
		            parameters.put("out_trade_no", dto.getTransactionId());

		            parameters.put("total_fee", dto.getTotalFee());
		            parameters.put("refund_fee", dto.getRefundFee());
		            parameters.put("refund_desc", dto.getRefundReason());
		            
		            /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
		            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
		            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);
		            String requestJsonStr = new ObjectMapper().writeValueAsString(parameters);

		            parameters.put("sign", sign);//
		            /** 生成xml结构的数据，用于统一下单接口的请求 */
		            String requestXML = CommonUtil.getRequestXml(parameters);
		            /**
		             * 读取证书
		             * 
		             */
		            CloseableHttpClient httpclient = null;
		            Map<String,String> result = new HashMap<String,String>();
		           try {
		                KeyStore keyStore = KeyStore.getInstance("PKCS12");
		                String pathname = "/tomcat/apiclient_cert.p12";//这里填你们的证书的地址，我这里放在linux服务器的/usr下面
		                //String pathname="D:\\home\\apiclient_cert.p12";
		                FileInputStream instream = new FileInputStream(new File(pathname)); //此处为证书所放的绝对路径
		                try {
		                    keyStore.load(instream, mch_id.toCharArray());
		                } finally {
		                    instream.close();
		                }
		                SSLContext sslcontext = SSLContexts.custom()
		                        .loadKeyMaterial(keyStore, mch_id.toCharArray())
		                        .build();
		                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
		                        sslcontext,
		                        new String[]{"TLSv1"},
		                        null,
		                        SSLConnectionSocketFactory.getDefaultHostnameVerifier());//SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER
		                httpclient = HttpClients.custom()
		                        .setSSLSocketFactory(sslsf)
		                        .build();
		            }
		            catch (Exception e){
		            	log.info("读取证书信息的时候发生异常异常信息是："+e.getMessage());
		                e.printStackTrace();
		            }
		            try {
		                String requestUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";
		                HttpPost httpPost = new HttpPost(requestUrl);
		                StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
		                // 设置类型
		                reqEntity.setContentType("application/x-www-form-urlencoded");
		                httpPost.setEntity(reqEntity);
		                log.info("executing request" + httpPost.getRequestLine());
		                CloseableHttpResponse response = httpclient.execute(httpPost);
		                try {
		                    HttpEntity entity = response.getEntity();
		                    System.out.println(response.getStatusLine());
		                    if (entity != null) {
		                        // 从request中取得输入流
		                        InputStream inputStream = entity.getContent();
		                        // 读取输入流
		                        SAXReader reader = new SAXReader();
		                        Document document = reader.read(inputStream);
		                        // 得到xml根元素
		                        Element root = document.getRootElement();
		                        // 得到根元素的所有子节点
		                        List<Element> elementList = root.elements();
		                        // 遍历所有子节点
		                        for (Element e : elementList)
		                        {
		                            result.put(e.getName(), e.getText());
		                        }
		                        // 释放资源
		                        inputStream.close();
		                    }
		                    EntityUtils.consume(entity);
		                }
		                finally {
		                    if(response!=null) {
		                        response.close();
		                    }
		                }
		            }
		            catch (Exception e){
		                e.printStackTrace();
		            }finally {
		                try {
		                    httpclient.close();
		                }
		                catch (Exception e){
		                    e.printStackTrace();
		                }
		            }
		            String refundjson=new ObjectMapper().writeValueAsString(dto);//关键
		            log.info("退款JSON"+new ObjectMapper().writeValueAsString(result));

		            if(result.get("result_code").equals("SUCCESS")) {

		            	wechatUsermapper.insertApiLogs("包期退款接口", refundjson, "success", new ObjectMapper().writeValueAsString(result));

		                return result.get("result_code");
		                }
		            else {
		            	wechatUsermapper.insertApiLogs("包期退款接口", refundjson, "fail", new ObjectMapper().writeValueAsString(result));
		            	throw new BaseServiceException(
		    					StatusCode.SYSTEM_ERROR.getCode(),
		    					"微信退款接口获取失败，请重新联系管理员！"+"["+result.get("err_code_des")+"]");
		            }
		        }
		        catch (Exception e){
		        	log.info("微信退款接口异常，异常信息是："+e.getMessage());
		        	throw new BaseServiceException(
							StatusCode.SYSTEM_ERROR.getCode(),
							e.getMessage());
		        }
			}

			
			public parkinglotsPayRefund findMonthlyCarRefundByOutNo(String outRefundNo) {
				// TODO Auto-generated method stub
				return monthlycarmapper.findMonthlyCarRefundByOutNo(outRefundNo);
			}







			public void updateMonthlyCarRefundByOutNo(String outRefundNo, String refundId) {
				// TODO Auto-generated method stub
				monthlycarmapper.updateMonthlyCarRefundByOutNo(outRefundNo,refundId);
			}







			public List<Map<String, Object>> findMonthlyCarPackage(Map<String, Object> map) {
				
				if(StringUtils.isBlank(map.get("userOpenId").toString())){
					throw new BaseServiceException(
							StatusCode.PARAMETER_FORMATE_RROR.getCode(),
							"小程序id不能为空");
				}
				if(map.get("packageType").toString().equals("1")){// 包期类型：1全包；2单包
					if(StringUtils.isBlank(map.get("aType").toString())){
						throw new BaseServiceException(
								StatusCode.PARAMETER_FORMATE_RROR.getCode(),
								"全包类型不能为空");
					}
				return monthlycarmapper.findMonthlyCarPackage(map);
				}else{
					if(StringUtils.isBlank(map.get("pklType").toString())){
						throw new BaseServiceException(
								StatusCode.PARAMETER_FORMATE_RROR.getCode(),
								"停车场类型不能为空");
					}
					return monthlycarmapper.findMonthlyCarPackage_single(map);
				}
			}







			public List<Map<String, Object>> findMonthlyCaParkinglotAll(Map<String, Object> map) {

				    if(StringUtils.isBlank(map.get("userOpenId").toString())){
						throw new BaseServiceException(
								StatusCode.PARAMETER_FORMATE_RROR.getCode(),
								"小程序id不能为空");
					}
				
					if(StringUtils.isBlank(map.get("aId").toString())){
						throw new BaseServiceException(
								StatusCode.PARAMETER_FORMATE_RROR.getCode(),
								"全包编号不能为空");
					}
					
					return monthlycarmapper.findMonthlyCarPackageParkingLotInfo(map);//套餐内包含停车场详情
			}







			public void monthlyCarAudit(Map<String, Object> map) {				
				if(StringUtils.isBlank(map.get("userOpenId").toString())){
					throw new BaseServiceException(
							StatusCode.PARAMETER_FORMATE_RROR.getCode(),
							"小程序id不能为空");
				}
				if(StringUtils.isBlank(map.get("au_veh_plate").toString())){
					throw new BaseServiceException(
							StatusCode.PARAMETER_FORMATE_RROR.getCode(),
							"车牌不能为空");
				}
				if(StringUtils.isBlank(map.get("au_veh_plate_color").toString())){
					throw new BaseServiceException(
							StatusCode.PARAMETER_FORMATE_RROR.getCode(),
							"车牌颜色不能为空");
				}
				if(StringUtils.isBlank(map.get("au_user_fullname").toString())){
					throw new BaseServiceException(
							StatusCode.PARAMETER_FORMATE_RROR.getCode(),
							"车主姓名不能为空");
				}
				
				if(StringUtils.isBlank(map.get("au_drive_license").toString())){
					throw new BaseServiceException(
							StatusCode.PARAMETER_FORMATE_RROR.getCode(),
							"图片信息不能为空");
				}
				
				if(StringUtils.isBlank(map.get("au_type").toString())){
					throw new BaseServiceException(
							StatusCode.PARAMETER_FORMATE_RROR.getCode(),
							"包期类型不能为空");
				}
				
				Map<String, Object> userMap = new HashMap<>();
				wechatUser user = wechatUsermapper.findWechatUserInfoByOpenId(map.get("userOpenId").toString());
				
				if(user==null){
					throw new BaseServiceException(
							StatusCode.PARAMETER_FORMATE_RROR.getCode(),
							"用户信息为空！");
				}
				
				
				long blackPlateNum = wechatUsermapper.findBlackPlateCountByPlate(map.get("au_veh_plate").toString(),map.get("au_veh_plate_color").toString());
				
				if(blackPlateNum != 0){
					throw new BaseServiceException(
							StatusCode.PARAMETER_FORMATE_RROR.getCode(),
							"车辆存在黑名单记录，请核实是否车辆欠费");
				}
				
				userMap.put("au_user_id", user.getUserId());
				userMap.put("au_veh_plate", map.get("au_veh_plate").toString());
				userMap.put("au_veh_plate_color", map.get("au_veh_plate_color").toString());
				userMap.put("au_user_fullname", map.get("au_user_fullname").toString());
				userMap.put("au_drive_license", map.get("au_drive_license").toString());
				userMap.put("au_type", map.get("au_type").toString());
				
				if(map.get("au_type").toString().equals("1")){
					if(StringUtils.isBlank(map.get("osp_id").toString())){
						throw new BaseServiceException(
								StatusCode.PARAMETER_FORMATE_RROR.getCode(),
								"单包信息不能为空");
					}
					userMap.put("osp_id", map.get("osp_id").toString());
					long num = monthlycarmapper.findMonthlyCarAuditCount(userMap);
					if(num!=0){
						throw new BaseServiceException(
								StatusCode.PARAMETER_FORMATE_RROR.getCode(),
								"有相同审核记录请核实");
					}
				}else{
					if(StringUtils.isBlank(map.get("a_id").toString())){
						throw new BaseServiceException(
								StatusCode.PARAMETER_FORMATE_RROR.getCode(),
								"全包信息不能为空");
					}
					userMap.put("a_id", map.get("a_id").toString());
					long num = monthlycarmapper.findMonthlyCarAuditCount(userMap);
					if(num!=0){
						throw new BaseServiceException(
								StatusCode.PARAMETER_FORMATE_RROR.getCode(),
								"有相同审核记录请核实");
					}
				}
				
				monthlycarmapper.monthlyCarAudit(userMap);
			
				
				
			}







			public List<Map<String, Object>> findMonthlyCarAudit(Map<String, Object> map) {
				
				if(StringUtils.isBlank(map.get("userOpenId").toString())){
					throw new BaseServiceException(
							StatusCode.PARAMETER_FORMATE_RROR.getCode(),
							"小程序id不能为空");
				}
				
				wechatUser user = wechatUsermapper.findWechatUserInfoByOpenId(map.get("userOpenId").toString());
				if(user==null){
					throw new BaseServiceException(
							StatusCode.PARAMETER_FORMATE_RROR.getCode(),
							"请核实用户信息");
				}
				map.put("au_user_id", user.getUserId());
				
				List<Map<String, Object>> auditList = new ArrayList<>(); 
				List<Map<String, Object>> newauditList = new ArrayList<>();
				auditList =monthlycarmapper.findMonthlyCarAudit(map);
				for(Map<String, Object> auditMap :auditList){
					if(auditMap.get("au_status").toString().equals("1")){
						if(auditMap.get("au_type").toString().equals("1")){
							String endingtime = monthlycarmapper.findMonthlyCarSingleEndingTime(auditMap.get("osp_id").toString(),auditMap.get("au_veh_plate").toString());
							auditMap.put("sub_expire_time", endingtime);
						}else if(auditMap.get("au_type").toString().equals("2")){
							String endingtime = monthlycarmapper.findMonthlyCarParkingAllEndingTime(auditMap.get("au_veh_plate").toString());
							auditMap.put("sub_expire_time", endingtime);
						}else if(auditMap.get("au_type").toString().equals("3")){
							String endingtime = monthlycarmapper.findMonthlyCarRoadAllEndingTime(auditMap.get("au_veh_plate").toString());
							auditMap.put("sub_expire_time", endingtime);
						}else if(auditMap.get("au_type").toString().equals("4")){
							String endingtime = monthlycarmapper.findMonthlyCarAllEndingTime(auditMap.get("au_veh_plate").toString());
							auditMap.put("sub_expire_time", endingtime);
						}
					}else{
						auditMap.put("sub_expire_time", "");
					}
					newauditList.add(auditMap);
				}
				
				
				
				return newauditList;
			}







			public Map<String, Object> findMonthlyCarAuditDet(Map<String, Object> map) {
				Map<String, Object> detMap = new  HashMap<>();
				if(StringUtils.isBlank(map.get("userOpenId").toString())){
					throw new BaseServiceException(
							StatusCode.PARAMETER_FORMATE_RROR.getCode(),
							"小程序id不能为空");
				}
				
				wechatUser user = wechatUsermapper.findWechatUserInfoByOpenId(map.get("userOpenId").toString());
				if(user==null){
					throw new BaseServiceException(
							StatusCode.PARAMETER_FORMATE_RROR.getCode(),
							"请核实用户信息");
				}
				List<Map<String, Object>> packageInfo = new ArrayList<>();
				List<Map<String, Object>> packageInfoList = new ArrayList<>();
				if(map.get("au_type").toString().equals("1")){
					if(StringUtils.isBlank(map.get("osp_id").toString())){
						throw new BaseServiceException(
								StatusCode.PARAMETER_FORMATE_RROR.getCode(),
								"单包信息不能为空");
					}
					packageInfo = monthlycarmapper.findOperationSubscriptionParkinglotById(map);
					if(packageInfo.size()==0){
						throw new BaseServiceException(
								StatusCode.PARAMETER_FORMATE_RROR.getCode(),
								"单包停车场套餐已取消，请联系管理员");
					}
					detMap.put("packageInfo", packageInfo);
					detMap.put("parkingInfo", monthlycarmapper.findOperationSubscriptionParkinglotDetById(map));
					Map<String, Object> monthMap = new HashMap<>();
					if(packageInfo.get(0).get("osp_status").toString().equals("1")){
						monthMap.put("price", Integer.parseInt(packageInfo.get(0).get("osp_money").toString()));
						monthMap.put("days", Integer.parseInt(packageInfo.get(0).get("osp_base_day").toString())+Integer.parseInt(packageInfo.get(0).get("osp_give_day_month").toString()));
						monthMap.put("name", "包月");
					}
					
					Map<String, Object> seasonMap = new HashMap<>();
					if(packageInfo.get(0).get("osp_season_status").toString().equals("1")){
					seasonMap.put("price", Integer.parseInt(packageInfo.get(0).get("osp_money").toString())*3);
					seasonMap.put("days", Integer.parseInt(packageInfo.get(0).get("osp_base_day").toString())*3+Integer.parseInt(packageInfo.get(0).get("osp_give_day_season").toString()));
					seasonMap.put("name", "包季");
					}
					Map<String, Object> halfYearMap = new HashMap<>();
					if(packageInfo.get(0).get("osp_half_year_status").toString().equals("1")){
					halfYearMap.put("price", Integer.parseInt(packageInfo.get(0).get("osp_money").toString())*6);
					halfYearMap.put("days", Integer.parseInt(packageInfo.get(0).get("osp_base_day").toString())*6+Integer.parseInt(packageInfo.get(0).get("osp_give_day_half_year").toString()));
					halfYearMap.put("name", "包半年");
					}
					Map<String, Object> yearMap = new HashMap<>();
					if(packageInfo.get(0).get("osp_year_status").toString().equals("1")){
					yearMap.put("price", Integer.parseInt(packageInfo.get(0).get("osp_money").toString())*12);
					yearMap.put("days", Integer.parseInt(packageInfo.get(0).get("osp_base_day").toString())*12+Integer.parseInt(packageInfo.get(0).get("osp_give_day_year").toString()));
					yearMap.put("name", "包年");
					}
					packageInfoList.add(monthMap);
					packageInfoList.add(seasonMap);
					packageInfoList.add(halfYearMap);
					packageInfoList.add(yearMap);
					detMap.put("packageInfoList", packageInfoList);
					return detMap;
				}else{
					if(StringUtils.isBlank(map.get("a_id").toString())){
						throw new BaseServiceException(
								StatusCode.PARAMETER_FORMATE_RROR.getCode(),
								"全包不能为空");
					}
					if(StringUtils.isBlank(map.get("a_id").toString())){
						throw new BaseServiceException(
								StatusCode.PARAMETER_FORMATE_RROR.getCode(),
								"全包不能为空");
					}
					packageInfo=monthlycarmapper.findOperationSubscriptionAllById(map);
					
					if(packageInfo.size()==0){
						throw new BaseServiceException(
								StatusCode.PARAMETER_FORMATE_RROR.getCode(),
								"全包停车场套餐已取消，请联系管理员");
					}
					detMap.put("packageInfo",packageInfo);
					
					detMap.put("parkingInfo", monthlycarmapper.findMonthlyCarPackageAllParkingLotInfo(map));
					
					Map<String, Object> monthMap = new HashMap<>();
					if(packageInfo.get(0).get("a_month_status").toString().equals("1")){
					monthMap.put("price", Integer.parseInt(packageInfo.get(0).get("a_money").toString()));
					monthMap.put("days", Integer.parseInt(packageInfo.get(0).get("a_base_day").toString())+Integer.parseInt(packageInfo.get(0).get("a_give_day_month").toString()));
					monthMap.put("name", "包月");
				    }
					Map<String, Object> seasonMap = new HashMap<>();
					if(packageInfo.get(0).get("a_season_status").toString().equals("1")){
					seasonMap.put("price", Integer.parseInt(packageInfo.get(0).get("a_money").toString())*3);
					seasonMap.put("days", Integer.parseInt(packageInfo.get(0).get("a_base_day").toString())*3+Integer.parseInt(packageInfo.get(0).get("a_give_day_season").toString()));
					seasonMap.put("name", "包季");
			        }          
					Map<String, Object> halfYearMap = new HashMap<>();
					if(packageInfo.get(0).get("a_half_year_status").toString().equals("1")){
					halfYearMap.put("price", Integer.parseInt(packageInfo.get(0).get("a_money").toString())*6);
					halfYearMap.put("days", Integer.parseInt(packageInfo.get(0).get("a_base_day").toString())*6+Integer.parseInt(packageInfo.get(0).get("a_give_day_half_year").toString()));
					halfYearMap.put("name", "包半年");
					}
					Map<String, Object> yearMap = new HashMap<>();
					if(packageInfo.get(0).get("a_year_status").toString().equals("1")){
					yearMap.put("price", Integer.parseInt(packageInfo.get(0).get("a_money").toString())*12);
					yearMap.put("days", Integer.parseInt(packageInfo.get(0).get("a_base_day").toString())*12+Integer.parseInt(packageInfo.get(0).get("a_give_day_year").toString()));
					yearMap.put("name", "包年");
					}
					packageInfoList.add(monthMap);
					packageInfoList.add(seasonMap);
					packageInfoList.add(halfYearMap);
					packageInfoList.add(yearMap);
					detMap.put("packageInfoList", packageInfoList);
					
					return detMap;
				}
				
			}







			public Map<String, Object> monthlyCarPackagePayByWechat(Map<String, Object> wechatPayMap,
					HttpServletRequest request) {
				
				String userOpenId=wechatPayMap.get("userOpenId").toString();
				
				
				
				String plate = wechatPayMap.get("plate").toString();
				
				String amount=wechatPayMap.get("amount").toString();
				
				String days=wechatPayMap.get("days").toString();
				
				String au_id=wechatPayMap.get("au_id").toString();
				
				
				if(StringUtils.isBlank(userOpenId)){
					throw new BaseException(StatusCode.MISSING_OPENID_ERROR.getCode(),"小程序userOpenId不能为空！");
				}
				
				if(StringUtils.isBlank(days)){
					throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"包期天数不能为空");
				}
				
				if(StringUtils.isBlank(au_id)){
					throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"审核信息不能为空");
				}
				
				
				if(StringUtils.isBlank(plate)){
					throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"车牌号不能为空！");
				}
				
				if(StringUtils.isBlank(amount)){
					throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"包期金额不能为空！");
				}
				wechatPayMap.put("au_status", "1");
				List<Map<String, Object>> list = monthlycarmapper.findMonthlyCarAudit(wechatPayMap);
				
				if(list.size()==0){
					throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"审核信息不能为空！");
				}
				
				long useMonthly = monthlycarmapper.findUseMonthlyCount(au_id);
				
				if(useMonthly!=0){
					throw new BaseException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),"包期天数有余，无法续包");
				}
				
				try{

		            //公众号的appid
		            String appid = "wx606d7fcc6d1402c9";
		            String openid = userOpenId;
		            //开始发送红包
		            //logger.info("++++++++++++++开始发送红包++++++++++++++++++");
		            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
		            /** 当前时间 yyyyMMddHHmmss */
		            String currTime = CommonUtil.getCurrTime();
		            /** 8位日期 */
		            String strTime = currTime.substring(8, currTime.length());
		            /** 四位随机数 */
		            String strRandom = CommonUtil.buildRandom(4) + "";
		            //商户订单号
		            //parameters.put("mch_billno",strTime + strRandom);
		            String outTradeNo = currTime+strTime+strRandom;
		            wechatPayMap.put("out_trade_no", outTradeNo);
		            /** 公众号APPID */
		            parameters.put("appid", appid);
		            parameters.put("body", plate+"包月缴费"+days);
		            /** 商户号 */
		            String mch_id = "1604629952";
		            parameters.put("mch_id", mch_id);
		            /** 随机字符串 */
		            parameters.put("nonce_str", CommonUtil.getNonceStr());
		            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/monthlyCar/payCallback_package");
		            parameters.put("out_trade_no", outTradeNo);
		            parameters.put("openid", openid);
		            parameters.put("spbill_create_ip", request.getRemoteAddr());
		            parameters.put("total_fee", amount);
		            parameters.put("trade_type", "JSAPI");
		            parameters.put("attach", au_id+"_"+days+"_"+outTradeNo);//优惠券recordId

		            
		            
		            /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
		            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
		            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);
		            String requestJsonStr = new ObjectMapper().writeValueAsString(parameters);
		            //logger.info("发送的信息是"+requestJsonStr);
		            parameters.put("sign", sign);//
		            /** 生成xml结构的数据，用于统一下单接口的请求 */
		            String requestXML = CommonUtil.getRequestXml(parameters);
		            /**
		             * 读取证书
		             * 
		             */
		            CloseableHttpClient httpclient = null;
		            Map<String,String> result = new HashMap<String,String>();
		           try {
		                KeyStore keyStore = KeyStore.getInstance("PKCS12");
		                String pathname = "/tomcat/apiclient_cert.p12";//这里填你们的证书的地址，我这里放在linux服务器的/usr下面
		                //String pathname="D:\\home\\apiclient_cert.p12";
		                FileInputStream instream = new FileInputStream(new File(pathname)); //此处为证书所放的绝对路径
		                try {
		                    keyStore.load(instream, mch_id.toCharArray());
		                } finally {
		                    instream.close();
		                }
		                // Trust own CA and all self-signed certs
		                SSLContext sslcontext = SSLContexts.custom()
		                        .loadKeyMaterial(keyStore, mch_id.toCharArray())
		                        .build();
		                // Allow TLSv1 protocol only
		                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
		                        sslcontext,
		                        new String[]{"TLSv1"},
		                        null,
		                        SSLConnectionSocketFactory.getDefaultHostnameVerifier());//SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER
		                httpclient = HttpClients.custom()
		                        .setSSLSocketFactory(sslsf)
		                        .build();
		            }
		            catch (Exception e){
		            	log.info("读取证书信息的时候发生异常异常信息是："+e.getMessage());
		                e.printStackTrace();
		            }
		            try {
		                String requestUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		                HttpPost httpPost = new HttpPost(requestUrl);
		                StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
		                // 设置类型
		                reqEntity.setContentType("application/x-www-form-urlencoded");
		                httpPost.setEntity(reqEntity);
		                log.info("executing request" + httpPost.getRequestLine());
		                CloseableHttpResponse response = httpclient.execute(httpPost);
		                try {
		                    HttpEntity entity = response.getEntity();
		                    System.out.println(response.getStatusLine());
		                    if (entity != null) {
		                        // 从request中取得输入流
		                        InputStream inputStream = entity.getContent();
		                        // 读取输入流
		                        SAXReader reader = new SAXReader();
		                        Document document = reader.read(inputStream);
		                        // 得到xml根元素
		                        Element root = document.getRootElement();
		                        // 得到根元素的所有子节点
		                        List<Element> elementList = root.elements();
		                        // 遍历所有子节点
		                        for (Element e : elementList)
		                        {
		                            result.put(e.getName(), e.getText());
		                        }
		                        // 释放资源
		                        inputStream.close();
		                    }
		                    EntityUtils.consume(entity);
		                }
		                finally {
		                    if(response!=null) {
		                        response.close();
		                    }
		                }
		            }
		            catch (Exception e){
		                e.printStackTrace();
		            }finally {
		                try {
		                    httpclient.close();
		                }
		                catch (Exception e){
		                    e.printStackTrace();
		                }
		            }
		            
		            log.info("发获取PrepayId："+new ObjectMapper().writeValueAsString(result));
		            String refundjson=new ObjectMapper().writeValueAsString(wechatPayMap);//关键
		            
		            if(result.get("result_code").equals("SUCCESS")) {
		            	Map<String, Object> map =CommonUtil.generateSignature(result);
		            	wechatUsermapper.insertApiLogs("统一下单接口_车辆自主包月", refundjson, "success", new ObjectMapper().writeValueAsString(result));
		            	
		            	map.put("price", amount);
		                return map;
		                }
		            else {
		            	log.info("获取PrepayId="+openid+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
		            	wechatUsermapper.insertApiLogs("统一下单接口_车辆自主包月", refundjson, "fail", new ObjectMapper().writeValueAsString(result));
		            	throw new BaseServiceException(
		    					StatusCode.SYSTEM_ERROR.getCode(),
		    					"统一下单接口获取失败，请重新联系管理员！"+"["+result.get("err_code_des")+"]");
		            }
		        }
		        catch (Exception e){
		        	log.info("统一下单接口_车辆自主包月异常，异常信息是："+e.getMessage());
		        	throw new BaseServiceException(
							StatusCode.SYSTEM_ERROR.getCode(),
							e.getMessage());
		        }
			}







			public void callBackNotify_package(String outTradeNo, String au_id, String days, String couponFee) throws IOException {
				PostMethod postMethod = null;
				try{

			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String unlicensedCarOutUrl = res.getString("monthlyCarPackage_url");
				
			    postMethod = new PostMethod(unlicensedCarOutUrl) ;
			   
			    NameValuePair[] Senddata = {
			            new NameValuePair("app_id","a00a9b24866c5b32bb9ca4231fd0c47c54966fa7"),
			            new NameValuePair("app_key","164edf151624b0c7173f1747279ee13f812e4807"),
			            new NameValuePair("au_id",au_id),
			            new NameValuePair("day",days),
			            new NameValuePair("sub_amount",couponFee),
			            new NameValuePair("pay_number",outTradeNo)
			            
			            
			    };
			    
			    
			    postMethod.setRequestBody(Senddata);
			    postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;

			   
			    HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
			    int response = client.executeMethod(postMethod); // POST
			    
			    if(response==200){
			    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
			    	log.info("++++++++++++++++++包期回调接口++++++++++++++++++++++"+dataJson);
			        String errorCode = dataJson.getString("error_code");
			        if(errorCode.equals("0")){
			        	log.info("au_id"+au_id+"回调成功！");
			        	wechatUsermapper.insertApiLogs("包期回调接口", "au_id:"+au_id, "success", "");

			        	
			        }else{
			        	log.info("au_id"+au_id+"回调失败！");
			        	wechatUsermapper.insertApiLogs("包期回调接口", "au_id:"+au_id, "fail", dataJson.getString("error_msg"));
			        	throw new BaseServiceException(
			        			Integer.parseInt(errorCode),
			        			dataJson.getString("error_msg"));
			        }
			       
			        
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"请查看包期回调接口");
			    }
				}finally {
					postMethod.releaseConnection();
				}
				
				
			}
			
			
			public List<Map<String, Object>> findSubscriptionRecordByUserOpenId_new(Map<String, Object> findRecordMap) {
				
				if(StringUtils.isBlank(findRecordMap.get("userOpenId").toString())&&StringUtils.isBlank(findRecordMap.get("userOpenIdZfb").toString())){
					throw new BaseException(StatusCode.MISSING_OPENID_ERROR.getCode(),"openId不能为空！");
				}

				wechatUser user = wechatUsermapper.findWechatUserInfoByOpenId(findRecordMap.get("userOpenId").toString());
				if(user==null){
					throw new BaseServiceException(
							StatusCode.PARAMETER_FORMATE_RROR.getCode(),
							"请核实用户信息");
				}
				findRecordMap.put("au_user_id", user.getUserId());
				findRecordMap.put("au_status", "1");
				return monthlycarmapper.findSubscriptionRecordByAudit(findRecordMap);
				
				//return monthlycarmapper.findSubscriptionRecordByUserOpenId_new(findRecordMap.get("userOpenId").toString(),findRecordMap.get("userOpenIdZfb").toString());
			}
			
			
			
			public void doInvoice_new(doInvoiceDto invoiceDto) throws IOException {
				 String userOpenId = invoiceDto.getUserOpenId();
					
					if(StringUtils.isBlank(invoiceDto.getUserOpenId())){
						throw new BaseServiceException(
								StatusCode.MISSING_OPENID_ERROR.getCode(),
								"用户userOpenId不能为空！");
					}
					
					invoice invoice = invoicemapper.findInvoiceInfoByTaxNumber(invoiceDto.getTaxNumber(),invoiceDto.getUserOpenId());
					if(invoice==null){
						/*if(StringUtils.isBlank(invoiceDto.getBankNumber())){
							throw new BaseServiceException(
									StatusCode.PARAMETER_FORMATE_RROR.getCode(),
									"开户行账号不能为空！");
						}*/
						
						if(StringUtils.isBlank(invoiceDto.getCompanyName())){
							throw new BaseServiceException(
									StatusCode.PARAMETER_FORMATE_RROR.getCode(),
									"发票抬头不能为空！");
						}
						/*if(StringUtils.isBlank(invoiceDto.getTaxNumber())){
							throw new BaseServiceException(
									StatusCode.PARAMETER_FORMATE_RROR.getCode(),
									"发票税号不能为空！");
						}*/
						if(StringUtils.isBlank(invoiceDto.getEmail())){
							throw new BaseServiceException(
									StatusCode.PARAMETER_FORMATE_RROR.getCode(),
									"邮箱地址不能为空！");
						}
						/*if(StringUtils.isBlank(invoiceDto.getAddressMobile())){
							throw new BaseServiceException(
									StatusCode.PARAMETER_FORMATE_RROR.getCode(),
									"联系地址电话不能为空！");
						}*/
						invoice invoice2 = new  invoice();
						invoice2.setUserOpenId(userOpenId);
						invoice2.setCompanyName(invoiceDto.getCompanyName());
						invoice2.setTaxNumber(invoiceDto.getTaxNumber());
						invoice2.setBankNumber(invoiceDto.getBankNumber());
						invoice2.setEmail(invoiceDto.getEmail());
						invoice2.setAddressMobile(invoiceDto.getAddressMobile());
						List<String> rIdList = new ArrayList<>();
						for(String outTradeNo : invoiceDto.getrIds().toString().split(",")){
						
							rIdList.add(outTradeNo);
						}
						Map<String, Object> sumPriceMap = new HashMap<>();
						if(rIdList.size()>0){
							
							sumPriceMap.put("price", invoiceDto.getInvoicePrice());
							sumPriceMap.put("vehPlate", invoiceDto.getVehPlate());
							//String vehiclePates=invoicemapper.getVehiclesByOutTradeNoList(outTradeNoList);
							this.doInvoiceSumPrice_monthlyCar_new(rIdList,invoice2,sumPriceMap);
						}
						
						
						invoicemapper.addInvoiceInfo(invoice2);
					}else{
						List<String> rIdList = new ArrayList<>();
						for(String outTradeNo : invoiceDto.getrIds().toString().split(",")){
							
							rIdList.add(outTradeNo);
						}
						Map<String, Object> sumPriceMap = new HashMap<>();
						if(rIdList.size()>0){
							sumPriceMap.put("price", invoiceDto.getInvoicePrice());
							sumPriceMap.put("vehPlate", invoiceDto.getVehPlate());
							this.doInvoiceSumPrice_monthlyCar_new(rIdList,invoice,sumPriceMap);
						}
						
					}
					
					
					
					
				
			}
			
			
private void doInvoiceSumPrice_monthlyCar_new(List<String> outTradeNoList, invoice invoice2, Map<String, Object> sumPriceMap) throws IOException {
				
				PostMethod postMethod = null;
				try{
				// 设置上传文件目录
			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String doInvoiceUrl = res.getString("doInvoice_url");
		    	
			    postMethod = new PostMethod(doInvoiceUrl) ;

		        NameValuePair[] Senddata = {
			            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
			            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
			            new NameValuePair("invoiceAmount",sumPriceMap.get("price")+""),
			            new NameValuePair("companyName",invoice2.getCompanyName()),
			            new NameValuePair("taxNumber",invoice2.getTaxNumber()),
			            new NameValuePair("addressAndMobile",invoice2.getAddressMobile()),
			            new NameValuePair("bankAndNumber",invoice2.getBankNumber()),
			            new NameValuePair("email",invoice2.getEmail()),
			            new NameValuePair("remark",sumPriceMap.get("vehPlate").toString())
			            
			    };
		        
		        
		        postMethod.setRequestBody(Senddata);
		        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;

		       
		        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
			    int response = client.executeMethod(postMethod); // POST
			    
			    if(response==200){
			    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
		            String errorCode = dataJson.getString("error_code");
		            if(errorCode.equals("0")){
		            String data = dataJson.getString("data");
		            JSONObject  dataJsonR = JSONObject.fromObject(data);
		                
		            	log.info("订单号："+outTradeNoList.toString()+"开票成功");
		            	wechatUsermapper.insertApiLogs("开票接口", "订单号:"+outTradeNoList.toString()+";税号:"+invoice2.getTaxNumber()+";开票金额:"+sumPriceMap, "success", "发票地址:"+dataJsonR.getString("pdfUrl"));
		         
		            	monthlycarmapper.updateSubscriptionRecordByList_new(outTradeNoList,dataJsonR.getString("pdfUrl"),invoice2.getCompanyName(),invoice2.getTaxNumber(),invoice2.getAddressMobile(),invoice2.getBankNumber(),invoice2.getEmail());
		          
		            	monthlycarmapper.updateRoadSubscriptionRecordByList_new(outTradeNoList,dataJsonR.getString("pdfUrl"),invoice2.getCompanyName(),invoice2.getTaxNumber(),invoice2.getAddressMobile(),invoice2.getBankNumber(),invoice2.getEmail());
		            
		            	

		            }else{
		            	log.info("开票号："+outTradeNoList.toString()+"开票失败");

		            	wechatUsermapper.insertApiLogs("开票接口", "订单号:"+outTradeNoList.toString()+";税号:"+invoice2.getTaxNumber()+";开票金额:"+sumPriceMap, "fail", dataJson.getString("error_msg"));
		            	throw new BaseServiceException(
		            			Integer.parseInt(errorCode),
		            			dataJson.getString("error_msg"));
		            }
		           
		            
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"请查看开票接口");
			    }
				}finally {
					postMethod.releaseConnection();
				}
				
			}


	

}
