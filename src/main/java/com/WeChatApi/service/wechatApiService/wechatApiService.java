package com.WeChatApi.service.wechatApiService;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;

import javax.transaction.Transactional;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.protocol.Protocol;
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

import com.WeChatApi.bean.condition.operationOrderCondition;
import com.WeChatApi.bean.condition.wechatUserCondition;
import com.WeChatApi.bean.dto.getCashUrl;
import com.WeChatApi.bean.dto.prePayDto;
import com.WeChatApi.bean.dto.prePayDto2;
import com.WeChatApi.bean.dto.rechargeDto;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.dto.userChargeRecordDto;
import com.WeChatApi.bean.dto.userRechargeRecordDto;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.redPack;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.Arithmetic;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.CommonUtil;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.controller.base.WXUtils;
import com.WeChatApi.controller.base.WxDecodeUtil;
import com.WeChatApi.dao.businessMapper;
import com.WeChatApi.dao.operationOrderMapper;
import com.WeChatApi.dao.wechatUserMapper;
import com.WeChatApi.service.operationOrderService.operationOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;


@Service
@Transactional
public class wechatApiService {
	
	@Autowired
	private wechatUserMapper wechatUsermapper;
	
	@Autowired
	private operationOrderMapper operationOrdermapper;
	
	@Autowired
	private businessMapper businessmapper;
	
	@Autowired
	private static ResourceBundle res = ResourceBundle.getBundle("rccApi");
	
	private static String mchId= res.getString("wechat_mch_id");
	private static String AppSecret= res.getString("wechat_AppSecret");
	private static String Appid= res.getString("wechat_appid");
	private static String mchName= res.getString("wechat_mch_name");
	//private static String p12Url= res.getString("p12Url");
	
	//private String AppSecret="42da2ad4c7bfad7524559abb47267973";
	
	//private String Appid="wx606d7fcc6d1402c9";
	
	private static Logger log = Logger.getLogger(redPack.class.getName());
	
	
	/**
	 * 小程序登录接口地址
	 */
	private String loginUrl="https://api.weixin.qq.com/sns/jscode2session";

