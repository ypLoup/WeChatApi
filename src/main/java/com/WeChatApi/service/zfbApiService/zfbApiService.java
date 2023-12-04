package com.WeChatApi.service.zfbApiService;


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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.WeChatApi.bean.dto.prePayDto;
import com.WeChatApi.bean.dto.prePayDto2;
import com.WeChatApi.bean.dto.rechargeDto;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.dto.roadPklPayCode;
import com.WeChatApi.bean.dto.userRechargeDto;
import com.WeChatApi.bean.models.noVehiclePlate;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.redPack;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.CommonUtil;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.operationOrderMapper;
import com.WeChatApi.dao.wechatUserMapper;
import com.WeChatApi.service.blueCardService.blueCardService;
import com.WeChatApi.service.businessService.businessService;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipayEncrypt;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;


@Service
@Transactional
public class zfbApiService {
	
	@Autowired
	private wechatUserMapper wechatUsermapper;
	
	@Autowired
	private operationOrderMapper operationOrdermapper;
	
	@Autowired
	private blueCardService bluecardservice;
	
	@Autowired
	private businessService businessservice;

	
	private static Logger log = Logger.getLogger(redPack.class.getName());

	public Map<String, Object> zfbUserLogin(String authCode) throws AlipayApiException {
		
		//使用支付宝小程序的固定方法获取auth_code
        if(StringUtils.isBlank(authCode)){
        	throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"支付宝authCode不能为空！");
        }
           
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
            AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
            // 值为authorization_code时，代表用code换取
            request.setGrantType("authorization_code");
            //授权码，用户对应用授权后得到的
            request.setCode(authCode);
            //这里使用execute方法
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            //刷新令牌，上次换取访问令牌时得到。见出参的refresh_token字段
            request.setRefreshToken(response.getAccessToken());
            //返回成功时 就将唯一标识返回
            if(response.isSuccess()){
            	String openId=response.getUserId();
                Map<String,Object> map=new HashMap<>();
                map.put("user_id", response.getUserId());
                map.put("access_token", response.getAccessToken());
                map.put("expires_in", response.getExpiresIn());
                map.put("refresh_token", response.getRefreshToken());
                map.put("re_expires_in", response.getReExpiresIn());
                //String phone=getPhone();
                wechatUser user=new wechatUser();
            	user.setUserOpenId(openId);
            	user.setUserFrom(2);
            	long num =wechatUsermapper.findWechatUserInfoCountByOpenId(openId);
            	if(num!=0){
            		wechatUsermapper.updateWechatUserInfoByOpenId(openId);
            	}else{
            		wechatUsermapper.addWechatUserInfo(user);
            	}
                return map;
            } else {
            	throw new BaseServiceException(
    					StatusCode.API_FREQUENTLY_ERROR.getCode(),
    					response.getSubMsg()+response.getSubCode());
            }
       

	}
	
	
	
	
		private String getPhone(String encryptedData) throws AlipayApiException, JsonProcessingException, IOException {
			//String encryptedData="LxO+w5h75gNL0syAIGvEltUFwgqeR+9gzm1YtCliUCvGXqNh2EHcif0xfBFwF69iSWBEo7dSSm3/TDzRrZwXrQ==";
			String plainData = AlipayEncrypt.decryptContent(encryptedData, "AES", "Dq2Z1qMFDhEQxrMk51uA8A==","UTF-8");
			
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(plainData);
			String phone = jsonNode.get("mobile").asText();
			return phone;
			}




