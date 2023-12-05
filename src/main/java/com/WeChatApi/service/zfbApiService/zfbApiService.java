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
		
		//ʹ��֧����С����Ĺ̶�������ȡauth_code
        if(StringUtils.isBlank(authCode)){
        	throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"֧����authCode����Ϊ�գ�");
        }
           
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
            AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
            // ֵΪauthorization_codeʱ��������code��ȡ
            request.setGrantType("authorization_code");
            //��Ȩ�룬�û���Ӧ����Ȩ��õ���
            request.setCode(authCode);
            //����ʹ��execute����
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            //ˢ�����ƣ��ϴλ�ȡ��������ʱ�õ��������ε�refresh_token�ֶ�
            request.setRefreshToken(response.getAccessToken());
            //���سɹ�ʱ �ͽ�Ψһ��ʶ����
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
		
		//ʹ��֧����С����Ĺ̶�������ȡauth_code
        if(StringUtils.isBlank(loginmap.get("authCode").toString())){
        	throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"֧����authCode����Ϊ�գ�");
        }
        //String a =loginmap.get("endata").toString();
           
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
            AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
            // ֵΪauthorization_codeʱ��������code��ȡ
            request.setGrantType("authorization_code");
            //��Ȩ�룬�û���Ӧ����Ȩ��õ���
            request.setCode(loginmap.get("authCode").toString());
            //����ʹ��execute����
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            //ˢ�����ƣ��ϴλ�ȡ��������ʱ�õ��������ε�refresh_token�ֶ�
            request.setRefreshToken(response.getAccessToken());
            //���سɹ�ʱ �ͽ�Ψһ��ʶ����
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
            		log.info("insertWeChatUserInfo��"+new ObjectMapper().writeValueAsString(user)+";num����"+new ObjectMapper().writeValueAsString(finduser)+"haveUser����"+new ObjectMapper().writeValueAsString(haveUser2)+"openId"+openId+"phoneNumber"+phone);
            		wechatUser insertUser= wechatUsermapper.findWechatUserInfoByOpenId(openId);
            		if(insertUser==null){
            			log.info("insertWeChatUserInfo��ʼ��"+new ObjectMapper().writeValueAsString(user));
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
		log.info("ͨ����"+noVehiclePlate.getChannelCode()+"ͣ�������"+noVehiclePlate.getParkingCode()+"���ƺ�"+noVehiclePlate.getVehiclePlate());

		if(StringUtils.isBlank(noVehiclePlate.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"֧����С����openId����Ϊ�գ�");
		}
		
		if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){//������ϢΪ�գ����Ƴ���ȡ�ۿ����
			//log.info("ͨ����"+noVehiclePlate.getChannelCode()+"ͣ�������"+noVehiclePlate.getParkingCode());
			if(noVehiclePlate.getChannelCode()==null){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"ͣ����ͨ���Ų���Ϊ�գ�");
			}
			
			if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"ͣ������Ų���Ϊ�գ�");
			}
			
			/******************������ȫ������������բ�ӿ�*******************/
			 operationOrderCondition condition = new operationOrderCondition();
			 condition.setVeh_auth_id(noVehiclePlate.getUserOpenId());
			 operationOrder order = operationOrdermapper.findOrderInfoLimitOne_(condition);
			 if(order==null){
				 throw new BaseServiceException(
							StatusCode.DATA_NOT_EXISTS.getCode(),
							"���Ƴ��޶�����Ϣ�����ʵ��");
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
			
		}else{//������Ϣ��Ϊ�գ������Ƴ���ȡ�ۿ����
			if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"���ƺŲ���Ϊ�գ�");
			}
			/*if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"ͣ������Ų���Ϊ�գ�");
			}*/
			operationOrderCondition condition2 = new operationOrderCondition();
			condition2.setVeh_plate(noVehiclePlate.getVehiclePlate());
			condition2.setOrder_status(1);
			condition2.setPkl_code(noVehiclePlate.getParkingCode());
			List<operationOrder> list =operationOrdermapper.findOrderInfoLimitOne(condition2);
			if(list.size()==0){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"���ƺš�"+noVehiclePlate.getVehiclePlate()+"�������ڶ�����¼�����ʵ");
			}
			for(operationOrder a:list){
				noVehiclePlate.setParkingCode(a.getPkl_code());
			}
			
			/*if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"ͣ������Ų���Ϊ�գ�");
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
		log.info("ͨ����"+noVehiclePlate.getChannelCode()+"ͣ�������"+noVehiclePlate.getParkingCode()+"���ƺ�"+noVehiclePlate.getVehiclePlate());

		if(StringUtils.isBlank(noVehiclePlate.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"֧����С����openId����Ϊ�գ�");
		}
		
		if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){//������ϢΪ�գ����Ƴ���ȡ�ۿ����
			//log.info("ͨ����"+noVehiclePlate.getChannelCode()+"ͣ�������"+noVehiclePlate.getParkingCode());
			if(noVehiclePlate.getChannelCode()==null){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"ͣ����ͨ���Ų���Ϊ�գ�");
			}
			
			if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"ͣ������Ų���Ϊ�գ�");
			}
			
			/******************������ȫ������������բ�ӿ�*******************/
			 Map<String, Object> aliPayMap  =bluecardservice.UnlicensedCarOut_zfb_new(noVehiclePlate,request);
			
			
			 operationOrderCondition condition = new operationOrderCondition();
			 condition.setVeh_auth_id(noVehiclePlate.getUserOpenId());
			 operationOrder order = operationOrdermapper.findOrderInfoLimitOne_(condition);
			 if(order==null){
				 throw new BaseServiceException(
							StatusCode.DATA_NOT_EXISTS.getCode(),
							"���Ƴ��޶�����Ϣ�����ʵ��");
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
			
		}else{//������Ϣ��Ϊ�գ������Ƴ���ȡ�ۿ����
			if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"���ƺŲ���Ϊ�գ�");
			}
			/*if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"ͣ������Ų���Ϊ�գ�");
			}*/
			operationOrderCondition condition2 = new operationOrderCondition();
			condition2.setVeh_plate(noVehiclePlate.getVehiclePlate());
			condition2.setOrder_status(1);
			condition2.setPkl_code(noVehiclePlate.getParkingCode());
			List<operationOrder> list =operationOrdermapper.findOrderInfoLimitOne(condition2);
			if(list.size()==0){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"���ƺš�"+noVehiclePlate.getVehiclePlate()+"�������ڶ�����¼�����ʵ");
			}
			for(operationOrder a:list){
				noVehiclePlate.setParkingCode(a.getPkl_code());
			}
			
			/*if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"ͣ������Ų���Ϊ�գ�");
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
		//��ó�ʼ����AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
        //�����������
        AlipayTradeCreateRequest request2 = new AlipayTradeCreateRequest();
        
        JSONObject json=new JSONObject();
        //������
        json.put("out_trade_no",prePaydto.getOutTradeNo());
        //��� ����Ľ������ԪΪ��λ�Ŀ��Բ�ת�����������ַ���
        json.put("total_amount",prePaydto.getOrderReceivable()/100.0f);
        //����
        json.put("subject","ͣ�����շ�");
        //�û�Ψһ��ʶid �������ʹ��buyer_id �ο��ĵ�
        json.put("buyer_id",prePaydto.getUserOpenId());
        
        //json.put("passback_params", prePaydto.getCouponRecordId());
        //����ת��Ϊjson�ַ���
        String jsonStr=json.toString();
        //�̻�ͨ���ýӿڽ��н��׵Ĵ����µ�
        request2.setBizContent(jsonStr);
        //�ص���ַ ���ܹ����ʵ����������Ϸ�����
        request2.setNotifyUrl("https://jiashan.iparking.tech/mp/WeChatApi/zfbApi/notifyUrl");
        try {
        	Map<String, Object>returnMap=new HashMap<String, Object>();
            AlipayTradeCreateResponse response = alipayClient.execute(request2);
            if(response.isSuccess()){
            	log.info(response.getBody());
            	wechatUsermapper.insertApiLogs("֧����ͳһ�µ��ӿ�","�û�id:"+prePaydto.getUserOpenId()+"������:"+prePaydto.getOutTradeNo()+":���:"+prePaydto.getOrderReceivable()/100+"֧�������׺�"+response.getTradeNo()+"�Ż�ȯid:"+prePaydto.getCouponRecordId(), "success", response.getTradeNo());
            	return response.getTradeNo();
            }else{
            	wechatUsermapper.insertApiLogs("֧����ͳһ�µ��ӿ�","�û�id:"+prePaydto.getUserOpenId()+"������:"+prePaydto.getOutTradeNo()+":���:"+prePaydto.getOrderReceivable()/100+"�Ż�ȯid:"+prePaydto.getCouponRecordId(), "fail", response.getSubCode()+"_"+response.getSubMsg());
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
		//��ó�ʼ����AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
        //�����������
        AlipayTradeCreateRequest request2 = new AlipayTradeCreateRequest();
        
        JSONObject json=new JSONObject();
        //������
        json.put("out_trade_no",prePaydto.getOutTradeNo());
        //��� ����Ľ������ԪΪ��λ�Ŀ��Բ�ת�����������ַ���
        json.put("total_amount",prePaydto.getOrderReceivable()/100.0f);
        //����
        json.put("subject","ͣ�����շ�");
        //�û�Ψһ��ʶid �������ʹ��buyer_id �ο��ĵ�
        json.put("buyer_id",prePaydto.getUserOpenId());
        if(StringUtils.isBlank(prePaydto.getCouponRecordId())){
        	json.put("passback_params", 0+"_"+0);
        }else{
        	json.put("passback_params", prePaydto.getCouponRecordId()+"_"+prePaydto.getCouponType());
        }
        
        //����ת��Ϊjson�ַ���
        String jsonStr=json.toString();
        //�̻�ͨ���ýӿڽ��н��׵Ĵ����µ�
        request2.setBizContent(jsonStr);
        //�ص���ַ ���ܹ����ʵ����������Ϸ�����
        request2.setNotifyUrl("https://jiashan.iparking.tech/mp/WeChatApi/zfbApi/notifyUrl");
        try {
        	Map<String, Object>returnMap=new HashMap<String, Object>();
            AlipayTradeCreateResponse response = alipayClient.execute(request2);
            if(response.isSuccess()){
            	log.info(response.getBody());
            	wechatUsermapper.insertApiLogs("֧����ͳһ�µ��ӿ�","�û�id:"+prePaydto.getUserOpenId()+"������:"+prePaydto.getOutTradeNo()+":���:"+prePaydto.getOrderReceivable()/100+"֧�������׺�"+response.getTradeNo()+"�Ż�ȯid:"+prePaydto.getCouponRecordId(), "success", response.getTradeNo());
            	return response.getTradeNo();
            }else{
            	wechatUsermapper.insertApiLogs("֧����ͳһ�µ��ӿ�","�û�id:"+prePaydto.getUserOpenId()+"������:"+prePaydto.getOutTradeNo()+":���:"+prePaydto.getOrderReceivable()/100+"�Ż�ȯid:"+prePaydto.getCouponRecordId(), "fail", response.getSubCode()+"_"+response.getSubMsg());
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
		
		
		if(StringUtils.isNotBlank(prePaydto.getOrderIds())){//��Ƿ��
			String orderId[]=prePaydto.getOrderIds().split(",");
			String sumBizAmount=orderMapper.findRoadOperationOrderAmount(Arrays.asList(orderId));
			//wechatCodeMap.put("sumBizAmount", sumBizAmount);
			//logger.info("-΢��С����Ƿ�Ѷ�������-run--"+wechatCodeMap);
			
			int int_bizAmount=prePaydto.getOrderReceivable();
			String bizAmount=int_bizAmount+"";
			if(sumBizAmount.equals(bizAmount)==false){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"�ɷѽ����ʵ��Ƿ�ѽ�һ�£����ʵ��");
			}
			/*if( prePaydto.getOrderIds().contains(",")==true){
				wechat_pay_code="872752322417164";//�ϲ�Ƿ��
			}else{//find road_plk_code
				roadPklPayCode payCodeInfo=parkingMapper.findPayCodeInfoByPklCode(pklCode);
				if(payCodeInfo==null){
					throw new BaseServiceException(
							StatusCode.CREDITAMOUNT_ERROR.getCode(),
							"�á�"+pklCode+"��ͣ����û�а󶨶�Ӧ��֧�����̻������ʵ��");
				}
				wechat_pay_code=payCodeInfo.getWechat_pay_code();
			}*/
			
		}/*else{//�����ɷ�
			//System.out.println(pklCode);
			roadPklPayCode payCodeInfo=parkingMapper.findPayCodeInfoByPklCode(pklCode);
			if(payCodeInfo==null){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"�á�"+pklCode+"��ͣ����û�а󶨶�Ӧ��֧�����̻������ʵ��");
			}
			wechat_pay_code=payCodeInfo.getWechat_pay_code();
			
		}*/
		
		
		//��ó�ʼ����AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
        //�����������
        AlipayTradeCreateRequest request2 = new AlipayTradeCreateRequest();
        
        JSONObject json=new JSONObject();
        //������
        json.put("out_trade_no",prePaydto.getOutTradeNo());
        //��� ����Ľ������ԪΪ��λ�Ŀ��Բ�ת�����������ַ���
        json.put("total_amount",prePaydto.getOrderReceivable()/100.0f);
        //����
        json.put("subject","·�߲�λͣ���շ�");
        //�û�Ψһ��ʶid �������ʹ��buyer_id �ο��ĵ�
        json.put("buyer_id",prePaydto.getUserOpenId());
        JSONObject passback_params=new JSONObject();
        passback_params.put("couponRecordId", prePaydto.getCouponRecordId());
        passback_params.put("orderIds", prePaydto.getOrderIds());
        String p_params=passback_params.toString();
        json.put("passback_params", "'"+p_params+"'");
        //����ת��Ϊjson�ַ���
        String jsonStr=json.toString();
        //�̻�ͨ���ýӿڽ��н��׵Ĵ����µ�
        request2.setBizContent(jsonStr);
        //�ص���ַ ���ܹ����ʵ����������Ϸ�����
        request2.setNotifyUrl("https://jiashanmp.iparking.tech/pos/notify_zfb.php");//��λ֧���ص��ӿ�
        try {
        	Map<String, Object>returnMap=new HashMap<String, Object>();
            AlipayTradeCreateResponse response = alipayClient.execute(request2);
            if(response.isSuccess()){
            	log.info(response.getBody());
            	wechatUsermapper.insertApiLogs("֧����ͳһ�µ��ӿ�_·�߲�λ","�û�id:"+prePaydto.getUserOpenId()+"������:"+prePaydto.getOutTradeNo()+":���:"+prePaydto.getOrderReceivable()/100+"֧�������׺�"+response.getTradeNo()+"�Ż�ȯid:"+prePaydto.getCouponRecordId(), "success", response.getTradeNo());
            	return response.getTradeNo();
            }else{
            	wechatUsermapper.insertApiLogs("֧����ͳһ�µ��ӿ�_·�߲�λ","�û�id:"+prePaydto.getUserOpenId()+"������:"+prePaydto.getOutTradeNo()+":���:"+prePaydto.getOrderReceivable()/100+"�Ż�ȯid:"+prePaydto.getCouponRecordId(), "fail", response.getSubCode()+"_"+response.getSubMsg());
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
					"֧����С����openId����Ϊ�գ�");
		}
	
		if(noVehiclePlate.getChannelCode()==null){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"ͣ����ͨ���Ų���Ϊ�գ�");
		}
		
		if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"ͣ������Ų���Ϊ�գ�");
		}
		
		/******************������ȫ������������բ�ӿ�*******************/
         
         
		bluecardservice.UnlicensedCarIn(noVehiclePlate);
	}

	public String getChargePrepayIdByAliPay(rechargeDto recharge, Integer recordId, HttpServletRequest request) {
		//��ó�ʼ����AlipayClient
				AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
		        //�����������
		        AlipayTradeCreateRequest request2 = new AlipayTradeCreateRequest();
		        
		        /** ��ǰʱ�� yyyyMMddHHmmss */
	            String currTime = CommonUtil.getCurrTime();
	            /** 8λ���� */
	            //String strTime = currTime.substring(8, currTime.length());
	            /** ��λ����� */
	            String strRandom = CommonUtil.buildRandom(4) + "";
	            
	            String outTradeNo=currTime+strRandom;
		        
		        JSONObject json=new JSONObject();
		        //������
		        json.put("out_trade_no",outTradeNo);
		        //��� ����Ľ������ԪΪ��λ�Ŀ��Բ�ת�����������ַ���
		        json.put("total_amount",recharge.getRealAmout()/100.0f);
		        //����
		        json.put("subject","�̻�id:"+recharge.getStoreId()+"����ֵ");
		        //�û�Ψһ��ʶid �������ʹ��buyer_id �ο��ĵ�
		        json.put("buyer_id",recharge.getUserOpenId());
		        json.put("passback_params", recharge.getScdId()+"_"+recharge.getStoreId()+"_"+recordId);
		        //����ת��Ϊjson�ַ���
		        String jsonStr=json.toString();
		        //�̻�ͨ���ýӿڽ��н��׵Ĵ����µ�
		        request2.setBizContent(jsonStr);
		        //�ص���ַ ���ܹ����ʵ����������Ϸ�����
		        request2.setNotifyUrl("https://jiashan.iparking.tech/mp/WeChatApi/business/aliPayCallback");
		        try {
		        	Map<String, Object>returnMap=new HashMap<String, Object>();
		            AlipayTradeCreateResponse response = alipayClient.execute(request2);
		            if(response.isSuccess()){
		            	log.info(response.getBody());
		            	wechatUsermapper.insertApiLogs("֧�����̻���ֵͳһ�µ��ӿ�","�̻���"+recharge.getStoreId()+"�ۿ����ͣ�"+recharge.getScdId()+"�û�id:"+recharge.getUserOpenId()+"������:"+outTradeNo+":���:"+recharge.getRealAmout()/100+";ʵ����"+recharge.getRealAmout()+"֧�������׺�"+response.getTradeNo(), "success", response.getTradeNo());
		            	return response.getTradeNo();
		            }else{
		            	wechatUsermapper.insertApiLogs("֧�����̻���ֵͳһ�µ��ӿ�","�̻���"+recharge.getStoreId()+"�ۿ����ͣ�"+recharge.getScdId()+"�û�id:"+recharge.getUserOpenId()+"������:"+outTradeNo+":���:"+recharge.getRealAmout()/100+";ʵ����"+recharge.getRealAmout(), "fail", response.getSubCode()+"_"+response.getSubMsg());
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
		//��ó�ʼ����AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
        //�����������
		AlipayTradeRefundRequest  request2 = new AlipayTradeRefundRequest ();

        JSONObject json=new JSONObject();
        //������
        json.put("out_trade_no",dto.getOutRefundNo());
        
        json.put("trade_no",dto.getTransactionId());
        //��� ����Ľ������ԪΪ��λ�Ŀ��Բ�ת�����������ַ���
        json.put("refund_amount",dto.getRefundFee()/100.0f);
        //����
        json.put("refund_reason",dto.getRefundReason());
        //����ת��Ϊjson�ַ���
        String jsonStr=json.toString();
        //�̻�ͨ���ýӿڽ��н��׵Ĵ����µ�
        request2.setBizContent(jsonStr);
        try {
        	String refundjson=new ObjectMapper().writeValueAsString(dto);//�ؼ�
        	Map<String, Object>returnMap=new HashMap<String, Object>();
        	AlipayTradeRefundResponse  response = alipayClient.execute(request2);
            if(response.isSuccess()){
            	Map<String, Object> alipayMap=new HashMap<String, Object>();
            	alipayMap.put("resultCode", "SUCCESS");
            	alipayMap.put("logTxt", response.getBody());
            	alipayMap.put("refundId", response.getTradeNo());
            	log.info(response.getBody());
            	//businessservice.updatePayrefundStatus(dto.getOutRefundNo(),response.getTradeNo());
            	wechatUsermapper.insertApiLogs("֧�����˿�ӿ�",refundjson, "success", response.getTradeNo());
            	return alipayMap;
            }else{
            	wechatUsermapper.insertApiLogs("֧�����˿�ӿ�",refundjson,"fail", response.getSubCode()+"_"+response.getSubMsg());
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
		//��ó�ʼ����AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
        //�����������
        AlipayTradeCreateRequest request2 = new AlipayTradeCreateRequest();
        
        /** ��ǰʱ�� yyyyMMddHHmmss */
        String currTime = CommonUtil.getCurrTime();
        /** 8λ���� */
        //String strTime = currTime.substring(8, currTime.length());
        /** ��λ����� */
        String strRandom = CommonUtil.buildRandom(4) + "";
        
        String outTradeNo=currTime+strRandom;
        
        JSONObject json=new JSONObject();
        //������
        json.put("out_trade_no",outTradeNo);
        //��� ����Ľ������ԪΪ��λ�Ŀ��Բ�ת�����������ַ���
        json.put("total_amount",userRecharge.getRealAmout()/100.0f);
        //����
        json.put("subject","�û�id:"+userRecharge.getUserId()+"Ԥ��ֵ");
        //�û�Ψһ��ʶid �������ʹ��buyer_id �ο��ĵ�
        json.put("buyer_id",userRecharge.getUserOpenId());
        json.put("passback_params", userRecharge.getUcdId()+"_"+userRecharge.getUserId()+"_"+r_id);
        //����ת��Ϊjson�ַ���
        String jsonStr=json.toString();
        //�̻�ͨ���ýӿڽ��н��׵Ĵ����µ�
        request2.setBizContent(jsonStr);
        //�ص���ַ ���ܹ����ʵ����������Ϸ�����
        request2.setNotifyUrl("https://jiashan.iparking.tech/mp/WeChatApi/wechatUser/aliPayCallback");
        try {
        	Map<String, Object>returnMap=new HashMap<String, Object>();
            AlipayTradeCreateResponse response = alipayClient.execute(request2);
            if(response.isSuccess()){
            	log.info(response.getBody());
            	wechatUsermapper.insertApiLogs("֧����ͳһ�µ��ӿ�_�û�Ԥ��ֵ","�û�id��"+userRecharge.getUserId()+"�ۿ����ͣ�"+userRecharge.getUcdId()+"�û�openid:"+userRecharge.getUserOpenId()+"������:"+outTradeNo+":���:"+userRecharge.getRealAmout()/100+";ʵ����"+userRecharge.getRealAmout()+"֧�������׺�"+response.getTradeNo(), "success", response.getTradeNo());
            	return response.getTradeNo();
            }else{
            	wechatUsermapper.insertApiLogs("֧����ͳһ�µ��ӿ�_�û�Ԥ��ֵ","�û�id��"+userRecharge.getUserId()+"�ۿ����ͣ�"+userRecharge.getUcdId()+"�û�openid:"+userRecharge.getUserOpenId()+"������:"+outTradeNo+":���:"+userRecharge.getRealAmout()/100+";ʵ����"+userRecharge.getRealAmout(), "fail", response.getSubCode()+"_"+response.getSubMsg());
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
						"С����openId����Ϊ�գ�");
         }
         
         int balance = Integer.valueOf(map.get("balance").toString());
         
         wechatUser  user = wechatUsermapper.findWechatUserInfoByOpenId(userOpenId);
         
         if(userOpenId.equals(user.getUserOpenIdZfb())==false){
         	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"���û���֧����С�����û������ʵ��");
         }
         
         if(user.getBalances()!=balance&&balance!=0&&balance>=1){
         	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"���ֽ����ʵ�ʽ�ƥ�䣬���ʵ��");
         }
         
		
				AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
		        //�����������
				AlipayFundTransUniTransferRequest aliPayRequest= new AlipayFundTransUniTransferRequest();
		        
		        *//** ��ǰʱ�� yyyyMMddHHmmss *//*
		        String currTime = CommonUtil.getCurrTime();
		        *//** 8λ���� *//*
		        //String strTime = currTime.substring(8, currTime.length());
		        *//** ��λ����� *//*
		        String strRandom = CommonUtil.buildRandom(4) + "";
		        
		        String outTradeNo=currTime+strRandom;
		        
		        JSONObject json=new JSONObject();
		        JSONObject payee_info_json=new JSONObject();
		        payee_info_json.put("identity", userOpenId);
		        payee_info_json.put("identity_type", "ALIPAY_USER_ID");
		        //������
		        json.put("out_biz_no",outTradeNo);
		        //��� ����Ľ������ԪΪ��λ�Ŀ��Բ�ת�����������ַ���
		        json.put("trans_amount",balance/100.0f);
		        
		        json.put("product_code","TRANS_ACCOUNT_NO_PWD");
		        
		        json.put("biz_scene","DIRECT_TRANSFER");
		        json.put("payee_info", payee_info_json);
		        json.put("order_title", "��"+userOpenId+"��"+"����");
		        String jsonStr=json.toString();
		        //�̻�ͨ���ýӿڽ��н��׵Ĵ����µ�
		        aliPayRequest.setBizContent(jsonStr);
		        //�ص���ַ ���ܹ����ʵ����������Ϸ�����
		       
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
         certAlipayRequest.setServerUrl("https://openapi.alipay.com/gateway.do");  //gateway:֧�������أ��̶���https://openapi.alipay.com/gateway.do
         certAlipayRequest.setAppId("2021002116635517");  //APPID ������Ӧ�ú�����,���������Ӧ�ò���ȡ APPID
         certAlipayRequest.setPrivateKey("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF");  //������Ӧ��˽Կ���ɿ������Լ�����
         certAlipayRequest.setFormat("json");  //�������ظ�ʽ��ֻ֧�� json ��ʽ
         certAlipayRequest.setCharset("UTF-8");  //�����ǩ��ʹ�õ��ַ������ʽ��֧�� GBK�� UTF-8
         certAlipayRequest.setSignType("RSA2");  //�̻�����ǩ���ַ�����ʹ�õ�ǩ���㷨���ͣ�Ŀǰ֧�� RSA2 �� RSA���Ƽ��̼�ʹ�� RSA2��   
         certAlipayRequest.setCertPath(""); //Ӧ�ù�Կ֤��·����app_cert_path �ļ�����·����
         certAlipayRequest.setAlipayPublicCertPath(""); //֧������Կ֤���ļ�·����alipay_cert_path �ļ�����·����
         certAlipayRequest.setRootCertPath("");  //֧����CA��֤���ļ�·����alipay_root_cert_path �ļ�����·����
         AlipayClient alipayClient = new DefaultAlipayClient(certAlipayRequest);
         AlipayFundTransUniTransferRequest request2 = new AlipayFundTransUniTransferRequest();
         //** ��ǰʱ�� yyyyMMddHHmmss *//*
	        String currTime = CommonUtil.getCurrTime();
	        //** 8λ���� *//*
	        //String strTime = currTime.substring(8, currTime.length());
	        //** ��λ����� *//*
	        String strRandom = CommonUtil.buildRandom(4) + "";
	        
	        String outTradeNo=currTime+strRandom;
	        
	        JSONObject json=new JSONObject();
	        JSONObject payee_info_json=new JSONObject();
	        payee_info_json.put("identity", userOpenId);
	        payee_info_json.put("identity_type", "ALIPAY_USER_ID");
	        //������
	        json.put("out_biz_no",outTradeNo);
	        //��� ����Ľ������ԪΪ��λ�Ŀ��Բ�ת�����������ַ���
	        json.put("trans_amount",balance/100.0f);
	        
	        json.put("product_code","TRANS_ACCOUNT_NO_PWD");
	        
	        json.put("biz_scene","DIRECT_TRANSFER");
	        json.put("payee_info", payee_info_json);
	        json.put("order_title", "��"+userOpenId+"��"+"����");
	        String jsonStr=json.toString();
         request2.setBizContent(jsonStr);
         AlipayFundTransUniTransferResponse response = alipayClient.certificateExecute(request2);
         if(response.isSuccess()){
         System.out.println("���óɹ�");
         } else {
         System.out.println("����ʧ��");
         }
		
	}*/
	
	




}