	public String wechatLogin(String jsCode,String ivData,String encryptedData) throws Exception {
		String phoneNumber="";
		if(StringUtils.isBlank(jsCode)){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"微信小程序jscode不能为空！");
		}
		GetMethod getMethod = null;
		try{
		getMethod = new GetMethod(loginUrl+"?appid="+Appid+"&secret="+AppSecret+"&js_code="+jsCode) ;
		
        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
	    int response = client.executeMethod(getMethod); // POST
	    
	    if(response==200){
	    	String errorCode="0";
	    	JSONObject  dataJson = JSONObject.fromObject(getMethod.getResponseBodyAsString());
            
            if(dataJson.has("errcode")) {
            	errorCode = dataJson.getString("errcode");
            	if(errorCode.equals("40029")){
                	throw new BaseServiceException(
        					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
        					"微信小程序jscode无效！");
                }else if(errorCode.equals("-1")){
                	throw new BaseServiceException(
        					StatusCode.SYSTEM_ERROR.getCode(),
        					"微信小程序系统繁忙，此时请开发者稍候再试");
                }else if(errorCode.equals("40163")){
                	throw new BaseServiceException(
        					StatusCode.SYSTEM_ERROR.getCode(),
        					"微信小程序code已无效，请重新请求！");
                }
            }else{
            	wechatUser user=new wechatUser();
            	String openId=dataJson.getString("openid");
            	wechatUser haveUser= wechatUsermapper.findWechatUserInfoByOpenId(openId);
            	if(haveUser!=null&&StringUtils.isBlank(haveUser.getUserMobile())){
            		List<String> ids = new ArrayList<>();
            		ids.add(haveUser.getUserId().toString());
            		wechatUsermapper.deleteBatch(ids);
            	}
            	String sessionKey=dataJson.getString("session_key");
            	if(StringUtils.isNotBlank(encryptedData)&&StringUtils.isNotBlank(ivData)){
            		phoneNumber =WXUtils.decrypt(Appid,encryptedData,sessionKey,ivData);
            		user.setUserMobile(phoneNumber);
            	}
            	

            	log.info("phone,openId："+phoneNumber+""+openId);
            	user.setUserOpenId(openId);
            	wechatUser haveUser2= wechatUsermapper.findWechatUserInfoByOpenId(openId);
            	wechatUser num =wechatUsermapper.findUserInfoByPhoneNum(phoneNumber);
            	//wechatUser num =wechatUsermapper.findWechatUserInfoCountByPhone(phoneNumber,openId);
            	if(num!=null){
            		user.setUserId(num.getUserId());
            		wechatUsermapper.updateWechatUserInfo(user);
            		return openId;
            	}
            	if(haveUser2!=null){
            		//wechatUsermapper.updateWechatUserInfoByOpenId(openId);
            		user.setUserId(haveUser2.getUserId());
            		wechatUsermapper.updateWechatUserInfo(user);
            		return openId;
            	}
            	
            	if(num==null&&haveUser2==null){
            		//log.info("insertWeChatUserInfo："+JSON.toJSONString(user)+";num参数"+JSON.toJSONString(num)+"haveUser参数"+JSON.toJSONString(haveUser2)+"openId"+openId+"phoneNumber"+phoneNumber);
            		wechatUser insertUser= wechatUsermapper.findWechatUserInfoByOpenId(openId);
            		if(insertUser==null){
            			//log.info("insertWeChatUserInfo开始："+JSON.toJSONString(user));
            			long iszengsong =wechatUsermapper.findIsZengsong();
            			//long isUserId =wechatUsermapper.findWechatUserPointRecordByUserId(user.getUserId());
            			if(iszengsong==1){
            				user.setPoint(500);
            				wechatUsermapper.addWechatUserInfo(user);
            				//wechatUsermapper.addWechatUserPointRecord(user.getUserId());
            				userChargeRecordDto dto = new userChargeRecordDto();
            				dto.setR_user_id(user.getUserId());
            				dto.setR_type(6);
            				dto.setR_discount_id(0);
            				dto.setR_balances(0);
            				dto.setR_point(500);
            				dto.setR_status(1);
            				businessmapper.insertChargeRecord_addWechatUserPoint(dto);
            			}else{
            				wechatUsermapper.addWechatUserInfo(user);
            			}
            			
            		}
            		
            	}
            	
            	return openId;
            }    
            	
	    }else{
	    	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					"小程序登录接口调用失败");
	    }
		}finally {
			getMethod.releaseConnection();
		}
		return jsCode;
		

	}

	public Map<String, Object> wechatSendredpack(redPack redpack, HttpServletRequest request) {
		
		try{

            //公众号的appid
            //String appid = "wx606d7fcc6d1402c9";
            /**
             * 根据APPID获取access_token
             * 我的access_token是做了一个定时器，每隔两个小时刷新一次access_token的值，
             * 并且保存在redis当中（需要详情的话请留言）。
             */
            //String access_token = RedisUtil.getJustOneMapValueFromRedis(jedisPool,appid,"access_token");
            //发给谁，该用户的openid
            String openid = redpack.getRe_openid();
            //红包的值，最低100分
            Integer redValue = redpack.getTotal_amount();
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
            parameters.put("mch_billno",strTime + strRandom);
            /** 商户号 */
            //String mch_id = "1604629952";
            parameters.put("mch_id", mchId);
            /** 随机字符串 */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            /** 公众号APPID */
            parameters.put("wxappid", Appid);
            /** 商户名称 */
            //String mch_name = "嘉善城服公司";
            parameters.put("send_name",mchName);
            /** 用户openid */
            parameters.put("re_openid",openid);
            /** 付款金额 */
            parameters.put("total_amount",redValue);
            /** 红包发放总人数 */
            parameters.put("total_num",1);
            /** 红包祝福语 */
            parameters.put("wishing",redpack.getWishing());
            /** 调用接口的机器Ip地址 */
            parameters.put("client_ip",request.getRemoteAddr());
            /** 活动名称 */
            String activityName = "这个填你们这个红包活动的名称";
            parameters.put("act_name",redpack.getAct_name());
            /** 备注 */
            parameters.put("remark","现金找零");
            parameters.put("notify_way","MINI_PROGRAM_JSAPI");
            /** 场景id  发放红包使用场景，红包金额大于200时必传
             * PRODUCT_1:商品促销 PRODUCT_2:抽奖 PRODUCT_4:企业内部福利  PRODUCT_5:渠道分润 */
            //parameters.put("scene_id","PRODUCT_2");
            /** 资金授权商户号 */
            //parameters.put("consume_mch_id","");
            /** 活动信息  资金授权商户号，服务商替特约商户发放时使用*/
            //parameters.put("risk_info","");
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
                //String pathname="D:\\home\\xiuzhou\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //此处为证书所放的绝对路径
                try {
                    keyStore.load(instream, mchId.toCharArray());
                } finally {
                    instream.close();
                }
                // Trust own CA and all self-signed certs
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mchId.toCharArray())
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
                //String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
            	String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendminiprogramhb";
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
            log.info("------------------发送红包结束---------------");
            log.info("发送红包微信返回的信息是："+new ObjectMapper().writeValueAsString(result));
            //假如发送成功的话，保存发送的信息
            if(result.get("return_msg").equals("发放成功")) {
            	log.info("红包发放成功openid="+openid+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
            	Map<String, Object> map =CommonUtil.generateSignature_redpack(result);
                return map;
                }
            else {
            	log.info("红包发放失败，openid="+openid+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
            	return null;
            }
        }
        catch (Exception e){
        	log.info("发送红包异常，异常信息是："+e.getMessage());
        	return null;
        }

		
	}

	public Map<String, Object> getPrepayId(prePayDto prepaydto, HttpServletRequest request) throws IOException {
		
		try{

            //公众号的appid
            //String appid = "wx606d7fcc6d1402c9";
            String openid = prepaydto.getUserOpenId();
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
            parameters.put("appid", Appid);
            parameters.put("body", "停车付费");
            /** 商户号 */
            //String mch_id = "1604629952";
            parameters.put("mch_id", mchId);
            /** 随机字符串 */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/wechatApi/payCallback");
            parameters.put("out_trade_no", prepaydto.getOutTradeNo());
            parameters.put("openid", openid);
            parameters.put("spbill_create_ip", request.getRemoteAddr());
            parameters.put("total_fee", prepaydto.getOrderReceivable());
            parameters.put("trade_type", "JSAPI");
            //parameters.put("attach", prepaydto.getCouponRecordId());//优惠券recordId
            
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
                    keyStore.load(instream, mchId.toCharArray());
                } finally {
                    instream.close();
                }
                // Trust own CA and all self-signed certs
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mchId.toCharArray())
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
            //log.info("------------------发送红包结束---------------");
            log.info("发获取PrepayId："+new ObjectMapper().writeValueAsString(result));
            String refundjson=new ObjectMapper().writeValueAsString(prepaydto);//关键
            //log.info("发获取PrepayId："+CommonUtil.generateSignature(result));
            //假如发送成功的话，保存发送的信息
            if(result.get("result_code").equals("SUCCESS")) {
            	Map<String, Object> map =CommonUtil.generateSignature(result);
            	wechatUsermapper.insertApiLogs("统一下单接口", refundjson, "success", new ObjectMapper().writeValueAsString(result));
            	//log.info("获取PrepayId="+openid+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
            	map.put("price", prepaydto.getOrderReceivable());
                return map;
                }
            else {
            	log.info("获取PrepayId="+openid+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
            	wechatUsermapper.insertApiLogs("统一下单接口", refundjson, "fail", new ObjectMapper().writeValueAsString(result));
            	throw new BaseServiceException(
    					StatusCode.SYSTEM_ERROR.getCode(),
    					"统一下单接口获取失败，请重新联系管理员！"+"["+result.get("err_code_des")+"]");
            }
        }
        catch (Exception e){
        	log.info("统一下单接口异常，异常信息是："+e.getMessage());
        	throw new BaseServiceException(
					StatusCode.SYSTEM_ERROR.getCode(),
					e.getMessage());
        }
	
	
	}
	
	
	
	
	