public Map<String, Object> zfbUserLogin_new(Map<String, String> loginmap) throws AlipayApiException, IOException {
		
		//使用支付宝小程序的固定方法获取auth_code
        if(StringUtils.isBlank(loginmap.get("authCode").toString())){
        	throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"支付宝authCode不能为空！");
        }
        //String a =loginmap.get("endata").toString();
           
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
            AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
            // 值为authorization_code时，代表用code换取
            request.setGrantType("authorization_code");
            //授权码，用户对应用授权后得到的
            request.setCode(loginmap.get("authCode").toString());
            //这里使用execute方法
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            //刷新令牌，上次换取访问令牌时得到。见出参的refresh_token字段
            request.setRefreshToken(response.getAccessToken());
            //返回成功时 就将唯一标识返回
            if(response.isSuccess()){
            	String phone="";
            	String openId=response.getUserId();
            	wechatUser haveUser= wechatUsermapper.findWechatUserInfoByOpenId(openId);
            	if(haveUser!=null&&StringUtils.isBlank(haveUser.getUserMobile())){
            		List<String> ids = new ArrayList<>();
            		ids.add(haveUser.getUserId().toString());
            		wechatUsermapper.deleteBatch(ids);
            	}
            	if(StringUtils.isNotBlank(loginmap.get("endata").toString())){
            		phone=getPhone(loginmap.get("endata").toString());
                }
            	 
                Map<String,Object> map=new HashMap<>();
                map.put("user_id", response.getUserId());
                map.put("access_token", response.getAccessToken());
                map.put("expires_in", response.getExpiresIn());
                map.put("refresh_token", response.getRefreshToken());
                map.put("re_expires_in", response.getReExpiresIn());
                wechatUser user=new wechatUser();
            	user.setUserOpenIdZfb(openId);
            	user.setUserMobile(phone);
            	user.setUserFrom(2);
            	//long num =wechatUsermapper.findWechatUserInfoCountByOpenId(openId);
            	log.info("zfbOpenId="+openId+";userMobile="+phone);
            	wechatUser finduser =wechatUsermapper.findUserInfoByPhoneNum(phone);
            	wechatUser haveUser2= wechatUsermapper.findWechatUserInfoByOpenId(openId);
            	if(finduser!=null){
            		user.setUserId(finduser.getUserId());
            		wechatUsermapper.updateWechatUserInfo(user);
            		return map;
            	}
            	if (haveUser2!=null){
            		user.setUserId(haveUser2.getUserId());
            		wechatUsermapper.updateWechatUserInfo(user);
            		return map;
            	}if(finduser==null&&haveUser2==null){
            		log.info("insertWeChatUserInfo："+new ObjectMapper().writeValueAsString(user)+";num参数"+new ObjectMapper().writeValueAsString(finduser)+"haveUser参数"+new ObjectMapper().writeValueAsString(haveUser2)+"openId"+openId+"phoneNumber"+phone);
            		wechatUser insertUser= wechatUsermapper.findWechatUserInfoByOpenId(openId);
            		if(insertUser==null){
            			log.info("insertWeChatUserInfo开始："+new ObjectMapper().writeValueAsString(user));
            			long iszengsong =wechatUsermapper.findIsZengsong();
            			//long isUserId =wechatUsermapper.findWechatUserPointRecordByUserId(user.getUserId());
            			if(iszengsong==1){
            				user.setPoint(500);
            				wechatUsermapper.addWechatUserInfo(user);
            				wechatUsermapper.addWechatUserPointRecord(user.getUserId());
            			}else{
            				wechatUsermapper.addWechatUserInfo(user);
            			}
            		}
            		//wechatUsermapper.addWechatUserInfo(user);
            	}
                return map;
            } else {
            	throw new BaseServiceException(
    					StatusCode.API_FREQUENTLY_ERROR.getCode(),
    					response.getSubMsg()+response.getSubCode());
            }
       

	}

	public Map<String, Object> VehiclePlateOut(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {
		Map<String, Object> vehcleMap = new HashMap<String, Object>();
		log.info("通道号"+noVehiclePlate.getChannelCode()+"停车场编号"+noVehiclePlate.getParkingCode()+"车牌号"+noVehiclePlate.getVehiclePlate());

		if(StringUtils.isBlank(noVehiclePlate.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"支付宝小程序openId不能为空！");
		}
		
		if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){//车牌信息为空，无牌车获取扣款费用
			//log.info("通道号"+noVehiclePlate.getChannelCode()+"停车场编号"+noVehiclePlate.getParkingCode());
			if(noVehiclePlate.getChannelCode()==null){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场通道号不能为空！");
			}
			
			if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场编号不能为空！");
			}
			
			/******************参数齐全，调用蓝卡开闸接口*******************/
			 operationOrderCondition condition = new operationOrderCondition();
			 condition.setVeh_auth_id(noVehiclePlate.getUserOpenId());
			 operationOrder order = operationOrdermapper.findOrderInfoLimitOne_(condition);
			 if(order==null){
				 throw new BaseServiceException(
							StatusCode.DATA_NOT_EXISTS.getCode(),
							"无牌车无订单信息，请核实！");
			 }
			 Map<String, Object>orderMap=new HashMap<String, Object>();
			 orderMap.put("veh_plate", order.getVeh_plate());
			 orderMap.put("order_entry_time", order.getOrder_entry_time()+"");
			 
			 orderMap.put("out_trade_no", order.getOut_trade_no());
			 orderMap.put("veh_plate_color_txt", order.getVeh_plate_color_txt());
			 orderMap.put("car_type", order.getCar_type());
			 Map<String, Object> aliPayMap  =bluecardservice.UnlicensedCarOut_zfb(noVehiclePlate,request);
			 vehcleMap.put("alipayReturn", aliPayMap.get("getPay"));
	         //vehcleMap=bluecardservice.UnlicensedCarOut_zfb(noVehiclePlate,request);
			 orderMap.put("order_receivable", aliPayMap.get("price"));
			 //orderMap.put("coupon_list", aliPayMap.get("coupon_list"));
	         vehcleMap.put("vehcleInfo", orderMap);
			 return vehcleMap;
			
		}else{//车牌信息不为空，根据牌车获取扣款费用
			if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"车牌号不能为空！");
			}
			/*if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场编号不能为空！");
			}*/
			operationOrderCondition condition2 = new operationOrderCondition();
			condition2.setVeh_plate(noVehiclePlate.getVehiclePlate());
			condition2.setOrder_status(1);
			condition2.setPkl_code(noVehiclePlate.getParkingCode());
			List<operationOrder> list =operationOrdermapper.findOrderInfoLimitOne(condition2);
			if(list.size()==0){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"车牌号【"+noVehiclePlate.getVehiclePlate()+"】不存在订单记录，请核实");
			}
			for(operationOrder a:list){
				noVehiclePlate.setParkingCode(a.getPkl_code());
			}
			
			/*if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场编号不能为空！");
			}*/
			 operationOrderCondition condition = new operationOrderCondition();
			 condition.setVeh_plate(noVehiclePlate.getVehiclePlate());
			 operationOrder order = operationOrdermapper.findOrderInfoLimitOne_(condition);
			 Map<String, Object>orderMap=new HashMap<String, Object>();
			 orderMap.put("veh_plate", order.getVeh_plate());
			 orderMap.put("order_entry_time", order.getOrder_entry_time()+"");
			 orderMap.put("order_receivable", order.getOrder_receivable());
			 orderMap.put("out_trade_no", order.getOut_trade_no());
			 orderMap.put("veh_plate_color_txt", order.getVeh_plate_color_txt());
			 orderMap.put("car_type", order.getCar_type());
			 //vehcleMap.put("alipayReturn", bluecardservice.licensedCarOut_zfb(noVehiclePlate,request));
			 Map<String, Object> aliPayMap  =bluecardservice.licensedCarOut_zfb(noVehiclePlate,request);
			 vehcleMap.put("alipayReturn", aliPayMap.get("getPay"));
	         //vehcleMap=bluecardservice.UnlicensedCarOut_zfb(noVehiclePlate,request);
			 orderMap.put("order_receivable", aliPayMap.get("price"));
			 //orderMap.put("coupon_list", aliPayMap.get("coupon_list"));
			 //vehcleMap =bluecardservice.licensedCarOut_zfb(noVehiclePlate,request);
			 vehcleMap.put("vehcleInfo", orderMap);
			 return vehcleMap;
		}
		
		
		
	}
	
	
	public Map<String, Object> VehiclePlateOut_new(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {
		Map<String, Object> vehcleMap = new HashMap<String, Object>();
		log.info("通道号"+noVehiclePlate.getChannelCode()+"停车场编号"+noVehiclePlate.getParkingCode()+"车牌号"+noVehiclePlate.getVehiclePlate());

		if(StringUtils.isBlank(noVehiclePlate.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"支付宝小程序openId不能为空！");
		}
		
		if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){//车牌信息为空，无牌车获取扣款费用
			//log.info("通道号"+noVehiclePlate.getChannelCode()+"停车场编号"+noVehiclePlate.getParkingCode());
			if(noVehiclePlate.getChannelCode()==null){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场通道号不能为空！");
			}
			
			if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场编号不能为空！");
			}
			
			/******************参数齐全，调用蓝卡开闸接口*******************/
			 Map<String, Object> aliPayMap  =bluecardservice.UnlicensedCarOut_zfb_new(noVehiclePlate,request);
			
			
			 operationOrderCondition condition = new operationOrderCondition();
			 condition.setVeh_auth_id(noVehiclePlate.getUserOpenId());
			 operationOrder order = operationOrdermapper.findOrderInfoLimitOne_(condition);
			 if(order==null){
				 throw new BaseServiceException(
							StatusCode.DATA_NOT_EXISTS.getCode(),
							"无牌车无订单信息，请核实！");
			 }
			 Map<String, Object>orderMap=new HashMap<String, Object>();
			 orderMap.put("order_id", order.getOrder_id());
			 orderMap.put("veh_plate", order.getVeh_plate());
			 orderMap.put("order_entry_time", order.getOrder_entry_time()+"");
			 
			 orderMap.put("out_trade_no", order.getOut_trade_no());
			 orderMap.put("veh_plate_color_txt", order.getVeh_plate_color_txt());
			 orderMap.put("car_type", order.getCar_type());
			
			 vehcleMap.put("alipayReturn", aliPayMap.get("getPay"));
	         //vehcleMap=bluecardservice.UnlicensedCarOut_zfb(noVehiclePlate,request);
			 orderMap.put("order_receivable", aliPayMap.get("price"));
			 orderMap.put("coupon_list", aliPayMap.get("coupon_list"));
	         vehcleMap.put("vehcleInfo", orderMap);
			 return vehcleMap;
			
		}else{//车牌信息不为空，根据牌车获取扣款费用
			if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"车牌号不能为空！");
			}
			/*if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场编号不能为空！");
			}*/
			operationOrderCondition condition2 = new operationOrderCondition();
			condition2.setVeh_plate(noVehiclePlate.getVehiclePlate());
			condition2.setOrder_status(1);
			condition2.setPkl_code(noVehiclePlate.getParkingCode());
			List<operationOrder> list =operationOrdermapper.findOrderInfoLimitOne(condition2);
			if(list.size()==0){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"车牌号【"+noVehiclePlate.getVehiclePlate()+"】不存在订单记录，请核实");
			}
			for(operationOrder a:list){
				noVehiclePlate.setParkingCode(a.getPkl_code());
			}
			
			/*if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场编号不能为空！");
			}*/
			 
			
			 //vehcleMap.put("alipayReturn", bluecardservice.licensedCarOut_zfb(noVehiclePlate,request));
			 Map<String, Object> aliPayMap  =bluecardservice.licensedCarOut_zfb_new(noVehiclePlate,request);
			 
			 
			 
			 operationOrderCondition condition = new operationOrderCondition();
			 condition.setVeh_plate(noVehiclePlate.getVehiclePlate());
			 operationOrder order = operationOrdermapper.findOrderInfoLimitOne_(condition);
			 Map<String, Object>orderMap=new HashMap<String, Object>();
			 orderMap.put("order_id", order.getOrder_id());
			 orderMap.put("veh_plate", order.getVeh_plate());
			 orderMap.put("order_entry_time", order.getOrder_entry_time()+"");
			 orderMap.put("order_receivable", order.getOrder_receivable());
			 orderMap.put("out_trade_no", order.getOut_trade_no());
			 orderMap.put("veh_plate_color_txt", order.getVeh_plate_color_txt());
			 orderMap.put("car_type", order.getCar_type());
			 vehcleMap.put("alipayReturn", aliPayMap.get("getPay"));
	         //vehcleMap=bluecardservice.UnlicensedCarOut_zfb(noVehiclePlate,request);
			 orderMap.put("order_receivable", aliPayMap.get("price"));
			 orderMap.put("coupon_list", aliPayMap.get("coupon_list"));
			 //vehcleMap =bluecardservice.licensedCarOut_zfb(noVehiclePlate,request);
			 vehcleMap.put("vehcleInfo", orderMap);
			 return vehcleMap;
		}
		
		
		
	}
	
	
	

	public String getPrepayId_zfb(prePayDto prePaydto, HttpServletRequest request) {
		//获得初始化的AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
        //设置请求参数
        AlipayTradeCreateRequest request2 = new AlipayTradeCreateRequest();
        
        JSONObject json=new JSONObject();
        //订单号
        json.put("out_trade_no",prePaydto.getOutTradeNo());
        //金额 这里的金额是以元为单位的可以不转换但必须是字符串
        json.put("total_amount",prePaydto.getOrderReceivable()/100.0f);
        //描述
        json.put("subject","停车场收费");
        //用户唯一标识id 这里必须使用buyer_id 参考文档
        json.put("buyer_id",prePaydto.getUserOpenId());
        
        //json.put("passback_params", prePaydto.getCouponRecordId());
        //对象转化为json字符串
        String jsonStr=json.toString();
        //商户通过该接口进行交易的创建下单
        request2.setBizContent(jsonStr);
        //回调地址 是能够访问到的域名加上方法名
        request2.setNotifyUrl("https://jiashan.iparking.tech/mp/WeChatApi/zfbApi/notifyUrl");
        try {
        	Map<String, Object>returnMap=new HashMap<String, Object>();
            AlipayTradeCreateResponse response = alipayClient.execute(request2);
            if(response.isSuccess()){
            	log.info(response.getBody());
            	wechatUsermapper.insertApiLogs("支付宝统一下单接口","用户id:"+prePaydto.getUserOpenId()+"订单号:"+prePaydto.getOutTradeNo()+":金额:"+prePaydto.getOrderReceivable()/100+"支付宝交易号"+response.getTradeNo()+"优惠券id:"+prePaydto.getCouponRecordId(), "success", response.getTradeNo());
            	return response.getTradeNo();
            }else{
            	wechatUsermapper.insertApiLogs("支付宝统一下单接口","用户id:"+prePaydto.getUserOpenId()+"订单号:"+prePaydto.getOutTradeNo()+":金额:"+prePaydto.getOrderReceivable()/100+"优惠券id:"+prePaydto.getCouponRecordId(), "fail", response.getSubCode()+"_"+response.getSubMsg());
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
	
	
	
	
	public String getPrepayId_zfb_new(prePayDto2 prePaydto, HttpServletRequest request) {
		//获得初始化的AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
        //设置请求参数
        AlipayTradeCreateRequest request2 = new AlipayTradeCreateRequest();
        
        JSONObject json=new JSONObject();
        //订单号
        json.put("out_trade_no",prePaydto.getOutTradeNo());
        //金额 这里的金额是以元为单位的可以不转换但必须是字符串
        json.put("total_amount",prePaydto.getOrderReceivable()/100.0f);
        //描述
        json.put("subject","停车场收费");
        //用户唯一标识id 这里必须使用buyer_id 参考文档
        json.put("buyer_id",prePaydto.getUserOpenId());
        if(StringUtils.isBlank(prePaydto.getCouponRecordId())){
        	json.put("passback_params", 0+"_"+0);
        }else{
        	json.put("passback_params", prePaydto.getCouponRecordId()+"_"+prePaydto.getCouponType());
        }
        
        //对象转化为json字符串
        String jsonStr=json.toString();
        //商户通过该接口进行交易的创建下单
        request2.setBizContent(jsonStr);
        //回调地址 是能够访问到的域名加上方法名
        request2.setNotifyUrl("https://jiashan.iparking.tech/mp/WeChatApi/zfbApi/notifyUrl");
        try {
        	Map<String, Object>returnMap=new HashMap<String, Object>();
            AlipayTradeCreateResponse response = alipayClient.execute(request2);
            if(response.isSuccess()){
            	log.info(response.getBody());
            	wechatUsermapper.insertApiLogs("支付宝统一下单接口","用户id:"+prePaydto.getUserOpenId()+"订单号:"+prePaydto.getOutTradeNo()+":金额:"+prePaydto.getOrderReceivable()/100+"支付宝交易号"+response.getTradeNo()+"优惠券id:"+prePaydto.getCouponRecordId(), "success", response.getTradeNo());
            	return response.getTradeNo();
            }else{
            	wechatUsermapper.insertApiLogs("支付宝统一下单接口","用户id:"+prePaydto.getUserOpenId()+"订单号:"+prePaydto.getOutTradeNo()+":金额:"+prePaydto.getOrderReceivable()/100+"优惠券id:"+prePaydto.getCouponRecordId(), "fail", response.getSubCode()+"_"+response.getSubMsg());
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
	
	@Autowired
	private operationOrderMapper orderMapper;
	
	
	public String roadsidePay_zfb(prePayDto2 prePaydto, HttpServletRequest request) {
		
		
		if(StringUtils.isNotBlank(prePaydto.getOrderIds())){//缴欠费
			String orderId[]=prePaydto.getOrderIds().split(",");
			String sumBizAmount=orderMapper.findRoadOperationOrderAmount(Arrays.asList(orderId));
			//wechatCodeMap.put("sumBizAmount", sumBizAmount);
			//logger.info("-微信小程序欠费订单创建-run--"+wechatCodeMap);
			
			int int_bizAmount=prePaydto.getOrderReceivable();
			String bizAmount=int_bizAmount+"";
			if(sumBizAmount.equals(bizAmount)==false){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"缴费金额与实际欠费金额不一致，请核实！");
			}
			/*if( prePaydto.getOrderIds().contains(",")==true){
				wechat_pay_code="872752322417164";//合并欠费
			}else{//find road_plk_code
				roadPklPayCode payCodeInfo=parkingMapper.findPayCodeInfoByPklCode(pklCode);
				if(payCodeInfo==null){
					throw new BaseServiceException(
							StatusCode.CREDITAMOUNT_ERROR.getCode(),
							"该【"+pklCode+"】停车场没有绑定对应的支付子商户，请核实！");
				}
				wechat_pay_code=payCodeInfo.getWechat_pay_code();
			}*/
			
		}/*else{//自主缴费
			//System.out.println(pklCode);
			roadPklPayCode payCodeInfo=parkingMapper.findPayCodeInfoByPklCode(pklCode);
			if(payCodeInfo==null){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"该【"+pklCode+"】停车场没有绑定对应的支付子商户，请核实！");
			}
			wechat_pay_code=payCodeInfo.getWechat_pay_code();
			
		}*/
		
		
		//获得初始化的AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
        //设置请求参数
        AlipayTradeCreateRequest request2 = new AlipayTradeCreateRequest();
        
        JSONObject json=new JSONObject();
        //订单号
        json.put("out_trade_no",prePaydto.getOutTradeNo());
        //金额 这里的金额是以元为单位的可以不转换但必须是字符串
        json.put("total_amount",prePaydto.getOrderReceivable()/100.0f);
        //描述
        json.put("subject","路边泊位停车收费");
        //用户唯一标识id 这里必须使用buyer_id 参考文档
        json.put("buyer_id",prePaydto.getUserOpenId());
        JSONObject passback_params=new JSONObject();
        passback_params.put("couponRecordId", prePaydto.getCouponRecordId());
        passback_params.put("orderIds", prePaydto.getOrderIds());
        String p_params=passback_params.toString();
        json.put("passback_params", "'"+p_params+"'");
        //对象转化为json字符串
        String jsonStr=json.toString();
        //商户通过该接口进行交易的创建下单
        request2.setBizContent(jsonStr);
        //回调地址 是能够访问到的域名加上方法名
        request2.setNotifyUrl("https://jiashanmp.iparking.tech/pos/notify_zfb.php");//泊位支付回调接口
        try {
        	Map<String, Object>returnMap=new HashMap<String, Object>();
            AlipayTradeCreateResponse response = alipayClient.execute(request2);
            if(response.isSuccess()){
            	log.info(response.getBody());
            	wechatUsermapper.insertApiLogs("支付宝统一下单接口_路边泊位","用户id:"+prePaydto.getUserOpenId()+"订单号:"+prePaydto.getOutTradeNo()+":金额:"+prePaydto.getOrderReceivable()/100+"支付宝交易号"+response.getTradeNo()+"优惠券id:"+prePaydto.getCouponRecordId(), "success", response.getTradeNo());
            	return response.getTradeNo();
            }else{
            	wechatUsermapper.insertApiLogs("支付宝统一下单接口_路边泊位","用户id:"+prePaydto.getUserOpenId()+"订单号:"+prePaydto.getOutTradeNo()+":金额:"+prePaydto.getOrderReceivable()/100+"优惠券id:"+prePaydto.getCouponRecordId(), "fail", response.getSubCode()+"_"+response.getSubMsg());
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
	
	
	

	public void VehiclePlateIn(noVehiclePlate noVehiclePlate) throws HttpException, IOException {
		// TODO Auto-generated method stub
		if(StringUtils.isBlank(noVehiclePlate.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"支付宝小程序openId不能为空！");
		}
	
		if(noVehiclePlate.getChannelCode()==null){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"停车场通道号不能为空！");
		}
		
		if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"停车场编号不能为空！");
		}
		
		/******************参数齐全，调用蓝卡开闸接口*******************/
         
         
		bluecardservice.UnlicensedCarIn(noVehiclePlate);
	}

	public String getChargePrepayIdByAliPay(rechargeDto recharge, Integer recordId, HttpServletRequest request) {
		//获得初始化的AlipayClient
				AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
		        //设置请求参数
		        AlipayTradeCreateRequest request2 = new AlipayTradeCreateRequest();
		        
		        /** 当前时间 yyyyMMddHHmmss */
	            String currTime = CommonUtil.getCurrTime();
	            /** 8位日期 */
	            //String strTime = currTime.substring(8, currTime.length());
	            /** 四位随机数 */
	            String strRandom = CommonUtil.buildRandom(4) + "";
	            
	            String outTradeNo=currTime+strRandom;
		        
		        JSONObject json=new JSONObject();
		        //订单号
		        json.put("out_trade_no",outTradeNo);
		        //金额 这里的金额是以元为单位的可以不转换但必须是字符串
		        json.put("total_amount",recharge.getRealAmout()/100.0f);
		        //描述
		        json.put("subject","商户id:"+recharge.getStoreId()+"余额充值");
		        //用户唯一标识id 这里必须使用buyer_id 参考文档
		        json.put("buyer_id",recharge.getUserOpenId());
		        json.put("passback_params", recharge.getScdId()+"_"+recharge.getStoreId()+"_"+recordId);
		        //对象转化为json字符串
		        String jsonStr=json.toString();
		        //商户通过该接口进行交易的创建下单
		        request2.setBizContent(jsonStr);
		        //回调地址 是能够访问到的域名加上方法名
		        request2.setNotifyUrl("https://jiashan.iparking.tech/mp/WeChatApi/business/aliPayCallback");
		        try {
		        	Map<String, Object>returnMap=new HashMap<String, Object>();
		            AlipayTradeCreateResponse response = alipayClient.execute(request2);
		            if(response.isSuccess()){
		            	log.info(response.getBody());
		            	wechatUsermapper.insertApiLogs("支付宝商户充值统一下单接口","商户："+recharge.getStoreId()+"折扣类型："+recharge.getScdId()+"用户id:"+recharge.getUserOpenId()+"订单号:"+outTradeNo+":金额:"+recharge.getRealAmout()/100+";实付："+recharge.getRealAmout()+"支付宝交易号"+response.getTradeNo(), "success", response.getTradeNo());
		            	return response.getTradeNo();
		            }else{
		            	wechatUsermapper.insertApiLogs("支付宝商户充值统一下单接口","商户："+recharge.getStoreId()+"折扣类型："+recharge.getScdId()+"用户id:"+recharge.getUserOpenId()+"订单号:"+outTradeNo+":金额:"+recharge.getRealAmout()/100+";实付："+recharge.getRealAmout(), "fail", response.getSubCode()+"_"+response.getSubMsg());
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

	public Map<String, Object> refundByAlipay(refundDto dto) throws JsonProcessingException {
		//获得初始化的AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
        //设置请求参数
		AlipayTradeRefundRequest  request2 = new AlipayTradeRefundRequest ();

        JSONObject json=new JSONObject();
        //订单号
        json.put("out_trade_no",dto.getOutRefundNo());
        
        json.put("trade_no",dto.getTransactionId());
        //金额 这里的金额是以元为单位的可以不转换但必须是字符串
        json.put("refund_amount",dto.getRefundFee()/100.0f);
        //描述
        json.put("refund_reason",dto.getRefundReason());
        //对象转化为json字符串
        String jsonStr=json.toString();
        //商户通过该接口进行交易的创建下单
        request2.setBizContent(jsonStr);
        try {
        	String refundjson=new ObjectMapper().writeValueAsString(dto);//关键
        	Map<String, Object>returnMap=new HashMap<String, Object>();
        	AlipayTradeRefundResponse  response = alipayClient.execute(request2);
            if(response.isSuccess()){
            	Map<String, Object> alipayMap=new HashMap<String, Object>();
            	alipayMap.put("resultCode", "SUCCESS");
            	alipayMap.put("logTxt", response.getBody());
            	alipayMap.put("refundId", response.getTradeNo());
            	log.info(response.getBody());
            	//businessservice.updatePayrefundStatus(dto.getOutRefundNo(),response.getTradeNo());
            	wechatUsermapper.insertApiLogs("支付宝退款接口",refundjson, "success", response.getTradeNo());
            	return alipayMap;
            }else{
            	wechatUsermapper.insertApiLogs("支付宝退款接口",refundjson,"fail", response.getSubCode()+"_"+response.getSubMsg());
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

	public String getChargePrepayIdByAliPay_userToUp(userRechargeDto userRecharge, Integer r_id, HttpServletRequest request) {
		//获得初始化的AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
        //设置请求参数
        AlipayTradeCreateRequest request2 = new AlipayTradeCreateRequest();
        
        /** 当前时间 yyyyMMddHHmmss */
        String currTime = CommonUtil.getCurrTime();
        /** 8位日期 */
        //String strTime = currTime.substring(8, currTime.length());
        /** 四位随机数 */
        String strRandom = CommonUtil.buildRandom(4) + "";
        
        String outTradeNo=currTime+strRandom;
        
        JSONObject json=new JSONObject();
        //订单号
        json.put("out_trade_no",outTradeNo);
        //金额 这里的金额是以元为单位的可以不转换但必须是字符串
        json.put("total_amount",userRecharge.getRealAmout()/100.0f);
        //描述
        json.put("subject","用户id:"+userRecharge.getUserId()+"预充值");
        //用户唯一标识id 这里必须使用buyer_id 参考文档
        json.put("buyer_id",userRecharge.getUserOpenId());
        json.put("passback_params", userRecharge.getUcdId()+"_"+userRecharge.getUserId()+"_"+r_id);
        //对象转化为json字符串
        String jsonStr=json.toString();
        //商户通过该接口进行交易的创建下单
        request2.setBizContent(jsonStr);
        //回调地址 是能够访问到的域名加上方法名
        request2.setNotifyUrl("https://jiashan.iparking.tech/mp/WeChatApi/wechatUser/aliPayCallback");
        try {
        	Map<String, Object>returnMap=new HashMap<String, Object>();
            AlipayTradeCreateResponse response = alipayClient.execute(request2);
            if(response.isSuccess()){
            	log.info(response.getBody());
            	wechatUsermapper.insertApiLogs("支付宝统一下单接口_用户预充值","用户id："+userRecharge.getUserId()+"折扣类型："+userRecharge.getUcdId()+"用户openid:"+userRecharge.getUserOpenId()+"订单号:"+outTradeNo+":金额:"+userRecharge.getRealAmout()/100+";实付："+userRecharge.getRealAmout()+"支付宝交易号"+response.getTradeNo(), "success", response.getTradeNo());
            	return response.getTradeNo();
            }else{
            	wechatUsermapper.insertApiLogs("支付宝统一下单接口_用户预充值","用户id："+userRecharge.getUserId()+"折扣类型："+userRecharge.getUcdId()+"用户openid:"+userRecharge.getUserOpenId()+"订单号:"+outTradeNo+":金额:"+userRecharge.getRealAmout()/100+";实付："+userRecharge.getRealAmout(), "fail", response.getSubCode()+"_"+response.getSubMsg());
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




	/*public void cashOut_zfb(Map<String, Object> map) throws AlipayApiException {
		
		 String userOpenId=map.get("userOpenId").toString();
         if(StringUtils.isBlank(userOpenId)){
         	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"小程序openId不能为空！");
         }
         
         int balance = Integer.valueOf(map.get("balance").toString());
         
         wechatUser  user = wechatUsermapper.findWechatUserInfoByOpenId(userOpenId);
         
         if(userOpenId.equals(user.getUserOpenIdZfb())==false){
         	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"该用户非支付宝小程序用户，请核实！");
         }
         
         if(user.getBalances()!=balance&&balance!=0&&balance>=1){
         	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"提现金额与实际金额不匹配，请核实！");
         }
         
		
				AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
		        //设置请求参数
				AlipayFundTransUniTransferRequest aliPayRequest= new AlipayFundTransUniTransferRequest();
		        
		        *//** 当前时间 yyyyMMddHHmmss *//*
		        String currTime = CommonUtil.getCurrTime();
		        *//** 8位日期 *//*
		        //String strTime = currTime.substring(8, currTime.length());
		        *//** 四位随机数 *//*
		        String strRandom = CommonUtil.buildRandom(4) + "";
		        
		        String outTradeNo=currTime+strRandom;
		        
		        JSONObject json=new JSONObject();
		        JSONObject payee_info_json=new JSONObject();
		        payee_info_json.put("identity", userOpenId);
		        payee_info_json.put("identity_type", "ALIPAY_USER_ID");
		        //订单号
		        json.put("out_biz_no",outTradeNo);
		        //金额 这里的金额是以元为单位的可以不转换但必须是字符串
		        json.put("trans_amount",balance/100.0f);
		        
		        json.put("product_code","TRANS_ACCOUNT_NO_PWD");
		        
		        json.put("biz_scene","DIRECT_TRANSFER");
		        json.put("payee_info", payee_info_json);
		        json.put("order_title", "【"+userOpenId+"】"+"提现");
		        String jsonStr=json.toString();
		        //商户通过该接口进行交易的创建下单
		        aliPayRequest.setBizContent(jsonStr);
		        //回调地址 是能够访问到的域名加上方法名
		       
		        try {
		        	Map<String, Object>returnMap=new HashMap<String, Object>();
		        	AlipayFundTransUniTransferResponse  response = alipayClient.execute(aliPayRequest);
		            if(response.isSuccess()){
		            	log.info(response.getBody());
		            	wechatUsermapper.updateWechatUserBalances(user.getUserId().toString());
		            	wechatUsermapper.insertRedpackRecord(userOpenId,outTradeNo,JSON.toJSONString(map),JSON.toJSONString(response.getBody()),null,"2","1","2");
		            	
		            }else{
		            	wechatUsermapper.insertRedpackRecord(userOpenId,outTradeNo,JSON.toJSONString(map),JSON.toJSONString(response.getBody()),"fail","2","0","2");
		            	throw new BaseServiceException(
								StatusCode.API_FREQUENTLY_ERROR.getCode(),response.getSubCode()+"_"
										+ response.getSubMsg()
								);
		            }
		        } catch (AlipayApiException e) {
		            e.printStackTrace();
		        }
		        
         
         CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
         certAlipayRequest.setServerUrl("https://openapi.alipay.com/gateway.do");  //gateway:支付宝网关（固定）https://openapi.alipay.com/gateway.do
         certAlipayRequest.setAppId("2021002116635517");  //APPID 即创建应用后生成,详情见创建应用并获取 APPID
         certAlipayRequest.setPrivateKey("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF");  //开发者应用私钥，由开发者自己生成
         certAlipayRequest.setFormat("json");  //参数返回格式，只支持 json 格式
         certAlipayRequest.setCharset("UTF-8");  //请求和签名使用的字符编码格式，支持 GBK和 UTF-8
         certAlipayRequest.setSignType("RSA2");  //商户生成签名字符串所使用的签名算法类型，目前支持 RSA2 和 RSA，推荐商家使用 RSA2。   
         certAlipayRequest.setCertPath(""); //应用公钥证书路径（app_cert_path 文件绝对路径）
         certAlipayRequest.setAlipayPublicCertPath(""); //支付宝公钥证书文件路径（alipay_cert_path 文件绝对路径）
         certAlipayRequest.setRootCertPath("");  //支付宝CA根证书文件路径（alipay_root_cert_path 文件绝对路径）
         AlipayClient alipayClient = new DefaultAlipayClient(certAlipayRequest);
         AlipayFundTransUniTransferRequest request2 = new AlipayFundTransUniTransferRequest();
         //** 当前时间 yyyyMMddHHmmss *//*
	        String currTime = CommonUtil.getCurrTime();
	        //** 8位日期 *//*
	        //String strTime = currTime.substring(8, currTime.length());
	        //** 四位随机数 *//*
	        String strRandom = CommonUtil.buildRandom(4) + "";
	        
	        String outTradeNo=currTime+strRandom;
	        
	        JSONObject json=new JSONObject();
	        JSONObject payee_info_json=new JSONObject();
	        payee_info_json.put("identity", userOpenId);
	        payee_info_json.put("identity_type", "ALIPAY_USER_ID");
	        //订单号
	        json.put("out_biz_no",outTradeNo);
	        //金额 这里的金额是以元为单位的可以不转换但必须是字符串
	        json.put("trans_amount",balance/100.0f);
	        
	        json.put("product_code","TRANS_ACCOUNT_NO_PWD");
	        
	        json.put("biz_scene","DIRECT_TRANSFER");
	        json.put("payee_info", payee_info_json);
	        json.put("order_title", "【"+userOpenId+"】"+"提现");
	        String jsonStr=json.toString();
         request2.setBizContent(jsonStr);
         AlipayFundTransUniTransferResponse response = alipayClient.certificateExecute(request2);
         if(response.isSuccess()){
         System.out.println("调用成功");
         } else {
         System.out.println("调用失败");
         }
		
	}*/
	
	




}