public Map<String, Object> getPrepayId_new(prePayDto2 prepaydto, HttpServletRequest request) throws IOException {
		
		try{

            //公众号的appid
            //String appid = "wx606d7fcc6d1402c9";
            String openid = prepaydto.getUserOpenId();
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
            parameters.put("appid", Appid);
            parameters.put("body", "停车付费");
            /** 商户号 */
            //String mch_id = "1604629952";
            parameters.put("mch_id", mchId);
            /** 随机字符串 */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/wechatApi/payCallback");
            if(prepaydto.getOutTradeNo().contains("J")){
            	log.info("捷顺订单："+prepaydto.getOutTradeNo());
            	String splita [] =prepaydto.getOutTradeNo().split("_");
        		String pklCode = splita[0].toString();
        		int max=10000;
            	int min=1000;
            	Random random = new Random();
            	String randomNum =  (random.nextInt(max - min) + min + 1)+"";
        		parameters.put("out_trade_no", pklCode+"_"+prepaydto.getOrderId()+"_"+randomNum);
        		
            }else{
            	 parameters.put("out_trade_no", prepaydto.getOutTradeNo());
            }
           
            parameters.put("openid", openid);
            parameters.put("spbill_create_ip", request.getRemoteAddr());
            parameters.put("total_fee", prepaydto.getOrderReceivable());
            parameters.put("trade_type", "JSAPI");
            if(StringUtils.isBlank(prepaydto.getCouponRecordId())){
            	parameters.put("attach", 0+"_"+prepaydto.getOrderId()+"_"+0);//优惠券recordId
            }else{
            	parameters.put("attach", prepaydto.getCouponRecordId()+"_"+prepaydto.getOrderId()+"_"+prepaydto.getCouponType());//优惠券recordId
            }
            
            //log.info("唤起支付传参："+JSON.toJSONString(parameters));
            
            
            /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);
            String requestJsonStr = new ObjectMapper().writeValueAsString(parameters);
            //logger.info("发送的信息是"+requestJsonStr);
            parameters.put("sign", sign);//
            log.info("唤起支付传参："+new ObjectMapper().writeValueAsString(parameters));
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
                //String pathname="D:\\home\\xiuzhou\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //此处为证书所放的绝对路径
                try {
                    keyStore.load(instream, mchId.toCharArray());
                } finally {
                    instream.close();
                }
                // Trust own CA and all self-signed certs
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mchId.toCharArray())
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
            //log.info("------------------发送红包结束---------------");
            log.info("发获取PrepayId："+new ObjectMapper().writeValueAsString(result));
            String refundjson=new ObjectMapper().writeValueAsString(prepaydto);//关键
            //log.info("发获取PrepayId："+CommonUtil.generateSignature(result));
            //假如发送成功的话，保存发送的信息
            if(result.get("result_code").equals("SUCCESS")) {
            	Map<String, Object> map =CommonUtil.generateSignature(result);
            	wechatUsermapper.insertApiLogs("统一下单接口", refundjson, "success", new ObjectMapper().writeValueAsString(result));
            	//log.info("获取PrepayId="+openid+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
            	map.put("price", prepaydto.getOrderReceivable());
                return map;
                }
            else {
            	log.info("获取PrepayId="+openid+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
            	wechatUsermapper.insertApiLogs("统一下单接口", refundjson, "fail", new ObjectMapper().writeValueAsString(result));
            	throw new BaseServiceException(
    					StatusCode.SYSTEM_ERROR.getCode(),
    					"统一下单接口获取失败，请重新联系管理员！"+"["+result.get("err_code_des")+"]");
            }
        }
        catch (Exception e){
        	log.info("统一下单接口异常，异常信息是："+e.getMessage());
        	throw new BaseServiceException(
					StatusCode.SYSTEM_ERROR.getCode(),
					e.getMessage());
        }
	
	
	}
	
	
	
	
	
	
	
	/**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     * @param strxml
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
	public Map<String,String> doXMLParse(String strxml) throws Exception {
        if(null == strxml || "".equals(strxml)) {
            return null;
        }

        Map<String,String> m = new HashMap<String,String>();
        InputStream in = String2Inputstream(strxml);
        SAXReader builder = new SAXReader();
        Document document = builder.read(in);
        Element root = document.getRootElement();
        List list = root.elements();
        Iterator it = list.iterator();
        while(it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.elements();
            if(children.isEmpty()) {
                v = e.getTextTrim();
            } else {
                v = getChildrenText(children);
            }

            m.put(k, v);
        }

        //关闭流
        in.close();

        return m;
    }

    private  InputStream String2Inputstream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }

    /**
     * 获取子结点的xml
     * @param children
     * @return String
     */
    @SuppressWarnings("rawtypes")
    private static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if(!children.isEmpty()) {
            Iterator it = children.iterator();
            while(it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextTrim();
                List list = e.elements();
                sb.append("<" + name + ">");
                if(!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }
    
    
    public void insertApiLogs(String apiName,String apiKey,String apiResult,String apiRemarks){
    	wechatUsermapper.insertApiLogs(apiName,apiKey,apiResult,apiRemarks);
    }

	public String refundByWechat(refundDto dto) {
		
		try{

            
            //String appid = "wx606d7fcc6d1402c9";
            
            //String openid = prepaydto.getUserOpenId();
            
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            /** 当前时间 yyyyMMddHHmmss */
            String currTime = CommonUtil.getCurrTime();
            /** 8位日期 */
            String strTime = currTime.substring(8, currTime.length());
            /** 四位随机数 */
            String strRandom = CommonUtil.buildRandom(4) + "";
           
            /** 公众号APPID */
            parameters.put("appid", Appid);
            //parameters.put("body", "停车付费");
            /** 商户号 */
            //String mch_id = "1604629952";
            parameters.put("mch_id", mchId);
            /** 随机字符串 */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/wechatApi/refundCallback");
            parameters.put("out_refund_no", dto.getOutRefundNo());
            parameters.put("transaction_id", dto.getTransactionId());

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
                    keyStore.load(instream, mchId.toCharArray());
                } finally {
                    instream.close();
                }
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mchId.toCharArray())
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

            	wechatUsermapper.insertApiLogs("微信退款接口", refundjson, "success", new ObjectMapper().writeValueAsString(result));

                return result.get("result_code");
                }
            else {
            	wechatUsermapper.insertApiLogs("统一下单接口", refundjson, "fail", new ObjectMapper().writeValueAsString(result));
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
	
	
	
/*public String refundByWechat_xz(refundDto dto) {
		
		try{

            
            //String appid = "wx606d7fcc6d1402c9";
            
            //String openid = prepaydto.getUserOpenId();
            
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            *//** 当前时间 yyyyMMddHHmmss *//*
            String currTime = CommonUtil.getCurrTime();
            *//** 8位日期 *//*
            String strTime = currTime.substring(8, currTime.length());
            *//** 四位随机数 *//*
            String strRandom = CommonUtil.buildRandom(4) + "";
           
            *//** 公众号APPID *//*
            parameters.put("appid", Appid);
            //parameters.put("body", "停车付费");
            *//** 商户号 *//*
            //String mch_id = "1604629952";
            parameters.put("mch_id", mchId);
            *//** 随机字符串 *//*
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            parameters.put("notify_url", "https://api-app.xz.iparking.tech/mp/WeChatApi/wechatApi/refundCallback_xz");
            parameters.put("out_refund_no", dto.getOutRefundNo());
            parameters.put("transaction_id", dto.getTransactionId());

            parameters.put("total_fee", dto.getTotalFee());
            parameters.put("refund_fee", dto.getRefundFee());
            parameters.put("refund_desc", dto.getRefundReason());
            parameters.put("attach", dto.getOrderType());
            
            *//** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 *//*
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);
            String requestJsonStr = JSON.toJSONString(parameters);

            parameters.put("sign", sign);//
            *//** 生成xml结构的数据，用于统一下单接口的请求 *//*
            String requestXML = CommonUtil.getRequestXml(parameters);
            *//**
             * 读取证书
             * 
             *//*
            CloseableHttpClient httpclient = null;
            Map<String,String> result = new HashMap<String,String>();
           try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                String pathname = "/tomcat/apiclient_cert.p12";//这里填你们的证书的地址，我这里放在linux服务器的/usr下面
                //String pathname="D:\\home\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //此处为证书所放的绝对路径
                try {
                    keyStore.load(instream, mchId.toCharArray());
                } finally {
                    instream.close();
                }
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mchId.toCharArray())
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
            String refundjson=JSON.toJSONString(dto);//关键
            log.info("退款JSON"+JSON.toJSONString(result));

            if(result.get("result_code").equals("SUCCESS")) {

            	wechatUsermapper.insertApiLogs("微信退款接口", refundjson, "success", JSON.toJSONString(result));

                return result.get("result_code");
                }
            else {
            	wechatUsermapper.insertApiLogs("统一下单接口", refundjson, "fail", JSON.toJSONString(result));
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
		
		
	}*/

	public String getWechatCashUrl(getCashUrl dto) {
		
		if(StringUtils.isBlank(dto.getOrderno_atm())){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"订单编号不能为空");
		}
		
		if(StringUtils.isBlank(dto.getParkpotid())){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"停车场编号不能为空");
		}
		
		
		
		if(StringUtils.isBlank(dto.getExtbusinessid())){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"现金机订单编号不能为空");
		}
		if(dto.getPaymenttotal()==null){
			throw new BaseServiceException(
					StatusCode.DATA_NOT_EXISTS.getCode(),
					"应缴费总金额不能为空");
		}
		
		if(dto.getPayment()==null){
			throw new BaseServiceException(
					StatusCode.DATA_NOT_EXISTS.getCode(),
					"现金收入金额不能为空");
		}
		
		int cashMent=dto.getPayment()-dto.getPaymenttotal();
		if(cashMent>0){//getCashUrl
			
			String outTradeNo=dto.getOrderno_atm();
			String[] outTradeNos=outTradeNo.split("_");
			String pakCode=outTradeNos[0];
			String orderNumber=outTradeNos[2];
			//String vehplate=dto.getVehPlate();
			operationOrderCondition condition = new operationOrderCondition();
			condition.setPkl_code(pakCode);
			condition.setOrder_number(orderNumber);
			//condition.setVeh_plate(vehplate);
			condition.setOrder_status(1);
			List<operationOrder>list = operationOrdermapper.findOrderInfo(condition);
			if(list.size()!=1){
				throw new BaseServiceException(
						StatusCode.DATA_NOT_MATCH.getCode(),
						"该笔订单存在问题，请核实");
			}
			
			//findUserOpenId
			String vehplate=list.get(0).getVeh_plate();
			/*wechatUser userInfo = wechatUsermapper.findUserInfoByVehplate(vehplate);
			
			if(userInfo==null){
				throw new BaseServiceException(
						StatusCode.DATA_NOT_EXISTS.getCode(),
						"该车主没有入驻微信小程序，无法找零请核实");
			}
			
			if(StringUtils.isBlank(userInfo.getUserOpenId())){
				throw new BaseServiceException(
						StatusCode.DATA_NOT_EXISTS.getCode(),
						"该车主没有入驻微信小程序，无法找零请核实");
			}*/
			
			String path="https://jiashan.iparking.tech/change?sign=";
			//String openid_cashMent_vehplate_outTradeNo_orderNoAtm_orderNumber_totalMent_payMent=userInfo.getUserOpenId()+"@"+cashMent+"@"+vehplate+"@"+outTradeNo+"@"+dto.getExtbusinessid()+"@"+orderNumber+"@"+dto.getPaymenttotal()+"@"+dto.getPayment();
			String userId_cashMent_orderId_totalMent_payMent="99"+"@"+cashMent+"@"+list.get(0).getOrder_id()+"@"+dto.getPaymenttotal()+"@"+dto.getPayment();
			log.info("加密前++++++++++++++"+userId_cashMent_orderId_totalMent_payMent);
			Arithmetic des = new Arithmetic();// 实例化一个对像
			des.getKey("jsy,2016.");// 生成密匙
			String sign = des.getEncString(userId_cashMent_orderId_totalMent_payMent);// 加密字符串,返回String的密文
			String[] datas=userId_cashMent_orderId_totalMent_payMent.split("@");
			
			operationOrderCondition orderCondition = new operationOrderCondition();
        	orderCondition.setOrder_id(Integer.valueOf(datas[2].toString()));
        	List<operationOrder> order = operationOrdermapper.findOrderInfo(orderCondition);
        	String[] outTradeNos_Order=order.get(0).getOut_trade_no().split("_");
			String pakCode_Order=outTradeNos_Order[0];
		    String doubleRed=Double.valueOf(datas[3].toString())/100+"";
			Double redValueDouble=Double.valueOf(doubleRed);
			log.info("红包订单插入到pay"+order.get(0).getOut_trade_no());
			operationOrdermapper.insertOperationPay(order.get(0).getOut_trade_no(),outTradeNos_Order[2].toString(),pakCode_Order,order.get(0).getVeh_plate().toString(),order.get(0).getOut_trade_no(),order.get(0).getOut_trade_no(),datas[3].toString(),redValueDouble.toString(),"10","现金","自助缴费","现金支付",datas[4].toString(),datas[1].toString());
			return path+sign;
		}else{
			return null;
		}

	}

	public Map<String, Object> redpack(Map<String, Object> map, HttpServletRequest request) {
		
		
		
		
		try{
            String signData=map.get("sign").toString();
            if(StringUtils.isBlank(signData)){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"红包验签不存在,请核实！");
            }
            Arithmetic des = new Arithmetic();// 实例化一个对像
            des.getKey("jsy,2016.");// 生成密匙
           
            
            String userId_cashMent_orderId_totalMent_payMent= des.getDesString(signData);
            String[] datas=userId_cashMent_orderId_totalMent_payMent.split("@");
            Map<String, Object> redmap= new HashMap<>();
            redmap.put("orderNum", datas[2].toString());
           
            String wechatJson = "";
            wechatJson=operationOrdermapper.findRedPackByOrderNumber( datas[2].toString());
            if(StringUtils.isNotBlank(wechatJson)){
            	Map<String, Object> redmapResult=this.findRedpackStatus(redmap, request);
                if(redmapResult.containsKey("status")) {
                	if(redmapResult.get("status").equals("RECEIVED")){
                		long isHave=operationOrdermapper.findRedPackCountByOrderNumber(datas[2].toString());
                        
                        if(isHave!=0){
                        	throw new BaseServiceException(
            						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
            						"该订单现金红包已发放成功且领取，请勿重复发放！");
                        }
                	}else if(redmapResult.get("status").equals("SENDING")){
                		throw new BaseServiceException(
        						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
        						"红包发放中！");
                		
                	}else if(redmapResult.get("status").equals("SENT")){
                		throw new BaseServiceException(
        						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
        						"红包已发送！");
                		
                	}else if(redmapResult.get("status").equals("FAILED")){
                		throw new BaseServiceException(
        						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
        						"红包发放失败，请联系管理员！");
                		
                	}else if(redmapResult.get("status").equals("RFUND_ING")){
                		throw new BaseServiceException(
        						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
        						"红包退款中！");
                		
                	}else if(redmapResult.get("status").equals("REFUND")){
                		throw new BaseServiceException(
        						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
        						"红包已退款！");
                		
                	}
                }else{
                	throw new BaseServiceException(
    						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
    						"红包发放中！");
                }
            }
            
            //根据订单号查询车主信息
            List<wechatUser>  user=wechatUsermapper.findUserInfoByOrderNum(datas[2].toString());
            
            if(user.size()==0){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"请先关注小程序【嘉善停车】绑定车牌后领取");
            }
            
            
            
            Integer redValue = Integer.valueOf(datas[1]);
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
            String mch_billno=strTime + strRandom;
            String mchBillno=datas[2].toString().replaceAll("_", "")+mch_billno;
            parameters.put("mch_billno",mchBillno);
            /** 商户号 */
            parameters.put("mch_id", mchId);
            /** 随机字符串 */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            /** 公众号APPID */
            parameters.put("wxappid", Appid);
            /** 商户名称 */
           
            parameters.put("send_name",mchName);
            /** 用户openid */
            parameters.put("re_openid",user.get(0).getUserOpenId());
            /** 付款金额 */
            parameters.put("total_amount",redValue);
            /** 红包发放总人数 */
            parameters.put("total_num",1);
            /** 红包祝福语 */
            parameters.put("wishing","现金找零【"+user.get(0).getUserId()+"】");
            /** 调用接口的机器Ip地址 */
            ResourceBundle res = ResourceBundle.getBundle("white_ip_list");
    	    String whiteIp = res.getString("whiteIp");
            parameters.put("client_ip",whiteIp);
            /** 活动名称 */
            
            parameters.put("act_name","现金找零【"+user.get(0).getUserId()+"】");
            /** 备注 */
            parameters.put("remark","现金找零");
            parameters.put("notify_way","MINI_PROGRAM_JSAPI");
            
            /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);

            parameters.put("sign", sign);//
            log.info("parameters："+parameters);
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
                    keyStore.load(instream, mchId.toCharArray());
                } finally {
                    instream.close();
                }
                // Trust own CA and all self-signed certs
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mchId.toCharArray())
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
                //String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
            	String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendminiprogramhb";
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
	            log.info("------------------发送红包结束---------------");
	            log.info("发送红包微信返回的信息是："+new ObjectMapper().writeValueAsString(result));
	            Map<String, Object> dataMap= new HashMap<String, Object>();//openid_cashMent_vehplate_outTradeNo_orderNoAtm
	        	dataMap.put("userOpenId", user.get(0).getUserOpenId());
	        	dataMap.put("cashMent", datas[1].toString());
	        	dataMap.put("oderId", datas[2].toString());
	        	dataMap.put("totalMent", datas[3].toString());
	        	dataMap.put("payMent", datas[4].toString());
            if(result.get("return_msg").equals("发放成功")) {
            	log.info("红包发放成功openid="+user.get(0).getUserOpenId()+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
            	Map<String, Object> map1 =CommonUtil.generateSignature_redpack(result);
            	operationOrderCondition orderCondition = new operationOrderCondition();
            	orderCondition.setOrder_id(Integer.valueOf(datas[2].toString()));
            	List<operationOrder> order = operationOrdermapper.findOrderInfo(orderCondition);
            	String[] outTradeNos=order.get(0).getOut_trade_no().split("_");
    			String pakCode=outTradeNos[0];
    		    String doubleRed=Double.valueOf(datas[3].toString())/100+"";
    			Double redValueDouble=Double.valueOf(doubleRed);
    			/*operationOrdermapper.insertOperationPay(order.get(0).getOut_trade_no(),outTradeNos[2].toString(),pakCode,order.get(0).getVeh_plate().toString(),mch_billno,mch_billno,datas[3].toString(),redValueDouble.toString(),"10","现金","自助缴费","现金支付",datas[4].toString(),datas[1].toString());*/
    			log.info("红包订单更新到pay"+order.get(0).getOut_trade_no());
    			operationOrdermapper.updateOperationPayInfoByOutTradeNo(mch_billno, datas[1].toString(), order.get(0).getOut_trade_no());
            	wechatUsermapper.insertRedpackRecord(user.get(0).getUserId().toString(),user.get(0).getUserOpenId(),redValue+"",datas[2].toString(),new ObjectMapper().writeValueAsString(dataMap),new ObjectMapper().writeValueAsString(result),new ObjectMapper().writeValueAsString(map1),"1","1","1");
                return map1;
                }
            else {
            	log.info("红包发放失败，openid="+user.get(0).getUserOpenId()+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
            	wechatUsermapper.insertRedpackRecord(user.get(0).getUserId().toString(),user.get(0).getUserOpenId(),redValue+"",datas[2].toString(),new ObjectMapper().writeValueAsString(dataMap),new ObjectMapper().writeValueAsString(result),"fail","1","0","1");
            	return null;
            }
        }
        catch (Exception e){
        	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					e.getMessage());
        	
        }

	}

	public void cashOut_wechat(Map<String, Object> map, HttpServletRequest request) {
		
		try{
            String userOpenId=map.get("userOpenId").toString();
            if(StringUtils.isBlank(userOpenId)){
            	throw new BaseServiceException(
						StatusCode.MISSING_OPENID_ERROR.getCode(),
						"小程序openId不能为空！");
            }
            
            int balance = Integer.valueOf(map.get("balance").toString());
            
            wechatUser  user = wechatUsermapper.findWechatUserInfoByOpenId(userOpenId);
            
            if(userOpenId.equals(user.getUserOpenId())==false){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"该用户非微信小程序用户，请核实！");
            }
            
            if(user.getBalances()!=balance&&balance!=0&&balance>=1){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"提现金额与实际金额不匹配，请核实！");
            }
            
            List<Map<String, Object>> refundList=new ArrayList<>();
            refundList=this.getRefundList(user,balance);
            for(Map<String, Object> refundmap :refundList){
            	
            	userChargeRecordDto userchargerecord = new userChargeRecordDto();
            	userchargerecord.setR_user_id(user.getUserId());
            	userchargerecord.setR_type(2);
            	userchargerecord.setR_balances(Integer.valueOf(refundmap.get("refundFee").toString()));
				userchargerecord.setR_point(0);
				userchargerecord.setR_discount_id(0);
				userchargerecord.setR_out_trade_no(refundmap.get("outTradeNo").toString());
				userchargerecord.setR_left_balances(0);
				userchargerecord.setR_left_point(0);
                int recordId=businessmapper.insertChargeRecord_cashOut(userchargerecord);

                SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
                /** 当前时间 yyyyMMddHHmmss */
                String currTime = CommonUtil.getCurrTime();
                /** 8位日期 */
                String strTime = currTime.substring(8, currTime.length());
                /** 四位随机数 */
                String strRandom = CommonUtil.buildRandom(4) + "";
                //商户订单号
                String mch_billno=strTime + strRandom;
                /** 公众号APPID */
                parameters.put("appid", Appid);
               
                /** 商户号 */
                parameters.put("mch_id", mchId);
                /** 随机字符串 */
                parameters.put("nonce_str", CommonUtil.getNonceStr());
                /**支付订单号**/
                parameters.put("out_trade_no",refundmap.get("outTradeNo").toString());
                /** 商户退款单号 */
                parameters.put("out_refund_no","TK"+refundmap.get("outTradeNo").toString());
                /** 订单金额 */
                parameters.put("total_fee",Integer.valueOf(refundmap.get("totalFee").toString()));
                /** 退款金额 */
                parameters.put("refund_fee",Integer.valueOf(refundmap.get("refundFee").toString()));
                /** 退款结果通知url */
                parameters.put("notify_url","https://jiashan.iparking.tech/mp/WeChatApi/wechatApi/cashOutCallback");
                /** 退款原因 */
                parameters.put("refund_desc","用户:"+user.getUserId()+"提现"+"_"+userchargerecord.getR_id());
                //parameters.put("attach",userchargerecord.getR_id());//充值记录表Id
                
                /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
                String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
                String sign = CommonUtil.createSign("UTF-8", parameters,api_key);

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
                        keyStore.load(instream, mchId.toCharArray());
                    } finally {
                        instream.close();
                    }
                    // Trust own CA and all self-signed certs
                    SSLContext sslcontext = SSLContexts.custom()
                            .loadKeyMaterial(keyStore, mchId.toCharArray())
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
                    //String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
                	//String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
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
    	            log.info("------------------发送提现结束---------------");
    	            log.info("发送微信提现返回的信息是："+new ObjectMapper().writeValueAsString(result));
    	           
                if(result.get("result_code").equals("SUCCESS")) {
                	log.info("微信提现成功openid="+userOpenId+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
                	wechatUsermapper.insertRedpackRecord(user.getUserId().toString(),userOpenId,refundmap.get("refundFee").toString()+"",refundmap.get("outTradeNo").toString(),new ObjectMapper().writeValueAsString(parameters),new ObjectMapper().writeValueAsString(result),null,"2","1","1");
                	
                	wechatUsermapper.updateWechatUserBalances(user.getUserId().toString());
                	
                    
                    }
                else {
                	//log.info("红包发放失败，openid="+userOpenId+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
                	wechatUsermapper.insertRedpackRecord(user.getUserId().toString(),userOpenId,refundmap.get("refundFee").toString()+"",refundmap.get("outTradeNo").toString(),new ObjectMapper().writeValueAsString(parameters),new ObjectMapper().writeValueAsString(result),"fail","2","0","1");
                	throw new BaseServiceException(
        					StatusCode.API_FREQUENTLY_ERROR.getCode(),
        					result.get("return_msg")+","+result.get("err_code_des"));
                	
                }
            }
          
            
        }
        catch (Exception e){
        	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					e.getMessage());
        	
        }
		
	}

	private List<Map<String, Object>> getRefundList(wechatUser user, int balance) {
		List<userRechargeRecordDto> recordList = businessmapper.findUseRechargeRecordByUserId(user.getUserId().toString());
		if(recordList.size()==0){
			throw new BaseServiceException(
					StatusCode.DATA_NOT_EXISTS.getCode(),
					"该用户没有符合退款的订单记录！");
		}
		int sumBalance =0;
		int needRefundFee=balance;
		List<Map<String, Object>> refundList= new ArrayList<>();
		//Map<String, Object> refund= new HashMap<>();
		for(int i = 0;i<recordList.size();i++){
			Map<String, Object> refund= new HashMap<>();
			if(balance>sumBalance){
				
				refund.put("outTradeNo", recordList.get(i).getOutTradeNo());
				refund.put("totalFee", recordList.get(i).getBalances());
				sumBalance+=Integer.valueOf(recordList.get(i).getBalances());
				
				if(needRefundFee>Integer.valueOf(recordList.get(i).getBalances())){
					refund.put("refundFee", needRefundFee-(needRefundFee-Integer.valueOf(recordList.get(i).getBalances())));
				}else{
					refund.put("refundFee", needRefundFee);
				}
				
				
				needRefundFee=balance-sumBalance;
			}else{
				break;
			}
			if(refund!=null){
				refundList.add(refund);
			}
			
		}
		return refundList;
	}

	public Map<String, Object> findRedpackStatus(Map<String, Object> map, HttpServletRequest request) {
		try{
            String orderNum=map.get("orderNum").toString();
            if(StringUtils.isBlank(orderNum)){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"订单号不能为空！");
            }
           
            String wechatJson = "";
            wechatJson=operationOrdermapper.findRedPackByOrderNumber(orderNum);
            
            //long isHave=operationOrdermapper.findRedPackCountByOrderNumber(datas[2].toString());
            
            if(StringUtils.isBlank(wechatJson)){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"该订单现金红包已发放记录不存在！");
            }
            JSONObject wechatJson_ = JSONObject.fromObject(wechatJson);
            //公众号的appid
            //String appid = "wx606d7fcc6d1402c9";
           
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
          
            String mchBillno=wechatJson_.getString("mch_billno");
            parameters.put("mch_billno",mchBillno);
            /** 商户号 */
            parameters.put("mch_id", mchId);
            /** 随机字符串 */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            /** 公众号APPID */
            parameters.put("appid", Appid);
           
            parameters.put("bill_type","MCHT");
     
            /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);

            parameters.put("sign", sign);//
            log.info("parameters："+parameters);
            /** 生成xml结构的数据，用于统一下单接口的请求 */
            String requestXML = CommonUtil.getRequestXml(parameters);
            /**
             * 读取证书
             * 
             */
            CloseableHttpClient httpclient = null;
            Map<String,Object> result = new HashMap<String,Object>();
            try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                String pathname = "/tomcat/apiclient_cert.p12";//这里填你们的证书的地址，我这里放在linux服务器的/usr下面
                //String pathname="D:\\home\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //此处为证书所放的绝对路径
                try {
                    keyStore.load(instream, mchId.toCharArray());
                } finally {
                    instream.close();
                }
                // Trust own CA and all self-signed certs
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mchId.toCharArray())
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
                //String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
            	String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gethbinfo";
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
	            log.info("------------------查询红包结束---------------");
	            log.info("查询红包微信返回的信息是："+new ObjectMapper().writeValueAsString(result));
	            Map<String, Object> dataMap= new HashMap<String, Object>();//openid_cashMent_vehplate_outTradeNo_orderNoAtm
	        	
	        	 if(result.get("result_code").equals("SUCCESS")) {
            	
            	//Map<String, Object> map1 =CommonUtil.generateSignature_redpack(result);
            	
                return result;
                }
            else {
            	
            	return null;
            }
        }
        catch (Exception e){
        	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					e.getMessage());
        	
        }
	}

	public Map<String, Object> roadPayByWechat(Map<String, Object> wechatCodeMap, HttpServletRequest request) {
		try{

            //公众号的appid
            //String appid = "wx606d7fcc6d1402c9";
            String openid = wechatCodeMap.get("userOpenid").toString();
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
            parameters.put("appid", Appid);
            parameters.put("body", "路边停车付费");
            /** 商户号 */
            parameters.put("mch_id", mchId);
            /** 随机字符串 */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/wechatApi/roadPayCallback");
            parameters.put("out_trade_no", wechatCodeMap.get("bizId").toString());
            parameters.put("openid", openid);
            parameters.put("spbill_create_ip", request.getRemoteAddr());
            parameters.put("total_fee", wechatCodeMap.get("sumBizAmount").toString());
            parameters.put("trade_type", "JSAPI");
            if(StringUtils.isNotBlank(wechatCodeMap.get("orderIds").toString())){
            	parameters.put("attach", wechatCodeMap.get("orderIds").toString());//欠费id
            }else{
            	parameters.put("attach", 0);//无欠费
            }
            
            System.out.println(parameters);
            
            
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
                //String pathname="D:\\home\\xiuzhou\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //此处为证书所放的绝对路径
                try {
                    keyStore.load(instream, mchId.toCharArray());
                } finally {
                    instream.close();
                }
                // Trust own CA and all self-signed certs
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mchId.toCharArray())
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
            //log.info("------------------发送红包结束---------------");
            log.info("发获取PrepayId："+new ObjectMapper().writeValueAsString(result));
            String refundjson=new ObjectMapper().writeValueAsString(wechatCodeMap);//关键
            //log.info("发获取PrepayId："+CommonUtil.generateSignature(result));
            //假如发送成功的话，保存发送的信息
            if(result.get("result_code").equals("SUCCESS")) {
            	Map<String, Object> map =CommonUtil.generateSignature(result);
            	wechatUsermapper.insertApiLogs("路边缴费统一下单接口", refundjson, "success", new ObjectMapper().writeValueAsString(result));
            	//log.info("获取PrepayId="+openid+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
            	//map.put("price", prepaydto.getOrderReceivable());
                return map;
                }
            else {
            	log.info("获取PrepayId="+openid+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
            	wechatUsermapper.insertApiLogs("路边缴费统一下单接口", refundjson, "fail", new ObjectMapper().writeValueAsString(result));
            	throw new BaseServiceException(
    					StatusCode.SYSTEM_ERROR.getCode(),
    					"统一下单接口获取失败，请重新联系管理员！"+"["+result.get("err_code_des")+"]");
            }
        }
        catch (Exception e){
        	log.info("路边缴费统一下单接口，异常信息是："+e.getMessage());
        	throw new BaseServiceException(
					StatusCode.SYSTEM_ERROR.getCode(),
					e.getMessage());
        }
	
	
	}

	public Map<String, Object> findRedpackStatus_web(Map<String, Object> map, HttpServletRequest request) {
		try{
            String orderNum=map.get("orderNum").toString();
            if(StringUtils.isBlank(orderNum)){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"订单号不能为空！");
            }
           
            String wechatJson = "";
            wechatJson=operationOrdermapper.findRedPackByOrderNumber_web(orderNum);
            
            //long isHave=operationOrdermapper.findRedPackCountByOrderNumber(datas[2].toString());
            
            if(StringUtils.isBlank(wechatJson)){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"该订单现金红包已发放记录不存在！");
            }
            JSONObject wechatJson_ = JSONObject.fromObject(wechatJson);
            //公众号的appid
            //String appid = "wx606d7fcc6d1402c9";
           
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
          
            String mchBillno=wechatJson_.getString("mch_billno");
            parameters.put("mch_billno",mchBillno);
            /** 商户号 */
            parameters.put("mch_id", mchId);
            /** 随机字符串 */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            /** 公众号APPID */
            parameters.put("appid", Appid);
           
            parameters.put("bill_type","MCHT");
     
            /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);

            parameters.put("sign", sign);//
            log.info("parameters："+parameters);
            /** 生成xml结构的数据，用于统一下单接口的请求 */
            String requestXML = CommonUtil.getRequestXml(parameters);
            /**
             * 读取证书
             * 
             */
            CloseableHttpClient httpclient = null;
            Map<String,Object> result = new HashMap<String,Object>();
            try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                String pathname = "/tomcat/apiclient_cert.p12";//这里填你们的证书的地址，我这里放在linux服务器的/usr下面
                //String pathname="D:\\home\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //此处为证书所放的绝对路径
                try {
                    keyStore.load(instream, mchId.toCharArray());
                } finally {
                    instream.close();
                }
                // Trust own CA and all self-signed certs
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mchId.toCharArray())
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
                //String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
            	String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gethbinfo";
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
	            log.info("------------------查询红包结束---------------");
	            log.info("查询红包微信返回的信息是："+new ObjectMapper().writeValueAsString(result));
	            Map<String, Object> dataMap= new HashMap<String, Object>();//openid_cashMent_vehplate_outTradeNo_orderNoAtm
	        	
	        	 if(result.get("result_code").equals("SUCCESS")) {
            	
            	//Map<String, Object> map1 =CommonUtil.generateSignature_redpack(result);
            	
                return result;
                }
            else {
            	
            	return null;
            }
        }
        catch (Exception e){
        	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					e.getMessage());
        	
        }
	}
    
	





}
